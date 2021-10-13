package kr.ac.ksa.skin_censor_classifier_test1.dto;

import android.graphics.drawable.Drawable;

public class ListViewDTO {
    private int idx;
    private Drawable image;
    private String result;
    private String date;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}
