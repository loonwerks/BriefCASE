package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
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

import com.collins.trustedsystems.briefcase.staircase.dialogs.AddSwitchDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

public class AddSwitchHandler extends AadlHandler {

	static final String SWITCH_COMP_TYPE_NAME = "CASE_Switch";
	public static final String SWITCH_COMP_IMPL_NAME = "SWITCH";
	public static final String SWITCH_INPUT_PORT_NAME = "input";
	static final String SWITCH_OUTPUT_PORT_NAME = "output";
	static final String SWITCH_CONTROL_PORT_NAME = "control";
	static final String CONNECTION_IMPL_NAME = "c";

	private String switchImplementationName;
	private String dispatchProtocol;
	private Map<String, String> inputPorts;
	private String outputPort;
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

		// Provide list of outports that can be connected to switch's in port
		List<String> outports = ModelTransformUtils.getOutports(ci);

		// Provide list of inports that switch's out port can be connected to
		List<String> inports = ModelTransformUtils.getInports(ci);

		// Get selected connection ends
		String inConnEnd = selectedConnection.getSource().getContext().getName() + "."
				+ selectedConnection.getSource().getConnectionEnd().getName();
//		String outConnEnd = selectedConnection.getDestination().getContext().getName() + "."
//				+ selectedConnection.getDestination().getConnectionEnd().getName();

		// Provide list of requirements so the user can choose which requirement is driving this
		// model transformation.
		List<String> requirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));

		// Open wizard to enter switch info
		AddSwitchDialog wizard = new AddSwitchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		wizard.setPorts(inports, outports);
//		wizard.setConnectionEnds(inConnEnd, outConnEnd);
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
//			outputPort = wizard.getOutputPort();
			controlPort = wizard.getControlPort();
			switchRequirement = wizard.getRequirement();
			switchAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the switch component
		insertSwitchComponent(uri);

		return;

	}

	/**
	 * Inserts a switch component into the model
	 * @param uri - The URI of the selected connection
	 */
	public void insertSwitchComponent(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

//		AddSwitchClaim claim = xtextEditor.getDocument().modify(resource -> {
		xtextEditor.getDocument().modify(resource -> {

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

			// Add switch
			Port ctlPort = ModelTransformUtils.getPort(containingImpl, controlPort);
			insertSwitch(containingImpl, inputPorts, selectedConnection, compCategory, controlPort,
					ctlPort.getCategory(), null);

			return null;
		});

	}

	/**
	 * Inserts a switch component into the model, including switch type definition
	 * and implementation (including correct wiring).
	 * The switch is inserted at the location of the selected connection
	 * @param compImpl - The component implementation to add the switch to
	 * @param inPortNames - The names for switch input ports
	 * @param inConnEnds - External connection ends to connect with switch in ports (specified by <subcomponent>.<feature>)
	 * @param inPortMap - Map of switch input port names to the message source connection ends (specified by <subcomponent>.<feature>)
	 * @param conn - The output connection of the switch (this is the connection the switch is placed on)
	 * @param compCategory - The component category of the switch
	 * @param controlConnEnd - External connection end to connect with control port
	 * @param controlPortType - The port type (event, event data, data) of the control port
	 * @param controlPortDataType - Data type of control port
	 * @return The newly created switch subcomponent
	 */
	public Subcomponent insertSwitch(ComponentImplementation compImpl,
//			List<String> inPortNames,
//			List<String> inConnEnds,
			Map<String, String> inPortMap, PortConnection outConn,
			ComponentCategory compCategory, String controlSrcPort, PortCategory controlPortType,
			String controlPortDataType) {

		PackageSection pkgSection = (PackageSection) compImpl.eContainer();

		final ComponentType switchType = (ComponentType) pkgSection
				.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

		// Give it a unique name
		switchType.setName(getUniqueName(SWITCH_COMP_TYPE_NAME, true, pkgSection.getOwnedClassifiers()));

		// Create switch input port(s)
		List<Port> inPorts = new ArrayList<>();
		int portIdx = 1;
		for (Map.Entry<String, String> inPort : inPortMap.entrySet()) {
//		for (int i = 0; i < inConnEnds.size(); i++) {
//		for (String sConnEnd : inConnEnds) {
			Port srcPort = ModelTransformUtils.getPort(compImpl, inPort.getValue());
//			Port port = ModelTransformUtils.getPort(compImpl, inConnEnds.get(i));
//			Port port = (Port) outConn.getSource().getConnectionEnd();
			Port portIn = null;
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
			} else {
				Dialog.showError("Add Switch", "Could not determine the port type of the source component.");
				return null;
			}

			portIn.setIn(true);
//			String name = SWITCH_INPUT_PORT_NAME + "_" + (i + 1);
			String name = inPort.getKey();
			if (!inPort.getKey().isEmpty()) {
//			if (inPortNames.size() > i) {
//				name = inPortNames.get(i);
				name = SWITCH_INPUT_PORT_NAME + "_" + (portIdx++);
			}
			portIn.setName(name);
			inPorts.add(portIn);
		}

		// Create switch output port
		Port port = (Port) outConn.getDestination().getConnectionEnd();
		Port outPort = null;
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
		} else {
			Dialog.showError("Add Switch", "Could not determine the port type of the destination component.");
			return null;
		}

		outPort.setOut(true);
		outPort.setName(SWITCH_OUTPUT_PORT_NAME);

		// Create switch control port
		Port controlPort = null;
		// If user didn't specify a control port type, make it an event data port
		if (controlPortType == null) {
			controlPort = ComponentCreateHelper.createOwnedEventDataPort(switchType);
		} else if (controlPortType == PortCategory.EVENT_DATA) {
			controlPort = ComponentCreateHelper.createOwnedEventDataPort(switchType);
			dataFeatureClassifier = Aadl2GlobalScopeUtil.get(pkgSection, Aadl2Package.eINSTANCE.getDataClassifier(),
					controlPortDataType);
			((EventDataPort) controlPort).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (controlPortType == PortCategory.DATA) {
			controlPort = ComponentCreateHelper.createOwnedDataPort(switchType);
			dataFeatureClassifier = Aadl2GlobalScopeUtil.get(pkgSection, Aadl2Package.eINSTANCE.getDataClassifier(),
					controlPortDataType);
			((DataPort) controlPort).setDataFeatureClassifier(dataFeatureClassifier);
		} else if (controlPortType == PortCategory.EVENT) {
			controlPort = ComponentCreateHelper.createOwnedEventPort(switchType);
		}
		controlPort.setIn(true);
		controlPort.setName(SWITCH_CONTROL_PORT_NAME);

		// Add switch properties
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

//		// Create a connection from switch output to selected connection destination
//		final PortConnection portConnOutput = compImpl.createOwnedPortConnection();
//		// Give it a unique name
//		portConnOutput.setName(getUniqueName(CONNECTION_IMPL_NAME, false, compImpl.getOwnedPortConnections()));
//		portConnOutput.setBidirectional(false);
//		final ConnectedElement switchOutputSrc = portConnOutput.createSource();
//		switchOutputSrc.setContext(switchSubcomp);
//		switchOutputSrc.setConnectionEnd(portOut);
//		final ConnectedElement switchOutputDst = portConnOutput.createDestination();
//		switchOutputDst.setContext(outConn.getDestination().getContext());
//		switchOutputDst.setConnectionEnd(outConn.getDestination().getConnectionEnd());
//
//		// Put portOutput in right place (after selected connection)
//		String destName = outConn.getName();
//		compImpl.getOwnedPortConnections().move(getIndex(destName, compImpl.getOwnedPortConnections()) + 1,
//				compImpl.getOwnedPortConnections().size() - 1);

		// Create input connections
		for (Map.Entry<String, String> inPort : inPortMap.entrySet()) {
//		for (String s : inConnEnds) {
//		for (Port p : inConnEnds) {
//			Port p = ModelTransformUtils.getPort(compImpl, s);
			Port p = ModelTransformUtils.getPort(compImpl, inPort.getValue());
			final PortConnection portConnInput = compImpl.createOwnedPortConnection();
			// Give it a unique name
			portConnInput.setName(getUniqueName(CONNECTION_IMPL_NAME, false, compImpl.getOwnedPortConnections()));
			portConnInput.setBidirectional(false);
			final ConnectedElement switchInputSrc = portConnInput.createSource();
//			switchInputSrc.setContext(ModelTransformUtils.getSubcomponent(compImpl, s));
			switchInputSrc.setContext(ModelTransformUtils.getSubcomponent(compImpl, inPort.getValue()));
			switchInputSrc.setConnectionEnd(p);
			final ConnectedElement switchInputDst = portConnInput.createDestination();
			switchInputDst.setContext(switchSubcomp);
//			switchInputDst.setConnectionEnd(ModelTransformUtils.getPort(compImpl, s));
			switchInputDst.setConnectionEnd(ModelTransformUtils.getPort(compImpl, inPort.getValue()));
		}

		// Rewire selected connection to initiate at the switch
		outConn.getSource().setContext(switchSubcomp);
		outConn.getSource().setConnectionEnd(outPort);

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
