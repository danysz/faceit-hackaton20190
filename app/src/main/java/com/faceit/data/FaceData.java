package com.faceit.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.vision.face.Face;

import com.faceit.global.Global;

public class FaceData implements Parcelable {
    public void setEyeLeftValue(EyeValue eyeLeftValue) {
        this.eyeLeftValue = eyeLeftValue;
    }

    public void setEyeRightValue(EyeValue eyeRightValue) {
        this.eyeRightValue = eyeRightValue;
    }

    public void setSmile(boolean smile) {
        isSmile = smile;
    }

    public static Creator<FaceData> getCREATOR() {
        return CREATOR;
    }

    private EyeValue eyeLeftValue = EyeValue.OPEN;
    private EyeValue eyeRightValue = EyeValue.OPEN;
    private boolean isSmile = true;

    public FaceData(Face face) {
        float isSmilingValue = face.getIsSmilingProbability();
        float isLeftEyeOpenValue = face.getIsLeftEyeOpenProbability();
        float isRightEyeOpenValue = face.getIsRightEyeOpenProbability();

        isSmile = Global.IsSmile(isSmilingValue);
        eyeLeftValue = EyeValue.getValueEye(isLeftEyeOpenValue);
        eyeRightValue = EyeValue.getValueEye(isRightEyeOpenValue);
    }

    public FaceData() {}


    protected FaceData(Parcel in) {
        eyeLeftValue = EyeValue.valueOf(in.readString());
        eyeRightValue = EyeValue.valueOf(in.readString());
        isSmile = in.readByte() == 0;
    }

    public static final Creator<FaceData> CREATOR = new Creator<FaceData>() {
        @Override
        public FaceData createFromParcel(Parcel in) {
            return new FaceData(in);
        }

        @Override
        public FaceData[] newArray(int size) {
            return new FaceData[size];
        }
    };

    public boolean isSmile() {
        return isSmile;
    }

    public EyeValue getEyeLeftValue() {
        return eyeLeftValue;
    }

    public EyeValue getEyeRightValue() {
        return eyeRightValue;
    }

    @Override
    public String toString() {
        return "FaceData{" +
                "eyeLeftValue=" + eyeLeftValue +
                ", eyeRightValue=" + eyeRightValue +
                ", isSmile=" + isSmile +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eyeLeftValue.toString());
        dest.writeString(eyeRightValue.toString());
        dest.writeByte((byte) (isSmile ? 0 : 1));
    }

    public boolean equals(boolean isSmile, EyeValue left,EyeValue right) {
        return this.isSmile == isSmile && left.equals(this.eyeLeftValue) && right.equals(this.eyeRightValue);
    }

    public enum EyeValue{
        OPEN,CLOSE,OTHER;
        public static EyeValue getValueEye(float value){
            EyeValue eyeValue;
            if (Global.IsEyeClose(value)){
                eyeValue = EyeValue.CLOSE;
            }else if (Global.IsEyeOpen(value)){
                eyeValue = EyeValue.OPEN;
            }else{
                eyeValue = EyeValue.OTHER;
            }
            return eyeValue;
        }
    }


}
