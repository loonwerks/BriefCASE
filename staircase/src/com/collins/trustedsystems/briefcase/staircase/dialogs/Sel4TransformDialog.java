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
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.dialogs.SubcomponentSelector.CheckStyle;
import com.collins.trustedsystems.briefcase.staircase.handlers.Sel4TransformHandler;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;

public class Sel4TransformDialog extends TitleAreaDialog {

	private Subcomponent subcomponent = null;

	private SubcomponentSelector subcomponentSelector = null;
	private Combo cboSel4Requirement;
	private String sel4Subcomponent = "";
	private String sel4Requirement = "";
	private List<String> requirements = new ArrayList<>();

	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public Sel4TransformDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("seL4 Transform");
		setMessage("Enter seL4 transform details for " + subcomponent.getName() + ".", IMessageProvider.NONE);
	}

	public void create(Subcomponent subcomponent) {
		this.subcomponent = subcomponent;
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

		// Add seL4 information fields
		createComponentSelectionField(container);
		createRequirementField(container);

		return area;
	}

	/**
	 * Creates the field for specifying the components to transform for seL4
	 * @param container
	 */
	private void createComponentSelectionField(Composite container) {
		// Only display this field if it's a system software component
		// containing multiple subcomponents
		if (this.subcomponent.getComponentType().getCategory() != ComponentCategory.SYSTEM
				|| !Sel4TransformHandler.isSoftwareSubcomponent(this.subcomponent)
				|| this.subcomponent.getComponentImplementation().getOwnedSubcomponents().size() <= 1) {
			return;
		}

		final Label lblComponentSelectionField = new Label(container, SWT.NONE);
		lblComponentSelectionField.setText("Component to transform for seL4");
		lblComponentSelectionField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		subcomponentSelector = new SubcomponentSelector(container, this.subcomponent, CheckStyle.SINGLE);

	}


	/**
	 * Creates the input field for selecting the resolute clause that drives
	 * the seL4 transform in this design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		final Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboSel4Requirement = new Combo(container, SWT.BORDER);
		cboSel4Requirement.setLayoutData(dataInfoField);
		cboSel4Requirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboSel4Requirement.add(r));
		cboSel4Requirement.setText(NO_REQUIREMENT_SELECTED);
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

		// Subcomponent transform for seL4
		if (subcomponentSelector == null) {
			sel4Subcomponent = this.subcomponent.getName();
		} else {
			List<String> selectedSubcomponents = subcomponentSelector.getContents();
			if (selectedSubcomponents.isEmpty()) {
				Dialog.showError("seL4 Transform", "No subcomponent has been selected to transform for seL4.");
				return false;
			} else {
				sel4Subcomponent = selectedSubcomponents.get(0);
			}
		}


		// Requirement
		sel4Requirement = cboSel4Requirement.getText();
		if (sel4Requirement.equals(NO_REQUIREMENT_SELECTED)) {
			sel4Requirement = "";
		} else if (!requirements.contains(sel4Requirement)) {
			Dialog.showError("seL4 Transform", "seL4 requirement " + sel4Requirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

		return true;
	}

	public String getSelectedSubcomponent() {
		return sel4Subcomponent;
	}

	public String getRequirement() {
		return sel4Requirement;
	}

}
