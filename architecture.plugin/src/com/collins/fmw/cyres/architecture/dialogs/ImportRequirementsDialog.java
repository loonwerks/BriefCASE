package com.collins.fmw.cyres.architecture.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.architecture.handlers.AddRequirement.CASE_Requirement;

public class ImportRequirementsDialog extends TitleAreaDialog {

	List<Button> btnReqs = new ArrayList<>();
	List<String> lblReqTexts = new ArrayList<>();
	List<Text> txtIDs = new ArrayList<>();
	List<String> lblComponents = new ArrayList<>();
	List<Button> btnAgreeProps = new ArrayList<>();
	List<Text> txtAgreeProps = new ArrayList<>();
	List<Text> txtRationales = new ArrayList<>();
	List<CASE_Requirement> existingRequirements = new ArrayList<>();
	List<CASE_Requirement> importRequirements = new ArrayList<>();
	List<CASE_Requirement> omitRequirements = new ArrayList<>();

	public ImportRequirementsDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Import Cybersecurity Requirements");
		setMessage(
				"Select the cybersecurity requirements to be imported into the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(6, false);
		container.setLayout(layout);

		// Add filter information fields
		createRequirementsSelectionField(container);

		return area;
	}

	private void createRequirementsSelectionField(Composite container) {

		Label lblHeaderField = new Label(container, SWT.NONE);
		FontData fontData = lblHeaderField.getFont().getFontData()[0];
		Font font = new Font(container.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblHeaderField.setFont(font);
		lblHeaderField.setText("Requirement");
		lblHeaderField = new Label(container, SWT.NONE);
		lblHeaderField.setText("ID");
		lblHeaderField.setFont(font);
		lblHeaderField = new Label(container, SWT.NONE);
		lblHeaderField.setText("Text");
		lblHeaderField.setFont(font);
		lblHeaderField = new Label(container, SWT.NONE);
		lblHeaderField.setText("Component");
		lblHeaderField.setFont(font);
		lblHeaderField = new Label(container, SWT.WRAP);
		lblHeaderField.setText("AGREE Property");
		lblHeaderField.setFont(font);
		lblHeaderField = new Label(container, SWT.WRAP);
		lblHeaderField.setText("Rationale for Omission");
		lblHeaderField.setFont(font);

		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Add requirements
		addRequirement("trusted_source",
				"The FlightPlanner shall only accept messages from a trusted GroundStation", "FlightPlanner",
				container);
		addRequirement("well_formed", "The FlightPlanner shall receive a well-formed command from the GroundStation",
				"FlightPlanner", container);
		addRequirement("not_hackable", "The WaypointManager shall never be hackable by anyone ever", "WaypointManager",
				container);

	}

	private void addRequirement(String reqName, String reqText, String compName, Composite container) {

		// If requirement has already been imported, ignore
		for (CASE_Requirement req : existingRequirements) {
			if (req.name.contentEquals(reqName) && req.component.contentEquals(compName)) {
				return;
			}
		}

		Button btnReq = new Button(container, SWT.CHECK);
		btnReq.setText(reqName);
		btnReq.setSelection(false);
		btnReq.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				enableRationaleFields();
			}
		});
		btnReqs.add(btnReq);

		Text txtID = new Text(container, SWT.BORDER);
		GridData reqIDInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtID.setLayoutData(reqIDInfoField);
		txtIDs.add(txtID);

		Label lblReqText = new Label(container, SWT.NONE);
		lblReqText.setText(reqText);
		lblReqTexts.add(reqText);

		Label lblCompName = new Label(container, SWT.NONE);
		lblCompName.setText(compName);
		lblComponents.add(compName);

//		Button btnAgree = new Button(container, SWT.CHECK);
//		btnAgree.setText("");
//		btnAgree.setSelection(false);
//		GridData agreePropInfoField = new GridData(SWT.CENTER, SWT.FILL, true, false);
//		btnAgree.setLayoutData(agreePropInfoField);
//		btnAgreeProps.add(btnAgree);

		Text txtAgree = new Text(container, SWT.BORDER);
		GridData agreeInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtAgree.setLayoutData(agreeInfoField);
		txtAgreeProps.add(txtAgree);

		Text txtRationale = new Text(container, SWT.BORDER);
		GridData dataInfoField = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtRationale.setLayoutData(dataInfoField);
		txtRationales.add(txtRationale);

		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	private void enableRationaleFields() {

		for (int i = 0; i < btnReqs.size(); i++) {
			txtRationales.get(i).setEnabled(!btnReqs.get(i).getSelection());
		}

	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private boolean saveInput() {

		importRequirements.clear();
		omitRequirements.clear();
		for (int i = 0; i < btnReqs.size(); i++) {
			if (btnReqs.get(i).getSelection()) {
				if (txtIDs.get(i).getText().isEmpty()) {
					Dialog.showError("Missing requirement ID", btnReqs.get(i).getText()
							+ " is missing a requirement ID. Requirement IDs must be assigned before requirements can be imported into model.");
					return false;
				}
//				importRequirements.add(new CASE_Requirement(btnReqs.get(i).getText(), txtIDs.get(i).getText(),
//						lblReqTexts.get(i), lblComponents.get(i), btnAgreeProps.get(i).getSelection(),
//						txtRationales.get(i).getText()));
				importRequirements.add(new CASE_Requirement(btnReqs.get(i).getText(), txtIDs.get(i).getText(),
						lblReqTexts.get(i), lblComponents.get(i), txtAgreeProps.get(i).getText(),
						txtRationales.get(i).getText()));
			} else {
//				omitRequirements.add(new CASE_Requirement(btnReqs.get(i).getText(), txtIDs.get(i).getText(),
//						lblReqTexts.get(i), lblComponents.get(i), btnAgreeProps.get(i).getSelection(),
//						txtRationales.get(i).getText()));
				omitRequirements.add(new CASE_Requirement(btnReqs.get(i).getText(), txtIDs.get(i).getText(),
						lblReqTexts.get(i), lblComponents.get(i), txtAgreeProps.get(i).getText(),
						txtRationales.get(i).getText()));
			}
		}
		return true;
	}

	@Override
	protected void okPressed() {
		if (saveInput()) {
			super.okPressed();
		}
	}

	public List<CASE_Requirement> getImportRequirements() {
		return importRequirements;
	}

	public List<CASE_Requirement> getOmitRequirements() {
		return omitRequirements;
	}

	public void setResoluteClauses(List<CASE_Requirement> resoluteClauses) {
		this.existingRequirements = resoluteClauses;
	}

}
