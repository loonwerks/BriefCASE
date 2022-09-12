package com.collins.trustedsystems.briefcase.assurance;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.analysis.external.ResoluteExternalFunctionLibraryType;
import com.rockwellcollins.atc.resolute.validation.BaseType;
import com.rockwellcollins.atc.resolute.validation.ResoluteType;

public class AssuranceType extends ResoluteExternalFunctionLibraryType {

	@Override
	public ResoluteType getType(String function) {

		switch (function.toLowerCase()) {
		case "security_requirements_imported_or_omitted_with_rationale":
		case "security_analysis_performed_on_current_model":
		case "security_analysis_produces_no_applicable_requirements":
			return BaseType.BOOL;
		case "get_security_requirements_review":
		case "get_agree_results_review":
			return BaseType.STRING;
		default:
			return BaseType.FAIL;
		}
	}

	@Override
	public List<ResoluteType> getArgTypes(String function) {
		List<ResoluteType> args = new ArrayList<>();
		switch (function.toLowerCase()) {
		case "security_requirements_imported_or_omitted_with_rationale":
		case "security_analysis_performed_on_current_model":
		case "security_analysis_produces_no_applicable_requirements":
		case "get_security_requirements_review":
		case "get_agree_results_review":
		default:
			args = null;
		}
		return args;
	}

}
