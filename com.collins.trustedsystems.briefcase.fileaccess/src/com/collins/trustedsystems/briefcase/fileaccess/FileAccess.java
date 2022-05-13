package com.collins.trustedsystems.briefcase.fileaccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;

import com.rockwellcollins.atc.resolute.analysis.execution.EvaluationContext;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteExternalFunctionLibrary;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteFailException;
import com.rockwellcollins.atc.resolute.analysis.values.BoolValue;
import com.rockwellcollins.atc.resolute.analysis.values.IntValue;
import com.rockwellcollins.atc.resolute.analysis.values.ListValue;
import com.rockwellcollins.atc.resolute.analysis.values.ResoluteValue;
import com.rockwellcollins.atc.resolute.analysis.values.StringValue;

public class FileAccess extends ResoluteExternalFunctionLibrary {

	private EvaluationContext context;

	@Override
	public ResoluteValue run(EvaluationContext context, String function, List<ResoluteValue> args) {

		this.context = context;

		try {
			switch (function.toLowerCase()) {
			case "canexecute": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new BoolValue(getFile(arg0.getString()).canExecute());
			}
			case "canread": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new BoolValue(getFile(arg0.getString()).canRead());
			}
			case "canwrite": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new BoolValue(getFile(arg0.getString()).canWrite());
			}
			case "compareto": {
				final ResoluteValue arg0 = args.get(0);
				final ResoluteValue arg1 = args.get(1);
				assert (arg0.isString());
				assert (arg1.isString());
				return new IntValue(getFile(arg0.getString()).compareTo(getFile(arg1.getString())));
			}
			case "fileexists": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new BoolValue(getFile(arg0.getString()).exists());
			}
			case "getabsolutepath": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new StringValue(getFile(arg0.getString()).getAbsolutePath());
			}
			case "getcontents": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new StringValue(getContents(getFile(arg0.getString())));
			}
			case "getparent": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new StringValue(getFile(arg0.getString()).getParent());
			}
			case "getfreespace": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new IntValue(getFile(arg0.getString()).getFreeSpace());
			}
			case "getname": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new StringValue(getFile(arg0.getString()).getName());
			}
			case "gettotalspace": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new IntValue(getFile(arg0.getString()).getTotalSpace());
			}
			case "getusablespace": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new IntValue(getFile(arg0.getString()).getUsableSpace());
			}
			case "hashcode": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new IntValue(getFile(arg0.getString()).hashCode());
			}
			case "isabsolute": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new BoolValue(getFile(arg0.getString()).isAbsolute());
			}
			case "isdirectory": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new BoolValue(getFile(arg0.getString()).isDirectory());
			}
			case "isfile": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new BoolValue(getFile(arg0.getString()).isFile());
			}
			case "ishidden": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new BoolValue((new File(arg0.getString())).isHidden());
			}
			case "lastmodified": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new IntValue(getFile(arg0.getString()).lastModified());
			}
			case "length": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				return new IntValue(getFile(arg0.getString()).length());
			}
			case "list": {
				final ResoluteValue arg0 = args.get(0);
				assert (arg0.isString());
				final List<StringValue> values = new ArrayList<>();
				for (String value : getFile(arg0.getString()).list()) {
					values.add(new StringValue(value));
				}
				return new ListValue(values);
			}
			}
		} catch (Exception e) {
			throw new ResoluteFailException(e.getMessage(), context.getThisInstance());
		}
		return null;
	}

	private String getContents(File file) throws Exception {
		String contents = "";
		final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (!contents.isEmpty()) {
				contents += System.lineSeparator();
			}
			contents += line;
		}
		bufferedReader.close();
		return contents.toString();
	}

	// Returns a file corresponding to either an absolute or relative path
	private File getFile(String filename) throws Exception {
		File file = new File(filename);
		if (file.exists()) {
			return file;
		} else if (!file.isAbsolute()) {
			final Resource r = context.getThisInstance().eResource();
			final Path p = new Path(r.getURI().segment(1) + File.separator + filename);
			final IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(p);
			if (f.exists()) {
				return f.getRawLocation().makeAbsolute().toFile();
			}
		}

		throw new Exception("Filename " + filename + " not found");

	}

}
