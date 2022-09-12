package com.collins.trustedsystems.briefcase.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;

public class Filesystem {

	public static String getBase(URI base) {
		String filename = base.lastSegment();
		int i = filename.lastIndexOf(".");
		return filename.substring(0, i);
	}

	public static URI createFolder(URI base, String[] subfolders) {
		for (int i = 0; i < subfolders.length; i++) {
			base = base.appendSegment(subfolders[i]);
			makeFolder(base);
		}
		return base;
	}

	public static void makeFolder(URI f) {
		IFolder folder = getRoot().getFolder(new Path(f.toPlatformString(true)));
		try {
			if (!folder.exists()) {
				folder.create(true, true, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			System.err.println("Error: trouble creating folder.");
			e.printStackTrace();
		}
	}

	public static IFile getFile(URI f) {
		final String pathString = f.isPlatform() ? f.toPlatformString(true) : f.toString();
		return getRoot().getFile(new Path(pathString));
	}

	public static String readFile(IFile res) throws CoreException, IOException {
		String contents = "";
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(res.getContents()));
		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			contents += line + System.lineSeparator();
		}
		bufferedReader.close();

		return contents;
	}

	public static void writeFile(IFile res, String contents) {
		writeFile(res, contents.getBytes());
	}

	public static void writeFile(IFile res, byte[] contents) {
		NullProgressMonitor monitor = new NullProgressMonitor();
		InputStream stream = new ByteArrayInputStream(contents);

		try {
			if (res.exists()) {
				res.delete(true, monitor);
			}
			res.create(stream, true, monitor);
		} catch (CoreException e) {
			System.err.println("Error: trouble writing file.");
			e.printStackTrace();
		}
	}

	public static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
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

	public static void copyFile(File sourceFile, File destinationFile) throws IOException {
		try (InputStream in = new FileInputStream(sourceFile);
				OutputStream out = new FileOutputStream(destinationFile)) {
			byte[] buf = new byte[1024];
			int length;
			while ((length = in.read(buf)) > 0) {
				out.write(buf, 0, length);
			}
		}
	}

	private static IWorkspaceRoot getRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
}
