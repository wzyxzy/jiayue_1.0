package com.jiayue.dto.base;

import java.io.Serializable;

public class WangqiUrlDataBean implements Serializable{

	private String url;
	private int definition;
	private String format;
	private int vbitrate;
	private int vheight;
	private int vwidth;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getDefinition() {
		return definition;
	}
	public void setDefinition(int definition) {
		this.definition = definition;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public int getVbitrate() {
		return vbitrate;
	}
	public void setVbitrate(int vbitrate) {
		this.vbitrate = vbitrate;
	}
	public int getVheight() {
		return vheight;
	}
	public void setVheight(int vheight) {
		this.vheight = vheight;
	}
	public int getVwidth() {
		return vwidth;
	}
	public void setVwidth(int vwidth) {
		this.vwidth = vwidth;
	}
	
	
}
