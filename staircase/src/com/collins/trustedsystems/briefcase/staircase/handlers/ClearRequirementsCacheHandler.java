package com.collins.trustedsystems.briefcase.staircase.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class ClearRequirementsCacheHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (Dialog.askQuestion("Clear Requirements Cache",
				"Clearing the requirements cache can have unintended consequences.  Are you sure you want to proceed?")) {
			clearRequirementsCache();
		}

		return null;
	}

	private void clearRequirementsCache() {

		// Clear imported requirements in requirements manager
		RequirementsManager.getInstance().clearRequirements();

		BriefcaseNotifier.notify("Requirements Manager", "Requirements cache cleared.");

	}

}
