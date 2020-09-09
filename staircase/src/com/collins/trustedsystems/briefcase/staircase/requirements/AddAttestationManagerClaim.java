package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddAttestationManagerClaim extends BuiltInClaim {

	private static final String ADD_ATTESTATION_MANAGER = "Add_Attestation_Manager";

	private final String reqContext;
	private final Subcomponent commDriver;
	private final Subcomponent attestationManager;
	private final Subcomponent attestationGate;

	public AddAttestationManagerClaim(String reqContext, Subcomponent commDriver, Subcomponent attestationManager,
			Subcomponent attestationGate) {
		super(ADD_ATTESTATION_MANAGER);
		this.reqContext = reqContext;
		this.commDriver = commDriver;
		this.attestationManager = attestationManager;
		this.attestationGate = attestationGate;
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		String qualifiedName = this.reqContext.substring(0, this.reqContext.lastIndexOf("."));
		callArgs.add(Create.THIS(qualifiedName, this.commDriver));
		callArgs.add(Create.THIS(qualifiedName, this.attestationManager));
		callArgs.add(Create.THIS(qualifiedName, this.attestationGate));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("comm_driver", Create.baseType("component")));
		defParams.add(Create.arg("attestation_manager", Create.baseType("component")));
		defParams.add(Create.arg("attestation_gate", Create.baseType("component")));
		return defParams;
	}

}
