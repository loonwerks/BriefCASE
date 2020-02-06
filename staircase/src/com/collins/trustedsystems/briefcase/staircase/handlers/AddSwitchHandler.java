package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Feature;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.dialogs.AddSwitchDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;

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

		// Provide list of outports that can be connected to monitor's expected in port
		List<String> outports = getOutports(ci);

		// Provide list of inports that monitor's alert out port can be connected to
		List<String> inports = getInports(ci);

		// Provide list of requirements so the user can choose which requirement is driving this
		// model transformation.
		List<String> requirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));

		// Open wizard to enter monitor info
		AddSwitchDialog wizard = new AddSwitchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		wizard.setPorts(inports, outports);
		wizard.setRequirements(requirements);
		wizard.create();
		if (wizard.open() == Window.OK) {
			switchImplementationName = wizard.getSwitchImplementationName();
			if (switchImplementationName == "") {
				switchImplementationName = SWITCH_COMP_IMPL_NAME;
			}
			dispatchProtocol = wizard.getDispatchProtocol();
			inputPorts = wizard.getInputPorts();
			outputPort = wizard.getOutputPort();
			controlPort = wizard.getControlPort();
			switchRequirement = wizard.getRequirement();
			switchAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the router component
		insertSwitchComponent(uri);

		return;

	}

	/**
	 * Inserts a switch component into the model
	 * @param uri - The URI of the selected connection
	 */
	public void insertSwitchComponent(URI uri) {



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
}
