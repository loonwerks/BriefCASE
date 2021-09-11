package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TransformDialogUtil {

	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public static void createTextField(Composite container, String labelText, Text text, String defaultText) {

		final Label lblTextField = new Label(container, SWT.NONE);
		lblTextField.setText(labelText);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(dataInfoField);
		text.setText(defaultText);
	}

	public static void createCheckboxField(Composite container, String labelText, Button btnCheckbox,
			boolean selection) {

		final Label lblCheckboxField = new Label(container, SWT.NONE);
		lblCheckboxField.setText(labelText);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnCheckbox = new Button(container, SWT.CHECK);
		btnCheckbox.setSelection(selection);
		btnCheckbox.setLayoutData(dataInfoField);

	}

	public static void createDataTypeField(Composite container, String labelText, MenuCombo menuCombo,
			Map<String, List<String>> types) {

		final Label lblRequestMessageDataTypeField = new Label(container, SWT.NONE);
		lblRequestMessageDataTypeField.setText(labelText);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		menuCombo = new MenuCombo(container, types);
	}

	public static void createDispatchProtocolField(Composite container, Label lblDispatchProtocol,
			Group grpDispatchProtocol, List<Button> btnDispatchProtocol, Label lblPeriod, Text txtPeriod) {
		lblDispatchProtocol = new Label(container, SWT.NONE);
		lblDispatchProtocol.setText("Dispatch protocol");
		lblDispatchProtocol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		grpDispatchProtocol = new Group(container, SWT.NONE);
		grpDispatchProtocol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		grpDispatchProtocol.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnDispatchProtocol.clear();



		final Button btnNoProtocol = new Button(grpDispatchProtocol, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);
		addSelectionListener(btnNoProtocol, lblPeriod, txtPeriod);

		final Button btnPeriodic = new Button(grpDispatchProtocol, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		final Button btnSporadic = new Button(grpDispatchProtocol, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		lblPeriod = new Label(container, SWT.NONE);
		lblPeriod.setLayoutData(dataInfoField);
		lblPeriod.setText("Period");
		lblPeriod.setEnabled(false);

		txtPeriod = new Text(container, SWT.BORDER);
		txtPeriod.setLayoutData(dataInfoField);
		txtPeriod.setEnabled(false);

		btnDispatchProtocol.add(btnNoProtocol);
		btnDispatchProtocol.add(btnPeriodic);
		btnDispatchProtocol.add(btnSporadic);

	}

	public static void createLogPortField(Composite container, List<Button> btnLogPortType) {
		final Label lblLogField = new Label(container, SWT.NONE);
		lblLogField.setText("Create log port");
		lblLogField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		final Group logGroup = new Group(container, SWT.NONE);
		logGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		logGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnLogPortType.clear();

		final Button btnNoLogPort = new Button(logGroup, SWT.RADIO);
		btnNoLogPort.setText("None");
		btnNoLogPort.setSelection(true);

		final Button btnEventLogPort = new Button(logGroup, SWT.RADIO);
		btnEventLogPort.setText("Event");
		btnEventLogPort.setSelection(false);

		final Button btnDataLogPort = new Button(logGroup, SWT.RADIO);
		btnDataLogPort.setText("Data");
		btnDataLogPort.setSelection(false);

		final Button btnEventDataLogPort = new Button(logGroup, SWT.RADIO);
		btnEventDataLogPort.setText("Event Data");
		btnEventDataLogPort.setSelection(false);

		btnLogPortType.add(btnDataLogPort);
		btnLogPortType.add(btnEventLogPort);
		btnLogPortType.add(btnEventDataLogPort);
		btnLogPortType.add(btnNoLogPort);

	}

	public static void createRequirementField(Composite container, Combo cboRequirement, List<String> requirements) {
		final Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		cboRequirement = new Combo(container, SWT.BORDER);
		cboRequirement.setLayoutData(dataInfoField);
		cboRequirement.add(NO_REQUIREMENT_SELECTED);
		for (String req : requirements) {
			cboRequirement.add(req);
		}
		cboRequirement.setText(NO_REQUIREMENT_SELECTED);

	}

	private static void addSelectionListener(Button button, Label label, Text text) {
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				label.setEnabled(!button.getSelection());
				text.setEnabled(!button.getSelection());
				if (button.getSelection()) {
					text.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

}
