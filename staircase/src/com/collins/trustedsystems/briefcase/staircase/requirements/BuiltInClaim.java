package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.List;

import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.PublicPackageSection;

import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;

public abstract class BuiltInClaim {

	protected final String claim;

	public BuiltInClaim(String claim) {
		this.claim = claim;
	}

	protected abstract List<Expr> getClaimArgs();

	public String getName() {
		return this.claim;
	}

	protected FnCallExpr getClaimCall() {
		final FnCallExpr claimCall = ResoluteFactory.eINSTANCE.createFnCallExpr();
		claimCall.setFn(getBuiltInClaimDefinition());
		claimCall.getArgs().addAll(getClaimArgs());
		return claimCall;
	}


	/**
	 * Imports CASE_Model_Transformations package into CASE_Requirements
	 * @return CASE_Model_Transformations package
	 */
	private AadlPackage importModelTransformationsPackage() {
		final AadlPackage contextPkg = CaseUtils.getCaseRequirementsPackage();
		if (contextPkg == null) {
			throw new RuntimeException("Could not find CASE_Requirements package");
		}

		PrivatePackageSection priv8 = contextPkg.getOwnedPrivateSection();
		if (priv8 == null) {
			priv8 = contextPkg.createOwnedPrivateSection();
		}

		if (!CaseUtils.addCaseModelTransformationsImport(priv8, false)) {
			throw new RuntimeException("Could not import CASE_Model_Transformations package.");
		}

		return CaseUtils.getCaseModelTransformationsPackage();
	}

	protected FunctionDefinition getBuiltInClaimDefinition() {

		final AadlPackage pkg = importModelTransformationsPackage();

		if (pkg == null || this.claim == null) {
			return null;
		}
		final PublicPackageSection publicSection = pkg.getOwnedPublicSection();
		for (AnnexLibrary annexLibrary : publicSection.getOwnedAnnexLibraries()) {
			final DefaultAnnexLibrary defaultLib = (DefaultAnnexLibrary) annexLibrary;
			if (defaultLib.getParsedAnnexLibrary() instanceof ResoluteLibrary) {
				final ResoluteLibrary resoluteLib = (ResoluteLibrary) defaultLib.getParsedAnnexLibrary();
				for (Definition def : resoluteLib.getDefinitions()) {
					if (def instanceof FunctionDefinition) {
						final FunctionDefinition fd = (FunctionDefinition) def;
						if (fd.getName().equalsIgnoreCase(this.claim)) {
							return fd;
						}
					}
				}
				break;
			}
		}
		return null;
	}

}
