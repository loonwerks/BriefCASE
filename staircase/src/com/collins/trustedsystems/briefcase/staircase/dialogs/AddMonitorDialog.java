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

import com.collins.trustedsystems.briefcase.staircase.dialogs.MultiPortSelector.PortDirection;
import com.collins.trustedsystems.briefcase.staircase.handlers.AddMonitorHandler;

public class AddMonitorDialog extends TitleAreaDialog {

	private Text txtMonitorImplementationName;
	private Label lblResetConnectionField;
	private Button btnReset;
	private Combo cboResetPort;
	private Button btnLatched;
	private MultiPortSelector mpsReferencePorts;
	private Combo cboAlertPort;
	private Button btnObservationGate;
	private List<Button> btnDispatchProtocol = new ArrayList<>();
	private Combo cboMonitorRequirement;
	private Text txtAgreeProperty;

	private String monitorImplementationName;
	private String resetPort = "";
	private boolean latched = false;
	private Map<String, String> referencePorts = new HashMap<>();
	private String alertPort = "";
	private boolean observationGate = false;
	private String dispatchProtocol = "";
	private String monitorRequirement = "";
	private String agreeProperty = "";

	private List<String> inports = new ArrayList<>();
	private List<String> outports = new ArrayList<>();
	private List<String> requirements = new ArrayList<>();

	private static final String NO_PORT_SELECTED = "<No port selected>";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";
	static final String MONITOR_REFERENCE_PORT_NAME = "reference";

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
		size.y += convertHeightInCharsToPixels(3);
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
		createReferencePortsField(container);
		createAlertPortField(container);
		createObservationGateField(container);
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

		Label lblLatchedField = new Label(container, SWT.NONE);
		lblLatchedField.setText("Latched");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnLatched = new Button(container, SWT.CHECK);
		btnLatched.setSelection(false);
		btnLatched.setLayoutData(dataInfoField);

	}

	/**
	 * Creates the input field for specifying what to connect the 'reference' ports to
	 * @param container
	 */
	private void createReferencePortsField(Composite container) {
		Label lblExpectedField = new Label(container, SWT.NONE);
		lblExpectedField.setText("Reference port connections");
		lblExpectedField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		// Port selector
		List<String> connectionEnds = new ArrayList<>();
		connectionEnds.add(NO_PORT_SELECTED);
		connectionEnds.addAll(outports);
		mpsReferencePorts = new MultiPortSelector(container, PortDirection.INPUT, connectionEnds, null,
				MONITOR_REFERENCE_PORT_NAME, "Monitor");
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
		inports.forEach(p -> cboAlertPort.add(p));
		cboAlertPort.setText(NO_PORT_SELECTED);

	}

	/**
	 * Creates the checkbox field for specifying if the monitor
	 * gates the observation signal
	 * @param container
	 */
	private void createObservationGateField(Composite container) {

		Label lblObservationGateField = new Label(container, SWT.NONE);
		lblObservationGateField.setText("Gate Oberserved connection");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnObservationGate = new Button(container, SWT.CHECK);
		btnObservationGate.setSelection(false);
		btnObservationGate.setLayoutData(dataInfoField);

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
		lblAgreeField.setText("Monitor policy");

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
		referencePorts = mpsReferencePorts.getContents();
		for (Map.Entry<String, String> refPort : referencePorts.entrySet()) {
			if (refPort.getKey().isEmpty()) {
				Dialog.showError("Add Switch",
						"A monitor reference port name must be specified to establish a connection with "
								+ refPort.getValue()
								+ ".");
				return false;
			}
		}
		alertPort = cboAlertPort.getText();
		if (alertPort.equals(NO_PORT_SELECTED)) {
			alertPort = "";
		} else if (!inports.contains(alertPort)) {
			Dialog.showError("Add Monitor", "Port " + alertPort
					+ " does not exist in the model.  Select a port from the list to connect the monitor's 'alert' port to, or choose "
					+ NO_PORT_SELECTED + ".");
			return false;
		}
		observationGate = btnObservationGate.getSelection();
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

	public Map<String, String> getReferencePorts() {
		return referencePorts;
	}

	public String getAlertPort() {
		return alertPort;
	}

	public boolean getObservationGate() {
		return observationGate;
	}

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
