package com.collins.trustedsystems.briefcase.staircase.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PackageSection;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.Activator;
import com.collins.trustedsystems.briefcase.util.TraverseProject;

public class CaseUtils {

	public static final String CASE_RESOURCE_PATH = Activator.PLUGIN_ID + "/resources/";
	public static final String CASE_MODEL_TRANSFORMATIONS_NAME = "CASE_Model_Transformations";
	public static final String CASE_MODEL_TRANSFORMATIONS_FILE = CASE_MODEL_TRANSFORMATIONS_NAME + ".aadl";
	public static final String CASE_REQUIREMENTS_NAME = "CASE_Requirements";
	public static final String CASE_REQUIREMENTS_DIR = "Requirements";
	public static final String CASE_REQUIREMENTS_FILE = CASE_REQUIREMENTS_DIR + "/" + CASE_REQUIREMENTS_NAME + ".aadl";
	public static final String CASE_REQUIREMENTS_DATABASE_FILE = ".reqdb";


	/**
	 * Adds the CASE_Model_Transformations file to the list of imported model units via the 'with' statement
	 * to the specified package section
	 * @param pkgSection - The package section (public or private) to add the imported file to
	 * @param addRenameAll - If true, will add a rename::all for the package
	 * @return CASE_Model_Transformations package
	 */
	public static boolean addCaseModelTransformationsImport(PackageSection pkgSection, boolean addRenameAll) {

		// First check if CASE_Model_Transformations file has already been imported in the model
		AadlPackage casePackage = null;
		for (ModelUnit modelUnit : pkgSection.getImportedUnits()) {
			if (modelUnit instanceof AadlPackage) {
				if (modelUnit.getName().equalsIgnoreCase(CASE_MODEL_TRANSFORMATIONS_NAME)) {
					casePackage = (AadlPackage) modelUnit;
					break;
				}
			}
		}

		if (casePackage == null) {
			// Try importing the resource
			casePackage = getCaseModelTransformationsPackage();
			if (casePackage == null) {
				Dialog.showError("Could not import " + CASE_MODEL_TRANSFORMATIONS_NAME,
						"Package " + CASE_MODEL_TRANSFORMATIONS_NAME + " could not be found.");
				return false;
			}
			// Add as "importedUnit" to package section
			pkgSection.getImportedUnits().add(casePackage);
		}

//		if (addRenameAll) {
//			// Check if the rename already exists
//			PackageRename pkgRename = null;
//			for (PackageRename pr : pkgSection.getOwnedPackageRenames()) {
//				if (pr.getRenamedPackage().getName().equalsIgnoreCase(CASE_MODEL_TRANSFORMATIONS_NAME)) {
//					pkgRename = pr;
//					break;
//				}
//			}
//
//			if (pkgRename == null) {
//				// Add the rename
//				pkgRename = pkgSection.createOwnedPackageRename();
////				pkgRename = Aadl2Factory.eINSTANCE.createPackageRename();
//				pkgRename.setRenameAll(true);
//				pkgRename.setRenamedPackage(casePackage);
////				pkgSection.getOwnedPackageRenames().add(pkgRename);
//			}
//
//		}

		return true;
	}

	/**
	 * Gets the CASE Model Transformations Package
	 * @return AadlPackage
	 */
	public static AadlPackage getCaseModelTransformationsPackage() {
		AadlPackage aadlPkg = null;
		final String pathName = CASE_RESOURCE_PATH + CASE_MODEL_TRANSFORMATIONS_FILE;
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource r = resourceSet.getResource(URI.createPlatformPluginURI(pathName, true), true);
		final EObject eObj = r.getContents().get(0);
		if (eObj instanceof AadlPackage) {
			aadlPkg = (AadlPackage) eObj;
		}
		return aadlPkg;
	}


	/**
	 * Get the AADL package for Case Resolute Requirements.
	 * @return The AADL package representing CASE Resolute Requirements.
	 */
	public static AadlPackage getCaseRequirementsPackage() {
		for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			if (pkg.getName().equalsIgnoreCase(CASE_REQUIREMENTS_NAME)) {
				// Make sure resource is refreshed
				final Resource r = pkg.eResource();
				r.unload();
				try {
					r.load(null);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				return (AadlPackage) r.getContents().get(0);
			}
		}
		// Cyber-security Requirements package not found.
		// Initialize and return AADL package containing the resolute annex
		return initCaseRequirementsPackage();
	}

	/**
	 * Initialize the Case Requirements file and package
	 * @return
	 */
	private static AadlPackage initCaseRequirementsPackage() {
		// Create CASE_REQUIREMENTS_FILE

		final IFile caseReqFile = TraverseProject.getCurrentProject().getFile(CASE_REQUIREMENTS_FILE);
		if (!caseReqFile.exists()) {

			// Create Requirements directory, if it doesn't exist
			if (createRequirementsFolder(TraverseProject.getCurrentProject()) == null) {
				return null;
			}

			final String newline = System.lineSeparator();
			final String tab = "\t";
			final String contents = "package CASE_Requirements" + newline + "private" + newline + tab
					+ "annex resolute" + "{** **};" + newline + "end CASE_Requirements;" + newline;
			final InputStream source = new ByteArrayInputStream(contents.getBytes());
			try {
				caseReqFile.create(source, false, new NullProgressMonitor());
			} catch (CoreException e) {
				System.out.println("CASE_Requirements package not created successfully.");
				e.printStackTrace();
				return null;
			}
		}

//		// Create CASE_REQUIREMENTS_NAME package
//		AadlPackage pkg = TraverseProject.getPackageInFile(getCaseRequirementsFile());
//		if (pkg == null) {
//			// Create a new package
//			pkg = Aadl2Factory.eINSTANCE.createAadlPackage();
//			pkg.setName(CASE_REQUIREMENTS_NAME);
//
//			// Create a resource for the requirements file, and add package to resource
//			ResourceSetImpl rs = new ResourceSetImpl();
//			Resource r = rs.createResource(URI.createFileURI(new File(CASE_REQUIREMENTS_FILE).getAbsolutePath()));
//			r.getContents().add(pkg);
//		}
//
//		assert (pkg.getName().equalsIgnoreCase(CASE_REQUIREMENTS_NAME));

		// Checking if the package has been inserted into the abstract syntax tree
		AadlPackage pkg = null;
		for (AadlPackage reqPkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			if (reqPkg.getName().equalsIgnoreCase(CASE_REQUIREMENTS_NAME)) {
				pkg = reqPkg;
				break;
			}
		}
		if (pkg == null) {
			System.out.println("CASE_Requirements package not created successfully.");
		} else {
			System.out.println("CASE_Requirements package was created successfully.");
		}
		assert (pkg != null);

		return pkg;
	}

	/**
	 * Create Requirements folder in specified project if it doesn't exist
	 * @param project
	 * @return Requirements folder or null if not created
	 */
	public static IFolder createRequirementsFolder(IProject project) {
		final IFolder reqFolder = project.getFolder(CASE_REQUIREMENTS_DIR);
		if (!reqFolder.exists()) {
			try {
				reqFolder.create(false, true, new NullProgressMonitor());
			} catch (CoreException e) {
				System.out.println("Requirements folder could not be created.");
				e.printStackTrace();
				return null;
			}
		}
		return reqFolder;
	}

	public static IFile getCaseRequirementsFile() {
		final IFile caseReqFile = TraverseProject.getCurrentProject().getFile(CASE_REQUIREMENTS_FILE);
		if (!caseReqFile.exists()) {
			initCaseRequirementsPackage();
		}
		return caseReqFile;
	}

}
