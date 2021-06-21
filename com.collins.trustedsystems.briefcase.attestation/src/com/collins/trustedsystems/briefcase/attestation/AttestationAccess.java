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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;

import com.collins.trustedsystems.briefcase.preferences.BriefcasePreferenceConstants;
import com.collins.trustedsystems.briefcase.util.Filesystem;
import com.collins.trustedsystems.briefcase.util.TraverseProject;

public class AttestationAccess {
	public static boolean createSourceDirectory() {
		final IProject project = TraverseProject.getCurrentProject();
		final String componentSourceFolderName = Platform.getPreferencesService().getString(
				"com.collins.trustedsystems.briefcase", BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER, "", null);
		final String kuImplFolderName = Platform.getPreferencesService().getString(
				"com.collins.trustedsystems.briefcase",
				BriefcasePreferenceConstants.KU_IMPL_FOLDER, "", null);
		URI uri = URI.createPlatformResourceURI(project.getFullPath().toString(), true);
		Filesystem.createFolder(uri, new String[] { componentSourceFolderName, kuImplFolderName });
		uri = URI.createURI(project.getLocation().toString()).appendSegment(componentSourceFolderName)
				.appendSegment(kuImplFolderName);
		File destinationDirectory = new File(uri.toString());

		// Copy attestation implementation
		try {
			File sourceDirectory = new File(getSourceDirectory());
			copyDirectory(sourceDirectory, destinationDirectory);
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

	private static String getSourceDirectory() {

		Bundle bundle = Platform.getBundle("com.collins.trustedsystems.briefcase.attestation");
		try {
			return (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources"), null))).getFile();
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to extract AC Validation from plug-in", e);
		}
	}

	private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
		if (!destinationDirectory.exists()) {
			destinationDirectory.mkdir();
		}
		for (String f : sourceDirectory.list()) {
			copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
		}
	}

	public static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
		if (source.isDirectory()) {
			copyDirectory(source, destination);
		} else {
			copyFile(source, destination);
		}
	}

	private static void copyFile(File sourceFile, File destinationFile) throws IOException {
		try (InputStream in = new FileInputStream(sourceFile);
				OutputStream out = new FileOutputStream(destinationFile)) {
			byte[] buf = new byte[1024];
			int length;
			while ((length = in.read(buf)) > 0) {
				out.write(buf, 0, length);
			}
		}
	}
}
