package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.DirectedFeature;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.FeatureGroup;
import org.osate.aadl2.FeatureGroupType;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;
import org.osate.xtext.aadl2.properties.util.TimingProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.AddFilterDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddFilterClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;

public class AddFilterHandler extends AadlHandler {

	public static final String FILTER_COMP_TYPE_NAME = "CASE_Filter";
	public static final String FILTER_PORT_IN_NAME = "Input";
	public static final String FILTER_PORT_OUT_NAME = "Output";
	public static final String FILTER_LOG_PORT_NAME = "LogMessage";
	public static final String FILTER_SUBCOMP_NAME = "Filter";
	static final String CONNECTION_IMPL_NAME = "c";

	private String filterComponentName;
	private String filterSubcomponentName;
	private String filterDispatchProtocol;
	private String filterPeriod;
	private String inputPortName;
	private String outputPortName;
	private PortCategory logPortType;
	private String filterRequirement;
	private String filterPolicy;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
//		if (!(eObj instanceof PortConnection)) {
		if (!(eObj instanceof Connection)) {
			Dialog.showError("Add Filter",
					"A connection between two components must be selected to add a filter.");
			return;
		}

		// Make sure the source and destination components are not filters.
		// If one (or both) is, they will need to be combined, so alert the user
//		final PortConnection selectedConnection = (PortConnection) eObj;
		final Connection selectedConnection = (Connection) eObj;
		Subcomponent subcomponent = (Subcomponent) selectedConnection.getDestination().getContext();

		if (subcomponent == null) {
			Dialog.showError("Add Filter", "A filter cannot be connected to the out port of a component.");
			return;
		}

		// For now a filter can only be added onto a thread, thread group, or process
		ComponentCategory compCategory = subcomponent.getCategory();
		if (compCategory != ComponentCategory.THREAD && compCategory != ComponentCategory.THREAD_GROUP
				&& compCategory != ComponentCategory.PROCESS) {
			Dialog.showError("Add Filter", "A filter can only be connected to a thread, thread group, or process.");
			return;
		}

		boolean createCompoundFilter = false;
//		PortConnection filterOutConn = null;
		Connection filterOutConn = null;
//		if (CasePropertyUtils.isCompType(subcomponent.getClassifier(), "FILTER")) {
		if (CasePropertyUtils.hasMitigationType(subcomponent.getClassifier(), MITIGATION_TYPE.FILTER)) {
			if (Dialog.askQuestion("Add Filter",
					"A CASE Filter cannot be inserted next to another CASE Filter.  Would you like to add a new filter specification to the existing filter instead?")) {

				createCompoundFilter = true;
				// Get filter outgoing connection
				ComponentImplementation ci = subcomponent.getContainingComponentImpl();
				for (Connection conn : ci.getOwnedConnections()) {
					Subcomponent src = (Subcomponent) conn.getSource().getContext();
					if (src != null && src.getName().equalsIgnoreCase(subcomponent.getName())) {
//						filterOutConn = (PortConnection) conn;
						filterOutConn = conn;
						break;
					}
				}

				if (filterOutConn == null) {
					Dialog.showError("Add Filter",
							"Unable to find the outgoing connection of the existing CASE Filter.");
					return;
				}

			} else {
				return;
			}
		} else {

			subcomponent = (Subcomponent) selectedConnection.getSource().getContext();
			if (subcomponent != null) {
//				if (CasePropertyUtils.isCompType(subcomponent.getClassifier(), "FILTER")) {
				if (CasePropertyUtils.hasMitigationType(subcomponent.getClassifier(), MITIGATION_TYPE.FILTER)) {
					if (Dialog.askQuestion("Add Filter",
							"A CASE Filter cannot be inserted next to another CASE Filter.  Would you like to add a new filter specification to the existing filter instead?")) {
						createCompoundFilter = true;
						filterOutConn = selectedConnection;
					} else {
						return;
					}
				}
			}
		}

		// Open wizard to enter filter info
		final AddFilterDialog wizard = new AddFilterDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		// Provide list of requirements so the user can choose which requirement is driving this
		// model transformation.
		// We only want to list requirements that aren't already associated with a filter transform
		List<String> requirements = new ArrayList<>();
		for (CyberRequirement req : RequirementsManager.getInstance().getImportedRequirements()) {
			boolean addFilterFound = false;
			try {
				FunctionDefinition fd = req.getResoluteClaim();
				for (FnCallExpr fnCallExpr : EcoreUtil2.getAllContentsOfType(fd.getBody().getExpr(),
						FnCallExpr.class)) {
					if (fnCallExpr.getFn().getName().equalsIgnoreCase("add_filter")) {
						addFilterFound = true;
						break;
					}
				}
			} catch (NullPointerException e) {
				addFilterFound = false;
			}

			if (!addFilterFound) {
				requirements.add(req.getId());
			}

		}

		wizard.setRequirements(requirements);
		if (createCompoundFilter) {
			wizard.createCompoundFilter(subcomponent);
		}
		wizard.create(selectedConnection.getContainingComponentImpl());
		if (wizard.open() == Window.OK) {
			filterComponentName = wizard.getFilterComponentName();
			if (filterComponentName == "") {
				filterComponentName = FILTER_COMP_TYPE_NAME;
			}
			filterSubcomponentName = wizard.getFilterSubcomponentName();
			if (filterSubcomponentName == "") {
				filterSubcomponentName = FILTER_SUBCOMP_NAME;
			}
			filterDispatchProtocol = wizard.getDispatchProtocol();
			filterPeriod = wizard.getPeriod();
			inputPortName = wizard.getInputPortName();
			if (inputPortName == "") {
				inputPortName = FILTER_PORT_IN_NAME;
			}
			outputPortName = wizard.getOutputPortName();
			if (outputPortName == "") {
				outputPortName = FILTER_PORT_OUT_NAME;
			}
			logPortType = wizard.getLogPortType();
			filterRequirement = wizard.getRequirement();
			filterPolicy = wizard.getPolicy();
		} else {
			return;
		}

		// Insert the filter component
		if (createCompoundFilter) {
			addFilterSpec(EcoreUtil.getURI(subcomponent), EcoreUtil.getURI(filterOutConn));
			BriefcaseNotifier.notify("StairCASE - Filter", "New requirement associated with Filter.");
		} else {
			insertFilterComponent(uri);
			BriefcaseNotifier.notify("StairCASE - Filter", "Filter added to model.");
		}

		// Save
		saveChanges(false);

		return;

	}

	/**
	 * Inserts a filter component into the model, including filter type definition
	 * and implementation (including correct wiring).  The filter is inserted at
	 * the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	public void insertFilterComponent(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		AddFilterClaim claim = xtextEditor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
//			PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
			Connection selectedConnection = (Connection) resource.getEObject(uri.fragment());
			final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
			PackageSection pkgSection = null;
			// Figure out if the selected connection is in the public or private section
			EObject eObj = selectedConnection.eContainer();
			while (eObj != null) {
				if (eObj instanceof PublicPackageSection) {
					pkgSection = aadlPkg.getOwnedPublicSection();
					break;
				} else if (eObj instanceof PrivatePackageSection) {
					pkgSection = aadlPkg.getOwnedPrivateSection();
					break;
				} else {
					eObj = eObj.eContainer();
				}
			}

			if (pkgSection == null) {
				// Something went wrong
				Dialog.showError("Add Filter", "No public or private package sections found.");
				return null;
			}

			// Import CASE_Properties file
			if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
				return null;
			}

			// Figure out component type by looking at the component type of the destination component
			ComponentCategory compCategory = ((Subcomponent) selectedConnection.getDestination().getContext())
					.getCategory();

			// If the component type is a process, we will need to put a single thread inside.
			// Per convention, we will attach all properties and contracts to the thread.
			// For this model transformation, we will create the thread first, then wrap it in a process
			// component, using the same mechanism we use for the seL4 transformation
			final boolean isProcess = (compCategory == ComponentCategory.PROCESS);
			if (isProcess) {
				compCategory = ComponentCategory.THREAD;
			} else if (compCategory == ComponentCategory.THREAD_GROUP) {
				compCategory = ComponentCategory.THREAD;
			}

			final ComponentType filterType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

			// Give it a unique name
			filterType.setName(
					ModelTransformUtils.getUniqueName(filterComponentName, true, pkgSection.getOwnedClassifiers()));

			// Create filter ports
//			final Port port = (Port) selectedConnection.getDestination().getConnectionEnd();
			final ConnectionEnd connectionEnd = selectedConnection.getDestination().getConnectionEnd();
//			Port portIn = null;
//			Port portOut = null;
			ConnectionEnd connEndIn = null;
			ConnectionEnd connEndOut = null;
//			DataSubcomponentType dataFeatureClassifier = null;
//			EObject featureClassifier = null;
			NamedElement featureClassifier = null;
//			if (port instanceof EventDataPort) {
//				portIn = ComponentCreateHelper.createOwnedEventDataPort(filterType);
//				dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
//				((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
//				portOut = ComponentCreateHelper.createOwnedEventDataPort(filterType);
//				((EventDataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
//			} else if (port instanceof DataPort) {
//				portIn = ComponentCreateHelper.createOwnedDataPort(filterType);
//				dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
//				((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
//				portOut = ComponentCreateHelper.createOwnedDataPort(filterType);
//				((DataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
//			} else if (port instanceof EventPort) {
//				Dialog.showError("Add Filter", "Cannot connect a filter to a non-data port.");
//				return null;
//			} else {
//				Dialog.showError("Add Filter", "Could not determine the port type of the destination component.");
//				return null;
//			}
			if (connectionEnd instanceof EventDataPort) {
				connEndIn = ComponentCreateHelper.createOwnedEventDataPort(filterType);
				featureClassifier = ((EventDataPort) connectionEnd).getDataFeatureClassifier();
				((EventDataPort) connEndIn).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
				connEndOut = ComponentCreateHelper.createOwnedEventDataPort(filterType);
				((EventDataPort) connEndOut).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
			} else if (connectionEnd instanceof DataPort) {
				connEndIn = ComponentCreateHelper.createOwnedDataPort(filterType);
				featureClassifier = ((DataPort) connectionEnd).getDataFeatureClassifier();
				((DataPort) connEndIn).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
				connEndOut = ComponentCreateHelper.createOwnedDataPort(filterType);
				((DataPort) connEndOut).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
			} else if (connectionEnd instanceof EventPort) {
				Dialog.showError("Add Filter", "Cannot connect a filter to a non-data port.");
				return null;
			} else if (connectionEnd instanceof FeatureGroup) {
				connEndIn = filterType.createOwnedFeatureGroup();
				featureClassifier = ((FeatureGroup) connectionEnd).getFeatureGroupType();
				((FeatureGroup) connEndIn).setFeatureType((FeatureGroupType) featureClassifier);
				FeatureGroupType featureClassifierOut = ((FeatureGroup) selectedConnection.getSource()
						.getConnectionEnd())
						.getFeatureGroupType();
				connEndOut = filterType.createOwnedFeatureGroup();
				((FeatureGroup) connEndOut).setFeatureType(featureClassifierOut);
				ModelTransformUtils.importContainingPackage(featureClassifierOut, pkgSection);
			} else {
				Dialog.showError("Add Filter", "Could not determine the port type of the destination component.");
				return null;
			}

			connEndIn.setName(inputPortName);
			connEndOut.setName(outputPortName);

			((DirectedFeature) connEndIn).setIn(true);
			((DirectedFeature) connEndOut).setOut(true);

//			portIn.setIn(true);
//			portIn.setName(inputPortName);

//			portOut.setOut(true);
//			portOut.setName(outputPortName);

			// The data subcomponent type could be in a different package.
			// Make sure to include it in the with clause
//			importContainingPackage(dataFeatureClassifier, pkgSection);
			ModelTransformUtils.importContainingPackage(featureClassifier, pkgSection);

			// Create log port, if necessary
			if (logPortType != null) {
				Port logPort = null;
				if (logPortType == PortCategory.EVENT) {
					logPort = ComponentCreateHelper.createOwnedEventPort(filterType);
				} else if (logPortType == PortCategory.DATA) {
					logPort = ComponentCreateHelper.createOwnedDataPort(filterType);
				} else {
					logPort = ComponentCreateHelper.createOwnedEventDataPort(filterType);
				}
				logPort.setOut(true);
				logPort.setName(FILTER_LOG_PORT_NAME);
			}

			// Add filter properties
			// CASE::Component_Type Property
//			if (!CasePropertyUtils.setCompType(filterType, "FILTER")) {
			if (!CasePropertyUtils.setMitigationType(filterType, MITIGATION_TYPE.FILTER)) {
//				return;
			}

			// CASE::Component_Spec property
			// Parse the ID from the Filter AGREE property
//			String filterPropId = "Req_" + filterType.getName();
			String filterPropId = "";
			try {
				filterPropId = filterPolicy
						.substring(filterPolicy.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
								filterPolicy.indexOf("\""))
						.trim();

			} catch (IndexOutOfBoundsException e) {
				if (!filterPolicy.isEmpty()) {
					// Agree property is malformed
					Dialog.showWarning("Add Filter", "Filter AGREE statement is malformed.");
				}
//				return;
			}

			if (!filterPropId.isEmpty()) {
				if (!CasePropertyUtils.setCompSpec(filterType, filterPropId)) {
//					return;
				}
			}

			final ComponentImplementation containingImpl = selectedConnection.getContainingComponentImpl();

			// Move filter to top of file
			pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);

			// Create Filter implementation
			final ComponentImplementation filterImpl = (ComponentImplementation) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			filterImpl.setName(filterType.getName() + ".Impl");
			final Realization r = filterImpl.createOwnedRealization();
			r.setImplemented(filterType);

//			// CASE::Component_Impl property
//			if (!filterImplementationLanguage.isEmpty()) {
//				if (!CaseUtils.addCasePropertyAssociation("COMP_IMPL", filterImplementationLanguage, filterImpl)) {
////					return;
//				}
//			}

			// Dispatch protocol
			if (!filterDispatchProtocol.isEmpty() && compCategory == ComponentCategory.THREAD) {
				Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(filterImpl,
						ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
				EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
				dispatchProtocolLit.setName(filterDispatchProtocol);
				NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
				nv.setNamedValue(dispatchProtocolLit);
				filterImpl.setPropertyValue(dispatchProtocolProp, nv);
			}
			// Period
			if (!filterPeriod.isEmpty() && compCategory == ComponentCategory.THREAD) {
				Property periodProp = GetProperties.lookupPropertyDefinition(filterImpl, TimingProperties._NAME, TimingProperties.PERIOD);
				IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
				UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
				unit.setName(filterPeriod.replaceAll("[\\d]", "").trim());
				periodLit.setBase(0);
				periodLit.setValue(Long.parseLong(filterPeriod.replaceAll("[\\D]", "").trim()));
				periodLit.setUnit(unit);
				filterImpl.setPropertyValue(periodProp, periodLit);
			}

			// Add it to proper place (just below component type)
			pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);

			// Insert filter feature in process component implementation
			final Subcomponent filterSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
					compCategory);

			// Give it a unique name
			filterSubcomp
					.setName(ModelTransformUtils.getUniqueName(filterSubcomponentName, true,
							containingImpl.getOwnedSubcomponents()));

			ComponentCreateHelper.setSubcomponentType(filterSubcomp, filterImpl);

			// Create connection from filter to connection destination
//			final PortConnection portConnOut = containingImpl.createOwnedPortConnection();
//			// Give it a unique name
//			portConnOut.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
//					containingImpl.getOwnedPortConnections()));
//			portConnOut.setBidirectional(false);
//			final ConnectedElement filterOutSrc = portConnOut.createSource();
//			filterOutSrc.setContext(filterSubcomp);
////			filterOutSrc.setConnectionEnd(portOut);
//			filterOutSrc.setConnectionEnd(connEndOut);
//			final ConnectedElement filterOutDst = portConnOut.createDestination();
//			filterOutDst.setContext(selectedConnection.getDestination().getContext());
//			filterOutDst.setConnectionEnd(selectedConnection.getDestination().getConnectionEnd());

			final Connection connOut = ComponentCreateHelper.createOwnedConnection(containingImpl, selectedConnection);
			// Give it a unique name
//			connOut.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
//					containingImpl.getOwnedPortConnections()));
			connOut.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
					containingImpl.getOwnedConnections()));
			connOut.setBidirectional(false);
			final ConnectedElement filterOutSrc = connOut.createSource();
			filterOutSrc.setContext(filterSubcomp);
//			filterOutSrc.setConnectionEnd(portOut);
			filterOutSrc.setConnectionEnd(connEndOut);
			final ConnectedElement filterOutDst = connOut.createDestination();
			filterOutDst.setContext(selectedConnection.getDestination().getContext());
			filterOutDst.setConnectionEnd(selectedConnection.getDestination().getConnectionEnd());

			// Put portConnOut in right place (after portConnIn)
			String destName = selectedConnection.getName();
//			containingImpl.getOwnedPortConnections().move(
//					getIndex(destName, containingImpl.getOwnedPortConnections()) + 1,
//					containingImpl.getOwnedPortConnections().size() - 1);
			containingImpl.getOwnedConnections().move(getIndex(destName, containingImpl.getOwnedConnections()) + 1,
					containingImpl.getOwnedConnections().size() - 1);

			// Rewire selected connection so the filter is the destination
			selectedConnection.getDestination().setContext(filterSubcomp);
//			selectedConnection.getDestination().setConnectionEnd(portIn);
			selectedConnection.getDestination().setConnectionEnd(connEndIn);

			// AGREE
			if (filterPolicy.length() > 0) {

//				// AGREE
//				String filterPolicyName = filterType.getName() + "_policy";
//
//				if (filterPolicy.isEmpty()) {
//					filterPolicy = "false;";
//				} else if (!filterPolicy.trim().endsWith(";")) {
//					filterPolicy = filterPolicy.trim() + ";";
//				}

				StringBuilder agreeClauses = new StringBuilder();
				agreeClauses.append("{**" + System.lineSeparator());

//				// Filter policy
//				agreeClauses.append("property " + filterPolicyName + " = " + filterPolicy + System.lineSeparator());
//
//				// Filter guarantee
//				agreeClauses.append("guarantee " + filterPropId + " The filter output shall be well-formed :"
//						+ System.lineSeparator());
//
//				if (portIn instanceof EventDataPort) {
//					agreeClauses.append("not event(" + portOut.getName() + ") -> if event" + portIn.getName() + ") and "
//							+ filterPolicyName + " then"
//							+ System.lineSeparator());
//					agreeClauses.append("event(" + portOut.getName() + ") and " + portOut.getName() + " = "
//							+ portIn.getName() + System.lineSeparator());
//					agreeClauses.append("else" + System.lineSeparator());
//					agreeClauses.append("not event(" + portOut.getName() + ");" + System.lineSeparator());
//				} else {
//					agreeClauses.append("if " + filterPolicyName + " then" + System.lineSeparator());
//					agreeClauses.append(portOut.getName() + " = " + portIn.getName() + System.lineSeparator());
//					agreeClauses.append("else" + System.lineSeparator());
//					// User will need to put an expression after the 'else'
//					agreeClauses.append(";" + System.lineSeparator());
//				}

				if (!filterPolicy.isEmpty()) {
					agreeClauses.append(filterPolicy + System.lineSeparator());
				}

				agreeClauses.append("**}");

				final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
						.createOwnedAnnexSubclause(filterType);
				annexSubclauseImpl.setName("agree");
				annexSubclauseImpl.setSourceText(agreeClauses.toString());
			}

			if (isProcess) {

				// TODO: Wrap thread component in a process

				// TODO: Bind process to processor
			}

			// Add add_filter claims to resolute prove statement, if applicable
			if (!filterRequirement.isEmpty()) {
				CyberRequirement req = RequirementsManager.getInstance().getRequirement(filterRequirement);
//				return new AddFilterClaim(
//						req.getContext(), filterSubcomp, portConnOut, dataFeatureClassifier);
				return new AddFilterClaim(
						req.getContext(), filterSubcomp, connOut, featureClassifier);
			}

			return null;
		});

		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(filterRequirement, claim);
		}

	}


	/**
	 * Adds a new spec to the specified filter
	 * @param uri
	 */
	private void addFilterSpec(URI subURI, URI connURI) {
		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		AddFilterClaim claim = xtextEditor.getDocument().modify(resource -> {

			Subcomponent subcomponent = (Subcomponent) resource.getEObject(subURI.fragment());
			ComponentType filter = subcomponent.getComponentType();

//			PortConnection connection = (PortConnection) resource.getEObject(connURI.fragment());
			Connection connection = (Connection) resource.getEObject(connURI.fragment());
//			Port port = (Port) connection.getDestination().getConnectionEnd();
			ConnectionEnd connectionEnd = connection.getDestination().getConnectionEnd();
//			DataSubcomponentType dataFeatureClassifier = null;
			NamedElement featureClassifier = null;
			if (connectionEnd instanceof EventDataPort) {
//				dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
				featureClassifier = ((EventDataPort) connectionEnd).getDataFeatureClassifier();
			} else if (connectionEnd instanceof DataPort) {
//				dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
				featureClassifier = ((DataPort) connectionEnd).getDataFeatureClassifier();
			} else if (connectionEnd instanceof FeatureGroup) {
				featureClassifier = ((FeatureGroup) connectionEnd).getFeatureGroupType();
			} else {
				Dialog.showError("Add Filter", "Could not determine the port type of the filter.");
				return null;
			}

			String filterPropId = "";
			try {
				filterPropId = filterPolicy
						.substring(filterPolicy.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
								filterPolicy.indexOf("\""))
						.trim();

			} catch (IndexOutOfBoundsException e) {
				// agree property is malformed
				Dialog.showError("Add Filter", "AGREE statement is malformed.");
				return null;
			}

			if (filterPropId.isEmpty()) {
				// agree property id is missing
				Dialog.showError("Add Filter", "AGREE statements on CASE components require a unique ID.");
				return null;
			}

			// Add AGREE spec
			DefaultAnnexSubclause subclause = null;
			String agreeClauses = "{** **}";
			for (AnnexSubclause sc : filter.getOwnedAnnexSubclauses()) {
				if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("agree")) {
					subclause = (DefaultAnnexSubclause) sc;
					break;
				}
			}

			if (subclause != null) {
				agreeClauses = subclause.getSourceText();
			}

			// Remove current agree annex. The modified one will be added below.
			Iterator<AnnexSubclause> i = filter.getOwnedAnnexSubclauses().iterator();
			while (i.hasNext()) {
				subclause = (DefaultAnnexSubclause) i.next();
				if (subclause.getName().equalsIgnoreCase("agree")) {
					i.remove();
					break;
				}
			}
			agreeClauses = agreeClauses.replace("**}", filterPolicy + System.lineSeparator() + "**}");
			DefaultAnnexSubclause newSubclause = (DefaultAnnexSubclause) filter
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			newSubclause.setName("agree");
			newSubclause.setSourceText(agreeClauses);

			// Add AGREE spec ID to COMP_SPEC property
			// Get current property value
			String propVal = "";
			Property prop = Aadl2GlobalScopeUtil.get(filter, Aadl2Package.eINSTANCE.getProperty(),
					CasePropertyUtils.CASE_PROPSET_NAME + "::" + CasePropertyUtils.COMP_SPEC);
			List<? extends PropertyExpression> propVals = filter.getPropertyValueList(prop);
			if (propVals != null) {
				for (PropertyExpression expr : propVals) {
					if (expr instanceof StringLiteral) {
						propVal += ((StringLiteral) expr).getValue() + ",";
					}
				}
			}

			// Append new spec ID
			propVal += filterPropId;

			// Write property to filter component
			if (!CasePropertyUtils.setCompSpec(filter, propVal)) {
//				return;
			}

			// Add add_filter claims to resolute prove statement, if applicable
			if (!filterRequirement.isEmpty()) {
				CyberRequirement req = RequirementsManager.getInstance().getRequirement(filterRequirement);
//				return new AddFilterClaim(req.getContext(), subcomponent, connection, dataFeatureClassifier);
				return new AddFilterClaim(req.getContext(), subcomponent, connection, featureClassifier);
			}
			return null;
		});

		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(filterRequirement, claim);
		}
	}

}
