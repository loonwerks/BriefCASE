package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.SaveOptions;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.aadl2.util.Aadl2Util;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.collins.trustedsystems.briefcase.util.Filesystem;
import com.collins.trustedsystems.briefcase.util.TraverseProject;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.AgreeFactory;
import com.rockwellcollins.atc.agree.agree.AssumeStatement;
import com.rockwellcollins.atc.agree.agree.BoolLitExpr;
import com.rockwellcollins.atc.agree.agree.NamedSpecStatement;
import com.rockwellcollins.atc.agree.agree.SpecStatement;
import com.rockwellcollins.atc.resolute.resolute.AnalysisStatement;
import com.rockwellcollins.atc.resolute.resolute.ArgueStatement;
import com.rockwellcollins.atc.resolute.resolute.BinaryExpr;
import com.rockwellcollins.atc.resolute.resolute.ClaimBody;
import com.rockwellcollins.atc.resolute.resolute.ClaimContext;
import com.rockwellcollins.atc.resolute.resolute.Definition;
import com.rockwellcollins.atc.resolute.resolute.Expr;
import com.rockwellcollins.atc.resolute.resolute.FnCallExpr;
import com.rockwellcollins.atc.resolute.resolute.FunctionDefinition;
import com.rockwellcollins.atc.resolute.resolute.ResoluteFactory;
import com.rockwellcollins.atc.resolute.resolute.ResoluteLibrary;
import com.rockwellcollins.atc.resolute.resolute.ResoluteSubclause;
import com.rockwellcollins.atc.resolute.resolute.StringExpr;
import com.rockwellcollins.atc.resolute.resolute.UndevelopedExpr;

/**
 * @author jbabar
 *
 */
public class CyberRequirement {

	// Requirement status
	// TODO: change them to enum
	public static final String toDo = "ToDo";
	public static final String add = "Import";
	public static final String omit = "Omit";
	public static final String unknown = "Unknown";
	public static final String notApplicable = "N/A";
	private static final boolean addQuotes = true;

	private final static String FALSE = "False";
	private final static String TRUE = "True";
	private final static String FORMALIZED = "Formalized";
	private final static String GENERATED_BY = "Generated_By";
	private final static String GENERATED_ON = "Generated_On";
	private final static String REQ_COMPONENT = "Req_Component";

	private String type = ""; // this is the requirement classification type as defined by the TA1 tool
	private String id = "";
	private String text = "";
	private String context = ""; // this is the qualified name of the component
	private boolean formalize = false;
	private String rationale = "";
	private long date = 0L;
	private String tool = "";
	private String status = toDo;

	transient private String subcomponentQualifiedName = null;

	private Comparator<Definition> alphabetical = (o1, o2) -> o1.getName().compareTo(o2.getName());

	public CyberRequirement(CyberRequirement req) {
		this(req.getDate(), req.getTool(), req.getStatus(), req.getType(), req.getId(), req.getText(), req.getContext(),
				req.getFormalize(),
				req.getRationale());
	}

	public CyberRequirement(long date, String tool, String status, String type, String id, String text, String context,
			boolean formalize,
			String rationale) {
		this.date = date;
		this.tool = (tool == null ? unknown : tool);
		this.type = (type == null ? unknown : type);
		this.id = (id == null ? "" : id);
		this.text = (text == null ? "" : text);
		this.context = (context == null ? "" : context);
		this.formalize = formalize;
		this.rationale = (rationale == null ? notApplicable : rationale);
		this.status = ((status == null || toDo.equals(status)) ? toDo
				: add.equals(status) ? add : omit);
	}

	public CyberRequirement(String type) {
		this(0L, null, null, type, null, null, null, false, null);
	}

	protected CyberRequirement() {
		this(0L, null, null, null, null, null, null, false, null);
	}

	public boolean completelyEqual(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CyberRequirement)) {
			return false;
		}
		CyberRequirement other = (CyberRequirement) obj;
		return Objects.equals(context, other.context) && date == other.date && Objects.equals(id, other.id)
				&& Objects.equals(rationale, other.rationale) && Objects.equals(status, other.status)
				&& Objects.equals(text, other.text) && Objects.equals(tool, other.tool)
				&& Objects.equals(type, other.type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CyberRequirement)) {
			return false;
		}
		CyberRequirement other = (CyberRequirement) obj;
		return Objects.equals(context, other.context) && Objects.equals(type, other.type);

	}

	public String getContext() {
		return context;
	}

	public long getDate() {
		return date;
	}

	public String getId() {
		return id;
	}

	public boolean getFormalize() {
		return formalize;
	}

	public String getRationale() {
		return rationale;
	}


	public FunctionDefinition getResoluteClaim() {
		// Get AADL Package
		final AadlPackage aadlPkg = CaseUtils.getCaseRequirementsPackage();
		if (aadlPkg == null) {
			return null;
		}

		// Get private section
		final PrivatePackageSection privateSection = aadlPkg.getOwnedPrivateSection();
		if (privateSection == null) {
			return null;
		}

		// Get Resolute annex
		FunctionDefinition fnDef = null;
		for (AnnexLibrary annexLib : privateSection.getOwnedAnnexLibraries()) {
			if (annexLib instanceof DefaultAnnexLibrary && annexLib.getName().equalsIgnoreCase("resolute")) {
				final DefaultAnnexLibrary defaultLib = (DefaultAnnexLibrary) annexLib;
				final ResoluteLibrary resLib = (ResoluteLibrary) defaultLib.getParsedAnnexLibrary();
				// Iterate over requirements
				for (Definition def : resLib.getDefinitions()) {
					if (def instanceof FunctionDefinition && def.getName().equalsIgnoreCase(id)) {
						fnDef = (FunctionDefinition) def;
						break;
					}
				}
				break;
			}
		}

		return fnDef;
	}

	public String getStatus() {
		return status;
	}

	public String getText() {
		return text;
	}

	public String getTool() {
		return tool;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(context, type);
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFormalize(boolean formalize) {
		this.formalize = formalize;
	}

	public void setRationale(String rationale) {
		this.rationale = rationale;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTool(String tool) {
		this.tool = tool;
	}

	public void setType(String type) {
		this.type = type;
	}

	public IFile getContainingFile() {
		return getContainingFile(context);
	}

	public Resource getContainingResource() {
		return getContainingResource(getContainingFile());
	}

	public Resource getContainingResource(IFile file) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		return resourceSet.getResource(uri, true);
	}

	public IFile getSubcomponentContainingFile() {
		subcomponentQualifiedName = getSubcomponentQualifiedName(context);
		return getContainingFile(subcomponentQualifiedName);
	}

	public String getSubcomponentQualifiedName() {
		if (subcomponentQualifiedName == null) {
			subcomponentQualifiedName = getSubcomponentQualifiedName(context);
		}
		return subcomponentQualifiedName;
	}

	public void insertClaimDefinition() {

		// Get modification context
		final AadlPackage pkg = CaseUtils.getCaseRequirementsPackage();
		final Resource aadlResource = pkg.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE
				.createEditingDomain(aadlResource.getResourceSet());

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {

				final ClaimBuilder builder = new ClaimBuilder(getId());
				// create the claim string
				final String claimString = "[" + getType() + "] " + getText();
				builder.addClaimString(claimString);
				builder.addClaimExpr(Create.UNDEVELOPED());

				final FunctionDefinition claimDefinition = builder.build();
				final ClaimBody claimBody = (ClaimBody) claimDefinition.getBody();
				setClaimContexts(claimBody);

				PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
				if (priv8 == null) {
					priv8 = pkg.createOwnedPrivateSection();
				}

				DefaultAnnexLibrary defResLib = null;
				ResoluteLibrary resLib = null;
				for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
					if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
						defResLib = (DefaultAnnexLibrary) library;
						resLib = EcoreUtil.copy((ResoluteLibrary) defResLib.getParsedAnnexLibrary());
						break;
					}
				}

				if (defResLib == null) {
					defResLib = (DefaultAnnexLibrary) priv8
							.createOwnedAnnexLibrary(Aadl2Package.eINSTANCE.getDefaultAnnexLibrary());
					defResLib.setName("resolute");
					defResLib.setSourceText("{** **}");
					resLib = ResoluteFactory.eINSTANCE.createResoluteLibrary();
				}

				for (Iterator<Definition> i = resLib.getDefinitions().iterator(); i.hasNext();) {
					final Definition def = i.next();
					if (def != null && def instanceof FunctionDefinition && def.hasName()
							&& def.getName().equalsIgnoreCase(claimDefinition.getName())) {
						i.remove();
						break;
					}
				}

				resLib.getDefinitions().add(claimDefinition);
				ECollections.sort(resLib.getDefinitions(), alphabetical);
				defResLib.setParsedAnnexLibrary(resLib);
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());

		} catch (Exception e) {
			e.printStackTrace();
		}
		domain.dispose();

	}


	public void insertClaimCall() {

		// Add the top-level goal and argue statement if they don't already exist

		final Classifier modificationContext = getModificationContext();
		if (modificationContext == null) {
			return;
		}
		final Resource aadlResource = modificationContext.eResource();

		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE
				.createEditingDomain();

		final AadlPackage reqPackage = CaseUtils.getCaseRequirementsPackage();


		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {

				DefaultAnnexLibrary annexLib = null;
				ResoluteLibrary resLib = null;
				final String topLevelGoalName = modificationContext.getName().replace(".", "_") + "_cyber_resilient";
				final PackageSection pkgSection = AadlUtil.getContainingPackageSection(modificationContext);
				CaseUtils.importCaseRequirementsPackage(pkgSection);

				for (AnnexLibrary library : pkgSection.getOwnedAnnexLibraries()) {
					if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
						annexLib = (DefaultAnnexLibrary) library;
						resLib = EcoreUtil.copy((ResoluteLibrary) annexLib.getParsedAnnexLibrary());
						break;
					}
				}

				if (annexLib == null) {
					annexLib = (DefaultAnnexLibrary) pkgSection
							.createOwnedAnnexLibrary(Aadl2Package.eINSTANCE.getDefaultAnnexLibrary());
					annexLib.setName("resolute");
					annexLib.setSourceText("{** **}");
					resLib = ResoluteFactory.eINSTANCE.createResoluteLibrary();
				}

				for (Iterator<Definition> i = resLib.getDefinitions().iterator(); i.hasNext();) {
					final Definition def = i.next();
					if (def.getName().equalsIgnoreCase(topLevelGoalName)) {
						i.remove();
					} else if (def.getName().equalsIgnoreCase("security_requirements_satisfied")) {
						i.remove();
					}
				}

				final FunctionDefinition requirementsSatisfiedClaim = CaseUtils.createRequirementsSatisfiedGoal();
				final FunctionDefinition topLevelClaim = CaseUtils.createTopLevelGoal(modificationContext,
						requirementsSatisfiedClaim);

				Expr currentExpr = requirementsSatisfiedClaim.getBody().getExpr();
				for (AnnexLibrary reqAnnexLib : reqPackage.getOwnedPrivateSection().getOwnedAnnexLibraries()) {
					if (reqAnnexLib.getName().equalsIgnoreCase("resolute")) {

						final ResoluteLibrary resReqLib = (ResoluteLibrary) ((DefaultAnnexLibrary) reqAnnexLib)
								.getParsedAnnexLibrary();
						for (Definition def : resReqLib.getDefinitions()) {

							if (!(def instanceof FunctionDefinition)
									|| !(((FunctionDefinition) def).getBody() instanceof ClaimBody)) {
								continue;
							}
							final ClaimBody body = (ClaimBody) ((FunctionDefinition) def).getBody();
							for (NamedElement attribute : body.getAttributes()) {
								if (attribute instanceof ClaimContext
										&& attribute.getName().equalsIgnoreCase(REQ_COMPONENT)) {
									final ClaimContext ctx = (ClaimContext) attribute;
									final String comp = ((StringExpr) ctx.getExpr()).getVal()
											.getValue()
											.replace("\"", "");
									final Classifier classifier = getImplementationClassifier(comp);
									if (classifier != null && classifier.getQualifiedName()
											.equalsIgnoreCase(modificationContext.getQualifiedName())) {
										final FnCallExpr fnCallExpr = ResoluteFactory.eINSTANCE.createFnCallExpr();
										fnCallExpr.setFn((FunctionDefinition) def);
										if (currentExpr instanceof UndevelopedExpr) {
											currentExpr = fnCallExpr;
										} else {
											final BinaryExpr bExpr = ResoluteFactory.eINSTANCE.createBinaryExpr();
											bExpr.setOp("and");
											bExpr.setLeft(currentExpr);
											bExpr.setRight(fnCallExpr);
											currentExpr = EcoreUtil.copy(bExpr);
										}
									}
									break;
								}
							}
						}
						break;
					}
				}

				requirementsSatisfiedClaim.getBody().setExpr(currentExpr);
				resLib.getDefinitions().add(topLevelClaim);
				resLib.getDefinitions().add(requirementsSatisfiedClaim);
				annexLib.setParsedAnnexLibrary(resLib);

				// Check to see if the 'argue' statement has been created
				// If not, create it
				DefaultAnnexSubclause subclause = null;
				for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
					if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("resolute")) {
						subclause = (DefaultAnnexSubclause) sc;
						break;
					}
				}
				if (subclause == null) {
					subclause = (DefaultAnnexSubclause) modificationContext
							.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
					subclause.setName("resolute");
					subclause.setSourceText("{** **}");
					subclause.setParsedAnnexSubclause(ResoluteFactory.eINSTANCE.createResoluteSubclause());
				}

				final ResoluteSubclause resclause = EcoreUtil
						.copy((ResoluteSubclause) subclause.getParsedAnnexSubclause());

				ArgueStatement argueStatement = null;
				for (Iterator<AnalysisStatement> i = resclause.getAnalyses().iterator(); i.hasNext();) {
					final AnalysisStatement as = i.next();
					if (as instanceof ArgueStatement) {
						final Expr expr = ((ArgueStatement) as).getExpr();
						if (expr instanceof FnCallExpr) {
							final FunctionDefinition fd = ((FnCallExpr) expr).getFn();
							if (fd != null && fd.hasName() && fd.getName().equalsIgnoreCase(topLevelGoalName)) {
								argueStatement = (ArgueStatement) as;
								break;
							}
						}
					}
				}

				if (argueStatement == null) {
					argueStatement = ResoluteFactory.eINSTANCE.createArgueStatement();
					argueStatement.setTag("argue");
					final FnCallExpr argueExpr = ResoluteFactory.eINSTANCE.createFnCallExpr();
					argueExpr.setFn(topLevelClaim);
					argueExpr.getArgs().add(ResoluteFactory.eINSTANCE.createThisExpr());
					argueStatement.setExpr(argueExpr);
					resclause.getAnalyses().add(argueStatement);
					subclause.setParsedAnnexSubclause(resclause);
				}
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
		}
		domain.dispose();

	}

	public void insertAgree() {
		insertAgreeSpec();
		updateClaimDefinition(new AgreePropCheckedClaim(getId(), getContext()));
		updateFormalize();
	}

	private void insertAgreeSpec() {

		final Resource aadlResource = getContainingResource(getSubcomponentContainingFile());
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE
				.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {

				final Classifier modificationContext = getModificationContext(aadlResource,
						getSubcomponentQualifiedName());
				if (modificationContext == null) {
					throw new RuntimeException("Unable to determine requirement context.");
				}

				DefaultAnnexSubclause subclause = null;
				AgreeContractSubclause agreeSubclause = null;
				for (AnnexSubclause sc : modificationContext.getOwnedAnnexSubclauses()) {
					if (sc instanceof DefaultAnnexSubclause && sc.getName().equalsIgnoreCase("agree")) {
						subclause = (DefaultAnnexSubclause) sc;
						agreeSubclause = EcoreUtil.copy((AgreeContractSubclause) subclause.getParsedAnnexSubclause());
						break;
					}
				}

				if (subclause == null) {
					subclause = (DefaultAnnexSubclause) modificationContext
							.createOwnedAnnexSubclause(Aadl2Package.eINSTANCE.getDefaultAnnexSubclause());
					subclause.setName("agree");
					subclause.setSourceText("{** **}");

					agreeSubclause = AgreeFactory.eINSTANCE.createAgreeContractSubclause();
				} else {
					// If an agree statement with this id already exists, do nothing
					for (SpecStatement spec : ((AgreeContract) agreeSubclause.getContract()).getSpecs()) {
						if (spec instanceof NamedSpecStatement) {
							if (((NamedSpecStatement) spec).getName().equalsIgnoreCase(id)) {
								Dialog.showError("Formalize Requirement",
										"Requirement " + id + " has already been formalized in AGREE.");
								return;
							}
						}
					}
				}

				final AssumeStatement agreeSpec = AgreeFactory.eINSTANCE.createAssumeStatement();
				agreeSpec.setName(id);
				agreeSpec.setStr(text);
				final BoolLitExpr falseExpr = AgreeFactory.eINSTANCE.createBoolLitExpr();
				final BooleanLiteral boolLit = Aadl2Factory.eINSTANCE.createBooleanLiteral();
				boolLit.setValue(false);
				falseExpr.setVal(boolLit);
				agreeSpec.setExpr(falseExpr);

				AgreeContract contract = (AgreeContract) agreeSubclause.getContract();
				if (contract == null) {
					// Add an empty contract to agreeSubclause
					contract = AgreeFactory.eINSTANCE.createAgreeContract();
					agreeSubclause.setContract(contract);
				}

				contract.getSpecs().add(agreeSpec);
				subclause.setParsedAnnexSubclause(agreeSubclause);
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
		}
		domain.dispose();

	}


	public void updateClaimDefinition(BuiltInClaim claim) {
		if (claim == null) {
			return;
		}

		// Get modification context
		final AadlPackage pkg = CaseUtils.getCaseRequirementsPackage();

		final Resource aadlResource = pkg.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {

				PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
				if (priv8 == null) {
					priv8 = pkg.createOwnedPrivateSection();
				}

				DefaultAnnexLibrary defResLib = null;
				ResoluteLibrary resLib = null;
				for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
					if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
						defResLib = (DefaultAnnexLibrary) library;
						resLib = EcoreUtil.copy((ResoluteLibrary) defResLib.getParsedAnnexLibrary());
						break;
					}
				}
				if (defResLib == null) {
					BriefcaseNotifier.printError("Could not find requirement " + getId() + " in CASE_Requirements.");
					return;
				}
				FunctionDefinition claimDefinition = null;
				for (Iterator<Definition> i = resLib.getDefinitions().iterator(); i.hasNext();) {
					final Definition def = i.next();
					if (def != null && def instanceof FunctionDefinition && def.hasName()
							&& def.getName().equalsIgnoreCase(getId())) {
						claimDefinition = (FunctionDefinition) def;
						i.remove();
						break;
					}
				}
				if (claimDefinition == null) {
					BriefcaseNotifier.printError("Could not find requirement " + getId() + " in CASE_Requirements.");
					return;
				}

				final ClaimBody body = (ClaimBody) claimDefinition.getBody();
				if (body.getExpr() instanceof UndevelopedExpr) {
					body.setExpr(claim.getClaimCall());
				} else {
					BinaryExpr bExpr = ResoluteFactory.eINSTANCE.createBinaryExpr();
					bExpr.setOp("and");
					bExpr.setLeft(body.getExpr());
					bExpr.setRight(claim.getClaimCall());
					body.setExpr(bExpr);
				}

				resLib.getDefinitions().add(claimDefinition);
				ECollections.sort(resLib.getDefinitions(), alphabetical);
				defResLib.setParsedAnnexLibrary(resLib);
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
		}

		domain.dispose();

	}

	private void updateFormalize() {

		// Get modification context
		final AadlPackage pkg = CaseUtils.getCaseRequirementsPackage();

		final Resource aadlResource = pkg.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {

				PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
				if (priv8 == null) {
					priv8 = pkg.createOwnedPrivateSection();
				}

				DefaultAnnexLibrary defResLib = null;
				ResoluteLibrary resLib = null;
				for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
					if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
						defResLib = (DefaultAnnexLibrary) library;
						resLib = EcoreUtil.copy((ResoluteLibrary) defResLib.getParsedAnnexLibrary());
						break;
					}
				}
				if (defResLib == null) {
					if (getFormalize()) {
						BriefcaseNotifier
								.printError("Could not find requirement " + getId() + " in CASE_Requirements.");
					}
					return;
				}
				FunctionDefinition claimDefinition = null;
				for (Iterator<Definition> i = resLib.getDefinitions().iterator(); i.hasNext();) {
					final Definition def = i.next();
					if (def != null && def instanceof FunctionDefinition && def.hasName()
							&& def.getName().equalsIgnoreCase(getId())) {
						claimDefinition = (FunctionDefinition) def;
						i.remove();
						break;
					}
				}
				if (claimDefinition == null) {
					if (getFormalize()) {
						BriefcaseNotifier
								.printError("Could not find requirement " + getId() + " in CASE_Requirements.");
					}
					return;
				}

				setClaimContexts((ClaimBody) claimDefinition.getBody());

				resLib.getDefinitions().add(claimDefinition);
				ECollections.sort(resLib.getDefinitions(), alphabetical);
				defResLib.setParsedAnnexLibrary(resLib);
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
		}

		domain.dispose();

	}


	public void removeClaimDefinition() {

		// Get modification context
		final AadlPackage pkg = CaseUtils.getCaseRequirementsPackage();

		final Resource aadlResource = pkg.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {

				PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
				if (priv8 == null) {
					priv8 = pkg.createOwnedPrivateSection();
				}

				DefaultAnnexLibrary defResLib = null;
				ResoluteLibrary resLib = null;
				for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
					if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
						defResLib = (DefaultAnnexLibrary) library;
						resLib = (ResoluteLibrary) defResLib.getParsedAnnexLibrary();
						break;
					}
				}
				if (defResLib == null) {
					return;
				}
				for (Iterator<Definition> i = resLib.getDefinitions().iterator(); i.hasNext();) {
					final Definition def = i.next();
					if (def != null && def instanceof FunctionDefinition && def.hasName()
							&& def.getName().equalsIgnoreCase(getId())) {
						i.remove();
						break;
					}
				}
			}

		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
		}

		domain.dispose();

	}


	public void removeClaimCall() {

		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();
		final Classifier modificationContext = getModificationContext();
		final Resource aadlResource = modificationContext.eResource();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {

				final AadlPackage reqPackage = CaseUtils.getCaseRequirementsPackage();

				DefaultAnnexLibrary annexLib = null;
				ResoluteLibrary resLib = null;
				final String topLevelGoalName = modificationContext.getName().replace(".", "_") + "_cyber_resilient";
				final PackageSection pkgSection = AadlUtil.getContainingPackageSection(modificationContext);

				for (AnnexLibrary library : pkgSection.getOwnedAnnexLibraries()) {
					if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
						annexLib = (DefaultAnnexLibrary) library;
						resLib = EcoreUtil.copy((ResoluteLibrary) annexLib.getParsedAnnexLibrary());
						break;
					}
				}
				if (annexLib == null) {
					return;
				}

				for (Iterator<Definition> i = resLib.getDefinitions().iterator(); i.hasNext();) {
					final Definition def = i.next();
					if (def.getName().equalsIgnoreCase(topLevelGoalName)) {
						i.remove();
					} else if (def.getName().equalsIgnoreCase("security_requirements_satisfied")) {
						i.remove();
					}
				}

				final FunctionDefinition requirementsSatisfiedClaim = CaseUtils.createRequirementsSatisfiedGoal();
				final FunctionDefinition topLevelClaim = CaseUtils.createTopLevelGoal(modificationContext,
						requirementsSatisfiedClaim);

				Expr currentExpr = requirementsSatisfiedClaim.getBody().getExpr();
				for (AnnexLibrary reqAnnexLib : reqPackage.getOwnedPrivateSection().getOwnedAnnexLibraries()) {
					if (reqAnnexLib.getName().equalsIgnoreCase("resolute")) {

						final ResoluteLibrary resReqLib = (ResoluteLibrary) ((DefaultAnnexLibrary) reqAnnexLib)
								.getParsedAnnexLibrary();
						for (Definition def : resReqLib.getDefinitions()) {

							if (def.getName().equalsIgnoreCase(getId())) {
								continue;
							} else if (!(def instanceof FunctionDefinition)
									|| !(((FunctionDefinition) def).getBody() instanceof ClaimBody)) {
								continue;
							}

							final ClaimBody body = (ClaimBody) ((FunctionDefinition) def).getBody();
							for (NamedElement attribute : body.getAttributes()) {
								if (attribute instanceof ClaimContext
										&& attribute.getName().equalsIgnoreCase(REQ_COMPONENT)) {
									final ClaimContext ctx = (ClaimContext) attribute;
									final String comp = ((StringExpr) ctx.getExpr()).getVal()
											.getValue()
											.replace("\"", "");

									final Classifier classifier = getImplementationClassifier(comp);
									if (classifier != null && classifier.getQualifiedName()
											.equalsIgnoreCase(modificationContext.getQualifiedName())) {
										final FnCallExpr fnCallExpr = ResoluteFactory.eINSTANCE.createFnCallExpr();
										fnCallExpr.setFn((FunctionDefinition) def);
										if (currentExpr instanceof UndevelopedExpr) {
											currentExpr = fnCallExpr;
										} else {
											final BinaryExpr bExpr = ResoluteFactory.eINSTANCE.createBinaryExpr();
											bExpr.setOp("and");
											bExpr.setLeft(currentExpr);
											bExpr.setRight(fnCallExpr);
											currentExpr = EcoreUtil.copy(bExpr);
										}
									}
									break;
								}
							}
						}
						break;
					}
				}

				requirementsSatisfiedClaim.getBody().setExpr(currentExpr);
				resLib.getDefinitions().add(topLevelClaim);
				resLib.getDefinitions().add(requirementsSatisfiedClaim);
				annexLib.setParsedAnnexLibrary(resLib);

			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
		}

		domain.dispose();
	}

	public void removeAgree() {
		removeAgreeSpec();
		removeAgreeCheckClaim();
		updateFormalize();
	}

	private void removeAgreeSpec() {

		final Resource aadlResource = getContainingResource(getSubcomponentContainingFile());
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {

				final Classifier modificationContext = getModificationContext(aadlResource,
						getSubcomponentQualifiedName());

				DefaultAnnexSubclause subclause = null;
				AgreeContractSubclause agreeSubclause = null;
				for (Iterator<AnnexSubclause> i = modificationContext.getOwnedAnnexSubclauses().iterator(); i
						.hasNext();) {
					AnnexSubclause sc = i.next();
					if (sc.getName().equalsIgnoreCase("agree")) {
						subclause = (DefaultAnnexSubclause) sc;
						agreeSubclause = (AgreeContractSubclause) subclause.getParsedAnnexSubclause();

						// If an agree statement with this id already exists, remove it
						for (Iterator<SpecStatement> j = ((AgreeContract) agreeSubclause.getContract()).getSpecs()
								.listIterator(); j.hasNext();) {
							final SpecStatement spec = j.next();
							if (spec instanceof NamedSpecStatement) {
								if (((NamedSpecStatement) spec).getName().equalsIgnoreCase(getId())) {
									// found; remove and return
									j.remove();
									if (((AgreeContract) agreeSubclause.getContract()).getSpecs().isEmpty()) {
										i.remove();
									}
									return;
								}
							}
						}

						break;
					}
				}
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
		}

		domain.dispose();

	}

	private void removeAgreeCheckClaim() {

		// Get modification context
		final AadlPackage pkg = CaseUtils.getCaseRequirementsPackage();

		final Resource aadlResource = pkg.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {

				PrivatePackageSection priv8 = pkg.getOwnedPrivateSection();
				if (priv8 == null) {
					priv8 = pkg.createOwnedPrivateSection();
				}

				DefaultAnnexLibrary defResLib = null;
				ResoluteLibrary resLib = null;
				for (AnnexLibrary library : priv8.getOwnedAnnexLibraries()) {
					if (library instanceof DefaultAnnexLibrary && library.getName().equalsIgnoreCase("resolute")) {
						defResLib = (DefaultAnnexLibrary) library;
						resLib = EcoreUtil.copy((ResoluteLibrary) defResLib.getParsedAnnexLibrary());
						break;
					}
				}
				if (defResLib == null) {
					return;
				}
				FunctionDefinition claimDefinition = null;
				for (Iterator<Definition> i = resLib.getDefinitions().iterator(); i.hasNext();) {
					final Definition def = i.next();
					if (def != null && def instanceof FunctionDefinition && def.hasName()
							&& def.getName().equalsIgnoreCase(getId())) {
						claimDefinition = (FunctionDefinition) def;
						i.remove();
						break;
					}
				}
				if (claimDefinition == null) {
					return;
				}

				Expr newExpr = removeClaim(claimDefinition.getBody().getExpr(),
						AgreePropCheckedClaim.AGREE_PROP_CHECKED);
				if (newExpr == null) {
					newExpr = Create.UNDEVELOPED();
				}
				claimDefinition.getBody().setExpr(newExpr);

				resLib.getDefinitions().add(claimDefinition);
				ECollections.sort(resLib.getDefinitions(), alphabetical);
				defResLib.setParsedAnnexLibrary(resLib);
			}

		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
		}

		domain.dispose();

	}

	private ClaimContext createResoluteContext(String contextName, String contextValue) {
		final ClaimContext context = ResoluteFactory.eINSTANCE.createClaimContext();
		context.setName(contextName);
		final StringLiteral stringLiteral = Aadl2Factory.eINSTANCE.createStringLiteral();
		stringLiteral.setValue(getStringLiteral(contextValue));
		final StringExpr stringExpr = ResoluteFactory.eINSTANCE.createStringExpr();
		stringExpr.setVal(stringLiteral);
		context.setExpr(stringExpr);
		return context;
	}



	/**
	 * Removes expressions matching the name argument from the expr argument.
	 * The name argument is a {@link String} that represents the name of the function call (for example, Agree_Prop_Checked).
	 * <p>
	 * This method assumes that the expr argument is composed of {@link FnCallExpr} and {@link BinaryExpr} elements.
	 * @param expr	expression to be modified
	 * @param name	name of the function call to remove
	 * @return		the expression with any references to the name argument removed; could be null if the top-level expression is a function call that matches the name argument.
	 */
	private Expr removeClaim(Expr expr, String name) {
		if (expr instanceof FnCallExpr) {
			final FnCallExpr fnCallExpr = (FnCallExpr) expr;
			if (fnCallExpr.getFn().getName().equalsIgnoreCase(name)) {
				// found name
				expr = null;
			}
		} else if (expr instanceof BinaryExpr) {
			final BinaryExpr binaryExpr = (BinaryExpr) expr;
			final Expr left = removeClaim(binaryExpr.getLeft(), name);
			final Expr right = removeClaim(binaryExpr.getRight(), name);
			if (left == null) {
				// left side is name
				expr = right;
			} else if (right == null) {
				// right side is name
				expr = left;
			} else {
				binaryExpr.setLeft(left);
				binaryExpr.setRight(right);
			}
		} else {
			// unexpected expression
			return null;
		}
		return expr;
	}


	private static String getStringLiteral(String string) {
		return addQuotes ? ("\"" + string + "\"") : string;
	}

	private static String getContext(ClaimBody claimBody, String context) {
		if (claimBody != null) {
			for (NamedElement claimAttribute : claimBody.getAttributes()) {
				if (claimAttribute instanceof ClaimContext) {
					final ClaimContext claimContext = (ClaimContext) claimAttribute;
					if (claimContext.getName().equalsIgnoreCase(context)) {
						if (claimContext.getExpr() instanceof StringExpr) {
							final String val = ((StringExpr) claimContext.getExpr()).getVal().getValue();
							if (addQuotes) {
								return val.substring(1, val.length() - 1);
							} else {
								return val;
							}
						}
					}
				}
			}
		}
		return "";
	}

	private void setClaimContexts(final ClaimBody claimBody) {
		// Annotate claim with requirement information
		final EList<NamedElement> claimAttributes = claimBody.getAttributes();
		claimAttributes.clear();
		claimAttributes.add(createResoluteContext(GENERATED_BY, getTool()));
		claimAttributes.add(
				createResoluteContext(GENERATED_ON, DateFormat.getDateInstance().format(new Date(getDate() * 1000))));
		claimAttributes.add(createResoluteContext(REQ_COMPONENT, this.getContext()));
		claimAttributes.add(
				createResoluteContext(FORMALIZED, (getFormalize() ? TRUE : FALSE)));

	}

	private static IFile getContainingFile(String context) {
		final Classifier classifier = getImplementationClassifier(context);
		if (classifier == null) {
			return null;
		}
		return Filesystem.getFile(classifier.eResource().getURI());
	}

	private static String getSubcomponentQualifiedName(String qualifiedName) {

		if (!qualifiedName.contains("::")) {
			return null;
		}
		final String pkgName = Aadl2Util.getPackageName(qualifiedName);

		final String[] parts = qualifiedName.split("\\.");
		if (parts.length <= 1) {
			return null;
		}
		final String compName = parts[0] + "." + parts[1];

		for (AadlPackage pkg : TraverseProject.getPackagesInProject(TraverseProject.getCurrentProject())) {
			if (pkg.getName().equalsIgnoreCase(pkgName)) {

				for (ComponentImplementation compImpl : EcoreUtil2.getAllContentsOfType(pkg.getOwnedPublicSection(),
						ComponentImplementation.class)) {
					if (compImpl.getQualifiedName().equalsIgnoreCase(compName)) {

						ComponentImplementation ci = compImpl;
						Subcomponent sub = null;
						for (int i = 2; i < parts.length; i++) {
							sub = null;
							for (Subcomponent subcomp : ci.getOwnedSubcomponents()) {
								if (subcomp.getName().equalsIgnoreCase(parts[i])) {
									ci = subcomp.getComponentImplementation();
									sub = subcomp;
									break;
								}
							}
							if (sub == null) {
								return null;
							}
						}
						return sub.getQualifiedName();
					}
				}

				break;
			}
		}

		return null;
	}


	private Classifier getModificationContext() {
		return getImplementationClassifier(context);
	}
	/**
	 * Return the containing component implementation referred to in the qualified name
	 * @param qualifiedName
	 * @return
	 */
	public static Classifier getImplementationClassifier(String qualifiedName) {
		Classifier classifier = null;
		if (!qualifiedName.contains("::")) {
			return null;
		}
		final String pkgName = Aadl2Util.getPackageName(qualifiedName);

		// The qualified name should either refer to a component implementation
		// or a component implementation's subcomponent or connection
		// A component implementation qualified name will appear as <Package>::<Component Implementation>
		// A subcomponent/connection qualified name will appear as <Package>::<Component Implementation>.<Subcomponent/Connection>
		// Note that there can be multiple nested subcomponents, such as <Package>::<Component Implementation>.<Subcomponent>.<Subcomponent
		// Since we want to return the component implementation, we want to remove the subcomponent(s) from the qualified name

		final String[] parts = qualifiedName.split("\\.");
		// Capture just the implementation name
		String compImplName = parts[0];
		if (parts.length > 1) {
			compImplName += "." + parts[1];
		}

		final List<IProject> projects = new ArrayList<IProject>();
		projects.add(TraverseProject.getCurrentProject());
		try {
			for (IProject proj : TraverseProject.getCurrentProject().getReferencedProjects()) {
				if (proj.isOpen()) {
					projects.add(proj);
				} else {
					System.err.println(
							"Referenced project (" + proj.getName() + ") is not open. This may cause an error.");
				}
			}
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		for (IProject proj : projects) {
			for (AadlPackage pkg : TraverseProject.getPackagesInProject(proj)) {
				if (pkg.getName().equalsIgnoreCase(pkgName)) {
					for (Classifier c : EcoreUtil2.getAllContentsOfType(pkg, Classifier.class)) {
						if (c.getQualifiedName().equalsIgnoreCase(compImplName)) {
							classifier = c;
							break;
						}
					}
					break;
				}
			}
		}

		return classifier;
	}

	static Classifier getModificationContext(Resource resource, String qualifiedName) {
		// Get modification context
		Classifier modificationContext = null;
		final TreeIterator<EObject> x = EcoreUtil.getAllContents(resource, true);
		while (x.hasNext()) {
			final EObject next = x.next();
			if (next instanceof NamedElement) {
				final NamedElement nextElement = (NamedElement) next;
				if (nextElement.getQualifiedName() != null
						&& nextElement.getQualifiedName().equalsIgnoreCase(qualifiedName)) {
					if (nextElement instanceof Subcomponent) {
						final Subcomponent sub = (Subcomponent) nextElement;
						modificationContext = sub.getComponentType();
					} else {
						modificationContext = (Classifier) nextElement;
					}
					break;
				}
			}
		}
		return modificationContext;
	}


	/**
	 * Claim String expected format: [type] description
	 * @param id
	 * @param claimString
	 * @param claimBody
	 * @return
	 */
	static CyberRequirement parseClaimString(String id, String claimString, ClaimBody claimBody) {
		if (id == null || claimBody == null || claimString == null || !claimString.matches("\\[.+\\]\\s.+")) {
			throw new RuntimeException("Invalid arguments: (id : " + id + ") (claimString : " + claimString
					+ ") (claimBody : " + claimBody + ")");
		}

		final String dateString = getContext(claimBody, GENERATED_ON);
		long date;
		try {
			date = DateFormat.getDateInstance().parse(dateString).getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Error in parsing date from resolute requirement: " + dateString);
			date = 0L;
		}

		final String tool = getContext(claimBody, GENERATED_BY);
		final String component = getContext(claimBody, REQ_COMPONENT);
		final boolean formalized = getContext(claimBody, FORMALIZED).equalsIgnoreCase(TRUE);
		final String status = CyberRequirement.add;

		final int start = claimString.indexOf('[');
		final int end = claimString.indexOf(']');
		final String text = claimString.substring(end + 1).trim();
		final String type = claimString.substring(start + 1, end).trim();

		return new CyberRequirement(date, tool, status, type, id, text, component, formalized,
				CyberRequirement.notApplicable);
	}
}
