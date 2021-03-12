package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.ComponentImplementation;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.dialogs.MultiPortSelector.PortDirection;
import com.collins.trustedsystems.briefcase.staircase.handlers.SwitchTransformHandler;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

public class SwitchTransformDialog extends TitleAreaDialog {

	private Text txtSwitchImplementationName;
	private MultiPortSelector mpsInputPorts;
//	private Combo cboOutputPort;
	private Combo cboControlPort;
	private List<Button> btnDispatchProtocol = new ArrayList<>();
	private Combo cboSwitchRequirement;
	private Text txtAgreeProperty;

	private String switchImplementationName;
	private Map<String, String> inputPorts = new HashMap<>();
//	private String outputPort = "";
	private String controlPort = "";
	private String dispatchProtocol = "";
	private String switchRequirement = "";
	private String agreeProperty = "";

//	private List<String> inports = new ArrayList<>();
	private List<String> outports = new ArrayList<>();
	private String inConnEnd = "";
//	private String outConnEnd = "";
	private List<String> requirements = new ArrayList<>();

	private static final String NO_PORT_SELECTED = "<No port selected>";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public SwitchTransformDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Switch Transform");
		setMessage(
				"Enter Switch details.  You may optionally leave these fields empty and manually edit the AADL switch component once it is added to the model.",
				IMessageProvider.NONE);
	}

	public void create(ComponentImplementation context, String inConnEnd) {
		this.inConnEnd = inConnEnd;
		// Provide list of outports that can be connected to monitor's expected in port
		this.outports = ModelTransformUtils.getOutports(context);
//		// Provide list of inports that monitor's alert out port can be connected to
//		// Currently ignored
//		inports = ModelTransformUtils.getInports(context);
		// Populate requirements list
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));
		create();
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(2);
		return size;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);
		final Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Add switch information fields
		createImplementationNameField(container);
		createInputPortField(container);
//		createOutputPortField(container);
		createControlPortField(container);
		createDispatchProtocolField(container);
		createRequirementField(container);
		createAgreeField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the switch implementation name
	 * @param container
	 */
	private void createImplementationNameField(Composite container) {
		final Label lblMonitorImplNameField = new Label(container, SWT.NONE);
		lblMonitorImplNameField.setText("Switch implementation name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtSwitchImplementationName = new Text(container, SWT.BORDER);
		txtSwitchImplementationName.setLayoutData(dataInfoField);
		txtSwitchImplementationName.setText(SwitchTransformHandler.SWITCH_COMP_IMPL_NAME);
	}

	/**
	 * Creates the input field for specifying what to connect the 'input' port to
	 * @param container
	 */
	private void createInputPortField(Composite container) {
		final Label lblInputField = new Label(container, SWT.NONE);
		lblInputField.setText("Input port connections");
		lblInputField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		// Port selector
		final List<String> connectionEnds = new ArrayList<>();
		connectionEnds.add(NO_PORT_SELECTED);
		connectionEnds.addAll(outports);
		mpsInputPorts = new MultiPortSelector(container, PortDirection.INPUT, connectionEnds, inConnEnd, "input",
				"Switch");

	}


	/**
	 * Creates the input field for specifying what to connect the 'control' port to
	 * @param container
	 */
	private void createControlPortField(Composite container) {
		final Label lblControlField = new Label(container, SWT.NONE);
		lblControlField.setText("Control port connection");
		lblControlField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboControlPort = new Combo(container, SWT.BORDER);
		cboControlPort.setLayoutData(dataInfoField);
		cboControlPort.add(NO_PORT_SELECTED);
		outports.forEach(p -> cboControlPort.add(p));
		cboControlPort.setText(NO_PORT_SELECTED);
	}


	/**
	 * Creates the input field for selecting the dispatch protocol
	 * @param container
	 */
	private void createDispatchProtocolField(Composite container) {
		final Label lblDispatchProtocolField = new Label(container, SWT.NONE);
		lblDispatchProtocolField.setText("Dispatch protocol");
		lblDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Create a group to contain the log port options
		final Group protocolGroup = new Group(container, SWT.NONE);
		protocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		protocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnDispatchProtocol.clear();

		final Button btnNoProtocol = new Button(protocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);

		final Button btnPeriodic = new Button(protocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		final Button btnSporadic = new Button(protocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		btnDispatchProtocol.add(btnNoProtocol);
		btnDispatchProtocol.add(btnPeriodic);
		btnDispatchProtocol.add(btnSporadic);

	}

	/**
	 * Creates the input field for selecting the resolute clause that drives
	 * the addition of this switch to the design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		final Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboSwitchRequirement = new Combo(container, SWT.BORDER);
		cboSwitchRequirement.setLayoutData(dataInfoField);
		cboSwitchRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboSwitchRequirement.add(r));
		cboSwitchRequirement.setText(NO_REQUIREMENT_SELECTED);

	}

	/**
	 * Creates the input text field for specifying the switch agree property
	 * @param container
	 */
	private void createAgreeField(Composite container) {
		final Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Switch AGREE contract");

		final GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtAgreeProperty = new Text(container, SWT.BORDER);
		txtAgreeProperty.setLayoutData(dataInfoField);

	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private boolean saveInput() {
		switchImplementationName = txtSwitchImplementationName.getText();
//		inputPort = cboInputPort.getText();
//		if (inputPort.equals(NO_PORT_SELECTED)) {
//			inputPort = "";
//		} else if (!outports.contains(inputPort)) {
//			Dialog.showError("Add Switch", "Port " + inputPort
//					+ " does not exist in the model.  Select a port from the list to connect the switch's 'input' port to, or choose "
//					+ NO_PORT_SELECTED + ".");
//			return false;
//		}
		inputPorts = mpsInputPorts.getContents();
		if (inputPorts.isEmpty()) {
			Dialog.showError("Switch Transform", "A switch must have at least one input port");
			return false;
		}
		for (Map.Entry<String, String> inPort : inputPorts.entrySet()) {
			if (inPort.getKey().isEmpty()) {
				Dialog.showError("Switch Transform",
						"A switch input port name must be specified to establish a connection with " + inPort.getValue()
								+ ".");
				return false;
			}
		}
//		outputPort = cboOutputPort.getText();
//		if (outputPort.equals(NO_PORT_SELECTED)) {
//			outputPort = "";
//		} else if (!inports.contains(outputPort)) {
//			Dialog.showError("Add Switch", "Port " + outputPort
//					+ " does not exist in the model.  Select a port from the list to connect the switch's 'output' port to, or choose "
//					+ NO_PORT_SELECTED + ".");
//			return false;
//		}
		controlPort = cboControlPort.getText();
		if (controlPort.equals(NO_PORT_SELECTED)) {
			controlPort = "";
		} else if (!outports.contains(controlPort)) {
			Dialog.showError("Switch Transform", "Port " + controlPort
					+ " does not exist in the model.  Select a port from the list to connect the switch's 'control' port to, or choose "
					+ NO_PORT_SELECTED + ".");
			return false;
		}
		for (Button b : btnDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				dispatchProtocol = b.getText();
				break;
			}
		}
		switchRequirement = cboSwitchRequirement.getText();
		if (switchRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			switchRequirement = "";
		} else if (!requirements.contains(switchRequirement)) {
			Dialog.showError("Switch Transform",
					"Switch requirement " + switchRequirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}
		agreeProperty = txtAgreeProperty.getText();

		return true;
	}

	@Override
	protected void okPressed() {
		if (!saveInput()) {
			return;
		}
		super.okPressed();
	}

	public String getSwitchImplementationName() {
		return switchImplementationName;
	}

	public Map<String, String> getInputPorts() {
		return inputPorts;
	}

//	public String getOutputPort() {
//		return outputPort;
//	}

	public String getControlPort() {
		return controlPort;
	}

	public String getDispatchProtocol() {
		return dispatchProtocol;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

	public String getRequirement() {
		return switchRequirement;
	}

}
