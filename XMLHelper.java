package com.chobits.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chobits.common.FileUtil;

public class XMLHelper {

	public XMLHelper() {
	}
	
	public static XMLElement parseText(String text) throws Exception{
		XMLDocument document = new XMLDocument();
		document.build(text);
		XMLElement rootElement = document.getRootElement();
		return rootElement;
	}
	
	public static XMLElement parseFile(String path) throws Exception{
		return parseFile(path, "UTF-8");
	}
	
	public static XMLElement parseFile(String path, String charsetName) throws Exception{
		FileUtil fu = new FileUtil();
		byte[] array = fu.readBytes(path);
		String text = new String(array, charsetName);
		return parseText(text);
	}
	
	public static XMLElement parseFile(File file) throws Exception{
		return parseFile(file.getAbsolutePath());
	}
	
	public static XMLElement parseFile(File file, String charsetName) throws Exception{
		return parseFile(file.getAbsolutePath(), charsetName);
	}
	
	public static XMLElement createElement(String name){
		XMLElement elem = new XMLElement();
		elem.setTagName(name);
		return elem;
	}
}
