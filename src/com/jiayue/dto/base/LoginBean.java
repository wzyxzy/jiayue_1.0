package com.jiayue.dto.base;

public class LoginBean {
	/**
     * code : SUCCESS
     * codeInfo : 用户登陆成功!
     * data : {"userId":"36","userName":"123645543","userEmail":"123@qq.com","userDetail":{"address1":"ds","birthday":{"date":3,"day":5,"hours":0,"minutes":0,"month":1,"seconds":0,"time":602438400000,"timezoneOffset":-480,"year":89},"birthdayShow":"1989-02-03","parents":"s","id":5,"kindergarten":"aaa","level":1,"sex":1,"userId":36,"subscibed":1}}
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
         * userId : 36
         * userName : 123645543
         * userEmail : 123@qq.com
         * userDetail : {"address1":"ds","birthday":{"date":3,"day":5,"hours":0,"minutes":0,"month":1,"seconds":0,"time":602438400000,"timezoneOffset":-480,"year":89},"birthdayShow":"1989-02-03","parents":"s","id":5,"kindergarten":"aaa","level":1,"sex":1,"userId":36,"subscibed":1}
         */

        private String userId;
        private String userName;
        private String userEmail;
        private String password;
        private int userStatus;//1:普通用户 2讲师
        
        

        public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public int getUserStatus() {
			return userStatus;
		}

		public void setUserStatus(int userStatus) {
			this.userStatus = userStatus;
		}

		public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserEmail() {
            return userEmail;
        }
    }
}
