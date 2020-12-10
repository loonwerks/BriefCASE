package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.Collections;
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
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.AddSwitchDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddSwitchClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class AddSwitchHandler extends AadlHandler {

	static final String SWITCH_COMP_TYPE_NAME = "CASE_Switch";
	public static final String SWITCH_COMP_IMPL_NAME = "SWITCH";
	public static final String SWITCH_INPUT_PORT_NAME = "input";
	public static final String SWITCH_OUTPUT_PORT_NAME = "output";
	public static final String SWITCH_CONTROL_PORT_NAME = "control";
	static final String CONNECTION_IMPL_NAME = "c";

	private String switchImplementationName;
	private String dispatchProtocol;
	private Map<String, String> inputPorts;
	private String controlPort;
	private String switchRequirement;
	private String switchAgreeProperty;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a connection
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof PortConnection)) {
			Dialog.showError("Add Monitor", "A connection between two components must be selected to add a monitor.");
			return;
		}
		final PortConnection selectedConnection = (PortConnection) eObj;
		ComponentImplementation ci = selectedConnection.getContainingComponentImpl();

		// Provide list of source outports that can be connected to switch's in port
		List<String> outports = ModelTransformUtils.getOutports(ci);

		// Get selected connection ends
		String inConnEnd = selectedConnection.getSource().getContext().getName() + "."
				+ selectedConnection.getSource().getConnectionEnd().getName();

		// Provide list of requirements so the user can choose which requirement is driving this
		// model transformation.
		List<String> requirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));

		// Open wizard to enter switch info
		AddSwitchDialog wizard = new AddSwitchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		wizard.setPorts(Collections.emptyList(), outports);
		wizard.setInportConnectionEnd(inConnEnd);
		wizard.setRequirements(requirements);
		wizard.create();
		if (wizard.open() == Window.OK) {
			switchImplementationName = wizard.getSwitchImplementationName();
			if (switchImplementationName == "") {
				switchImplementationName = SWITCH_COMP_IMPL_NAME;
			}
			dispatchProtocol = wizard.getDispatchProtocol();
			inputPorts = wizard.getInputPorts();
			controlPort = wizard.getControlPort();
			switchRequirement = wizard.getRequirement();
			switchAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the switch component
		insertSwitchComponent(uri);

		BriefcaseNotifier.notify("StairCASE - Switch", "Switch added to model.");

		return;

	}

	/**
	 * Inserts a switch component into the model
	 * @param uri - The URI of the selected connection
	 */
	public void insertSwitchComponent(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		AddSwitchClaim claim = xtextEditor.getDocument().modify(resource -> {
//		xtextEditor.getDocument().modify(resource -> {

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

			// Add switch
			Subcomponent switchSub = insertSwitch(containingImpl, inputPorts, selectedConnection, compCategory,
					dispatchProtocol, controlPort, switchRequirement, switchAgreeProperty);

			// Add add_switch claim to resolute prove statement, if applicable
			if (!switchRequirement.isEmpty()) {
				CyberRequirement req = RequirementsManager.getInstance().getRequirement(switchRequirement);
				NamedElement dataFeatureClassifier = null;
				ConnectionEnd port = selectedConnection.getDestination().getConnectionEnd();
				if (port instanceof EventDataPort) {
					dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
				} else if (port instanceof DataPort) {
					dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
				} else if (port instanceof FeatureGroup) {
					dataFeatureClassifier = ((FeatureGroup) port).getAllFeatureGroupType();
				}
				// TODO: handle case where selected connection is an event connection, in which case there is no data type
				return new AddSwitchClaim(req.getContext(), switchSub, dataFeatureClassifier);
			}

			return null;
		});

		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(switchRequirement, claim);
		}

	}

	/**
	 * Inserts a switch component into the model, including switch type definition
	 * and implementation (including correct wiring).
	 * The switch is inserted at the location of the selected connection
	 * This is a public static function to support other transforms that include a switch.
	 * @param compImpl - The component implementation to add the switch to
	 * @param inPortMap - Map of switch input port names to the message source connection ends (specified by subcomponent.feature)
	 * @param conn - The output connection of the switch (this is the connection the switch is placed on)
	 * @param compCategory - The component category of the switch
	 * @param dispatchProtocol - The dispatch protocol (sporadic, periodic) for thread components
	 * @param controlSrcPort - External connection end to connect with control port (can be null)
	 * @param switchRequirement - The requirement that drives this transform
	 * @param switchAgreeProperty - The spec that defines the behavior of this switch
	 * @return The newly created switch subcomponent
	 */
	public static Subcomponent insertSwitch(ComponentImplementation compImpl,
			Map<String, String> inPortMap, Connection outConn,
			ComponentCategory compCategory, String dispatchProtocol, String controlSrcPort,
			String switchRequirement, String switchAgreeProperty) {

		PackageSection pkgSection = (PackageSection) compImpl.eContainer();

		final ComponentType switchType = (ComponentType) pkgSection
				.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

		// Give it a unique name
		switchType.setName(
				ModelTransformUtils.getUniqueName(SWITCH_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

		// Create switch input port(s)
		Map<String, ConnectionEnd> inPorts = new HashMap<>();
		for (Map.Entry<String, String> inPort : inPortMap.entrySet()) {

			if (inPort.getKey().isEmpty()) {
				continue;
			}

			ConnectionEnd srcPort = ModelTransformUtils.getPort(compImpl, inPort.getValue());
			ConnectionEnd portIn = null;
			DataSubcomponentType dataFeatureClassifier = null;
			if (srcPort instanceof EventDataPort) {
				dataFeatureClassifier = ((EventDataPort) srcPort).getDataFeatureClassifier();
				portIn = ComponentCreateHelper.createOwnedEventDataPort(switchType);
				((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (srcPort instanceof DataPort) {
				dataFeatureClassifier = ((DataPort) srcPort).getDataFeatureClassifier();
				portIn = ComponentCreateHelper.createOwnedDataPort(switchType);
				((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
			} else if (srcPort instanceof EventPort) {
				portIn = ComponentCreateHelper.createOwnedEventPort(switchType);
				return null;
			} else if (srcPort instanceof FeatureGroup) {
				portIn = switchType.createOwnedFeatureGroup();
				((FeatureGroup) portIn).setFeatureType(((FeatureGroup) srcPort).getAllFeatureGroupType());
			} else {
				Dialog.showError("Add Switch", "Could not determine the port type of the source component.");
				return null;
			}

			((DirectedFeature) portIn).setIn(true);
			portIn.setName(inPort.getKey());
			inPorts.put(inPort.getKey(), portIn);
		}

		// Create switch output port
		ConnectionEnd port = outConn.getDestination().getConnectionEnd();
		ConnectionEnd outPort = null;
		DataSubcomponentType dataFeatureClassifier = null;
		if (port instanceof EventDataPort) {
			dataFeatureClassifier = ((EventDataPort) port).getDataFeatureClassifier();
			outPort = ComponentCreateHelper.createOwnedEventDataPort(switchType);
			((EventDataPort) outPort).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (port instanceof DataPort) {
			dataFeatureClassifier = ((DataPort) port).getDataFeatureClassifier();
			outPort = ComponentCreateHelper.createOwnedDataPort(switchType);
			((DataPort) outPort).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (port instanceof EventPort) {
			outPort = ComponentCreateHelper.createOwnedEventPort(switchType);
			return null;
		} else if (port instanceof FeatureGroup) {
			outPort = switchType.createOwnedFeatureGroup();
			((FeatureGroup) outPort).setFeatureType(((FeatureGroup) port).getAllFeatureGroupType());
		} else {
			Dialog.showError("Add Switch", "Could not determine the port type of the destination component.");
			return null;
		}

		((DirectedFeature) outPort).setOut(true);
		outPort.setName(SWITCH_OUTPUT_PORT_NAME);

		// If user didn't specify a control port type, make it an event data port
		// Get control port
		ConnectionEnd ctlSrcPort = ModelTransformUtils.getPort(compImpl, controlSrcPort);
		ConnectionEnd controlPort = null;
		if (ctlSrcPort == null) {
			controlPort = ComponentCreateHelper.createOwnedEventDataPort(switchType);
		} else if (ctlSrcPort instanceof EventDataPort) {
			controlPort = ComponentCreateHelper.createOwnedEventDataPort(switchType);
			dataFeatureClassifier = ((EventDataPort) ctlSrcPort).getDataFeatureClassifier();
			((EventDataPort) controlPort).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (ctlSrcPort instanceof DataPort) {
			controlPort = ComponentCreateHelper.createOwnedDataPort(switchType);
			dataFeatureClassifier = ((DataPort) ctlSrcPort).getDataFeatureClassifier();
			((DataPort) controlPort).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (ctlSrcPort instanceof EventPort) {
			controlPort = ComponentCreateHelper.createOwnedEventPort(switchType);
		} else if (ctlSrcPort instanceof FeatureGroup) {
			controlPort = switchType.createOwnedFeatureGroup();
			((FeatureGroup) controlPort).setFeatureType(((FeatureGroup) ctlSrcPort).getAllFeatureGroupType());
		}
		((DirectedFeature) controlPort).setIn(true);
		controlPort.setName(SWITCH_CONTROL_PORT_NAME);

		// Add switch properties
		// CASE::COMP_TYPE Property
//		if (!CasePropertyUtils.setCompType(switchType, "SWITCH"))
		if (!CasePropertyUtils.setMitigationType(switchType, MITIGATION_TYPE.GATE)) {
//				return;
		}

		// CASE::COMP_SPEC property
		String switchPropId = switchType.getName() + "_policy";

		if (!switchPropId.isEmpty()) {
			if (!CasePropertyUtils.setCompSpec(switchType, switchPropId)) {
//			if (!CasePropertyUtils.addCasePropertyAssociation(CasePropertyUtils.COMP_SPEC, switchPropId, switchType)) {
//					return;
			}
		}

		// Move to top of file
		pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);

		// Create switch implementation
		final ComponentImplementation switchImpl = (ComponentImplementation) pkgSection
				.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
		switchImpl.setName(switchType.getName() + ".Impl");
		final Realization r = switchImpl.createOwnedRealization();
		r.setImplemented(switchType);

		// Move below component type
		pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);

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
		switchSubcomp.setName(
				ModelTransformUtils.getUniqueName(SWITCH_COMP_IMPL_NAME, true, compImpl.getOwnedSubcomponents()));

		ComponentCreateHelper.setSubcomponentType(switchSubcomp, switchImpl);

		// Create input connections
		for (Map.Entry<String, String> inPort : inPortMap.entrySet()) {

			if (inPort.getKey().isEmpty()) {
				continue;
			}

			ConnectionEnd p = ModelTransformUtils.getPort(compImpl, inPort.getValue());
			final Connection portConnInput = ComponentCreateHelper.createOwnedConnection(compImpl, p);
			// Give it a unique name
			portConnInput.setName(
					ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false, compImpl.getOwnedPortConnections()));
			portConnInput.setBidirectional(false);
			final ConnectedElement switchInputSrc = portConnInput.createSource();
			switchInputSrc.setContext(ModelTransformUtils.getSubcomponent(compImpl, inPort.getValue()));
			switchInputSrc.setConnectionEnd(p);
			final ConnectedElement switchInputDst = portConnInput.createDestination();
			switchInputDst.setContext(switchSubcomp);
			switchInputDst.setConnectionEnd(inPorts.get(inPort.getKey()));
		}

		// Create control connection
		if (controlSrcPort != null) {
			final Connection portConnControl = ComponentCreateHelper.createOwnedConnection(compImpl, controlPort);
			// Give it a unique name
			portConnControl.setName(
					ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false, compImpl.getOwnedPortConnections()));
			portConnControl.setBidirectional(false);
			final ConnectedElement switchControlSrc = portConnControl.createSource();
			switchControlSrc.setContext(ModelTransformUtils.getSubcomponent(compImpl, controlSrcPort));
			switchControlSrc.setConnectionEnd(ModelTransformUtils.getPort(compImpl, controlSrcPort));
			final ConnectedElement switchControlDst = portConnControl.createDestination();
			switchControlDst.setContext(switchSubcomp);
			switchControlDst.setConnectionEnd(controlPort);
		}

		// Rewire selected connection to initiate at the switch
		outConn.getSource().setContext(switchSubcomp);
		outConn.getSource().setConnectionEnd(outPort);

		// AGREE
		String agreeClauses = "{**" + System.lineSeparator();

		agreeClauses += switchAgreeProperty;
		agreeClauses += System.lineSeparator() + "**}";

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
