package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.SaveOptions;
import org.osate.aadl2.Aadl2Factory;
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
import org.osate.aadl2.RangeValue;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.MemoryProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;
import org.osate.xtext.aadl2.properties.util.TimingProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.FilterTransformDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddFilterClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class FilterTransformHandler extends AadlHandler {

	public static final String FILTER_COMP_TYPE_NAME = "Filter";
	public static final String FILTER_PORT_IN_NAME = "Input";
	public static final String FILTER_PORT_OUT_NAME = "Output";
	public static final String FILTER_LOG_PORT_NAME = "LogMessage";
	public static final String FILTER_SUBCOMP_NAME = "Filter";
	static final String CONNECTION_IMPL_NAME = "c";

	private String filterComponentName;
	private String filterSubcomponentName;
	private String filterDispatchProtocol;
	private String filterPeriod;
	private String filterExecutionTime;
	private Integer filterDomain;
	private String filterStackSize;
	private String inputPortName;
	private String outputPortName;
	private PortCategory logPortType;
	private String filterRequirement;
	private String filterPolicy;
	private boolean isSel4Process = false;

	@Override
	protected String getJobName() {
		return "Filter transform";
	}

	@Override
	protected void runCommand(EObject eObj) {

		Connection selectedConnection = null;

		// Check if it is a connection
		if (eObj instanceof Connection) {

			selectedConnection = (Connection) eObj;
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
		} else if (eObj instanceof ComponentImplementation) {
			if (((ComponentImplementation) eObj).getOwnedConnections().isEmpty()) {
				Dialog.showError("Filter Transform",
						"The selected component implementation must contain at least one connected subcomponent to add a filter.");
				return;
			}
		} else {
			Dialog.showError("Filter Transform",
					"A connection between two components, or a component implementation containing at least one connected subcomponent, must be selected to add a filter.");
			return;
		}

		// Open wizard to enter filter info
		final FilterTransformDialog wizard = new FilterTransformDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		wizard.create(eObj);
		if (wizard.open() == Window.OK) {
			final String connection = wizard.getConnection();
			if (!connection.isEmpty()) {
				for (Connection conn : ((ComponentImplementation) eObj).getOwnedConnections()) {
					if (conn.getName().equalsIgnoreCase(connection)) {
						selectedConnection = conn;
						break;
					}
				}
				if (selectedConnection == null) {
					Dialog.showError("Filter Transform", "Unable to determine connection to insert filter.");
					return;
				}
			}
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
			filterExecutionTime = wizard.getExecutionTime();
			filterDomain = wizard.getDomain();
			filterStackSize = wizard.getStackSize();
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
		if (insertFilterComponent(selectedConnection)) {
			BriefcaseNotifier.notify("BriefCASE - Filter Transform", "Filter added to model.");

			// Format and save
			format(true);
		} else {
			BriefcaseNotifier.printError("Filter transform failed.");
		}

		return;

	}

	/**
	 * Inserts a filter component into the model, including filter type definition
	 * and implementation (including correct wiring).  The filter is inserted at
	 * the location of the selected connection
	 * @param uri - The URI of the selected connection
	 * @return success
	 */
	public boolean insertFilterComponent(Connection selectedConnection) {

		if (selectedConnection == null) {
			Dialog.showError("Filter Transform", "Invalid connection.");
			return false;
		}

		final Resource aadlResource = selectedConnection.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE
				.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			AddFilterClaim claim = null;

			@Override
			protected void doExecute() {

				final PackageSection pkgSection = AadlUtil.getContainingPackageSection(selectedConnection);
				if (pkgSection == null) {
					// Something went wrong
					BriefcaseNotifier.printError("No public or private package sections found.");
					throw new RuntimeException();
				}

				// Import CASE_Properties file
				if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
					BriefcaseNotifier.printError("Could not import CASE_Properties property set.");
					throw new RuntimeException();
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
					BriefcaseNotifier.printError("Cannot connect a filter to a non-data port.");
					throw new RuntimeException();
				} else if (connectionEnd instanceof FeatureGroup) {
					connEndIn = filterType.createOwnedFeatureGroup();
					featureClassifier = ((FeatureGroup) connectionEnd).getFeatureGroupType();
					((FeatureGroup) connEndIn).setFeatureType((FeatureGroupType) featureClassifier);
					final FeatureGroupType featureClassifierOut = ((FeatureGroup) selectedConnection.getSource()
							.getConnectionEnd()).getFeatureGroupType();
					connEndOut = filterType.createOwnedFeatureGroup();
					((FeatureGroup) connEndOut).setFeatureType(featureClassifierOut);
					ModelTransformUtils.importContainingPackage(featureClassifierOut, pkgSection);
				} else {
					BriefcaseNotifier.printError("Could not determine the port type of the destination component.");
					throw new RuntimeException();
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
				CasePropertyUtils.setMitigationType(filterType, MITIGATION_TYPE.FILTER);

//				// CASE::Component_Spec property
//				String filterPropId = filterName + "_" + connEndOut.getName();
//				CasePropertyUtils.setCompSpec(filterType, filterPropId);

				// Move filter to top of file
//				pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
				try {
					pkgSection.getOwnedClassifiers()
							.move(getIndex(containingImpl.getTypeName(), pkgSection.getOwnedClassifiers()),
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
					final Property periodProp = GetProperties.lookupPropertyDefinition(filterImpl,
							TimingProperties._NAME, TimingProperties.PERIOD);
					final IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unit.setName(filterPeriod.replaceAll("[\\d]", "").trim());
					periodLit.setBase(0);
					periodLit.setValue(Long.parseLong(filterPeriod.replaceAll("[\\D]", "").trim()));
					periodLit.setUnit(unit);
					filterImpl.setPropertyValue(periodProp, periodLit);
				}

				// Execution Time
				if (compCategory == ComponentCategory.THREAD && !filterExecutionTime.isEmpty()) {
					final Property executionTimeProp = GetProperties.lookupPropertyDefinition(filterImpl,
							TimingProperties._NAME, TimingProperties.COMPUTE_EXECUTION_TIME);
					String[] parts = filterExecutionTime.split("\\.\\.");
					final UnitLiteral unitMin = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unitMin.setName(parts[0].replaceAll("[\\d]", "").trim());
					final IntegerLiteral executionTimeMin = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					executionTimeMin.setBase(0);
					executionTimeMin.setValue(Long.parseLong(parts[0].replaceAll("[\\D]", "").trim()));
					executionTimeMin.setUnit(unitMin);
					final UnitLiteral unitMax = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unitMax.setName(parts[1].replaceAll("[\\d]", "").trim());
					final IntegerLiteral executionTimeMax = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					executionTimeMax.setBase(0);
					executionTimeMax.setValue(Long.parseLong(parts[1].replaceAll("[\\D]", "").trim()));
					executionTimeMax.setUnit(unitMax);
					final RangeValue executionTimeRange = Aadl2Factory.eINSTANCE.createRangeValue();
					executionTimeRange.setMinimum(executionTimeMin);
					executionTimeRange.setMaximum(executionTimeMax);
					filterImpl.setPropertyValue(executionTimeProp, executionTimeRange);
				}

				// Domain
				if (compCategory == ComponentCategory.PROCESS && filterDomain != null) {
					if (CaseUtils.importCaseSchedulingPackage(pkgSection)) {
						final Property domainProp = GetProperties.lookupPropertyDefinition(filterImpl,
								CaseUtils.CASE_SCHEDULING_NAME, "Domain");
						final IntegerLiteral domainLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
						domainLit.setBase(0);
						domainLit.setValue(filterDomain);
						filterImpl.setPropertyValue(domainProp, domainLit);
					}
				}

				// Stack Size
				if (compCategory == ComponentCategory.THREAD && !filterStackSize.isEmpty()) {
					final Property stackSizeProp = GetProperties.lookupPropertyDefinition(filterImpl,
							MemoryProperties._NAME, MemoryProperties.STACK_SIZE);
					final IntegerLiteral stackSizeLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unit.setName(filterStackSize.replaceAll("[\\d]", "").trim());
					stackSizeLit.setBase(0);
					stackSizeLit.setValue(Long.parseLong(filterStackSize.replaceAll("[\\D]", "").trim()));
					stackSizeLit.setUnit(unit);
					filterImpl.setPropertyValue(stackSizeProp, stackSizeLit);
				}

				// Add it to proper place (just below component type)
//				pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);
				try {
					pkgSection.getOwnedClassifiers()
							.move(getIndex(containingImpl.getTypeName(), pkgSection.getOwnedClassifiers()),
									pkgSection.getOwnedClassifiers().size() - 1);
				} catch (Exception e) {

				}

				// Insert filter feature in process component implementation
				final Subcomponent filterSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
						compCategory);

				// Give it a unique name
				filterSubcomp.setName(ModelTransformUtils.getUniqueName(filterSubcomponentName, true,
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
					containingImpl.getOwnedConnections()
							.move(getIndex(destName, containingImpl.getOwnedConnections()) + 1,
									containingImpl.getOwnedConnections().size() - 1);
				} catch (Exception e) {

				}

				// Rewire selected connection so the filter is the destination
				selectedConnection.getDestination().setContext(filterSubcomp);
				selectedConnection.getDestination().setConnectionEnd(connEndIn);

				// AGREE
//				final String filterPolicyName = filterName + "_policy";
				final StringBuilder agreeClauses = new StringBuilder();
				if (!filterPolicy.isEmpty()) {

					// CASE::Component_Spec property
					String filterPropId = filterName + "_" + connEndOut.getName();
					CasePropertyUtils.setCompSpec(filterType, filterPropId);

					filterPolicy = filterPolicy.trim();

					agreeClauses.append("{**" + System.lineSeparator());

					// Filter policy
//					agreeClauses.append("property " + filterPolicyName + " = " + filterPolicy + System.lineSeparator());

					// Filter guarantee
					agreeClauses.append("guarantee " + filterPropId + " \"The filter output shall be well-formed\" :"
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
						agreeClauses
								.append(connEndOut.getName() + " = " + connEndIn.getName() + System.lineSeparator());
						agreeClauses.append("else" + System.lineSeparator());
						// User will need to put an expression after the 'else'
						agreeClauses.append(";" + System.lineSeparator());
					}

					agreeClauses.append("**}");

					if (!isSel4Process) {
						final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
								.createOwnedAnnexSubclause(filterType);
						annexSubclauseImpl.setName("agree");
						annexSubclauseImpl.setSourceText(agreeClauses.toString());
					}

				}

				// Add thread if this is a seL4 process
				if (isSel4Process) {

					// Create 'lift contract' statement in process implementation.
					// This will be done even if an AGREE property hasn't been specified yet.
					// That way if one is manually added later, everything will still work.
					final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
							.createOwnedAnnexSubclause(filterImpl);
					annexSubclauseImpl.setName("agree");
					annexSubclauseImpl.setSourceText(
							"{**" + System.lineSeparator() + "lift contract;" + System.lineSeparator() + "**}");

					Sel4TransformHandler.insertThreadInSel4Process((ProcessImplementation) filterImpl,
							filterDispatchProtocol, filterExecutionTime, filterStackSize, agreeClauses.toString());

				}

				// Add add_filter claims to resolute prove statement, if applicable
				if (!filterRequirement.isEmpty()) {
					final CyberRequirement req = RequirementsManager.getInstance().getRequirement(filterRequirement);
					claim = new AddFilterClaim(req.getContext(), filterSubcomp, connOut, featureClassifier);
				}

				return;

			}

			@Override
			public Collection<AddFilterClaim> getResult() {
				if (claim == null) {
					return null;
				} else {
					return Collections.singletonList(claim);
				}
			}

		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			domain.dispose();
		}

		@SuppressWarnings("unchecked")
		final List<AddFilterClaim> claim = (List<AddFilterClaim>) cmd.getResult();
		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(filterRequirement, claim.get(0));
		}

		return true;

	}

}
