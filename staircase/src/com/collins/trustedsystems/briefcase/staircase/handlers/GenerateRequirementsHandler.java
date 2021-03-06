package com.collins.trustedsystems.briefcase.staircase.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.osate.aadl2.ComponentImplementation;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.json.export.Aadl2Json;
import com.collins.trustedsystems.briefcase.json.export.AadlTranslate.AgreePrintOption;
import com.collins.trustedsystems.briefcase.util.ModelHashcode;
import com.collins.trustedsystems.briefcase.util.TraverseProject;
import com.google.gson.JsonObject;

public class GenerateRequirementsHandler extends AadlHandler {

	private final static String GENERATE_REQUIREMENTS_TOOL_COMMAND = "com.collins.trustedsystems.briefcase.staircase.commands.GenerateRequirements.tool";

	@Override
	protected void runCommand(URI uri) {

		// Check if a component implementation is selected
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof ComponentImplementation)) {
			Dialog.showError("Generate Cyber Requirements", "Select the top-level system implementation for analysis.");
			return;
		}

		final ComponentImplementation ci = (ComponentImplementation) eObj;

		final IProject project = TraverseProject.getCurrentProject();
		if (project == null) {
			Dialog.showError("Generate Cyber Requirements",
					"Unable to determine current AADL project name.  Make sure the top-level system implementation is open in the text editor.");
			return;
		}

		String hashcode = "";
		try {
			hashcode = ModelHashcode.getHashcode(ci);
		} catch (Exception e) {
			Dialog.showError("Generate Cyber Requirements", e.getMessage());
			return;
		}

		final JsonObject header = new JsonObject();
		header.addProperty("project", project.getName());
		header.addProperty("implementation", ci.getQualifiedName());
		header.addProperty("date", System.currentTimeMillis());
		header.addProperty("hash", hashcode);

		final String tool = this.executionEvent.getParameter(GENERATE_REQUIREMENTS_TOOL_COMMAND);
		switch (tool.toLowerCase()) {

		case "gearcase": // CRA

				break;

		case "dcrypps": // Vanderbilt / DOLL

			try {
				// Generate json
				final URI jsonURI = Aadl2Json.createJson(header, AgreePrintOption.BOTH);
			} catch (Exception e) {
				Dialog.showError("Generate Cyber Requirements", "Unable to export model to JSON format.");
				return;
			}

			// TODO: Call tool
			if (Dialog.askQuestion("Generate Cyber Requirements", "DCRYPPS analysis complete.  Import requirements?")) {
				try {
					final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(this.executionEvent);
					final IHandlerService handlerService = window.getService(IHandlerService.class);
					handlerService.executeCommand("com.collins.trustedsystems.briefcase.architecture.commands.ImportRequirements",
							null);
				} catch (Exception e) {
					Dialog.showError("Generate Cyber Requirements", e.getMessage());
					return;
				}
			}

			break;

		default:
			Dialog.showError("Generate Cyber Requirements", tool + " is not a recognized cyber requirements tool.");
			return;
		}

	}
}
