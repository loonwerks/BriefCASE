package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.util.List;

import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Element;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;

import com.rockwellcollins.atc.resolute.resolute.Arg;
import com.rockwellcollins.atc.resolute.resolute.BaseType;
import com.rockwellcollins.atc.resolute.resolute.BinaryExpr;
import com.rockwellcollins.atc.resolute.resolute.BoolExpr;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.IdExpr;
import com.rockwellcollins.atc.resolute.resolute.ListExpr;
import com.rockwellcollins.atc.resolute.resolute.ListType;
import com.rockwellcollins.atc.resolute.resolute.NestedDotID;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.SetExpr;
import com.rockwellcollins.atc.resolute.resolute.SetType;
import com.rockwellcollins.atc.resolute.resolute.StringExpr;
import com.rockwellcollins.atc.resolute.resolute.ThisExpr;
import com.rockwellcollins.atc.resolute.resolute.Type;
import com.rockwellcollins.atc.resolute.resolute.UndevelopedExpr;

public class Create {

	public static ResoluteFactory factory = ResoluteFactory.eINSTANCE;
	public static Aadl2Factory aadlFactory = Aadl2Factory.eINSTANCE;

	public static IdExpr id(NamedElement element) {
		final IdExpr idExpr = factory.createIdExpr();
		idExpr.setId(element);
		return idExpr;
	}

	public static FnCallExpr fnCallExpr(FunctionDefinition fnDef) {
		final FnCallExpr fnCallExpr = factory.createFnCallExpr();
		fnCallExpr.setFn(fnDef);
		return fnCallExpr;
	}

	public static Arg arg(String name, Type t) {
		final Arg arg = factory.createArg();
		arg.setName(name);
		arg.setType(t);
		return arg;
	}

	public static StringExpr stringExpr(String val) {
		final StringExpr se = factory.createStringExpr();
		final StringLiteral x = aadlFactory.createStringLiteral();
		x.setValue("\"" + val + "\"");
		se.setVal(x);
		return se;
	}

	public static BinaryExpr andExpr(Expr left, Expr right) {
		final BinaryExpr expr = factory.createBinaryExpr();
		expr.setLeft(left);
		expr.setRight(right);
		expr.setOp("and");
		return expr;
	}

	public static BinaryExpr orExpr(Expr left, Expr right) {
		final BinaryExpr expr = factory.createBinaryExpr();
		expr.setLeft(left);
		expr.setRight(right);
		expr.setOp("or");
		return expr;
	}

	public static BinaryExpr impliesExpr(Expr left, Expr right) {
		final BinaryExpr expr = factory.createBinaryExpr();
		expr.setLeft(left);
		expr.setRight(right);
		expr.setOp("=>");
		return expr;
	}

	public static SetExpr setExpr(List<Expr> exprs) {
		final SetExpr expr = factory.createSetExpr();
		exprs.forEach(e -> expr.getExprs().add(e));
		return expr;
	}

	public static ListExpr listExpr(List<Expr> exprs) {
		final ListExpr expr = factory.createListExpr();
		exprs.forEach(e -> expr.getExprs().add(e));
		return expr;
	}

	public static BaseType baseType(String typeString) {
		final BaseType t = factory.createBaseType();
		t.setType(typeString);
		return t;
	}

	public static SetType setType(Type type) {
		final SetType t = factory.createSetType();
		t.setType(type);
		return t;
	}

	public static ListType listType(Type type) {
		final ListType t = factory.createListType();
		t.setType(type);
		return t;
	}

	public static ThisExpr THIS() {
		return factory.createThisExpr();
	}

	public static ThisExpr THIS(String qualifiedName) {
		final ThisExpr thisExpr = factory.createThisExpr();
		final NestedDotID id = factory.createNestedDotID();
		qualifiedNameToNestedDotID(id, qualifiedName);
		thisExpr.setSub(id.getSub());
		return thisExpr;
	}

	public static ThisExpr THIS(String qualifiedName, NamedElement namedElement) {
		final ThisExpr thisExpr = factory.createThisExpr();
		NestedDotID id = factory.createNestedDotID();
		final NestedDotID sub = factory.createNestedDotID();
		sub.setBase(namedElement);
		qualifiedNameToNestedDotID(id, qualifiedName);
		if (id.getSub() != null) {
			thisExpr.setSub(id.getSub());
			id = thisExpr.getSub();
			while (id.getSub() != null) {
				id = id.getSub();
			}
			id.setSub(sub);
		} else {
			thisExpr.setSub(sub);
		}
		return thisExpr;
	}

	public static ThisExpr THIS(NamedElement ne) {
		final ThisExpr thisExpr = factory.createThisExpr();
		if (ne != null) {
			NestedDotID id = factory.createNestedDotID();
			id.setBase(ne);
			thisExpr.setSub(id);
		}
		return thisExpr;
	}

	public static BoolExpr TRUE() {
		final BoolExpr be = factory.createBoolExpr();
		be.setVal(trueAADLLiteral());
		return be;
	}

	public static BoolExpr FALSE() {
		final BoolExpr be = factory.createBoolExpr();
		be.setVal(falseAADLLiteral());
		return be;
	}

	public static UndevelopedExpr UNDEVELOPED() {
		return factory.createUndevelopedExpr();
	}

	private static BooleanLiteral trueAADLLiteral() {
		final BooleanLiteral lit = aadlFactory.createBooleanLiteral();
		lit.setValue(true);
		return lit;
	}

	private static BooleanLiteral falseAADLLiteral() {
		final BooleanLiteral lit = aadlFactory.createBooleanLiteral();
		lit.setValue(false);
		return lit;
	}

	private static void qualifiedNameToNestedDotID(NestedDotID nestedDotID, String qualifiedName) {

		// Get containing classifier (should be component implementation)
		final Classifier classifier = CyberRequirement.getImplementationClassifier(qualifiedName);
		if (classifier == null) {
			return;
		}

		if (nestedDotID.getBase() == null) {
			nestedDotID.setBase(classifier);
		}

		final String[] parts = qualifiedName.split("\\.");
		if (parts.length > 2 && classifier instanceof ComponentImplementation) {
			// parts[0] and parts[1] will be the containing component implementation name
			// parts[2] and on will refer to a subcomponent or connection or feature
			final ComponentImplementation ci = (ComponentImplementation) classifier;
			for (Element e : ci.getOwnedElements()) {
				if (e instanceof NamedElement) {
					final NamedElement ne = (NamedElement) e;
					if (ne.getName().equalsIgnoreCase(parts[2])) {
						final NestedDotID n = factory.createNestedDotID();
						n.setBase(ne);
						if (parts.length > 3 && ne instanceof Subcomponent) {
							final Subcomponent sub = (Subcomponent) ne;
							String qName = sub.getClassifier().getQualifiedName();
							for (int i = 3; i < parts.length; i++) {
								qName += "." + parts[i];
							}
							qualifiedNameToNestedDotID(n, qName);
						}
						nestedDotID.setSub(n);
						break;
					}
				}
			}
		}

		return;
	}
}
