package com.jiayue.dto.base;

import java.util.List;

/**
 * Created by BAO on 2018-06-21.
 */

public class RecordBean {


    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"id":5,"attachCode":"20185211526892009517","realPrice":0,"payType":0,"phoneType":1,"payTime":{"date":29,"day":5,"hours":0,"minutes":0,"month":11,"seconds":0,"time":-2209276800000,"timezoneOffset":-480,"year":-1},"attachList":[{"attachId":2,"attachName":"wqewq","attachOrderId":5,"attachType":0,"bookId":"2","id":3,"price":0},{"attachId":2,"attachName":"wqewq","attachOrderId":5,"attachType":0,"bookId":"2","id":4,"price":0},{"attachId":8,"attachName":"309a","attachOrderId":5,"attachType":1,"bookId":"1","id":6,"price":0},{"attachId":4,"attachName":"tea","attachOrderId":5,"attachType":2,"bookId":"1","id":7,"price":0}]}]
     */

    private String code;
    private String codeInfo;
    private List<Data> data;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public List<Data> getData() {
        return data;
    }

    public static class Data {
        /**
         * id : 5
         * attachCode : 20185211526892009517
         * realPrice : 0.0
         * payType : 0
         * phoneType : 1
         * payTime : {"date":29,"day":5,"hours":0,"minutes":0,"month":11,"seconds":0,"time":-2209276800000,"timezoneOffset":-480,"year":-1}
         * attachList : [{"attachId":2,"attachName":"wqewq","attachOrderId":5,"attachType":0,"bookId":"2","id":3,"price":0},{"attachId":2,"attachName":"wqewq","attachOrderId":5,"attachType":0,"bookId":"2","id":4,"price":0},{"attachId":8,"attachName":"309a","attachOrderId":5,"attachType":1,"bookId":"1","id":6,"price":0},{"attachId":4,"attachName":"tea","attachOrderId":5,"attachType":2,"bookId":"1","id":7,"price":0}]
         */

        private int id;
        private String attachCode;
        private double realPrice;
        private int payType;
        private int phoneType;
        private PayTime payTime;
        private List<AttachList> attachList;

        public void setId(int id) {
            this.id = id;
        }

        public void setAttachCode(String attachCode) {
            this.attachCode = attachCode;
        }

        public void setRealPrice(double realPrice) {
            this.realPrice = realPrice;
        }

        public void setPayType(int payType) {
            this.payType = payType;
        }

        public void setPhoneType(int phoneType) {
            this.phoneType = phoneType;
        }

        public void setPayTime(PayTime payTime) {
            this.payTime = payTime;
        }

        public void setAttachList(List<AttachList> attachList) {
            this.attachList = attachList;
        }

        public int getId() {
            return id;
        }

        public String getAttachCode() {
            return attachCode;
        }

        public double getRealPrice() {
            return realPrice;
        }

        public int getPayType() {
            return payType;
        }

        public int getPhoneType() {
            return phoneType;
        }

        public PayTime getPayTime() {
            return payTime;
        }

        public List<AttachList> getAttachList() {
            return attachList;
        }

        public static class PayTime {
            /**
             * date : 29
             * day : 5
             * hours : 0
             * minutes : 0
             * month : 11
             * seconds : 0
             * time : -2209276800000
             * timezoneOffset : -480
             * year : -1
             */

            private int date;
            private int day;
            private int hours;
            private int minutes;
            private int month;
            private int seconds;
            private long time;
            private int timezoneOffset;
            private int year;

            public void setDate(int date) {
                this.date = date;
            }

            public void setDay(int day) {
                this.day = day;
            }

            public void setHours(int hours) {
                this.hours = hours;
            }

            public void setMinutes(int minutes) {
                this.minutes = minutes;
            }

            public void setMonth(int month) {
                this.month = month;
            }

            public void setSeconds(int seconds) {
                this.seconds = seconds;
            }

            public void setTime(long time) {
                this.time = time;
            }

            public void setTimezoneOffset(int timezoneOffset) {
                this.timezoneOffset = timezoneOffset;
            }

            public void setYear(int year) {
                this.year = year;
            }

            public int getDate() {
                return date;
            }

            public int getDay() {
                return day;
            }

            public int getHours() {
                return hours;
            }

            public int getMinutes() {
                return minutes;
            }

            public int getMonth() {
                return month;
            }

            public int getSeconds() {
                return seconds;
            }

            public long getTime() {
                return time;
            }

            public int getTimezoneOffset() {
                return timezoneOffset;
            }

            public int getYear() {
                return year;
            }
        }

        public static class AttachList {
            /**
             * attachId : 2
             * attachName : wqewq
             * attachOrderId : 5
             * attachType : 0
             * bookId : 2
             * id : 3
             * price : 0
             */
            private String groupId;
            private List<GroupList> groupList;

            public String getGroupId() {
                return groupId;
            }

            public void setGroupId(String groupId) {
                this.groupId = groupId;
            }

            public List<GroupList> getGroupList() {
                return groupList;
            }

            public void setGroupList(List<GroupList> groupList) {
                this.groupList = groupList;
            }
        }

        public static class GroupList {
            private int attachId;
            private String attachName;
            private int attachOrderId;
            private int attachType;
            private int id;
            private float price;

            public void setAttachId(int attachId) {
                this.attachId = attachId;
            }

            public void setAttachName(String attachName) {
                this.attachName = attachName;
            }

            public void setAttachOrderId(int attachOrderId) {
                this.attachOrderId = attachOrderId;
            }

            public void setAttachType(int attachType) {
                this.attachType = attachType;
            }

            public void setId(int id) {
                this.id = id;
            }

            public void setPrice(float price) {
                this.price = price;
            }

            public int getAttachId() {
                return attachId;
            }

            public String getAttachName() {
                return attachName;
            }

            public int getAttachOrderId() {
                return attachOrderId;
            }

            public int getAttachType() {
                return attachType;
            }


            public int getId() {
                return id;
            }

            public float getPrice() {
                return price;
            }
        }
    }
}
