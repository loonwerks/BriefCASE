/*
Copyright (c) 2021, Collins Aerospace.
Developed with the sponsorship of Defense Advanced Research Projects Agency (DARPA).

Permission is hereby granted, free of charge, to any person obtaining a copy of this data,
including any software or models in source or binary form, as well as any drawings, specifications,
and documentation (collectively "the Data"), to deal in the Data without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Data, and to permit persons to whom the Data is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Data.

THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS, SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR OTHER DEALINGS IN THE DATA.
*/
package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

public class MenuCombo {

	private Text text;
	private Button button;
	private Menu menu;

	public MenuCombo(Composite parent, Map<String, List<String>> items) {

		final Composite control = new Composite(parent, SWT.NONE);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		control.setLayout(new GridLayout(2, false));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		text = new Text(control, SWT.BORDER);
		text.setLayoutData(dataInfoField);
		button = new Button(control, SWT.NONE);
		button.setText("...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				menu.setLocation(button.toDisplay(new Point(0, 0)));
				menu.setVisible(true);
			}
		});


		final MenuManager mgr = new MenuManager();

		for (Map.Entry<String, List<String>> item : items.entrySet()) {

			final MenuManager packageMenu = new MenuManager();
			packageMenu.setMenuText(item.getKey());

			for (String type : item.getValue()) {

				packageMenu.add(new Action(type) {
					@Override
					public void run() {
						text.setText(item.getKey() + "::" + type);
					}
				});

			}
			mgr.add(packageMenu);
		}

		menu = mgr.createContextMenu(button);
		button.setMenu(menu);

	}

	public String getText() {
		return text.getText();
	}

	public void setText(String string) {
		text.setText(string);
	}

	public void setEnabled(boolean enabled) {
		text.setEnabled(enabled);
		button.setEnabled(enabled);
	}

	public void setFocus() {
		text.setFocus();
	}
}
