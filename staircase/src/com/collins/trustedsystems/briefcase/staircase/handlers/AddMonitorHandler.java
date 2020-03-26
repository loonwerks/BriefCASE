package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.AddMonitorDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddMonitorClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class AddMonitorHandler extends AadlHandler {

	static final String MONITOR_COMP_TYPE_NAME = "CASE_Monitor";
	static final String MONITOR_OBSERVED_PORT_NAME = "observed";
	static final String MONITOR_GATE_PORT_NAME = "output";
	static final String MONITOR_ALERT_PORT_NAME = "alert";
//	static final String MONITOR_ALERT_PORT_DATA_TYPE = "Base_Types::Boolean";
	static final String MONITOR_RESET_PORT_NAME = "reset";
	public static final String MONITOR_COMP_IMPL_NAME = "MON";
	static final String CONNECTION_IMPL_NAME = "c";

	private String monitorImplementationName;
	private String dispatchProtocol;
	private String resetPort;
	private boolean latched;
	private Map<String, String> referencePorts;
	private String alertPort;
	private boolean observationGate;
	private String monitorRequirement;
	private String monitorAgreeProperty;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof PortConnection)) {
			Dialog.showError("Add Monitor",
					"A connection between two components must be selected to add a monitor.");
			return;
		}
		final PortConnection selectedConnection = (PortConnection) eObj;
		ComponentImplementation ci = selectedConnection.getContainingComponentImpl();

		// Provide list of outports that can be connected to monitor's expected in port
		List<String> outports = ModelTransformUtils.getOutports(ci);

		// Provide list of inports that monitor's alert out port can be connected to
		List<String> inports = ModelTransformUtils.getInports(ci);

		// Provide list of requirements so the user can choose which requirement is driving this
		// model transformation.
		List<String> requirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));

		// Open wizard to enter monitor info
		AddMonitorDialog wizard = new AddMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		wizard.setPorts(inports, outports);
		wizard.setRequirements(requirements);
		wizard.create();
		if (wizard.open() == Window.OK) {
			monitorImplementationName = wizard.getMonitorImplementationName();
			if (monitorImplementationName == "") {
				monitorImplementationName = MONITOR_COMP_IMPL_NAME;
			}
			resetPort = wizard.getResetPort();
			latched = wizard.getLatched();
			dispatchProtocol = wizard.getDispatchProtocol();
			referencePorts = wizard.getReferencePorts();
			alertPort = wizard.getAlertPort();
			observationGate = wizard.getObservationGate();
			monitorRequirement = wizard.getRequirement();
			monitorAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the monitor component
		insertMonitor(uri);

		BriefcaseNotifier.notify("StairCASE - Monitor", "Monitor added to model.");

		return;

	}

	/**
	 * Inserts a monitor component into the model, including monitor type definition
	 * and implementation (including correct wiring for monitored signal).
	 * The monitor is inserted at the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	private void insertMonitor(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		AddMonitorClaim claim = xtextEditor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			PortConnection selectedConnection = (PortConnection) resource.getEObject(uri.fragment());
			final ComponentImplementation containingImpl = selectedConnection.getContainingComponentImpl();
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
				Dialog.showError("Add Monitor", "No public or private package sections found.");
				return null;
			}

			// Import CASE_Properties file
			if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
				return null;
			}
//			// Import CASE_Model_Transformations file
//			if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
//				return null;
//			}

			// Figure out component type by looking at the component type of the destination component
			ComponentCategory compCategory = ((Subcomponent) selectedConnection.getDestination().getContext())
					.getCategory();

			// If the component type is a process, we will need to put a single thread inside.
			// Per convention, we will attach all properties and contracts to the thread.
			// For this model transformation, we will create the thread first, then wrap it in a process
			// component, using the same mechanism we use for the seL4 transformation
			boolean isProcess = (compCategory == ComponentCategory.PROCESS);
			if (isProcess) {
				compCategory = ComponentCategory.THREAD;
			} else if (compCategory == ComponentCategory.THREAD_GROUP) {
				compCategory = ComponentCategory.THREAD;
			}

			final ComponentType monitorType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

			// Give it a unique name
			monitorType.setName(getUniqueName(MONITOR_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

			// Create monitor observed port
			Port portSrc = (Port) selectedConnection.getSource().getConnectionEnd();
			Port portObserved = null;
			DataSubcomponentType observedDataFeatureClassifier = null;
			if (portSrc instanceof EventDataPort) {
				observedDataFeatureClassifier = ((EventDataPort) portSrc).getDataFeatureClassifier();
				portObserved = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				((EventDataPort) portObserved).setDataFeatureClassifier(observedDataFeatureClassifier);
			} else if (portSrc instanceof DataPort) {
				observedDataFeatureClassifier = ((DataPort) portSrc).getDataFeatureClassifier();
				portObserved = ComponentCreateHelper.createOwnedDataPort(monitorType);
				((DataPort) portObserved).setDataFeatureClassifier(observedDataFeatureClassifier);
			} else if (portSrc instanceof EventPort) {
				portObserved = ComponentCreateHelper.createOwnedEventPort(monitorType);
				return null;
			} else {
				Dialog.showError("Add Monitor", "Could not determine the port type of the destination component.");
				return null;
			}

			portObserved.setIn(true);
			portObserved.setName(MONITOR_OBSERVED_PORT_NAME);

			// Create reference ports
			Map<Port, Port> monReferencePorts = new HashMap<>();
			DataSubcomponentType dataFeatureClassifier = null;
			for (Map.Entry<String, String> refEntry : referencePorts.entrySet()) {
				Port monRefPort = null;
				Port srcRefPort = ModelTransformUtils.getPort(containingImpl, refEntry.getValue());
				// If user didn't specify an expected outport, use the same type as the observed port
				if (srcRefPort == null) {
					srcRefPort = portObserved;
				}
				if (srcRefPort instanceof EventDataPort) {
					monRefPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					dataFeatureClassifier = ((EventDataPort) srcRefPort).getDataFeatureClassifier();
					((EventDataPort) monRefPort).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (srcRefPort instanceof DataPort) {
					monRefPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
					dataFeatureClassifier = ((DataPort) srcRefPort).getDataFeatureClassifier();
					((DataPort) monRefPort).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (srcRefPort instanceof EventPort) {
					monRefPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
				}
				monRefPort.setIn(true);
				monRefPort.setName(refEntry.getKey());
				monReferencePorts.put(srcRefPort, monRefPort);
			}

			// Create monitor alert port
			Port monAlertPort = null;
			final Port dstAlertPort = ModelTransformUtils.getPort(containingImpl, alertPort);
			// If user didn't specify an alert inport, make it an event data port
			if (dstAlertPort == null) {
				monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
			} else if (dstAlertPort instanceof EventDataPort) {
				monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				dataFeatureClassifier = ((EventDataPort) dstAlertPort).getDataFeatureClassifier();
				((EventDataPort) monAlertPort).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (dstAlertPort instanceof DataPort) {
				monAlertPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
				dataFeatureClassifier = ((DataPort) dstAlertPort).getDataFeatureClassifier();
				((DataPort) monAlertPort).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (dstAlertPort instanceof EventPort) {
				monAlertPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
			}
			monAlertPort.setOut(true);
			monAlertPort.setName(MONITOR_ALERT_PORT_NAME);

			// Create observation gate output port, if needed
			Port monGatePort = null;
			if (observationGate) {
				final Port dstGatePort = (Port) selectedConnection.getDestination().getConnectionEnd();
//				// If user didn't specify a gate inport, make it an event data port
				if (dstGatePort == null) {
					monGatePort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				} else if (dstGatePort instanceof EventDataPort) {
					monGatePort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					dataFeatureClassifier = ((EventDataPort) dstGatePort).getDataFeatureClassifier();
					((EventDataPort) monGatePort).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (dstGatePort instanceof DataPort) {
					monGatePort = ComponentCreateHelper.createOwnedDataPort(monitorType);
					dataFeatureClassifier = ((DataPort) dstGatePort).getDataFeatureClassifier();
					((DataPort) monGatePort).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (dstAlertPort instanceof EventPort) {
					monGatePort = ComponentCreateHelper.createOwnedEventPort(monitorType);
				}
				monGatePort.setOut(true);
				monGatePort.setName(MONITOR_GATE_PORT_NAME);
			}

			// Create monitor reset port, if needed
			Port monResetPort = null;
			Port srcResetPort = null;
			if (resetPort != null) {
				srcResetPort = ModelTransformUtils.getPort(containingImpl, resetPort);
				// If user didn't specify a reset outport, make it an event data port
				if (srcResetPort == null) {
					monResetPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				} else if (srcResetPort instanceof EventDataPort) {
					monResetPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					dataFeatureClassifier = ((EventDataPort) srcResetPort).getDataFeatureClassifier();
					((EventDataPort) monResetPort).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (srcResetPort instanceof DataPort) {
					monResetPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
					dataFeatureClassifier = ((DataPort) srcResetPort).getDataFeatureClassifier();
					((DataPort) monResetPort).setDataFeatureClassifier(dataFeatureClassifier);
				} else if (srcResetPort instanceof EventPort) {
					monResetPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
				}
				monResetPort.setIn(true);
				monResetPort.setName(MONITOR_RESET_PORT_NAME);
			}

			// Add monitor properties
			// CASE::COMP_TYPE Property
			if (!CasePropertyUtils.setCompType(monitorType, "MONITOR")) {
//			if (!CasePropertyUtils.addCasePropertyAssociation(CasePropertyUtils.COMP_TYPE, "MONITOR", monitorType)) {
//					return;
			}

			// CASE::COMP_SPEC property
			// Parse the ID from the Monitor AGREE property
			String monitorPropId = "Req_" + monitorType.getName();
			if (!monitorAgreeProperty.isEmpty()) {
				String monitorSpec = monitorPropId + "_alert";
				if (observationGate) {
					monitorSpec += "," + monitorPropId + "_gate";
				}
				if (!CasePropertyUtils.setCompSpec(monitorType, monitorSpec)) {
//				if (!CasePropertyUtils.addCasePropertyAssociation(CasePropertyUtils.COMP_SPEC, monitorSpec,
//						monitorType)) {
//						return;
				}
			}

			// Move to top of file
			pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);

			// Create monitor implementation
			final ComponentImplementation monitorImpl = (ComponentImplementation) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			monitorImpl.setName(monitorType.getName() + ".Impl");
			final Realization r = monitorImpl.createOwnedRealization();
			r.setImplemented(monitorType);

			// Move below component type
			pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);

//			// CASE::COMP_IMPL property
//			if (!monitorImplementationLanguage.isEmpty()) {
//				if (!CaseUtils.addCasePropertyAssociation("COMP_IMPL", monitorImplementationLanguage, monitorImpl)) {
////						return;
//				}
//			}

			// Dispatch protocol property
			if (!dispatchProtocol.isEmpty() && compCategory == ComponentCategory.THREAD) {
				Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(monitorImpl,
						ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
				EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
				dispatchProtocolLit.setName(dispatchProtocol);
				NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
				nv.setNamedValue(dispatchProtocolLit);
				monitorImpl.setPropertyValue(dispatchProtocolProp, nv);
			}

			// Insert monitor subcomponent in containing component implementation
			final Subcomponent monitorSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
					compCategory);

			// Give it a unique name
			monitorSubcomp
					.setName(getUniqueName(monitorImplementationName, true, containingImpl.getOwnedSubcomponents()));

			ComponentCreateHelper.setSubcomponentType(monitorSubcomp, monitorImpl);

			// Create a connection from selected connection source to monitor observed input
			final PortConnection portConnObserved = containingImpl.createOwnedPortConnection();
			// Give it a unique name
			portConnObserved
					.setName(getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
			portConnObserved.setBidirectional(false);
			final ConnectedElement monitorObservedSrc = portConnObserved.createSource();
			monitorObservedSrc.setContext(selectedConnection.getSource().getContext());
			monitorObservedSrc.setConnectionEnd(selectedConnection.getSource().getConnectionEnd());
			final ConnectedElement monitorObservedDst = portConnObserved.createDestination();
			monitorObservedDst.setContext(monitorSubcomp);
			monitorObservedDst.setConnectionEnd(portObserved);

			// Put portConnObserved in right place (after selected connection)
			String destName = selectedConnection.getName();
			containingImpl.getOwnedPortConnections().move(
					getIndex(destName, containingImpl.getOwnedPortConnections()) + 1,
					containingImpl.getOwnedPortConnections().size() - 1);

			// Change selected connection source to monitor gate output port, if needed
			if (observationGate) {
				selectedConnection.getSource().setConnectionEnd(monGatePort);
				selectedConnection.getSource().setContext(monitorSubcomp);
			}

			// Create Reference port connections, if provided
			if (!referencePorts.isEmpty()) {
				for (Map.Entry<Port, Port> portEntry : monReferencePorts.entrySet()) {
					final PortConnection portConnExpected = containingImpl.createOwnedPortConnection();
					portConnExpected.setName(
							getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
					portConnExpected.setBidirectional(false);
					final ConnectedElement monitorExpectedSrc = portConnExpected.createSource();
					monitorExpectedSrc.setContext(
							ModelTransformUtils.getSubcomponent(containingImpl,
									referencePorts.get(portEntry.getValue().getName())));
					monitorExpectedSrc.setConnectionEnd(portEntry.getKey());
					final ConnectedElement monitorExpectedDst = portConnExpected.createDestination();
					monitorExpectedDst.setContext(monitorSubcomp);
					monitorExpectedDst.setConnectionEnd(portEntry.getValue());
					// Put portConnExpected in right place (before portConnObserved)
					destName = portConnObserved.getName();
					containingImpl.getOwnedPortConnections().move(
							getIndex(destName, containingImpl.getOwnedPortConnections()),
							containingImpl.getOwnedPortConnections().size() - 1);
				}
			}

			// Create Alert connection, if provided
			PortConnection portConnAlert = null;
			if (!alertPort.isEmpty()) {
				portConnAlert = containingImpl.createOwnedPortConnection();
				portConnAlert
						.setName(getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
				portConnAlert.setBidirectional(false);
				final ConnectedElement monitorAlertSrc = portConnAlert.createSource();
				monitorAlertSrc.setContext(monitorSubcomp);
				monitorAlertSrc.setConnectionEnd(monAlertPort);
				final ConnectedElement monitorAlertDst = portConnAlert.createDestination();
				monitorAlertDst.setContext(ModelTransformUtils.getSubcomponent(containingImpl, alertPort));
				monitorAlertDst.setConnectionEnd(dstAlertPort);
				// Put portConnAlert in right place (after portConnObserved)
				destName = portConnObserved.getName();
				containingImpl.getOwnedPortConnections().move(
						getIndex(destName, containingImpl.getOwnedPortConnections()) + 1,
						containingImpl.getOwnedPortConnections().size() - 1);
			}

			// Create Reset connection, if provided
			if (resetPort != null && !resetPort.isEmpty()) {
				final PortConnection portConnReset = containingImpl.createOwnedPortConnection();
				portConnReset
						.setName(getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
				portConnReset.setBidirectional(false);
				final ConnectedElement monitorResetSrc = portConnReset.createSource();
				monitorResetSrc.setContext(ModelTransformUtils.getSubcomponent(containingImpl, resetPort));
				monitorResetSrc.setConnectionEnd(srcResetPort);
				final ConnectedElement monitorResetDst = portConnReset.createDestination();
				monitorResetDst.setContext(monitorSubcomp);
				monitorResetDst.setConnectionEnd(monResetPort);
				// Put portConnExpected in right place (after alert)
				destName = portConnAlert.getName();
				containingImpl.getOwnedPortConnections().move(
						getIndex(destName, containingImpl.getOwnedPortConnections()) + 1,
						containingImpl.getOwnedPortConnections().size() - 1);
			}

			// AGREE
			if (monitorAgreeProperty.length() > 0) {

				if (!monitorAgreeProperty.trim().endsWith(";")) {
					monitorAgreeProperty = monitorAgreeProperty.trim() + ";";
				}
				String monitorPolicy = monitorType.getName() + "_policy";
				String agreeClauses = "{**" + System.lineSeparator();

				agreeClauses += "const monitor_latched : bool = " + latched + ";" + System.lineSeparator();
				agreeClauses += "property " + monitorPolicy + " = " + monitorAgreeProperty
						+ System.lineSeparator();
				agreeClauses += "guarantee " + monitorPropId + "_alert"
						+ " \"A violation of the monitor policy shall trigger an alert\" : event(alert) = not "
						+ monitorPolicy + ";"
						+ System.lineSeparator();
				if (observationGate) {
					agreeClauses += "guarantee " + monitorPropId + "_gate"
							+ " \"A violation of the monitor policy shall prevent the observed message from propagating\" : if "
							+ monitorPolicy + " then event(" + MONITOR_GATE_PORT_NAME + ") and "
							+ MONITOR_GATE_PORT_NAME + " = " + MONITOR_OBSERVED_PORT_NAME + " else not event("
							+ MONITOR_GATE_PORT_NAME + ");" + System.lineSeparator();
				}
				agreeClauses += "**}";

				final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
						.createOwnedAnnexSubclause(monitorType);
				annexSubclauseImpl.setName("agree");
				annexSubclauseImpl.setSourceText(agreeClauses);
			}

			if (isProcess) {

				// TODO: Wrap thread component in a process

				// TODO: Bind process to processor
			}

			// Add add_monitor claims to resolute prove statement, if applicable
			if (!monitorRequirement.isEmpty()) {
				CyberRequirement req = RequirementsManager.getInstance().getRequirement(monitorRequirement);
				if (observationGate) {
					return new AddMonitorClaim(req.getContext(), monitorSubcomp,
						selectedConnection.getDestination().getContext().getQualifiedName(),
						observedDataFeatureClassifier);
				} else {
					return new AddMonitorClaim(req.getContext(), monitorSubcomp);
				}
			}

			return null;
		});

		RequirementsManager.getInstance().modifyRequirement(monitorRequirement, claim);

	}

}
