package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Connection;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddFilterClaim extends BuiltInClaim {

	private static final String ADD_FILTER = "Add_Filter";

	private final String reqContext;
	private final Subcomponent filter;
//	private final PortConnection connection;
	private final Connection connection;
	private final NamedElement msgType;

//	public AddFilterClaim(String context, Subcomponent filter, PortConnection connection, NamedElement msgType) {
	public AddFilterClaim(String context, Subcomponent filter, Connection connection, NamedElement msgType) {
		super(ADD_FILTER);
		this.reqContext = context;
		this.filter = filter;
		this.connection = connection;
		this.msgType = msgType;
	}

	@Override
	public List<Expr> getCallArgs() {
		final List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS(this.reqContext));
		final String qualifiedName = this.reqContext.substring(0, this.reqContext.lastIndexOf("."));
		callArgs.add(Create.THIS(qualifiedName, this.filter));
		callArgs.add(Create.THIS(qualifiedName, this.connection));
		callArgs.add(Create.id(this.msgType));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		final List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("comp_context", Create.baseType("component")));
		defParams.add(Create.arg("filter", Create.baseType("component")));
		defParams.add(Create.arg("conn", Create.baseType("connection")));
		defParams.add(Create.arg("message_type", Create.baseType("data")));
		return defParams;
	}

}
