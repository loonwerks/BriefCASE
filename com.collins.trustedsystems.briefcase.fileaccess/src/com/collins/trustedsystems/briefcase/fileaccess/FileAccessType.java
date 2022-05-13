package com.collins.trustedsystems.briefcase.fileaccess;

import java.util.ArrayList;
import java.util.List;

import com.rockwellcollins.atc.resolute.analysis.external.ResoluteExternalFunctionLibraryType;
import com.rockwellcollins.atc.resolute.validation.BaseType;
import com.rockwellcollins.atc.resolute.validation.ListType;
import com.rockwellcollins.atc.resolute.validation.ResoluteType;

public class FileAccessType extends ResoluteExternalFunctionLibraryType {

	@Override
	public ResoluteType getType(String function) {
		switch (function.toLowerCase()) {
		case "canexecute":
		case "canread":
		case "canwrite":
			return BaseType.BOOL;
		case "compareto":
			return BaseType.INT;
		case "fileexists":
			return BaseType.BOOL;
		case "getabsolutepath":
		case "getcontents":
		case "getparent":
			return BaseType.STRING;
		case "getfreespace":
			return BaseType.INT;
		case "getname":
			return BaseType.STRING;
		case "gettotalspace":
		case "getusablespace":
		case "hashcode":
			return BaseType.INT;
		case "isabsolute":
		case "isdirectory":
		case "isfile":
		case "ishidden":
			return BaseType.BOOL;
		case "lastmodified":
		case "length":
			return BaseType.INT;
		case "list":
			return new ListType(BaseType.STRING);
		default:
			return BaseType.FAIL;
		}
	}

	@Override
	public List<ResoluteType> getArgTypes(String function) {
		List<ResoluteType> args = new ArrayList<>();
		switch (function.toLowerCase()) {
		case "canexecute":
		case "canread":
		case "canwrite":
		case "fileexists":
		case "getabsolutepath":
		case "getcontents":
		case "getparent":
		case "getfreespace":
		case "getname":
		case "gettotalspace":
		case "getusablespace":
		case "hashcode":
		case "isabsolute":
		case "isdirectory":
		case "isfile":
		case "ishidden":
		case "lastmodified":
		case "length":
		case "list":
			args.add(BaseType.STRING);
			break;
		case "compareto":
			args.add(BaseType.STRING);
			args.add(BaseType.STRING);
			break;
		default:
			args = null;
		}
		return args;
	}

}
