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
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.handlers.AddSwitchHandler;

public class AddSwitchDialog extends TitleAreaDialog {

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

	private List<String> inports = new ArrayList<>();
	private List<String> outports = new ArrayList<>();
	private String inConnEnd = "";
//	private String outConnEnd = "";
	private List<String> requirements = new ArrayList<>();

	private static final String NO_PORT_SELECTED = "<No port selected>";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AddSwitchDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Switch");
		setMessage(
				"Enter Switch details.  You may optionally leave these fields empty and manually edit the AADL switch component once it is added to the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(2);
		return size;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
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
		Label lblMonitorImplNameField = new Label(container, SWT.NONE);
		lblMonitorImplNameField.setText("Switch implementation name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtSwitchImplementationName = new Text(container, SWT.BORDER);
		txtSwitchImplementationName.setLayoutData(dataInfoField);
		txtSwitchImplementationName.setText(AddSwitchHandler.SWITCH_COMP_IMPL_NAME);
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
		List<String> connectionEnds = new ArrayList<>();
		connectionEnds.add(NO_PORT_SELECTED);
		connectionEnds.addAll(outports);
		mpsInputPorts = new MultiPortSelector(container, connectionEnds, inConnEnd, "input");

	}


	/**
	 * Creates the input field for specifying what to connect the 'control' port to
	 * @param container
	 */
	private void createControlPortField(Composite container) {
		Label lblControlField = new Label(container, SWT.NONE);
		lblControlField.setText("Control port connection");
		lblControlField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		GridData dataInfoField = new GridData();
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
		Label lblDispatchProtocolField = new Label(container, SWT.NONE);
		lblDispatchProtocolField.setText("Dispatch protocol");
		lblDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Create a group to contain the log port options
		Group protocolGroup = new Group(container, SWT.NONE);
		protocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		protocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnDispatchProtocol.clear();

		Button btnNoProtocol = new Button(protocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);

		Button btnPeriodic = new Button(protocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		Button btnSporadic = new Button(protocolGroup, SWT.RADIO);
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
		Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		GridData dataInfoField = new GridData();
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
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Switch AGREE contract");

		GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
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
		for (Map.Entry<String, String> inPort : inputPorts.entrySet()) {
			if (inPort.getKey().isEmpty()) {
				Dialog.showError("Add Switch",
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
			Dialog.showError("Add Switch", "Port " + controlPort
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
			Dialog.showError("Add Switch",
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

	public void setPorts(List<String> inports, List<String> outports) {
		this.inports = inports;
		this.outports = outports;
	}

//	public void setConnectionEnds(String inConnEnd, String outConnEnd) {
//		this.inConnEnd = inConnEnd;
//		this.outConnEnd = outConnEnd;
//	}

	public void setInportConnectionEnd(String inConnEnd) {
		this.inConnEnd = inConnEnd;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

}
