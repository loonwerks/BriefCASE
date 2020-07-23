package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddAttestationManagerClaim extends BuiltInClaim {

	private static final String ADD_ATTESTATION_MANAGER = "Add_Attestation_Manager";

	private final Subcomponent commDriver;
	private final Subcomponent attestationManager;
	private final Subcomponent attestationGate;

	public AddAttestationManagerClaim(Subcomponent commDriver, Subcomponent attestationManager,
			Subcomponent attestationGate) {
		super(ADD_ATTESTATION_MANAGER);
		this.commDriver = commDriver;
		this.attestationManager = attestationManager;
		this.attestationGate = attestationGate;
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS(this.commDriver));
		callArgs.add(Create.THIS(this.attestationManager));
		callArgs.add(Create.THIS(this.attestationGate));
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
