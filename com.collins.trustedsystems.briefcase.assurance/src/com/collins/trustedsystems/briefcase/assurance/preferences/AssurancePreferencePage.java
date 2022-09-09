package com.collins.trustedsystems.briefcase.assurance.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.collins.trustedsystems.briefcase.Activator;

public class AssurancePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public AssurancePreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {

//		label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
//		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 5));

		Label label = new Label(getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		final FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
		final Font boldFont = boldDescriptor.createFont(label.getDisplay());
		label.setFont(boldFont);
		label.addDisposeListener(e -> boldFont.dispose());
		label.setText("Review Documents");

		final FileFieldEditor reqReviewFileFieldEditor = new FileFieldEditor(
				AssurancePreferenceConstants.REQUIREMENTS_REVIEW_FILENAME, "Requirements review filename:", true,
				getFieldEditorParent()) {

			@Override
			protected String changePressed() {

				FileDialog dlgOpen = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
				dlgOpen.setText("Requirements review file");
				if (!getTextControl().getText().isEmpty()) {
					dlgOpen.setFileName(getTextControl().getText());
				} else {
					dlgOpen.setFileName("");
				}
				dlgOpen.setFilterExtensions(new String[] { "*.*" });
				String fileName = dlgOpen.open();
				if (fileName == null) {
					return null;
				} else {
					fileName = fileName.trim();
				}

				return fileName;
			}

			@Override
			protected boolean checkState() {
				// Don't want to enforce proper path/filenaming
				clearErrorMessage();
				return true;
			}
		};
		addField(reqReviewFileFieldEditor);

		final FileFieldEditor agreeReviewFileFieldEditor = new FileFieldEditor(
				AssurancePreferenceConstants.AGREE_REVIEW_FILENAME, "Formal verification review filename:", true,
				getFieldEditorParent()) {

			@Override
			protected String changePressed() {

				FileDialog dlgOpen = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
				dlgOpen.setText("Formal verification review file");
				if (!getTextControl().getText().isEmpty()) {
					dlgOpen.setFileName(getTextControl().getText());
				} else {
					dlgOpen.setFileName("");
				}
				dlgOpen.setFilterExtensions(new String[] { "*.*" });
				String fileName = dlgOpen.open();
				if (fileName == null) {
					return null;
				} else {
					fileName = fileName.trim();
				}

				return fileName;
			}

			@Override
			protected boolean checkState() {
				// Don't want to enforce proper path/filenaming
				clearErrorMessage();
				return true;
			}
		};
		addField(agreeReviewFileFieldEditor);

	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
//		setDescription("BriefCASE Assurance Settings");
	}

}
