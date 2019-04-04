package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BAO on 2018-07-18.
 */

public class RecommendBean {


    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"ordinaryList":[{"bookId":73,"bookName":"音乐等级考试 音乐基础知识 乐理\u2022视唱练耳分册（初级\u2022音乐版）上册","bookIntroduction":"本教材包括音乐学习者必须掌握的基本乐理和视唱练耳的技能和方法。","attachOneId":611,"attachOneName":"序篇","coverPath":"attachRecommend/313c891f-405b-455f-bdc1-703be0ef5a29.jpg","introductionPath":"attachRecommend/34c81a6c-9105-4c2d-bf71-9c251adccf1d.jpg","encodeName":"http://www.pndoo.com/jiayue.html?code=bcad6aa137261aceefc6e661c6eec37d","attachOnePrice":0,"attachTwoList":[{"attachTwoId":368,"attachTwoName":"曲目001.小星星-短","attachTwoPrice":0},{"attachTwoId":369,"attachTwoName":"曲目002.G弦上的咏叹调-长","attachTwoPrice":0}]}],"roundMapList":[{"bookId":63,"bookName":"电力机车制动机（M+Book版）","bookIntroduction":"本书详细介绍了目前我国电力机车典型制动机的构造、控制关系、作用原理、验收规则和典型故障处理等内容。","attachOneId":217,"attachOneName":"第一章","coverPath":"attachRecommend/401797d4-a118-4ade-b637-cd12a3f5b6ed.jpg","introductionPath":"attachRecommend/72e47e01-9146-4c2e-b10b-6a5d2069555c.jpg","encodeName":"http://www.pndoo.com/jiayue.html?code=1425fe54ec29dcd5bcbb6ab55bb04204","attachOnePrice":0,"attachTwoList":[{"attachTwoId":88,"attachTwoName":"图1-3  直通式空气制动机视频","attachTwoPrice":0}]}]}
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
         * ordinaryList : [{"bookId":73,"bookName":"音乐等级考试 音乐基础知识 乐理\u2022视唱练耳分册（初级\u2022音乐版）上册","bookIntroduction":"本教材包括音乐学习者必须掌握的基本乐理和视唱练耳的技能和方法。","attachOneId":611,"attachOneName":"序篇","coverPath":"attachRecommend/313c891f-405b-455f-bdc1-703be0ef5a29.jpg","introductionPath":"attachRecommend/34c81a6c-9105-4c2d-bf71-9c251adccf1d.jpg","encodeName":"http://www.pndoo.com/jiayue.html?code=bcad6aa137261aceefc6e661c6eec37d","attachOnePrice":0,"attachTwoList":[{"attachTwoId":368,"attachTwoName":"曲目001.小星星-短","attachTwoPrice":0},{"attachTwoId":369,"attachTwoName":"曲目002.G弦上的咏叹调-长","attachTwoPrice":0}]}]
         * roundMapList : [{"bookId":63,"bookName":"电力机车制动机（M+Book版）","bookIntroduction":"本书详细介绍了目前我国电力机车典型制动机的构造、控制关系、作用原理、验收规则和典型故障处理等内容。","attachOneId":217,"attachOneName":"第一章","coverPath":"attachRecommend/401797d4-a118-4ade-b637-cd12a3f5b6ed.jpg","introductionPath":"attachRecommend/72e47e01-9146-4c2e-b10b-6a5d2069555c.jpg","encodeName":"http://www.pndoo.com/jiayue.html?code=1425fe54ec29dcd5bcbb6ab55bb04204","attachOnePrice":0,"attachTwoList":[{"attachTwoId":88,"attachTwoName":"图1-3  直通式空气制动机视频","attachTwoPrice":0}]}]
         */

        private List<OrdinaryList> ordinaryList;
        private List<OrdinaryList> roundMapList;

        public void setOrdinaryList(List<OrdinaryList> ordinaryList) {
            this.ordinaryList = ordinaryList;
        }

        public void setRoundMapList(List<OrdinaryList> roundMapList) {
            this.roundMapList = roundMapList;
        }

        public List<OrdinaryList> getOrdinaryList() {
            return ordinaryList;
        }

        public List<OrdinaryList> getRoundMapList() {
            return roundMapList;
        }

        public static class OrdinaryList implements Serializable{
            /**
             * bookId : 73
             * bookName : 音乐等级考试 音乐基础知识 乐理•视唱练耳分册（初级•音乐版）上册
             * bookIntroduction : 本教材包括音乐学习者必须掌握的基本乐理和视唱练耳的技能和方法。
             * attachOneId : 611
             * attachOneName : 序篇
             * coverPath : attachRecommend/313c891f-405b-455f-bdc1-703be0ef5a29.jpg
             * introductionPath : attachRecommend/34c81a6c-9105-4c2d-bf71-9c251adccf1d.jpg
             * encodeName : http://www.pndoo.com/jiayue.html?code=bcad6aa137261aceefc6e661c6eec37d
             * attachOnePrice : 0
             * attachTwoList : [{"attachTwoId":368,"attachTwoName":"曲目001.小星星-短","attachTwoPrice":0},{"attachTwoId":369,"attachTwoName":"曲目002.G弦上的咏叹调-长","attachTwoPrice":0}]
             */

            private int bookId;
            private String bookName;
            private String bookIntroduction;
            private int attachOneId;
            private String attachOneName;
            private String coverPath;
            private String introductionPath;
            private String encodeName;
            private float attachOnePrice;
            private String content;
            private List<AttachTwoList> attachTwoList;
            private String bookImgPath;

            public String getBookImgPath() {
                return bookImgPath;
            }

            public void setBookImgPath(String bookImgPath) {
                this.bookImgPath = bookImgPath;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public void setBookId(int bookId) {
                this.bookId = bookId;
            }

            public void setBookName(String bookName) {
                this.bookName = bookName;
            }

            public void setBookIntroduction(String bookIntroduction) {
                this.bookIntroduction = bookIntroduction;
            }

            public void setAttachOneId(int attachOneId) {
                this.attachOneId = attachOneId;
            }

            public void setAttachOneName(String attachOneName) {
                this.attachOneName = attachOneName;
            }

            public void setCoverPath(String coverPath) {
                this.coverPath = coverPath;
            }

            public void setIntroductionPath(String introductionPath) {
                this.introductionPath = introductionPath;
            }

            public void setEncodeName(String encodeName) {
                this.encodeName = encodeName;
            }

            public void setAttachOnePrice(float attachOnePrice) {
                this.attachOnePrice = attachOnePrice;
            }

            public void setAttachTwoList(List<AttachTwoList> attachTwoList) {
                this.attachTwoList = attachTwoList;
            }

            public int getBookId() {
                return bookId;
            }

            public String getBookName() {
                return bookName;
            }

            public String getBookIntroduction() {
                return bookIntroduction;
            }

            public int getAttachOneId() {
                return attachOneId;
            }

            public String getAttachOneName() {
                return attachOneName;
            }

            public String getCoverPath() {
                return coverPath;
            }

            public String getIntroductionPath() {
                return introductionPath;
            }

            public String getEncodeName() {
                return encodeName;
            }

            public float getAttachOnePrice() {
                return attachOnePrice;
            }

            public List<AttachTwoList> getAttachTwoList() {
                return attachTwoList;
            }

        }

        public static class AttachTwoList implements Serializable{
            /**
             * attachTwoId : 368
             * attachTwoName : 曲目001.小星星-短
             * attachTwoPrice : 0
             */

            private int attachTwoId;
            private String attachTwoName;
            private float attachTwoPrice;

            public void setAttachTwoId(int attachTwoId) {
                this.attachTwoId = attachTwoId;
            }

            public void setAttachTwoName(String attachTwoName) {
                this.attachTwoName = attachTwoName;
            }

            public void setAttachTwoPrice(float attachTwoPrice) {
                this.attachTwoPrice = attachTwoPrice;
            }

            public int getAttachTwoId() {
                return attachTwoId;
            }

            public String getAttachTwoName() {
                return attachTwoName;
            }

            public float getAttachTwoPrice() {
                return attachTwoPrice;
            }
        }
    }
}
