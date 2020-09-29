package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.List;

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
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.ThreadGroupImplementation;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;
import org.osate.xtext.aadl2.properties.util.TimingProperties;

import com.collins.trustedsystems.briefcase.staircase.handlers.AddFilterHandler;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

/**
 * This class creates the Add Filter wizard
 */
public class AddFilterDialog extends TitleAreaDialog {

	private ComponentImplementation context = null;
	private Subcomponent compoundFilter = null;
	private Text txtFilterComponentName;
	private Text txtFilterSubcomponentName;
//	private Text txtFilterImplementationLanguage;
	private List<Button> btnDispatchProtocol = new ArrayList<>();
	private Label lblPeriodField;
	private Text txtPeriod;
	private Text txtInputPortName;
	private Text txtOutputPortName;
	private List<Button> btnLogPortType = new ArrayList<>();
	private Combo cboFilterRequirement;
	private Text txtAgreeProperty;
	private List<Button> btnPropagateGuarantees = new ArrayList<>();
//	private String filterImplementationLanguage = "";
	private String filterComponentName = "";
	private String filterSubcomponentName = "";
	private String filterDispatchProtocol = "";
	private String filterPeriod = "";
	private String filterInputPortName = "";
	private String filterOutputPortName = "";
	private PortCategory logPortType = null;
	private String filterRequirement = "";
	private String agreeProperty = "";
	private String sourceName = "";
	private List<String> sourceGuarantees = new ArrayList<>();
	private List<String> propagateGuarantees = new ArrayList<>();
	private List<String> requirements = new ArrayList<>();

//	private static final String DEFAULT_IMPL_LANGUAGE = "CakeML";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AddFilterDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Filter");
		setMessage(
				"Enter Filter details.  You may optionally leave these fields empty and manually edit the AADL filter component once it is added to the model.",
				IMessageProvider.NONE);
	}

	public void create(ComponentImplementation context) {
		this.context = context;
		create();
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
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Add filter information fields
		createFilterComponentNameField(container);
		createFilterSubcomponentNameField(container);
//		createImplementationLanguageField(container);
		// Only display dispatch protocol if filter is a thread
		if (context instanceof ProcessImplementation || context instanceof ThreadGroupImplementation) {
			createDispatchProtocolField(container);
		}
		createPortNamesField(container);
		createLogPortField(container);

		createRequirementField(container);
		if (compoundFilter == null) {
			// Don't show the propagate guarantees field if we're adding a spec to an existing filter.
			// Too complicated to figure out at this time
			createGuaranteeSelectionField(container);
		}
		createAgreeField(container);

		// set focus
		parent.forceFocus();

		return area;
	}

	/**
	 * Creates the input text field for specifying the filter component name
	 * @param container
	 */
	private void createFilterComponentNameField(Composite container) {
		Label lblFilterCompNameField = new Label(container, SWT.NONE);
		lblFilterCompNameField.setText("Filter component name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtFilterComponentName = new Text(container, SWT.BORDER);
		txtFilterComponentName.setLayoutData(dataInfoField);
		if (compoundFilter == null) {
			txtFilterComponentName.setText(ModelTransformUtils.getUniqueName(AddFilterHandler.FILTER_COMP_TYPE_NAME,
					true, AadlUtil.getContainingPackageSection(context).getOwnedClassifiers()));
		} else {
			txtFilterComponentName.setText(compoundFilter.getComponentType().getName());
			txtFilterComponentName.setEnabled(false);
		}
	}

	/**
	 * Creates the input text field for specifying the filter implementation name
	 * @param container
	 */
	private void createFilterSubcomponentNameField(Composite container) {
		Label lblFilterSubcomponentNameField = new Label(container, SWT.NONE);
		lblFilterSubcomponentNameField.setText("Filter subcomponent instance name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtFilterSubcomponentName = new Text(container, SWT.BORDER);
		txtFilterSubcomponentName.setLayoutData(dataInfoField);
		if (compoundFilter == null) {
			txtFilterSubcomponentName.setText(ModelTransformUtils.getUniqueName(AddFilterHandler.FILTER_SUBCOMP_NAME,
					true, context.getOwnedSubcomponents()));
		} else {
			txtFilterSubcomponentName.setText(compoundFilter.getName());
			txtFilterSubcomponentName.setEnabled(false);
		}
	}

//	/**
//	 * Creates the input text field for specifying the filter implementation language
//	 * @param container
//	 */
//	private void createImplementationLanguageField(Composite container) {
//		Label lblImplLangField = new Label(container, SWT.NONE);
//		lblImplLangField.setText("Filter implementation language");
//
//		GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = SWT.FILL;
//		txtFilterImplementationLanguage = new Text(container, SWT.BORDER);
//		txtFilterImplementationLanguage.setLayoutData(dataInfoField);
//		if (compoundFilter != null) {
//			ComponentImplementation ci = compoundFilter.getComponentImplementation();
//			EList<PropertyExpression> propVals = ci.getPropertyValues(CaseUtils.CASE_PROPSET_NAME, "COMP_IMPL");
//			if (!propVals.isEmpty()) {
//				// There should be only one property value
//				PropertyExpression expr = propVals.get(0);
//				if (expr instanceof StringLiteral) {
//					txtFilterImplementationLanguage.setText(((StringLiteral) expr).getValue());
//				}
//			}
//			txtFilterImplementationLanguage.setEnabled(false);
//		} else {
//			txtFilterImplementationLanguage.setText(DEFAULT_IMPL_LANGUAGE);
//		}
//	}

	/**
	 * Creates the input field for selecting the dispatch protocol
	 * @param container
	 */
	private void createDispatchProtocolField(Composite container) {
		Label lblDispatchProtocolField = new Label(container, SWT.NONE);
		lblDispatchProtocolField.setText("Dispatch protocol");
		lblDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		// Create a group to contain the protocol options
		Group protocolGroup = new Group(container, SWT.NONE);
		protocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		protocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnDispatchProtocol.clear();

		Button btnNoProtocol = new Button(protocolGroup, SWT.RADIO);
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

		Button btnPeriodic = new Button(protocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		Button btnSporadic = new Button(protocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		lblPeriodField = new Label(container, SWT.NONE);
		lblPeriodField.setText("Period");
		lblPeriodField.setEnabled(false);

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtPeriod = new Text(container, SWT.BORDER);
		txtPeriod.setLayoutData(dataInfoField);
		txtPeriod.setEnabled(false);

		if (compoundFilter != null) {
			Property prop = GetProperties.lookupPropertyDefinition(compoundFilter, ThreadProperties._NAME,
					ThreadProperties.DISPATCH_PROTOCOL);
			List<? extends PropertyExpression> protocol = compoundFilter.getPropertyValueList(prop);
			if (!protocol.isEmpty()) {
				NamedValue nv = (NamedValue) protocol.get(0);
				EnumerationLiteral el = (EnumerationLiteral) nv.getNamedValue();
				String dispatchProtocol = el.getName();
				if (dispatchProtocol.equalsIgnoreCase("Periodic")) {
					btnNoProtocol.setSelection(false);
					btnPeriodic.setSelection(true);
				} else if (dispatchProtocol.equalsIgnoreCase("Sporadic")) {
					btnNoProtocol.setSelection(false);
					btnSporadic.setSelection(true);
				} else {
					btnNoProtocol.setSelection(true);
				}
			}

			btnNoProtocol.setEnabled(false);
			btnPeriodic.setEnabled(false);
			btnSporadic.setEnabled(false);

			prop = GetProperties.lookupPropertyDefinition(compoundFilter, TimingProperties._NAME,
					TimingProperties.PERIOD);
			List<? extends PropertyExpression> periodVals = compoundFilter.getPropertyValueList(prop);
			if (!periodVals.isEmpty()) {
				String period = "";
				if (periodVals.get(0) instanceof IntegerLiteral) {
					IntegerLiteral periodVal = (IntegerLiteral) periodVals.get(0);
					period = Long.toString(periodVal.getValue());
					if (periodVal.getUnit() != null) {
						period += periodVal.getUnit().getName();
					}
				}
				txtPeriod.setText(period);
			}
			lblPeriodField.setEnabled(false);
			txtPeriod.setEnabled(false);
		}

		btnDispatchProtocol.add(btnNoProtocol);
		btnDispatchProtocol.add(btnPeriodic);
		btnDispatchProtocol.add(btnSporadic);

	}

	/**
	 * Creates the input field for specifying the names of the filter input and output ports
	 * @param container
	 */
	private void createPortNamesField(Composite container) {
		Label lblPortNamesField = new Label(container, SWT.NONE);
		lblPortNamesField.setText("Filter Port Names");
		lblPortNamesField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		Composite portNames = new Composite(container, SWT.BORDER);
		portNames.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		portNames.setLayout(new GridLayout(2, true));

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;

		Label lblInputPortName = new Label(portNames, SWT.NONE);
		lblInputPortName.setText("Input port name");
		lblInputPortName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		txtInputPortName = new Text(portNames, SWT.BORDER);
		txtInputPortName.setLayoutData(dataInfoField);

		Label lblOutputPortName = new Label(portNames, SWT.NONE);
		lblOutputPortName.setText("Output port name");
		lblOutputPortName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		txtOutputPortName = new Text(portNames, SWT.BORDER);
		txtOutputPortName.setLayoutData(dataInfoField);

		if (compoundFilter == null) {
			txtInputPortName.setText(AddFilterHandler.FILTER_PORT_IN_NAME);
			txtOutputPortName.setText(AddFilterHandler.FILTER_PORT_OUT_NAME);
		} else {
			for (Feature f : compoundFilter.getComponentType().getOwnedFeatures()) {
				if (f instanceof Port) {
					Port p = (Port) f;
					if (p.isIn()) {
						txtInputPortName.setText(p.getName());
					} else if (p.isOut()) {
						txtOutputPortName.setText(p.getName());
					}
				}
			}
			lblInputPortName.setEnabled(false);
			lblOutputPortName.setEnabled(false);
			txtInputPortName.setEnabled(false);
			txtOutputPortName.setEnabled(false);
		}

	}

	/**
	 * Creates the input field for specifying if the filter should contain
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

		if (compoundFilter != null) {
			ComponentType ct = compoundFilter.getComponentType();
			for (Feature f : ct.getOwnedFeatures()) {
				if (f.getName().equalsIgnoreCase(AddFilterHandler.FILTER_LOG_PORT_NAME)) {
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
		Label lblRequirementField = new Label(container, SWT.NONE);
		lblRequirementField.setText("Requirement");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboFilterRequirement = new Combo(container, SWT.BORDER);
		cboFilterRequirement.setLayoutData(dataInfoField);
		cboFilterRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboFilterRequirement.add(r));
		cboFilterRequirement.setText(NO_REQUIREMENT_SELECTED);

	}

	/**
	 * Creates the input text field for specifying the filter agree property
	 * @param container
	 */
	private void createAgreeField(Composite container) {
		Label lblAgreeField = new Label(container, SWT.NONE);
		lblAgreeField.setText("Filter AGREE contract");

		GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtAgreeProperty = new Text(container, SWT.BORDER);
		txtAgreeProperty.setLayoutData(dataInfoField);

	}

	/**
	 * Creates the input text field for specifying the guarantees to propagate
	 * @param container
	 */
	private void createGuaranteeSelectionField(Composite container) {

		// Only create this field if there are guarantees in the source
		// component to propagate
		if (sourceGuarantees.size() > 0) {

			Label lblSelectionField = new Label(container, SWT.NONE);
			lblSelectionField.setText("Preserve Guarantees from " + sourceName);
			lblSelectionField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			GridData selectionFieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);

			Composite selectionField = new Composite(container, SWT.BORDER);
			GridLayout layout = new GridLayout(1, true);
			selectionField.setLayout(layout);
			selectionField.setLayoutData(selectionFieldLayoutData);

			btnPropagateGuarantees.clear();
			for (String guarantee : sourceGuarantees) {
				Button selectGuarantee = new Button(selectionField, SWT.CHECK);
				String formattedGuarantee = guarantee.trim();
				// Parse the requirement ID (could be empty)
				formattedGuarantee = formattedGuarantee
						.substring("guarantee ".length(), formattedGuarantee.indexOf("\"")).trim();
				if (formattedGuarantee.isEmpty()) {
//					formattedGuarantee = formattedGuarantee
//						.substring(formattedGuarantee.indexOf("\"") + 1, formattedGuarantee.lastIndexOf("\""));
					formattedGuarantee = "<unnamed guarantee>";
				}
				selectGuarantee.setToolTipText(guarantee.trim());
				selectGuarantee.setText(formattedGuarantee.trim());
				selectGuarantee.setSelection(true);
				selectGuarantee.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

				btnPropagateGuarantees.add(selectGuarantee);
			}

		}
	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 */
	private boolean saveInput() {
		List<Classifier> componentsInPackage = AadlUtil.getContainingPackageSection(context).getOwnedClassifiers();

		// Filter Component Name
		if (!txtFilterComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtFilterComponentName.getText())) {
			Dialog.showError("Add Filter", "Filter component name " + txtFilterComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (compoundFilter == null
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtFilterComponentName.getText()) != null) {
			Dialog.showError("Add Filter", "Component " + txtFilterComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtFilterComponentName.setText(
					ModelTransformUtils.getUniqueName(txtFilterComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			filterComponentName = txtFilterComponentName.getText();
		}

//		filterImplementationLanguage = txtFilterImplementationLanguage.getText();

		// Filter Subcomponent Instance Name
		if (!txtFilterSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtFilterSubcomponentName.getText())) {
			Dialog.showError("Add Filter", "Filter subcomponent instance name " + txtFilterSubcomponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (compoundFilter == null && AadlUtil.findNamedElementInList(context.getOwnedSubcomponents(),
				txtFilterSubcomponentName.getText()) != null) {
			Dialog.showError("Add Filter", "Subcomponent " + txtFilterSubcomponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtFilterSubcomponentName.setText(ModelTransformUtils.getUniqueName(txtFilterSubcomponentName.getText(),
					true, context.getOwnedSubcomponents()));
			return false;
		} else {
			filterSubcomponentName = txtFilterSubcomponentName.getText();
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
					Dialog.showError("Add Filter", "Filter period " + txtPeriod.getText()
							+ " is malformed. See the AADL definition of Period in Timing_Properties.aadl.");
					return false;
				}
				break;
			}
		}

		// Input Port Name
		if (txtInputPortName.getText().isEmpty() || ModelTransformUtils.isValidName(txtInputPortName.getText())) {
			filterInputPortName = txtInputPortName.getText();
		} else {
			Dialog.showError("Add Filter", "Filter input port name " + txtInputPortName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		}

		// Output Port Name
		if (txtOutputPortName.getText().isEmpty() || ModelTransformUtils.isValidName(txtOutputPortName.getText())) {
			filterOutputPortName = txtOutputPortName.getText();
		} else {
			Dialog.showError("Add Filter", "Filter output port name " + txtOutputPortName.getText()
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
			Dialog.showError("Add Filter",
					"Filter requirement " + filterRequirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

		// AGREE Property
		agreeProperty = txtAgreeProperty.getText();

		// Propagate Guarantees
		propagateGuarantees.clear();
		for (int i = 0; i < btnPropagateGuarantees.size(); i++) {
			if (btnPropagateGuarantees.get(i).getSelection()) {

				// TODO: Maybe look into passing actual guarantees instead of strings, that way we can parse/unparse it
//				AgreeAnnexParser parser = new AgreeAnnexParser();
//				NamedSpecStatement nss = parser.parseNamedSpecStatement(strSourceGuarantees.get(i));
//				AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
//				String expr = unparser.unparseExpr(nss.getExpr(), "").trim();
//				String desc = nss.getStr().trim();
//				String id = nss.getName();
				// Parse the guarantee (for now do it the old fashioned way)
				String guarantee = sourceGuarantees.get(i);
				String expr = guarantee.substring(guarantee.lastIndexOf(":") + 1, guarantee.lastIndexOf(";")).trim();
				String desc = guarantee.substring(guarantee.indexOf("\""), guarantee.lastIndexOf("\"") + 1).trim();
				String id = guarantee.substring(guarantee.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
						guarantee.indexOf("\"")).trim();

				// If guarantee has an ID, append a suffix to maintain ID uniqueness
				if (id.length() > 0) {
					id = id.concat("_Filter");
				} else {
					id = "Filter";
				}
				guarantee = "guarantee " + id + " " + desc + " : " + expr + ";";
				propagateGuarantees.add(guarantee);
			}
		}
		return true;
	}

	@Override
	protected void okPressed() {
		if (!saveInput()) {
			return;
		}
		super.okPressed();
	}

	public String getFilterComponentName() {
		return filterComponentName;
	}

	public String getFilterSubcomponentName() {
		return filterSubcomponentName;
	}

//	public String getFilterImplementationLanguage() {
//		return filterImplementationLanguage;
//	}

	public String getDispatchProtocol() {
		return filterDispatchProtocol;
	}

	public String getPeriod() {
		return filterPeriod;
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

	public String getAgreeProperty() {
		return agreeProperty;
	}

	public List<String> getGuaranteeList() {
		return propagateGuarantees;
	}

	public String getRequirement() {
		return filterRequirement;
	}

	public void setGuaranteeList(String sourceName, List<String> guarantees) {
		this.sourceName = sourceName;
		this.sourceGuarantees = guarantees;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

	public void createCompoundFilter(Subcomponent comp) {
		this.compoundFilter = comp;
	}

}