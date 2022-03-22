package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Connection;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddFilterClaim extends BuiltInClaim {

	private static final String ADD_FILTER = "Add_Filter";

	private final String reqContext;
	private final Subcomponent filter;
	private final Connection connection;
	private final NamedElement msgType;

	public AddFilterClaim(String context, Subcomponent filter, Connection connection, NamedElement msgType) {
		super(ADD_FILTER);
		this.reqContext = context;
		this.filter = filter;
		this.connection = connection;
		this.msgType = msgType;
	}

	@Override
	public List<Expr> getClaimArgs() {
		final List<Expr> claimArgs = new ArrayList<>();
		claimArgs.add(Create.stringExpr(reqContext));
		final String qualifiedName = this.reqContext.substring(0, this.reqContext.lastIndexOf("."));
		claimArgs.add(Create.stringExpr(qualifiedName + "." + this.filter.getName()));
		claimArgs.add(Create.stringExpr(qualifiedName + "." + this.connection.getName()));
		claimArgs.add(Create.id(this.msgType));
		return claimArgs;
	}

}
