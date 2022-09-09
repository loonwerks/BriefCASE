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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.StringLiteral;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.Activator;
import com.collins.trustedsystems.briefcase.util.TraverseProject;
import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.BaseType;
import com.rockwellcollins.atc.resolute.resolute.BinaryExpr;
import com.rockwellcollins.atc.resolute.resolute.ClaimArg;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimStrategy;
import com.rockwellcollins.atc.resolute.resolute.ClaimString;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.IdExpr;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;
import com.rockwellcollins.atc.resolute.resolute.UndevelopedExpr;

public class CaseUtils {

	public static final String CASE_RESOURCE_PATH = Activator.PLUGIN_ID + "/resources/";
	public static final String CASE_MODEL_TRANSFORMATIONS_NAME = "CASE_Model_Transformations";
	public static final String CASE_MODEL_TRANSFORMATIONS_FILE = CASE_MODEL_TRANSFORMATIONS_NAME + ".aadl";
	public static final String CASE_ASSURANCE_NAME = "CASE_Assurance";
	public static final String CASE_ASSURANCE_FILE = CASE_ASSURANCE_NAME + ".aadl";
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

	public static boolean importCaseRequirementsPackage(PackageSection pkgSection) {
		// First check if CASE_Requirements file has already been imported in the model
		AadlPackage casePackage = null;

		for (ModelUnit modelUnit : pkgSection.getImportedUnits()) {
			if (modelUnit instanceof AadlPackage && modelUnit.hasName()) {
				if (modelUnit.getName().equalsIgnoreCase(CASE_REQUIREMENTS_NAME)) {
					casePackage = (AadlPackage) modelUnit;
					break;
				}
			}
		}
		if (casePackage == null) {
			// Try importing the resource
			casePackage = getCaseRequirementsPackage();
			if (casePackage == null) {
				Dialog.showError("Could not import " + CASE_REQUIREMENTS_NAME,
						"Package " + CASE_REQUIREMENTS_NAME + " could not be found.");
				return false;
			}
			// Add as "importedUnit" to package section
			pkgSection.getImportedUnits().add(casePackage);
		}
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
	 * Gets the CASE Assurance Package
	 * @return AadlPackage
	 */
	public static AadlPackage getCaseAssuracePackage() {
		AadlPackage aadlPkg = null;
		final String pathName = CASE_RESOURCE_PATH + CASE_ASSURANCE_FILE;
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource r = resourceSet.getResource(URI.createPlatformPluginURI(pathName, true), true);
		final EObject eObj = r.getContents().get(0);
		if (eObj instanceof AadlPackage) {
			aadlPkg = (AadlPackage) eObj;
		}
		return aadlPkg;
	}

	public static AadlPackage getCaseAssuracePackage(ResourceSet resourceSet) {
		AadlPackage aadlPkg = null;
		final String pathName = CASE_RESOURCE_PATH + CASE_ASSURANCE_FILE;
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
			final String contents = "package CASE_Requirements" + newline + "private" + newline + tab + "annex resolute"
					+ "{** **};" + newline + "end CASE_Requirements;" + newline;
			final InputStream source = new ByteArrayInputStream(contents.getBytes());
			try {
				caseReqFile.create(source, false, new NullProgressMonitor());
			} catch (CoreException e) {
				System.out.println("CASE_Requirements package not created successfully.");
				e.printStackTrace();
				return null;
			}
		}

		// Checking if the package has been inserted into the abstract syntax tree
		AadlPackage pkg = null;
		for (AadlPackage reqPkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			if (reqPkg.getName().equalsIgnoreCase(CASE_REQUIREMENTS_NAME)) {
				pkg = reqPkg;
				break;
			}
		}
		if (pkg == null) {
			System.out.println("CASE_Requirements package was not created successfully.");
		} else {
			System.out.println("CASE_Requirements package was created successfully.");
		}
		assert (pkg != null);

		return pkg;
	}

	public static FunctionDefinition createRequirementsSatisfiedInModelGoal() {
		final FunctionDefinition requirementsSatisfiedInModelGoal = ResoluteFactory.eINSTANCE
				.createFunctionDefinition();
		requirementsSatisfiedInModelGoal.setName("security_requirements_satisfied_in_model");
		requirementsSatisfiedInModelGoal.setClaimType("goal");
		final ClaimBody claimBody = ResoluteFactory.eINSTANCE.createClaimBody();
		final ClaimString claimStr = ResoluteFactory.eINSTANCE.createClaimString();
		claimStr.setStr("Security requirements are satisfied in the system model");
		claimBody.getClaim().add(claimStr);
		final UndevelopedExpr undeveloped = ResoluteFactory.eINSTANCE.createUndevelopedExpr();
		claimBody.setExpr(undeveloped);
		requirementsSatisfiedInModelGoal.setBody(claimBody);
		return requirementsSatisfiedInModelGoal;
	}

	public static FunctionDefinition createRequirementsSatisfiedGoal(Classifier system,
			FunctionDefinition requirementsSatisfiedInModelGoal) {
		final FunctionDefinition requirementsSatisfiedGoal = ResoluteFactory.eINSTANCE.createFunctionDefinition();
		requirementsSatisfiedGoal.setName("security_requirements_satisfied");
		requirementsSatisfiedGoal.setClaimType("goal");
		final Arg arg = ResoluteFactory.eINSTANCE.createArg();
		final BaseType sysType = ResoluteFactory.eINSTANCE.createBaseType();
		sysType.setType("system");
		arg.setName("sys");
		arg.setType(sysType);
		requirementsSatisfiedGoal.getArgs().add(arg);
		final ClaimBody claimBody = ResoluteFactory.eINSTANCE.createClaimBody();
		final ClaimString claimStr = ResoluteFactory.eINSTANCE.createClaimString();
		claimStr.setStr("Security requirements are satisfied");
		claimBody.getClaim().add(claimStr);
		final BinaryExpr andExpr = ResoluteFactory.eINSTANCE.createBinaryExpr();
		andExpr.setOp("and");
		final FnCallExpr reqSatisfiedInModelExpr = ResoluteFactory.eINSTANCE.createFnCallExpr();
		reqSatisfiedInModelExpr.setFn(requirementsSatisfiedInModelGoal);
		andExpr.setLeft(reqSatisfiedInModelExpr);
		final FnCallExpr reqSatisfiedInImplExpr = ResoluteFactory.eINSTANCE.createFnCallExpr();
		final AadlPackage assurancePkg = getCaseAssuracePackage(system.eResource().getResourceSet());
		final PackageSection packageSection = assurancePkg.getOwnedPrivateSection();
		final AnnexLibrary annexLibrary = packageSection.getOwnedAnnexLibraries().get(0);
		final ResoluteLibrary assuranceLib = (ResoluteLibrary) ((DefaultAnnexLibrary) annexLibrary)
				.getParsedAnnexLibrary();
		for (Definition def : assuranceLib.getDefinitions()) {
			if (def.getName().equalsIgnoreCase("security_requirements_satisfied_in_implementation")) {
				reqSatisfiedInImplExpr.setFn((FunctionDefinition) def);
				break;
			}
		}
		final IdExpr argExpr = ResoluteFactory.eINSTANCE.createIdExpr();
		argExpr.setId(arg);
		reqSatisfiedInImplExpr.getArgs().add(argExpr);
		andExpr.setRight(reqSatisfiedInImplExpr);
		claimBody.setExpr(andExpr);
		requirementsSatisfiedGoal.setBody(claimBody);
		return requirementsSatisfiedGoal;
	}

	public static FunctionDefinition createTopLevelGoal(Classifier system,
			FunctionDefinition requirementsSatisfiedGoal) {

		final FunctionDefinition topLevelGoal = ResoluteFactory.eINSTANCE.createFunctionDefinition();
		topLevelGoal.setName(system.getName().replace(".", "_") + "_cyber_resilient");
		topLevelGoal.setClaimType("goal");
		final Arg arg = ResoluteFactory.eINSTANCE.createArg();
		final BaseType sysType = ResoluteFactory.eINSTANCE.createBaseType();
		sysType.setType("system");
		arg.setName("sys");
		arg.setType(sysType);
		topLevelGoal.getArgs().add(arg);
		final ClaimBody claimBody = ResoluteFactory.eINSTANCE.createClaimBody();
		final ClaimString claimStr1 = ResoluteFactory.eINSTANCE.createClaimString();
		claimStr1.setStr("The ");
		claimBody.getClaim().add(claimStr1);
		final ClaimArg claimArg = ResoluteFactory.eINSTANCE.createClaimArg();
		claimArg.setArg(arg);
		claimBody.getClaim().add(claimArg);
		final ClaimString claimStr2 = ResoluteFactory.eINSTANCE.createClaimString();
		claimStr2.setStr(" is acceptably cyber-resilient");
		claimBody.getClaim().add(claimStr2);
		final ClaimStrategy claimStrategy = ResoluteFactory.eINSTANCE.createClaimStrategy();
		final StringLiteral strategyVal = Aadl2Factory.eINSTANCE.createStringLiteral();
		strategyVal.setValue("\"Argue over security requirements\"");
		claimStrategy.setVal(strategyVal);
		claimBody.getAttributes().add(claimStrategy);
		final FnCallExpr requirementsComplete = ResoluteFactory.eINSTANCE.createFnCallExpr();
		final AadlPackage assurancePkg = getCaseAssuracePackage(system.eResource().getResourceSet());
		final PackageSection packageSection = assurancePkg.getOwnedPrivateSection();
		final AnnexLibrary annexLibrary = packageSection.getOwnedAnnexLibraries().get(0);
		final ResoluteLibrary assuranceLib = (ResoluteLibrary) ((DefaultAnnexLibrary) annexLibrary)
				.getParsedAnnexLibrary();
		for (Definition def : assuranceLib.getDefinitions()) {
			if (def.getName().equalsIgnoreCase("security_requirements_complete")) {
				requirementsComplete.setFn((FunctionDefinition) def);
				break;
			}
		}
		final IdExpr argExpr = ResoluteFactory.eINSTANCE.createIdExpr();
		argExpr.setId(arg);
		requirementsComplete.getArgs().add(argExpr);
		final FnCallExpr requirementsSatisfied = ResoluteFactory.eINSTANCE.createFnCallExpr();
		requirementsSatisfied.setFn(requirementsSatisfiedGoal);
		requirementsSatisfied.getArgs().add(EcoreUtil.copy(argExpr));
		final BinaryExpr andExpr = ResoluteFactory.eINSTANCE.createBinaryExpr();
		andExpr.setOp("and");
		andExpr.setLeft(requirementsComplete);
		andExpr.setRight(requirementsSatisfied);
		claimBody.setExpr(andExpr);
		topLevelGoal.setBody(claimBody);

		return topLevelGoal;

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

	public static IFile getCaseRequirementsFile(IProject project) {
//		final IFile caseReqFile = TraverseProject.getCurrentProject().getFile(CASE_REQUIREMENTS_FILE);
		if (project == null) {
			return null;
		}
		final IFile caseReqFile = project.getFile(CASE_REQUIREMENTS_FILE);
		if (!caseReqFile.exists()) {
			initCaseRequirementsPackage();
		}
		return caseReqFile;
	}

	public static void formatCaseRequirements() {

		// First check if CASE_Requirements is already open
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		final IEditorPart activeEditor = window.getActivePage().getActiveEditor();
		for (IEditorReference ref : window.getActivePage().getEditorReferences()) {
			final IEditorPart editor = ref.getEditor(false);
			if (editor == null) {
				continue;
			} else if (editor instanceof XtextEditor
					&& editor.getTitle().equalsIgnoreCase(CASE_REQUIREMENTS_NAME + ".aadl")) {
				((SourceViewer) ((XtextEditor) editor).getInternalSourceViewer()).doOperation(ISourceViewer.FORMAT);

				if (editor.isDirty()) {
					editor.doSave(new NullProgressMonitor());
				}

				return;
			}
		}

		// CASE_Requirements isn't open, so open, format, save, and close it
		final IFile file = getCaseRequirementsFile(TraverseProject.getCurrentProject());
		IWorkbenchPage page = null;
		IEditorPart part = null;
		IEditorReference editorRef = null;

		if (file != null && file.exists()) {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final IEditorDescriptor desc = PlatformUI.getWorkbench()
					.getEditorRegistry()
					.getDefaultEditor(file.getName());
			try {
				part = page.openEditor(new FileEditorInput(file), desc.getId());

				for (IEditorReference editor : page.getEditorReferences()) {
					if (editor.getEditor(false) == part) {
						editorRef = editor;
						page.hideEditor(editor);
						break;
					}
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

		if (part != null && part instanceof XtextEditor) {

			((SourceViewer) ((XtextEditor) part).getInternalSourceViewer()).doOperation(ISourceViewer.FORMAT);
			((XtextEditor) part).doSave(null);

			if (editorRef != null) {
				page.showEditor(editorRef);
			}

			((XtextEditor) part).close(false);
		}

		if (activeEditor != null) {
			activeEditor.setFocus();
		}
	}

	/**
	 * Imports CASE_Model_Transformations to CASE_Requirements
	 * @return
	 */
	public void importModelTransformationsToRequirementsPackage() {
		final AadlPackage contextPkg = getCaseRequirementsPackage();
		if (contextPkg == null) {
			throw new RuntimeException("Could not find CASE_Requirements.");
		}

		PrivatePackageSection priv8 = contextPkg.getOwnedPrivateSection();
		if (priv8 == null) {
			priv8 = contextPkg.createOwnedPrivateSection();
		}

		if (!CaseUtils.addCaseModelTransformationsImport(priv8, false)) {
			throw new RuntimeException("Could not import CASE_Model_Transformations package.");
		}
	}

}
