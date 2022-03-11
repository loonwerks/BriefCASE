package com.collins.trustedsystems.briefcase.modelaccess;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.analysis.external.ResoluteExternalFunctionLibraryType;
import com.rockwellcollins.atc.resolute.validation.BaseType;
import com.rockwellcollins.atc.resolute.validation.ResoluteType;
import com.rockwellcollins.atc.resolute.validation.SetType;

public class ModelAccessType extends ResoluteExternalFunctionLibraryType {

	@Override
	public ResoluteType getType(String function) {
		switch (function.toLowerCase()) {
		case "getcomponent":
		case "getcomponentfrom":
			return BaseType.COMPONENT;
		case "getconnection":
		case "getconnectionfrom":
			return BaseType.CONNECTION;
		case "getport":
		case "getportfrom":
			return BaseType.FEATURE;
		case "getcomponentset":
		case "getcomponentsetfrom":
			return new SetType(BaseType.COMPONENT);
		default:
			return BaseType.FAIL;
		}
	}

	@Override
	public List<ResoluteType> getArgTypes(String function) {
		List<ResoluteType> args = new ArrayList<>();
		switch (function.toLowerCase()) {
		case "getcomponent":
			args.add(BaseType.STRING);
			break;
		case "getcomponentfrom":
			args.add(BaseType.COMPONENT);
			args.add(BaseType.STRING);
			break;
		case "getconnection":
			args.add(BaseType.STRING);
			break;
		case "getconnectionfrom":
			args.add(BaseType.COMPONENT);
			args.add(BaseType.STRING);
			break;
		case "getport":
			args.add(BaseType.STRING);
			break;
		case "getportfrom":
			args.add(BaseType.COMPONENT);
			args.add(BaseType.STRING);
			break;
		case "getcomponentset":
			args.add(BaseType.STRING);
			break;
		case "getcomponentsetfrom":
			args.add(BaseType.COMPONENT);
			args.add(BaseType.STRING);
			break;
		default:
			args = null;
		}
		return args;
	}

}
