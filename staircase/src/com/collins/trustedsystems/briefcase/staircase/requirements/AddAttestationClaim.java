package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddAttestationClaim extends BuiltInClaim {

	private static final String ADD_ATTESTATION_MANAGER = "Add_Attestation_Manager";

	private final String reqContext;
	private final Subcomponent commDriver;
	private final Subcomponent attestationManager;
	private final Subcomponent attestationGate;

	public AddAttestationClaim(String reqContext, Subcomponent commDriver, Subcomponent attestationManager,
			Subcomponent attestationGate) {
		super(ADD_ATTESTATION_MANAGER);
		this.reqContext = reqContext;
		this.commDriver = commDriver;
		this.attestationManager = attestationManager;
		this.attestationGate = attestationGate;
	}

	@Override
	public List<Expr> getClaimArgs() {
		final List<Expr> claimArgs = new ArrayList<>();
		final String qualifiedName = this.reqContext.substring(0, this.reqContext.lastIndexOf("."));
		claimArgs.add(Create.stringExpr(qualifiedName + "." + this.commDriver.getName()));
		claimArgs.add(Create.stringExpr(qualifiedName + "." + this.attestationManager.getName()));
		claimArgs.add(Create.stringExpr(qualifiedName + "." + this.attestationGate.getName()));
		return claimArgs;
	}

}
