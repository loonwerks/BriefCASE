package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddProxyClaim extends BuiltInClaim {

	private static final String ADD_PROXY = "Add_Proxy";

	public AddProxyClaim() {
		super(ADD_PROXY);

	}

	@Override
	public List<Expr> getClaimArgs() {
		final List<Expr> claimArgs = new ArrayList<>();
		return claimArgs;
	}

}
