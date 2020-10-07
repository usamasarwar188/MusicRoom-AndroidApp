package com.django.mod.Model;

public class Status {
    String id;
    String username;
    String dateTime;
    String statusText;
    String songName;
    String audioUrl;
    String nLikes;



    public Status(String id,String username, String dateTime, String statusText, String songName) {
        this.id=id;
        this.username = username;
        this.dateTime = dateTime;
        this.statusText = statusText;
        this.songName = songName;
    }

    public Status(String id,String username, String dateTime, String statusText,String audioUrl, String songName) {
        this.id=id;
        this.username = username;
        this.dateTime = dateTime;
        this.statusText = statusText;
        this.audioUrl=audioUrl;
        this.songName = songName;
    }

    public Status(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }


    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }



    public String getnLikes() {
        return nLikes;
    }

    public void setnLikes(String nLikes) {
        this.nLikes = nLikes;
    }

    public String getnSad() {
        return nSad;
    }

    public void setnSad(String nSad) {
        this.nSad = nSad;
    }

    public String getnHaha() {
        return nHaha;
    }

    public void setnHaha(String nHaha) {
        this.nHaha = nHaha;
    }

    String nSad;
    String nHaha;

}
