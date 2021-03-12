package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ProveStatement;

public class BaseClaim extends BuiltInClaim {

	public final static String BASE_CLAIM = null;

	private final CyberRequirement requirement;

	private FunctionDefinition claimDef = null;

	public BaseClaim(CyberRequirement requirement) {
		super(BASE_CLAIM);
		this.requirement = requirement;
	}

	@Override
	public List<Expr> getCallArgs() {
		final List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS(this.requirement.getContext()));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		final List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("comp_context", Create.baseType("component")));
		return defParams;
	}

	@Override
	public FunctionDefinition buildClaimDefinition(FunctionDefinition fd) {

		final ClaimBuilder builder = new ClaimBuilder(this.requirement.getId());
		final List<Arg> defParams = getDefinitionParams();

		for (Arg arg : defParams) {
			builder.addArg(arg);
		}

		// create the claim string
		final String claimString = "[" + this.requirement.getType() + "] "
				+ this.requirement.getText();

		builder.addClaimString(claimString);
		builder.addClaimExpr(Create.UNDEVELOPED());

		this.claimDef = builder.build();

		return this.claimDef;

	}

	@Override
	public ProveStatement buildClaimCall(ProveStatement prove) {

		if (this.claimDef == null) {
			return null;
		}

		final ClaimCallBuilder builder = new ClaimCallBuilder(this.claimDef);
		final List<Expr> callArgs = getCallArgs();

		for (Expr e : callArgs) {
			builder.addArg(e);
		}

		return builder.build();
	}

}
