package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.List;

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
import org.osate.aadl2.PortCategory;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.handlers.AddMonitorHandler;

public class AddMonitorDialog extends TitleAreaDialog {

	private Text txtMonitorImplementationName;
	private Label lblResetConnectionField;
	private Button btnReset;
	private Combo cboResetPort;
	private Label lblLatchedField;
	private Button btnLatched;
	private Combo cboExpectedPort;
	private Combo cboAlertPort;
	private Label lblAlertPortTypeField = null;
	private Group grpAlertPortType = null;
	private List<Button> btnAlertPortType = new ArrayList<>();
//	private Label lblAlertPortDataTypeField = null;
//	private Text txtAlertPortDataType;
	private List<Button> btnDispatchProtocol = new ArrayList<>();
	private Combo cboMonitorRequirement;
	private Text txtAgreeProperty;

	private String monitorImplementationName;
	private String resetPort = "";
	private boolean latched = false;
	private String expectedPort = "";
	private String alertPort = "";
	private boolean createSwitch = false;
	private PortCategory alertPortType = null;
//	private String alertPortDataType = "";
	private String dispatchProtocol = "";
	private String monitorRequirement = "";
	private String agreeProperty = "";

	private List<String> inports = new ArrayList<>();
	private List<String> outports = new ArrayList<>();
	private List<String> requirements = new ArrayList<>();

	private static final String NO_PORT_SELECTED = "<No port selected>";
	public static final String CREATE_SWITCH = "<Create Switch>";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AddMonitorDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Monitor");
		setMessage(
				"Enter Monitor details.  You may optionally leave these fields empty and manually edit the AADL monitor component once it is added to the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(1);
		return size;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Add monitor information fields
		createMonitorImplementationNameField(container);
		createResetPortField(container);
		createLatchedField(container);
		createExpectedPortField(container);
		createAlertPortField(container);
		createAlertPortTypeField(container);
//		createAlertPortDataTypeField(container);
		createDispatchProtocolField(container);
		createRequirementField(container);
		createAgreeField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the monitor implementation name
	 * @param container
	 */
	private void createMonitorImplementationNameField(Composite container) {
		Label lblMonitorImplNameField = new Label(container, SWT.NONE);
		lblMonitorImplNameField.setText("Monitor implementation name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtMonitorImplementationName = new Text(container, SWT.BORDER);
		txtMonitorImplementationName.setLayoutData(dataInfoField);
		txtMonitorImplementationName.setText(AddMonitorHandler.MONITOR_COMP_IMPL_NAME);
	}

	/**
	 * Creates the checkbox field for adding a reset port
	 * @param container
	 */
	private void createResetPortField(Composite container) {

		Label lblResetField = new Label(container, SWT.NONE);
		lblResetField.setText("Create reset port");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnReset = new Button(container, SWT.CHECK);
		btnReset.setSelection(false);
		btnReset.setLayoutData(dataInfoField);
		btnReset.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				lblResetConnectionField.setEnabled(btnReset.getSelection());
				cboResetPort.setEnabled(btnReset.getSelection());
			}
		});

		lblResetConnectionField = new Label(container, SWT.NONE);
		lblResetConnectionField.setText("    Reset port connection");
		lblResetConnectionField.setEnabled(false);

		dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboResetPort = new Combo(container, SWT.BORDER);
		cboResetPort.setLayoutData(dataInfoField);
		cboResetPort.add(NO_PORT_SELECTED);
		outports.forEach(p -> cboResetPort.add(p));
		cboResetPort.setText(NO_PORT_SELECTED);
		cboResetPort.setEnabled(false);

	}


	/**
	 * Creates the checkbox field for specifying if the monitor is latched
	 * @param container
	 */
	private void createLatchedField(Composite container) {

		lblLatchedField = new Label(container, SWT.NONE);
		lblLatchedField.setText("Latched");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnLatched = new Button(container, SWT.CHECK);
		btnLatched.setSelection(false);
		btnLatched.setLayoutData(dataInfoField);

	}

	/**
	 * Creates the input field for specifying what to connect the 'expected' port to
	 * @param container
	 */
	private void createExpectedPortField(Composite container) {
		Label lblExpectedField = new Label(container, SWT.NONE);
		lblExpectedField.setText("Expected port connection");
		lblExpectedField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboExpectedPort = new Combo(container, SWT.BORDER);
		cboExpectedPort.setLayoutData(dataInfoField);
		cboExpectedPort.add(NO_PORT_SELECTED);
		outports.forEach(p -> cboExpectedPort.add(p));
		cboExpectedPort.setText(NO_PORT_SELECTED);
	}

	/**
	 * Creates the input field for specifying what to connect the 'alert' port to
	 * @param container
	 */
	private void createAlertPortField(Composite container) {
		Label lblAlertField = new Label(container, SWT.NONE);
		lblAlertField.setText("Alert port connection");
		lblAlertField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboAlertPort = new Combo(container, SWT.BORDER);
		cboAlertPort.setLayoutData(dataInfoField);
		cboAlertPort.add(NO_PORT_SELECTED);
		cboAlertPort.add(CREATE_SWITCH);
		inports.forEach(p -> cboAlertPort.add(p));
		cboAlertPort.setText(NO_PORT_SELECTED);
		cboAlertPort.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
//				if (cboAlertPort.getText().equals(CREATE_SWITCH)) {
//					lblAlertPortTypeField.setEnabled(true);
//					grpAlertPortType.setEnabled(true);
//					for (Button btn : btnAlertPortType) {
//						btn.setSelection(false);
//						btn.setEnabled(true);
//					}
//					lblAlertPortDataTypeField.setEnabled(true);
//					txtAlertPortDataType.setEnabled(true);
//				} else {
//					lblAlertPortTypeField.setEnabled(false);
//					grpAlertPortType.setEnabled(false);
//					for (Button btn : btnAlertPortType) {
//						btn.setSelection(false);
//						btn.setEnabled(false);
//					}
//					lblAlertPortDataTypeField.setEnabled(false);
//					txtAlertPortDataType.setText("");
//					txtAlertPortDataType.setEnabled(false);
//				}
				updateAlertPortFields();
			}
		});

	}


	/**
	 * Creates the input field for specifying the alert port type
	 * (Data, Event Data, Event)
	 * @param container
	 */
	private void createAlertPortTypeField(Composite container) {
		lblAlertPortTypeField = new Label(container, SWT.NONE);
		lblAlertPortTypeField.setText("  Alert port type");
		lblAlertPortTypeField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		lblAlertPortTypeField.setEnabled(false);

		// Create a group to contain the log port options
		grpAlertPortType = new Group(container, SWT.NONE);
		grpAlertPortType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		grpAlertPortType.setLayout(new RowLayout(SWT.HORIZONTAL));
		grpAlertPortType.setEnabled(false);

		btnAlertPortType.clear();

		Button btnEventAlertPort = new Button(grpAlertPortType, SWT.RADIO);
		btnEventAlertPort.setText("Event");
		btnEventAlertPort.setSelection(false);
		btnEventAlertPort.setEnabled(false);
		btnEventAlertPort.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				updateAlertPortFields();
			}
		});

		Button btnDataAlertPort = new Button(grpAlertPortType, SWT.RADIO);
		btnDataAlertPort.setText("Data");
		btnDataAlertPort.setSelection(false);
		btnDataAlertPort.setEnabled(false);
		btnDataAlertPort.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				updateAlertPortFields();
			}
		});

		Button btnEventDataAlertPort = new Button(grpAlertPortType, SWT.RADIO);
		btnEventDataAlertPort.setText("Event Data");
		btnEventDataAlertPort.setSelection(false);
		btnEventDataAlertPort.setEnabled(false);
		btnEventDataAlertPort.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				updateAlertPortFields();
			}
		});

		btnAlertPortType.add(btnDataAlertPort);
		btnAlertPortType.add(btnEventAlertPort);
		btnAlertPortType.add(btnEventDataAlertPort);

	}

	private void updateAlertPortFields() {
		if (cboAlertPort.getText().equals(CREATE_SWITCH)) {
			lblLatchedField.setEnabled(false);
			btnLatched.setSelection(false);
			btnLatched.setEnabled(false);
			lblAlertPortTypeField.setEnabled(true);
			grpAlertPortType.setEnabled(true);
//			lblAlertPortDataTypeField.setEnabled(false);
//			txtAlertPortDataType.setEnabled(false);
			for (Button btn : btnAlertPortType) {
				btn.setEnabled(true);
//				if (btn.getText().equalsIgnoreCase("Data") || btn.getText().equalsIgnoreCase("Event Data")) {
//					if (btn.getSelection()) {
//						lblAlertPortDataTypeField.setEnabled(true);
//						txtAlertPortDataType.setEnabled(true);
//					}
//				} else if (btn.getText().equalsIgnoreCase("Event")) {
//					if (btn.getSelection()) {
//						lblAlertPortDataTypeField.setEnabled(false);
//						txtAlertPortDataType.setText("");
//						txtAlertPortDataType.setEnabled(false);
//					}
//				}
			}
		} else {
			lblLatchedField.setEnabled(true);
			btnLatched.setEnabled(true);
			lblAlertPortTypeField.setEnabled(false);
			grpAlertPortType.setEnabled(false);
			for (Button btn : btnAlertPortType) {
				btn.setSelection(false);
				btn.setEnabled(false);
			}
//			lblAlertPortDataTypeField.setEnabled(false);
//			txtAlertPortDataType.setText("");
//			txtAlertPortDataType.setEnabled(false);
		}
	}

//	/**
//	 * Creates the input field for specifying the alert port data type
//	 * @param container
//	 */
//	private void createAlertPortDataTypeField(Composite container) {
//		lblAlertPortDataTypeField = new Label(container, SWT.NONE);
//		lblAlertPortDataTypeField.setText("  Alert port data type");
//		lblAlertPortDataTypeField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
//		lblAlertPortDataTypeField.setEnabled(false);
//
//		GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = GridData.FILL;
//		txtAlertPortDataType = new Text(container, SWT.BORDER);
//		txtAlertPortDataType.setLayoutData(dataInfoField);
//		txtAlertPortDataType.setEnabled(false);
//
//	}

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
	 * the addition of this monitor to the design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboMonitorRequirement = new Combo(container, SWT.BORDER);
		cboMonitorRequirement.setLayoutData(dataInfoField);
		cboMonitorRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboMonitorRequirement.add(r));
		cboMonitorRequirement.setText(NO_REQUIREMENT_SELECTED);

	}

	/**
	 * Creates the input text field for specifying the monitor agree property
	 * @param container
	 */
	private void createAgreeField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Monitor AGREE contract");

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
		monitorImplementationName = txtMonitorImplementationName.getText();
		if (btnReset.getSelection()) {
			resetPort = cboResetPort.getText();
			if (resetPort.equals(NO_PORT_SELECTED)) {
				resetPort = "";
			} else if (!outports.contains(resetPort)) {
				Dialog.showError("Add Monitor", "Port " + resetPort
						+ " does not exist in the model.  Select a port from the list to connect the monitor's 'reset' port to, or choose "
						+ NO_PORT_SELECTED + ".");
				return false;
			}
		} else {
			resetPort = null;
		}
		latched = btnLatched.getSelection();
		expectedPort = cboExpectedPort.getText();
		if (expectedPort.equals(NO_PORT_SELECTED)) {
			expectedPort = "";
		} else if (!outports.contains(expectedPort)) {
			Dialog.showError("Add Monitor", "Port " + expectedPort
					+ " does not exist in the model.  Select a port from the list to connect the monitor's 'expected' port to, or choose "
					+ NO_PORT_SELECTED + ".");
			return false;
		}
		alertPort = cboAlertPort.getText();
		if (alertPort.equals(NO_PORT_SELECTED)) {
			alertPort = "";
		} else if (alertPort.equals(CREATE_SWITCH)) {
			alertPort = "";
			createSwitch = true;
			alertPortType = null;
			for (int i = 0; i < btnAlertPortType.size(); i++) {
				if (btnAlertPortType.get(i).getSelection()) {
					alertPortType = PortCategory.get(i);
					break;
				}
			}
//			alertPortDataType = txtAlertPortDataType.getText();
		} else if (!inports.contains(alertPort)) {
			Dialog.showError("Add Monitor", "Port " + alertPort
					+ " does not exist in the model.  Select a port from the list to connect the monitor's 'alert' port to, or choose "
					+ NO_PORT_SELECTED + ".");
			return false;
		}
		for (Button b : btnDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				dispatchProtocol = b.getText();
				break;
			}
		}
		monitorRequirement = cboMonitorRequirement.getText();
		if (monitorRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			monitorRequirement = "";
		} else if (!requirements.contains(monitorRequirement)) {
			Dialog.showError("Add Monitor",
					"Monitor requirement " + monitorRequirement
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

	public String getMonitorImplementationName() {
		return monitorImplementationName;
	}

	public String getResetPort() {
		return resetPort;
	}

	public boolean getLatched() {
		return latched;
	}

	public String getExpectedPort() {
		return expectedPort;
	}

	public String getAlertPort() {
		return alertPort;
	}

	public boolean getCreateSwitch() {
		return createSwitch;
	}

	public PortCategory getAlertControlPortType() {
		return alertPortType;
	}

//	public String getAlertControlPortDataType() {
//		return alertPortDataType;
//	}

	public String getDispatchProtocol() {
		return dispatchProtocol;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

	public String getRequirement() {
		return monitorRequirement;
	}

	public void setPorts(List<String> inports, List<String> outports) {
		this.inports = inports;
		this.outports = outports;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

}
