package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddSwitchClaim extends BuiltInClaim {

	private static final String ADD_SWITCH = "Add_Switch";

	private final String reqContext;
	private final Subcomponent selector;
	private final NamedElement msgType;

	public AddSwitchClaim(String context, Subcomponent selector, NamedElement msgType) {
		super(ADD_SWITCH);
		this.reqContext = context;
		this.selector = selector;
		this.msgType = msgType;
	}

	@Override
	public List<Expr> getClaimArgs() {
		final List<Expr> claimArgs = new ArrayList<>();
		claimArgs.add(Create.stringExpr(reqContext));
		claimArgs.add(Create.stringExpr(selector.getName()));
		claimArgs.add(Create.id(this.msgType));
		return claimArgs;
	}

}
