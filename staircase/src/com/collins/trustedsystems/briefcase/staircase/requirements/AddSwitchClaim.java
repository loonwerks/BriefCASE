package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
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
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS(this.reqContext));
		callArgs.add(Create.THIS(this.selector));
		callArgs.add(Create.id(this.msgType));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("switch_context", Create.baseType("component")));
		defParams.add(Create.arg("switch", Create.baseType("component")));
		defParams.add(Create.arg("message_type", Create.baseType("data")));
		return defParams;
	}

}
