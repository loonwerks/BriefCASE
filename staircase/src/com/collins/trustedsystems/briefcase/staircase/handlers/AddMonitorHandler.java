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
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ArrayDimension;
import org.osate.aadl2.Classifier;
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
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;
import org.osate.xtext.aadl2.properties.util.TimingProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.AddMonitorDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddMonitorClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class AddMonitorHandler extends AadlHandler {

	public static final String MONITOR_COMP_TYPE_NAME = "CASE_Monitor";
	public static final String MONITOR_OBSERVED_PORT_NAME = "Observed";
	public static final String MONITOR_GATE_PORT_NAME = "Output";
	public static final String MONITOR_ALERT_PORT_NAME = "Alert";
//	static final String MONITOR_ALERT_PORT_DATA_TYPE = "Base_Types::Boolean";
	public static final String MONITOR_RESET_PORT_NAME = "Reset";
	public static final String MONITOR_SUBCOMP_NAME = "Monitor";
	static final String CONNECTION_IMPL_NAME = "c";

	private String monitorComponentName;
	private String monitorSubcomponentName;
	private String observationPortName;
	private String observationGatePortName;
	private String dispatchProtocol;
	private String period;
	private String resetPortName;
	private String resetPort;
	private boolean latched;
	private Map<String, String> referencePorts;
	private String alertPortName;
	private String alertPort;
	private PortCategory alertPortCategory;
	private String alertPortDataType;
	private boolean observationGate;
	private String monitorRequirement;
	private String monitorAgreeProperty;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof Connection)) {
			Dialog.showError("Add Monitor",
					"A connection between two components must be selected to add a monitor.");
			return;
		}
		final Connection selectedConnection = (Connection) eObj;
		ComponentImplementation ci = selectedConnection.getContainingComponentImpl();

		// TODO: Check if monitor is being placed after a filter. If so, filter requirement claim needs
		// to be updated so filter_exists doesn't return false

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
		wizard.create(selectedConnection.getContainingComponentImpl());
		if (wizard.open() == Window.OK) {
			monitorComponentName = wizard.getMonitorComponentName();
			if (monitorComponentName.isEmpty()) {
				monitorComponentName = MONITOR_COMP_TYPE_NAME;
			}
			monitorSubcomponentName = wizard.getMonitorSubcomponentName();
			if (monitorSubcomponentName.isEmpty()) {
				monitorSubcomponentName = MONITOR_SUBCOMP_NAME;
			}
			observationPortName = wizard.getObservationPortName();
			if (observationPortName.isEmpty()) {
				observationPortName = MONITOR_OBSERVED_PORT_NAME;
			}
			observationGatePortName = wizard.getObservationGatePortName();
			if (observationGatePortName.isEmpty()) {
				observationGatePortName = MONITOR_GATE_PORT_NAME;
			}
			resetPortName = wizard.getResetPortName();
			if (resetPortName.isEmpty()) {
				resetPortName = MONITOR_RESET_PORT_NAME;
			}
			resetPort = wizard.getResetPort();
			latched = wizard.getLatched();
			dispatchProtocol = wizard.getDispatchProtocol();
			period = wizard.getPeriod();
			referencePorts = wizard.getReferencePorts();
			alertPortName = wizard.getAlertPortName();
			if (alertPortName.isEmpty()) {
				alertPortName = MONITOR_ALERT_PORT_NAME;
			}
			alertPort = wizard.getAlertPort();
			alertPortCategory = wizard.getAlertPortCategory();
			alertPortDataType = wizard.getAlertPortDataType();
			observationGate = wizard.getObservationGate();
			monitorRequirement = wizard.getRequirement();
			monitorAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the monitor component
		insertMonitor(uri);

		BriefcaseNotifier.notify("StairCASE - Monitor", "Monitor added to model.");

		// Save
		saveChanges(false);

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
			Connection selectedConnection = (Connection) resource.getEObject(uri.fragment());
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

			// Figure out component type by looking at the component type of the source or destination component
			// Note that the context could be null if the connection end is the containing component
			Subcomponent context = (Subcomponent) selectedConnection.getDestination().getContext();
			if (context == null) {
				context = (Subcomponent) selectedConnection.getSource().getContext();
			}
			ComponentCategory compCategory = context.getCategory();

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
			monitorType.setName(
					ModelTransformUtils.getUniqueName(monitorComponentName, true, pkgSection.getOwnedClassifiers()));

			// Create monitor observed port
			ConnectionEnd portSrc = selectedConnection.getSource().getConnectionEnd();
			ConnectionEnd portObserved = null;
			NamedElement observedDataFeatureClassifier = null;
			if (portSrc instanceof EventDataPort) {
				observedDataFeatureClassifier = ((EventDataPort) portSrc).getDataFeatureClassifier();
				portObserved = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				((EventDataPort) portObserved)
						.setDataFeatureClassifier((DataSubcomponentType) observedDataFeatureClassifier);
				for (ArrayDimension dim : ((EventDataPort) portSrc).getArrayDimensions()) {
					ArrayDimension arrayDimension = ((EventDataPort) portObserved).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
			} else if (portSrc instanceof DataPort) {
				observedDataFeatureClassifier = ((DataPort) portSrc).getDataFeatureClassifier();
				portObserved = ComponentCreateHelper.createOwnedDataPort(monitorType);
				((DataPort) portObserved)
						.setDataFeatureClassifier((DataSubcomponentType) observedDataFeatureClassifier);
				for (ArrayDimension dim : ((DataPort) portSrc).getArrayDimensions()) {
					ArrayDimension arrayDimension = ((DataPort) portObserved).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
			} else if (portSrc instanceof EventPort) {
				portObserved = ComponentCreateHelper.createOwnedEventPort(monitorType);
			} else if (portSrc instanceof FeatureGroup) {
				portObserved = monitorType.createOwnedFeatureGroup();
				((FeatureGroup) portObserved).setFeatureType(((FeatureGroup) portSrc).getFeatureGroupType());
			} else {
				Dialog.showError("Add Monitor", "Could not determine the port type of the destination component.");
				return null;
			}

			((DirectedFeature) portObserved).setIn(true);
			portObserved.setName(observationPortName);

			// Create reference ports
			Map<ConnectionEnd, ConnectionEnd> monReferencePorts = new HashMap<>();
			DataSubcomponentType dataFeatureClassifier = null;
			for (Map.Entry<String, String> refEntry : referencePorts.entrySet()) {
				ConnectionEnd monRefPort = null;
				ConnectionEnd srcRefPort = ModelTransformUtils.getPort(containingImpl, refEntry.getValue());
				// If user didn't specify an expected outport, use the same type as the observed port
				if (srcRefPort == null) {
					srcRefPort = portObserved;
				}
				if (srcRefPort instanceof EventDataPort) {
					monRefPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					dataFeatureClassifier = ((EventDataPort) srcRefPort).getDataFeatureClassifier();
					((EventDataPort) monRefPort).setDataFeatureClassifier(dataFeatureClassifier);
					for (ArrayDimension dim : ((EventDataPort) srcRefPort).getArrayDimensions()) {
						ArrayDimension arrayDimension = ((EventDataPort) monRefPort).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (srcRefPort instanceof DataPort) {
					monRefPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
					dataFeatureClassifier = ((DataPort) srcRefPort).getDataFeatureClassifier();
					((DataPort) monRefPort).setDataFeatureClassifier(dataFeatureClassifier);
					for (ArrayDimension dim : ((DataPort) srcRefPort).getArrayDimensions()) {
						ArrayDimension arrayDimension = ((DataPort) monRefPort).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (srcRefPort instanceof EventPort) {
					monRefPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
				} else if (srcRefPort instanceof FeatureGroup) {
					monRefPort = monitorType.createOwnedFeatureGroup();
					((FeatureGroup) monRefPort).setFeatureType(((FeatureGroup) srcRefPort).getFeatureGroupType());
				}
				((DirectedFeature) monRefPort).setIn(true);
				monRefPort.setName(refEntry.getKey());
				monReferencePorts.put(srcRefPort, monRefPort);
			}

			// Create monitor alert port
			Port monAlertPort = null;
			final ConnectionEnd dstAlertPort = ModelTransformUtils.getPort(containingImpl, alertPort);
			DataSubcomponentType alertDataFeatureClassifier = null;
			if (!alertPortDataType.isEmpty()) {
				alertDataFeatureClassifier = Aadl2GlobalScopeUtil.get(containingImpl,
						Aadl2Package.eINSTANCE.getDataSubcomponentType(), alertPortDataType);
				if (alertDataFeatureClassifier == null) {
					// Aadl2GlobalScopeUtil.get() doesn't seem to find elements in current package
					for (Classifier c : pkgSection.getOwnedClassifiers()) {
						if (c.getQualifiedName().equalsIgnoreCase(alertPortDataType)
								&& c instanceof DataSubcomponentType) {
							alertDataFeatureClassifier = (DataSubcomponentType) c;
							break;
						}
					}
				} else {
					ModelTransformUtils.importContainingPackage(alertDataFeatureClassifier, pkgSection);
				}
			}
			// If user didn't specify an alert inport, make it an event data port with no type
			if (dstAlertPort == null) {
				if (alertPortCategory != null) {
					if (alertPortCategory == PortCategory.DATA) {
						monAlertPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
						if (alertDataFeatureClassifier != null) {
							((DataPort) monAlertPort).setDataFeatureClassifier(alertDataFeatureClassifier);
						}
					} else if (alertPortCategory == PortCategory.EVENT) {
						monAlertPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
					} else {
						monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
						if (alertDataFeatureClassifier != null) {
							((EventDataPort) monAlertPort).setDataFeatureClassifier(alertDataFeatureClassifier);
						}
					}
				} else {
					monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					if (alertDataFeatureClassifier != null) {
						((EventDataPort) monAlertPort).setDataFeatureClassifier(alertDataFeatureClassifier);
					}
				}
			} else if (dstAlertPort instanceof EventDataPort) {
				monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				dataFeatureClassifier = ((EventDataPort) dstAlertPort).getDataFeatureClassifier();
				((EventDataPort) monAlertPort).setDataFeatureClassifier(dataFeatureClassifier);
				for (ArrayDimension dim : ((EventDataPort) dstAlertPort).getArrayDimensions()) {
					ArrayDimension arrayDimension = ((EventDataPort) monAlertPort).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
			} else if (dstAlertPort instanceof DataPort) {
				monAlertPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
				dataFeatureClassifier = ((DataPort) dstAlertPort).getDataFeatureClassifier();
				((DataPort) monAlertPort).setDataFeatureClassifier(dataFeatureClassifier);
				for (ArrayDimension dim : ((DataPort) dstAlertPort).getArrayDimensions()) {
					ArrayDimension arrayDimension = ((DataPort) monAlertPort).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
			} else if (dstAlertPort instanceof EventPort) {
				monAlertPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
			} else if (dstAlertPort instanceof FeatureGroup) {
				// TODO: Names of all feature group features should be provided so user can pick one

			}
			monAlertPort.setOut(true);
			monAlertPort.setName(alertPortName);

			// Create observation gate output port, if needed
			ConnectionEnd monGatePort = null;
			if (observationGate) {
				final ConnectionEnd dstGatePort = selectedConnection.getDestination().getConnectionEnd();
				// If user didn't specify a gate inport, make it an event data port
				if (dstGatePort == null) {
					monGatePort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				} else if (dstGatePort instanceof EventDataPort) {
					monGatePort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					dataFeatureClassifier = ((EventDataPort) dstGatePort).getDataFeatureClassifier();
					((EventDataPort) monGatePort).setDataFeatureClassifier(dataFeatureClassifier);
					for (ArrayDimension dim : ((EventDataPort) dstGatePort).getArrayDimensions()) {
						ArrayDimension arrayDimension = ((EventDataPort) monGatePort).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (dstGatePort instanceof DataPort) {
					monGatePort = ComponentCreateHelper.createOwnedDataPort(monitorType);
					dataFeatureClassifier = ((DataPort) dstGatePort).getDataFeatureClassifier();
					((DataPort) monGatePort).setDataFeatureClassifier(dataFeatureClassifier);
					for (ArrayDimension dim : ((DataPort) dstGatePort).getArrayDimensions()) {
						ArrayDimension arrayDimension = ((DataPort) monGatePort).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (dstGatePort instanceof EventPort) {
					monGatePort = ComponentCreateHelper.createOwnedEventPort(monitorType);
				} else if (dstGatePort instanceof FeatureGroup) {
					monGatePort = monitorType.createOwnedFeatureGroup();
					((FeatureGroup) monGatePort).setFeatureType(((FeatureGroup) dstGatePort).getFeatureGroupType());
				}
				((DirectedFeature) monGatePort).setOut(true);
				monGatePort.setName(observationGatePortName);
			}

			// Create monitor reset port, if needed
			ConnectionEnd monResetPort = null;
			ConnectionEnd srcResetPort = null;
			if (resetPort != null) {
				srcResetPort = ModelTransformUtils.getPort(containingImpl, resetPort);
				// If user didn't specify a reset outport, make it an event data port
				if (srcResetPort == null) {
					monResetPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				} else if (srcResetPort instanceof EventDataPort) {
					monResetPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					dataFeatureClassifier = ((EventDataPort) srcResetPort).getDataFeatureClassifier();
					((EventDataPort) monResetPort).setDataFeatureClassifier(dataFeatureClassifier);
					for (ArrayDimension dim : ((EventDataPort) srcResetPort).getArrayDimensions()) {
						ArrayDimension arrayDimension = ((EventDataPort) monResetPort).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (srcResetPort instanceof DataPort) {
					monResetPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
					dataFeatureClassifier = ((DataPort) srcResetPort).getDataFeatureClassifier();
					((DataPort) monResetPort).setDataFeatureClassifier(dataFeatureClassifier);
					for (ArrayDimension dim : ((DataPort) srcResetPort).getArrayDimensions()) {
						ArrayDimension arrayDimension = ((DataPort) monResetPort).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (srcResetPort instanceof EventPort) {
					monResetPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
				} else if (srcResetPort instanceof FeatureGroup) {
					monResetPort = monitorType.createOwnedFeatureGroup();
					((FeatureGroup) monResetPort).setFeatureType(((FeatureGroup) srcResetPort).getFeatureGroupType());
				}
				((DirectedFeature) monResetPort).setIn(true);
				monResetPort.setName(resetPortName);
			}

			// Add monitor properties
			// CASE_Properties::Component_Type Property
			if (!CasePropertyUtils.setMitigationType(monitorType, MITIGATION_TYPE.MONITOR)) {
//				return null;
			}

			// CASE_Properties::Component_Spec property
			String monitorAlertPropId = monitorType.getName() + "_" + alertPortName;
			String monitorGatePropId = "";
			if (observationGate) {
				monitorGatePropId += monitorType.getName() + "_" + observationGatePortName;
			}
			if (!CasePropertyUtils.setCompSpec(monitorType,
					monitorAlertPropId + (monitorGatePropId.isEmpty() ? "" : "," + monitorGatePropId))) {
//				return null;
			}

//			// CASE_Properties::Monitor_Latched
//			if (!CasePropertyUtils.setMonitorLatched(monitorType, latched)) {
//				return null;
//			}

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
			// Period
			if (!period.isEmpty() && compCategory == ComponentCategory.THREAD) {
				Property periodProp = GetProperties.lookupPropertyDefinition(monitorImpl, TimingProperties._NAME,
						TimingProperties.PERIOD);
				IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
				UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
				unit.setName(period.replaceAll("[\\d]", "").trim());
				periodLit.setBase(0);
				periodLit.setValue(Long.parseLong(period.replaceAll("[\\D]", "").trim()));
				periodLit.setUnit(unit);
				monitorImpl.setPropertyValue(periodProp, periodLit);
			}

			// Insert monitor subcomponent in containing component implementation
			final Subcomponent monitorSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
					compCategory);

			// Give it a unique name
			monitorSubcomp
					.setName(ModelTransformUtils.getUniqueName(monitorSubcomponentName, true,
							containingImpl.getOwnedSubcomponents()));

			ComponentCreateHelper.setSubcomponentType(monitorSubcomp, monitorImpl);

			// Create a connection from selected connection source to monitor observed input
			final Connection connObserved = ComponentCreateHelper.createOwnedConnection(containingImpl,
					selectedConnection.getSource().getConnectionEnd());
			// Give it a unique name
			connObserved
					.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
							containingImpl.getOwnedConnections()));
			connObserved.setBidirectional(false);
			final ConnectedElement monitorObservedSrc = connObserved.createSource();
			monitorObservedSrc.setContext(selectedConnection.getSource().getContext());
			monitorObservedSrc.setConnectionEnd(selectedConnection.getSource().getConnectionEnd());
			final ConnectedElement monitorObservedDst = connObserved.createDestination();
			monitorObservedDst.setContext(monitorSubcomp);
			monitorObservedDst.setConnectionEnd(portObserved);

			// Put portConnObserved in right place (after selected connection)
			String destName = selectedConnection.getName();
			containingImpl.getOwnedConnections().move(getIndex(destName, containingImpl.getOwnedConnections()) + 1,
					containingImpl.getOwnedPortConnections().size() - 1);

			// Change selected connection source to monitor gate output port, if needed
			if (observationGate) {
				selectedConnection.getSource().setConnectionEnd(monGatePort);
				selectedConnection.getSource().setContext(monitorSubcomp);
			}

			// Create Reference port connections, if provided
			if (!referencePorts.isEmpty()) {
				for (Map.Entry<ConnectionEnd, ConnectionEnd> portEntry : monReferencePorts.entrySet()) {
					final Connection connExpected = ComponentCreateHelper.createOwnedConnection(containingImpl,
							portEntry.getKey());
					connExpected.setName(
							ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
									containingImpl.getOwnedConnections()));
					connExpected.setBidirectional(false);
					final ConnectedElement monitorExpectedSrc = connExpected.createSource();
					monitorExpectedSrc.setContext(
							ModelTransformUtils.getSubcomponent(containingImpl,
									referencePorts.get(portEntry.getValue().getName())));
					monitorExpectedSrc.setConnectionEnd(portEntry.getKey());
					final ConnectedElement monitorExpectedDst = connExpected.createDestination();
					monitorExpectedDst.setContext(monitorSubcomp);
					monitorExpectedDst.setConnectionEnd(portEntry.getValue());
					// Put connExpected in right place (before connObserved)
					destName = connObserved.getName();
					containingImpl.getOwnedConnections().move(getIndex(destName, containingImpl.getOwnedConnections()),
							containingImpl.getOwnedConnections().size() - 1);
				}
			}

			// Create Alert connection, if provided
			Connection connAlert = null;
			if (!alertPort.isEmpty()) {
				connAlert = ComponentCreateHelper.createOwnedConnection(containingImpl, monAlertPort);
				connAlert
						.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
								containingImpl.getOwnedConnections()));
				connAlert.setBidirectional(false);
				final ConnectedElement monitorAlertSrc = connAlert.createSource();
				monitorAlertSrc.setContext(monitorSubcomp);
				monitorAlertSrc.setConnectionEnd(monAlertPort);
				final ConnectedElement monitorAlertDst = connAlert.createDestination();
				monitorAlertDst.setContext(ModelTransformUtils.getSubcomponent(containingImpl, alertPort));
				monitorAlertDst.setConnectionEnd(dstAlertPort);
				// Put connAlert in right place (after connObserved)
				destName = connObserved.getName();
				containingImpl.getOwnedConnections().move(getIndex(destName, containingImpl.getOwnedConnections()) + 1,
						containingImpl.getOwnedConnections().size() - 1);
			}

			// Create Reset connection, if provided
			if (resetPort != null && !resetPort.isEmpty()) {
				final Connection connReset = ComponentCreateHelper.createOwnedConnection(containingImpl, monResetPort);
				connReset
						.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
								containingImpl.getOwnedConnections()));
				connReset.setBidirectional(false);
				final ConnectedElement monitorResetSrc = connReset.createSource();
				monitorResetSrc.setContext(ModelTransformUtils.getSubcomponent(containingImpl, resetPort));
				monitorResetSrc.setConnectionEnd(srcResetPort);
				final ConnectedElement monitorResetDst = connReset.createDestination();
				monitorResetDst.setContext(monitorSubcomp);
				monitorResetDst.setConnectionEnd(monResetPort);
				// Put connExpected in right place (after observed)
				destName = connObserved.getName();
				containingImpl.getOwnedConnections().move(getIndex(destName, containingImpl.getOwnedConnections()) + 1,
						containingImpl.getOwnedConnections().size() - 1);
			}

			// AGREE
			if (monitorAgreeProperty.isEmpty()) {
				monitorAgreeProperty = "false;";
			} else if (!monitorAgreeProperty.trim().endsWith(";")) {
				monitorAgreeProperty = monitorAgreeProperty.trim() + ";";
			}

			String monitorPolicyName = monitorType.getName() + "_policy";

			String resetString = "";
			if (resetPort != null) {
				if (monResetPort instanceof EventPort || monResetPort instanceof EventDataPort) {
					resetString = "not event(" + resetPortName + ") and ";
				} else {
					resetString = "not " + resetPortName + " and ";
				}
			}

			StringBuilder agreeClauses = new StringBuilder();
			agreeClauses.append("{**" + System.lineSeparator());

			agreeClauses
					.append("const is_latched : bool = " + (latched ? "true" : "false") + ";"
					+ System.lineSeparator());
			agreeClauses
					.append("property " + monitorPolicyName + " = " + monitorAgreeProperty + System.lineSeparator());

			agreeClauses.append("property alerted = (not " + monitorPolicyName + ") -> ((" + resetString
					+ "is_latched and pre(alerted)) or (");
			if (portObserved instanceof EventPort || portObserved instanceof EventDataPort) {
				agreeClauses.append("event(" + observationPortName + ") and ");
			}
			agreeClauses.append("not " + monitorPolicyName + "));" + System.lineSeparator());

			String alertExpr = "";
			if (monAlertPort instanceof EventPort || monAlertPort instanceof EventDataPort) {
				alertExpr = "alerted <=> event(" + alertPortName + ")";
			}
			if (alertExpr.isEmpty()) {
				alertExpr = "false";
				agreeClauses.append("-- GUARANTEE EXPRESSION INCOMPLETE" + System.lineSeparator());
			}
			agreeClauses.append("guarantee " + monitorAlertPropId
					+ " \"A violation of the monitor policy shall trigger an alert\" : " + alertExpr + ";"
					+ System.lineSeparator());

			if (observationGate) {
				String gateExpr = "";
				if (monGatePort instanceof EventPort || monGatePort instanceof EventDataPort) {
					gateExpr = "if alerted then not event(" + observationGatePortName + ") else event("
							+ observationGatePortName + ") and " + observationGatePortName + " = "
							+ observationPortName;
				} else {
					gateExpr = "false";
					agreeClauses.append("-- GUARANTEE EXPRESSION INCOMPLETE" + System.lineSeparator());
				}
				agreeClauses.append("guarantee " + monitorGatePropId
						+ " \"A violation of the monitor policy shall prevent propagation of the Observed input.\" : "
						+ gateExpr + ";" + System.lineSeparator());

			}

			agreeClauses.append("**}");

			final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
					.createOwnedAnnexSubclause(monitorType);
			annexSubclauseImpl.setName("agree");
			annexSubclauseImpl.setSourceText(agreeClauses.toString());

			if (isProcess) {

				// TODO: Wrap thread component in a process

				// TODO: Bind process to processor
			}

			// Add add_monitor claims to resolute prove statement, if applicable
			if (!monitorRequirement.isEmpty()) {
				CyberRequirement req = RequirementsManager.getInstance().getRequirement(monitorRequirement);
				if (observationGate) {
					String gateContextName = "";
					if (selectedConnection.getDestination() != null
							&& selectedConnection.getDestination().getContext() != null) {
						gateContextName = selectedConnection.getDestination().getContext().getName();
					}
					return new AddMonitorClaim(req.getContext(), monitorSubcomp, gateContextName,
							observedDataFeatureClassifier);
				} else {
					return new AddMonitorClaim(req.getContext(), monitorSubcomp);
				}
			}

			return null;
		});

		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(monitorRequirement, claim);
		}

	}

}
