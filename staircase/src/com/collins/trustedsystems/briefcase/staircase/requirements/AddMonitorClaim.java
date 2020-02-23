package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.PortConnection;
import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddMonitorClaim extends BuiltInClaim {

	private static final String ADD_MONITOR = "Add_Monitor";

	private final String reqContext;
	private final Subcomponent monitor;
	private final PortConnection connection;

	public AddMonitorClaim(String context, Subcomponent monitor, PortConnection connection) {
		super(ADD_MONITOR);
		this.reqContext = context;
		this.monitor = monitor;
		this.connection = connection;
	}

	@Override
	public List<Expr> getCallArgs() {
		List<Expr> callArgs = new ArrayList<>();
		callArgs.add(Create.THIS(this.reqContext));
		callArgs.add(Create.stringExpr(this.monitor.getName()));
		callArgs.add(Create.THIS(this.connection));
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("comp_context", Create.baseType("component")));
		defParams.add(Create.arg("monitor_name", Create.baseType("string")));
		defParams.add(Create.arg("observed_connection", Create.baseType("connection")));
		return defParams;
	}

}
