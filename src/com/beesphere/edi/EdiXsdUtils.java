package com.beesphere.edi;

import com.beesphere.xsd.model.XsdElement;
import com.beesphere.xsd.model.XsdEntity;
import com.beesphere.xsd.model.XsdSet;
import com.beesphere.xsd.model.XsdStandard;

public class EdiXsdUtils {
	
    public static boolean isGroup (XsdEntity entity) {
    	if (entity.getExtraAttribute (EdiNames.QUALIFIER) != null && 
    			entity.getExtraAttribute (EdiNames.QUALIFIER).getValue().equals (EdiNames.GROUP_QUALIFIER)) {
    		return true;
    	}
    	return false;
    }
    
	public static boolean isSegment (XsdEntity entity) {
    	if (entity.getExtraAttribute (EdiNames.QUALIFIER) != null && 
    			entity.getExtraAttribute (EdiNames.QUALIFIER).getValue().equals (EdiNames.SEGMENT_QUALIFIER)) {
    		return true;
    	}
    	return false;
    }

	public static boolean isComposite (XsdEntity entity) {
    	if (entity.getExtraAttribute (EdiNames.QUALIFIER) != null && 
    			entity.getExtraAttribute (EdiNames.QUALIFIER).getValue().equals (EdiNames.COMPOSITE_QUALIFIER)) {
    		return true;
    	}
    	return false;
    }

	public static boolean isSubComposite (XsdEntity entity) {
    	if (entity.getExtraAttribute (EdiNames.QUALIFIER) != null && 
    			entity.getExtraAttribute (EdiNames.QUALIFIER).getValue().equals (EdiNames.SUB_COMPOSITE_QUALIFIER)) {
    		return true;
    	}
    	return false;
    }
	
	public static boolean isField (XsdEntity entity) {
    	if (entity instanceof XsdElement && ((XsdElement)entity).getComplexType () == null && 
    			entity.getExtraAttribute (EdiNames.QUALIFIER) == null) {
    		return true;
    	}
    	return false;
    }
	
	public static XsdSet getSet (XsdElement element) {
	    if (element.getComplexType () == null) {
	    	return null;
	    }
	    return element.getComplexType ().getSet ();
	}
	    

    public static int getMinOccurs (String minOccurs, int defValue) {
    	return getOccurs (minOccurs, true, defValue);
    }
    
    public static int getMaxOccurs (String maxOccurs, int defValue) {
    	return getOccurs (maxOccurs, false, defValue);
    }
    
    private static int getOccurs (String occurs, boolean min, int defValue) {
    	if (occurs == null) {
    		if (min) {
    			return defValue;
    		} else {
    			return defValue;
    		}
    	}
    	if (occurs.equals (XsdStandard.UNBOUNDED)) {
    		return -1;
    	}
    	return Integer.parseInt (occurs);
    }
    
}
