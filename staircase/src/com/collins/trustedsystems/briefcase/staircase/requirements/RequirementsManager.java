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
import org.eclipse.ui.PlatformUI;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
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
	private static RequirementsDatabaseHelper reqDb = null;

	public static RequirementsManager getInstance() {
		if (instance == null || currentProject == null) {
			instance = new RequirementsManager();
		} else if (currentProject != TraverseProject.getCurrentProject()) {
			instance = new RequirementsManager();
		}
		return instance;
	}

	private RequirementsManager() {

		currentProject = TraverseProject.getCurrentProject();

		reqDb = new RequirementsDatabaseHelper(currentProject);

		// Read in any existing imported requirements
		reqDb.importRequirements(findImportedRequirements(currentProject));
	}

	public void clearRequirements() {
		reqDb.reset();
		reqDb.importRequirements(findImportedRequirements(currentProject));
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
	public boolean updateRequirements(List<CyberRequirement> updatedReqs) {

		final Set<String> implementationClassifiers = new HashSet<>();
		for (CyberRequirement r : updatedReqs) {
			try {
				final String[] parts = r.getContext().split("\\.");
				// Capture just the implementation name
				String classifierQualifiedName = parts[0];
				if (parts.length > 1) {
					classifierQualifiedName += "." + parts[1];
				}
				implementationClassifiers.add(classifierQualifiedName);
				final CyberRequirement existing = reqDb.get(r);
				if (existing == null) {
					// not possible; signal error
					throw new RuntimeException("Updated requirement not found in requirements database : " + r);
				} else {
					if (CyberRequirement.toDo.equals(existing.getStatus())
							|| CyberRequirement.omit.equals(existing.getStatus())) {
						switch (r.getStatus()) {
						case CyberRequirement.toDo:
						case CyberRequirement.omit:
							// do nothing
							break;
						case CyberRequirement.add:
							// add to model
							System.out.println("Requirement " + r.getId());
							r.insertClaimDefinition();
							if (r.getFormalize()) {
								r.insertAgree();
							}
							break;
						default:
							// Unknown status; signal error
							throw new RuntimeException("Updated requirement has invalid status : " + r);
						}
					} else if (CyberRequirement.add.equals(existing.getStatus())) {
						switch (r.getStatus()) {
						case CyberRequirement.toDo:
						case CyberRequirement.omit:
							if (r.isMitigated() && !Dialog.askQuestion("Requirements Manager", "Requirement "
									+ r.getId()
									+ " has already been mitigated in the model.  Removing this requirement will not undo the mitigation.  Remove anyway?")) {
								continue;
							}
							// remove resolute claim definition and claim call
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
				reqDb.updateRequirement(r);
			} catch (Exception e) {
				BriefcaseNotifier.printError(e.getMessage());
				return false;
			}
		}
		for (String implementationClassifier : implementationClassifiers) {
			CyberRequirement.updateClaimCall(implementationClassifier);
		}
		reqDb.saveRequirementsDatabase();
		CaseUtils.formatCaseRequirements();
		return true;
	}


	public boolean readRequirementFiles(boolean importRequirements, String filename) {
		final List<CyberRequirement> existing = findImportedRequirements(currentProject);
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

		return true;
	}

	public static List<CyberRequirement> findImportedRequirements(IProject project) {

		final IFile file = CaseUtils.getCaseRequirementsFile(project);

		// Get modification context
		final AadlPackage aadlPkg = TraverseProject.getPackageInFile(file);
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
					final CyberRequirement r = CyberRequirement.parseClaimString(fd.getName(), reqClaimString, cb);
					if (r != null) {
						resoluteClauses.add(r);
					}
				}
			}
		}

		return resoluteClauses;

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
			final String[] filterExts = { "*.json", "*.txt", "*.*" };
			dlgReqFile.setFilterExtensions(filterExts);
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

}
