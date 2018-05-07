package com.chobits.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class XMLElement {
	
	private String id = "";
	private Map<String, String> mapAttrs = new HashMap<String, String>();
	private List<XMLElement> children = new ArrayList<XMLElement>();
	private String text = "";
	private String tagName = "";
	private XMLElement parent = null;
	private boolean cdata = false;
	
	public XMLElement() {	
		this.id = UUID.randomUUID().toString();
	}
	
	public void build(XMLNode node, XMLElement parent) throws Exception{
		mapAttrs.clear();
		children.clear();
		
		this.id = node.getId();
		this.parent = parent;
		this.tagName = node.getName();
		this.text = node.getText();
		this.cdata = node.isCdata();
		
		Iterator<String> ite = node.getMapAttrs().keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			String value = node.getMapAttrs().get(key).getValue();
			mapAttrs.put(key, value);
		}
		
		for(int i=0;i<node.getChildren().size();i++){
			XMLNode childNode = node.getChildren().get(i);
			if(childNode.getType()==XMLNode.TYPE_HEAD || childNode.getType()==XMLNode.TYPE_HEAD_ADN_TAIL){
				XMLElement childElem = new XMLElement();
				childElem.build(childNode, this);
				children.add(childElem);
			}			
		}
	}

	public String getText() {
		return text;
	}
	
	public String getTextTrim() {
		return text.trim();
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public XMLElement getParent() {
		return parent;
	}

	public void setParent(XMLElement parent) {
		this.parent = parent;
	}
	
	public String attributeValue(String name){
		return mapAttrs.get(name);
	}
	
	public List<String> getAttributeNames(){
		List<String> list = new ArrayList<String>(mapAttrs.keySet());
		return list;
	}
	
	public Map<String, String> getAttributeMap(){
		return mapAttrs;
	}
	
	public String getAttributeValue(String name){
		return attributeValue(name);
	}
	
	public void addAttribute(String name, String value){
		mapAttrs.put(name, value);
	}
	
	public void setAttribute(String name, String value){
		addAttribute(name, value);
	}
	
	public void add(XMLElement elem){
		children.add(elem);
	}
	
	public void addElement(XMLElement elem){
		add(elem);
	}
	
	public String asXML(){
		XMLUtil xu = new XMLUtil();
		
		String attrsString = "";
		Iterator<String> ite = mapAttrs.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			String value = mapAttrs.get(key);
			attrsString += " "+key+"="+xu.spillValue(value);
		}
		if(attrsString.isEmpty()){
			attrsString = "";
		}
		
		String childrenText = "";
		for(int i=0;i<children.size();i++){
			childrenText += children.get(i).asXML();
		}
		
		String plainText = "";
		if(cdata){
			plainText = "<![CDATA["+text+"]]>";
		}else{
			plainText = text;
		}
		
		String xml = "";
		if(children.size()>0){
			if(plainText.isEmpty()){
				xml += "<"+tagName+""+attrsString+">";
				xml += childrenText;
				xml += "</"+tagName+">";
			}else{
				xml += "<"+tagName+""+attrsString+">";
				xml += childrenText;
				xml += plainText;
				xml += "</"+tagName+">";
			}
		}else{
			if(plainText.isEmpty()){
				xml += "<"+tagName+""+attrsString+"/>";
			}else{
				xml += "<"+tagName+""+attrsString+">"+plainText+"</"+tagName+">";
			}
		}
		return xml;
	}
	
	public String asSecurityXML(){
		XMLUtil xu = new XMLUtil();
		
		String attrsString = "";
		Iterator<String> ite = mapAttrs.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			String value = mapAttrs.get(key);
			value = xu.escapeText(value);
			attrsString += " "+key+"="+xu.spillValue(value);
		}
		if(attrsString.isEmpty()){
			attrsString = "";
		}
		
		String childrenText = "";
		for(int i=0;i<children.size();i++){
			childrenText += children.get(i).asSecurityXML();
		}
		
		String plainText = "";
		if(cdata){
			plainText = "<![CDATA["+xu.escapeText(text)+"]]>";
		}else{
			plainText = xu.escapeText(text);
		}
		
		String xml = "";
		if(children.size()>0){
			if(plainText.isEmpty()){
				xml += "<"+tagName+""+attrsString+">";
				xml += childrenText;
				xml += "</"+tagName+">";
			}else{
				xml += "<"+tagName+""+attrsString+">";
				xml += childrenText;
				xml += plainText;
				xml += "</"+tagName+">";
			}
		}else{
			if(plainText.isEmpty()){
				xml += "<"+tagName+""+attrsString+"/>";
			}else{
				xml += "<"+tagName+""+attrsString+">"+plainText+"</"+tagName+">";
			}
		}
		return xml;
	}

	public XMLElement element(String name){
		XMLElement result = null;
		for(int i=0;i<children.size();i++){
			XMLElement child = children.get(i);
			if(child.getTagName().equals(name)){
				result = child;
				break;
			}
		}
		return result;
	}
	
	public boolean hasAttributes(){
		return mapAttrs.size()>0;
	}
	
	public boolean hasChildren(){
		return children.size()>0;
	}
	
	public boolean hasElement(String name){
		boolean exist = false;
		for(int i=0;i<children.size();i++){
			XMLElement child = children.get(i);
			if(child.getTagName().equals(name)){
				exist = true;
				break;
			}
		}
		return exist;
	}
	
	public boolean containsAttribute(String name){
		return mapAttrs.containsKey(name);
	}
	
	public String elementText(String name){
		XMLElement result = this.element(name);
		return result==null ? null : result.getText();
	}
	
	public String elementTextTrim(String name){
		XMLElement result = this.element(name);
		return result==null ? null : result.getText();
	}
	
	public List<XMLElement> elements(){
		return children;
	}
	
	public List<XMLElement> elements(String name){
		List<XMLElement> array = new ArrayList<XMLElement>();
		for(int i=0;i<children.size();i++){
			XMLElement child = children.get(i);
			if(child.getTagName().equals(name)){
				array.add(child);
			}
		}
		return array;
	}
	
	public Iterator<XMLElement> elementsIterator(){
		return this.elements().iterator();
	}
	
	public Iterator<XMLElement> elementsIterator(String name){
		return elements(name).iterator();
	}
	
	public boolean isCdata() {
		return cdata;
	}

	public void setCDATA(boolean cdata) {
		this.cdata = cdata;
	}
	
	public void addCDATA(String text) {
		this.cdata = true;
		this.text = text;
	}
	
	public void removeAllElements(){
		children.clear();
	}
	
	public void removeElement(XMLElement elem){
		if(elem==null){
			return;
		}
		int index = -1;
		for(int i=0;i<children.size();i++){
			XMLElement child = children.get(i);
			if(child.getId().equals(elem.getId())){
				index = i;
				break;
			}
		}
		if(index>-1){
			children.remove(index);
		}
	}
	
	public boolean removeElement(String name){
		if(name==null || name.isEmpty()){
			return false;
		}
		int index = -1;
		for(int i=0;i<children.size();i++){
			XMLElement child = children.get(i);
			if(child.getTagName().equals(name)){
				index = i;
				break;
			}
		}
		if(index>-1){
			children.remove(index);
			return true;
		}
		return false;
	}
	
	public void removeElements(String name){
		while(true){
			boolean ok = this.removeElement(name);
			if(!ok){
				break;
			}
		}
	}
	
	public int getElementIndex(XMLElement elem){
		int index = -1;
		if(elem==null){
			return -1;
		}
		for(int i=0;i<children.size();i++){
			XMLElement child = children.get(i);
			if(child.getId().equals(elem.getId())){
				index = i;
				break;
			}
		}
		return index;
	}

	public String getId() {
		return id;
	}
}
