package com.chobits.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLNode {

	//uuid
	private String id = "";
	
	public static final int TYPE_DECLARATION = 11;//声明节点
	public static final int TYPE_DOCTYPE = 12;//版本类型节点
	public static final int TYPE_NOTE = 10;//注释
	public static final int TYPE_HEAD = 1;//只能表示头定义的节点
	public static final int TYPE_TAIL = 2;//只能表示尾定义的节点
	public static final int TYPE_HEAD_ADN_TAIL = 3;//既有头也有尾的节点
	public static final int TYPE_CDATA = 4;//cdada文本白话文节点
	
	private String srcXML = "";
	private String name = "";
	private String text = "";
	private int type = -1;
	private int startIndex = -1;
	private int endIndex = -1;
	private XMLNode parent = null;
	private List<XMLNode> children = new ArrayList<XMLNode>();
	private boolean closed = false;
	private boolean cdata = false;
	private Map<String, XMLAttribute> mapAttrs = new HashMap<String, XMLAttribute>();
	private XMLUtil xu = new XMLUtil();
	
	public XMLNode(){		
	}
	
	public void build(String srcXML, int type, int startIndex, int endIndex) throws Exception {
		closed = false;
		text = "";
		this.srcXML = srcXML;
		this.type = type;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		
		String seed = "";
		String[] arrays = null;
		String attrString = "";
		switch(type){
			case TYPE_DECLARATION:
				name = "?xml";
				break;
			case TYPE_DOCTYPE:
				name = "DOCTYPE";
				break;
			case TYPE_NOTE:
				name = "!--";
				break;
			case TYPE_CDATA:
				name = "<![CDATA[";
				break;
			case TYPE_HEAD:
				seed = this.srcXML.substring(1, this.srcXML.length()-1);
				arrays = seed.split(" ");
				name = arrays[0];
				attrString = this.srcXML.substring(name.length()+1, this.srcXML.length()-1).trim();
				if(!attrString.isEmpty()){
					mapAttrs = xu.splitAttrString(attrString);
				}
				break;
			case TYPE_TAIL:
				name = this.srcXML.substring(2, this.srcXML.length()-1);
				break;
			case TYPE_HEAD_ADN_TAIL:
				seed = this.srcXML.substring(1, this.srcXML.length()-2);
				arrays = seed.split(" ");
				name = arrays[0];
				attrString = this.srcXML.substring(name.length()+1, this.srcXML.length()-2).trim();
				if(!attrString.isEmpty()){
					mapAttrs = xu.splitAttrString(attrString);
				}
				break;
		}
	}
	
	public String getName(){
		return name;
	}

	public int getType() {
		return type;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public List<XMLNode> getChildren() {
		return children;
	}

	public XMLNode getParent() {
		return parent;
	}

	public void setParent(XMLNode parent) {
		this.parent = parent;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	@Override
	public String toString(){
		String space = this.getTabSpace();
		String text = this.getText();
		String xml = space + name+" {cdata:"+cdata+", text:"+text+", attrs-count:"+mapAttrs.size()+"}\n";
	
		for(int i=0;i<children.size();i++){
			XMLNode child = children.get(i);
			if(child.getType()==XMLNode.TYPE_HEAD || child.getType()==XMLNode.TYPE_HEAD_ADN_TAIL){
				xml += child.toString();
			}
		}
		return xml;
	}
	
	private String getTabSpace(){
		XMLNode temp = this.parent;
		String space = "";
		while(true){
			if(temp==null){
				break;
			}else{
				space += "\t";
				temp = temp.parent;
			}
		}
		return space;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getSrcXML() {
		return srcXML;
	}

	public boolean isCdata() {
		return cdata;
	}

	public void setCdata(boolean cdata) {
		this.cdata = cdata;
	}

	public String getText() {
		if(cdata){
			for(int i=0;i<children.size();i++){
				XMLNode child = children.get(i);
				if(child.getType()==XMLNode.TYPE_CDATA){
					text = child.getSrcXML().substring("<![CDATA[".length(), child.getSrcXML().length()-"]]>".length());
					break;
				}
			}
		}
		text = xu.unescapeText(text).trim();
//		if(!temp.startsWith("<?xml")){
//			text = xu.unescapeText(text);
//		}
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Map<String, XMLAttribute> getMapAttrs() {
		return mapAttrs;
	}

	public void setMapAttrs(Map<String, XMLAttribute> mapAttrs) {
		this.mapAttrs = mapAttrs;
	}
}
