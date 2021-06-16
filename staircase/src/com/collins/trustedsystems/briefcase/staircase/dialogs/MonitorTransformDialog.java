package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.dialogs.MultiPortSelector.PortDirection;
import com.collins.trustedsystems.briefcase.staircase.handlers.MonitorTransformHandler;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

public class MonitorTransformDialog extends TitleAreaDialog {

	private ComponentImplementation context;
	private Text txtMonitorComponentName;
	private Text txtMonitorSubcomponentName;
	private Text txtObservationPortName;
	private Label lblObservationGatePortNameField;
	private Text txtObservationGatePortName;
	private Label lblResetPortNameField;
	private Text txtResetPortName;
	private Label lblResetConnectionField;
	private Button btnReset;
	private Combo cboResetPort;
	private Button btnLatched;
	private MultiPortSelector mpsReferencePorts;
	private Text txtAlertPortName;
	private Combo cboAlertPort;
	private Label lblAlertPortDataTypeField;
	private MenuCombo txtAlertPortDataType;
	private Label lblAlertPortCategoryField;
	private List<Button> btnAlertPortCategory = new ArrayList<>();
	private Button btnObservationGate;
	private Button btnCreateThread = null;
	private Label lblDispatchProtocolField;
	private Group protocolGroup;
	private List<Button> btnDispatchProtocol = new ArrayList<>();
	private Label lblPeriodField;
	private Text txtPeriod;
	private Combo cboMonitorRequirement;
	private Text txtAgreeProperty;

	private String monitorComponentName = "";
	private String monitorSubcomponentName = "";
	private String observationPortName = "";
	private String observationGatePortName = "";
	private String resetPortName = "";
	private String resetPort = "";
	private boolean latched = false;
	private Map<String, String> referencePorts = new HashMap<>();
	private String alertPortName = "";
	private String alertPort = "";
	private String alertPortDataType = "";
	private PortCategory alertPortCategory = null;
	private boolean observationGate = false;
	private boolean createThread = false;
	private String dispatchProtocol = "";
	private String period = "";
	private String monitorRequirement = "";
	private String agreeProperty = "";

	private List<String> inports = new ArrayList<>();
	private List<String> outports = new ArrayList<>();
	private Map<String, List<String>> types = new HashMap<>();
	private List<String> requirements = new ArrayList<>();

	private static final String NO_PORT_SELECTED = "<No port selected>";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";
	static final String MONITOR_REFERENCE_PORT_NAME = "reference";

	public MonitorTransformDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Monitor Transform");
		setMessage(
				"Enter Monitor details.  You may optionally leave these fields empty and manually edit the AADL monitor component once it is added to the model.",
				IMessageProvider.NONE);
	}

	public void create(ComponentImplementation context) {
		this.context = context;
		// Provide list of outports that can be connected to monitor's expected in port
		outports = ModelTransformUtils.getOutports(context);
		// Provide list of inports that monitor's alert out port can be connected to
		inports = ModelTransformUtils.getInports(context);
		// Populate data types list
		types = ModelTransformUtils.getTypes(context);
		// Populate requirements list
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));
		create();
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(3);
		return size;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		final Composite area = (Composite) super.createDialogArea(parent);
		area.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		final TabFolder folder = new TabFolder(area, SWT.NONE);
		createGeneralTab(folder);
		createPortsTab(folder);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

//		Composite area = (Composite) super.createDialogArea(parent);
//		Composite container = new Composite(area, SWT.NONE);
//		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		GridLayout layout = new GridLayout(2, false);
//		container.setLayout(layout);
//
//		// Add monitor information fields
//		createMonitorComponentNameField(container);
//		createMonitorSubcomponentNameField(container);
//		createLatchedField(container);
//		createResetPortField(container);
//		createReferencePortsField(container);
//		createAlertPortField(container);
//		createObservationGateField(container);
//		// Only display dispatch protocol if filter is a thread
//		if (context instanceof ProcessImplementation || context instanceof ThreadGroupImplementation) {
//			createDispatchProtocolField(container);
//		}
//		createRequirementField(container);
//		createAgreeField(container);

		return area;
	}

	private void createGeneralTab(TabFolder folder) {

		final Composite container = new Composite(folder, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		final TabItem genTab = new TabItem(folder, SWT.NONE);
		genTab.setText("General");

		createMonitorComponentNameField(container);
		createMonitorSubcomponentNameField(container);
		createLatchedField(container);
		createObservationGateField(container);
		if (context instanceof SystemImplementation) {
			createCreateThreadField(container);
		}
//		// Only display dispatch protocol if monitor is a thread
//		if (context instanceof ProcessImplementation || context instanceof ThreadGroupImplementation
//				|| (context instanceof SystemImplementation && context.getTypeName().endsWith("_seL4"))) {
			createDispatchProtocolField(container);
//		}
		createRequirementField(container);
		createAgreeField(container);

		genTab.setControl(container);
	}

	private void createPortsTab(TabFolder folder) {

		final Composite container = new Composite(folder, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		final TabItem portsTab = new TabItem(folder, SWT.NONE);
		portsTab.setText("Ports");

		createObservationPortNameField(container);
		createObservationGatePortNameField(container);
		createResetPortField(container);
		createReferencePortsField(container);

		createAlertPortNameField(container);
		createAlertPortField(container);
		createAlertPortCategoryField(container);
		createAlertPortDataTypeField(container);

		portsTab.setControl(container);
	}

	/**
	 * Creates the input text field for specifying the monitor component name
	 * @param container
	 */
	private void createMonitorComponentNameField(Composite container) {
		final Label lblMonitorComponentNameField = new Label(container, SWT.NONE);
		lblMonitorComponentNameField.setText("Monitor component instance name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtMonitorComponentName = new Text(container, SWT.BORDER);
		txtMonitorComponentName.setLayoutData(dataInfoField);
		txtMonitorComponentName.setText(MonitorTransformHandler.MONITOR_COMP_TYPE_NAME);
	}

	/**
	 * Creates the input text field for specifying the monitor subcomponent instance name
	 * @param container
	 */
	private void createMonitorSubcomponentNameField(Composite container) {
		final Label lblMonitorSubcomponentNameField = new Label(container, SWT.NONE);
		lblMonitorSubcomponentNameField.setText("Monitor subcomponent instance name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtMonitorSubcomponentName = new Text(container, SWT.BORDER);
		txtMonitorSubcomponentName.setLayoutData(dataInfoField);
		txtMonitorSubcomponentName.setText(MonitorTransformHandler.MONITOR_SUBCOMP_NAME);
	}

	/**
	 * Creates the input text field for specifying the monitor observation port name
	 * @param container
	 */
	private void createObservationPortNameField(Composite container) {
		final Label lblObservationPortNameField = new Label(container, SWT.NONE);
		lblObservationPortNameField.setText("Observation port name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtObservationPortName = new Text(container, SWT.BORDER);
		txtObservationPortName.setLayoutData(dataInfoField);
		txtObservationPortName.setText(MonitorTransformHandler.MONITOR_OBSERVED_PORT_NAME);
	}

	/**
	 * Creates the input text field for specifying the monitor observation gate port name
	 * @param container
	 */
	private void createObservationGatePortNameField(Composite container) {
		lblObservationGatePortNameField = new Label(container, SWT.NONE);
		lblObservationGatePortNameField.setText("Observation gate port name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtObservationGatePortName = new Text(container, SWT.BORDER);
		txtObservationGatePortName.setLayoutData(dataInfoField);
		lblObservationGatePortNameField.setEnabled(false);
		txtObservationGatePortName.setEnabled(false);
	}

	/**
	 * Creates the checkbox field for adding a reset port
	 * @param container
	 */
	private void createResetPortField(Composite container) {

		final Label lblResetField = new Label(container, SWT.NONE);
		lblResetField.setText("Create reset port");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnReset = new Button(container, SWT.CHECK);
		btnReset.setSelection(false);
		btnReset.setLayoutData(dataInfoField);
		btnReset.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				txtResetPortName.setEnabled(btnReset.getSelection());
				lblResetConnectionField.setEnabled(btnReset.getSelection());
				cboResetPort.setEnabled(btnReset.getSelection());
				if (btnReset.getSelection()) {
					txtResetPortName.setText(MonitorTransformHandler.MONITOR_RESET_PORT_NAME);
				} else {
					txtResetPortName.setText("");
				}
			}
		});

		lblResetPortNameField = new Label(container, SWT.NONE);
		lblResetPortNameField.setText("    Reset port name");
		lblResetPortNameField.setEnabled(false);

		dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtResetPortName = new Text(container, SWT.BORDER);
		txtResetPortName.setLayoutData(dataInfoField);
		txtResetPortName.setEnabled(false);

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

		final Label lblLatchedField = new Label(container, SWT.NONE);
		lblLatchedField.setText("Latched");

		final GridData dataInfoField = new GridData();
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
		final Label lblExpectedField = new Label(container, SWT.NONE);
		lblExpectedField.setText("Reference port connections");
		lblExpectedField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		// Port selector
		final List<String> connectionEnds = new ArrayList<>();
		connectionEnds.add(NO_PORT_SELECTED);
		connectionEnds.addAll(outports);
		mpsReferencePorts = new MultiPortSelector(container, PortDirection.INPUT, connectionEnds, null,
				MONITOR_REFERENCE_PORT_NAME, "Monitor");
	}

	/**
	 * Creates the input text field for specifying the monitor alert port name
	 * @param container
	 */
	private void createAlertPortNameField(Composite container) {
		final Label lblAlertPortNameField = new Label(container, SWT.NONE);
		lblAlertPortNameField.setText("Alert port name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtAlertPortName = new Text(container, SWT.BORDER);
		txtAlertPortName.setLayoutData(dataInfoField);
		txtAlertPortName.setText(MonitorTransformHandler.MONITOR_ALERT_PORT_NAME);
	}

	/**
	 * Creates the input field for specifying what to connect the 'alert' port to
	 * @param container
	 */
	private void createAlertPortField(Composite container) {
		final Label lblAlertField = new Label(container, SWT.NONE);
		lblAlertField.setText("Alert port connection");
		lblAlertField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboAlertPort = new Combo(container, SWT.BORDER);
		cboAlertPort.setLayoutData(dataInfoField);
		cboAlertPort.add(NO_PORT_SELECTED);
		inports.forEach(p -> cboAlertPort.add(p));
		cboAlertPort.setText(NO_PORT_SELECTED);

		cboAlertPort.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				final boolean enable = cboAlertPort.getText().equals(NO_PORT_SELECTED);
				lblAlertPortDataTypeField.setEnabled(enable);
				txtAlertPortDataType.setEnabled(enable);
				lblAlertPortCategoryField.setEnabled(enable);
				for (Button b : btnAlertPortCategory) {
					b.setEnabled(enable);
				}
				if (!enable) {
					for (Button b : btnAlertPortCategory) {
						b.setSelection(false);
					}
					txtAlertPortDataType.setText("");
				}
			}
		});
	}

	/**
	 * Creates the input field for specifying the monitor alert port category
	 * @param container
	 */
	private void createAlertPortCategoryField(Composite container) {
		lblAlertPortCategoryField = new Label(container, SWT.NONE);
		lblAlertPortCategoryField.setText("Alert port type");
		lblAlertPortCategoryField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		final Group alertPortCategoryGroup = new Group(container, SWT.NONE);
		alertPortCategoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		alertPortCategoryGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnAlertPortCategory.clear();

		final Button btnEventPort = new Button(alertPortCategoryGroup, SWT.RADIO);
		btnEventPort.setText("Event");
		btnEventPort.setSelection(false);
//		btnEventPort.addListener(SWT.Selection, e -> {
//			if (e.type == SWT.Selection) {
//				if (btnEventPort.getSelection()) {
//					lblAlertPortDataTypeField.setEnabled(false);
//					txtAlertPortDataType.setText("");
//					txtAlertPortDataType.setEnabled(false);
//				}
//			}
//		});

		final Button btnDataPort = new Button(alertPortCategoryGroup, SWT.RADIO);
		btnDataPort.setText("Data");
		btnDataPort.setSelection(false);
//		btnDataPort.addListener(SWT.Selection, e -> {
//			if (e.type == SWT.Selection) {
//				if (btnDataPort.getSelection()) {
//					lblAlertPortDataTypeField.setEnabled(true);
//					txtAlertPortDataType.setEnabled(true);
//				}
//			}
//		});

		final Button btnEventDataPort = new Button(alertPortCategoryGroup, SWT.RADIO);
		btnEventDataPort.setText("Event Data");
		btnEventDataPort.setSelection(false);
//		btnEventDataPort.addListener(SWT.Selection, e -> {
//			if (e.type == SWT.Selection) {
//				if (btnEventDataPort.getSelection()) {
//					lblAlertPortDataTypeField.setEnabled(true);
//					txtAlertPortDataType.setEnabled(true);
//				}
//			}
//		});

		btnAlertPortCategory.add(btnDataPort);
		btnAlertPortCategory.add(btnEventPort);
		btnAlertPortCategory.add(btnEventDataPort);

	}

	/**
	 * Creates the input field for specifying the monitor alert port data type
	 * @param container
	 */
	private void createAlertPortDataTypeField(Composite container) {

		lblAlertPortDataTypeField = new Label(container, SWT.NONE);
		lblAlertPortDataTypeField.setText("Alert port data type");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
//		txtAlertPortDataType = new Text(container, SWT.BORDER);
//		txtAlertPortDataType.setLayoutData(dataInfoField);
		txtAlertPortDataType = new MenuCombo(container, types);

	}

	/**
	 * Creates the checkbox field for specifying if the monitor
	 * gates the observation signal
	 * @param container
	 */
	private void createObservationGateField(Composite container) {

		final Label lblObservationGateField = new Label(container, SWT.NONE);
		lblObservationGateField.setText("Gate Observed connection");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnObservationGate = new Button(container, SWT.CHECK);
		btnObservationGate.setSelection(false);
		btnObservationGate.setLayoutData(dataInfoField);
		btnObservationGate.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				txtObservationGatePortName.setEnabled(btnObservationGate.getSelection());
				lblObservationGatePortNameField.setEnabled(btnObservationGate.getSelection());
				if (btnObservationGate.getSelection()) {
					txtObservationGatePortName.setText(MonitorTransformHandler.MONITOR_GATE_PORT_NAME);
				} else {
					txtObservationGatePortName.setText("");
				}
			}
		});

	}

	/**
	 * Creates the input field for specifying if a thread should also be created
	 * if filter is a process
	 * @param container
	 */
	private void createCreateThreadField(Composite container) {
		final Label lblCreateThreadField = new Label(container, SWT.NONE);
		lblCreateThreadField.setText("Create internal thread component");
		lblCreateThreadField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnCreateThread = new Button(container, SWT.CHECK);
		btnCreateThread.setSelection(true);
		btnCreateThread.setLayoutData(dataInfoField);
		btnCreateThread.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				if (btnCreateThread.getSelection()) {
					for (Button b : btnDispatchProtocol) {
						b.setEnabled(true);
					}
				} else {
					for (Button b : btnDispatchProtocol) {
						if (b.getText().equals("None")) {
							b.setSelection(true);
							txtPeriod.setText("");
						} else {
							b.setSelection(false);
						}
						b.setEnabled(false);
					}
				}
				lblDispatchProtocolField.setEnabled(btnCreateThread.getSelection());
				protocolGroup.setEnabled(btnCreateThread.getSelection());
				lblPeriodField.setEnabled(btnCreateThread.getSelection());
				txtPeriod.setEnabled(btnCreateThread.getSelection());
			}
		});

	}


	/**
	 * Creates the input field for selecting the dispatch protocol
	 * @param container
	 */
	private void createDispatchProtocolField(Composite container) {
		lblDispatchProtocolField = new Label(container, SWT.NONE);
		lblDispatchProtocolField.setText("Dispatch protocol");
		lblDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		protocolGroup = new Group(container, SWT.NONE);
		protocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		protocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnDispatchProtocol.clear();

		final Button btnNoProtocol = new Button(protocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);
		btnNoProtocol.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblPeriodField.setEnabled(!btnNoProtocol.getSelection());
				txtPeriod.setEnabled(!btnNoProtocol.getSelection());
				if (btnNoProtocol.getSelection()) {
					txtPeriod.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btnPeriodic = new Button(protocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		final Button btnSporadic = new Button(protocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		lblPeriodField = new Label(container, SWT.NONE);
		lblPeriodField.setText("Period");
		lblPeriodField.setEnabled(false);
		lblPeriodField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtPeriod = new Text(container, SWT.BORDER);
		txtPeriod.setLayoutData(dataInfoField);
		txtPeriod.setEnabled(false);

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
		final Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		final GridData dataInfoField = new GridData();
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
		final Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Monitor policy");

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
		final List<Classifier> componentsInPackage = AadlUtil.getContainingPackageSection(context)
				.getOwnedClassifiers();
		final Set<String> portNames = new HashSet<>();

		// Monitor Component Name
		if (!txtMonitorComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtMonitorComponentName.getText())) {
			Dialog.showError("Monitor Transform", "Monitor component name " + txtMonitorComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!txtMonitorComponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtMonitorComponentName.getText()) != null) {
			Dialog.showError("Monitor Transform", "Component " + txtMonitorComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtMonitorComponentName.setText(
					ModelTransformUtils.getUniqueName(txtMonitorComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			monitorComponentName = txtMonitorComponentName.getText();
		}

		// Monitor Subcomponent Instance Name
		if (!txtMonitorSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtMonitorSubcomponentName.getText())) {
			Dialog.showError("Monitor Transform",
					"Monitor subcomponent instance name " + txtMonitorSubcomponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!txtMonitorSubcomponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(context.getAllSubcomponents(),
				txtMonitorSubcomponentName.getText()) != null) {
			Dialog.showError("Monitor Transform", "Subcomponent " + txtMonitorSubcomponentName.getText()
					+ " already exists in " + context.getName() + ". Use the suggested name or enter a new one.");
			txtMonitorSubcomponentName.setText(ModelTransformUtils.getUniqueName(txtMonitorSubcomponentName.getText(),
					true, context.getAllSubcomponents()));
			return false;
		} else {
			monitorSubcomponentName = txtMonitorSubcomponentName.getText();
		}
//		monitorSubcomponentName = txtMonitorSubcomponentName.getText();

		// Observation Port
		if (!txtObservationPortName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtObservationPortName.getText())) {
			Dialog.showError("Monitor Transform", "Observation port name " + txtObservationPortName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!portNames.add(txtObservationPortName.getText())) {
			Dialog.showError("Monitor Transform", "The Observation port name " + txtObservationPortName.getText()
					+ " cannot be used since it has already been specified for a different port in this monitor.");
			txtObservationPortName.setText("");
			return false;
		} else {
			observationPortName = txtObservationPortName.getText();
		}

		// Reset Port
		if (btnReset.getSelection()) {
			if (!txtResetPortName.getText().isEmpty() && !ModelTransformUtils.isValidName(txtResetPortName.getText())) {
				Dialog.showError("Monitor Transform", "Reset port name " + txtResetPortName.getText()
						+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
				return false;
			} else if (!portNames.add(txtResetPortName.getText())) {
				Dialog.showError("Monitor Transform", "The Reset port name " + txtResetPortName.getText()
						+ " cannot be used since it has already been specified for a different port in this monitor.");
				txtResetPortName.setText("");
				return false;
			} else {
				resetPortName = txtResetPortName.getText();
			}
			resetPort = cboResetPort.getText();
			if (resetPort.equals(NO_PORT_SELECTED)) {
				resetPort = "";
			} else if (!outports.contains(resetPort)) {
				Dialog.showError("Monitor Transform", "Port " + resetPort
						+ " does not exist in the model.  Select a port from the list to connect the monitor's 'reset' port to, or choose "
						+ NO_PORT_SELECTED + ".");
				return false;
			}
		} else {
			resetPort = null;
		}

		// Latched
		latched = btnLatched.getSelection();

		// Reference Ports
		referencePorts = mpsReferencePorts.getContents();
		for (Map.Entry<String, String> refPort : referencePorts.entrySet()) {
			if (refPort.getKey().isEmpty()) {
				Dialog.showError("Monitor Transform",
						"A monitor reference port name must be specified in order to establish a connection with "
								+ refPort.getValue()
								+ ".");
				return false;
			} else if (!portNames.add(refPort.getKey())) {
				Dialog.showError("Monitor Transform", "The Reference port name " + refPort.getKey()
						+ " cannot be used since it has already been specified for a different port in this monitor.");
				return false;
			}
		}

		// Alert Port
		if (!txtAlertPortName.getText().isEmpty() && !ModelTransformUtils.isValidName(txtAlertPortName.getText())) {
			Dialog.showError("Monitor Transform", "Alert port name " + txtAlertPortName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!portNames.add(txtAlertPortName.getText())) {
			Dialog.showError("Monitor Transform", "The Alert port name " + txtAlertPortName.getText()
					+ " cannot be used since it has already been specified for a different port in this monitor.");
			txtAlertPortName.setText("");
			return false;
		} else {
			alertPortName = txtAlertPortName.getText();
		}
		alertPort = cboAlertPort.getText();
		if (alertPort.equals(NO_PORT_SELECTED)) {
			alertPort = "";
		} else if (!inports.contains(alertPort)) {
			Dialog.showError("Monitor Transform", "Port " + alertPort
					+ " does not exist in the model.  Select a port from the list to connect the monitor's 'alert' port to, or choose "
					+ NO_PORT_SELECTED + ".");
			return false;
		}
		alertPortCategory = null;
		for (int i = 0; i < btnAlertPortCategory.size(); i++) {
			if (btnAlertPortCategory.get(i).getSelection()) {
				alertPortCategory = PortCategory.get(i);
				break;
			}
		}
		// Alert Port Data Type
		if (!txtAlertPortDataType.getText().isEmpty() && !txtAlertPortDataType.getText().contains("::")
				&& !txtAlertPortDataType.getText().startsWith("[")) {
			// Check if type is defined in current package
			String dataType = txtAlertPortDataType.getText();
			if (dataType.contains("[")) {
				dataType = dataType.substring(0, dataType.indexOf("["));
			}
			if (AadlUtil.findNamedElementInList(componentsInPackage, dataType) == null) {
				Dialog.showError("Monitor Transform",
						"Alert port data type was not found in package "
								+ AadlUtil.getContainingPackage(context).getName()
								+ ". Enter the data type's qualified name if it is defined in a different package.");
				return false;
//			} else {
//				// Add the package name
//				txtAlertPortDataType.setText(
//						AadlUtil.getContainingPackage(context).getName() + "::" + txtAlertPortDataType.getText());
			}
		}
		if (txtAlertPortDataType.getText().contains("[")) {
			try {
				Long.parseLong(txtAlertPortDataType.getText().substring(txtAlertPortDataType.getText().indexOf("[") + 1,
						txtAlertPortDataType.getText().length() - 1));
			} catch (NumberFormatException e) {
				Dialog.showError("Monitor Transform",
						"The array size specified on the alert port data type is malformed.");
				return false;
			}
		}
		alertPortDataType = txtAlertPortDataType.getText();

		// Gate Observed Signal
		observationGate = btnObservationGate.getSelection();
		if (observationGate) {
			if (!txtObservationGatePortName.getText().isEmpty()
					&& !ModelTransformUtils.isValidName(txtObservationGatePortName.getText())) {
				Dialog.showError("Monitor Transform",
						"Observation gate port name " + txtObservationGatePortName.getText()
						+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
				return false;
			} else if (!portNames.add(txtObservationGatePortName.getText())) {
				Dialog.showError("Monitor Transform", "The Observation gate port name "
						+ txtObservationGatePortName.getText()
						+ " cannot be used since it has already been specified for a different port in this monitor.");
				txtObservationGatePortName.setText("");
				return false;
			} else {
				observationGatePortName = txtObservationGatePortName.getText();
			}
		}

		// Create thread
		if (btnCreateThread != null) {
			createThread = btnCreateThread.getSelection();
		}

		// Dispatch Protocol and Period
		for (Button b : btnDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				dispatchProtocol = b.getText();
				// make sure period is properly formatted
				if (txtPeriod.getText().isEmpty()
						|| txtPeriod.getText().matches("((\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?)")) {
					period = txtPeriod.getText();
				} else {
					Dialog.showError("Monitor Transform", "Monitor period " + txtPeriod.getText()
							+ " is malformed. See the AADL definition of Period in Timing_Properties.aadl.");
					return false;
				}
				break;
			}
		}

		// Requirement
		monitorRequirement = cboMonitorRequirement.getText();
		if (monitorRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			monitorRequirement = "";
		} else if (!requirements.contains(monitorRequirement)) {
			Dialog.showError("Monitor Transform",
					"Monitor requirement " + monitorRequirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

		// AGREE Property
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

	public String getMonitorComponentName() {
		return monitorComponentName;
	}

	public String getMonitorSubcomponentName() {
		return monitorSubcomponentName;
	}

	public String getObservationPortName() {
		return observationPortName;
	}

	public String getObservationGatePortName() {
		return observationGatePortName;
	}

	public String getResetPortName() {
		return resetPortName;
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

	public String getAlertPortName() {
		return alertPortName;
	}

	public String getAlertPort() {
		return alertPort;
	}

	public PortCategory getAlertPortCategory() {
		return alertPortCategory;
	}

	public String getAlertPortDataType() {
		return alertPortDataType;
	}

	public boolean getObservationGate() {
		return observationGate;
	}

	public boolean createThread() {
		return createThread;
	}

	public String getDispatchProtocol() {
		return dispatchProtocol;
	}

	public String getPeriod() {
		return period;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

	public String getRequirement() {
		return monitorRequirement;
	}

}
