package com.skytree.epubtest;

import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class CustomFont {
	public String fontFaceName;
	public String fontFileName;

	CustomFont(String faceName, String fileName) {
		this.fontFaceName = faceName;
		this.fontFileName = fileName;
	}

	public String getFullName() {
		String fullName = "";
		if (fontFileName == null || fontFileName.isEmpty()) {
			fullName = this.fontFaceName;
		} else {
			fullName = this.fontFaceName + "!!!/fonts/" + this.fontFileName;
		}
		return fullName;
	}
}