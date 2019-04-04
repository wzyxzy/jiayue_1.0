package com.jiayue.dto.base;

import java.util.List;

public class CommentBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"author":"小明","bookImgPath":"2015/7/30/images/","bookImg":"32363953-def9-478b-b9c8-09cdde41dc1b.jpg","bookName":"城邑","authorInfo":"本不是我的错，最后却成了我的错！","mainContents":[{"id":"1","title":"fsa","addTime":"2015-08-11","content":"fsd","hasNewReply":"0","QACount":"5"},{"id":"2","title":"safdsaf","addTime":"2015-08-11","content":"fsdfsdfasda\t","hasNewReply":"0","QACount":"7"},{"id":"3","title":"fsdf","addTime":"2015-08-27","content":"fhfjhsdalfs","hasNewReply":"0","QACount":"0"}]}
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
         * author : 小明
         * bookImgPath : 2015/7/30/images/
         * bookImg : 32363953-def9-478b-b9c8-09cdde41dc1b.jpg
         * bookName : 城邑
         * authorInfo : 本不是我的错，最后却成了我的错！
         * mainContents : [{"id":"1","title":"fsa","addTime":"2015-08-11","content":"fsd","hasNewReply":"0","QACount":"5"},{"id":"2","title":"safdsaf","addTime":"2015-08-11","content":"fsdfsdfasda\t","hasNewReply":"0","QACount":"7"},{"id":"3","title":"fsdf","addTime":"2015-08-27","content":"fhfjhsdalfs","hasNewReply":"0","QACount":"0"}]
         */

        private String author;
        private String bookImgPath;
        private String bookImg;
        private String bookName;
        private String authorInfo;
        private List<MainContents> mainContents;

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setBookImgPath(String bookImgPath) {
            this.bookImgPath = bookImgPath;
        }

        public void setBookImg(String bookImg) {
            this.bookImg = bookImg;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public void setAuthorInfo(String authorInfo) {
            this.authorInfo = authorInfo;
        }

        public void setMainContents(List<MainContents> mainContents) {
            this.mainContents = mainContents;
        }

        public String getAuthor() {
            return author;
        }

        public String getBookImgPath() {
            return bookImgPath;
        }

        public String getBookImg() {
            return bookImg;
        }

        public String getBookName() {
            return bookName;
        }

        public String getAuthorInfo() {
            return authorInfo;
        }

        public List<MainContents> getMainContents() {
            return mainContents;
        }

        public static class MainContents {
            /**
             * id : 1
             * title : fsa
             * addTime : 2015-08-11
             * content : fsd
             * hasNewReply : 0
             * QACount : 5
             */

            private String id;
            private String title;
            private String addTime;
            private String content;
            private String hasNewReply;
            private String QACount;

            public void setId(String id) {
                this.id = id;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public void setAddTime(String addTime) {
                this.addTime = addTime;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public void setHasNewReply(String hasNewReply) {
                this.hasNewReply = hasNewReply;
            }

            public void setQACount(String QACount) {
                this.QACount = QACount;
            }

            public String getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getAddTime() {
                return addTime;
            }

            public String getContent() {
                return content;
            }

            public String getHasNewReply() {
                return hasNewReply;
            }

            public String getQACount() {
                return QACount;
            }
        }
    }
}
