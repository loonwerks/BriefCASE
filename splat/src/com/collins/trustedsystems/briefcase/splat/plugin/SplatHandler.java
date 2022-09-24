package com.collins.trustedsystems.briefcase.splat.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.StringLiteral;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ProgrammingProperties;
import org.osgi.framework.Bundle;

import com.collins.trustedsystems.briefcase.Activator;
import com.collins.trustedsystems.briefcase.json.export.Aadl2Json;
import com.collins.trustedsystems.briefcase.preferences.BriefcasePreferenceConstants;
import com.collins.trustedsystems.briefcase.staircase.handlers.AadlHandler;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.collins.trustedsystems.briefcase.util.TraverseProject;

public class SplatHandler extends AadlHandler {

	static final String bundleId = "com.collins.trustedsystems.briefcase.splat";
	private final static String FOLDER_PACKAGE_DELIMITER = "_";
	private String outputDir = "";
	private String componentSourceFolderName = "";
	private MessageConsoleStream out = null;

	private MessageConsole findConsole(String name) {
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

	@Override
	protected void runCommand(EObject eObj) {

		// This only works on linux
		if (!System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("nux")) {
			Dialog.showError("SPLAT", "SPLAT synthesis is only supported on Linux operating systems.");
			return;
		}

		// Check if a component implementation is selected
		if (!(eObj instanceof ComponentImplementation)) {
			Dialog.showError("SPLAT", "Select a component implementation containing components for synthesis.");
			return;
		}
		final ComponentImplementation ci = (ComponentImplementation) eObj;

		runSplat(ci);

		format(true);

	}

	private void runSplat(ComponentImplementation ci) {

		try {

			final URI jsonURI = Aadl2Json.createJson();
			final IFile file = ResourcesPlugin.getWorkspace()
					.getRoot()
					.getFile(new Path(jsonURI.toPlatformString(true)));
			final String jsonPath = file.getRawLocation().toOSString();

			final Bundle bundle = Platform.getBundle(bundleId);
			final String splatDir = (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources"), null)))
					.getFile();
			final String splatPath = (FileLocator
					.toFileURL(FileLocator.find(bundle, new Path("resources/splat"), null))).getFile();

			final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(executionEvent);
			final IWorkbenchPage page = window.getActivePage();
			final IConsoleView view = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			final MessageConsole console = findConsole("SPLAT");
			out = console.newMessageStream();

			// command line parameters
			final List<String> cmdLineArgs = new ArrayList<>();
			cmdLineArgs.add(splatPath);
			cmdLineArgs.add("-outdir");

			componentSourceFolderName = Activator.getDefault()
					.getPreferenceStore()
					.getString(BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER);
			if (componentSourceFolderName.isBlank()) {
				componentSourceFolderName = Activator.getDefault()
						.getPreferenceStore()
						.getDefaultString(BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER);
			}
			final IProject project = TraverseProject.getCurrentProject();
			outputDir = URI.createURI(project.getLocation().toString()).appendSegment(componentSourceFolderName)
					.toString();
			cmdLineArgs.add(outputDir);

			cmdLineArgs.add(jsonPath);

			final Runtime rt = Runtime.getRuntime();
			rt.exec("chmod a+x " + splatPath);
			final String[] subCmds = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
			final String[] environmentVars = { "LD_LIBRARY_PATH=" + splatDir };
			final Process clientProcess = Runtime.getRuntime().exec(subCmds, environmentVars);

			final BufferedReader stdErr = new BufferedReader(new InputStreamReader(clientProcess.getErrorStream()));

			out.println(String.join(" ", cmdLineArgs) + " " + "LD_LIBRARY_PATH=" + splatDir);

			view.display(console);

			String s = null;
			final List<String> componentSourceDirectories = new ArrayList<>();
			while ((s = stdErr.readLine()) != null) {
				if (s.startsWith("Code written to directory: ")) {
					componentSourceDirectories.add(s.replace("Code written to directory: ", ""));
				}
				out.println(s);
			}

			int exitVal = clientProcess.waitFor();
			if (exitVal == 0) {

				// Compile
				exitVal = compileCakeSource(componentSourceDirectories);

				// refresh project directory
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

				// Insert the location of the source code into the filter component implementations in the model
				insertSourceCodeLocation(componentSourceDirectories);

				// update log
				if (Activator.getDefault().getPreferenceStore()
						.getBoolean(BriefcasePreferenceConstants.SPLAT_GENERATE_LOG)) {
					updateLog();
				}
			}

			if (exitVal == 0) {
				out.println("SPLAT completed successfully.");
			} else {
				out.println("SPLAT has encountered an error and was unable to complete.");
			}

		} catch (Exception e) {
			Dialog.showError("SPLAT", "SPLAT has encountered an error and was unable to complete.");
			e.printStackTrace();
			return;
		}

		BriefcaseNotifier.notify("SPLAT", "SPLAT completed.");

		return;
	}

	private int compileCakeSource(List<String> componentSourceDirectories) {

		int status = 0;
		for (String compDir : componentSourceDirectories) {

			final String comp = compDir.replace(outputDir + "/", "");
			final List<String> cmdLineArgs = new ArrayList<>();
			cmdLineArgs.add("bash");
			cmdLineArgs.add("make.sh");
			cmdLineArgs.add(comp);

			try {
				out.print("Compiling CakeML code for " + comp + "... ");
				final Runtime rt = Runtime.getRuntime();
				rt.exec("chmod a+x " + compDir);
				final String[] subCmds = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
				final Process clientProcess = Runtime.getRuntime().exec(subCmds);

				final int exitVal = clientProcess.waitFor();
				if (exitVal == 0) {
					out.println("Success");
				} else {
					out.println("Failure");
					status = -1;
				}
			} catch (Exception e) {
				out.println("Failure");
				out.println("\t" + e.getMessage());
				status = -1;
			}
		}
		return status;
	}


	private void insertSourceCodeLocation(List<String> componentSourceDirectories) {

		// Get all the folders in the output directory
		final Map<AadlPackage, List<String>> pkgMap = new HashMap<>();

		for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			final List<String> compNames = new ArrayList<>();
			for (String d : componentSourceDirectories) {
				if (d.replace(outputDir + "/", "").startsWith(pkg.getName() + "_")) {
					compNames.add(d.replace(outputDir + "/", "").substring(pkg.getName().length() + 1));
				}
			}

			if (compNames.isEmpty()) {
				continue;
			}

			List<String> pkgComps = new ArrayList<>();
			if (pkgMap.containsKey(pkg)) {
				pkgComps = pkgMap.get(pkg);
			}
			pkgComps.addAll(compNames);
			pkgMap.put(pkg, pkgComps);

		}


		// Iterate through project packages
		for (AadlPackage pkg : pkgMap.keySet()) {

			final XtextResourceSet resourceSet = new XtextResourceSet();
			resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
			final AadlPackage aadlPackage = (AadlPackage) resourceSet.getEObject(EcoreUtil.getURI(pkg), true);

			final Resource aadlResource = aadlPackage.eResource();
			final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE
					.createEditingDomain(resourceSet);

			// We execute this command on the command stack because otherwise, we will not
			// have write permissions on the editing domain.
			final Command cmd = new RecordingCommand(domain) {

				@Override
				protected void doExecute() {

					for (ComponentImplementation ci : EcoreUtil2.getAllContentsOfType(aadlPackage,
							ComponentImplementation.class)) {

						if (pkgMap.get(pkg).contains(ci.getType().getName())) {
							// Insert language property
							CasePropertyUtils.setCompLanguage(ci, "CakeML");

							// Insert source text property
							final String compSourceText = componentSourceFolderName + "/" + aadlPackage.getName()
									+ FOLDER_PACKAGE_DELIMITER
									+ ci.getType().getName() + "/" + aadlPackage.getName() + FOLDER_PACKAGE_DELIMITER
									+ ci.getType().getName() + ".S";
							final String ffiSourceText = componentSourceFolderName + "/" + aadlPackage.getName()
									+ FOLDER_PACKAGE_DELIMITER + ci.getType().getName() + "/basis_ffi.c";

							final Property sourceTextProp =
									GetProperties.lookupPropertyDefinition(ci, ProgrammingProperties._NAME,
											ProgrammingProperties.SOURCE_TEXT);

							final List<StringLiteral> listVal = new ArrayList<>();
							final StringLiteral compSourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
							compSourceTextLit.setValue(compSourceText);
							listVal.add(compSourceTextLit);
							final StringLiteral ffiSourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
							ffiSourceTextLit.setValue(ffiSourceText);
							listVal.add(ffiSourceTextLit);

							ci.setPropertyValue(sourceTextProp, listVal);

						}
					}

				}

			};

			try {
				((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

				// We're done: Save the model
				aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
			} catch (Exception e) {
				e.printStackTrace();
			}
			domain.dispose();

		}

	}

	private void updateLog() {
		final Date date = new Date(System.currentTimeMillis());
		final String status = "SPLAT completed successfully on " + date + System.lineSeparator();
		final File file = new File(
				Activator.getDefault().getPreferenceStore().getString(BriefcasePreferenceConstants.SPLAT_LOG_FILENAME));
		try {
			final FileWriter writer = new FileWriter(file, true);
			writer.write(status);
			writer.close();
		} catch (IOException e) {
			Dialog.showWarning("SPLAT", "Unable to write to log file.");
		}

	}

}
