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
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.handlers.AddAttestationManagerHandler;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

/**
 * This class creates the Add Attestation wizard
 */
public class AddAttestationManagerDialog extends TitleAreaDialog {

	private ComponentImplementation context = null;
	private Text txtMgrComponentName;
	private Text txtGateComponentName;
	private Text txtMgrSubcomponentName;
	private Text txtGateSubcomponentName;
//	private Text txtMgrImplementationLanguage;
//	private Text txtGateImplementationLanguage;
	private Text txtCacheTimeout;
	private Combo cboCacheSize;
	private Text txtIdListDataType;
	private List<Button> btnDispatchProtocol = new ArrayList<>();
	private List<Button> btnLogPortType = new ArrayList<>();
	private Combo cboRequirement;
	private Button btnPropagateGuarantees;
	private Text txtAgreeProperty;
	private String attestationManagerComponentName;
	private String attestationGateComponentName;
	private String attestationManagerSubcomponentName;
	private String attestationGateSubcomponentName;
//	private String attestationManagerImplLanguage = "";
//	private String attestationGateImplLanguage = "";
	private long cacheTimeout = 0;
	private long cacheSize = 0;
	private String idListDataType = "";
	private String dispatchProtocol = "";
	private PortCategory logPortType = null;
	private String requirement;
	private boolean propagateGuarantees;
	private String commDriver = "";
	private List<String> requirements = new ArrayList<>();
	private String agreeProperty;
	private Subcomponent attestationManager = null;
	private Subcomponent attestationGate = null;

	private static final int MAX_CACHE_SIZE = 6;
	private static final int DEFAULT_CACHE_SIZE = 4;

//	private static final String DEFAULT_IMPL_LANGUAGE = "CakeML";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AddAttestationManagerDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Attestation");
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

	public void create(Subcomponent commDriver, List<String> requirements, Subcomponent attestationManager,
			Subcomponent attestationGate) {

		if (commDriver == null) {
			Dialog.showError("Add Attestation", "Unknown communication driver.");
			return;
		}

		this.context = commDriver.getComponentImplementation();
		this.commDriver = commDriver.getName();
		this.requirements = requirements;
		this.attestationManager = attestationManager;
		this.attestationGate = attestationGate;

		super.create();
		setTitle("Add Attestation to " + this.commDriver);
		setMessage("Enter Attestation details for adding attestation to " + this.commDriver
				+ ".  You may optionally leave these fields empty and manually edit the AADL attestation manager and gate components once they are added to the model.",
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
		createMgrComponentNameField(container);
		createMgrSubcomponentNameField(container);
		createGateComponentNameField(container);
		createGateSubcomponentNameField(container);
//		createMgrImplementationLanguageField(container);
//		createGateImplementationLanguageField(container);
		createCacheTimeoutField(container);
		createCacheSizeField(container);
		createIdListDataTypeField(container);
		createDispatchProtocolField(container);
		createLogPortField(container);
		createRequirementField(container);
		if (attestationManager == null) {
			createPropagateGuaranteesField(container);
			createAgreePropertyField(container);
		}

		return area;
	}

	private void createMgrComponentNameField(Composite container) {

		Label lblComponentNameField = new Label(container, SWT.NONE);
		lblComponentNameField.setText("Attestation Manager component name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtMgrComponentName = new Text(container, SWT.BORDER);
		txtMgrComponentName.setLayoutData(dataInfoField);
		if (attestationManager == null) {
			txtMgrComponentName
					.setText(ModelTransformUtils.getUniqueName(AddAttestationManagerHandler.AM_COMP_TYPE_NAME, true,
							AadlUtil.getContainingPackageSection(context).getOwnedClassifiers()));
		} else {
			txtMgrComponentName.setText(attestationManager.getComponentType().getName());
			txtMgrComponentName.setEnabled(false);
		}

	}

	private void createMgrSubcomponentNameField(Composite container) {

		Label lblSubcomponentNameField = new Label(container, SWT.NONE);
		lblSubcomponentNameField.setText("Attestation Manager subcomponent name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtMgrSubcomponentName = new Text(container, SWT.BORDER);
		txtMgrSubcomponentName.setLayoutData(dataInfoField);
		if (attestationManager == null) {
			txtMgrSubcomponentName.setText(ModelTransformUtils.getUniqueName(
					AddAttestationManagerHandler.AM_SUBCOMP_NAME, true, context.getOwnedSubcomponents()));
		} else {
			txtMgrSubcomponentName.setText(attestationManager.getName());
			txtMgrSubcomponentName.setEnabled(false);
		}

	}

//	private void createMgrImplementationLanguageField(Composite container) {
//
//		Label lblImplLangField = new Label(container, SWT.NONE);
//		lblImplLangField.setText("Attestation Manager implementation language");
//
//		GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = SWT.FILL;
//		txtMgrImplementationLanguage = new Text(container, SWT.BORDER);
//		txtMgrImplementationLanguage.setLayoutData(dataInfoField);
//		txtMgrImplementationLanguage.setText(DEFAULT_IMPL_LANGUAGE);
//		if (attestationManager != null) {
//			txtMgrImplementationLanguage.setEnabled(false);
//		}
//	}

	private void createGateComponentNameField(Composite container) {

		Label lblComponentNameField = new Label(container, SWT.NONE);
		lblComponentNameField.setText("Attestation Gate component name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtGateComponentName = new Text(container, SWT.BORDER);
		txtGateComponentName.setLayoutData(dataInfoField);
		if (attestationManager == null) {
			txtGateComponentName
					.setText(ModelTransformUtils.getUniqueName(AddAttestationManagerHandler.AG_COMP_TYPE_NAME, true,
							AadlUtil.getContainingPackageSection(context).getOwnedClassifiers()));
		} else {
			txtGateComponentName.setText(attestationGate.getComponentType().getName());
			txtGateComponentName.setEnabled(false);
		}

	}

	private void createGateSubcomponentNameField(Composite container) {

		Label lblSubcomponentNameField = new Label(container, SWT.NONE);
		lblSubcomponentNameField.setText("Attestation Gate subcomponent name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtGateSubcomponentName = new Text(container, SWT.BORDER);
		txtGateSubcomponentName.setLayoutData(dataInfoField);
		if (attestationGate == null) {
			txtGateSubcomponentName.setText(ModelTransformUtils.getUniqueName(
					AddAttestationManagerHandler.AG_SUBCOMP_NAME, true, context.getOwnedSubcomponents()));
		} else {
			txtGateSubcomponentName.setText(attestationGate.getName());
			txtGateSubcomponentName.setEnabled(false);
		}

	}

//	private void createGateImplementationLanguageField(Composite container) {
//
//		Label lblImplLangField = new Label(container, SWT.NONE);
//		lblImplLangField.setText("Attestation Gate implementation language");
//
//		GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = SWT.FILL;
//		txtGateImplementationLanguage = new Text(container, SWT.BORDER);
//		txtGateImplementationLanguage.setLayoutData(dataInfoField);
//		txtGateImplementationLanguage.setText(DEFAULT_IMPL_LANGUAGE);
//		if (attestationGate != null) {
//			txtGateImplementationLanguage.setEnabled(false);
//		}
//	}

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
			Property prop = Aadl2GlobalScopeUtil.get(ci, Aadl2Package.eINSTANCE.getProperty(),
					CasePropertyUtils.CASE_PROPSET_NAME + "::" + CasePropertyUtils.CACHE_TIMEOUT);
			List<? extends PropertyExpression> propVals = ci.getPropertyValueList(prop);

			if (propVals != null) {
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
			Property prop = Aadl2GlobalScopeUtil.get(ci, Aadl2Package.eINSTANCE.getProperty(),
					CasePropertyUtils.CASE_PROPSET_NAME + "::" + CasePropertyUtils.CACHE_SIZE);
			List<? extends PropertyExpression> propVals = ci.getPropertyValueList(prop);
			if (propVals != null) {
				// There should be only one property value
				PropertyExpression expr = propVals.get(0);
				if (expr instanceof IntegerLiteral) {
					cboCacheSize.setText(Long.toString(((IntegerLiteral) expr).getValue()));
				}
			}
			cboCacheSize.setEnabled(false);
		}

	}

	private void createIdListDataTypeField(Composite container) {

		Label lblIdListDataTypeField = new Label(container, SWT.NONE);
		lblIdListDataTypeField.setText("ID list data type");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtIdListDataType = new Text(container, SWT.BORDER);
		txtIdListDataType.setLayoutData(dataInfoField);

		if (attestationManager != null) {

			ComponentType ct = attestationManager.getComponentType();
			for (Feature f : ct.getOwnedFeatures()) {
				if (f.getName().equalsIgnoreCase(AddAttestationManagerHandler.AM_PORT_TRUSTED_IDS_NAME)) {
					String dataType = "";
					if (f instanceof EventDataPort) {
						EventDataPort port = (EventDataPort) f;
						dataType = port.getDataFeatureClassifier().getQualifiedName();
					} else if (f instanceof DataPort) {
						DataPort port = (DataPort) f;
						dataType = port.getDataFeatureClassifier().getQualifiedName();
					}
					txtIdListDataType.setText(dataType);
				}
			}

			txtIdListDataType.setEnabled(false);
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
		List<Classifier> componentsInPackage = AadlUtil.getContainingPackageSection(context).getOwnedClassifiers();

		// Attestation Manager Component Name
		if (!txtMgrComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtMgrComponentName.getText())) {
			Dialog.showError("Add Attestation", "Attestation Manager component name " + txtMgrComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (attestationManager == null
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtMgrComponentName.getText()) != null) {
			Dialog.showError("Add Attestation", "Component " + txtMgrComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtMgrComponentName.setText(
					ModelTransformUtils.getUniqueName(txtMgrComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			attestationManagerComponentName = txtMgrComponentName.getText();
		}

		// Attestation Gate Component Name
		if (!txtGateComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtGateComponentName.getText())) {
			Dialog.showError("Add Attestation", "Attestation Gate component name " + txtGateComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (attestationManager == null
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtGateComponentName.getText()) != null) {
			Dialog.showError("Add Attestation", "Component " + txtGateComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtGateComponentName.setText(
					ModelTransformUtils.getUniqueName(txtGateComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			attestationGateComponentName = txtGateComponentName.getText();
		}

		// Attestation Manager Subcomponent Instance Name
		if (!txtMgrSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtMgrSubcomponentName.getText())) {
			Dialog.showError("Add Attestation",
					"Attestation Manager subcomponent instance name " + txtMgrSubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (attestationManager == null && AadlUtil.findNamedElementInList(context.getOwnedSubcomponents(),
				txtMgrSubcomponentName.getText()) != null) {
			Dialog.showError("Add Attestation", "Subcomponent " + txtMgrSubcomponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtMgrSubcomponentName.setText(ModelTransformUtils.getUniqueName(txtMgrSubcomponentName.getText(), true,
					context.getOwnedSubcomponents()));
			return false;
		} else {
			attestationManagerSubcomponentName = txtMgrSubcomponentName.getText();
		}
//		attestationManagerSubcomponentName = txtMgrSubcomponentName.getText();

//		attestationManagerImplLanguage = txtMgrImplementationLanguage.getText();

		// Attestation Gate Subcomponent Instance Name
		if (!txtGateSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtGateSubcomponentName.getText())) {
			Dialog.showError("Add Attestation",
					"Attestation Gate subcomponent instance name " + txtGateSubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (attestationManager == null && AadlUtil.findNamedElementInList(context.getOwnedSubcomponents(),
				txtGateSubcomponentName.getText()) != null) {
			Dialog.showError("Add Attestation", "Subcomponent " + txtGateSubcomponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtGateSubcomponentName.setText(ModelTransformUtils.getUniqueName(txtGateSubcomponentName.getText(), true,
					context.getOwnedSubcomponents()));
			return false;
		} else {
			attestationGateSubcomponentName = txtGateSubcomponentName.getText();
		}
//		attestationGateSubcomponentName = txtGateSubcomponentName.getText();

//		attestationGateImplLanguage = txtGateImplementationLanguage.getText();

		// Timeout
		try {
			cacheTimeout = Long.parseLong(txtCacheTimeout.getText());
		} catch (NumberFormatException e) {
			Dialog.showError("Add Attestation", "Value of Cache Timeout must be an integer.");
			return false;
		}

		// Cache Size
		try {
			cacheSize = Long.parseLong(cboCacheSize.getText());
		} catch (NumberFormatException e) {
			Dialog.showError("Add Attestation", "Value of Cache Size must be an integer.");
			return false;
		}

		// ID List Data Type
		if (!txtIdListDataType.getText().isEmpty() && !txtIdListDataType.getText().contains("::")) {
			Dialog.showError("Add Attestation", "ID list data type must be a qualified name.");
			return false;
		}
		idListDataType = txtIdListDataType.getText();

		// Dispatch Protocol and Period
		for (Button b : btnDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				dispatchProtocol = b.getText();
				break;
			}
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
		requirement = cboRequirement.getText();
		if (requirement.equals(NO_REQUIREMENT_SELECTED)) {
			requirement = "";
		} else if (!requirements.contains(requirement)) {
			Dialog.showError("Add Attestation", "Attestation requirement " + requirement
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

//	public String getAttestationManagerImplLanguage() {
//		return attestationManagerImplLanguage;
//	}

//	public String getAttestationGateImplLanguage() {
//		return attestationGateImplLanguage;
//	}

	public long getCacheTimeout() {
		return cacheTimeout;
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public String getIdListDataType() {
		return idListDataType;
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
