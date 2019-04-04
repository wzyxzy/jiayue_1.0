package com.jiayue.dto.base;

/**
 * Created by BAO on 2018-06-25.
 */

public class AliBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2018050860101131&biz_content=%7B%22body%22%3A%22%E5%9F%8E%E9%82%91%22%2C%22out_trade_no%22%3A%2220185231527059509767%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E5%8A%A0%E9%98%85%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.0%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F101.200.74.204%3A8080%2Fpndoo_server%2Fattachorder.json%3F_method%3DAttachOrderAlipayCallback&sign=Paucsq3sCE%2Fy5Gp8MpWQLPvB3cvMXv188Zu%2B49sHHsUoFFUAJM6PzBdqiqkOR8BhSIN6avpaGgutmxUvQ1ZA4TjL2VjRUphfnMcfZvenq1eTQWjlXiTxWDFyAUoPMPjtBu7RIWjr61AIMBseT6b9F6AbJKgCIDdj14Wbj%2FgJf0QtDEbzNr5CNWO5XnOwi88uy0cUxFbfQUsujbNfTUYF7HACE%2FQmvexh2EMzQvYfGM6W%2B8eTKeMZq9LvuwVF6GpnVRGbi4rxSWcQENhg8cnkRMznP%2BKlqTrP4QSHC5bioyFiVkvmno5WfvYgH3Y3LkiyC9d%2BoVPkakTEwjAlq9nPgQ%3D%3D&sign_type=RSA2&timestamp=2018-05-23+15%3A12%3A25&version=1.0
     */

    private String code;
    private String codeInfo;
    private String data;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public String getData() {
        return data;
    }
}
