package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.dialogs.ImportRequirementsGUI;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.collins.trustedsystems.briefcase.util.TraverseProject;

public class ImportRequirementsHandler extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Determine if this should run in "Requirements Manager" mode or "Import Requirements" mode.
		final boolean importRequirements = (event.getCommand().getId().indexOf("Import") != -1);
		String filename = event.getParameter("filename");

		// Get the current project
		final IProject project = TraverseProject.getCurrentProject();
		if (project == null) {
			Dialog.showError("Requirements Manager",
					"Unable to determine current project.  Open a project file in the editor.");
			return null;
		}

		if (importRequirements) {
			final FileDialog dlgReqFile = new FileDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN);
			dlgReqFile.setText("Select requirements file to import");
			dlgReqFile.setFilterPath(project.getLocation().append("Requirements").toOSString());
			final String[] filterExts = { "*.json", "*.*" };
			dlgReqFile.setFilterExtensions(filterExts);
			filename = dlgReqFile.open();
			if (filename == null) {
				return null;
			}
		}

		final boolean result = run(importRequirements, filename);

		// Format and save
		if (result) {
			AadlHandler.format(true);
		}

		return null;
	}

	public boolean run(final boolean importRequirements, final String filename) {

		/*
		 * Steps for requirements manager:
		 * (1) Accept requirement input files from user.
		 * (2) Filter requirement input files: select those that match the hashcode of the AADL model.
		 * (3) Remove from the requirements database all "ToDo" requirements that do not match the hashcode of the current AADL model.
		 * (4) Read requirements from the filtered files and add them to the requirements database.
		 * (5) Collect the list of requirements from the current AADL model.
		 * (6) Display to the user the requirements database and from the AADL model.
		 * (7) User can reclassify these requirements with the following restrictions:
		 * (7a) "Omit" with old hashcode: cannot be modified.
		 * (7b) "ToDo" to "Add" or "Add+Agree": add requirement to the AADL model.
		 * (7c) "ToDo" to "Omit": add requirement to the requirements database as an omitted requirement.
		 * (7d) "Add" or "Add+Agree" to "Omit": remove from AADL model and add to requirements database as omitted requirement (note hashcode of the model).
		 * (7e) "Add" or "Add+Agree" to "ToDo": not allowed.
		 * (7f) "Add+Agree" to "Add": not allowed.
		 * (8) "ToDo" and "Omit" requirements stay in the requirements database, "Add" and "Add+Agree" go into the AADL model.
		 */

		final RequirementsManager reqMgr = RequirementsManager.getInstance();
		if (!reqMgr.readRequirementFiles(importRequirements, filename)) {
			// No requirement files were read
			return false;
		}

		final ImportRequirementsGUI wizard = new ImportRequirementsGUI(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				importRequirements ? ImportRequirementsGUI.IMPORT : ImportRequirementsGUI.MANAGE);
		wizard.setRequirements(reqMgr.getRequirements());

		if (wizard.getRequirements().isEmpty()) {
			Dialog.showInfo("Requirements Manager", "No requirements found in model.");
		} else if (wizard.open() == SWT.OK) {
			final List<CyberRequirement> updatedReqs = wizard.getRequirements();
			reqMgr.updateRequirements(updatedReqs);
			BriefcaseNotifier.notify("Requirements Manager", "Requirements import complete.");
			return true;
		}

		return false;
	}

}
