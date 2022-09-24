/*
Copyright (c) 2021, Collins Aerospace.
Developed with the sponsorship of Defense Advanced Research Projects Agency (DARPA).

Permission is hereby granted, free of charge, to any person obtaining a copy of this data,
including any software or models in source or binary form, as well as any drawings, specifications,
and documentation (collectively "the Data"), to deal in the Data without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Data, and to permit persons to whom the Data is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Data.

THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS, SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR OTHER DEALINGS IN THE DATA.
*/
package com.collins.trustedsystems.briefcase.attestation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;

import com.collins.trustedsystems.briefcase.Activator;
import com.collins.trustedsystems.briefcase.preferences.BriefcasePreferenceConstants;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.collins.trustedsystems.briefcase.util.Filesystem;
import com.collins.trustedsystems.briefcase.util.TraverseProject;

public class AttestationAccess {

	private final static String BUILD = "build";

	public static boolean createSourceDirectory() {
		final IProject project = TraverseProject.getCurrentProject();
		String componentSourceFolderName = Activator.getDefault()
				.getPreferenceStore()
				.getString(BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER);
		if (componentSourceFolderName.isBlank()) {
			componentSourceFolderName = Activator.getDefault()
					.getPreferenceStore()
					.getDefaultString(BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER);
		}
		String kuImplFolderName = Activator.getDefault()
				.getPreferenceStore()
				.getString(BriefcasePreferenceConstants.KU_IMPL_FOLDER);
		if (kuImplFolderName.isBlank()) {
			kuImplFolderName = Activator.getDefault()
					.getPreferenceStore()
					.getDefaultString(BriefcasePreferenceConstants.KU_IMPL_FOLDER);
		}
		URI uri = URI.createPlatformResourceURI(project.getFullPath().toString(), true);
		Filesystem.createFolder(uri, new String[] { componentSourceFolderName, kuImplFolderName });
		uri = URI.createURI(project.getLocation().toString()).appendSegment(componentSourceFolderName)
				.appendSegment(kuImplFolderName);
		File destinationDirectory = new File(uri.toString());

		// Copy attestation implementation
		try {
			File sourceDirectory = new File(getSourceDirectory());
			Filesystem.copyDirectory(sourceDirectory, destinationDirectory);
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean compileCakeSource() {

		// This only works on linux
		if (!System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("nux")) {
			BriefcaseNotifier.printWarning(
					"Attestation Manager CakeML compilation is only supported on Linux operating systems");
			return false;
		}

		final IProject project = TraverseProject.getCurrentProject();
		String componentSourceFolderName = Activator.getDefault()
				.getPreferenceStore()
				.getString(BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER);
		if (componentSourceFolderName.isBlank()) {
			componentSourceFolderName = Activator.getDefault()
					.getPreferenceStore()
					.getDefaultString(BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER);
		}
		String kuImplFolderName = Activator.getDefault()
				.getPreferenceStore()
				.getString(BriefcasePreferenceConstants.KU_IMPL_FOLDER);
		if (kuImplFolderName.isBlank()) {
			kuImplFolderName = Activator.getDefault()
					.getPreferenceStore()
					.getDefaultString(BriefcasePreferenceConstants.KU_IMPL_FOLDER);
		}
		final URI buildUri = URI.createURI(project.getLocation().toString())
				.appendSegment(componentSourceFolderName)
				.appendSegment(kuImplFolderName)
				.appendSegment(BUILD);
		// If build dir doesn't exist, create it
		final File buildDir = new File(buildUri.toString());
		if (!buildDir.exists()) {
			Filesystem.createFolder(buildUri.trimSegments(1), new String[] { BUILD });
		}

		final List<String> cmdLineArgs = new ArrayList<>();
		cmdLineArgs.add("cmake");
		cmdLineArgs.add("..");

		try {

			final Runtime rt = Runtime.getRuntime();
			rt.exec("chmod a+x " + buildUri.toString());
			final String[] subCmds = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
			final Process clientProcess = Runtime.getRuntime().exec(subCmds);

			final int exitVal = clientProcess.waitFor();
			if (exitVal == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	private static String getSourceDirectory() {

		Bundle bundle = Platform.getBundle("com.collins.trustedsystems.briefcase.attestation");
		try {
			return (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources"), null))).getFile();
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to extract AC Validation from plug-in", e);
		}
	}

}
