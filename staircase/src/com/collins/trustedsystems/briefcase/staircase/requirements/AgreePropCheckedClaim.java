package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AgreePropCheckedClaim extends BuiltInClaim {

	public static final String AGREE_PROP_CHECKED = "Agree_Property_Checked";
	private final String reqId;
	private final String reqContext;

	public AgreePropCheckedClaim(String reqId, String context) {
		super(AGREE_PROP_CHECKED);
		this.reqId = reqId;
		this.reqContext = context;
	}

	@Override
	public List<Expr> getClaimArgs() {
		final List<Expr> claimArgs = new ArrayList<>();
		claimArgs.add(Create.stringExpr(reqContext));
		claimArgs.add(Create.stringExpr(reqId));
		return claimArgs;
	}

}
