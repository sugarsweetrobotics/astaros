package net.ysuga;

import java.util.LinkedList;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IValueType;

public class AstahUtility {

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
			currentPackage = AstahUtility.getPackageFromModel(currentPackage, tokens[i]);
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

	public static INamedElement[] getStereoTypedElement(IPackage pack, String string) {
		LinkedList<INamedElement> nelist = new LinkedList<INamedElement>();
		for(INamedElement ne : pack.getOwnedElements()) {
			for(String st : ne.getStereotypes()) {
				if (st.equals(string)) {
					nelist.add(ne);
				}
			}
		}
		return nelist.toArray(new INamedElement[nelist.size()]);
	}
	

	public static IAttribute[] getStereoTypedParts (IBlock block, String string) {
		String[] keys = {string};
		return getStereoTypedParts(block, keys);
	}
	
	public static IAttribute[] getStereoTypedParts (IBlock block, String[] strings) {
		LinkedList<IAttribute> alist = new LinkedList<IAttribute>();
		for(IAttribute att : block.getParts()) {
			for(String key : strings) {
				for(String st : att.getType().getStereotypes()) {
					if (st.equals(key)) {
						alist.add(att);
					}
				}
			}
		}
		return alist.toArray(new IAttribute[alist.size()]);
	}
	
}
