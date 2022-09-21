package com.collins.trustedsystems.briefcase.preferences;

import java.io.File;

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
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + "splat.log");
		store.setDefault(BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER, "Component_Source");
		store.setDefault(BriefcasePreferenceConstants.KU_IMPL_FOLDER, "KU_Attestation");
		store.setDefault(BriefcasePreferenceConstants.EXECUTION_TIME, "10ms..50ms");
		store.setDefault(BriefcasePreferenceConstants.PERIOD, "500ms");
		store.setDefault(BriefcasePreferenceConstants.EXECUTION_TIME, "10ms..50ms");
		store.setDefault(BriefcasePreferenceConstants.STACK_SIZE, "1048576 Bytes");
		store.setDefault(BriefcasePreferenceConstants.DISPATCH_PROTOCOL,
				BriefcasePreferenceConstants.DISPATCH_PROTOCOL_PERIODIC);
		store.setDefault(BriefcasePreferenceConstants.DISPATCH_PROTOCOL_NONE, false);
		store.setDefault(BriefcasePreferenceConstants.DISPATCH_PROTOCOL_PERIODIC, true);
		store.setDefault(BriefcasePreferenceConstants.DISPATCH_PROTOCOL_SPORADIC, false);
	}
}
