package com.collins.trustedsystems.briefcase.assurance.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.collins.trustedsystems.briefcase.Activator;

/**
 * Class used to initialize default preference values.
 */
public class AssurancePreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(AssurancePreferenceConstants.REQUIREMENTS_REVIEW_FILENAME, "");
		store.setDefault(AssurancePreferenceConstants.AGREE_REVIEW_FILENAME, "");
	}
}
