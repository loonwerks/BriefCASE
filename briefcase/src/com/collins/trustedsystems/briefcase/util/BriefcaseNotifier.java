package com.collins.trustedsystems.briefcase.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class BriefcaseNotifier {

	public final static String CONSOLE_NAME = "BriefCASE";

	public BriefcaseNotifier(String message) {
		println(message);
	}

	public BriefcaseNotifier(String message, int severity) {
		if (severity == IStatus.INFO) {
			printInfo(message);
		} else if (severity == IStatus.WARNING) {
			printWarning(message);
		} else if (severity == IStatus.ERROR) {
			printError(message);
		} else {
			println(message);
		}
	}

	public BriefcaseNotifier(String title, String message) {
		notify(title, message);
	}

	public static void notify(String title, String message) {

		Display.getDefault().asyncExec(() -> {
			final AbstractNotificationPopup popup = new BriefcasePopup(title, message);
			popup.open();
		});
	}

	public static void println(String text) {

		Display.getDefault().asyncExec(() -> {
			final MessageConsole console = findConsole(CONSOLE_NAME);
			final MessageConsoleStream out = console.newMessageStream();
			out.println(text);

			try {
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final IConsoleView view = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
				view.display(console);
			} catch (Exception e) {

			}
		});

	}

	public static void printInfo(String text) {
		println("[ Info ]\t" + text);
	}

	public static void printWarning(String text) {
		println("[ Warning ]\t" + text);
	}

	public static void printError(String text) {
		println("[ Error ]\t" + text);
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

			final Composite container = new Composite(parent, SWT.NULL);

			final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			container.setLayoutData(data);
			container.setLayout(new GridLayout(1, false));

			final Label lblMsg = new Label(container, SWT.NULL);
			lblMsg.setText(message);

		}

		@Override
		protected String getPopupShellTitle() {
			return title;
		}
	}

	public static MessageConsole findConsole(String name) {
		final ConsolePlugin plugin = ConsolePlugin.getDefault();
		final IConsoleManager conMan = plugin.getConsoleManager();
		final IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName())) {
				return (MessageConsole) existing[i];
			}
		}
		// no console found, so create a new one
		final MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
