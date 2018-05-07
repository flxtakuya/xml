package com.chobits.xml;

import java.util.ArrayList;
import java.util.List;

public class XMLUnicode {
	
	public XMLUnicode(){
		
	}
	
	public String decode(String src){
		if(src==null || src.equals("")){
			return "";
		}
		List<Integer> listHeader = new ArrayList<Integer>();
		List<Integer> listTail = new ArrayList<Integer>();
		int startIndex = 0;
		List<Character> listSub = new ArrayList<Character>();
		while(true){
			startIndex = src.indexOf("&#", startIndex);
			if(startIndex==-1){
				break;
			}else{
				int tempEndIndex = this.findEndIndex(src, startIndex+2);
				if(tempEndIndex==-1){
					startIndex += 2;
				}else{
					listHeader.add(startIndex);
					listTail.add(tempEndIndex);
					String sub = src.substring(startIndex+2, tempEndIndex);
					char dest = this.traceValue(sub);
					listSub.add(dest);
					startIndex = tempEndIndex +1;
				}				
			}
		}
		
		String result = "";
		if(listSub.size()==0){
			result = src;
		}else{
			StringBuffer strBuffer = new StringBuffer();
			for(int m=0;m<listSub.size();m++){
				int s = listHeader.get(m);
				int e = listTail.get(m);
				char dest = listSub.get(m);
				if(m==0){
					if(s>0){
						String temp = src.substring(0, s);
						strBuffer.append(temp);
					}
					strBuffer.append(dest);
				}else if(m==listHeader.size()-1){
					strBuffer.append(dest);
					if(e<src.length()-1){
						String temp = src.substring(e+1, src.length());
						strBuffer.append(temp);
					}
				}else{
					strBuffer.append(dest);
					int ns = listHeader.get(m+1);
					String temp = src.substring(e+1, ns);
					strBuffer.append(temp);
				}
			}	
			result = strBuffer.toString();
		}	
		return result;
	}
	
	private int findEndIndex(String src, int startIndex){
		int index = -1;
		for(int i=startIndex;i<src.length();i++){
			char c = src.charAt(i);
			if(c==';'){
				index = i;
				break;
			}
		}
		return index;
	}
	
	private char traceValue(String dest){
		 char c = (char) Integer.parseInt(dest); 
		 return c;
	}
	
	public static void main(String[] args){
		XMLUnicode xu = new XMLUnicode();
		String src = "343432432432";
		System.out.println(src+"|"+src.indexOf("&#"));
		String result = xu.decode(src);
		System.out.println(result);
//		
//	    String s = "&#29305;";  
//	    char c = (char) Integer.parseInt(s.substring(2,s.length()-1));  
//	    System.out.println(c);
	}
}
