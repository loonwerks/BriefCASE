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
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
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
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ProgrammingProperties;
import org.osgi.framework.Bundle;

import com.collins.trustedsystems.briefcase.Activator;
import com.collins.trustedsystems.briefcase.json.export.Aadl2Json;
import com.collins.trustedsystems.briefcase.preferences.BriefcasePreferenceConstants;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.collins.trustedsystems.briefcase.util.Filesystem;
import com.collins.trustedsystems.briefcase.util.TraverseProject;

public class SplatHandler extends AbstractHandler {

	static final String bundleId = "com.collins.trustedsystems.briefcase.splat";
	private final static String FOLDER_PACKAGE_DELIMITER = "_";
	private String outputDir = "";

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName())) {
				return (MessageConsole) existing[i];
			}
		}
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		if (xtextEditor == null) {
			Dialog.showError("SPLAT", "An AADL editor must be active in order to generate JSON.");
			return null;
		}

		try {

			URI jsonURI = Aadl2Json.createJson();
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(jsonURI.toPlatformString(true)));
			String jsonPath = file.getRawLocation().toOSString();

			Bundle bundle = Platform.getBundle(bundleId);
			String splatDir = (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources"), null))).getFile();
			String splatPath = (FileLocator
					.toFileURL(FileLocator.find(bundle, new Path("resources/splat"), null))).getFile();

//			// Check if user selection and run SPLAT on the chosen platform
//			boolean isLinuxrPlatformSelected = Activator.getDefault().getPreferenceStore()
//					.getBoolean(SplatPreferenceConstants.CHECK_PLATFORM_PREFERENCE);
//			if (!isLinuxrPlatformSelected) {
//				if (!checkDockerInstall()) {
//					Dialog.showError("SPLAT",
//							"Docker is not installed.  A Docker installation is required in order to run SPLAT.");
//					return null;
//				}
//			}

			// Initialize process and other objects
			Process ClientProcess = null;

			MessageConsole console = findConsole("SPLAT");
			MessageConsoleStream out = console.newMessageStream();
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
			IWorkbenchPage page = window.getActivePage();
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view = (IConsoleView) page.showView(id);

			// command line parameters
			List<String> cmdLineArgs = new ArrayList<>();
//			String commands = "";

			// acquiring user preferences and setting them up accordingly for the exec command
//			if (isLinuxrPlatformSelected) {
			cmdLineArgs.add(splatPath);
//			}
//			String assuranceLevel = Activator.getDefault().getPreferenceStore()
//					.getString(SplatPreferenceConstants.ASSURANCE_LEVEL);
//			cmdLineArgs.add("-alevel");
//			if (assuranceLevel.equals(SplatPreferenceConstants.ASSURANCE_LEVEL_CAKE)) {
//				cmdLineArgs.add("cake");
//			} else if (assuranceLevel.equals(SplatPreferenceConstants.ASSURANCE_LEVEL_HOL)) {
//				cmdLineArgs.add("hol");
//			} else if (assuranceLevel.equals(SplatPreferenceConstants.ASSURANCE_LEVEL_FULL)) {
//				cmdLineArgs.add("full");
//			} else {
//				cmdLineArgs.add("basic");
//			}

//			String codeGeneration = Activator.getDefault().getPreferenceStore()
//					.getString(SplatPreferenceConstants.CODE_GENERATION);
//			cmdLineArgs.add("-codegen");
//			if (codeGeneration.equals(SplatPreferenceConstants.CODE_GENERATION_C)) {
//				cmdLineArgs.add("C");
//			} else if (codeGeneration.equals(SplatPreferenceConstants.CODE_GENERATION_SML)) {
//				cmdLineArgs.add("SML");
//			} else if (codeGeneration.equals(SplatPreferenceConstants.CODE_GENERATION_ADA)) {
//				cmdLineArgs.add("Ada");
//			} else if (codeGeneration.equals(SplatPreferenceConstants.CODE_GENERATION_SLANG)) {
//				cmdLineArgs.add("Slang");
//			} else {
//				cmdLineArgs.add("Java");
//			}

//			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.CHECK_PROPERTIES)) {
//				cmdLineArgs.add("-checkprops");
//			}

			cmdLineArgs.add("-outdir");
//			if (!isLinuxrPlatformSelected) {
//				cmdLineArgs.add("./");
//			} else {
			String componentSourceFolderName = Platform.getPreferencesService().getString(
					"com.collins.trustedsystems.briefcase", BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER, "",
					null);
			IProject project = TraverseProject.getCurrentProject();
			outputDir = URI.createURI(project.getLocation().toString()).appendSegment(componentSourceFolderName)
					.toString();
			cmdLineArgs.add(outputDir);
//			}

//			cmdLineArgs.add("-intwidth");
//			cmdLineArgs.add(Integer.toString(
//					Activator.getDefault().getPreferenceStore().getInt(SplatPreferenceConstants.INTEGER_WIDTH)));

//			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.OPTIMIZE)) {
//				cmdLineArgs.add("optimize");
//			}

//			cmdLineArgs.add("-endian");
//			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.ENDIAN_BIG)) {
//				cmdLineArgs.add("MSB");
//			} else {
//				cmdLineArgs.add("LSB");
//			}

//			cmdLineArgs.add("-encoding");
//			String encoding = Activator.getDefault().getPreferenceStore().getString(SplatPreferenceConstants.ENCODING);
//			if (encoding.equals(SplatPreferenceConstants.ENCODING_UNSIGNED)) {
//				cmdLineArgs.add("Unsigned");
//			} else if (encoding.equals(SplatPreferenceConstants.ENCODING_SIGN_MAG)) {
//				cmdLineArgs.add("Sign_mag");
//			} else if (encoding.equals(SplatPreferenceConstants.ENCODING_ZIGZAG)) {
//				cmdLineArgs.add("Zigzag");
//			} else {
//				cmdLineArgs.add("Twos_comp");
//			}

//			if (Activator.getDefault().getPreferenceStore().getBoolean(SplatPreferenceConstants.PRESERVE_MODEL_NUMS)) {
//				cmdLineArgs.add("-preserve_model_nums");
//			}

			// input file
//			if (!isLinuxrPlatformSelected) {
//				cmdLineArgs.add(jsonURI.lastSegment());
//			} else {
				cmdLineArgs.add(jsonPath);
//			}

//			// Run SPLAT inside docker container
//			if (!isLinuxrPlatformSelected) {
//
//				Process dockerLoadImage = null;
//				Process dockerListImages = null;
//
//				// name of the splat image
//				String dockerImage = "splatimg";
//				System.out.println(
//						"_________________________________________________________________________________________________________________");
//				System.out.println("Running SPLAT inside docker container");
//				System.out.println(
//						"_________________________________________________________________________________________________________________");
//
//				// Prepare the volume mounting format for docker
//				boolean imageExists = false;
//				String jsonFileName = jsonURI.lastSegment();
//
//				java.net.URI splatImageURI = (FileLocator
//						.toFileURL(FileLocator.find(bundle, new Path("resources/splat_image.tar"), null))).toURI();
//				String splatTarFilePath = splatImageURI.normalize().getPath();
//				String splatImagePath = splatTarFilePath.substring(1, splatTarFilePath.length());
//				System.out.println("Location of docker image: " + splatImagePath);
//
//				// Copy json file to user specified directory
//				File sourceFile = new File(jsonPath);
//				File destFile = new File(Activator.getDefault().getPreferenceStore()
//						.getString(SplatPreferenceConstants.OUTPUT_DIRECTORY) + "/" + jsonFileName);
//				if (!destFile.exists()) {
//					Files.copy(sourceFile.toPath(), destFile.toPath());
//				}
//
//				// List the available docker images in the local machine and check if the required image exists
//				String listDockerImage = "docker image ls";
//				dockerListImages = Runtime.getRuntime().exec(listDockerImage);
//				BufferedReader stdInp = new BufferedReader(new InputStreamReader(dockerListImages.getInputStream()));
//				String s = null;
//
//				while ((s = stdInp.readLine()) != null) {
//					List<String> tempList = new ArrayList<String>(Arrays.asList(s.split(" ")));
//					tempList.removeAll(Arrays.asList(""));
//					if (tempList.get(0).equals(dockerImage)) {
//						imageExists = true;
//						break;
//					}
//				}
//
//				// If the required image does not exist in the local machine then load the image
//				if (!imageExists) {
//					System.out.println("Loading docker image \"" + dockerImage + "\" for SPLAT");
//					String loadDockerImage = "docker load -i " + splatImagePath;
//					dockerLoadImage = Runtime.getRuntime().exec(loadDockerImage);
//					BufferedReader stdErr = new BufferedReader(
//							new InputStreamReader(dockerLoadImage.getErrorStream()));
//
//					console = findConsole("SPLAT");
//					out = console.newMessageStream();
//					window = HandlerUtil.getActiveWorkbenchWindow(event);
//					page = window.getActivePage();
//					id = IConsoleConstants.ID_CONSOLE_VIEW;
//					view = (IConsoleView) page.showView(id);
//					view.display(console);
//
//					s = null;
//					while ((s = stdErr.readLine()) != null) {
//						out.println(s);
//					}
//				} else {
//					System.out.println("SPLAT image \"" + dockerImage + "\" is already loaded");
//				}
//
//				// build the docker run command
//				commands += "docker run --rm -v ";
//				commands += Activator.getDefault().getPreferenceStore()
//						.getString(SplatPreferenceConstants.OUTPUT_DIRECTORY) + ":/user ";
//				commands += dockerImage + " ";
//				for (String c : cmdLineArgs) {
//					commands += c + " ";
//				}
//				System.out.println(commands);
//				ClientProcess = Runtime.getRuntime().exec(commands);
//				ClientProcess.waitFor();
//				destFile.delete();
//			}

			// Run SPLAT in LINUX environment
//			else {

//				System.out.println("Running SPLAT in LINUX environment");
				Runtime rt = Runtime.getRuntime();
				rt.exec("chmod a+x " + splatPath);
				String[] subCmds = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
				String[] environmentVars = { "LD_LIBRARY_PATH=" + splatDir };
				ClientProcess = Runtime.getRuntime().exec(subCmds, environmentVars);
//			}

			BufferedReader stdErr = new BufferedReader(new InputStreamReader(ClientProcess.getErrorStream()));

			console = findConsole("SPLAT");
			out = console.newMessageStream();

//			if (!isLinuxrPlatformSelected) {
//				out.println(commands);
//			} else {
				String cmdLine = "";
				for (String st : cmdLineArgs) {
					cmdLine += st + " ";
				}
				cmdLine += "LD_LIBRARY_PATH=" + splatDir;
				out.println(cmdLine);
				System.out.println("SPLAT binary exists");
//			}

			window = HandlerUtil.getActiveWorkbenchWindow(event);
			page = window.getActivePage();
			id = IConsoleConstants.ID_CONSOLE_VIEW;
			view = (IConsoleView) page.showView(id);
			view.display(console);

			String s = null;
			while ((s = stdErr.readLine()) != null) {
				out.println(s);
			}

			int exitVal = ClientProcess.waitFor();
			if (exitVal == 0) {

				// refresh project directory
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

				// Insert the location of the source code into the filter component implementations in the model
				insertSourceCodeLocation(xtextEditor);

				// update log
				if (Activator.getDefault().getPreferenceStore()
						.getBoolean(BriefcasePreferenceConstants.SPLAT_GENERATE_LOG)) {
					updateLog();
				}

				out.println("SPLAT completed successfully.");
			} else {
				out.println("SPLAT has encountered an error and was unable to complete.");
			}

		} catch (Exception e) {
			Dialog.showError("SPLAT", "SPLAT has encountered an error and was unable to complete.");
			e.printStackTrace();
			return null;
		}

		BriefcaseNotifier.notify("SPLAT", "SPLAT completed.");

		return null;
	}



	private void insertSourceCodeLocation(XtextEditor currentEditor) {

		// Look in the SPLAT output directory for filters (each will be in its own folder)
//		String outputDir = Activator.getDefault().getPreferenceStore()
//				.getString(BriefcasePreferenceConstants.SPLAT_OUTPUT_FOLDER).replace("\\", "/") + "/";

		// Get all the folders in the output directory
		File dir = new File(outputDir);
		String[] compDirs = dir.list((current, name) -> new File(current, name).isDirectory());
		Map<AadlPackage, List<String>> pkgMap = new HashMap<>();

		for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			List<String> compNames = new ArrayList<>();
			for (String d : compDirs) {
				if (d.startsWith(pkg.getName())) {
					compNames.add(d.substring(pkg.getName().length() + 1));
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
			IFile file = Filesystem.getFile(pkg.eResource().getURI());
			XtextEditor editor = getEditor(file);
			if (editor != null) {
				editor.getDocument().modify(resource -> {
					AadlPackage aadlPackage = (AadlPackage) resource.getContents().get(0);
					for (ComponentImplementation ci : EcoreUtil2.getAllContentsOfType(aadlPackage,
							ComponentImplementation.class)) {
						if (pkgMap.get(pkg).contains(ci.getType().getName())) {
							// Insert language property
							String implLang = "CakeML";
//							String assuranceLevel = Activator.getDefault().getPreferenceStore()
//									.getString(SplatPreferenceConstants.ASSURANCE_LEVEL);
//							if (!assuranceLevel.equalsIgnoreCase(SplatPreferenceConstants.ASSURANCE_LEVEL_BASIC)) {
//								implLang = "CakeML";
//							}
							if (!CasePropertyUtils.setCompLanguage(ci, implLang)) {
//								return;
							}

							// Insert source text property
							String sourceText = outputDir + "/" + aadlPackage.getName() + FOLDER_PACKAGE_DELIMITER
									+ ci.getType().getName() + "/"
									+ ci.getType().getName();
							if (implLang.equalsIgnoreCase("c")) {
								sourceText += ".c";
							} else if (implLang.equalsIgnoreCase("cakeml")) {
								sourceText += ".S";
							} else {
								sourceText += ".o";
							}
							Property sourceTextProp = GetProperties.lookupPropertyDefinition(ci,
									ProgrammingProperties._NAME, ProgrammingProperties.SOURCE_TEXT);

							// Get any existing source text already in model
							Property prop = Aadl2GlobalScopeUtil.get(ci, Aadl2Package.eINSTANCE.getProperty(),
									ProgrammingProperties._NAME + "::" + ProgrammingProperties.SOURCE_TEXT);
							List<? extends PropertyExpression> currentSource = ci.getPropertyValueList(prop);
							List<StringLiteral> listVal = new ArrayList<>();
							if (currentSource != null) {
								for (PropertyExpression pe : currentSource) {
									if (pe instanceof StringLiteral) {
										StringLiteral source = (StringLiteral) pe;
										if (!source.getValue().equalsIgnoreCase(sourceText)) {
											listVal.add(source);
										}
									}
								}
							}

							StringLiteral sourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
							sourceTextLit.setValue(sourceText);
							listVal.add(sourceTextLit);
							ci.setPropertyValue(sourceTextProp, listVal);

						}
					}
					return null;
				});
				closeEditor(editor, !editor.equals(currentEditor), true);
			}
		}

	}

	private XtextEditor getEditor(IFile file) {
		IWorkbenchPage page = null;
		IEditorPart part = null;

		if (file.exists()) {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
			try {
				part = page.openEditor(new FileEditorInput(file), desc.getId());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

		if (part == null) {
			return null;
		}

		XtextEditor xedit = null;
		xedit = (XtextEditor) part;

		return xedit;
	}

	private void closeEditor(XtextEditor editor, boolean close, boolean save) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (save) {
			page.saveEditor(editor, false);
		}

		if (close) {
			page.closeEditor(editor, false);
		}
	}

	private void updateLog() {
		Date date = new Date(System.currentTimeMillis());
		String status = "SPLAT completed successfully on " + date + System.lineSeparator();
		File file = new File(
				Activator.getDefault().getPreferenceStore().getString(BriefcasePreferenceConstants.SPLAT_LOG_FILENAME));
		FileWriter writer;
		try {
			writer = new FileWriter(file, true);
			writer.write(status);
			writer.close();
		} catch (IOException e) {
			Dialog.showWarning("SPLAT", "Unable to write to log file.");
		}

	}

//	private boolean checkDockerInstall() {
//		try {
//			Runtime.getRuntime().exec("docker --version");
//		} catch (IOException e) {
//			return false;
//		}
//		return true;
//	}

}
