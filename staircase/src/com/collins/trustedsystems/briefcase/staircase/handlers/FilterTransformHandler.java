package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.Iterator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ArrayDimension;
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
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;
import org.osate.xtext.aadl2.properties.util.TimingProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.FilterTransformDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddFilterClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class FilterTransformHandler extends AadlHandler {

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
	private boolean isSel4Process = false;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof Connection)) {
			Dialog.showError("Filter Transform",
					"A connection between two components must be selected to add a filter.");
			return;
		}

		// Make sure the source and destination components are not filters.
		// If one (or both) is, they will need to be combined, so alert the user
		final Connection selectedConnection = (Connection) eObj;
		Subcomponent subcomponent = (Subcomponent) selectedConnection.getDestination().getContext();

		if (subcomponent == null) {
			Dialog.showError("Filter Transform", "A filter cannot be connected to the out port of a component.");
			return;
		}

		// For now a filter can only be added onto a thread, thread group, or process
		final ComponentCategory compCategory = subcomponent.getCategory();
		if (compCategory != ComponentCategory.THREAD && compCategory != ComponentCategory.THREAD_GROUP
				&& compCategory != ComponentCategory.PROCESS) {
			Dialog.showError("Filter Transform",
					"A filter can only be connected to a thread, thread group, or process.");
			return;
		}

//		// Check that a filter isn't being added to a thread in a seL4 process
//		if (subcomponent.getContainingComponentImpl() instanceof ProcessImplementation
//				&& subcomponent.getContainingComponentImpl().getTypeName().endsWith("_seL4")) {
//			Dialog.showError("Filter Transform", "An seL4 process cannot contain multiple components.");
//			return;
//		}

//		isSel4Process = subcomponent.getComponentImplementation() instanceof ProcessImplementation
//				&& subcomponent.getContainingComponentImpl().getTypeName().endsWith("_seL4");

		boolean createCompoundFilter = false;
		Connection filterOutConn = null;
		if (CasePropertyUtils.hasMitigationType(subcomponent.getClassifier(), MITIGATION_TYPE.FILTER)) {
			if (Dialog.askQuestion("Filter Transform",
					"A CASE Filter cannot be inserted next to another CASE Filter.  Would you like to add a new filter specification to the existing filter instead?")) {

				createCompoundFilter = true;
				// Get filter outgoing connection
				final ComponentImplementation ci = subcomponent.getContainingComponentImpl();
				for (Connection conn : ci.getOwnedConnections()) {
					final Subcomponent src = (Subcomponent) conn.getSource().getContext();
					if (src != null && src.getName().equalsIgnoreCase(subcomponent.getName())) {
						filterOutConn = conn;
						break;
					}
				}

				if (filterOutConn == null) {
					Dialog.showError("Filter Transform",
							"Unable to find the outgoing connection of the existing CASE Filter.");
					return;
				}

			} else {
				return;
			}
		} else {

			subcomponent = (Subcomponent) selectedConnection.getSource().getContext();
			if (subcomponent != null) {
				if (CasePropertyUtils.hasMitigationType(subcomponent.getClassifier(), MITIGATION_TYPE.FILTER)) {
					if (Dialog.askQuestion("Filter Transform",
							"A CASE Filter cannot be inserted next to another CASE Filter.  Would you like to add a new filter specification to the existing filter instead?")) {
						createCompoundFilter = true;
						filterOutConn = selectedConnection;
					} else {
						return;
					}
				}
			}
		}
//		if (createCompoundFilter && isSel4Process) {
//			// Make sure seL4 process contains a filter thread
//			ProcessImplementation pi = (ProcessImplementation) subcomponent.getComponentImplementation();
//			if (pi.getOwnedThreadSubcomponents().size() != 1) {
//				Dialog.showError("Filter Transform", "seL4 filter does not contain a single thread.");
//				return;
//			}
//			if (!CasePropertyUtils.hasMitigationType(pi.getOwnedThreadSubcomponents().get(0).getClassifier(),
//					MITIGATION_TYPE.FILTER)) {
//				Dialog.showError("Filter Transform", "seL4 process does not contain a filter thread.");
//				return;
//			}
//		}

		// Open wizard to enter filter info
		final FilterTransformDialog wizard = new FilterTransformDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

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
			isSel4Process = wizard.createThread();
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
			BriefcaseNotifier.notify("StairCASE - Filter Transform", "New requirement associated with Filter.");
		} else {
			insertFilterComponent(uri);
			BriefcaseNotifier.notify("StairCASE - Filter Transform", "Filter added to model.");
		}

		// Format and save
		format(true);

//		// Save
//		saveChanges(false);

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

		final AddFilterClaim claim = xtextEditor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			final Connection selectedConnection = (Connection) resource.getEObject(uri.fragment());
			PackageSection pkgSection = AadlUtil.getContainingPackageSection(selectedConnection);
			if (pkgSection == null) {
				// Something went wrong
				Dialog.showError("Filter Transform", "No public or private package sections found.");
				return null;
			}

			// Import CASE_Properties file
			if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
				return null;
			}

			final ComponentImplementation containingImpl = selectedConnection.getContainingComponentImpl();

			// Figure out component type by looking at the component type of the destination component
			ComponentCategory compCategory = ((Subcomponent) selectedConnection.getDestination().getContext())
					.getCategory();
			if (compCategory == ComponentCategory.THREAD_GROUP) {
				compCategory = ComponentCategory.THREAD;
			}

			final ComponentType filterType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

			// Give it a unique name
			final String filterName = ModelTransformUtils.getUniqueName(filterComponentName, true,
					pkgSection.getOwnedClassifiers());
			filterType.setName(filterName + (isSel4Process ? "_seL4" : ""));

			// Create filter ports
			final ConnectionEnd connectionEnd = selectedConnection.getDestination().getConnectionEnd();

			ConnectionEnd connEndIn = null;
			ConnectionEnd connEndOut = null;
			NamedElement featureClassifier = null;
			if (connectionEnd instanceof EventDataPort) {
				connEndIn = ComponentCreateHelper.createOwnedEventDataPort(filterType);
				featureClassifier = ((EventDataPort) connectionEnd).getDataFeatureClassifier();
				((EventDataPort) connEndIn).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
				for (ArrayDimension dim : ((EventDataPort) connectionEnd).getArrayDimensions()) {
					final ArrayDimension arrayDimension = ((EventDataPort) connEndIn).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
				connEndOut = ComponentCreateHelper.createOwnedEventDataPort(filterType);
				((EventDataPort) connEndOut).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
				for (ArrayDimension dim : ((EventDataPort) connectionEnd).getArrayDimensions()) {
					final ArrayDimension arrayDimension = ((EventDataPort) connEndOut).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
			} else if (connectionEnd instanceof DataPort) {
				connEndIn = ComponentCreateHelper.createOwnedDataPort(filterType);
				featureClassifier = ((DataPort) connectionEnd).getDataFeatureClassifier();
				((DataPort) connEndIn).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
				for (ArrayDimension dim : ((DataPort) connectionEnd).getArrayDimensions()) {
					final ArrayDimension arrayDimension = ((DataPort) connEndIn).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
				connEndOut = ComponentCreateHelper.createOwnedDataPort(filterType);
				((DataPort) connEndOut).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
				for (ArrayDimension dim : ((DataPort) connectionEnd).getArrayDimensions()) {
					final ArrayDimension arrayDimension = ((DataPort) connEndOut).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
			} else if (connectionEnd instanceof EventPort) {
				Dialog.showError("Filter Transform", "Cannot connect a filter to a non-data port.");
				return null;
			} else if (connectionEnd instanceof FeatureGroup) {
				connEndIn = filterType.createOwnedFeatureGroup();
				featureClassifier = ((FeatureGroup) connectionEnd).getFeatureGroupType();
				((FeatureGroup) connEndIn).setFeatureType((FeatureGroupType) featureClassifier);
				final FeatureGroupType featureClassifierOut = ((FeatureGroup) selectedConnection.getSource()
						.getConnectionEnd())
						.getFeatureGroupType();
				connEndOut = filterType.createOwnedFeatureGroup();
				((FeatureGroup) connEndOut).setFeatureType(featureClassifierOut);
				ModelTransformUtils.importContainingPackage(featureClassifierOut, pkgSection);
			} else {
				Dialog.showError("Filter Transform", "Could not determine the port type of the destination component.");
				return null;
			}

			connEndIn.setName(inputPortName);
			connEndOut.setName(outputPortName);

			((DirectedFeature) connEndIn).setIn(true);
			((DirectedFeature) connEndOut).setOut(true);

			// The data subcomponent type could be in a different package.
			// Make sure to include it in the with clause
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
			if (!CasePropertyUtils.setMitigationType(filterType, MITIGATION_TYPE.FILTER)) {
//				return;
			}

			// CASE::Component_Spec property
			String filterPropId = filterName + "_" + connEndOut.getName();
			if (!CasePropertyUtils.setCompSpec(filterType, filterPropId)) {
//				return;
			}

			// Move filter to top of file
//			pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
			try {
				pkgSection.getOwnedClassifiers().move(
					getIndex(containingImpl.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);
			} catch (Exception e) {

			}

			// Create Filter implementation
			final ComponentImplementation filterImpl = (ComponentImplementation) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			filterImpl.setName(filterType.getName() + ".Impl");
			final Realization r = filterImpl.createOwnedRealization();
			r.setImplemented(filterType);

			// Dispatch protocol
			if (compCategory == ComponentCategory.THREAD && !filterDispatchProtocol.isEmpty()) {
				final Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(filterImpl,
						ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
				final EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
				dispatchProtocolLit.setName(filterDispatchProtocol);
				final NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
				nv.setNamedValue(dispatchProtocolLit);
				filterImpl.setPropertyValue(dispatchProtocolProp, nv);
			}

			// Period
			if (!filterPeriod.isEmpty()) {
				final Property periodProp = GetProperties.lookupPropertyDefinition(filterImpl, TimingProperties._NAME,
						TimingProperties.PERIOD);
				final IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
				final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
				unit.setName(filterPeriod.replaceAll("[\\d]", "").trim());
				periodLit.setBase(0);
				periodLit.setValue(Long.parseLong(filterPeriod.replaceAll("[\\D]", "").trim()));
				periodLit.setUnit(unit);
				filterImpl.setPropertyValue(periodProp, periodLit);
			}

			// Add it to proper place (just below component type)
//			pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);
			try {
				pkgSection.getOwnedClassifiers().move(
					getIndex(containingImpl.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);
			} catch (Exception e) {

			}

			// Insert filter feature in process component implementation
			final Subcomponent filterSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
					compCategory);

			// Give it a unique name
			filterSubcomp
					.setName(ModelTransformUtils.getUniqueName(filterSubcomponentName, true,
							containingImpl.getAllSubcomponents()));

			ComponentCreateHelper.setSubcomponentType(filterSubcomp, filterImpl);

			// Create connection from filter to connection destination
			final Connection connOut = ComponentCreateHelper.createOwnedConnection(containingImpl, connEndOut);
			// Give it a unique name
			connOut.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
					containingImpl.getAllConnections()));
			connOut.setBidirectional(false);
			final ConnectedElement filterOutSrc = connOut.createSource();
			filterOutSrc.setContext(filterSubcomp);
			filterOutSrc.setConnectionEnd(connEndOut);
			final ConnectedElement filterOutDst = connOut.createDestination();
			filterOutDst.setContext(selectedConnection.getDestination().getContext());
			filterOutDst.setConnectionEnd(selectedConnection.getDestination().getConnectionEnd());

			// Put portConnOut in right place (after portConnIn)
			final String destName = selectedConnection.getName();
			try {
				containingImpl.getOwnedConnections().move(getIndex(destName, containingImpl.getOwnedConnections()) + 1,
					containingImpl.getOwnedConnections().size() - 1);
			} catch (Exception e) {

			}

			// Rewire selected connection so the filter is the destination
			selectedConnection.getDestination().setContext(filterSubcomp);
			selectedConnection.getDestination().setConnectionEnd(connEndIn);

			// AGREE
//			final String filterPolicyName = filterName + "_policy";

			if (filterPolicy.isEmpty()) {
				filterPolicy = "false;";
			} else if (!filterPolicy.trim().endsWith(";")) {
				filterPolicy = filterPolicy.trim() + ";";
			}

			final StringBuilder agreeClauses = new StringBuilder();
			agreeClauses.append("{**" + System.lineSeparator());

			// Filter policy
//			agreeClauses.append("property " + filterPolicyName + " = " + filterPolicy + System.lineSeparator());

			// Filter guarantee
			agreeClauses.append(
					"guarantee " + filterPropId + " \"The filter output shall be well-formed\" :"
							+ System.lineSeparator());

			if (connEndIn instanceof EventDataPort) {
				agreeClauses.append("if event(" + connEndIn.getName() + ") and " + filterPolicy + " then"
						+ System.lineSeparator());
				agreeClauses.append("event(" + connEndOut.getName() + ") and " + connEndOut.getName() + " = "
						+ connEndIn.getName() + System.lineSeparator());
				agreeClauses.append("else" + System.lineSeparator());
				agreeClauses.append("not event(" + connEndOut.getName() + ");" + System.lineSeparator());
			} else {
				agreeClauses.append("if " + filterPolicy + " then" + System.lineSeparator());
				agreeClauses.append(connEndOut.getName() + " = " + connEndIn.getName() + System.lineSeparator());
				agreeClauses.append("else" + System.lineSeparator());
				// User will need to put an expression after the 'else'
				agreeClauses.append(";" + System.lineSeparator());
			}

			agreeClauses.append("**}");

			if (isSel4Process) {
				final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
						.createOwnedAnnexSubclause(filterImpl);
				annexSubclauseImpl.setName("agree");
				annexSubclauseImpl.setSourceText(
						"{**" + System.lineSeparator() + "lift contract;" + System.lineSeparator() + "**}");
			} else {
				final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
						.createOwnedAnnexSubclause(filterType);
				annexSubclauseImpl.setName("agree");
				annexSubclauseImpl.setSourceText(agreeClauses.toString());
			}

			// Add thread if this is a seL4 process
			if (isSel4Process) {
				Sel4TransformHandler.insertThreadInSel4Process((ProcessImplementation) filterImpl,
						filterDispatchProtocol, agreeClauses.toString());

			}

			// Add add_filter claims to resolute prove statement, if applicable
			if (!filterRequirement.isEmpty()) {
				final CyberRequirement req = RequirementsManager.getInstance().getRequirement(filterRequirement);
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

		final AddFilterClaim claim = xtextEditor.getDocument().modify(resource -> {

			final Subcomponent subcomponent = (Subcomponent) resource.getEObject(subURI.fragment());
			ComponentType filter = null;
			// If it's an seL4 process, get the contained thread type instead
			if (isSel4Process) {
				ProcessImplementation pi = (ProcessImplementation) subcomponent.getComponentImplementation();
				filter = pi.getOwnedThreadSubcomponents().get(0).getComponentType();
			} else {
				filter = subcomponent.getComponentType();
			}

			final Connection connection = (Connection) resource.getEObject(connURI.fragment());
			final ConnectionEnd connectionEnd = connection.getSource().getConnectionEnd();
			NamedElement featureClassifier = null;
			if (connectionEnd instanceof EventDataPort) {
				featureClassifier = ((EventDataPort) connectionEnd).getDataFeatureClassifier();
			} else if (connectionEnd instanceof DataPort) {
				featureClassifier = ((DataPort) connectionEnd).getDataFeatureClassifier();
			} else if (connectionEnd instanceof FeatureGroup) {
				featureClassifier = ((FeatureGroup) connectionEnd).getFeatureGroupType();
			} else {
				Dialog.showError("Filter Transform", "Could not determine the port type of the filter.");
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
			final Iterator<AnnexSubclause> i = filter.getOwnedAnnexSubclauses().iterator();
			while (i.hasNext()) {
				subclause = (DefaultAnnexSubclause) i.next();
				if (subclause.getName().equalsIgnoreCase("agree")) {
					i.remove();
					break;
				}
			}

			String filterPropId = "property " + filter.getName() + "_policy = ";
			filterPolicy = filterPolicy.replace(";", "").trim();
			agreeClauses = agreeClauses.replace(filterPropId, filterPropId + filterPolicy + " and ");
			final DefaultAnnexSubclause newSubclause = (DefaultAnnexSubclause) filter
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			newSubclause.setName("agree");
			newSubclause.setSourceText(agreeClauses);

			// Add add_filter claims to resolute prove statement, if applicable
			if (!filterRequirement.isEmpty()) {
				final CyberRequirement req = RequirementsManager.getInstance().getRequirement(filterRequirement);
				return new AddFilterClaim(req.getContext(), subcomponent, connection, featureClassifier);
			}
			return null;
		});

		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(filterRequirement, claim);
		}
	}

}
