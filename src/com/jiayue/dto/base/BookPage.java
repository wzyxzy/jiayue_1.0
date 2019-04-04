package com.jiayue.dto.base;

import java.util.ArrayList;
import java.util.List;

public class BookPage {

	private int pageNo = 0;
	List<TextPart> parts = new ArrayList<TextPart>();

	public List<TextPart> getParts() {
		return parts;
	}

	public void setParts(List<TextPart> parts) {
		this.parts = parts;
	}
	public void addPart(TextPart part) {
		parts.add(part);
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

}
