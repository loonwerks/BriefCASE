package com.collins.trustedsystems.briefcase.json.export;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Comment;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PackageSection;
import org.osate.annexsupport.AnnexUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractLibrary;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreePackage;
import com.rockwellcollins.atc.agree.agree.Arg;
import com.rockwellcollins.atc.agree.agree.ArraySubExpr;
import com.rockwellcollins.atc.agree.agree.AssertStatement;
import com.rockwellcollins.atc.agree.agree.AssignStatement;
import com.rockwellcollins.atc.agree.agree.AssumeStatement;
import com.rockwellcollins.atc.agree.agree.BinaryExpr;
import com.rockwellcollins.atc.agree.agree.BoolLitExpr;
import com.rockwellcollins.atc.agree.agree.CallExpr;
import com.rockwellcollins.atc.agree.agree.ComponentRef;
import com.rockwellcollins.atc.agree.agree.ConstStatement;
import com.rockwellcollins.atc.agree.agree.Contract;
import com.rockwellcollins.atc.agree.agree.DoubleDotRef;
import com.rockwellcollins.atc.agree.agree.EnumLitExpr;
import com.rockwellcollins.atc.agree.agree.EqStatement;
import com.rockwellcollins.atc.agree.agree.EventExpr;
import com.rockwellcollins.atc.agree.agree.ExistsExpr;
import com.rockwellcollins.atc.agree.agree.Expr;
import com.rockwellcollins.atc.agree.agree.FnDef;
import com.rockwellcollins.atc.agree.agree.FoldLeftExpr;
import com.rockwellcollins.atc.agree.agree.ForallExpr;
import com.rockwellcollins.atc.agree.agree.GetPropertyExpr;
import com.rockwellcollins.atc.agree.agree.GuaranteeStatement;
import com.rockwellcollins.atc.agree.agree.IfThenElseExpr;
import com.rockwellcollins.atc.agree.agree.IndicesExpr;
import com.rockwellcollins.atc.agree.agree.IntLitExpr;
import com.rockwellcollins.atc.agree.agree.NamedElmExpr;
import com.rockwellcollins.atc.agree.agree.NodeDef;
import com.rockwellcollins.atc.agree.agree.NodeEq;
import com.rockwellcollins.atc.agree.agree.NodeLemma;
import com.rockwellcollins.atc.agree.agree.NodeStmt;
import com.rockwellcollins.atc.agree.agree.PreExpr;
import com.rockwellcollins.atc.agree.agree.PrevExpr;
import com.rockwellcollins.atc.agree.agree.PrimType;
import com.rockwellcollins.atc.agree.agree.PropertyStatement;
import com.rockwellcollins.atc.agree.agree.RealCast;
import com.rockwellcollins.atc.agree.agree.RealLitExpr;
import com.rockwellcollins.atc.agree.agree.RecordLitExpr;
import com.rockwellcollins.atc.agree.agree.SelectionExpr;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.agree.agree.ThisRef;
import com.rockwellcollins.atc.agree.agree.Type;
import com.rockwellcollins.atc.agree.agree.UnaryExpr;

public class AgreeTranslate {

	private static JsonElement genBinaryExpr(BinaryExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("BinaryExpr"));
		result.add("left", genExpr(expr.getLeft()));
		result.add("op", new JsonPrimitive((expr.getOp())));
		result.add("right", genExpr(expr.getRight()));
		return result;
	}

	private static JsonElement genUnaryExpr(UnaryExpr expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("UnaryExpr"));
		result.add("operand", genExpr(expr.getExpr()));
		result.add("op", new JsonPrimitive(expr.getOp()));
		return result;
	}

	private static JsonElement genIntLitExpr(IntLitExpr expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("IntLitExpr"));
		result.add("value", new JsonPrimitive(expr.getVal()));

		return result;
	}

	private static JsonElement genRealLitExpr(RealLitExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("RealLitExpr"));
		result.add("value", new JsonPrimitive(expr.getVal()));
		return result;
	}

	private static JsonElement genBoolLitExpr(BoolLitExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("BoolLitExpr"));
		result.add("value", new JsonPrimitive(expr.getVal().getValue() + ""));
		return result;
	}

	private static JsonElement genSelectionExpr(SelectionExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("Selection"));
		String selection = expr.getField().getName();

		result.add("target", genExpr(expr.getTarget()));
		result.add("field", new JsonPrimitive(selection));
		return result;
	}

	private static JsonElement genNamedElmExpr(NamedElmExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("NamedElmExpr"));
		result.add("name", new JsonPrimitive(expr.getElm().getName()));
		return result;
	}

	private static JsonElement genCallExpr(CallExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("CallExpr"));
		result.add("function", genDoubleDotRef(expr.getRef()));
		JsonArray args = new JsonArray();
		for (Expr arg : expr.getArgs()) {
			args.add(genExpr(arg));
		}
		result.add("args", args);
		return result;
	}

	private static JsonElement genIfThenElseExpr(IfThenElseExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("IfThenElseExpr"));
		result.add("if", genExpr(expr.getA()));
		result.add("then", genExpr(expr.getB()));
		result.add("else", genExpr(expr.getC()));
		return result;
	}

	private static JsonElement genRecordLitExpr(RecordLitExpr expr) {

		JsonObject result = new JsonObject();

		result.add("kind", new JsonPrimitive("RecordLitExpr"));
		result.add("recordType", genDoubleDotRef(expr.getRecordType()));
		JsonObject fields = new JsonObject();
		int sz = expr.getArgs().size();
		for (int i = 0; i < sz; i++) {
			String name = expr.getArgs().get(i).getName();
			JsonElement v = genExpr(expr.getArgExpr().get(i));
			fields.add(name, v);
		}

		result.add("value", fields);
		return result;
	}

	private static JsonElement genEventExpr(EventExpr expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("EventExpr"));
		result.add("id", genExpr(expr.getPort()));
		return result;
	}

	private static JsonElement genPreExpr(PreExpr expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("PreExpr"));
		result.add("expr", genExpr(expr.getExpr()));
		return result;
	}

	private static JsonElement genPrevExpr(PrevExpr expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("PrevExpr"));
		result.add("delay", genExpr(expr.getDelay()));
		result.add("init", genExpr(expr.getInit()));
		return result;
	}

	private static JsonElement genGetPropertyExpr(GetPropertyExpr expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("GetPropertyExpr"));
		result.add("property", new JsonPrimitive(expr.getProp().getName()));
		result.add("componentRef", genComponentRef(expr.getComponentRef()));
		return result;
	}

	private static JsonElement genComponentRef(ComponentRef ref) {
		if (ref instanceof ThisRef) {
			return new JsonPrimitive("ThisRef");
		} else if (ref instanceof DoubleDotRef) {
			return genDoubleDotRef((DoubleDotRef) ref);
		} else {
			return null;
		}
	}

	private static JsonElement genForallExpr(ForallExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("ForallExpr"));
		result.add("binding", new JsonPrimitive(expr.getBinding().getName()));
		result.add("array", genExpr(expr.getArray()));
		result.add("expr", genExpr(expr.getExpr()));
		return result;
	}

	private static JsonElement genExistsExpr(ExistsExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("ExistsExpr"));
		result.add("binding", new JsonPrimitive(expr.getBinding().getName()));
		result.add("array", genExpr(expr.getArray()));
		result.add("expr", genExpr(expr.getExpr()));
		return result;
	}

	private static JsonElement genExpr(Expr expr) {

		if (expr instanceof IntLitExpr) {
			return genIntLitExpr((IntLitExpr) expr);
		} else if (expr instanceof RealLitExpr) {
			return genRealLitExpr((RealLitExpr) expr);
		} else if (expr instanceof BoolLitExpr) {
			return genBoolLitExpr((BoolLitExpr) expr);
		} else if (expr instanceof BinaryExpr) {
			return genBinaryExpr((BinaryExpr) expr);
		} else if (expr instanceof UnaryExpr) {
			return genUnaryExpr((UnaryExpr) expr);
		} else if (expr instanceof SelectionExpr) {
			return genSelectionExpr((SelectionExpr) expr);
		} else if (expr instanceof NamedElmExpr) {
			return genNamedElmExpr((NamedElmExpr) expr);
		} else if (expr instanceof EnumLitExpr) {
			return genEnumLitExpr((EnumLitExpr) expr);
		} else if (expr instanceof CallExpr) {
			return genCallExpr((CallExpr) expr);
		} else if (expr instanceof IfThenElseExpr) {
			return genIfThenElseExpr((IfThenElseExpr) expr);
		} else if (expr instanceof RecordLitExpr) {
			return genRecordLitExpr((RecordLitExpr) expr);
		} else if (expr instanceof EventExpr) {
			return genEventExpr((EventExpr) expr);
		} else if (expr instanceof PreExpr) {
			return genPreExpr((PreExpr) expr);
		} else if (expr instanceof PrevExpr) {
			return genPrevExpr((PrevExpr) expr);
		} else if (expr instanceof ForallExpr) {
			return genForallExpr((ForallExpr) expr);
		} else if (expr instanceof ExistsExpr) {
			return genExistsExpr((ExistsExpr) expr);
		} else if (expr instanceof ArraySubExpr) {
			return genArraySubExpr((ArraySubExpr) expr);
		} else if (expr instanceof FoldLeftExpr) {
			return genFoldLeftExpr((FoldLeftExpr) expr);
		} else if (expr instanceof IndicesExpr) {
			return genIndicesExpr((IndicesExpr) expr);
		} else if (expr instanceof RealCast) {
			return genRealCast((RealCast) expr);
		} else if (expr instanceof GetPropertyExpr) {
			return genGetPropertyExpr((GetPropertyExpr) expr);
		} else {
			return new JsonPrimitive("new_case/genExpr/" + (expr == null ? "null" : expr.toString()));
		}
	}

	private static JsonElement genIndicesExpr(IndicesExpr expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("IndicesExpr"));
		result.add("array", genExpr(expr.getArray()));
		return result;
	}

	private static JsonElement genRealCast(RealCast expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("RealCast"));
		result.add("expr", genExpr(expr.getExpr()));
		return result;
	}

	private static JsonElement genFoldLeftExpr(FoldLeftExpr expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("FoldLeftExpr"));
		result.add("binding", new JsonPrimitive(expr.getBinding().getName()));
		result.add("array", genExpr(expr.getArray()));
		result.add("accumulator", new JsonPrimitive(expr.getAccumulator().getName()));
		result.add("initial", genExpr(expr.getInitial()));
		result.add("expr", genExpr(expr.getExpr()));
		return result;
	}

	private static JsonElement genArraySubExpr(ArraySubExpr expr) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("ArraySubExpr"));
		result.add("array", genExpr(expr.getExpr()));
		result.add("index", genExpr(expr.getIndex()));
		return result;
	}

	private static JsonElement genEnumLitExpr(EnumLitExpr expr) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("AadlEnumerator"));
		result.add("type", genDoubleDotRef(expr.getEnumType()));
		result.add("value", new JsonPrimitive(expr.getValue()));
		return result;
	}

	private static JsonElement genAssertStatement(AssertStatement stmt) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("AssertStatement"));
		if (stmt.getName() != null) {
			result.add("name", new JsonPrimitive(stmt.getName()));
		}
		if (stmt.getStr() != null) {
			result.add("label", new JsonPrimitive(stmt.getStr()));
		}
		result.add("expr", genExpr(stmt.getExpr()));
		return result;
	}

	private static JsonElement genAssumeStatement(AssumeStatement stmt) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("AssumeStatement"));
		if (stmt.getName() != null) {
			result.add("name", new JsonPrimitive(stmt.getName()));
		}
		result.add("label", new JsonPrimitive(stmt.getStr()));
		result.add("expr", genExpr(stmt.getExpr()));
		return result;
	}

	private static JsonElement genEqStatement(EqStatement stmt) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("EqStatement"));
		JsonArray args = new JsonArray();
		for (Arg arg : stmt.getLhs()) {
			args.add(genArg(arg));
		}
		result.add("left", args);
		result.add("expr", genExpr(stmt.getExpr()));
		return result;
	}

	private static JsonElement genGuaranteeStatement(GuaranteeStatement stmt) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("GuaranteeStatement"));
		if (stmt.getName() != null) {
			result.add("name", new JsonPrimitive(stmt.getName()));
		}
		result.add("label", new JsonPrimitive(stmt.getStr()));
		result.add("expr", genExpr(stmt.getExpr()));
		return result;
	}

	private static JsonElement genAssignStatement(AssignStatement stmt) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("AssignStatement"));
		result.add("expr", genExpr(stmt.getExpr()));
		return result;
	}

	private static JsonElement genPropertyStatement(PropertyStatement stmt) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("PropertyStatement"));
		result.add("name", new JsonPrimitive(stmt.getName()));
		result.add("expr", genExpr(stmt.getExpr()));
		return result;
	}

	private static JsonElement genDoubleDotRef(DoubleDotRef ref) {
		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("DoubleDotRef"));
		NamedElement ne = ref.getElm();
		if (ne.getContainingClassifier() == null) {
			AadlPackage pkg = (AadlPackage) Aadl2Json.getContainingModelUnit(ne);
			result.add("packageName", new JsonPrimitive(pkg.getName()));
		}
		result.add("name", new JsonPrimitive(ne.getQualifiedName()));
		return result;
	}

	private static JsonElement genPrimType(PrimType type) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("PrimType"));

		result.add("primType", new JsonPrimitive(type.getName()));
		return result;
	}

	private static JsonElement genType(Type type) {
		JsonElement result = null;
		if (type instanceof DoubleDotRef) {
			result = genDoubleDotRef((DoubleDotRef) type);
		} else if (type instanceof PrimType) {
			result = genPrimType((PrimType) type);
		} else {
			result = new JsonPrimitive("new_case/genType/" + type);
		}
		return result;
	}

	private static JsonElement genArg(Arg arg) {

		JsonObject result = new JsonObject();
		result.add("name", new JsonPrimitive(arg.getName()));
		result.add("type", genType(arg.getType()));
		return result;
	}

	private static JsonElement genFnDef(FnDef stmt) {

		JsonObject result = new JsonObject();
		result.add("kind", new JsonPrimitive("FnDef"));

		if (stmt.getContainingClassifier() == null) {
			AadlPackage pkg = (AadlPackage) Aadl2Json.getContainingModelUnit(stmt);
			result.add("packageName", new JsonPrimitive(pkg.getName()));
		}
		result.add("name", new JsonPrimitive(stmt.getName()));

		JsonArray args = new JsonArray();
		for (Arg arg : stmt.getArgs()) {
			args.add(genArg(arg));
		}
		result.add("args", args);
		result.add("type", genType(stmt.getType()));
		result.add("expr", genExpr(stmt.getExpr()));
		return result;
	}

	private static JsonElement genConstStatement(ConstStatement stmt) {
		JsonObject result = new JsonObject();

		result.add("kind", new JsonPrimitive("ConstStatement"));
		result.add("name", new JsonPrimitive(stmt.getName()));
		result.add("type", genType(stmt.getType()));
		result.add("expr", genExpr(stmt.getExpr()));
		return result;
	}

	private static JsonElement genNodeStatement(NodeStmt stmt) {
		JsonObject result = new JsonObject();
		if (stmt instanceof NodeEq) {
			result.add("kind", new JsonPrimitive("NodeEq"));
			JsonArray args = new JsonArray();
			for (Arg arg : ((NodeEq) stmt).getLhs()) {
				args.add(genArg(arg));
			}
			result.add("left", args);
			result.add("expr", genExpr(stmt.getExpr()));
		} else if (stmt instanceof NodeLemma) {
			result.add("kind", new JsonPrimitive("NodeLemma"));
			result.add("lemma", new JsonPrimitive(((NodeLemma) stmt).getStr()));
			result.add("expr", genExpr(stmt.getExpr()));
		}

		return result;
	}

	private static JsonElement genNodeDef(NodeDef stmt) {
		JsonObject result = new JsonObject();

		result.add("kind", new JsonPrimitive("NodeDef"));
		result.add("name", new JsonPrimitive(stmt.getName()));
		JsonArray args = new JsonArray();
		for (Arg arg : stmt.getArgs()) {
			args.add(genArg(arg));
		}
		result.add("args", args);
		JsonArray retList = new JsonArray();
		for (Arg arg : stmt.getRets()) {
			retList.add(genArg(arg));
		}
		result.add("returns", retList);
		JsonArray exprList = new JsonArray();
		for (NodeStmt nodeStmt : stmt.getNodeBody().getStmts()) {
			exprList.add(genNodeStatement(nodeStmt));
		}
		result.add("body", exprList);
		return result;
	}

	private static JsonElement genSpecStatement(SpecStatement stmt) {
		if (stmt instanceof AssertStatement) {
			return genAssertStatement((AssertStatement) stmt);
		} else if (stmt instanceof AssumeStatement) {
			return genAssumeStatement((AssumeStatement) stmt);
		} else if (stmt instanceof EqStatement) {
			return genEqStatement((EqStatement) stmt);
		} else if (stmt instanceof GuaranteeStatement) {
			return genGuaranteeStatement((GuaranteeStatement) stmt);
		} else if (stmt instanceof AssignStatement) {
			return genAssignStatement((AssignStatement) stmt);
		} else if (stmt instanceof PropertyStatement) {
			return genPropertyStatement((PropertyStatement) stmt);
		} else if (stmt instanceof FnDef) {
			return genFnDef((FnDef) stmt);
		} else if (stmt instanceof ConstStatement) {
			return genConstStatement((ConstStatement) stmt);
		} else if (stmt instanceof NodeDef) {
			return genNodeDef((NodeDef) stmt);
		} else {
			return new JsonPrimitive("new_case/genSpecStatement/" + stmt.toString());
		}

	}

	private static JsonObject genContract(Contract contr) {
		JsonObject result = new JsonObject();

		if (contr instanceof AgreeContract) {
			EList<SpecStatement> stmts = ((AgreeContract) contr).getSpecs();

			JsonArray stmtJsonList = new JsonArray();
			for (SpecStatement stmt : stmts) {
				stmtJsonList.add(genSpecStatement(stmt));
			}
			result.add("statements", stmtJsonList);

			JsonArray comJsonList = new JsonArray();
			for (Comment com : contr.getOwnedComments()) {
				comJsonList.add(new JsonPrimitive(com.getBody()));
			}
		}

		return result;
	}

	private static JsonArray genComponentClassifier(ComponentClassifier cc) {

		JsonArray components = new JsonArray();

		EList<AnnexSubclause> annexSubClauses = AnnexUtil.getAllAnnexSubclauses(cc,
				AgreePackage.eINSTANCE.getAgreeContractSubclause());

		for (AnnexSubclause anx : annexSubClauses) {
			if (anx instanceof AgreeContractSubclause) {
				AgreeContract contr = (AgreeContract) ((AgreeContractSubclause) anx).getContract();
				components.add(genContract(contr));
			}
		}

		return components;
	}

	private static JsonArray genAadlPackage(AadlPackage pkg) {

		JsonArray components = new JsonArray();

		List<AnnexLibrary> annexLibraries = AnnexUtil.getAllActualAnnexLibraries(pkg,
				AgreePackage.eINSTANCE.getAgreeContractLibrary());

		for (AnnexLibrary anl : annexLibraries) {
			if (anl instanceof AgreeContractLibrary) {
				AgreeContract contr = (AgreeContract) ((AgreeContractLibrary) anl).getContract();
				components.add(genContract(contr));
			}
		}

		return components;
	}

	private static JsonObject genAadlPackageSection(PackageSection pkgSection) {

		JsonObject agreeLib = new JsonObject();

		for (AnnexLibrary annexLib : pkgSection.getOwnedAnnexLibraries()) {
			DefaultAnnexLibrary defaultAnnexLib = (DefaultAnnexLibrary) annexLib;
			if (defaultAnnexLib.getParsedAnnexLibrary() instanceof AgreeContractLibrary) {
				AgreeContract contr = (AgreeContract) ((AgreeContractLibrary) defaultAnnexLib.getParsedAnnexLibrary())
						.getContract();
				agreeLib = genContract(contr);
				break;
			}
		}

		return agreeLib;
	}

	public static JsonObject genAnnexLibrary(AnnexLibrary annexLib) {
		DefaultAnnexLibrary defaultAnnexLib = (DefaultAnnexLibrary) annexLib;
		AgreeContract contr = (AgreeContract) ((AgreeContractLibrary) defaultAnnexLib.getParsedAnnexLibrary())
				.getContract();
		return genContract(contr);
	}

	public static JsonObject genAnnexSubclause(AnnexSubclause annexSub) {
		DefaultAnnexSubclause defaultAnnexSub = (DefaultAnnexSubclause) annexSub;
		AgreeContract contr = (AgreeContract) ((AgreeContractSubclause) defaultAnnexSub.getParsedAnnexSubclause())
				.getContract();
		return genContract(contr);
	}

}
