package com.django.mod.Model;

public class Song implements  Comparable<Song>{
    long id;
    String title;
    String album;
    String artist;
    String path;



    String cleantitle;

    public Song(long id,String title, String album, String artist, String path) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.path = path;
        this.id=id;
        setCleantitle();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCleantitle() {
        return cleantitle;
    }

    public void setCleantitle() {

        //cleantitle = path.substring(path.lastIndexOf("/") + 1);
        //cleantitle=cleantitle.substring(0,cleantitle.length()-4);

        cleantitle=title;
        if (cleantitle.charAt(0)=='"')
            cleantitle=cleantitle.substring(1);
        if (cleantitle.length()>35) {
            cleantitle = cleantitle.substring(0, 35);
            cleantitle=cleantitle.concat("...");
        }
        //cleantitle.indexOf('\n');
    }


    @Override
    public int compareTo(Song o) {

        return this.cleantitle.compareTo(o.cleantitle);
    }
}
