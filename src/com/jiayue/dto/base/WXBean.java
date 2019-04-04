package com.jiayue.dto.base;

/**
 * Created by BAO on 2018-06-25.
 */

public class WXBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"appid":"wxb5c69e77124b6557","noncestr":"1011725670","package":"Sign=WXPay","partnerid":"1299218901","prepayid":"wx221149213802708e44f810942443092087","sign":"43B26A82833362B33DC012D959C1D890","timestamp":"1529639370"}
     */

    private String code;
    private String codeInfo;
    private Data data;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        /**
         * appid : wxb5c69e77124b6557
         * noncestr : 1011725670
         * package : Sign=WXPay
         * partnerid : 1299218901
         * prepayid : wx221149213802708e44f810942443092087
         * sign : 43B26A82833362B33DC012D959C1D890
         * timestamp : 1529639370
         */

        private String appid;
        private String noncestr;
//        @SerializedName("package")
//        private String packageX;
        private String partnerid;
        private String prepayid;
        private String sign;
        private String timestamp;

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

//        public void setPackageX(String packageX) {
//            this.packageX = packageX;
//        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getAppid() {
            return appid;
        }

        public String getNoncestr() {
            return noncestr;
        }

//        public String getPackageX() {
//            return packageX;
//        }

        public String getPartnerid() {
            return partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public String getSign() {
            return sign;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
