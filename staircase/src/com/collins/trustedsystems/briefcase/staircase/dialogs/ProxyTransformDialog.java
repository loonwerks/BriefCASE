package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ThreadGroupImplementation;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.handlers.ProxyTransformHandler;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

public class ProxyTransformDialog extends TitleAreaDialog {

	private ComponentImplementation context;
	private Text txtHighProxyComponentName;
	private Text txtHighProxySubcomponentName;
	private Text txtLowProxyComponentName;
	private Text txtLowProxySubcomponentName;
	private List<Button> btnDispatchProtocol = new ArrayList<>();
	private Label lblPeriodField;
	private Text txtPeriod;
	private Combo cboProxyRequirement;
	private String highProxyComponentName = "";
	private String highProxySubcomponentName = "";
	private String lowProxyComponentName = "";
	private String lowProxySubcomponentName = "";
	private String dispatchProtocol = "";
	private String period = "";
	private String proxyRequirement = "";

	private List<String> inports = new ArrayList<>();
	private List<String> outports = new ArrayList<>();

	private List<String> requirements = new ArrayList<>();
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public ProxyTransformDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Proxy Transform");
		setMessage("Enter Proxy Transform details for " + context.getName() + ".", IMessageProvider.NONE);
	}

	public void create(ComponentImplementation context) {
		this.context = context;
		// Provide list of outports and inports that can be connected to the proxies
		outports = ModelTransformUtils.getOutports(context);
		// Provide list of inports that monitor's alert out port can be connected to
		inports = ModelTransformUtils.getInports(context);
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
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Proxy high component name
		createHighProxyComponentNameField(container);
		// Proxy high subcomponent name
		createHighProxySubcomponentNameField(container);
		// Proxy low component name
		createLowProxyComponentNameField(container);
		// Proxy low subcomponent name
		createLowProxySubcomponentNameField(container);
		// Ports

		// Only display dispatch protocol if filter is a thread
		if (context instanceof ProcessImplementation || context instanceof ThreadGroupImplementation) {
			createDispatchProtocolField(container);
		}

		// Add proxy information fields
		createRequirementField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the high proxy component name
	 * @param container
	 */
	private void createHighProxyComponentNameField(Composite container) {
		final Label lblHighProxyComponentNameField = new Label(container, SWT.NONE);
		lblHighProxyComponentNameField.setText("High proxy component type name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtHighProxyComponentName = new Text(container, SWT.BORDER);
		txtHighProxyComponentName.setLayoutData(dataInfoField);
		txtHighProxyComponentName.setText(ProxyTransformHandler.HIGH_PROXY_COMP_TYPE_NAME);
	}

	/**
	 * Creates the input text field for specifying the high proxy subcomponent name
	 * @param container
	 */
	private void createHighProxySubcomponentNameField(Composite container) {
		final Label lblHighProxySubcomponentNameField = new Label(container, SWT.NONE);
		lblHighProxySubcomponentNameField.setText("High proxy subcomponent name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtHighProxySubcomponentName = new Text(container, SWT.BORDER);
		txtHighProxySubcomponentName.setLayoutData(dataInfoField);
		txtHighProxySubcomponentName.setText(ProxyTransformHandler.HIGH_PROXY_SUBCOMP_NAME);
	}

	/**
	 * Creates the input text field for specifying the low proxy component name
	 * @param container
	 */
	private void createLowProxyComponentNameField(Composite container) {
		final Label lblLowProxyComponentNameField = new Label(container, SWT.NONE);
		lblLowProxyComponentNameField.setText("Low proxy component type name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtLowProxyComponentName = new Text(container, SWT.BORDER);
		txtLowProxyComponentName.setLayoutData(dataInfoField);
		txtLowProxyComponentName.setText(ProxyTransformHandler.LOW_PROXY_COMP_TYPE_NAME);
	}

	/**
	 * Creates the input text field for specifying the low proxy subcomponent name
	 * @param container
	 */
	private void createLowProxySubcomponentNameField(Composite container) {
		final Label lblLowProxySubcomponentNameField = new Label(container, SWT.NONE);
		lblLowProxySubcomponentNameField.setText("Low proxy subcomponent name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtLowProxySubcomponentName = new Text(container, SWT.BORDER);
		txtLowProxySubcomponentName.setLayoutData(dataInfoField);
		txtLowProxySubcomponentName.setText(ProxyTransformHandler.LOW_PROXY_SUBCOMP_NAME);
	}

	/**
	 * Creates the input field for selecting the dispatch protocol
	 * @param container
	 */
	private void createDispatchProtocolField(Composite container) {
		final Label lblDispatchProtocolField = new Label(container, SWT.NONE);
		lblDispatchProtocolField.setText("Dispatch protocol");
		lblDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		final Group protocolGroup = new Group(container, SWT.NONE);
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
	 * the proxy transform in this design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		final Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboProxyRequirement = new Combo(container, SWT.BORDER);
		cboProxyRequirement.setLayoutData(dataInfoField);
		cboProxyRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboProxyRequirement.add(r));
		cboProxyRequirement.setText(NO_REQUIREMENT_SELECTED);
	}

	@Override
	protected void okPressed() {
		saveInput();
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
		final Set<String> portNames = new HashSet<>();

		// High Proxy Component Name
		if (!txtHighProxyComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtHighProxyComponentName.getText())) {
			Dialog.showError("Proxy Transform", "High Proxy component name " + txtHighProxyComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (AadlUtil.findNamedElementInList(componentsInPackage, txtHighProxyComponentName.getText()) != null) {
			Dialog.showError("Proxy Transform", "Component " + txtHighProxyComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtHighProxyComponentName.setText(
					ModelTransformUtils.getUniqueName(txtHighProxyComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			highProxyComponentName = txtHighProxyComponentName.getText();
		}

		// High Proxy Subcomponent Name
		if (!txtHighProxySubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtHighProxySubcomponentName.getText())) {
			Dialog.showError("Proxy Transform",
					"High Proxy subcomponent instance name " + txtHighProxySubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (AadlUtil.findNamedElementInList(context.getOwnedSubcomponents(),
				txtHighProxySubcomponentName.getText()) != null) {
			Dialog.showError("Proxy Transform", "Subcomponent " + txtHighProxySubcomponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtHighProxySubcomponentName.setText(ModelTransformUtils
					.getUniqueName(txtHighProxySubcomponentName.getText(), true, context.getOwnedSubcomponents()));
			return false;
		} else {
			highProxySubcomponentName = txtHighProxySubcomponentName.getText();
		}

		// Low Proxy Component Name
		if (!txtLowProxyComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtLowProxyComponentName.getText())) {
			Dialog.showError("Proxy Transform", "Low Proxy component name " + txtLowProxyComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (AadlUtil.findNamedElementInList(componentsInPackage, txtLowProxyComponentName.getText()) != null) {
			Dialog.showError("Proxy Transform", "Component " + txtLowProxyComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtLowProxyComponentName.setText(
					ModelTransformUtils.getUniqueName(txtLowProxyComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			lowProxyComponentName = txtLowProxyComponentName.getText();
		}

		// Low Proxy Subcomponent Name
		if (!txtLowProxySubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtLowProxySubcomponentName.getText())) {
			Dialog.showError("Proxy Transform",
					"Low Proxy subcomponent instance name " + txtLowProxySubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (AadlUtil.findNamedElementInList(context.getOwnedSubcomponents(),
				txtLowProxySubcomponentName.getText()) != null) {
			Dialog.showError("Proxy Transform", "Subcomponent " + txtLowProxySubcomponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtLowProxySubcomponentName.setText(ModelTransformUtils.getUniqueName(txtLowProxySubcomponentName.getText(),
					true, context.getOwnedSubcomponents()));
			return false;
		} else {
			lowProxySubcomponentName = txtLowProxySubcomponentName.getText();
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
		proxyRequirement = cboProxyRequirement.getText();
		if (proxyRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			proxyRequirement = "";
		} else if (!requirements.contains(proxyRequirement)) {
			Dialog.showError("Proxy Transform",
					"Proxy requirement " + proxyRequirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

		return true;
	}

	public String getHighProxyComponentName() {
		return highProxyComponentName;
	}

	public String getHighProxySubcomponentName() {
		return highProxySubcomponentName;
	}

	public String getLowProxyComponentName() {
		return lowProxyComponentName;
	}

	public String getLowProxySubcomponentName() {
		return lowProxySubcomponentName;
	}

	public String getDispatchProtocol() {
		return dispatchProtocol;
	}

	public String getPeriod() {
		return period;
	}

	public String getRequirement() {
		return proxyRequirement;
	}

}
