package com.chobits.xml;

public class XMLAttribute {
	
	private String name = "";
	private String value = "";
	
	public XMLAttribute(){		
	}
	
	public XMLAttribute(String name, Object value) throws Exception{
		this.name = name;
		this.value = String.valueOf(value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(Object value) throws Exception {
		this.value = String.valueOf(value);
	}
	
	@Override
	public String toString(){
		return "{\"name\":\""+name+"\", \"value\":\""+value+"\"}";
	}
}
