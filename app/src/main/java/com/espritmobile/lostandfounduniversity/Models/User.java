package com.espritmobile.lostandfounduniversity.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Noor on 05/12/2016.
 */

public class User implements Parcelable {
    private String userId;
    private String email;
    private String username;
    private String password;
    private String telephone ;
    private String university ;
    private String userPicture;
    private String token;

    protected User(Parcel in) {
        userId = in.readString();
        email = in.readString();
        username = in.readString();
        password = in.readString();
        telephone = in.readString();
        university = in.readString();
        userPicture = in.readString();
        token = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public User() {
    }

    public User(String email, String username, String password, String telephone, String university, String userPicture, String token) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.telephone = telephone;
        this.university = university;
        this.userPicture = userPicture;
        this.token = token;
    }

    public User(String userId, String email, String username, String password, String telephone, String university, String userPicture,String token) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.telephone = telephone;
        this.university = university;
        this.userPicture = userPicture;
        this.token = token;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(email);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(telephone);
        parcel.writeString(university);
        parcel.writeString(userPicture);
        parcel.writeString(token);
    }
}
