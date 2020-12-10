package com.example.finalproject;

import java.io.Serializable;

public class TrackObject implements Serializable {

    String idTrack;
    String idAlbum;
    String idArtist;
    String strTrack;
    String strAlbum;
    String strArtist;
    String strGenre;
    String strTrackThumb;
    long id;

    public TrackObject(String trackID, String albumID, String artistID, String trackName, String albumName, String artistName, String genre, String artURL) {
        idTrack = trackID; idAlbum = albumID; idArtist = artistID; strTrack = trackName; strAlbum = albumName; strArtist = artistName; strGenre = genre; strTrackThumb = artURL;
    }

    public TrackObject(String trackID, String albumID, String artistID, String trackName, String albumName, String artistName, String genre, String artURL, long i) {
        idTrack = trackID; idAlbum = albumID; idArtist = artistID; strTrack = trackName; strAlbum = albumName; strArtist = artistName; strGenre = genre; strTrackThumb = artURL;
        id = i;
    }

    public String getIdTrack() {
        return idTrack;
    }

    public void setIdTrack(String idTrack) {
        this.idTrack = idTrack;
    }

    public String getIdArtist() {
        return idArtist;
    }

    public void setIdArtist(String idArtist) {
        this.idArtist = idArtist;
    }

    public String getStrTrack() {
        return strTrack;
    }

    public void setStrTrack(String strTrack) {
        this.strTrack = strTrack;
    }

    public String getStrAlbum() {
        return strAlbum;
    }

    public void setStrAlbum(String strAlbum) {
        this.strAlbum = strAlbum;
    }

    public String getStrArtist() {
        return strArtist;
    }

    public void setStrArtist(String strArtist) {
        this.strArtist = strArtist;
    }

    public String getStrGenre() {
        return strGenre;
    }

    public void setStrGenre(String strGenre) {
        this.strGenre = strGenre;
    }

    public String getStrTrackThumb() {
        return strTrackThumb;
    }

    public void setStrTrackThumb(String strTrackThumb) {
        this.strTrackThumb = strTrackThumb;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
