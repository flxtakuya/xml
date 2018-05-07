package com.chobits.xml;

import java.util.Iterator;
import java.util.Map;

public class XMLDeclaration {
	
	private String version = "1.0"; 
	private String encoding = "UTF-8";
	
	public void build(String text) throws Exception{
		String firstNode = "<?xml";
		String lastNode = "?>";
		String context = text.substring(firstNode.length()+1, text.length()-lastNode.length());
		
		XMLUtil xu = new XMLUtil();
		Map<String, XMLAttribute> map = xu.splitAttrString(context);
		Iterator<String> ite = map.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			if(key.toLowerCase().equals("version")){
				version = map.get(key).getValue();
				continue;
			}
			if(key.toLowerCase().equals("encoding")){
				encoding = map.get(key).getValue();
				continue;
			}
		}
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}	
	
	@Override
	public String toString(){
		return "<?xml version=\""+version+"\" encoding=\""+encoding+"\"?>";
	}
}
