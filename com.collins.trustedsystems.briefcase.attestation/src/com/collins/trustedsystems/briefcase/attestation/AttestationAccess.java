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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
		Filesystem.createFolder(uri, new String[] { componentSourceFolderName, kuImplFolderName, BUILD });
		uri = URI.createURI(project.getLocation().toString()).appendSegment(componentSourceFolderName)
				.appendSegment(kuImplFolderName);
		final File destinationDirectory = new File(uri.toString());

		// Copy attestation implementation
		try {
			final File sourceDirectory = new File(getSourceDirectory());
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
		final URI buildUri = URI.createURI(project.getLocation().toString(), true)
				.appendSegment(componentSourceFolderName)
				.appendSegment(kuImplFolderName)
				.appendSegment(BUILD);

		final List<String> cmdLineArgs = new ArrayList<>();
		cmdLineArgs.add("cmake");
		cmdLineArgs.add(buildUri.trimSegments(1).toString());

		try {

			new Runner(cmdLineArgs, new File(buildUri.toString()));
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	public static class Runner {
		protected Process process;
		protected BufferedReader fromProcess;

		Runner(List<String> args, File directory) throws Exception {

			ProcessBuilder processBuilder = new ProcessBuilder(args);
			processBuilder.directory(directory);
			processBuilder.redirectErrorStream(true);

			try {
				process = processBuilder.start();
			} catch (IOException e) {
				final Exception generalException = new Exception(
						"Unable to start UnBBayes by executing: " + String.join(" ", processBuilder.command()), e);
				throw generalException;
			}
			addShutdownHook();
			fromProcess = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String s = "";
			while ((s = fromProcess.readLine()) != null) {
				System.out.println(s);
			}

			if (process != null) {
				process.waitFor();
			}
		}

		private final Thread shutdownHook = new Thread("shutdown-hook") {
			@Override
			public void run() {
				Runner.this.stop();
			}
		};

		private void addShutdownHook() {
			Runtime.getRuntime().addShutdownHook(shutdownHook);
		}

		private void removeShutdownHook() {
			try {
				Runtime.getRuntime().removeShutdownHook(shutdownHook);
			} catch (IllegalStateException e) {
				// Ignore, we are already shutting down
			}
		}

		public synchronized void stop() {
			/**
			 * This must be synchronized since two threads (an Engine or a shutdown
			 * hook) may try to stop the process at the same time
			 */

			if (process != null) {
				process.destroy();
				process = null;
			}

			removeShutdownHook();
		}
	};

	private static String getSourceDirectory() {

		Bundle bundle = Platform.getBundle("com.collins.trustedsystems.briefcase.attestation");
		try {
			return (FileLocator.toFileURL(FileLocator.find(bundle, new Path("resources"), null))).getFile();
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to extract AC Validation from plug-in", e);
		}
	}

}
