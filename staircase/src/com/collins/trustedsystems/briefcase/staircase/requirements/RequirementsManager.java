package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.util.ModelHashcode;
import com.collins.trustedsystems.briefcase.util.TraverseProject;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimString;
import com.rockwellcollins.atc.resolute.resolute.ClaimText;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.DefinitionBody;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;

/**
 *  Manages insertion, deletion, and modification of logical CASE requirements/claims in the model
 */
public class RequirementsManager {

	private static IProject currentProject = null;
	// Singleton instance
	private static RequirementsManager instance = null;
	private final RequirementsDatabaseHelper reqDb = new RequirementsDatabaseHelper();

	public static RequirementsManager getInstance() {
		if (instance == null || currentProject == null) {
			instance = new RequirementsManager();
		} else if (currentProject != TraverseProject.getCurrentProject()) {
			instance = new RequirementsManager();
		}
		return instance;
	}

	private static XtextEditor activeEditor = null;

	public static void closeEditor(XtextEditor editor, boolean save) {
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (save) {
			page.saveEditor(editor, false);
		}

		if (editor.equals(activeEditor)) {
			return;
		} else {
			page.closeEditor(editor, false);
		}
	}

	public static XtextEditor getEditor(IFile file) {
		IWorkbenchPage page = null;
		IEditorPart part = null;

		activeEditor = EditorUtils.getActiveXtextEditor();

		if (file.exists()) {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
					.getDefaultEditor(file.getName());
			try {
				part = page.openEditor(new FileEditorInput(file), desc.getId());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

		if (part == null) {
			return null;
		}

		return (XtextEditor) part;

	}

	private RequirementsManager() {
		// Initialize requirements list

		if (currentProject == null) {
			currentProject = TraverseProject.getCurrentProject();
		}

		// Read in any existing imported requirements
		reqDb.importRequirements(findImportedRequirements());
	}

	public void clearRequirements() {
		reqDb.reset();
		reqDb.importRequirements(findImportedRequirements());
	}


	public List<CyberRequirement> getImportedRequirements() {
		return reqDb.getImportedRequirements();
	}

	public CyberRequirement getRequirement(String requirementId) {
		return reqDb.get(requirementId);
	}

	public List<CyberRequirement> getRequirements() {
		return reqDb.getRequirements();
	}

	public void modifyRequirement(String reqId, BuiltInClaim claim) {
		final CyberRequirement req = getRequirement(reqId);
		if (req != null) {
			req.updateClaimDefinition(claim);
			CaseUtils.formatCaseRequirements();
		}
	}


	/**
	 * Updates the imported requirements list by either modifying existing requirements
	 * or adding them if they're new
	 * @param updatedReqs - list of requirements to add/update
	 */
	public void updateRequirements(List<CyberRequirement> updatedReqs) {

		for (CyberRequirement r : updatedReqs) {
			final CyberRequirement existing = reqDb.get(r);
			if (existing == null) {
				// not possible; signal error
				throw new RuntimeException("Updated requirement not found in requirements database : " + r);
			} else {
				if (existing.getStatus() == CyberRequirement.toDo || existing.getStatus() == CyberRequirement.omit) {
					switch (r.getStatus()) {
					case CyberRequirement.toDo:
					case CyberRequirement.omit:
						// do nothing
						break;
					case CyberRequirement.add:
						// add to model
						System.out.println("Requirement " + r.getId());
						r.insertClaimDefinition();
						r.insertClaimCall();
						if (r.getFormalize()) {
							r.insertAgree();
						}
						break;
					default:
						// Unknown status; signal error
						throw new RuntimeException("Updated requirement has invalid status : " + r);
					}
				} else if (existing.getStatus() == CyberRequirement.add) {
					switch (r.getStatus()) {
					case CyberRequirement.toDo:
					case CyberRequirement.omit:
						// remove resolute claim definition and claim call
						r.removeClaimCall();
						r.removeClaimDefinition();
						if (existing.getFormalize()) {
							r.removeAgree();
						}
						break;
					case CyberRequirement.add:
						if (existing.getFormalize() && !r.getFormalize()) {
							r.removeAgree();
						} else if (!existing.getFormalize() && r.getFormalize()) {
							r.insertAgree();
						}
						break;
					default:
						// Unknown status; signal error
						throw new RuntimeException("Updated requirement has invalid status : " + r);
					}
				} else {
					// Unknown status; signal error
					throw new RuntimeException("Existing requirement has invalid status : " + existing);
				}
			}
		}

		updatedReqs.forEach(r -> reqDb.updateRequirement(r));
		reqDb.saveRequirementsDatabase();
		CaseUtils.formatCaseRequirements();
	}

	public boolean readRequirementFiles(boolean importRequirements, String filename) {
		final List<CyberRequirement> existing = findImportedRequirements();
		final Set<String> reqIds = new HashSet<String>();
		for (CyberRequirement r : existing) {
			if (r.getId() != null && !r.getId().isEmpty() && !reqIds.add(r.getId())) {
				Dialog.showError("Error in Claims file",
						"Duplicate requirement ID found in claims file " + r.getId() + ".");
				return false;
			}
		}

		if (importRequirements) {
			final List<JsonRequirementsFile> jsonReqFiles = readInputFiles(filename);
			if (jsonReqFiles.isEmpty()) {
				return false;
			}
			reqDb.reset();
			reqDb.importJsonRequirementsFiles(jsonReqFiles);
		}
		reqDb.importRequirements(existing);
		// TODO: If the user cancels importing requirements the reqdb will may not be correct
		// Commenting out following line should be ok
//		reqDb.saveRequirementsDatabase();

		return true;
	}

	public List<CyberRequirement> findImportedRequirements() {

		final IFile file = CaseUtils.getCaseRequirementsFile();
		final XtextEditor editor = getEditor(file);

		if (editor != null) {
			final List<CyberRequirement> embeddedRequirements = editor.getDocument().readOnly(resource -> {

				// Get modification context
				final AadlPackage aadlPkg = CaseUtils.getCaseRequirementsPackage();
				if (aadlPkg == null) {
					return Collections.emptyList();
				}

				final PrivatePackageSection priv8 = aadlPkg.getOwnedPrivateSection();
				if (priv8 == null) {
					throw new RuntimeException("Could not find private package section for " + aadlPkg);
				}

				ResoluteLibrary resLib = null;
				for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
					if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
						resLib = (ResoluteLibrary) ((DefaultAnnexLibrary) library).getParsedAnnexLibrary();
						break;
					}
				}

				if (resLib == null) {
					return Collections.emptyList();
				}

				// If this function definition already exists, remove it
				final List<CyberRequirement> resoluteClauses = new ArrayList<>();
				for (Definition def : resLib.getDefinitions()) {
					if (def instanceof FunctionDefinition) {
						final FunctionDefinition fd = (FunctionDefinition) def;
						final DefinitionBody db = fd.getBody();
						if (db instanceof ClaimBody) {
							String reqClaimString = "";
							final ClaimBody cb = (ClaimBody) db;
							for (ClaimText ct : cb.getClaim()) {
								if (ct instanceof ClaimString) {
									reqClaimString += ((ClaimString) ct).getStr();
								}
							}
							// Annotate claim with requirement information
							final CyberRequirement r = CyberRequirement.parseClaimString(fd.getName(), reqClaimString,
									cb);
							if (r != null) {
								resoluteClauses.add(r);
							}
						}
					}
				}

				return resoluteClauses;
			});

			// Close editor, if necessary (no saving, read-only)
			closeEditor(editor, false);

			return embeddedRequirements;
		}

		return new ArrayList<CyberRequirement>();
	}


	protected List<JsonRequirementsFile> readInputFiles(String filename) {
		final List<JsonRequirementsFile> reqs = new ArrayList<>();
		final List<String> filenames = new ArrayList<String>();
		String filterPath = null;

		// If a filename was passed to this command, open the file.
		// Otherwise, prompt the user for the file
		// TODO: only allow a single file to be selected?
		if (filename == null || filename.isEmpty()) {
			final FileDialog dlgReqFile = new FileDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					SWT.MULTI);
			dlgReqFile.setText("Select requirements file to import.");
			dlgReqFile.open();
			for (String fn : dlgReqFile.getFileNames()) {
				filenames.add(fn);
			}
			filterPath = dlgReqFile.getFilterPath();
		} else {
			filenames.add(filename);
		}

		for (String fn : filenames) {
			final File reqFile = new File(filterPath, fn);
			if (!reqFile.exists()) {
				Dialog.showError("File not found",
						"Cannot find the requirements file " + reqFile.getAbsolutePath() + ".");
				continue;
			}
			final JsonRequirementsFile jsonFile = new JsonRequirementsFile();
			if (!jsonFile.importFile(reqFile)) {
				Dialog.showError("Problem with " + reqFile.getName(),
						"Could not load cyber requirements file " + reqFile.getName() + ".");
				continue;
			}
			// Alert user if there aren't any requirements to import
			if (jsonFile.getRequirements().isEmpty()) {
				Dialog.showError("No new requirements to import", reqFile.getName()
						+ " does not contain any requirements that are not already present in this model.");
				continue;
			}

			// Alert user if the model has changed since the requirements were generated
			try {
				final ComponentImplementation ci = reqDb
						.getComponentImplementationInCurrentEditor(jsonFile.getImplementation());
				if (ci == null || !jsonFile.getHashcode().contentEquals(ModelHashcode.getHashcode(ci))) {
					throw new Exception();
				}
			} catch (Exception e) {
				if (!Dialog.askQuestion("Import Requirements",
						"The model has changed since requirements in file " + reqFile.getName()
								+ " were generated, and therefore may no longer be applicable.  Import anyway?")) {
					continue;
				}
			}

			// Add the requirements in this file to the accumulated list of requirements
			reqs.add(jsonFile);
		}

		return reqs;
	}


//	/**
//	 * Requirements Database helper class.
//	 *
//	 * <p>Its purpose is to provide a central entity to keep track of
//	 * requirements generated via TA1 tools,
//	 * requirements imported into the AADL models,
//	 * and requirements saved to the disk.
//	 *
//	 * <p>It provides quick insertion, searching and updating of requirements,
//	 * as well a mechanism to modify a set of requirements in the AADL model.
//	 *
//	 * <p>It is expected to be used as a singleton class by the enclosing RequirementsManager singleton instance.
//	 *
//	 * @author Junaid Babar
//	 *
//	 */
//	public class RequirementsDatabaseHelper {
//
//		private HashMap<String, CyberRequirement> requirements;
//		private HashMap<String, String> idToKey;
//		private String analysisOutputFilename = "";
//
//		private String makeKey(CyberRequirement req) {
//			return req.getType() + req.getContext();
//		}
//
//		private String makeKey(String type, String context) {
//			return type + context;
//		}
//
//		public RequirementsDatabaseHelper() {
//			requirements = new HashMap<String, CyberRequirement>();
//			idToKey = new HashMap<String, String>();
//			readRequirementsDatabase();
//		}
//
//		public void reset() {
//			requirements.clear();
//			idToKey.clear();
//			analysisOutputFilename = "";
//		}
//
//		public void readRequirementsDatabase() {
//			// Read database from the physical requirements database file
//			final IPath reqFilePath = TraverseProject.getCurrentProject()
//					.getFile(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE).getLocation();
//			File reqFile = null;
//			if (reqFilePath != null) {
//				reqFile = reqFilePath.toFile();
//			}
//			final JsonRequirementsFile jsonReqFile = new JsonRequirementsFile();
//			if (!reqFile.exists() || !jsonReqFile.importFile(reqFile)) {
////				Dialog.showInfo("Missing requirements database",
////						"No requirements database found. Starting a new database.");
//			} else {
//				// Add the requirements in this file to the accumulated list of requirements
//				importRequirements(jsonReqFile.getRequirements());
//			}
//		}
//
//		public void saveRequirementsDatabase() {
//			// Write database to physical requirements database file
//			if (requirements.isEmpty()) {
//				return;
//			}
//			final IPath reqFilePath = TraverseProject.getCurrentProject()
//					.getFile(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE).getLocation();
//			File reqFile = null;
//			if (reqFilePath != null) {
//				reqFile = reqFilePath.toFile();
//			}
//			final JsonRequirementsFile jsonReqFile = new JsonRequirementsFile(CyberRequirement.notApplicable,
//					new Date().getTime(), CyberRequirement.notApplicable, CyberRequirement.notApplicable,
//					analysisOutputFilename, getRequirements());
//			if (!jsonReqFile.exportFile(reqFile)) {
//				throw new RuntimeException("Could not save cyber requirements file " + reqFile.getName() + ".");
//			}
//		}
//
//		public void importJsonRequrementsFiles(List<JsonRequirementsFile> reqFiles) {
//			reqFiles.forEach(file -> importJsonRequrementsFile(file));
//		}
//
//		public void importJsonRequrementsFile(JsonRequirementsFile reqFile) {
//
//			analysisOutputFilename = reqFile.getFilename();
//
//			// Prepending the qualified name of the top-level implementation to each requirement's context
//			// before calling importRequirements
//
//			// Make changes to the requirements' contexts
//			final List<CyberRequirement> reqs = reqFile.getRequirements();
//			final ComponentImplementation ci = getComponentImplementationInCurrentEditor(reqFile.getImplementation());
//			if (ci == null) {
//				throw new RuntimeException(
//				"Unknown top-level component implementation referred by import requirements file: " + reqFile.getImplementation());
//			}
//
//			reqs.forEach(e -> e.setContext(ci.getQualifiedName() + "." + e.getContext()));
//
//			// Import requirements
//			importRequirements(reqs);
//		}
//
//		public void importRequirements(List<CyberRequirement> reqs) {
//			// Add reqs to the database of requirements
//			// Note: if requirement already exists, it will get updated
//			reqs.forEach(e -> importRequirement(e));
//		}
//
//		public ComponentImplementation getComponentImplementationInCurrentEditor(String implInstanceName) {
//			// implInstanceName example:
//			// topLevelImplementation_Impl_Instance
//			final String[] tokens = implInstanceName.split("_");
//			if (tokens.length < 3) {
//				return null;
//			}
//			String topLevelImplName = tokens[tokens.length - 3] + "." + tokens[tokens.length - 2];
//			for (int i = tokens.length - 4; i >= 0; --i) {
//				topLevelImplName = tokens[i] + "_" + topLevelImplName;
//			}
//
//			final XtextEditor editor = (XtextEditor) TraverseProject.getCurrentEditor();
//			if (editor != null) {
//				final IFile file = (IFile) editor.getResource();
//				final AadlPackage pkg = TraverseProject.getPackageInFile(file);
//				for (NamedElement e : pkg.getOwnedPublicSection().getOwnedClassifiers()) {
//					if (e instanceof ComponentImplementation && e.getName().equalsIgnoreCase(topLevelImplName)) {
//						System.out.println("Found complement implementation: " + e.getQualifiedName());
//						return (ComponentImplementation) e;
//					}
//				}
//			}
//			return null;
//		}
//
//		public void importRequirement(CyberRequirement req) {
//			// Add req to the database of requirements
//			// Note: if requirement already exists, it will get updated
//			updateRequirement(req);
//		}
//
//		public void removeToDos() {
//			getToDoRequirements().forEach(req -> {
//				requirements.remove(makeKey(req));
//				if (req.getId() != null && !req.getId().isEmpty()) {
//					idToKey.remove(req.getId());
//				}
//			});
//		}
//
//		public void updateRequirement(CyberRequirement req) {
//			final String key = makeKey(req);
//			requirements.put(key, req);
//			if (req.getId() != null && !req.getId().isEmpty()) {
//				idToKey.put(req.getId(), key);
//			}
//		}
//
//		public boolean contains(CyberRequirement req) {
//			return requirements.containsKey(makeKey(req));
//		}
//
//		public boolean contains(String type, String context) {
//			return requirements.containsKey(makeKey(type, context));
//		}
//
//		public boolean contains(String reqId) {
//			return idToKey.containsKey(reqId);
//		}
//
//		public CyberRequirement get(CyberRequirement req) {
//			return requirements.get(makeKey(req));
//		}
//
//		public CyberRequirement get(String type, String context) {
//			return requirements.get(makeKey(type, context));
//		}
//
//		public CyberRequirement get(String reqId) {
//			if (contains(reqId)) {
//				return requirements.get(idToKey.get(reqId));
//			}
//			return null;
//		}
//
//		public List<CyberRequirement> getRequirements() {
//			final List<CyberRequirement> list = new ArrayList<CyberRequirement>();
//			requirements.values().forEach(e -> list.add(new CyberRequirement(e)));
//			return list;
//		}
//
//		private List<CyberRequirement> getRequirements(final String filterString) {
//			final List<CyberRequirement> list = new ArrayList<CyberRequirement>();
//			requirements.values().forEach(r -> {
//				// Using "==" instead of equals because filterString is one of three constants
//				if (r.getStatus() == filterString) {
//					list.add(new CyberRequirement(r));
//				}
//			});
//			return list;
//		}
//
//		public final List<CyberRequirement> getOmittedRequirements() {
//			return getRequirements(CyberRequirement.omit);
//		}
//
//		public List<CyberRequirement> getToDoRequirements() {
//			return getRequirements(CyberRequirement.toDo);
//		}
//
//		public List<CyberRequirement> getAddRequirements() {
//			return getRequirements(CyberRequirement.add);
//		}
//
//		public List<CyberRequirement> getImportedRequirements() {
//			final List<CyberRequirement> list = getAddRequirements();
//			return list;
//		}
//	}
}
