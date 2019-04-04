package com.jiayue.dto.base;

import java.io.Serializable;

public class ShopListDataBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ShopListCartBean cart;

	public ShopListCartBean getCart() {
		return cart;
	}

	public void setCart(ShopListCartBean cart) {
		this.cart = cart;
	}
	
}
