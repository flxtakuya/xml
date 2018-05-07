package com.chobits.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class XMLDocument{

	/**
	 * 唯一标识
	 */
	private String id = UUID.randomUUID().toString();
	/**
	 * 从文件或原始字符串原始的字符串
	 */
	private String srcXMLText = "";
	/**
	 * 除了声明，doctype意外的部分
	 */
	private String bodyText = "";
	//声明对象
	private XMLDeclaration declaration = null;
	//语法类型
	private XMLDoctype doctype = null;
	//从第几个字符到哪个字符结束
	private XMLPoint bodyPoint = null;
	//标签和尾标签临时存放标识
	private Map<String, String> matchIdCache = new HashMap<String, String>();
	//
	private XMLNode rootNode = null;
	private XMLNode currentParentNode = null;
	private List<String> notes = new ArrayList<String>();
	private XMLElement rootElement = null;
	
	public XMLDocument() {
	}
	
	public void build(String text) throws Exception{
		matchIdCache.clear();
		notes.clear();
		
		srcXMLText = text;
		adapterHeaderInfo();
		adapterDocType();
		adapterBodyText();
	}

	private void adapterHeaderInfo() throws Exception{
		if(srcXMLText==null || srcXMLText.isEmpty()){
			throw new Exception("xml文本是空的");
		}
		String firstNode = "<?xml";
		String lastNode = "?>";
		int s = srcXMLText.indexOf(firstNode);
		int e = srcXMLText.indexOf(lastNode);
		if(s==-1 && e==-1){
			declaration = new XMLDeclaration();
			bodyPoint = new XMLPoint(0, srcXMLText.length());
		}else{
			if(s>-1 && e>-1 && e>s){
				declaration = new XMLDeclaration();
				declaration.build(srcXMLText.substring(s, e+2));
				bodyPoint = new XMLPoint(e+2, srcXMLText.length());
			}else{
				throw new Exception("xml头声明错误");
			}
		}		
	}
	
	private void adapterDocType() throws Exception{
		String firstNode = "<!DOCTYPE";
		int s = srcXMLText.indexOf(firstNode);
		if(s>-1){
			int e = s;
			while(true){
				e++;
				char c = srcXMLText.charAt(e);
				if(c=='>'){
					break;
				}
			}
			String context = srcXMLText.substring(s+1+firstNode.length(), e).trim();
			doctype = new XMLDoctype();
			doctype.build(context);
			bodyPoint = new XMLPoint(e+1, srcXMLText.length());
		}
	}
	
	private void adapterBodyText() throws Exception{
		bodyText = srcXMLText.substring(bodyPoint.getStartIndex(), bodyPoint.getEndIndex()).trim();
		bodyText = bodyText.replaceAll("\r", "").replaceAll("\n", " ").replaceAll("\t", "").replaceAll("\r\n", " ").replaceAll("  ", " ");
		
		int index = 0;
		while(true){
			char c = bodyText.charAt(index);
			if(c=='<'){
				int skipSize = this.findNode(index);
				index += skipSize;
			}else{
				index ++;
			}
			if(index>=bodyText.length()){
				break;
			}
		}
		rootElement = new XMLElement();
		rootElement.build(rootNode, null);
	}
	
	private int findNode(int startIndex) throws Exception{
		int skipSize = 0;
//		String tempStr = bodyText.substring(startIndex, bodyText.length()).replaceAll(" ", "");
		String tempStr = bodyText.substring(startIndex, bodyText.length());
		if(tempStr.startsWith("<![CDATA[")){
			int tempStartIndex = bodyText.indexOf("<![CDATA[", startIndex);
			int tempEndIndex = bodyText.indexOf("]]>", startIndex);
			String srcXML = bodyText.substring(tempStartIndex, tempEndIndex+3);
			this.acceptCDATA(srcXML, tempStartIndex, tempEndIndex+3);
			skipSize = tempEndIndex+3-tempStartIndex;
		}else{
			for(int i=startIndex;i<bodyText.length();i++){
				char c = bodyText.charAt(i);
				if(c==' '){
					for(int k=i+1;k<bodyText.length();k++){
						char tempChar = bodyText.charAt(k);
						if(tempChar=='>'){
							String seed = bodyText.substring(startIndex, k+1).trim();
							if(seed.indexOf("<!--")==0){
								int tempEndNoteIndex = bodyText.indexOf("-->", startIndex);
								seed = bodyText.substring(startIndex, tempEndNoteIndex+3);
								acceptNote(seed, startIndex, tempEndNoteIndex+3);
								skipSize = tempEndNoteIndex+3-startIndex;
							}else if(seed.indexOf("<![CDATA[")==0){
								int tempEndCdataIndex = bodyText.indexOf("]]>", startIndex);
								seed = bodyText.substring(startIndex, tempEndCdataIndex+3);
								acceptCDATA(seed, startIndex, tempEndCdataIndex+3);
								skipSize = tempEndCdataIndex+3-startIndex;
							}else{
								if(seed.endsWith("/>")){
									this.acceptHeaderAndTailNode(seed, startIndex,  k+1);
									skipSize =  k+1-startIndex;
								}else{
									this.acceptHeaderNode(seed, startIndex, k+1);
									skipSize = k+1-startIndex;
								}
							}						
							break;
						}
					}
					break;
				}
				if(c=='/'){
					char nextChar = bodyText.charAt(i+1);
					if(nextChar=='>'){
						this.acceptHeaderAndTailNode(bodyText.substring(startIndex, i+2), startIndex,  i+2);
						skipSize = i+2-startIndex;
						break;
					}else{
						for(int k=i+1;k<bodyText.length();k++){
							char tempChar = bodyText.charAt(k);
							if(tempChar=='>'){
								acceptTailNode(bodyText.substring(i-1, k+1), i-1, k+1);
								skipSize = k+1-(i-1);
								break;
							}
						}
					}
					break;
				}
				if(c=='>'){
					boolean flag = this.checkHeaderNodeAvalited(bodyText.substring(startIndex, i+1), startIndex, i+1);
					if(flag){
						this.acceptHeaderNode(bodyText.substring(startIndex, i+1), startIndex, i+1);
						skipSize = i+1-startIndex;
						break;
					}else{
						skipSize = i+1-startIndex;
						break;
					}
				}
			}
		}
		return skipSize;
	}
	
	private boolean checkHeaderNodeAvalited(String srcXML, int startIndex, int endIndex) throws Exception{
		String tag = srcXML.substring(1, srcXML.length()-1);
		String tag1 = "</"+tag+">";
		int idx = this.bodyText.substring(startIndex, this.bodyText.length()).indexOf(tag1);
		return idx>-1;
	}
	
	private void acceptHeaderNode(String srcXML, int startIndex, int endIndex) throws Exception{
		XMLNode node = new XMLNode();
		node.setId(UUID.randomUUID().toString());
		node.build(srcXML.trim(), XMLNode.TYPE_HEAD, startIndex, endIndex);

		matchIdCache.put(node.getName(), node.getId());
		
		if(rootNode==null){
			currentParentNode = node;
			rootNode = currentParentNode;
		}else{
			node.setParent(currentParentNode);
			currentParentNode.getChildren().add(node);
			currentParentNode = node;
		}
	}
	
	private void acceptHeaderAndTailNode(String srcXML, int startIndex, int endIndex) throws Exception{
		XMLNode node = new XMLNode();
		node.setId(UUID.randomUUID().toString());
		node.build(srcXML.trim(), XMLNode.TYPE_HEAD_ADN_TAIL, startIndex, endIndex);
		node.setClosed(true);
		
		if(currentParentNode==null){
			currentParentNode = node;
			rootNode = node;
		}else{
			node.setParent(currentParentNode);
			currentParentNode.getChildren().add(node);
		}		
	}
	
	private void acceptCDATA(String srcXML, int startIndex, int endIndex) throws Exception{
		XMLNode node = new XMLNode();
		node.setId(UUID.randomUUID().toString());
		node.build(srcXML.trim(), XMLNode.TYPE_CDATA, startIndex, endIndex);
		node.setClosed(true);
		
		currentParentNode.setCdata(true);
		currentParentNode.getChildren().add(node);
	}
	
	private void acceptNote(String srcXML, int startIndex, int endIndex) throws Exception{
		XMLNode node = new XMLNode();
		node.setId(UUID.randomUUID().toString());
		node.build(srcXML.trim(), XMLNode.TYPE_NOTE, startIndex, endIndex);
		node.setClosed(true);
		
		if(currentParentNode==null){
		}else{
			currentParentNode.getChildren().add(node);
		}
	}
	
	private void acceptTailNode(String srcXML, int startIndex, int endIndex) throws Exception{
		XMLNode node = new XMLNode();
		node.build(srcXML.trim(), XMLNode.TYPE_TAIL, startIndex, endIndex);
		String headerUID = matchIdCache.get(node.getName());
		node.setId(headerUID);
		
		matchIdCache.remove(node.getName());
		node.setClosed(true);
		
		if(currentParentNode.getParent()==null){
		}else{
			currentParentNode.getParent().getChildren().add(node);
		}
		
		if(currentParentNode!=null){
			if(currentParentNode.getChildren().size()>0){
				String text = "";
				
				XMLNode firstChild = currentParentNode.getChildren().get(0);
				text += bodyText.substring(currentParentNode.getEndIndex(), firstChild.getStartIndex()).trim();
				
				XMLNode lastChild = currentParentNode.getChildren().get(currentParentNode.getChildren().size()-1);
				text += bodyText.substring(lastChild.getEndIndex(), startIndex).trim();
				currentParentNode.setText(text.trim());
			}else{
				String text = bodyText.substring(currentParentNode.getEndIndex(), startIndex);
				currentParentNode.setText(text.trim());
			}
			
			currentParentNode.setClosed(true);
			currentParentNode = currentParentNode.getParent();
		}
	}

	public String getId() {
		return id;
	}

	public XMLDeclaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(XMLDeclaration declaration) {
		this.declaration = declaration;
	}

	public XMLDoctype getDoctype() {
		return doctype;
	}

	public void setDoctype(XMLDoctype doctype) {
		this.doctype = doctype;
	}
	
	public XMLElement getRootElement(){
		return rootElement;
	}
	
	public String asXML() throws Exception{
		String xml = "";
		xml += declaration.toString()+"\n";
		xml += doctype.toString()+"\n";
		xml += rootElement.asXML();
		return xml;
	}
	
	public String asSecurityXML(){
		String xml = "";
		xml += declaration.toString()+"\n";
		xml += doctype.toString()+"\n";
		xml += rootElement.asSecurityXML();
		return xml;
	}
	
	public static void main(String[] args){
		try {
			XMLElement root = XMLHelper.parseFile("1.xml");
//			
//			String strtitle = root.elementTextTrim("strtitle");
//			System.out.println("strtitle="+strtitle);
//			
//			String subform = root.elementTextTrim("subform");
//			System.out.println("subform="+subform);
			
			System.out.println("xml="+root.asXML());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
