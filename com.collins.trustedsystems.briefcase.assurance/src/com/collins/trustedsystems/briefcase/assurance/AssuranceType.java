package com.collins.trustedsystems.briefcase.assurance;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.analysis.external.ResoluteExternalFunctionLibraryType;
import com.rockwellcollins.atc.resolute.validation.BaseType;
import com.rockwellcollins.atc.resolute.validation.ResoluteType;

public class AssuranceType extends ResoluteExternalFunctionLibraryType {

	public final static String SECURITY_REQUIREMENTS_IMPORTED_OR_OMITTED_WITH_RATIONALE = "security_requirements_imported_or_omitted_with_rationale";
	public final static String SECURITY_ANALYSIS_PERFORMED_ON_CURRENT_MODEL = "security_analysis_performed_on_current_model";
	public final static String SECURITY_ANALYSIS_PRODUCES_NO_APPLICABLE_REQUIREMENTS = "security_analysis_produces_no_applicable_requirements";
	public final static String SECURITY_REQUIREMENTS_REVIEW = "security_requirements_review";
	public final static String AGREE_RESULTS_REVIEW = "agree_results_review";
	public final static String REQUIREMENTS_STATED_IN_TERMS_OF_INTERFACE = "requirements_stated_in_terms_of_interface";
	public final static String TOOL_GENERATED_APIS_CORRECTLY_DERIVED_FROM_MODEL = "tool_generated_apis_correctly_derived_from_model";
	public final static String APPLICATION_CODE_USES_TOOL_GENERATED_APIS = "application_code_uses_tool_generated_apis";
	public final static String LIBRARY_USAGE_HAS_NO_EFFECT_OUTSIDE_OF_COMPONENT_BOUNDARY = "library_usage_has_no_effect_outside_of_component_boundary";
	public final static String APPLICATION_CODE_RUNS_TO_COMPLETION = "application_code_runs_to_completion";
	public final static String COMPONENT_OUTPUT_SATISFIES_CONTRACT_ON_EVERY_DISPATCH = "component_output_satisfies_contract_on_every_dispatch";
	public final static String COMPONENT_OUTPUT_SATISFIES_INFORMATION_FLOW_SPECIFICATIONS_ON_EVERY_DISPATCH = "component_output_satisfies_information_flow_specifications_on_every_dispatch";
	public final static String COMPONENT_EXECUTION_TIME_CONFORMS_TO_SPECIFIED_WCET_ON_EVERY_DISPATCH = "component_execution_time_conforms_to_specified_wcet_on_every_dispatch";
	public final static String COMPONENT_INFRASTRUCTURE_CODE_SATISFIES_AADL_SEMANTICS = "component_infrastructure_code_satisfies_aadl_semantics";
	public final static String IDENTIFIABLE_COMPONENT_PLATFORM_DEPLOYMENT = "identifiable_component_platform_deployment";
	public final static String APPLICATION_INFRASTRUCTURE_DEPLOYMENT_INTERFACE_IDENTIFICATION = "application_infrastructure_deployment_interface_identification";
	public final static String PORT_CORRESPONDENCE_TRACEABILITY = "port_correspondence_traceability";
	public final static String COMPONENT_DISPATCH = "component_dispatch";
	public final static String INFRASTRUCTURE_APPLICATION_NON_INTERFERENCE = "infrastructure_application_non_interference";
	public final static String SPECIFIED_INFORMATION_FLOW_DIRECTIONALITY_ENFORCED_BY_DEPLOYMENT = "specified_information_flow_directionality_enforced_by_deployment";
	public final static String DEPLOYED_COMPONENT_SATISFIES_FUNCTIONAL_REQUIREMENTS = "deployed_component_satisfies_functional_requirements";
	public final static String DEPLOYED_COMPONENT_SATISFIES_INTRA_COMPONENT_FLOWS = "deployed_component_satisfies_intra_component_flows";
	public final static String PLATFORM_COMPONENTS_GUARANTEE_REQUIRED_PROPERTIES = "platform_components_guarantee_required_properties";

	@Override
	public ResoluteType getType(String function) {

		switch (function.toLowerCase()) {
		case SECURITY_REQUIREMENTS_IMPORTED_OR_OMITTED_WITH_RATIONALE:
		case SECURITY_ANALYSIS_PERFORMED_ON_CURRENT_MODEL:
		case SECURITY_ANALYSIS_PRODUCES_NO_APPLICABLE_REQUIREMENTS:
		case SECURITY_REQUIREMENTS_REVIEW:
		case AGREE_RESULTS_REVIEW:
		case REQUIREMENTS_STATED_IN_TERMS_OF_INTERFACE:
		case TOOL_GENERATED_APIS_CORRECTLY_DERIVED_FROM_MODEL:
		case APPLICATION_CODE_USES_TOOL_GENERATED_APIS:
		case LIBRARY_USAGE_HAS_NO_EFFECT_OUTSIDE_OF_COMPONENT_BOUNDARY:
		case APPLICATION_CODE_RUNS_TO_COMPLETION:
		case COMPONENT_OUTPUT_SATISFIES_CONTRACT_ON_EVERY_DISPATCH:
		case COMPONENT_OUTPUT_SATISFIES_INFORMATION_FLOW_SPECIFICATIONS_ON_EVERY_DISPATCH:
		case COMPONENT_EXECUTION_TIME_CONFORMS_TO_SPECIFIED_WCET_ON_EVERY_DISPATCH:
		case COMPONENT_INFRASTRUCTURE_CODE_SATISFIES_AADL_SEMANTICS:
		case IDENTIFIABLE_COMPONENT_PLATFORM_DEPLOYMENT:
		case APPLICATION_INFRASTRUCTURE_DEPLOYMENT_INTERFACE_IDENTIFICATION:
		case PORT_CORRESPONDENCE_TRACEABILITY:
		case COMPONENT_DISPATCH:
		case INFRASTRUCTURE_APPLICATION_NON_INTERFERENCE:
		case SPECIFIED_INFORMATION_FLOW_DIRECTIONALITY_ENFORCED_BY_DEPLOYMENT:
		case DEPLOYED_COMPONENT_SATISFIES_FUNCTIONAL_REQUIREMENTS:
		case PLATFORM_COMPONENTS_GUARANTEE_REQUIRED_PROPERTIES:
			return BaseType.BOOL;
		default:
			return BaseType.FAIL;
		}
	}

	@Override
	public List<ResoluteType> getArgTypes(String function) {
		List<ResoluteType> args = new ArrayList<>();
		switch (function.toLowerCase()) {
		case SECURITY_REQUIREMENTS_IMPORTED_OR_OMITTED_WITH_RATIONALE:
		case SECURITY_ANALYSIS_PERFORMED_ON_CURRENT_MODEL:
		case SECURITY_ANALYSIS_PRODUCES_NO_APPLICABLE_REQUIREMENTS:
		case SECURITY_REQUIREMENTS_REVIEW:
		case AGREE_RESULTS_REVIEW:
			break;
		case REQUIREMENTS_STATED_IN_TERMS_OF_INTERFACE:
		case TOOL_GENERATED_APIS_CORRECTLY_DERIVED_FROM_MODEL:
		case APPLICATION_CODE_USES_TOOL_GENERATED_APIS:
		case LIBRARY_USAGE_HAS_NO_EFFECT_OUTSIDE_OF_COMPONENT_BOUNDARY:
		case APPLICATION_CODE_RUNS_TO_COMPLETION:
		case COMPONENT_OUTPUT_SATISFIES_CONTRACT_ON_EVERY_DISPATCH:
		case COMPONENT_OUTPUT_SATISFIES_INFORMATION_FLOW_SPECIFICATIONS_ON_EVERY_DISPATCH:
		case COMPONENT_EXECUTION_TIME_CONFORMS_TO_SPECIFIED_WCET_ON_EVERY_DISPATCH:
		case COMPONENT_INFRASTRUCTURE_CODE_SATISFIES_AADL_SEMANTICS:
		case IDENTIFIABLE_COMPONENT_PLATFORM_DEPLOYMENT:
		case APPLICATION_INFRASTRUCTURE_DEPLOYMENT_INTERFACE_IDENTIFICATION:
		case PORT_CORRESPONDENCE_TRACEABILITY:
		case COMPONENT_DISPATCH:
		case INFRASTRUCTURE_APPLICATION_NON_INTERFERENCE:
		case SPECIFIED_INFORMATION_FLOW_DIRECTIONALITY_ENFORCED_BY_DEPLOYMENT:
		case DEPLOYED_COMPONENT_SATISFIES_FUNCTIONAL_REQUIREMENTS:
		case DEPLOYED_COMPONENT_SATISFIES_INTRA_COMPONENT_FLOWS:
			args.add(BaseType.COMPONENT);
			break;
		case PLATFORM_COMPONENTS_GUARANTEE_REQUIRED_PROPERTIES:
			args.add(BaseType.SYSTEM);
			break;
		default:
			args = null;
		}
		return args;
	}

}
