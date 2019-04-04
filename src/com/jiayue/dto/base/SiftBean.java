package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class SiftBean implements Serializable{
	/**
	   * code : SUCCESS
	   * codeInfo : 操作成功!
	   * data : {"zipSavePath":"attachdir/63/63.zip","lastTime":{"date":8,"day":1,"hours":15,"minutes":58,"month":4,"seconds":6,"time":1494230286000,"timezoneOffset":-480,"year":117},"correspondence":[{"siftName":"52658668-22c1-4481-9329-91a7a50b1cf0.sift","SiftType":"AttachOne","attachFlag":"1_10"},{"siftName":"e68b5cf3-0a7d-419d-982c-763bf9e29b1f.sift","SiftType":"AttachTwo","attachFlag":"b3_3"}]}
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

	  public static class Data implements Serializable{
	    /**
	     * zipSavePath : /mnt/data/upload/attachdir/63/63.zip
	     * lastTime : {"date":8,"day":1,"hours":15,"minutes":58,"month":4,"seconds":6,"time":1494230286000,"timezoneOffset":-480,"year":117}
	     * correspondence : [{"siftName":"52658668-22c1-4481-9329-91a7a50b1cf0.sift","SiftType":"AttachOne","attachFlag":"1_10"},{"siftName":"e68b5cf3-0a7d-419d-982c-763bf9e29b1f.sift","SiftType":"AttachTwo","attachFlag":"b3_3"}]
	     */

	    private String zipSavePath;
	    private LastTime lastTime;
	    private int confidence; 
	    private List<Correspondence> correspondence;

	    public void setZipSavePath(String zipSavePath) {
	      this.zipSavePath = zipSavePath;
	    }

	    public void setLastTime(LastTime lastTime) {
	      this.lastTime = lastTime;
	    }
	    
	    

	    public int getConfidence() {
			return confidence;
		}

		public void setConfidence(int confidence) {
			this.confidence = confidence;
		}

		public void setCorrespondence(List<Correspondence> correspondence) {
	      this.correspondence = correspondence;
	    }

	    public String getZipSavePath() {
	      return zipSavePath;
	    }

	    public LastTime getLastTime() {
	      return lastTime;
	    }

	    public List<Correspondence> getCorrespondence() {
	      return correspondence;
	    }

	    public static class LastTime implements Serializable{
	      /**
	       * date : 8
	       * day : 1
	       * hours : 15
	       * minutes : 58
	       * month : 4
	       * seconds : 6
	       * time : 1494230286000
	       * timezoneOffset : -480
	       * year : 117
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

	    public static class Correspondence implements Serializable{
	      /**
	       * siftName : 52658668-22c1-4481-9329-91a7a50b1cf0.sift
	       * SiftType : AttachOne
	       * attachFlag : 1_10
	       */

	      private String siftName;
	      private String SiftType;
	      private String attachFlag;
	      private String imageName;

	      
	      public String getImageName() {
			return imageName;
		}

		public void setImageName(String imageName) {
			this.imageName = imageName;
		}

		public void setSiftName(String siftName) {
	        this.siftName = siftName;
	      }

	      public void setSiftType(String SiftType) {
	        this.SiftType = SiftType;
	      }

	      public void setAttachFlag(String attachFlag) {
	        this.attachFlag = attachFlag;
	      }

	      public String getSiftName() {
	        return siftName;
	      }

	      public String getSiftType() {
	        return SiftType;
	      }

	      public String getAttachFlag() {
	        return attachFlag;
	      }
	    }
	  }
}
