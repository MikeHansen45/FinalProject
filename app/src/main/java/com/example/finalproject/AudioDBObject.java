package com.example.finalproject;

import org.json.JSONObject;

import static java.lang.Integer.parseInt;

public class AudioDBObject {

    String idAlbum;
    String idArtist;
    String strAlbum;
    String strArtist;
    String intYearReleased;
    String strGenre;
    String strAlbumThumb;
    int yearReleased;

    AudioDBObject(String albumID, String artistID, String albumName, String artistName, String releaseYear, String genre, String imageURL) {
        idAlbum = albumID;
        idArtist = artistID;
        strAlbum = albumName;
        strArtist = artistName;
        intYearReleased = releaseYear;
        strGenre = genre;
        strAlbumThumb = imageURL;

        yearReleased = parseInt(releaseYear);
    }

    public String toString() {
        return strArtist + " - " + strAlbum + " (" + intYearReleased + "), " + strGenre;
    }

    public void setIdAlbum(String id) {
        idAlbum = id;
    }

    public void setIdArtist(String id) {
        idArtist = id;
    }

    public void setAlbum(String album) {
        strAlbum = album;
    }

    public void setArtist(String artist) {
        strArtist = artist;
    }

    public void setyear(String y) {
        intYearReleased = y;
        yearReleased = parseInt(y);
    }

    public void setGenre(String g) {
        strGenre = g;
    }

    public void setAlbumArt(String a) {
        strAlbumThumb = a;
    }

    public String getIdAlbum() {
        return idAlbum;
    }

    public String getIdArtist() {
        return idArtist;
    }

    public String getAlbum() {
        return strAlbum;
    }

    public String getArtist() {
        return strArtist;
    }

    public String getYearString() {
        return intYearReleased;
    }

    public int getYearInt() {
        return yearReleased;
    }

    public String getGenre() {
        return strGenre;
    }

    public String getAlbumThumb() {
        return strAlbumThumb;
    }

}
