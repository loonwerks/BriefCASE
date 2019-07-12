package com.collins.fmw.cyres.architecture.requirements;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.aadl2.util.Aadl2Util;
import org.osate.ui.dialogs.Dialog;

import com.collins.fmw.cyres.util.plugin.Filesystem;
import com.collins.fmw.cyres.util.plugin.TraverseProject;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreeFactory;
import com.rockwellcollins.atc.agree.agree.NamedSpecStatement;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.agree.parsing.AgreeAnnexParser;
import com.rockwellcollins.atc.resolute.resolute.AnalysisStatement;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;

public class CyberRequirement {

	private String type = ""; // this is the requirement classification type as defined by the TA1 tool
	private String id = "";
	private String text = "";
	private String context = ""; // this is the qualified name of the component
	private boolean agree = false;
	private String rationale = "";

	public CyberRequirement(String type) {
		this.type = type;
	}

	public CyberRequirement(String type, String id, String text, String context, boolean agree, String rationale) {
		this.type = type;
		this.id = id;
		this.text = text;
		this.context = context;
		this.agree = agree;
		this.rationale = rationale;
	}

	public String getType() {
		return this.type;
	}

	public String getId() {
		return this.id;
	}

	public String getText() {
		return this.text;
	}

	public String getContext() {
		return this.context;
	}

	public boolean hasAgree() {
		return this.agree;
	}

	public void setAgree() {
		this.agree = true;
	}

	public void insertClaim(BuiltInClaim claim, Resource resource) {

		if (claim == null) {
			return;
		}

		// Get modification context
		Classifier implementationContext = getImplementationClassifier(this.context);
		if (implementationContext == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}
		Classifier modificationContext = getModificationContext(implementationContext.getQualifiedName(), resource);
		if (modificationContext == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}

		FunctionDefinition currentClaimDef = getClaimDefinition(modificationContext);
		FunctionDefinition newClaimDef = claim.buildClaimDefinition(currentClaimDef);

		// Insert it into model
		insertClaimDefinition(modificationContext, newClaimDef);

		// Build Claim Call
		ProveStatement newClaimCall = null;
		if (newClaimDef != null) {
			ProveStatement currentClaimCall = getClaimCall(modificationContext);
			newClaimCall = claim.buildClaimCall(currentClaimCall);

			if (newClaimCall == null) {
				throw new RuntimeException("Unable to generate the claim call.");
			}

			// Insert it into model
			insertClaimCall(modificationContext, newClaimDef, newClaimCall);

		}

	}

	private void insertClaimDefinition(Classifier modificationContext, FunctionDefinition claimDefinition) {

		if (modificationContext == null) {
			return;
		}

		AadlPackage pkg = AadlUtil.getContainingPackage(modificationContext);
		if (pkg == null) {
			throw new RuntimeException("Could not find containing package for " + modificationContext);
		}

		PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
		if (priv8 == null) {
			priv8 = pkg.createOwnedPrivateSection();
		}

		DefaultAnnexLibrary defResLib = null;
		ResoluteLibrary resLib = null;
		for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
			if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
				defResLib = (DefaultAnnexLibrary) library;
				resLib = EcoreUtil.copy((ResoluteLibrary) defResLib.getParsedAnnexLibrary());
				break;
			}
		}

		if (defResLib == null) {
			defResLib = (DefaultAnnexLibrary) priv8
					.createOwnedAnnexLibrary(Aadl2Package.eINSTANCE.getDefaultAnnexLibrary());
			defResLib.setName("resolute");
			defResLib.setSourceText("{** **}");

			resLib = ResoluteFactory.eINSTANCE.createResoluteLibrary();
		}

		// If this function definition already exists, remove it
		Iterator<Definition> i = resLib.getDefinitions().iterator();
		while (i.hasNext()) {
			Definition def = i.next();
			if (def.getName().equalsIgnoreCase(claimDefinition.getName())) {
				i.remove();
				break;
			}
		}

		resLib.getDefinitions().add(claimDefinition);
		defResLib.setParsedAnnexLibrary(resLib);

	}

	private void insertClaimCall(Classifier modificationContext, FunctionDefinition claimDefinition,
			ProveStatement claimCall) {

		if (modificationContext == null) {
			return;
		}

		DefaultAnnexSubclause subclause = null;
		ResoluteSubclause resclause = null;
		for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("resolute")) {
				subclause = (DefaultAnnexSubclause) sc;
				resclause = EcoreUtil.copy((ResoluteSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (subclause == null) {
			subclause = (DefaultAnnexSubclause) modificationContext
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			subclause.setName("resolute");
			subclause.setSourceText("{** **}");

			resclause = ResoluteFactory.eINSTANCE.createResoluteSubclause();
		}

		// If the prove statement already exists, remove it
		Iterator<AnalysisStatement> i = resclause.getProves().iterator();
		while (i.hasNext()) {
			AnalysisStatement as = i.next();
			if (as instanceof ProveStatement) {
				ProveStatement prove = (ProveStatement) as;
				Expr expr = prove.getExpr();
				if (expr instanceof FnCallExpr) {
					FnCallExpr fnCallExpr = (FnCallExpr) expr;
					if (fnCallExpr.getFn().getName().equalsIgnoreCase(claimDefinition.getName())) {
						i.remove();
						break;
					}
				}
			}
		}

		resclause.getProves().add(claimCall);
		subclause.setParsedAnnexSubclause(resclause);
	}

	private FunctionDefinition getClaimDefinition(Classifier modificationContext) {
		if (modificationContext == null) {
			return null;
		}
		for (AnnexSubclause annexSubclause : modificationContext.getOwnedAnnexSubclauses()) {
			DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
			// See if there's a resolute annex
			if (defaultSubclause.getParsedAnnexSubclause() instanceof ResoluteSubclause) {
				ResoluteSubclause resoluteClause = (ResoluteSubclause) defaultSubclause.getParsedAnnexSubclause();
				// See if there are any 'prove' clauses
				for (AnalysisStatement as : resoluteClause.getProves()) {
					if (as instanceof ProveStatement) {
						ProveStatement prove = (ProveStatement) as;
						Expr expr = prove.getExpr();
						if (expr instanceof FnCallExpr) {
							FnCallExpr fnCall = (FnCallExpr) expr;
							FunctionDefinition fd = fnCall.getFn();
							if (fd.getName().equalsIgnoreCase(this.id)) {
								return fd;
							}
						}
					}
				}
				break;
			}
		}
		return null;
	}

	private ProveStatement getClaimCall(Classifier modificationContext) {
		if (modificationContext == null) {
			return null;
		}
		for (AnnexSubclause annexSubclause : modificationContext.getOwnedAnnexSubclauses()) {
			DefaultAnnexSubclause defaultSubclause = (DefaultAnnexSubclause) annexSubclause;
			// See if there's a resolute annex
			if (defaultSubclause.getParsedAnnexSubclause() instanceof ResoluteSubclause) {
				ResoluteSubclause resoluteClause = (ResoluteSubclause) defaultSubclause.getParsedAnnexSubclause();
				// See if there are any 'prove' clauses
				for (AnalysisStatement as : resoluteClause.getProves()) {
					if (as instanceof ProveStatement) {
						ProveStatement prove = (ProveStatement) as;
						Expr expr = prove.getExpr();
						if (expr instanceof FnCallExpr) {
							FnCallExpr fnCall = (FnCallExpr) expr;
							FunctionDefinition fd = fnCall.getFn();
							if (fd.getName().equalsIgnoreCase(this.id)) {
								return prove;
							}
						}
					}
				}
				break;
			}
		}
		return null;
	}

	public void insertAgree(Resource resource) {

		Classifier modificationContext = getModificationContext(this.context, resource);
		if (modificationContext == null) {
			throw new RuntimeException("Unable to determine requirement context.");
		}

		DefaultAnnexSubclause subclause = null;
		AgreeContractSubclause agreeSubclause = null;
		for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
			if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("agree")) {
				subclause = (DefaultAnnexSubclause) sc;
				agreeSubclause = EcoreUtil.copy((AgreeContractSubclause) subclause.getParsedAnnexSubclause());
				break;
			}
		}

		if (subclause == null) {
			subclause = (DefaultAnnexSubclause) modificationContext
					.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
			subclause.setName("agree");
			subclause.setSourceText("{** **}");

			agreeSubclause = AgreeFactory.eINSTANCE.createAgreeContractSubclause();
		} else {
			// If an agree statement with this id already exists, do nothing
			for (SpecStatement spec : ((AgreeContract) agreeSubclause.getContract()).getSpecs()) {
				if (spec instanceof NamedSpecStatement) {
					if (((NamedSpecStatement) spec).getName().equalsIgnoreCase(this.id)) {
						Dialog.showError("Formalize Requirement",
								"Requirement " + this.id + " has already been formalized in AGREE.");
						return;
					}
				}
			}
		}

		String assume = "assume ";
		if (!this.id.isEmpty()) {
			assume += this.id + " ";
		}
		assume += "\"" + this.text + "\" : false;";
		AgreeAnnexParser parser = new AgreeAnnexParser();
		NamedSpecStatement agreeSpec = parser.parseNamedSpecStatement(assume);

		AgreeContract contract = (AgreeContract) agreeSubclause.getContract();
		contract.getSpecs().add(agreeSpec);
		subclause.setParsedAnnexSubclause(agreeSubclause);
	}

	private Classifier getModificationContext(String qualifiedName, Resource resource) {
		// Get modification context
		Classifier modificationContext = null;
		TreeIterator<EObject> x = EcoreUtil.getAllContents(resource, true);
		while (x.hasNext()) {
			EObject next = x.next();
			if (next instanceof NamedElement) {
				NamedElement nextElement = (NamedElement) next;
				if (nextElement.getQualifiedName() != null
						&& nextElement.getQualifiedName().equalsIgnoreCase(qualifiedName)) {
					if (nextElement instanceof Subcomponent) {
						Subcomponent sub = (Subcomponent) nextElement;
						modificationContext = sub.getComponentType();
					} else {
						modificationContext = (Classifier) nextElement;
					}
					break;
				}
			}
		}
		return modificationContext;
	}

	/**
	 * Return the component implementation referred to by the qualified name
	 * @param qualifiedName
	 * @return
	 */
	public Classifier getImplementationClassifier() {
		return getImplementationClassifier(this.context);
	}

	public static Classifier getImplementationClassifier(String qualifiedName) {
		Classifier classifier = null;
		if (!qualifiedName.contains("::")) {
			return null;
		}
		String pkgName = Aadl2Util.getPackageName(qualifiedName);

		// The qualified name should either refer to a component implementation
		// or a component implementation's subcomponent or connection
		// A component implementation qualified name will appear as <Package>::<Component Implementation>
		// A subcomponent/connection qualified name will appear as <Package>::<Component Implementation>.<Subcomponent/Connection>
		// Since we want to return the component implementation, we want to truncate the subcomponent from the qualified name
		String[] parts = qualifiedName.split("\\.");
		String compImplName = "";
		if (parts.length > 0) {
			compImplName = parts[0] + "." + parts[1];
		}

		for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			if (pkg.getName().equalsIgnoreCase(pkgName)) {
				for (Classifier c : EcoreUtil2.getAllContentsOfType(pkg, Classifier.class)) {
					if (c.getQualifiedName().equalsIgnoreCase(compImplName)) {
						classifier = c;
						break;
					}
				}
				break;
			}
		}

		return classifier;
	}

	public FunctionDefinition getResoluteClaim() {
		FunctionDefinition fnDef = null;

		// Get AADL Package
		Classifier classifier = getImplementationClassifier();
		if (classifier == null) {
			return null;
		}
		AadlPackage aadlPkg = AadlUtil.getContainingPackage(classifier);
		if (aadlPkg == null) {
			return null;
		}

		// Get private section
		PrivatePackageSection privateSection = aadlPkg.getOwnedPrivateSection();
		if (privateSection == null) {
			return null;
		}

		// Get Resolute annex
		for (AnnexLibrary annexLib : privateSection.getOwnedAnnexLibraries()) {
			if (annexLib instanceof DefaultAnnexLibrary && annexLib.getName().equalsIgnoreCase("resolute")) {
				DefaultAnnexLibrary defaultLib = (DefaultAnnexLibrary) annexLib;
				ResoluteLibrary resLib = (ResoluteLibrary) defaultLib.getParsedAnnexLibrary();
				// Iterate over requirements
				for (Definition def : resLib.getDefinitions()) {
					if (def instanceof FunctionDefinition && def.getName().equalsIgnoreCase(this.id)) {
						fnDef = (FunctionDefinition) def;
						break;
					}
				}
				break;
			}
		}

		return fnDef;
	}

	public IFile getContainingFile() {
		Classifier modificationContext = getImplementationClassifier(this.context);
		if (modificationContext == null) {
			return null;
		}
		return Filesystem.getFile(modificationContext.eResource().getURI());
	}

}
