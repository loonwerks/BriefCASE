package com.collins.trustedsystems.briefcase.assurance.artifact;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PlatformUI;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.util.TraverseProject;

public class ArtifactDescriptionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get the current project
		final IProject project = TraverseProject.getCurrentProject();
		if (project == null) {
			Dialog.showError("Requirements Manager",
					"Unable to determine current project.  Open a project file in the editor.");
			return null;
		}

		final ArtifactDescriptionManager dialog = new ArtifactDescriptionManager(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), project);
		dialog.open();

		return null;
	}

}
