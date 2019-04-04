package com.jiayue.dto.base;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PhotoBean implements Parcelable {


    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"attachName":"11111","attachType":"One","confidence":5},{"attachName":"22222","attachType":"Two","confidence":5}]
     */

    private String code;
    private String codeInfo;
    private ArrayList<Data> data;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(code);
        out.writeString(codeInfo);
        out.writeList(data);
    }

    public static final Parcelable.Creator<PhotoBean> CREATOR = new Parcelable.Creator<PhotoBean>() {
        public PhotoBean createFromParcel(Parcel in) {
            return new PhotoBean(in);
        }

        public PhotoBean[] newArray(int size) {
            return new PhotoBean[size];
        }
    };

    @SuppressWarnings("unchecked")
	private PhotoBean(Parcel in) {
        code = in.readString();
        codeInfo = in.readString();
        data = in.readArrayList(Data.class.getClassLoader());
    }

    public static class Data implements Parcelable {
        /**
         * attachName : 11111
         * attachType : One
         * confidence : 5
         */


        private String attachName;
        private String attachType;
        private int confidence;
        private String imagePath;

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public void setAttachName(String attachName) {
            this.attachName = attachName;
        }

        public void setAttachType(String attachType) {
            this.attachType = attachType;
        }

        public void setConfidence(int confidence) {
            this.confidence = confidence;
        }

        public String getAttachName() {
            return attachName;
        }

        public String getAttachType() {
            return attachType;
        }

        public int getConfidence() {
            return confidence;
        }


        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeString(attachName);
            out.writeString(attachType);
            out.writeInt(confidence);
            out.writeString(imagePath);
        }

        public static final Parcelable.Creator<PhotoBean.Data> CREATOR = new Parcelable.Creator<PhotoBean.Data>() {
            public PhotoBean.Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            public PhotoBean.Data[] newArray(int size) {
                return new PhotoBean.Data[size];
            }
        };

        public Data() {
        }

        private Data(Parcel in) {
            attachName = in.readString();
            attachType = in.readString();
            confidence = in.readInt();
            imagePath = in.readString();
        }

    }
}
