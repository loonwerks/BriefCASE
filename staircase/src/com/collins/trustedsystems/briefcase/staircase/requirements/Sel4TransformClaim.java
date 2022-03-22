package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Expr;

public class Sel4TransformClaim extends BuiltInClaim {

	private static final String SEL4_TRANSFORM = "seL4_Transform";

	private final String reqContext;

	public Sel4TransformClaim(String context) {
		super(SEL4_TRANSFORM);
		this.reqContext = context;
	}

	@Override
	public List<Expr> getClaimArgs() {
		final List<Expr> claimArgs = new ArrayList<>();
		claimArgs.add(Create.stringExpr(reqContext));
		return claimArgs;
	}

}
