package com.chobits.xml;

import java.util.List;

public class TestXML {

	public static void main(String[] args){
		try {
			XMLElement root = XMLHelper.parseFile("1.xml");
			
			String strtitle = root.elementTextTrim("strtitle");
			System.out.println("strtitle="+strtitle);
			
			String subform = root.elementTextTrim("subform");
			System.out.println("subform="+subform);
			
			System.out.println("xml="+root.asXML());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
