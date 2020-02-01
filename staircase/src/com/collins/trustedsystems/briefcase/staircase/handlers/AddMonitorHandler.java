package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
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
import org.osate.aadl2.Feature;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.AddMonitorDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddMonitorClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;

public class AddMonitorHandler extends AadlHandler {

	static final String MONITOR_COMP_TYPE_NAME = "CASE_Monitor";
	static final String MONITOR_EXPECTED_PORT_NAME = "expected";
	static final String MONITOR_OBSERVED_PORT_NAME = "observed";
	static final String MONITOR_ALERT_PORT_NAME = "alert";
	static final String MONITOR_RESET_PORT_NAME = "reset";
	public static final String MONITOR_COMP_IMPL_NAME = "MON";
	static final String SWITCH_COMP_TYPE_NAME = "CASE_Switch";
	static final String SWITCH_COMP_IMPL_NAME = "SWITCH";
	static final String SWITCH_INPUT_PORT_NAME = "input";
	static final String SWITCH_OUTPUT_PORT_NAME = "output";
	static final String SWITCH_CONTROL_PORT_NAME = "control";
	static final String CONNECTION_IMPL_NAME = "c";

	private String monitorImplementationName;
	private String dispatchProtocol;
	private String resetPort;
	private boolean latched;
	private String expectedPort;
	private String alertPort;
	private boolean createSwitch;
	private PortCategory alertControlPortType;
	private String alertPortDataType;
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
		List<String> outports = getOutports(ci);

		// Provide list of inports that monitor's alert out port can be connected to
		List<String> inports = getInports(ci);

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
			expectedPort = wizard.getExpectedPort();
			alertPort = wizard.getAlertPort();
			createSwitch = wizard.getCreateSwitch();
			alertControlPortType = wizard.getAlertControlPortType();
			alertPortDataType = wizard.getAlertControlPortDataType();
			monitorRequirement = wizard.getRequirement();
			monitorAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the monitor component
		insertMonitor(uri);

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
			if (!CaseUtils.addCasePropertyImport(pkgSection)) {
				return null;
			}
			// Import CASE_Model_Transformations file
			if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
				return null;
			}

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

			// Create Switch, if needed
			Subcomponent switchSub = null;
			if (createSwitch) {
				switchSub = insertSwitch(containingImpl, selectedConnection, compCategory);
				alertPort = switchSub.getName() + "." + SWITCH_CONTROL_PORT_NAME;
			}

			final ComponentType monitorType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

			// Give it a unique name
			monitorType.setName(getUniqueName(MONITOR_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

			// Create monitor observed port
			Port port = (Port) selectedConnection.getDestination().getConnectionEnd();
			Port portObserved = null;
			DataSubcomponentType dataFeatureClassifier = null;
			if (port instanceof EventDataPort) {
				dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
				portObserved = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				((EventDataPort) portObserved).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (port instanceof DataPort) {
				dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
				portObserved = ComponentCreateHelper.createOwnedDataPort(monitorType);
				((DataPort) portObserved).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (port instanceof EventPort) {
				portObserved = ComponentCreateHelper.createOwnedEventPort(monitorType);
				return null;
			} else {
				Dialog.showError("Add Monitor", "Could not determine the port type of the destination component.");
				return null;
			}

			portObserved.setIn(true);
			portObserved.setName(MONITOR_OBSERVED_PORT_NAME);

			// Create expected port
			Port monExpectedPort = null;
			Port srcExpectedPort = getPort(containingImpl, expectedPort);
			// If user didn't specify an expected outport, use the same type as the observed port
			if (srcExpectedPort == null) {
				srcExpectedPort = portObserved;
			}
			if (srcExpectedPort instanceof EventDataPort) {
				monExpectedPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
				dataFeatureClassifier = ((EventDataPort) srcExpectedPort).getDataFeatureClassifier();
				((EventDataPort) monExpectedPort).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (srcExpectedPort instanceof DataPort) {
				monExpectedPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
				dataFeatureClassifier = ((DataPort) srcExpectedPort).getDataFeatureClassifier();
				((DataPort) monExpectedPort).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (srcExpectedPort instanceof EventPort) {
				monExpectedPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
			}
			monExpectedPort.setIn(true);
			monExpectedPort.setName(MONITOR_EXPECTED_PORT_NAME);

			// Create monitor alert port
			Port monAlertPort = null;
			final Port dstAlertPort = getPort(containingImpl, alertPort);
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

			// Create monitor reset port, if needed
			Port monResetPort = null;
			Port srcResetPort = null;
			if (resetPort != null) {
				srcResetPort = getPort(containingImpl, resetPort);
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
			if (!CaseUtils.addCasePropertyAssociation("COMP_TYPE", "MONITOR", monitorType)) {
//					return;
			}

			// CASE::COMP_SPEC property
			// Parse the ID from the Monitor AGREE property
			String monitorPropId = "monitor_policy";
//			try {
//				monitorPropId = monitorAgreeProperty
//						.substring(monitorAgreeProperty.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
//								monitorAgreeProperty.indexOf("\""))
//						.trim();
//
//			} catch (IndexOutOfBoundsException e) {
//				if (!monitorAgreeProperty.isEmpty()) {
//					// Agree property is malformed
//					Dialog.showWarning("Add Monitor", "Monitor AGREE statement is malformed.");
//				}
////					return;
//			}

			if (!monitorPropId.isEmpty()) {
				if (!CaseUtils.addCasePropertyAssociation("COMP_SPEC", monitorPropId, monitorType)) {
//						return;
				}
			}

			// Move monitor to proper location
			// (just before component it connects to on communication pathway)
			final Subcomponent subcomponent = (Subcomponent) selectedConnection.getDestination().getContext();
			String destName = "";
			if (subcomponent.getSubcomponentType() instanceof ComponentImplementation) {
				// Get the component type name
				destName = subcomponent.getComponentImplementation().getType().getName();
			} else {
				destName = subcomponent.getName();
			}

			pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);

			// Create monitor implementation
			final ComponentImplementation monitorImpl = (ComponentImplementation) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			monitorImpl.setName(monitorType.getName() + ".Impl");
			final Realization r = monitorImpl.createOwnedRealization();
			r.setImplemented(monitorType);

			// Add it to proper place
			pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);

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
			destName = selectedConnection.getName();
			containingImpl.getOwnedPortConnections().move(
					getIndex(destName, containingImpl.getOwnedPortConnections()) + 1,
					containingImpl.getOwnedPortConnections().size() - 1);

			// Create Expected connection, if provided
			if (!expectedPort.isEmpty()) {
				final PortConnection portConnExpected = containingImpl.createOwnedPortConnection();
				portConnExpected
						.setName(getUniqueName(CONNECTION_IMPL_NAME, false, containingImpl.getOwnedPortConnections()));
				portConnExpected.setBidirectional(false);
				final ConnectedElement monitorExpectedSrc = portConnExpected.createSource();
				monitorExpectedSrc.setContext(getSubcomponent(containingImpl, expectedPort));
				monitorExpectedSrc.setConnectionEnd(srcExpectedPort);
				final ConnectedElement monitorExpectedDst = portConnExpected.createDestination();
				monitorExpectedDst.setContext(monitorSubcomp);
				monitorExpectedDst.setConnectionEnd(monExpectedPort);
				// Put portConnExpected in right place (before portConnObserved)
				destName = portConnObserved.getName();
				containingImpl.getOwnedPortConnections().move(
						getIndex(destName, containingImpl.getOwnedPortConnections()),
						containingImpl.getOwnedPortConnections().size() - 1);
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
				monitorAlertDst.setContext(getSubcomponent(containingImpl, alertPort));
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
				monitorResetSrc.setContext(getSubcomponent(containingImpl, resetPort));
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
				String agreeClauses = "{**" + System.lineSeparator();

				agreeClauses += "const latched : bool = " + latched + System.lineSeparator();
				agreeClauses += "property monitor_policy = " + monitorAgreeProperty + System.lineSeparator();
//				agreeClauses += "guarantee \"...\" : if";
				agreeClauses = agreeClauses + "**}";

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
				return new AddMonitorClaim(req.getContext(), monitorSubcomp);

			}

			return null;
		});

		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(monitorRequirement, claim);
		}
	}

	/**
	 * Returns all the in data ports in the specified component implementation
	 * @param ci - component implementation
	 * @return list of in port names
	 */
	private List<String> getInports(ComponentImplementation ci) {
		List<String> inports = new ArrayList<>();
		// Get component implementation out ports
		for (Feature f : ci.getAllFeatures()) {
			if (f instanceof Port && ((Port) f).isOut()) {
				inports.add(f.getName());
			}
		}

		// Get subcomponent in ports
		for (Subcomponent s : ci.getOwnedSubcomponents()) {
			for (Feature f : s.getAllFeatures()) {
				if (f instanceof Port && ((Port) f).isIn()) {
					inports.add(s.getName() + "." + f.getName());
				}
			}
		}
		return inports;
	}

	/**
	 * Returns all the out data ports in the specified component implementation
	 * @param ci - component implementation
	 * @return list of out port names
	 */
	private List<String> getOutports(ComponentImplementation ci) {
		List<String> outports = new ArrayList<>();
		// Get component implementation in ports
		for (Feature f : ci.getAllFeatures()) {
			if (f instanceof Port && ((Port) f).isIn()) {
				outports.add(f.getName());
			}
		}

		// Get subcomponent out ports
		for (Subcomponent s : ci.getOwnedSubcomponents()) {
			for (Feature f : s.getAllFeatures()) {
				if (f instanceof Port && ((Port) f).isOut()) {
					outports.add(s.getName() + "." + f.getName());
				}
			}
		}
		return outports;
	}

	/**
	 * Returns the port of the specified subcomponent port name
	 * in the specified component implementation
	 * @param ci - component implementation
	 * @param portName - <subcomponent> . <feature name>
	 * @return
	 */
	private Port getPort(ComponentImplementation ci, String portName) {
		String[] parts = portName.split("\\.");
		if (parts.length == 1) {
			for (Feature f : ci.getAllFeatures()) {
				if (f.getName().equalsIgnoreCase(portName)) {
					if (f instanceof Port) {
						return (Port) f;
					} else {
						return null;
					}
				}
			}
		} else if (parts.length > 1) {
			for (Subcomponent s : ci.getOwnedSubcomponents()) {
				if (s.getName().equalsIgnoreCase(parts[0])) {
					for (Feature f : s.getAllFeatures()) {
						if (f.getName().equalsIgnoreCase(parts[1])) {
							if (f instanceof Port) {
								return (Port) f;
							} else {
								return null;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private Subcomponent getSubcomponent(ComponentImplementation ci, String portName) {
		String[] parts = portName.split("\\.");
		if (parts.length == 2) {
			for (Subcomponent s : ci.getOwnedSubcomponents()) {
				if (s.getName().equalsIgnoreCase(parts[0])) {
					return s;
				}
			}
		}
		return null;
	}


	/**
	 * Inserts a switch component into the model, including switch type definition
	 * and implementation (including correct wiring).
	 * The switch is inserted at the location of the selected connection
	 * @param compImpl - The component implementation to add the switch to
	 * @param conn - The connection to pass through the switch
	 * @param compCategory - The component category of the switch
	 * @return The newly created switch subcomponent
	 */
	private Subcomponent insertSwitch(ComponentImplementation compImpl, PortConnection conn,
			ComponentCategory compCategory) {

		PackageSection pkgSection = (PackageSection) compImpl.eContainer();

		final ComponentType switchType = (ComponentType) pkgSection
				.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

		// Give it a unique name
		switchType.setName(getUniqueName(SWITCH_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

		// Create switch input port
		Port port = (Port) conn.getSource().getConnectionEnd();
		Port portIn = null;
		DataSubcomponentType dataFeatureClassifier = null;
		if (port instanceof EventDataPort) {
			dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
			portIn = ComponentCreateHelper.createOwnedEventDataPort(switchType);
			((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (port instanceof DataPort) {
			dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
			portIn = ComponentCreateHelper.createOwnedDataPort(switchType);
			((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (port instanceof EventPort) {
			portIn = ComponentCreateHelper.createOwnedEventPort(switchType);
			return null;
		} else {
			Dialog.showError("Add Switch", "Could not determine the port type of the destination component.");
			return null;
		}

		portIn.setIn(true);
		portIn.setName(SWITCH_INPUT_PORT_NAME);

		// Create switch output port
		port = (Port) conn.getDestination().getConnectionEnd();
		Port portOut = null;
		dataFeatureClassifier = null;
		if (port instanceof EventDataPort) {
			dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
			portOut = ComponentCreateHelper.createOwnedEventDataPort(switchType);
			((EventDataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (port instanceof DataPort) {
			dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
			portOut = ComponentCreateHelper.createOwnedDataPort(switchType);
			((DataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (port instanceof EventPort) {
			portOut = ComponentCreateHelper.createOwnedEventPort(switchType);
			return null;
		} else {
			Dialog.showError("Add Switch", "Could not determine the port type of the destination component.");
			return null;
		}

		portOut.setOut(true);
		portOut.setName(SWITCH_OUTPUT_PORT_NAME);

		// Create switch control port
		Port controlPort = null;
		// If user didn't specify a control port type, make it an event data port
		if (alertControlPortType == null) {
			controlPort = ComponentCreateHelper.createOwnedEventDataPort(switchType);
		} else if (alertControlPortType == PortCategory.EVENT_DATA) {
			controlPort = ComponentCreateHelper.createOwnedEventDataPort(switchType);
			dataFeatureClassifier = Aadl2GlobalScopeUtil.get(pkgSection, Aadl2Package.eINSTANCE.getDataClassifier(),
					alertPortDataType);
			((EventDataPort) controlPort).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (alertControlPortType == PortCategory.DATA) {
			controlPort = ComponentCreateHelper.createOwnedDataPort(switchType);
			dataFeatureClassifier = Aadl2GlobalScopeUtil.get(pkgSection, Aadl2Package.eINSTANCE.getDataClassifier(),
					alertPortDataType);
			((DataPort) controlPort).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (alertControlPortType == PortCategory.EVENT) {
			controlPort = ComponentCreateHelper.createOwnedEventPort(switchType);
		}
		controlPort.setIn(true);
		controlPort.setName(SWITCH_CONTROL_PORT_NAME);

		// Add monitor properties
		// CASE::COMP_TYPE Property
		if (!CaseUtils.addCasePropertyAssociation("COMP_TYPE", "SWITCH", switchType)) {
//				return;
		}

		// CASE::COMP_SPEC property
		String switchPropId = switchType.getQualifiedName().replace("::", "_") + "_switch_policy";

		if (!switchPropId.isEmpty()) {
			if (!CaseUtils.addCasePropertyAssociation("COMP_SPEC", switchPropId, switchType)) {
//					return;
			}
		}

		// Move switch to proper location
		// (just before component it connects to on communication pathway)
//		final Subcomponent subcomponent = (Subcomponent) selectedConnection.getDestination().getContext();
//		String destName = "";
//		if (subcomponent.getSubcomponentType() instanceof ComponentImplementation) {
//			// Get the component type name
//			destName = subcomponent.getComponentImplementation().getType().getName();
//		} else {
//			destName = subcomponent.getName();
//		}
//
//		pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
//				pkgSection.getOwnedClassifiers().size() - 1);

		// Create switch implementation
		final ComponentImplementation switchImpl = (ComponentImplementation) pkgSection
				.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
		switchImpl.setName(switchType.getName() + ".Impl");
		final Realization r = switchImpl.createOwnedRealization();
		r.setImplemented(switchType);

		// Add it to proper place
//		pkgSection.getOwnedClassifiers().move(getIndex(destName, pkgSection.getOwnedClassifiers()),
//				pkgSection.getOwnedClassifiers().size() - 1);

//		// CASE::COMP_IMPL property
//		if (!switchImplementationLanguage.isEmpty()) {
//			if (!CaseUtils.addCasePropertyAssociation("COMP_IMPL", switchImplementationLanguage, switchImpl)) {
////					return;
//			}
//		}

		// Dispatch protocol property
		if (!dispatchProtocol.isEmpty() && compCategory == ComponentCategory.THREAD) {
			Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(switchImpl, ThreadProperties._NAME,
					ThreadProperties.DISPATCH_PROTOCOL);
			EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
			dispatchProtocolLit.setName(dispatchProtocol);
			NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
			nv.setNamedValue(dispatchProtocolLit);
			switchImpl.setPropertyValue(dispatchProtocolProp, nv);
		}

		// Insert switch subcomponent in containing component implementation
		final Subcomponent switchSubcomp = ComponentCreateHelper.createOwnedSubcomponent(compImpl, compCategory);

		// Give it a unique name
		switchSubcomp.setName(getUniqueName(SWITCH_COMP_IMPL_NAME, true, compImpl.getOwnedSubcomponents()));

		ComponentCreateHelper.setSubcomponentType(switchSubcomp, switchImpl);

		// Create a connection from switch output to selected connection destination
		final PortConnection portConnOutput = compImpl.createOwnedPortConnection();
		// Give it a unique name
		portConnOutput.setName(getUniqueName(CONNECTION_IMPL_NAME, false, compImpl.getOwnedPortConnections()));
		portConnOutput.setBidirectional(false);
		final ConnectedElement switchOutputSrc = portConnOutput.createSource();
		switchOutputSrc.setContext(switchSubcomp);
		switchOutputSrc.setConnectionEnd(portOut);
		final ConnectedElement switchOutputDst = portConnOutput.createDestination();
		switchOutputDst.setContext(conn.getDestination().getContext());
		switchOutputDst.setConnectionEnd(conn.getDestination().getConnectionEnd());

		// Put portOutput in right place (after selected connection)
		String destName = conn.getName();
		compImpl.getOwnedPortConnections().move(getIndex(destName, compImpl.getOwnedPortConnections()) + 1,
				compImpl.getOwnedPortConnections().size() - 1);

		// Rewire selected connection to terminate at the switch
		conn.getDestination().setContext(switchSubcomp);
		conn.getDestination().setConnectionEnd(portIn);

		// AGREE
		String agreeClauses = "{**" + System.lineSeparator();

//		agreeClauses += "guarantee " + switchPropId + "\"...\" : if";
		agreeClauses += "**}";

		final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper.createOwnedAnnexSubclause(switchType);
		annexSubclauseImpl.setName("agree");
		annexSubclauseImpl.setSourceText(agreeClauses);

//		if (isProcess) {
//
//			// TODO: Wrap thread component in a process
//
//			// TODO: Bind process to processor
//		}

		return switchSubcomp;
	}

}
