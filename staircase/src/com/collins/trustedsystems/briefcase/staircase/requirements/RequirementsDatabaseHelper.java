package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.NamedElement;

import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.util.TraverseProject;

/**
 * Requirements Database helper class.
 *
 * <p>Its purpose is to provide a central entity to keep track of
 * requirements generated via TA1 tools,
 * requirements imported into the AADL models,
 * and requirements saved to the disk.
 *
 * <p>It provides quick insertion, searching and updating of requirements,
 * as well a mechanism to modify a set of requirements in the AADL model.
 *
 * @author Junaid Babar
 *
 */
public class RequirementsDatabaseHelper {

	private HashMap<String, CyberRequirement> requirements;
	private HashMap<String, String> idToKey;
	private String analysisOutputFilename = "";
	private IProject currentProject = null;

	private String makeKey(CyberRequirement req) {
		return req.getType() + req.getContext();
	}

	private String makeKey(String type, String context) {
		return type + context;
	}

	public RequirementsDatabaseHelper() {
		requirements = new HashMap<String, CyberRequirement>();
		idToKey = new HashMap<String, String>();
		currentProject = TraverseProject.getCurrentProject();
		readRequirementsDatabase();
	}

	public RequirementsDatabaseHelper(IProject currentProject) {
		requirements = new HashMap<String, CyberRequirement>();
		idToKey = new HashMap<String, String>();
		this.currentProject = currentProject;
		readRequirementsDatabase();
	}

	public void reset() {
		requirements.clear();
		idToKey.clear();
		analysisOutputFilename = "";
	}

	public void readRequirementsDatabase() {
		// Read database from the physical requirements database file
		final IPath reqFilePath = currentProject
				.getFile(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE)
				.getLocation();
		File reqFile = null;
		if (reqFilePath != null) {
			reqFile = reqFilePath.toFile();
		}
//		final JsonRequirementsFile jsonReqFile = new JsonRequirementsFile();
//		if (!reqFile.exists() || !jsonReqFile.importFile(reqFile)) {
////			Dialog.showInfo("Missing requirements database",
////					"No requirements database found. Starting a new database.");
//		} else {
//			// Add the requirements in this file to the accumulated list of requirements
//			importRequirements(jsonReqFile.getRequirements());
//			analysisOutputFilename = jsonReqFile.getFilename();
//		}

		final RequirementsDatabaseFile reqDbFile = new RequirementsDatabaseFile();
		if (reqFile.exists() && reqDbFile.importFile(reqFile)) {
			// Add the requirements in this file to the accumulated list of requirements
			importRequirements(reqDbFile.getRequirements());
			analysisOutputFilename = reqDbFile.getFilename();
		}
	}

	public void saveRequirementsDatabase() {
		// Write database to physical requirements database file
		if (requirements.isEmpty()) {
			return;
		}
		final IPath reqFilePath = currentProject
				.getFile(CaseUtils.CASE_REQUIREMENTS_DATABASE_FILE)
				.getLocation();
		File reqFile = null;
		if (reqFilePath != null) {
			reqFile = reqFilePath.toFile();
		}
//		final JsonRequirementsFile jsonReqFile = new JsonRequirementsFile(CyberRequirement.notApplicable,
//				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss")),
//				CyberRequirement.notApplicable, CyberRequirement.notApplicable,
//				analysisOutputFilename, getRequirements());
//		if (!jsonReqFile.exportFile(reqFile)) {
//			throw new RuntimeException("Could not save cyber requirements file " + reqFile.getName() + ".");
//		}
		final RequirementsDatabaseFile reqDbFile = new RequirementsDatabaseFile(
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss")), analysisOutputFilename,
				getRequirements());
		if (!reqDbFile.exportFile(reqFile)) {
			throw new RuntimeException("Could not save requirements database " + reqFile.getName() + ".");
		}
	}

	public void importJsonRequirementsFiles(List<JsonRequirementsFile> reqFiles) {
		reqFiles.forEach(file -> importJsonRequirementsFile(file));
	}

	public void importJsonRequirementsFile(JsonRequirementsFile reqFile) {

		analysisOutputFilename = reqFile.getFilename();

		// Prepending the qualified name of the top-level implementation to each requirement's context
		// before calling importRequirements

		// Make changes to the requirements' contexts
		final List<CyberRequirement> reqs = reqFile.getRequirements();
		final ComponentImplementation ci = getComponentImplementationInCurrentEditor(reqFile.getImplementation());
		if (ci == null) {
			throw new RuntimeException(
					"Unknown top-level component implementation referred by import requirements file: "
							+ reqFile.getImplementation());
		}

		reqs.forEach(e -> e.setContext(ci.getQualifiedName() + "." + e.getContext()));

		// Import requirements
		importRequirements(reqs);
	}

	public void importRequirements(List<CyberRequirement> reqs) {
		// Add reqs to the database of requirements
		// Note: if requirement already exists, it will get updated
		reqs.forEach(e -> importRequirement(e));
	}

	public ComponentImplementation getComponentImplementationInCurrentEditor(String implInstanceName) {
		// implInstanceName example:
		// topLevelImplementation_Impl_Instance
		final String[] tokens = implInstanceName.split("_");
		if (tokens.length < 3) {
			return null;
		}
		String topLevelImplName = tokens[tokens.length - 3] + "." + tokens[tokens.length - 2];
		for (int i = tokens.length - 4; i >= 0; --i) {
			topLevelImplName = tokens[i] + "_" + topLevelImplName;
		}

		final XtextEditor editor = (XtextEditor) TraverseProject.getCurrentEditor();
		if (editor != null) {
			final IFile file = (IFile) editor.getResource();
			final AadlPackage pkg = TraverseProject.getPackageInFile(file);
			for (NamedElement e : pkg.getOwnedPublicSection().getOwnedClassifiers()) {
				if (e instanceof ComponentImplementation && e.getName().equalsIgnoreCase(topLevelImplName)) {
					System.out.println("Found complement implementation: " + e.getQualifiedName());
					return (ComponentImplementation) e;
				}
			}
		}
		return null;
	}

	public void importRequirement(CyberRequirement req) {
		// Add req to the database of requirements
		// Note: if requirement already exists, it will get updated
		updateRequirement(req);
	}

	public void removeToDos() {
		getToDoRequirements().forEach(req -> {
			requirements.remove(makeKey(req));
			if (req.getId() != null && !req.getId().isEmpty()) {
				idToKey.remove(req.getId());
			}
		});
	}

	public void updateRequirement(CyberRequirement req) {
		final String key = makeKey(req);
		requirements.put(key, req);
		if (req.getId() != null && !req.getId().isEmpty()) {
			idToKey.put(req.getId(), key);
		}
	}

	public boolean contains(CyberRequirement req) {
		return requirements.containsKey(makeKey(req));
	}

	public boolean contains(String type, String context) {
		return requirements.containsKey(makeKey(type, context));
	}

	public boolean contains(String reqId) {
		return idToKey.containsKey(reqId);
	}

	public CyberRequirement get(CyberRequirement req) {
		return requirements.get(makeKey(req));
	}

	public CyberRequirement get(String type, String context) {
		return requirements.get(makeKey(type, context));
	}

	public CyberRequirement get(String reqId) {
		if (contains(reqId)) {
			return requirements.get(idToKey.get(reqId));
		}
		return null;
	}

	public List<CyberRequirement> getRequirements() {
		final List<CyberRequirement> list = new ArrayList<CyberRequirement>();
		requirements.values().forEach(e -> list.add(new CyberRequirement(e)));
		return list;
	}

	private List<CyberRequirement> getRequirements(final String filterString) {
		final List<CyberRequirement> list = new ArrayList<CyberRequirement>();
		requirements.values().forEach(r -> {
			// Using "==" instead of equals because filterString is one of three constants
			if (r.getStatus() == filterString) {
				list.add(new CyberRequirement(r));
			}
		});
		return list;
	}

	public final List<CyberRequirement> getOmittedRequirements() {
		return getRequirements(CyberRequirement.omit);
	}

	public List<CyberRequirement> getToDoRequirements() {
		return getRequirements(CyberRequirement.toDo);
	}

	public List<CyberRequirement> getAddRequirements() {
		return getRequirements(CyberRequirement.add);
	}

	public List<CyberRequirement> getImportedRequirements() {
		return getAddRequirements();
	}

	public String getAnalysisOutputFilename() {
		return analysisOutputFilename;
	}

}
