package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.ArgueStatement;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;

public class ClaimCallBuilder {

	private static ResoluteFactory f = ResoluteFactory.eINSTANCE;

	public final FunctionDefinition def;
	public List<Expr> args = new ArrayList<>();

	public ClaimCallBuilder(FunctionDefinition fd) {
		this.def = fd;
		this.args = new ArrayList<>();
	}

	public ClaimCallBuilder(ArgueStatement argue) {
		if (argue == null) {
			throw new RuntimeException("Claim call cannot be null.");
		}
		if (argue.getExpr() instanceof FnCallExpr) {
			final FnCallExpr fnCallExpr = (FnCallExpr) argue.getExpr();
			this.def = fnCallExpr.getFn();
			for (Expr expr : fnCallExpr.getArgs()) {
				this.args.add(expr);
			}
		} else {
			throw new RuntimeException("Argue statement can only contain a claim call.");
		}
	}

	public Expr addArg(Expr e) {
		this.args.add(e);
		return e;
	}

	public ArgueStatement build() {
		final ArgueStatement argue = f.createArgueStatement();
		argue.setTag("argue");

		final FnCallExpr fn = f.createFnCallExpr();
		fn.setFn(this.def);
		for (Expr expr : this.args) {
			fn.getArgs().add(expr);
		}

		argue.setExpr(fn);
		return argue;
	}
}
