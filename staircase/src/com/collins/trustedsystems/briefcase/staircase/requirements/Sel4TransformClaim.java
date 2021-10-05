package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.Expr;

public class Sel4TransformClaim extends BuiltInClaim {

	private static final String SEL4_TRANSFORM = "seL4_Transform";

	private final String reqContext;
//	private final ComponentImplementation rootImpl;
//	private final String processor;
//	private final String transformedComponent;

	public Sel4TransformClaim(String context) {
//	public Sel4TransformClaim(String context, ComponentImplementation rootImpl, String transformedComponent) {
//	public Sel4TransformClaim(String context, ComponentImplementation rootImpl, String processor,
//			String transformedComponent) {
		super(SEL4_TRANSFORM);
		this.reqContext = context;
//		this.rootImpl = rootImpl;
//		this.processor = processor;
//		this.transformedComponent = transformedComponent;
	}

	@Override
	public List<Expr> getCallArgs() {
		final List<Expr> callArgs = new ArrayList<>();

//		final String rootQualName = rootImpl.getQualifiedName() + ".";
		callArgs.add(Create.THIS(this.reqContext));
//		callArgs.add(Create.stringExpr(this.processor));
//		callArgs.add(Create.THIS(rootQualName + this.transformedComponent));

		return callArgs;
	}

	@Override
	public List<Arg> getDefinitionParams() {
		final List<Arg> defParams = new ArrayList<>();
		defParams.add(Create.arg("comp_context", Create.baseType("component")));
//		defParams.add(Create.arg("processor_name", Create.baseType("string")));
//		defParams.add(Create.arg("transformed_component", Create.baseType("component")));
		return defParams;
	}

}
