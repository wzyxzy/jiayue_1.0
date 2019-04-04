package com.jiayue.dto.base;

import java.io.Serializable;

/**
 * Created by BAO on 2016-08-10.
 */
public class GoodsFapiaoBean implements Serializable{
    int id;
    String invoiceTitle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }
}
