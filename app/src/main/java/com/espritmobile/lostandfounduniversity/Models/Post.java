package com.espritmobile.lostandfounduniversity.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gadour on 06/12/2016.
 */

public class Post implements Parcelable {

    private String idPost;
    private String userId;
    private String object;
    private String state;
    private String type;
    private String date;
    private String time;
    private String place;
    private String timePost;
    private String placePost;
    private String postPicture;
    private String description;

    protected Post(Parcel in) {
        idPost = in.readString();
        userId = in.readString();
        object = in.readString();
        state = in.readString();
        type = in.readString();
        date = in.readString();
        time = in.readString();
        place = in.readString();
        timePost = in.readString();
        placePost = in.readString();
        postPicture = in.readString();
        description = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTimePost() {
        return timePost;
    }

    public void setTimePost(String timePost) {
        this.timePost = timePost;
    }

    public String getPlacePost() {
        return placePost;
    }

    public void setPlacePost(String placePost) {
        this.placePost = placePost;
    }

    public String getPostPicture() {
        return postPicture;
    }

    public void setPostPicture(String postPicture) {
        this.postPicture = postPicture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Post() {
    }

    public Post(String userId, String object, String state, String type, String date, String time, String place, String timePost, String placePost, String postPicture, String description) {
        this.userId = userId;
        this.object = object;
        this.state = state;
        this.type = type;
        this.date = date;
        this.time = time;
        this.place = place;
        this.timePost = timePost;
        this.placePost = placePost;
        this.postPicture = postPicture;
        this.description = description;
    }

    public Post(String idPost, String userId, String object, String state, String type, String date, String time, String place, String timePost, String placePost, String postPicture, String description) {
        this.idPost = idPost;
        this.userId = userId;
        this.object = object;
        this.state = state;
        this.type = type;
        this.date = date;
        this.time = time;
        this.place = place;
        this.timePost = timePost;
        this.placePost = placePost;
        this.postPicture = postPicture;
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idPost);
        parcel.writeString(userId);
        parcel.writeString(object);
        parcel.writeString(state);
        parcel.writeString(type);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(place);
        parcel.writeString(timePost);
        parcel.writeString(placePost);
        parcel.writeString(postPicture);
        parcel.writeString(description);
    }

}
