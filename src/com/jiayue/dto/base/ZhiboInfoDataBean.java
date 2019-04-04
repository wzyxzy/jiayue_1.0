package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class ZhiboInfoDataBean implements Serializable{

	private String channel_id;
	private String channel_name;
	private String channel_describe;
	private String channel_status;
	private String hls_downstream_address;
	private String rtmp_downstream_address;
	private String flv_downstream_address;
	private String player_id;
	private String resolution;
	private String password;
	private List<ZhiboSourceBean> upstream_list;
	private String courseName;
	private String courseDisc;
	private int courseId;
	private List<ZhiboAddressBean> multirate_address;
	private String userSig;
	private int admin;


	public int getAdmin() {
		return admin;
	}

	public void setAdmin(int admin) {
		this.admin = admin;
	}

	public String getUserSig() {
		return userSig;
	}

	public void setUserSig(String userSig) {
		this.userSig = userSig;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getChannel_name() {
		return channel_name;
	}
	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}
	public String getChannel_describe() {
		return channel_describe;
	}
	public void setChannel_describe(String channel_describe) {
		this.channel_describe = channel_describe;
	}
	public String getHls_downstream_address() {
		return hls_downstream_address;
	}
	public void setHls_downstream_address(String hls_downstream_address) {
		this.hls_downstream_address = hls_downstream_address;
	}
	public String getChannel_status() {
		return channel_status;
	}
	public void setChannel_status(String channel_status) {
		this.channel_status = channel_status;
	}
	public String getPlayer_id() {
		return player_id;
	}
	public void setPlayer_id(String player_id) {
		this.player_id = player_id;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}
	public List<ZhiboSourceBean> getUpstream_list() {
		return upstream_list;
	}
	public void setUpstream_list(List<ZhiboSourceBean> upstream_list) {
		this.upstream_list = upstream_list;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseDisc() {
		return courseDisc;
	}

	public void setCourseDisc(String courseDisc) {
		this.courseDisc = courseDisc;
	}

	public List<ZhiboAddressBean> getMultirate_address() {
		return multirate_address;
	}

	public void setMultirate_address(List<ZhiboAddressBean> multirate_address) {
		this.multirate_address = multirate_address;
	}

	public String getRtmp_downstream_address() {
		return rtmp_downstream_address;
	}

	public void setRtmp_downstream_address(String rtmp_downstream_address) {
		this.rtmp_downstream_address = rtmp_downstream_address;
	}

	public String getFlv_downstream_address() {
		return flv_downstream_address;
	}

	public void setFlv_downstream_address(String flv_downstream_address) {
		this.flv_downstream_address = flv_downstream_address;
	}
	
}
