package com.collins.trustedsystems.briefcase.util;

import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class BriefcaseNotifier {

	public static void notify(String title, String message) {
		AbstractNotificationPopup popup = new BriefcasePopup(title, message);
		popup.open();
	}

	static class BriefcasePopup extends AbstractNotificationPopup {

		private final String title;
		private final String message;
		private static final long CLOSE_DELAY = 3000;

		public BriefcasePopup(String title, String message) {
			super(Display.getCurrent());
			setDelayClose(CLOSE_DELAY);
			this.title = title;
			this.message = message;
		}

		@Override
		protected void createContentArea(Composite parent) {

			Composite container = new Composite(parent, SWT.NULL);

			GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			container.setLayoutData(data);
			container.setLayout(new GridLayout(1, false));

			Label lblMsg = new Label(container, SWT.NULL);
			lblMsg.setText(message);

		}

		@Override
		protected String getPopupShellTitle() {
			return title;
		}
	}
}
