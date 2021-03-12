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
import org.osate.aadl2.ComponentImplementation;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;

public class ProxyTransformDialog extends TitleAreaDialog {

	private ComponentImplementation context;

	private Combo cboProxyRequirement;

	private String proxyRequirement = "";

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

		// Add proxy information fields
		createRequirementField(container);

		return area;
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

	public String getRequirement() {
		return proxyRequirement;
	}

}
