package net.ysuga;

import java.util.ArrayList;
import java.util.Arrays;

import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.SysmlModelEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IValueType;
import com.change_vision.jude.api.inf.model.IValueTypeProperty;

public class RosMsgManager {

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
	    System.out.println("RosMsgManager.addValueType(" + fullname + ")");
		if (Arrays.asList(primitiveTypes).contains(fullname)) {
		    System.out.println(" - input message is primitive type.");
			return true;
		}
		System.out.println(" - Astah calls 'rosmsg show " + fullname + "' command...");		
		String output = ProcessManager.rosmsg_show(fullname);
		System.out.println(" - Command output is :\n" + output + "\n");
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
					newPackage = AstahUtility.getPackageFromModel(currentPackage, tokens[i]);
				}

				currentPackage = newPackage;
			}
			
			
			IValueType value = sysmlModelEditor.createValueType(currentPackage, tokens[tokens.length-1]);
			for(NamedValue nv : parser.parameters) {
				RosMsgManager.addValueType(model, nv.getFullType());
				IValueType vt = AstahUtility.getValueTypeFromModel(model, nv.getFullType());
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
	

	

}
