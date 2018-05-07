package com.chobits.xml;

public class XMLDoctype {

	private String name = "web-app";
	private String type = "PUBLIC";
	private String ims = "//UNKNOWN/";
	private String dtd = "unknown.dtd";
	
	public void build(String context) throws Exception{
		String[] array = context.split(" ");
		if(array.length!=4){
			throw new Exception("<!DOCTYPE声明错误，正确格式如 <!DOCTYPE web-app PUBLIC \"//UNKNOWN/\" \"unknown.dtd\">");
		}
		
		XMLUtil xu = new XMLUtil();
		name = xu.cutValue(array[0]);
		type = xu.cutValue(array[1]);
		ims = xu.cutValue(array[2]);
		dtd = xu.cutValue(array[3]);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIms() {
		return ims;
	}

	public void setIms(String ims) {
		this.ims = ims;
	}

	public String getDtd() {
		return dtd;
	}

	public void setDtd(String dtd) {
		this.dtd = dtd;
	}
	
	@Override
	public String toString(){
		return "<!DOCTYPE "+name+" "+type+" \""+ims+"\" \""+dtd+"\">";
	}
}
