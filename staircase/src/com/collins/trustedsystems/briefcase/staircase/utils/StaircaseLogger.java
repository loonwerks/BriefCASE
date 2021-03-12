package com.collins.trustedsystems.briefcase.staircase.utils;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class StaircaseLogger {
	private static MessageConsole console = findConsole("StairCASE");
	private static MessageConsoleStream out = console.newMessageStream();
	private static IConsoleView view = getConsoleView();

	public static void logMessage(String message) {
		out.println(message);
		displayView();
	}

	public static void logInfo(String message) {
		out.println("[INFO]: " + message);
		displayView();
	}

	public static void logWarning(String message) {
		out.println("[WARNING]: " + message);
		displayView();
	}

	public static void logError(String message) {
		out.println("[ERROR]: " + message);
		displayView();
	}

	private static void displayView() {
		if (view != null) {
			view.display(console);
		}
	}

	private static MessageConsole findConsole(String name) {
		final ConsolePlugin plugin = ConsolePlugin.getDefault();
		final IConsoleManager conMan = plugin.getConsoleManager();

		for (IConsole con : conMan.getConsoles()) {
			if (name.contentEquals(con.getName())) {
				return (MessageConsole) con;
			}
		}
		// no console found, so create a new one
		final MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	private static IConsoleView getConsoleView() {
		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();
		try {
			return (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
		} catch (PartInitException e) {
			return null;
		}
	}
}
