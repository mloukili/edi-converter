package com.beesphere.edi.dialect;

public abstract class AbstractDialect implements Dialect {
	
	private static final long serialVersionUID = 6776067816650306860L;

	protected String segment = "~";
	protected String composite = "*";
	protected String field = "^";
	protected String subField = "&";
	protected String escape;

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getComposite() {
        return composite;
    }

    public void setComposite(String composite) {
        this.composite = composite;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getSubField() {
        return subField;
    }
    
    public void setSubField(String subField) {
        this.subField = subField;
    }

    public String getEscape() {
        return escape;
    }

    public void setEscape(String escape) {
        this.escape = escape;
    }
    
    public String toString () {
    	return "Segment(" + segment + "), Composite(" + composite + "), Field(" + field + "), SubField(" + subField + "), escape (" + escape + ")";
    }
	
}
