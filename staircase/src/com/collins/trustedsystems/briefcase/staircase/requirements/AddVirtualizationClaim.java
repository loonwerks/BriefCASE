package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Expr;

public class AddVirtualizationClaim extends BuiltInClaim {

	private static final String ADD_VIRTUALIZATION = "Add_Virtualization";

	private final List<String> boundComponents;
	private final Subcomponent virtualProcessor;

	public AddVirtualizationClaim(List<String> boundComponents, Subcomponent virtualProcessor) {
		super(ADD_VIRTUALIZATION);
		this.boundComponents = boundComponents;
		this.virtualProcessor = virtualProcessor;
	}

	@Override
	public List<Expr> getClaimArgs() {
		final List<Expr> claimArgs = new ArrayList<>();
		final String sysQualName = virtualProcessor.getContainingClassifier().getQualifiedName() + ".";
		String boundComps = "{";
		for (int i = 0; i < boundComponents.size(); ++i) {
			boundComps += sysQualName + boundComponents.get(i);
			if (i < boundComponents.size() - 1) {
				boundComps += ",";
			}
		}
		boundComps += "}";
		claimArgs.add(Create.stringExpr(boundComps));
		claimArgs.add(Create.stringExpr(this.virtualProcessor.getName()));

		return claimArgs;
	}

}
