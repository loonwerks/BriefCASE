package com.collins.trustedsystems.briefcase.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.collins.trustedsystems.briefcase.Activator;

/**
 * Class used to initialize default preference values.
 */
public class BriefcasePreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(BriefcasePreferenceConstants.SPLAT_OUTPUT_FOLDER, "SPLAT");
		store.setDefault(BriefcasePreferenceConstants.SPLAT_GENERATE_LOG, true);
		store.setDefault(BriefcasePreferenceConstants.SPLAT_LOG_FILENAME,
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/splat.log");
		store.setDefault(BriefcasePreferenceConstants.KU_IMPL_FOLDER, "KU_Attestation");
	}
}
