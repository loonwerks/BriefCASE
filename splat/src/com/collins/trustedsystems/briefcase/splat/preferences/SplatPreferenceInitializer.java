package com.collins.trustedsystems.briefcase.splat.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.collins.trustedsystems.briefcase.splat.Activator;

public class SplatPreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		store.setDefault(SplatPreferenceConstants.CHECK_PROPERTIES, false);
//		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL, SplatPreferenceConstants.ASSURANCE_LEVEL_BASIC);
//		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL_BASIC, true);
//		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL_CAKE, false);
//		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL_HOL, false);
//		store.setDefault(SplatPreferenceConstants.ASSURANCE_LEVEL_FULL, false);
//		store.setDefault(SplatPreferenceConstants.CODE_GENERATION, SplatPreferenceConstants.CODE_GENERATION_C);
//		store.setDefault(SplatPreferenceConstants.CODE_GENERATION_C, true);
//		store.setDefault(SplatPreferenceConstants.CODE_GENERATION_SML, false);
//		store.setDefault(SplatPreferenceConstants.CODE_GENERATION_ADA, false);
//		store.setDefault(SplatPreferenceConstants.CODE_GENERATION_SLANG, false);
//		store.setDefault(SplatPreferenceConstants.CODE_GENERATION_JAVA, false);
//		store.setDefault(SplatPreferenceConstants.INTEGER_WIDTH, 32);
//		store.setDefault(SplatPreferenceConstants.OPTIMIZE, false);
//		store.setDefault(SplatPreferenceConstants.ENDIAN, SplatPreferenceConstants.ENDIAN_LITTLE);
//		store.setDefault(SplatPreferenceConstants.ENDIAN_LITTLE, true);
//		store.setDefault(SplatPreferenceConstants.ENDIAN_BIG, false);
//		store.setDefault(SplatPreferenceConstants.ENCODING, SplatPreferenceConstants.ENCODING_TWOS_COMP);
//		store.setDefault(SplatPreferenceConstants.ENCODING_UNSIGNED, false);
//		store.setDefault(SplatPreferenceConstants.ENCODING_TWOS_COMP, true);
//		store.setDefault(SplatPreferenceConstants.ENCODING_SIGN_MAG, false);
//		store.setDefault(SplatPreferenceConstants.ENCODING_ZIGZAG, false);
//		store.setDefault(SplatPreferenceConstants.PRESERVE_MODEL_NUMS, false);
		store.setDefault(SplatPreferenceConstants.OUTPUT_DIRECTORY,
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
		store.setDefault(SplatPreferenceConstants.GENERATE_LOG, true);
		store.setDefault(SplatPreferenceConstants.LOG_FILENAME,
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/splat.log");
//		store.setDefault(SplatPreferenceConstants.CHECK_PLATFORM_PREFERENCE, false);
	}
}
