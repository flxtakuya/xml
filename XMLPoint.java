package com.chobits.xml;

public class XMLPoint {
	
	private int startIndex = -1;
	private int endIndex = -1;
	
	public XMLPoint(int startIndex, int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
}
