package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
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
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.handlers.AddAttestationManagerHandler;
import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;

/**
 * This class creates the Add Attestation Manager wizard
 */
public class AddAttestationManagerDialog extends TitleAreaDialog {

	private Text txtImplementationName;
	private Text txtImplementationLanguage;
	private Text txtCacheTimeout;
	private Combo cboCacheSize;
	private List<Button> btnDispatchProtocol = new ArrayList<>();
	private List<Button> btnLogPortType = new ArrayList<>();
	private Combo cboRequirement;
	private Button btnPropagateGuarantees;
	private Text txtAgreeProperty;
	private String implementationName;
	private String implementationLanguage = "";
	private String cacheTimeout;
	private String cacheSize;
	private String dispatchProtocol = "";
	private PortCategory logPortType = null;
	private String requirement;
	private boolean propagateGuarantees;
	private String commDriver = "";
	private List<String> requirements = new ArrayList<>();
	private String agreeProperty;
	private Subcomponent attestationManager = null;

	private static final int MAX_CACHE_SIZE = 6;
	private static final int DEFAULT_CACHE_SIZE = 4;

	private static final String DEFAULT_IMPL_LANGUAGE = "CakeML";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AddAttestationManagerDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Attestation Manager");
		setMessage(
				"Enter Attestation Manager details.  You may optionally leave these fields empty and manually edit the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(1);
		return size;
	}

	public void create(String commDriver, List<String> requirements, Subcomponent attestationManager) {
		this.commDriver = commDriver;
		this.requirements = requirements;
		this.attestationManager = attestationManager;

		if (commDriver == null || commDriver.isEmpty()) {
			Dialog.showError("Add Attestation Manager", "Unknown communication driver.");
			return;
		}
		super.create();
		setTitle("Add Attestation Manager to " + commDriver);
		setMessage("Enter Attestation Manager details for adding attestation to " + commDriver
				+ ".  You may optionally leave these fields empty and manually edit the AADL attestation manager component once it is added to the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Add attestation manager information fields
		createImplementationNameField(container);
		createImplementationLanguageField(container);
		createCacheTimeoutField(container);
		createCacheSizeField(container);
		createDispatchProtocolField(container);
		createLogPortField(container);
		createRequirementField(container);
		if (attestationManager == null) {
			createPropagateGuaranteesField(container);
			createAgreePropertyField(container);
		}

		return area;
	}


	private void createImplementationNameField(Composite container) {

		Label lblImplNameField = new Label(container, SWT.NONE);
		lblImplNameField.setText("Attestation Manager implementation name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtImplementationName = new Text(container, SWT.BORDER);
		txtImplementationName.setLayoutData(dataInfoField);
		if (attestationManager == null) {
			txtImplementationName.setText(AddAttestationManagerHandler.AM_IMPL_NAME);
		} else {
			txtImplementationName.setText(attestationManager.getName());
			txtImplementationName.setEnabled(false);
		}

	}

	private void createImplementationLanguageField(Composite container) {

		Label lblImplLangField = new Label(container, SWT.NONE);
		lblImplLangField.setText("Attestation Manager implementation language");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtImplementationLanguage = new Text(container, SWT.BORDER);
		txtImplementationLanguage.setLayoutData(dataInfoField);
		txtImplementationLanguage.setText(DEFAULT_IMPL_LANGUAGE);
		if (attestationManager != null) {
			txtImplementationLanguage.setEnabled(false);
		}
	}

	private void createCacheTimeoutField(Composite container) {

		Label lblCacheTimeoutField = new Label(container, SWT.NONE);
		lblCacheTimeoutField.setText("Cache timeout (minutes)");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtCacheTimeout = new Text(container, SWT.BORDER);
		txtCacheTimeout.setLayoutData(dataInfoField);

		if (attestationManager != null) {
			ComponentImplementation ci = attestationManager.getComponentImplementation();
			EList<PropertyExpression> propVals = ci.getPropertyValues(CaseUtils.CASE_PROPSET_NAME, "CACHE_TIMEOUT");
			if (!propVals.isEmpty()) {
				// There should be only one property value
				PropertyExpression expr = propVals.get(0);
				if (expr instanceof IntegerLiteral) {
					txtCacheTimeout.setText(Long.toString(((IntegerLiteral) expr).getValue()));
				}
			}
			txtCacheTimeout.setEnabled(false);
		}

	}

	private void createCacheSizeField(Composite container) {

		Label lblCacheSizeField = new Label(container, SWT.NONE);
		lblCacheSizeField.setText("Cache size");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		cboCacheSize = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		cboCacheSize.setLayoutData(dataInfoField);
		for (int i = 1; i <= MAX_CACHE_SIZE; i++) {
			cboCacheSize.add(Integer.toString(i));
		}
		if (attestationManager == null) {
			cboCacheSize.setText(Integer.toString(DEFAULT_CACHE_SIZE));
		} else {
			ComponentImplementation ci = attestationManager.getComponentImplementation();
			EList<PropertyExpression> propVals = ci.getPropertyValues(CaseUtils.CASE_PROPSET_NAME, "CACHE_SIZE");
			if (!propVals.isEmpty()) {
				// There should be only one property value
				PropertyExpression expr = propVals.get(0);
				if (expr instanceof IntegerLiteral) {
					cboCacheSize.setText(Long.toString(((IntegerLiteral) expr).getValue()));
				}
			}
			cboCacheSize.setEnabled(false);
		}

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
	 * Creates the input field for specifying if the attestation manager should contain
	 * a port for logging messages
	 * @param container
	 */
	private void createLogPortField(Composite container) {
		Label lblLogField = new Label(container, SWT.NONE);
		lblLogField.setText("Create log port");
		lblLogField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Create a group to contain the log port options
		Group logGroup = new Group(container, SWT.NONE);
		logGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		logGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnLogPortType.clear();

		Button btnNoLogPort = new Button(logGroup, SWT.RADIO);
		btnNoLogPort.setText("None");
		btnNoLogPort.setSelection(true);

		Button btnEventLogPort = new Button(logGroup, SWT.RADIO);
		btnEventLogPort.setText("Event");
		btnEventLogPort.setSelection(false);

		Button btnDataLogPort = new Button(logGroup, SWT.RADIO);
		btnDataLogPort.setText("Data");
		btnDataLogPort.setSelection(false);

		Button btnEventDataLogPort = new Button(logGroup, SWT.RADIO);
		btnEventDataLogPort.setText("Event Data");
		btnEventDataLogPort.setSelection(false);

		if (attestationManager != null) {
			ComponentType ct = attestationManager.getComponentType();
			for (Feature f : ct.getOwnedFeatures()) {
				if (f.getName().equalsIgnoreCase(AddAttestationManagerHandler.AM_LOG_PORT_NAME)) {
					btnNoLogPort.setSelection(false);
					if (f instanceof DataPort) {
						btnDataLogPort.setSelection(true);
					} else if (f instanceof EventPort) {
						btnEventLogPort.setSelection(true);
					} else if (f instanceof EventDataPort) {
						btnEventDataLogPort.setSelection(true);
					}
					break;
				}
			}
			btnDataLogPort.setEnabled(false);
			btnEventLogPort.setEnabled(false);
			btnEventDataLogPort.setEnabled(false);
			btnNoLogPort.setEnabled(false);
		}

		btnLogPortType.add(btnDataLogPort);
		btnLogPortType.add(btnEventLogPort);
		btnLogPortType.add(btnEventDataLogPort);
		btnLogPortType.add(btnNoLogPort);

	}

	/**
	 * Creates the input field for selecting the resolute clause that drives
	 * the addition of this filter to the design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboRequirement = new Combo(container, SWT.BORDER);
		cboRequirement.setLayoutData(dataInfoField);
		cboRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboRequirement.add(r));
		cboRequirement.setText(NO_REQUIREMENT_SELECTED);

	}

	/**
	 * Creates the input text field for specifying the guarantees to propagate
	 * @param container
	 */
	private void createPropagateGuaranteesField(Composite container) {

		Label lblPropagateGuaranteesField = new Label(container, SWT.NONE);
		lblPropagateGuaranteesField.setText("Preserve Guarantees from " + commDriver);

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnPropagateGuarantees = new Button(container, SWT.CHECK);
		btnPropagateGuarantees.setSelection(true);
		btnPropagateGuarantees.setLayoutData(dataInfoField);

	}

	/**
	 * Creates the input text field for specifying the attestation agree property
	 * @param container
	 */
	private void createAgreePropertyField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Attestation AGREE contract");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtAgreeProperty = new Text(container, SWT.BORDER);
		txtAgreeProperty.setLayoutData(dataInfoField);
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
		implementationName = txtImplementationName.getText();
		implementationLanguage = txtImplementationLanguage.getText();
		cacheTimeout = txtCacheTimeout.getText();
		cacheSize = cboCacheSize.getText();
		for (Button b : btnDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				dispatchProtocol = b.getText();
				break;
			}
		}
		logPortType = null;
		for (int i = 0; i < btnLogPortType.size(); i++) {
			if (btnLogPortType.get(i).getSelection()) {
				logPortType = PortCategory.get(i);
				break;
			}
		}
		requirement = cboRequirement.getText();
		if (requirement.equals(NO_REQUIREMENT_SELECTED)) {
			requirement = "";
		} else if (!requirements.contains(requirement)) {
			Dialog.showError("Add Attestation Manager",
					"Attestation Manager requirement " + requirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}
		if (attestationManager == null) {
			agreeProperty = txtAgreeProperty.getText();
			propagateGuarantees = btnPropagateGuarantees.getSelection();
		}
		return true;
	}

	public String getImplementationName() {
		return implementationName;
	}

	public String getImplementationLanguage() {
		return implementationLanguage;
	}

	public String getCacheTimeout() {
		return cacheTimeout;
	}

	public String getCacheSize() {
		return cacheSize;
	}

	public String getDispatchProtocol() {
		return dispatchProtocol;
	}

	public PortCategory getLogPortType() {
		return logPortType;
	}

	public boolean getPropagateGuarantees() {
		return propagateGuarantees;
	}

	public String getRequirement() {
		return requirement;
	}

	public String getAgreeProperty() {
		return agreeProperty;
	}

}
