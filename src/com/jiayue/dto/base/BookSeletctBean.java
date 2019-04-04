package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-10-26 下午1:52:47 项目名称：wyst
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：CourseName.java 类说明：
 *        ------------------------------------------------------------------
 */
public class BookSeletctBean implements Serializable {

	/**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"bookId":1,"bookName":"枯井"},{"bookId":2,"bookName":"大学物理"}]
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
         * bookId : 1
         * bookName : 枯井
         */

        private int bookId;
        private String bookName;

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public int getBookId() {
            return bookId;
        }

        public String getBookName() {
            return bookName;
        }
    }

}
