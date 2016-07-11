package com.martoio.android.youlocalapplication;

/**
 * Created by Martin on 7/9/2016 for YouLocalApplication.
 *
 * Basic Java object to represent the user returned from the JSON call;
 * Consists of some of the fields with the corresponding getters/setters;
 */
public class YouLocalUser {


    private String mid;
    private double mLatitude;
    private double mLongitude;
    private String mEmail;
    private String mFullName;
    private String mAbout;
    private String mAvatarURL;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getAbout() {
        return mAbout;
    }

    public void setAbout(String about) {
        mAbout = about;
    }

    public String getAvatarURL() {
        return mAvatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        mAvatarURL = avatarURL;
    }
}
