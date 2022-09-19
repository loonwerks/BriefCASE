package com.collins.trustedsystems.briefcase.preferences;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.Activator;

public class BriefcasePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor splatLogFieldEditor = null;
	private FileFieldEditor splatLogFileFieldEditor = null;

	public BriefcasePreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {

		Label label = new Label(getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		final FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
		final Font boldFont = boldDescriptor.createFont(label.getDisplay());
		label.setFont(boldFont);
		label.addDisposeListener(e -> boldFont.dispose());

		label.setText("SPLAT");

		// Output folder name
//		addField(new DirectoryFieldEditor(BriefcasePreferenceConstants.SPLAT_OUTPUT_FOLDER, "SPLAT output folder name",
//				getFieldEditorParent()));
//		addField(new StringFieldEditor(BriefcasePreferenceConstants.SPLAT_OUTPUT_FOLDER, "SPLAT output folder name",
//				getFieldEditorParent()));

		splatLogFieldEditor = new BooleanFieldEditor(BriefcasePreferenceConstants.SPLAT_GENERATE_LOG,
				"Generate SPLAT run log",
				getFieldEditorParent());
		addField(splatLogFieldEditor);

		splatLogFileFieldEditor = new FileFieldEditor(BriefcasePreferenceConstants.SPLAT_LOG_FILENAME,
				"SPLAT log filename:",
				true, getFieldEditorParent()) {

			@Override
			protected String changePressed() {

				final FileDialog dlgSaveAs = new FileDialog(getShell(), SWT.SAVE | SWT.SHEET);
				dlgSaveAs.setText("SPLAT log file");
				if (!getTextControl().getText().isEmpty()) {
					dlgSaveAs.setFileName(getTextControl().getText());
				} else {
					dlgSaveAs.setFileName("splat.log");
				}
				dlgSaveAs.setOverwrite(false);
				dlgSaveAs.setFilterExtensions(new String[] { "*.log", "*.*" });
				String fileName = dlgSaveAs.open();
				if (fileName == null) {
					return null;
				} else {
					fileName = fileName.trim();
				}

				// Create the file if it doesn't exist
				try {
					final File file = new File(fileName);
					file.createNewFile();
				} catch (IOException e) {
					Dialog.showError("SPLAT log file - Error", "A problem occurred while creating the file.");
					return null;
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
		addField(splatLogFileFieldEditor);

		label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 5));

		label = new Label(getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		label.setFont(boldFont);
		label.addDisposeListener(e -> boldFont.dispose());
		label.setText("Component Source");

		addField(new StringFieldEditor(BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER,
				"Component source folder name", getFieldEditorParent()));

		addField(new StringFieldEditor(BriefcasePreferenceConstants.KU_IMPL_FOLDER,
				"KU Attestation implementation folder name", getFieldEditorParent()));
	}

	private void configureEnabledFieldEditors() {
		splatLogFileFieldEditor.setEnabled(splatLogFieldEditor.getBooleanValue(), getFieldEditorParent());
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
//		setDescription("BriefCASE Settings");
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		configureEnabledFieldEditors();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		configureEnabledFieldEditors();
	}

	@Override
	protected void initialize() {
		super.initialize();
		configureEnabledFieldEditors();
	}

}
