package com.collins.trustedsystems.briefcase.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.collins.trustedsystems.briefcase.Activator;

/**
 * Class used to initialize default preference values.
 */
public class CasePreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(CasePreferenceConstants.CASE_SPLAT_OUTPUT_FILENAME, "SWTheory.sml");
		store.setDefault(CasePreferenceConstants.CASE_DEBUG, false);
	}
}
