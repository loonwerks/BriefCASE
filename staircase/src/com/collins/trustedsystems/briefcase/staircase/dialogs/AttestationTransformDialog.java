package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
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
import org.osate.aadl2.Context;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.handlers.AttestationTransformHandler;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

/**
 * This class creates the Attestation Transform wizard
 */
public class AttestationTransformDialog extends TitleAreaDialog {

	private ComponentImplementation context = null;
	private TabFolder folder;
	private List<String> commDrivers = new ArrayList<>();
	private Combo cboCommDrivers;
	private Text txtMgrComponentName;
	private Text txtGateComponentName;
	private Text txtMgrSubcomponentName;
	private Text txtGateSubcomponentName;
	private MenuCombo txtRequestMessageDataType;
	private MenuCombo txtResponseMessageDataType;
	private Text txtCacheTimeout;
//	private Combo cboCacheSize;
	private MenuCombo txtIdListDataType;
	private PortNamesControl pncPortNames = null;
	private Label lblMgrDispatchProtocolField;
	private Group mgrProtocolGroup;
	private List<Button> btnMgrDispatchProtocol = new ArrayList<>();
	private Label lblMgrPeriodField;
	private Text txtMgrPeriod;
	private Text txtMgrStackSize;
	private Label lblGateDispatchProtocolField;
	private Group gateProtocolGroup;
	private List<Button> btnGateDispatchProtocol = new ArrayList<>();
	private Label lblGatePeriodField;
	private Text txtGatePeriod;
	private Text txtGateStackSize;
	private List<Button> btnMgrLogPortType = new ArrayList<>();
	private List<Button> btnGateLogPortType = new ArrayList<>();
	private Button btnCreateThread = null;
	private Label lblUseKUImplementationField;
	private Button btnUseKUImplementation;
	private Combo cboRequirement;
//	private Text txtGateAgreeProperty;
	private String attestationManagerComponentName;
	private String attestationGateComponentName;
	private String attestationManagerSubcomponentName;
	private String attestationGateSubcomponentName;
	private String requestMessageDataType = "";
	private String responseMessageDataType = "";
	private long cacheTimeout = 0;
//	private long cacheSize = 0;
	private String idListDataType = "";
	private Map<String, List<String>> gatePortNames = new HashMap<>();
	private String mgrDispatchProtocol = "";
	private String mgrPeriod = "";
	private String mgrStackSize = "";
	private String gateDispatchProtocol = "";
	private String gatePeriod = "";
	private String gateStackSize = "";
	private PortCategory mgrLogPortType = null;
	private PortCategory gateLogPortType = null;
	private String requirement;
	private String commDriver = "";
	private boolean createThread = false;
	private boolean useKUImplementation = true;
	private List<String> requirements = new ArrayList<>();
	private Map<String, List<String>> types = new HashMap<>();
//	private String gateAgreeProperty;

//	private static final int MAX_CACHE_SIZE = 6;
//	private static final int DEFAULT_CACHE_SIZE = 4;

	private static final int MGR_TAB_IDX = 0;
	private static final int GATE_TAB_IDX = 1;

	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";
	private static final String NO_COMM_DRIVER_SELECTED = "<No communication driver selected>";

	public AttestationTransformDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Attestation Transform");
		setMessage(
				"Enter Attestation details.  You may optionally leave these fields empty and manually edit the model.",
				IMessageProvider.NONE);

	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(1);
		return size;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}


	public void create(EObject context) {

		if (context instanceof Subcomponent) {
			this.context = ((Subcomponent) context).getContainingComponentImpl();
			this.commDriver = ((Subcomponent) context).getName();
		} else if (context instanceof ComponentImplementation) {
			this.context = (ComponentImplementation) context;
			for (Subcomponent sub : ((ComponentImplementation) context).getOwnedSubcomponents()) {
				// Check if selected subcomponent is a comm driver
				if (CasePropertyUtils.isCommDriver(sub.getClassifier())) {
					commDrivers.add(sub.getName());
				}
			}
		}

		// Populate package::type list
		types = ModelTransformUtils.getTypes(context);

		// Populate requirements list
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));

		super.create();
		setTitle("Add Attestation" + (this.commDriver.isEmpty() ? "" : " to " + this.commDriver));
		setMessage("Enter Attestation details for adding attestation to " + this.commDriver
				+ ".  You may optionally leave these fields empty and manually edit the AADL attestation manager and gate components once they are added to the model.",
				IMessageProvider.NONE);
	}


	@Override
	protected Control createDialogArea(Composite parent) {

		final Composite area = (Composite) super.createDialogArea(parent);
		area.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		folder = new TabFolder(area, SWT.NONE);
		createMgrTab(folder);
		createGateTab(folder);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		final Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, true);
		container.setLayout(layout);

		if (!commDrivers.isEmpty()) {
			cboCommDrivers = TransformDialogUtil.createComboField(container, "Communication Driver", commDrivers,
					NO_COMM_DRIVER_SELECTED);
			cboCommDrivers.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateCommDriver();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					updateCommDriver();
				}
			});
		}

		if (context instanceof SystemImplementation) {
			createCreateThreadField(container);
		}
		cboRequirement = TransformDialogUtil.createComboField(container, "Requirement", requirements,
				NO_REQUIREMENT_SELECTED);

		return area;
	}

	private void createMgrTab(TabFolder folder) {

		final Composite container = new Composite(folder, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		final TabItem mgrTab = new TabItem(folder, SWT.NONE);
		mgrTab.setText("Manager");

		txtMgrComponentName = TransformDialogUtil.createTextField(container, "Attestation Manager component name",
				ModelTransformUtils.getUniqueName(AttestationTransformHandler.AM_COMP_TYPE_NAME, true,
						AadlUtil.getContainingPackageSection(context).getOwnedClassifiers()));
		txtMgrSubcomponentName = TransformDialogUtil.createTextField(container, "Attestation Manager subcomponent name",
				ModelTransformUtils.getUniqueName(AttestationTransformHandler.AM_SUBCOMP_NAME, true,
						context.getOwnedSubcomponents()));
		txtRequestMessageDataType = TransformDialogUtil.createDataTypeField(container, "Request Message data type",
				types);
		txtResponseMessageDataType = TransformDialogUtil.createDataTypeField(container, "Response Message data type",
				types);
		txtIdListDataType = TransformDialogUtil.createDataTypeField(container, "ID list data type", types);
		createMgrDispatchProtocolField(container);
		txtMgrStackSize = TransformDialogUtil.createTextField(container, "Stack size", "");
		btnMgrLogPortType = TransformDialogUtil.createLogPortField(container);
		createUseKUImplementationField(container);

		mgrTab.setControl(container);
	}

	private void createGateTab(TabFolder folder) {

		final Composite container = new Composite(folder, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		container.setLayout(new GridLayout(1, false));

		final TabItem gateTab = new TabItem(folder, SWT.NONE);
		gateTab.setText("Gate");

		final Composite namesContainer = new Composite(container, SWT.NONE);
		namesContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		namesContainer.setLayout(new GridLayout(2, false));

		txtGateComponentName = TransformDialogUtil.createTextField(namesContainer, "Attestation Gate component name",
				ModelTransformUtils.getUniqueName(AttestationTransformHandler.AG_COMP_TYPE_NAME, true,
						AadlUtil.getContainingPackageSection(context).getOwnedClassifiers()));
		txtGateSubcomponentName = TransformDialogUtil.createTextField(namesContainer,
				"Attestation Gate subcomponent name",
				ModelTransformUtils.getUniqueName(AttestationTransformHandler.AG_SUBCOMP_NAME, true,
						context.getOwnedSubcomponents()));

		final Composite portsContainer = new Composite(container, SWT.NONE);
		portsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		portsContainer.setLayout(new GridLayout(1, false));
		createGatePortNamesField(portsContainer);

		final Composite miscContainer = new Composite(container, SWT.NONE);
		miscContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		miscContainer.setLayout(new GridLayout(2, false));
		createGateDispatchProtocolField(miscContainer);
		txtGateStackSize = TransformDialogUtil.createTextField(miscContainer, "Stack size", "");
		btnGateLogPortType = TransformDialogUtil.createLogPortField(miscContainer);

		gateTab.setControl(container);
	}


	private void createCacheTimeoutField(Composite container) {

		final Label lblCacheTimeoutField = new Label(container, SWT.NONE);
		lblCacheTimeoutField.setText("Re-attestation period");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		txtCacheTimeout = new Text(container, SWT.BORDER);
		txtCacheTimeout.setLayoutData(dataInfoField);
	}


	private void createGatePortNamesField(Composite container) {
		final Label lblPortNamesField = new Label(container, SWT.NONE);
		lblPortNamesField.setText("Port names");
		lblPortNamesField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		// Get connections that will pass through gate
		final List<String> commPortNames = new ArrayList<>();
		final List<String> defaultInputPortNames = new ArrayList<>();
		final List<String> defaultOutputPortNames = new ArrayList<>();
		for (PortConnection conn : this.context.getOwnedPortConnections()) {
			if (conn.getSource().getContext() != null
					&& conn.getSource().getContext().getName().equalsIgnoreCase(commDriver)
					&& conn.getDestination().getContext() != null) {

				if (isAttestationManager(conn.getDestination().getContext())) {
					continue;
				}

				final String commPortName = conn.getSource().getConnectionEnd().getName();
				if (!commPortNames.contains(commPortName)) {
					commPortNames.add(commPortName);
					defaultInputPortNames.add(commPortName + "_in");
					defaultOutputPortNames.add(commPortName + "_out");
				}
			}
		}

		// Port names control
		pncPortNames = new PortNamesControl(container, commDriver + " Output Port Name", commPortNames,
				defaultInputPortNames, defaultOutputPortNames, "Gate");
	}

	private void updateCommDriver() {

		if (cboCommDrivers.getText().contentEquals(NO_COMM_DRIVER_SELECTED)) {
			commDriver = "";
		} else if (!commDrivers.contains(cboCommDrivers.getText())) {
			cboCommDrivers.setText(NO_COMM_DRIVER_SELECTED);
			commDriver = "";
			return;
		} else {
			commDriver = cboCommDrivers.getText();
		}

		// Get connections that will pass through gate
		final List<String> commPortNames = new ArrayList<>();
		final List<String> defaultInputPortNames = new ArrayList<>();
		final List<String> defaultOutputPortNames = new ArrayList<>();
		for (PortConnection conn : this.context.getOwnedPortConnections()) {
			if (conn.getSource().getContext() != null
					&& conn.getSource().getContext().getName().equalsIgnoreCase(commDriver)
					&& conn.getDestination().getContext() != null) {

				if (isAttestationManager(conn.getDestination().getContext())) {
					continue;
				}

				final String commPortName = conn.getSource().getConnectionEnd().getName();
				if (!commPortNames.contains(commPortName)) {
					commPortNames.add(commPortName);
					defaultInputPortNames.add(commPortName + "_in");
					defaultOutputPortNames.add(commPortName + "_out");
				}
			}
		}

		// Port names control
		if (pncPortNames != null) {
			pncPortNames.reset(commDriver, commPortNames, defaultInputPortNames, defaultOutputPortNames);
		}
	}

	private boolean isAttestationManager(Context context) {

		if (!(context instanceof Subcomponent)) {
			return false;
		}
		// Check if it's an attestation manager
		if (CasePropertyUtils.hasMitigationType(((Subcomponent) context).getClassifier(),
				MITIGATION_TYPE.ATTESTATION)) {
			return true;
		}

		return false;
	}

	/**
	 * Creates the input field for selecting the Manager dispatch protocol
	 * @param container
	 */
	private void createMgrDispatchProtocolField(Composite container) {
		lblMgrDispatchProtocolField = new Label(container, SWT.NONE);
		lblMgrDispatchProtocolField.setText("Dispatch protocol");
		lblMgrDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		mgrProtocolGroup = new Group(container, SWT.NONE);
		mgrProtocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		mgrProtocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnMgrDispatchProtocol.clear();

		final Button btnNoProtocol = new Button(mgrProtocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);
		btnNoProtocol.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblMgrPeriodField.setEnabled(!btnNoProtocol.getSelection());
				txtMgrPeriod.setEnabled(!btnNoProtocol.getSelection());
				if (btnNoProtocol.getSelection()) {
					txtMgrPeriod.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btnPeriodic = new Button(mgrProtocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		final Button btnSporadic = new Button(mgrProtocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		lblMgrPeriodField = new Label(container, SWT.NONE);
		lblMgrPeriodField.setLayoutData(dataInfoField);
		lblMgrPeriodField.setText("Period");
		lblMgrPeriodField.setEnabled(false);

		txtMgrPeriod = new Text(container, SWT.BORDER);
		txtMgrPeriod.setLayoutData(dataInfoField);
		txtMgrPeriod.setEnabled(false);

		btnMgrDispatchProtocol.add(btnNoProtocol);
		btnMgrDispatchProtocol.add(btnPeriodic);
		btnMgrDispatchProtocol.add(btnSporadic);

	}


	/**
	 * Creates the input field for selecting the Gate dispatch protocol
	 * @param container
	 */
	private void createGateDispatchProtocolField(Composite container) {
		lblGateDispatchProtocolField = new Label(container, SWT.NONE);
		lblGateDispatchProtocolField.setText("Dispatch protocol");
		lblGateDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		gateProtocolGroup = new Group(container, SWT.NONE);
		gateProtocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		gateProtocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnGateDispatchProtocol.clear();

		final Button btnNoProtocol = new Button(gateProtocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);
		btnNoProtocol.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblGatePeriodField.setEnabled(!btnNoProtocol.getSelection());
				txtGatePeriod.setEnabled(!btnNoProtocol.getSelection());
				if (btnNoProtocol.getSelection()) {
					txtGatePeriod.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btnPeriodic = new Button(gateProtocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		final Button btnSporadic = new Button(gateProtocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		lblGatePeriodField = new Label(container, SWT.NONE);
		lblGatePeriodField.setLayoutData(dataInfoField);
		lblGatePeriodField.setText("Period");
		lblGatePeriodField.setEnabled(false);

		txtGatePeriod = new Text(container, SWT.BORDER);
		txtGatePeriod.setLayoutData(dataInfoField);
		txtGatePeriod.setEnabled(false);

		btnGateDispatchProtocol.add(btnNoProtocol);
		btnGateDispatchProtocol.add(btnPeriodic);
		btnGateDispatchProtocol.add(btnSporadic);

	}


	/**
	 * Creates the input field for specifying the KU remote attestation
	 * implementation should be used
	 * @param container
	 */
	private void createUseKUImplementationField(Composite container) {
		lblUseKUImplementationField = new Label(container, SWT.NONE);
		lblUseKUImplementationField.setText("Use KU Remote Attestation implementation");
		lblUseKUImplementationField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnUseKUImplementation = new Button(container, SWT.CHECK);
		btnUseKUImplementation.setLayoutData(dataInfoField);
		btnUseKUImplementation.setSelection(true);

	}

	/**
	 * Creates the input field for specifying if a thread should also be created
	 * if filter is a process
	 * @param container
	 */
	private void createCreateThreadField(Composite container) {
		final Label lblCreateThreadField = new Label(container, SWT.NONE);
		lblCreateThreadField.setText("Create internal thread components");
		lblCreateThreadField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		btnCreateThread = new Button(container, SWT.CHECK);
		btnCreateThread.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		btnCreateThread.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				if (btnCreateThread.getSelection()) {
					for (Button b : btnMgrDispatchProtocol) {
						b.setEnabled(true);
					}
					for (Button b : btnGateDispatchProtocol) {
						b.setEnabled(true);
					}
					btnUseKUImplementation.setSelection(true);
				} else {
					for (Button b : btnMgrDispatchProtocol) {
						if (b.getText().equals("None")) {
							b.setSelection(true);
							txtMgrPeriod.setText("");
						} else {
							b.setSelection(false);
						}
						b.setEnabled(false);
					}
					for (Button b : btnGateDispatchProtocol) {
						if (b.getText().equals("None")) {
							b.setSelection(true);
							txtGatePeriod.setText("");
						} else {
							b.setSelection(false);
						}
						b.setEnabled(false);
					}
					btnUseKUImplementation.setSelection(false);
				}
				lblMgrDispatchProtocolField.setEnabled(btnCreateThread.getSelection());
				mgrProtocolGroup.setEnabled(btnCreateThread.getSelection());
				lblMgrPeriodField.setEnabled(btnCreateThread.getSelection());
				txtMgrPeriod.setEnabled(btnCreateThread.getSelection());

				lblGateDispatchProtocolField.setEnabled(btnCreateThread.getSelection());
				gateProtocolGroup.setEnabled(btnCreateThread.getSelection());
				lblGatePeriodField.setEnabled(btnCreateThread.getSelection());
				txtGatePeriod.setEnabled(btnCreateThread.getSelection());

				lblUseKUImplementationField.setEnabled(btnCreateThread.getSelection());
				btnUseKUImplementation.setEnabled(btnCreateThread.getSelection());
			}
		});
		btnCreateThread.setSelection(true);

	}


	@Override
	protected void okPressed() {
		if (!saveInput()) {
			return;
		}
		super.okPressed();
	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private boolean saveInput() {
		final List<Classifier> componentsInPackage = AadlUtil.getContainingPackageSection(context)
				.getOwnedClassifiers();

		// Comm driver
		if (cboCommDrivers != null) {
			commDriver = cboCommDrivers.getText();
			if (commDriver.isEmpty() || commDriver.contentEquals(NO_COMM_DRIVER_SELECTED)
					|| !commDrivers.contains(commDriver)) {
				Dialog.showError("Attestation Transform", "A communication driver must be specified.");
				return false;
			}
		}

		// Attestation Manager Component Name
		if (!txtMgrComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtMgrComponentName.getText())) {
			Dialog.showError("Attestation Transform",
					"Attestation Manager component name " + txtMgrComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			folder.setSelection(MGR_TAB_IDX);
			return false;
		} else if (!txtMgrComponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtMgrComponentName.getText()) != null) {
			Dialog.showError("Attestation Transform", "Component " + txtMgrComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtMgrComponentName.setText(
					ModelTransformUtils.getUniqueName(txtMgrComponentName.getText(), true, componentsInPackage));
			folder.setSelection(MGR_TAB_IDX);
			return false;
		} else {
			attestationManagerComponentName = txtMgrComponentName.getText();
		}

		// Attestation Gate Component Name
		if (!txtGateComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtGateComponentName.getText())) {
			Dialog.showError("Attestation Transform",
					"Attestation Gate component name " + txtGateComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			folder.setSelection(GATE_TAB_IDX);
			return false;
		} else if (!txtGateComponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtGateComponentName.getText()) != null) {
			Dialog.showError("Attestation Transform", "Component " + txtGateComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtGateComponentName.setText(
					ModelTransformUtils.getUniqueName(txtGateComponentName.getText(), true, componentsInPackage));
			folder.setSelection(GATE_TAB_IDX);
			return false;
		} else {
			attestationGateComponentName = txtGateComponentName.getText();
		}

		// Attestation Manager Subcomponent Instance Name
		if (!txtMgrSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtMgrSubcomponentName.getText())) {
			Dialog.showError("Attestation Transform",
					"Attestation Manager subcomponent instance name " + txtMgrSubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			folder.setSelection(MGR_TAB_IDX);
			return false;
		} else if (!txtMgrSubcomponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(context.getAllSubcomponents(),
				txtMgrSubcomponentName.getText()) != null) {
			Dialog.showError("Attestation Transform", "Subcomponent " + txtMgrSubcomponentName.getText()
					+ " already exists in " + context.getName() + ". Use the suggested name or enter a new one.");
			txtMgrSubcomponentName.setText(ModelTransformUtils.getUniqueName(txtMgrSubcomponentName.getText(), true,
					context.getAllSubcomponents()));
			folder.setSelection(MGR_TAB_IDX);
			return false;
		} else {
			attestationManagerSubcomponentName = txtMgrSubcomponentName.getText();
		}

		// Attestation Gate Subcomponent Instance Name
		if (!txtGateSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtGateSubcomponentName.getText())) {
			Dialog.showError("Attestation Transform",
					"Attestation Gate subcomponent instance name " + txtGateSubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			folder.setSelection(GATE_TAB_IDX);
			return false;
		} else if (!txtGateSubcomponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(context.getAllSubcomponents(),
				txtGateSubcomponentName.getText()) != null) {
			Dialog.showError("Attestation Transform", "Subcomponent " + txtGateSubcomponentName.getText()
					+ " already exists in " + context.getName() + ". Use the suggested name or enter a new one.");
			txtGateSubcomponentName.setText(ModelTransformUtils.getUniqueName(txtGateSubcomponentName.getText(), true,
					context.getAllSubcomponents()));
			folder.setSelection(GATE_TAB_IDX);
			return false;
		} else {
			attestationGateSubcomponentName = txtGateSubcomponentName.getText();
		}

		// Request Message Data Type
		if (!txtRequestMessageDataType.getText().isEmpty() && !txtRequestMessageDataType.getText().contains("::")) {
			// Check if type is defined in current package
			if (AadlUtil.findNamedElementInList(componentsInPackage, txtRequestMessageDataType.getText()) == null) {
				Dialog.showError("Attestation Transform",
						"Request message data type was not found in package "
								+ AadlUtil.getContainingPackage(context).getName()
								+ ". Enter the data type's qualified name if it is defined in a different package.");
				folder.setSelection(MGR_TAB_IDX);
				return false;
			}
		}
		requestMessageDataType = txtRequestMessageDataType.getText();

		// Response Message Data Type
		if (!txtResponseMessageDataType.getText().isEmpty() && !txtResponseMessageDataType.getText().contains("::")) {
			// Check if type is defined in current package
			if (AadlUtil.findNamedElementInList(componentsInPackage, txtResponseMessageDataType.getText()) == null) {
				Dialog.showError("Attestation Transform",
						"Response message data type was not found in package "
								+ AadlUtil.getContainingPackage(context).getName()
								+ ". Enter the data type's qualified name if it is defined in a different package.");
				folder.setSelection(MGR_TAB_IDX);
				return false;
			}
		}
		responseMessageDataType = txtResponseMessageDataType.getText();


		// TODO: Timeout
//		try {
//			if (!txtCacheTimeout.getText().isEmpty()) {
//				cacheTimeout = Long.parseLong(txtCacheTimeout.getText());
//			}
//		} catch (NumberFormatException e) {
//			Dialog.showError("Attestation Transform", "Value of Cache Timeout must be an integer.");
//			return false;
//		}

//		// Cache Size
//		try {
//			cacheSize = Long.parseLong(cboCacheSize.getText());
//		} catch (NumberFormatException e) {
//			Dialog.showError("Attestation Transform", "Value of Cache Size must be an integer.");
//			return false;
//		}

		// ID List Data Type
		if (!txtIdListDataType.getText().isEmpty() && !txtIdListDataType.getText().contains("::")) {
			// Check if type is defined in current package
			if (AadlUtil.findNamedElementInList(componentsInPackage, txtIdListDataType.getText()) == null) {
				Dialog.showError("Attestation Transform",
						"ID list data type was not found in package " + AadlUtil.getContainingPackage(context).getName()
								+ ". Enter the data type's qualified name if it is defined in a different package.");
				folder.setSelection(MGR_TAB_IDX);
				return false;
			}
		}
		idListDataType = txtIdListDataType.getText();

		// Gate port names
		gatePortNames = pncPortNames.getContents();
		// Make sure there are no duplicate names
		final Set<String> portNames = new HashSet<>();
		for (List<String> port : gatePortNames.values()) {
			if (!portNames.add(port.get(0).toLowerCase())) {
				Dialog.showError("Attestation Transform", "Attestation Gate has multiple ports named " + port.get(0)
							+ ". All port names must be unique.");
				folder.setSelection(GATE_TAB_IDX);
				return false;
			} else if (!portNames.add(port.get(1).toLowerCase())) {
				Dialog.showError("Attestation Transform", "Attestation Gate has multiple ports named " + port.get(1)
						+ ". All port names must be unique.");
				folder.setSelection(GATE_TAB_IDX);
				return false;
			} else if (port.get(0).equalsIgnoreCase(port.get(1))) {
				Dialog.showError("Attestation Transform", "Attestation Gate has multiple ports named " + port.get(0)
						+ ". All port names must be unique.");
				folder.setSelection(GATE_TAB_IDX);
				return false;
			}
		}

		// Create thread
		if (btnCreateThread != null) {
			createThread = btnCreateThread.getSelection();
		}

		// Mgr Dispatch Protocol and Period
		for (Button b : btnMgrDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				mgrDispatchProtocol = b.getText();
				// make sure period is properly formatted
				if (txtMgrPeriod.getText().isEmpty()
						|| txtMgrPeriod.getText().matches("((\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?)")) {
					mgrPeriod = txtMgrPeriod.getText();
				} else {
					Dialog.showError("Attestation Transform", "Attestation Manager period " + txtMgrPeriod.getText()
							+ " is malformed. See the AADL definition of Period in Timing_Properties.aadl.");
					folder.setSelection(MGR_TAB_IDX);
					return false;
				}
				break;
			}
		}

		// Gate Dispatch Protocol and Period
		for (Button b : btnGateDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				gateDispatchProtocol = b.getText();
				// make sure period is properly formatted
				if (txtGatePeriod.getText().isEmpty()
						|| txtGatePeriod.getText().matches("((\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?)")) {
					gatePeriod = txtGatePeriod.getText();
				} else {
					Dialog.showError("Attestation Transform", "Attestation Gate period " + txtGatePeriod.getText()
							+ " is malformed. See the AADL definition of Period in Timing_Properties.aadl.");
					folder.setSelection(GATE_TAB_IDX);
					return false;
				}
				break;
			}
		}

		// Mgr Stack Size
		if (txtMgrStackSize.getText().isEmpty()
				|| txtMgrStackSize.getText().matches("((\\d)+(\\s)*(bits|Bytes|KByte|MByte|GByte|TByte)?)")) {
			mgrStackSize = txtMgrStackSize.getText();
		} else {
			Dialog.showError("Attestation Transform", "Attestation Manager stack size " + txtMgrStackSize.getText()
					+ " is malformed. See the AADL definition of Stack_Size in Memory_Properties.aadl.");
			folder.setSelection(MGR_TAB_IDX);
			return false;
		}

		// Gate Stack Size
		if (txtGateStackSize.getText().isEmpty()
				|| txtGateStackSize.getText().matches("((\\d)+(\\s)*(bits|Bytes|KByte|MByte|GByte|TByte)?)")) {
			gateStackSize = txtGateStackSize.getText();
		} else {
			Dialog.showError("Attestation Transform", "Attestation Gate stack size " + txtGateStackSize.getText()
					+ " is malformed. See the AADL definition of Stack_Size in Memory_Properties.aadl.");
			folder.setSelection(GATE_TAB_IDX);
			return false;
		}

		// Mgr Log Port
		mgrLogPortType = null;
		for (int i = 0; i < btnMgrLogPortType.size(); i++) {
			if (btnMgrLogPortType.get(i).getSelection()) {
				mgrLogPortType = PortCategory.get(i);
				break;
			}
		}

		// Gate Log Port
		gateLogPortType = null;
		for (int i = 0; i < btnGateLogPortType.size(); i++) {
			if (btnGateLogPortType.get(i).getSelection()) {
				gateLogPortType = PortCategory.get(i);
				break;
			}
		}

		// Use KU implementation
		useKUImplementation = btnUseKUImplementation.getSelection();

		// Requirement
		requirement = cboRequirement.getText();
		if (requirement.equals(NO_REQUIREMENT_SELECTED)) {
			requirement = "";
		} else if (!requirements.contains(requirement)) {
			Dialog.showError("Attestation Transform", "Attestation requirement " + requirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

//		// AGREE
//		gateAgreeProperty = txtGateAgreeProperty.getText();

		return true;
	}

	public String getCommDriver() {
		return commDriver;
	}

	public String getAttestationManagerComponentName() {
		return attestationManagerComponentName;
	}

	public String getAttestationGateComponentName() {
		return attestationGateComponentName;
	}

	public String getAttestationManagerSubcomponentName() {
		return attestationManagerSubcomponentName;
	}

	public String getAttestationGateSubcomponentName() {
		return attestationGateSubcomponentName;
	}

	public String getRequestMessageDataType() {
		return requestMessageDataType;
	}

	public String getResponseMessageDataType() {
		return responseMessageDataType;
	}

	public long getCacheTimeout() {
		return cacheTimeout;
	}

//	public long getCacheSize() {
//		return cacheSize;
//	}

	public String getIdListDataType() {
		return idListDataType;
	}

	public Map<String, List<String>> getGatePortNames() {
		return gatePortNames;
	}

	public boolean createThread() {
		return createThread;
	}

	public String getMgrDispatchProtocol() {
		return mgrDispatchProtocol;
	}

	public String getMgrPeriod() {
		return mgrPeriod;
	}

	public String getMgrStackSize() {
		return mgrStackSize;
	}

	public String getGateDispatchProtocol() {
		return gateDispatchProtocol;
	}

	public String getGatePeriod() {
		return gatePeriod;
	}

	public String getGateStackSize() {
		return gateStackSize;
	}

	public PortCategory getMgrLogPortType() {
		return mgrLogPortType;
	}

	public PortCategory getGateLogPortType() {
		return gateLogPortType;
	}

	public boolean useKUImplementation() {
		return useKUImplementation;
	}

	public String getRequirement() {
		return requirement;
	}

//	public String getGateAgreeProperty() {
//		return gateAgreeProperty;
//	}

}
