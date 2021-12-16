package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TransformDialogUtil {

	public static Text createTextField(Composite container, String labelText, String defaultText) {

		final Label lblTextField = new Label(container, SWT.NONE);
		lblTextField.setText(labelText);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Text text = new Text(container, SWT.BORDER);
		text.setLayoutData(dataInfoField);
		text.setText(defaultText);
		return text;
	}

	public static Button createCheckboxField(Composite container, String labelText,
			boolean selection) {

		final Label lblCheckboxField = new Label(container, SWT.NONE);
		lblCheckboxField.setText(labelText);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		final Button btnCheckbox = new Button(container, SWT.CHECK);
		btnCheckbox.setSelection(selection);
		btnCheckbox.setLayoutData(dataInfoField);
		return btnCheckbox;
	}

	public static MenuCombo createDataTypeField(Composite container, String labelText,
			Map<String, List<String>> types) {

		final Label lblRequestMessageDataTypeField = new Label(container, SWT.NONE);
		lblRequestMessageDataTypeField.setText(labelText);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		return new MenuCombo(container, types);
	}


	public static List<Button> createLogPortField(Composite container) {
		final Label lblLogField = new Label(container, SWT.NONE);
		lblLogField.setText("Create log port");
		lblLogField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		final Group logGroup = new Group(container, SWT.NONE);
		logGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		logGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		final List<Button> btnLogPortType = new ArrayList<>();

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

		return btnLogPortType;
	}

	public static Combo createComboField(Composite container, String labelText, List<String> contents,
			String defaultText) {
		final Label lblComboField = new Label(container, SWT.NONE);
		lblComboField.setText(labelText);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Combo combo = new Combo(container, SWT.BORDER);
		combo.setLayoutData(dataInfoField);
		combo.add(defaultText);
		for (String c : contents) {
			combo.add(c);
		}
		combo.setText(defaultText);
		return combo;
	}


}
