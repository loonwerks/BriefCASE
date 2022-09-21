package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Connection;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.preferences.BriefcasePreferenceConstants;
import com.collins.trustedsystems.briefcase.staircase.handlers.FilterTransformHandler;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;

/**
 * This class creates the Add Filter wizard
 */
public class FilterTransformDialog extends TitleAreaDialog {

	private ComponentImplementation context = null;
	private List<String> connections = new ArrayList<>();
	private Combo cboConnections = null;
	private Text txtFilterComponentName;
	private Text txtFilterSubcomponentName;
	private Button btnCreateThread = null;
	private Label lblDispatchProtocolField;
	private Group protocolGroup;
	private List<Button> btnDispatchProtocol = new ArrayList<>();
	private Label lblPeriodField;
	private Text txtPeriod;
	private Text txtExecutionTime;
	private Text txtDomain;
	private Text txtStackSize;
	private Text txtInputPortName;
	private Text txtOutputPortName;
	private List<Button> btnLogPortType = new ArrayList<>();
	private Combo cboFilterRequirement;
	private Text txtPolicy;
	private String connection = "";
	private String filterComponentName = "";
	private String filterSubcomponentName = "";
	private boolean createThread = false;
	private String filterDispatchProtocol = "";
	private String filterPeriod = "";
	private String filterExecutionTime = "";
	private Integer filterDomain = null;
	private String filterStackSize = "";
	private String filterInputPortName = "";
	private String filterOutputPortName = "";
	private PortCategory logPortType = null;
	private String filterRequirement = "";
	private String policy = "";
	private List<String> requirements = new ArrayList<>();

	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";
	private static final String NO_CONNECTION_SELECTED = "<No connection selected>";

	public FilterTransformDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Filter Transform");
		setMessage(
				"Enter Filter details.  You may optionally leave these fields empty and manually edit the AADL filter component once it is added to the model.",
				IMessageProvider.NONE);
	}

	public void create(EObject context) {

		if (context instanceof Connection) {
			this.context = ((Connection) context).getContainingComponentImpl();
		} else if (context instanceof ComponentImplementation) {
			this.context = (ComponentImplementation) context;
			for (Connection conn : this.context.getOwnedConnections()) {
				Subcomponent subcomponent = (Subcomponent) conn.getDestination().getContext();
				if (subcomponent != null) {
					final ComponentCategory compCategory = subcomponent.getCategory();
					if (compCategory == ComponentCategory.THREAD || compCategory == ComponentCategory.THREAD_GROUP
							|| compCategory == ComponentCategory.PROCESS) {
						this.connections.add(conn.getName());
					}
				}
			}
		}

		// Provide list of requirements so the user can choose which requirement is driving this
		// model transformation.
		// We only want to list requirements that aren't already associated with a filter transform
		for (CyberRequirement req : RequirementsManager.getInstance().getImportedRequirements()) {
			boolean addFilterFound = false;
			try {
				final FunctionDefinition fd = req.getResoluteClaim();
				for (FnCallExpr fnCallExpr : EcoreUtil2.getAllContentsOfType(fd.getBody().getExpr(),
						FnCallExpr.class)) {
					if (fnCallExpr.getFn().getName().equalsIgnoreCase("add_filter")) {
						addFilterFound = true;
						break;
					}
				}
			} catch (NullPointerException e) {
				addFilterFound = false;
			}

			if (!addFilterFound) {
				requirements.add(req.getId());
			}

		}

		create();
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

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);
		final Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		if (!connections.isEmpty()) {
			cboConnections = TransformDialogUtil.createComboField(container, "Place Filter on connection", connections,
					NO_CONNECTION_SELECTED);
		}
		// Add filter information fields
		txtFilterComponentName = TransformDialogUtil.createTextField(container, "Filter component name",
				ModelTransformUtils.getUniqueName(FilterTransformHandler.FILTER_COMP_TYPE_NAME, true,
						AadlUtil.getContainingPackageSection(context).getOwnedClassifiers()));
		txtFilterSubcomponentName = TransformDialogUtil.createTextField(container, "Filter subcomponent name",
				ModelTransformUtils.getUniqueName(FilterTransformHandler.FILTER_SUBCOMP_NAME, true,
						context.getOwnedSubcomponents()));
		if (context instanceof SystemImplementation) {
			createCreateThreadField(container);
		}
		createDispatchProtocolField(container);
		txtExecutionTime = TransformDialogUtil.createTextField(container, "Execution time",
				Platform.getPreferencesService()
						.getString("com.collins.trustedsystems.briefcase", BriefcasePreferenceConstants.EXECUTION_TIME,
								"", null));
		if (context instanceof SystemImplementation) {
			txtDomain = TransformDialogUtil.createTextField(container, "Domain", "");
		}
		txtStackSize = TransformDialogUtil.createTextField(container, "Stack size", Platform.getPreferencesService()
				.getString("com.collins.trustedsystems.briefcase", BriefcasePreferenceConstants.STACK_SIZE, "", null));
		createPortNamesField(container);

		btnLogPortType = TransformDialogUtil.createLogPortField(container);
		cboFilterRequirement = TransformDialogUtil.createComboField(container, "Requirement", requirements,
				NO_REQUIREMENT_SELECTED);
		txtPolicy = TransformDialogUtil.createTextField(container, "Filter policy", "");

		// set focus
		parent.forceFocus();

		return area;
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

		btnCreateThread = new Button(container, SWT.CHECK);
		btnCreateThread.setSelection(true);
		btnCreateThread.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
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
	 * Creates the input field for selecting the dispatch protocol and period
	 * @param container
	 */
	private void createDispatchProtocolField(Composite container) {
		lblDispatchProtocolField = new Label(container, SWT.NONE);
		lblDispatchProtocolField.setText("Dispatch protocol");
		lblDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Create a group to contain the protocol options
		protocolGroup = new Group(container, SWT.NONE);
		protocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		protocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnDispatchProtocol.clear();

		final Button btnNoProtocol = new Button(protocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(Platform.getPreferencesService()
				.getBoolean("com.collins.trustedsystems.briefcase", BriefcasePreferenceConstants.DISPATCH_PROTOCOL_NONE,
						false, null));
		btnNoProtocol.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblPeriodField.setEnabled(!btnNoProtocol.getSelection());
				txtPeriod.setEnabled(!btnNoProtocol.getSelection());
				if (btnNoProtocol.getSelection()) {
					txtPeriod.setText("");
				} else {
					txtPeriod.setText(Platform.getPreferencesService()
							.getString("com.collins.trustedsystems.briefcase", BriefcasePreferenceConstants.PERIOD, "",
									null));
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btnPeriodic = new Button(protocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(Platform.getPreferencesService()
				.getBoolean("com.collins.trustedsystems.briefcase",
						BriefcasePreferenceConstants.DISPATCH_PROTOCOL_PERIODIC, true, null));

		final Button btnSporadic = new Button(protocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(Platform.getPreferencesService()
				.getBoolean("com.collins.trustedsystems.briefcase",
						BriefcasePreferenceConstants.DISPATCH_PROTOCOL_SPORADIC, false, null));

		lblPeriodField = new Label(container, SWT.NONE);
		lblPeriodField.setText("Period");
		lblPeriodField.setEnabled(false);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtPeriod = new Text(container, SWT.BORDER);
		txtPeriod.setLayoutData(dataInfoField);
		txtPeriod.setEnabled(false);

		btnDispatchProtocol.add(btnNoProtocol);
		btnDispatchProtocol.add(btnPeriodic);
		btnDispatchProtocol.add(btnSporadic);

		btnNoProtocol.notifyListeners(SWT.Selection, null);
	}

	/**
	 * Creates the input field for specifying the names of the filter input and output ports
	 * @param container
	 */
	private void createPortNamesField(Composite container) {
		final Label lblPortNamesField = new Label(container, SWT.NONE);
		lblPortNamesField.setText("Filter Port Names");
		lblPortNamesField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		final Composite portNames = new Composite(container, SWT.BORDER);
		portNames.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		portNames.setLayout(new GridLayout(2, true));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;

		final Label lblInputPortName = new Label(portNames, SWT.NONE);
		lblInputPortName.setText("Input port name");
		lblInputPortName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		txtInputPortName = new Text(portNames, SWT.BORDER);
		txtInputPortName.setLayoutData(dataInfoField);

		final Label lblOutputPortName = new Label(portNames, SWT.NONE);
		lblOutputPortName.setText("Output port name");
		lblOutputPortName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		txtOutputPortName = new Text(portNames, SWT.BORDER);
		txtOutputPortName.setLayoutData(dataInfoField);

		txtInputPortName.setText(FilterTransformHandler.FILTER_PORT_IN_NAME);
		txtOutputPortName.setText(FilterTransformHandler.FILTER_PORT_OUT_NAME);
	}


	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 */
	private boolean saveInput() {
		final List<Classifier> componentsInPackage = AadlUtil.getContainingPackageSection(context)
				.getOwnedClassifiers();

		// Connection
		if (cboConnections != null) {
			connection = cboConnections.getText();
			if (connection.equals(NO_CONNECTION_SELECTED)) {
				Dialog.showError("Filter Transform", "A connection must be selected to insert the filter.");
				return false;
			} else if (!connections.contains(connection)) {
				Dialog.showError("Filter Transform", "Connection " + connection
						+ " does not exist in the model.  Select a connection from the list.");
				return false;
			}
		}

		// Filter Component Name
		txtFilterComponentName.setText(txtFilterComponentName.getText().trim());
		if (!txtFilterComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtFilterComponentName.getText())) {
			Dialog.showError("Filter Transform", "Filter component name " + txtFilterComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!txtFilterComponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtFilterComponentName.getText()) != null) {
			Dialog.showError("Filter Transform", "Component " + txtFilterComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtFilterComponentName.setText(
					ModelTransformUtils.getUniqueName(txtFilterComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			filterComponentName = txtFilterComponentName.getText();
		}

		// Filter Subcomponent Instance Name
		txtFilterSubcomponentName.setText(txtFilterSubcomponentName.getText().trim());
		if (!txtFilterSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtFilterSubcomponentName.getText())) {
			Dialog.showError("Filter Transform",
					"Filter subcomponent instance name " + txtFilterSubcomponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!txtFilterSubcomponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(context.getAllSubcomponents(),
				txtFilterSubcomponentName.getText()) != null) {
			Dialog.showError("Filter Transform", "Subcomponent " + txtFilterSubcomponentName.getText()
					+ " already exists in " + context.getName() + ". Use the suggested name or enter a new one.");
			txtFilterSubcomponentName.setText(ModelTransformUtils.getUniqueName(txtFilterSubcomponentName.getText(),
					true, context.getAllSubcomponents()));
			return false;
		} else {
			filterSubcomponentName = txtFilterSubcomponentName.getText();
		}

		// Create thread
		if (btnCreateThread != null) {
			createThread = btnCreateThread.getSelection();
		}

		// Dispatch Protocol and Period
		for (Button b : btnDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				filterDispatchProtocol = b.getText();
				// make sure period is properly formatted
				if (txtPeriod.getText().isEmpty()
						|| txtPeriod.getText().matches("((\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?)")) {
					filterPeriod = txtPeriod.getText();
				} else {
					Dialog.showError("Filter Transform", "Filter period " + txtPeriod.getText()
							+ " is malformed. See the AADL definition of Period in Timing_Properties.aadl.");
					return false;
				}
				break;
			}
		}

		// Execution Time
		if (txtExecutionTime.getText().isEmpty()
				|| txtExecutionTime.getText()
						.matches(
								"((\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?\\.\\.(\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?)")) {
			filterExecutionTime = txtExecutionTime.getText();
		} else {
			Dialog.showError("Filter Transform", "Filter execution time " + txtExecutionTime.getText()
					+ " is malformed. See the AADL definition of Compute_Execution_Time in Timing_Properties.aadl.");
			return false;
		}

		// Domain
		try {
			if (txtDomain != null && !txtDomain.getText().isBlank()) {
				filterDomain = Integer.parseInt(txtDomain.getText().trim());
				if (filterDomain < 2) {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			Dialog.showError("Filter Transform",
					"Filter domain " + txtDomain.getText() + " must be an integer greater than 1.");
			filterDomain = null;
			return false;
		}

		// Stack Size
		if (txtStackSize.getText().isEmpty()
				|| txtStackSize.getText().matches("((\\d)+(\\s)*(bits|Bytes|KByte|MByte|GByte|TByte)?)")) {
			filterStackSize = txtStackSize.getText();
		} else {
			Dialog.showError("Filter Transform", "Filter stack size " + txtStackSize.getText()
					+ " is malformed. See the AADL definition of Stack_Size in Memory_Properties.aadl.");
			return false;
		}

		// Input Port Name
		if (txtInputPortName.getText().isEmpty() || ModelTransformUtils.isValidName(txtInputPortName.getText())) {
			filterInputPortName = txtInputPortName.getText();
		} else {
			Dialog.showError("Filter Transform", "Filter input port name " + txtInputPortName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		}

		// Output Port Name
		if (txtOutputPortName.getText().isEmpty() || ModelTransformUtils.isValidName(txtOutputPortName.getText())) {
			if (!txtOutputPortName.getText().isEmpty()
					&& txtOutputPortName.getText().equalsIgnoreCase(txtInputPortName.getText())) {
				Dialog.showError("Filter Transform", "Filter input and output port names must be different");
				return false;
			}
			filterOutputPortName = txtOutputPortName.getText();
		} else {
			Dialog.showError("Filter Transform", "Filter output port name " + txtOutputPortName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		}

		// Log Port
		logPortType = null;
		for (int i = 0; i < btnLogPortType.size(); i++) {
			if (btnLogPortType.get(i).getSelection()) {
				logPortType = PortCategory.get(i);
				break;
			}
		}

		// Requirement
		filterRequirement = cboFilterRequirement.getText();
		if (filterRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			filterRequirement = "";
		} else if (!requirements.contains(filterRequirement)) {
			Dialog.showError("Filter Transform",
					"Filter requirement " + filterRequirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

		// AGREE Property
		policy = txtPolicy.getText();

		return true;
	}

	@Override
	protected void okPressed() {
		if (!saveInput()) {
			return;
		}
		super.okPressed();
	}

	public String getConnection() {
		return connection;
	}

	public String getFilterComponentName() {
		return filterComponentName;
	}

	public String getFilterSubcomponentName() {
		return filterSubcomponentName;
	}

	public boolean createThread() {
		return createThread;
	}

	public String getDispatchProtocol() {
		return filterDispatchProtocol;
	}

	public String getPeriod() {
		return filterPeriod;
	}

	public String getExecutionTime() {
		return filterExecutionTime;
	}

	public Integer getDomain() {
		return filterDomain;
	}

	public String getStackSize() {
		return filterStackSize;
	}

	public String getInputPortName() {
		return filterInputPortName;
	}

	public String getOutputPortName() {
		return filterOutputPortName;
	}

	public PortCategory getLogPortType() {
		return logPortType;
	}

	public String getPolicy() {
		return policy;
	}

	public String getRequirement() {
		return filterRequirement;
	}

}