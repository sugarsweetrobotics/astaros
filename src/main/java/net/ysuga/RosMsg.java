package net.ysuga;

import java.util.ArrayList;
import java.util.Arrays;

import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.SysmlModelEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IValueType;
import com.change_vision.jude.api.inf.model.IValueTypeProperty;

public class RosMsg {

	static String[] primitiveTypes = { "bool", "int8", "uint8", "int16",
		"uint16", "int32", "uint32", "int64", "uint64", "float32",
		"float64", "string", "time", "duration" };
	
	static public void initializePrimitiveTypes(IModel project) {
		TransactionManager.beginTransaction();
		try {
			SysmlModelEditor sysmlModelEditor = ModelEditorFactory
					.getSysmlModelEditor();
			IPackage currentPackage = (IPackage) project;
			
			for (String t : primitiveTypes) {
				try {
					IValueType valueType = sysmlModelEditor.createValueType(
							currentPackage, t);
				} catch (InvalidEditingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidEditingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TransactionManager.endTransaction();
	}

	static private boolean validateRosMsgOutput(String output) {
		if (output.startsWith("Unable to load msg") || output.length() == 0) {
			return false;
		}
		return true;
	}
	
	private static class NamedValue {
		private String type;
		private String value;
		
		public NamedValue(String t, String v) {
			type = t;
			value = v;
		}
		
		public String getFullType() {
			if(type.endsWith("]")) {
				return type.substring(0, type.indexOf("["));
			}
			return type;
		}
		
		public String getMultiplicity() {
			if(type.endsWith("]")) {
				return type.substring(type.indexOf("[")+1, type.length()-1);
			}
			return "";
		}
		
		public String getType() {
			String[] tokens = type.split("/");
			return tokens[tokens.length -1];
		}
	}
	
	private static class OutputParser {
		ArrayList<NamedValue> parameters;
		public OutputParser(String output) throws InvalidRosMsgException {
			parameters = new ArrayList<NamedValue>();
			String[] tokens = output.split("\n");
			for(String t : tokens) {
				String[] nv = t.split(" ");
				
				if(nv.length == 2) {
					
					parameters.add(new NamedValue(nv[0], nv[1]));
				}
			}
		}
		
		
	}
	
	public static boolean addValueType(IModel model, String fullname) {
		if (Arrays.asList(primitiveTypes).contains(fullname)) {
			return true;
		}
		String output = ProcessManager.rosmsg_show(fullname);
		if (!validateRosMsgOutput(output)) {
			return false;
		}
		OutputParser parser = null;
		try {
			parser = new OutputParser(output);
		} catch (InvalidRosMsgException e1) {
			return false;
		}
		String[] tokens = fullname.split("/");
		
		try {
			SysmlModelEditor sysmlModelEditor = ModelEditorFactory
					.getSysmlModelEditor();

			IPackage currentPackage = (IPackage) model;

			for (int i = 0; i < tokens.length - 1; i++) {
				IPackage newPackage = null;
				try {
					newPackage = sysmlModelEditor.createPackage(currentPackage,
							tokens[i]);
				} catch (InvalidEditingException e) {
					newPackage = null;
				}
				if (newPackage == null) {
					newPackage = getPackageFromModel(currentPackage, tokens[i]);
				}

				currentPackage = newPackage;
			}
			
			
			IValueType value = sysmlModelEditor.createValueType(currentPackage, tokens[tokens.length-1]);
			for(NamedValue nv : parser.parameters) {
				RosMsg.addValueType(model, nv.getFullType());
				IValueType vt = getValueTypeFromModel(model, nv.getFullType());
				IValueTypeProperty vtp = sysmlModelEditor.createValueTypeProperty(value, nv.value, vt);
				String[][] arg = new String[1][1];
				arg[0][0] = nv.getMultiplicity();
				if(arg[0][0].length() != 0) {
				vtp.setMultiplicityStrings(arg);
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (InvalidEditingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
		
		return true;
	}
	
	static public IPackage getPackageFromModel(IPackage currentPackage, String name) {
		for(INamedElement ne : currentPackage.getOwnedElements()) {
			if(ne instanceof IPackage) {
				IPackage p = (IPackage)ne;
				if (p.getName().equals(name)) {
					return p;
				}
			}
		}
		return null;
	}
	
	static public IValueType getValueTypeFromModel(IModel model, String name) {
		String[] tokens = name.split("/");
		IPackage currentPackage = (IPackage) model;
		
		for(int i = 0;i < tokens.length-1;i++) {
			currentPackage = getPackageFromModel(currentPackage, tokens[i]);
			if(currentPackage == null) {
				return null;
			}
		}
		
		for(INamedElement ne : currentPackage.getOwnedElements()) {
			if(ne instanceof IValueType) {
				IValueType v = (IValueType)ne;
				if (v.getName().equals(tokens[tokens.length-1])) {
					return v;
				}
			}
		}
		return null;
		
	}
}
