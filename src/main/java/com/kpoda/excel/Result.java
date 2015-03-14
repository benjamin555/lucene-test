package com.kpoda.excel;

import java.util.ArrayList;
import java.util.List;


/**
* @author 陈嘉镇
* @version 创建时间：2015-3-14 下午10:29:54
* @email benjaminchen555@gmail.com
*/
public class Result {
	
	private String fileName;
	private List<String> row;

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setRowBySeperate(String content, String excelSeparete) {
		assert(content!=null);
		assert(excelSeparete!=null);
		
		String[] cells = content.split(excelSeparete);
		row = new ArrayList<String>();
		for (String cell : cells) {
			row.add(cell);
		}
	}

	public List<String> getRow() {
		return row;
	}

	public void setRow(List<String> row) {
		this.row = row;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return "Result [fileName=" + fileName + ", row=" + row + "]";
	}

}
