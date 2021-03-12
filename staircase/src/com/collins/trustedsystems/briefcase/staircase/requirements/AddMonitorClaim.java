package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddMonitorClaim extends BuiltInClaim {

	private static final String ADD_MONITOR = "Add_Monitor";
	private static final String ADD_MONITOR_GATE = "Add_Monitor_Gate";

	private final String reqContext;
	private final Subcomponent monitor;
	private String gateContext = null;
	private NamedElement msgType = null;
	private String monitorType = null;

	public AddMonitorClaim(String context, Subcomponent monitor) {
		super(ADD_MONITOR);
		this.reqContext = context;
		this.monitor = monitor;
		this.monitorType = ADD_MONITOR;
	}

	public AddMonitorClaim(String context, Subcomponent monitor, String gateContext, NamedElement msgType) {
		super(ADD_MONITOR_GATE);
		this.reqContext = context;
		this.monitor = monitor;
		this.gateContext = gateContext;
		this.msgType = msgType;
		this.monitorType = ADD_MONITOR_GATE;
	}

	@Override
	public List<Expr> getCallArgs() {
		final List<Expr> callArgs = new ArrayList<>();
		final String qualifiedName = this.reqContext.substring(0, this.reqContext.lastIndexOf("."));
		callArgs.add(Create.THIS(this.reqContext));
		callArgs.add(Create.THIS(qualifiedName, this.monitor));
		if (this.monitorType == ADD_MONITOR_GATE) {
			if (gateContext.isEmpty()) {
				callArgs.add(Create.THIS());
			} else {
				callArgs.add(Create.THIS(qualifiedName + "." + this.gateContext));
			}
			callArgs.add(Create.id(this.msgType));
		}
		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		final List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("comp_context", Create.baseType("component")));
		defParams.add(Create.arg("monitor", Create.baseType("component")));
		if (this.monitorType == ADD_MONITOR_GATE) {
			String compContext = this.reqContext.substring(this.reqContext.lastIndexOf(".") + 1);
			if (compContext.equalsIgnoreCase(this.gateContext)) {
				defParams.add(Create.arg("comp_context", Create.baseType("component")));
			} else {
				defParams.add(Create.arg("gate_context", Create.baseType("component")));
			}
			defParams.add(Create.arg("message_type", Create.baseType("data")));
		}
		return defParams;
	}

}
