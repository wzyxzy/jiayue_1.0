package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class ShopListCartBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ShopListAddTimeBean addTime;
	private String cartCode;
	private String id;
	private List<ShopListBean> items;
	private ShopListUpdateTimeBean updateTime;
	private String userId;

	public ShopListAddTimeBean getAddTime() {
		return addTime;
	}

	public void setAddTime(ShopListAddTimeBean addTime) {
		this.addTime = addTime;
	}

	public String getCartCode() {
		return cartCode;
	}

	public void setCartCode(String cartCode) {
		this.cartCode = cartCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ShopListBean> getItems() {
		return items;
	}

	public void setItems(List<ShopListBean> items) {
		this.items = items;
	}

	public ShopListUpdateTimeBean getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(ShopListUpdateTimeBean updateTime) {
		this.updateTime = updateTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
