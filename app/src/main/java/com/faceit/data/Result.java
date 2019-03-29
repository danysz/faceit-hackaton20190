package com.faceit.data;

public class Result {

    private FaceData faceData;
    private String pathImage;
    private boolean isSame;

    public Result(FaceData faceData, String pathImage, boolean isSame) {
        this.faceData = faceData;
        this.pathImage = pathImage;
        this.isSame = isSame;
    }

    public FaceData getFaceData() {
        return faceData;
    }

    public void setFaceData(FaceData faceData) {
        this.faceData = faceData;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public boolean isSame() {
        return isSame;
    }

    public void setSame(boolean same) {
        isSame = same;
    }
}
