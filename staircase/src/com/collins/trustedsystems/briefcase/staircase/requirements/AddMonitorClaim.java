package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Feature;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddMonitorClaim extends BuiltInClaim {

	private static final String ADD_MONITOR = "Add_Monitor";
	private static final String ADD_MONITOR_GATE = "Add_Monitor_Gate";

	private final String reqContext;
	private final Subcomponent monitor;
	private Feature alertPort = null;
	private String gateContext = null;
	private NamedElement msgType = null;
	private String monitorType = null;

	public AddMonitorClaim(String context, Subcomponent monitor, Feature alertPort) {
		super(ADD_MONITOR);
		this.reqContext = context;
		this.monitor = monitor;
		this.alertPort = alertPort;
		this.monitorType = ADD_MONITOR;
	}

	public AddMonitorClaim(String context, Subcomponent monitor, Feature alertPort, String gateContext,
			NamedElement msgType) {
		super(ADD_MONITOR_GATE);
		this.reqContext = context;
		this.monitor = monitor;
		this.alertPort = alertPort;
		this.gateContext = gateContext;
		this.msgType = msgType;
		this.monitorType = ADD_MONITOR_GATE;
	}

	@Override
	public List<Expr> getClaimArgs() {
		final List<Expr> claimArgs = new ArrayList<>();
		final String qualifiedName = this.reqContext.substring(0, this.reqContext.lastIndexOf("."));
		claimArgs.add(Create.stringExpr(reqContext));
		claimArgs.add(Create.stringExpr(qualifiedName + "." + this.monitor.getName()));
		claimArgs.add(Create.stringExpr(qualifiedName + "." + this.monitor.getName() + "." + this.alertPort.getName()));
		if (this.monitorType == ADD_MONITOR_GATE) {
			if (gateContext.isEmpty()) {
				claimArgs.add(Create.stringExpr(""));
			} else {
				claimArgs.add(Create.stringExpr(qualifiedName + "." + this.gateContext));
			}
			claimArgs.add(Create.id(this.msgType));
		}
		return claimArgs;
	}

}
