package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.handlers.AddVirtualizationHandler;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

public class AddVirtualizationDialog extends TitleAreaDialog {

	private ComponentImplementation context;
	private Text txtVirtualProcessorComponentName;
	private Text txtVirtualProcessorSubcomponentName;
	private Text txtVirtualMachineOS;
	private SubcomponentSelector subcomponentSelector = null;
	private Combo cboVirtualizationRequirement;

	private String virtualProcessorComponentName = "";
	private String virtualProcessorSubcomponentName = "";
	private String virtualMachineOS = "";
	private List<String> subcomponents = new ArrayList<>();
	private String virtualizationRequirement = "";

	private Subcomponent subcomponent = null;
	private List<String> requirements = new ArrayList<>();

	private static final String DEFAULT_OS = "Linux";
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AddVirtualizationDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add Virtualization");
		setMessage(
				"Enter virtualization details.  You may optionally leave these fields empty and manually edit the AADL virtualization component once it is added to the model.",
				IMessageProvider.NONE);
	}

	public void create(Subcomponent subcomponent) {
		this.context = subcomponent.getContainingComponentImpl();
		this.subcomponent = subcomponent;
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
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		// Add virtualization information fields
		createVirtualProcessorComponentNameField(container);
		createVirtualProcessorSubcomponentNameField(container);
		createOSField(container);
		createComponentSelectionField(container);
		createRequirementField(container);

		return area;
	}

	/**
	 * Creates the input text field for specifying the virtual processor component name
	 * @param container
	 */
	private void createVirtualProcessorComponentNameField(Composite container) {
		Label lblVirtualProcessorNameField = new Label(container, SWT.NONE);
		lblVirtualProcessorNameField.setText("Virtual Processor component name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtVirtualProcessorComponentName = new Text(container, SWT.BORDER);
		txtVirtualProcessorComponentName.setLayoutData(dataInfoField);
		txtVirtualProcessorComponentName.setText(AddVirtualizationHandler.VIRTUAL_PROCESSOR_COMP_TYPE_NAME);
	}

	/**
	 * Creates the input text field for specifying the virtual processor subcomponent instance name
	 * @param container
	 */
	private void createVirtualProcessorSubcomponentNameField(Composite container) {
		Label lblVirtualProcessorNameField = new Label(container, SWT.NONE);
		lblVirtualProcessorNameField.setText("Virtual Processor subcomponent name");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtVirtualProcessorSubcomponentName = new Text(container, SWT.BORDER);
		txtVirtualProcessorSubcomponentName.setLayoutData(dataInfoField);
		txtVirtualProcessorSubcomponentName.setText(AddVirtualizationHandler.VIRTUAL_PROCESSOR_SUBCOMP_NAME);
	}

	/**
	 * Creates the input text field for specifying the OS on the VM
	 * @param container
	 */
	private void createOSField(Composite container) {
		Label lblOSField = new Label(container, SWT.NONE);
		lblOSField.setText("Virtual machine OS");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtVirtualMachineOS = new Text(container, SWT.BORDER);
		txtVirtualMachineOS.setLayoutData(dataInfoField);
		txtVirtualMachineOS.setText(DEFAULT_OS);
	}


	/**
	 * Creates the field for specifying the components to virtualize
	 * @param container
	 */
	private void createComponentSelectionField(Composite container) {
		// Don't display this field if
		// Component contains one or no subcomponents OR component is a process
		if (this.subcomponent.getComponentImplementation().getOwnedSubcomponents() == null
				|| this.subcomponent.getComponentImplementation().getOwnedSubcomponents().size() <= 1
				|| this.subcomponent.getComponentType().getCategory() == ComponentCategory.PROCESS) {
			return;
		}

		Label lblComponentSelectionField = new Label(container, SWT.NONE);
		lblComponentSelectionField.setText("Components in VM");
		lblComponentSelectionField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		subcomponentSelector = new SubcomponentSelector(container, this.subcomponent);

	}


	/**
	 * Creates the input field for selecting the resolute clause that drives
	 * the addition of this virtualization to the design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboVirtualizationRequirement = new Combo(container, SWT.BORDER);
		cboVirtualizationRequirement.setLayoutData(dataInfoField);
		cboVirtualizationRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboVirtualizationRequirement.add(r));
		cboVirtualizationRequirement.setText(NO_REQUIREMENT_SELECTED);
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

		List<Classifier> componentsInPackage = AadlUtil.getContainingPackageSection(context).getOwnedClassifiers();

		// VirtualProcessor Component Name
		if (!txtVirtualProcessorComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtVirtualProcessorComponentName.getText())) {
			Dialog.showError("Add Virtualization",
					"VirtualProcessor component name " + txtVirtualProcessorComponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (AadlUtil.findNamedElementInList(componentsInPackage,
				txtVirtualProcessorComponentName.getText()) != null) {
			Dialog.showError("Add Virtualization", "Component " + txtVirtualProcessorComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtVirtualProcessorComponentName.setText(ModelTransformUtils
					.getUniqueName(txtVirtualProcessorComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			virtualProcessorComponentName = txtVirtualProcessorComponentName.getText();
		}

		// VirtualProcessor Subcomponent Instance Name
		if (!txtVirtualProcessorSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtVirtualProcessorSubcomponentName.getText())) {
			Dialog.showError("Add VirtualProcessor",
					"Virtual Processor subcomponent instance name " + txtVirtualProcessorSubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (AadlUtil.findNamedElementInList(context.getOwnedSubcomponents(),
				txtVirtualProcessorSubcomponentName.getText()) != null) {
			Dialog.showError("Add Virtualization", "Subcomponent " + txtVirtualProcessorSubcomponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtVirtualProcessorSubcomponentName.setText(ModelTransformUtils.getUniqueName(
					txtVirtualProcessorSubcomponentName.getText(), true, context.getOwnedSubcomponents()));
			return false;
		} else {
			virtualProcessorSubcomponentName = txtVirtualProcessorSubcomponentName.getText();
		}
//		virtualProcessorName = txtVirtualProcessorName.getText();

		// OS
		virtualMachineOS = txtVirtualMachineOS.getText();

		// Subcomponents to virtualize
		if (subcomponentSelector == null) {
			subcomponents.add(this.subcomponent.getName());
		} else {
			subcomponents = subcomponentSelector.getContents();
			if (subcomponents.isEmpty()) {
				Dialog.showError("Add Virtualization", "No subcomponents have been selected to virtualize.");
				return false;
			}
		}

		// Requirement
		virtualizationRequirement = cboVirtualizationRequirement.getText();
		if (virtualizationRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			virtualizationRequirement = "";
		} else if (!requirements.contains(virtualizationRequirement)) {
			Dialog.showError("Add Virtualization", "Virtualization requirement " + virtualizationRequirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

		return true;
	}

	public String getVirtualProcessorComponentName() {
		return virtualProcessorComponentName;
	}

	public String getVirtualProcessorSubcomponentName() {
		return virtualProcessorSubcomponentName;
	}

	public String getVirtualMachineOS() {
		return virtualMachineOS;
	}

	public List<String> getVirtualizationComponents() {
		return subcomponents;
	}

	public String getRequirement() {
		return virtualizationRequirement;
	}

//	public void setSelectedSubcomponent(Subcomponent subcomponent) {
//		this.subcomponent = subcomponent;
//	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

}
