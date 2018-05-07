package com.chobits.xml;

import java.util.HashMap;
import java.util.Map;

public class XMLUtil {

	private XMLUnicode unicoded = new XMLUnicode();
	
	public XMLUtil() {
	}

	public Map<String, XMLAttribute> splitAttrString(String text) throws Exception{
		Map<String, XMLAttribute> map = new HashMap<String, XMLAttribute>();
		if(text.isEmpty()){
			return map;
		}
		
		int startCutIndex = 0;
		int endCutIndex = 0;
		int tempIndex = 0;
		while(true){
			char c = text.charAt(tempIndex);
			if(c=='='){
				endCutIndex = this.findTailIndex(text, tempIndex);
				String seed = text.substring(startCutIndex, endCutIndex);
				XMLAttribute attr = this.createAttribute(seed);
				map.put(attr.getName(), attr);
				tempIndex = endCutIndex;
				startCutIndex = endCutIndex+1;
			}else{
				tempIndex += 1;
			}
			if(tempIndex>=text.length()-1){
				break;
			}
		}
		return map;
	}
	
	private XMLAttribute createAttribute(String seed) throws Exception{
		int index = seed.indexOf("=");
		String name = seed.substring(0, index).trim();
		String value = seed.substring(index+1, seed.length()).trim();
		value = this.cutValue(value);
		String temp = this.unescapeText(value);
		if(temp.indexOf("<")==-1){
			value = temp;
		}		
		XMLAttribute attr = new XMLAttribute(name, value);
		return attr;
	}
	
	private int findTailIndex(String text, int startIndex) throws Exception{
		int returnIndex = -1;
		
		int dIndex = text.indexOf("\"", startIndex+1);
		int sIndex = text.indexOf("\'", startIndex+1);
		if(dIndex==-1 && sIndex==-1){
			throw new Exception("xml属性文"+text+"格式错误，属性值必须是被单引号或双引号包围");
		}else if(dIndex>-1 && sIndex==-1){
			returnIndex = text.indexOf("\"", dIndex+1)+1;
		}else if(dIndex==-1 && sIndex>-1){
			returnIndex = text.indexOf("\'", sIndex+1)+1;
		}else if(dIndex>-1 && sIndex>-1){
			if(dIndex<sIndex){
				returnIndex = text.indexOf("\"", dIndex+1)+1;
			}else{
				returnIndex = text.indexOf("\'", sIndex+1)+1;
			}
		}
		return returnIndex;
	}
	
	public String cutValue(String text) throws Exception{
		text = text.trim();
		String value = text.substring(1, text.length()-1);
		return value;
	}
	
	public String spillValue(String text){
		String value = "\""+text+"\"";
		return value;
	}
	
	public String unescapeText(String text){
		if(text!=null && !text.isEmpty()){
			text = unicoded.decode(text);
			text = text.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"").replaceAll("&apos;", "'");
		}
		return text;
	}
	
	public String escapeText(String text){
		if(text!=null && !text.isEmpty()){
			text = text.replaceAll("\\\\u", "&#");
			text = text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
		}
		return text;
	}
}
