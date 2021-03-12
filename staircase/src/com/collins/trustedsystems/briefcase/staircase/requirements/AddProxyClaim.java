package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddProxyClaim extends BuiltInClaim {

	private static final String ADD_PROXY = "Add_Proxy";

	public AddProxyClaim() {
		super(ADD_PROXY);

	}

	@Override
	public List<Expr> getCallArgs() {
		final List<Expr> callArgs = new ArrayList<>();
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		final List<Arg> defParams = new ArrayList<>();
		return defParams;
	}

}
