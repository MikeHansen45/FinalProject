package com.example.finalproject;

import android.graphics.Bitmap;

public class Event {
    private String name, type,url,info;
    private double priceMin, priceMax;
    private Bitmap promotionalImg;

    public Event(String NAME, String TYPE, String URl, String INFO){
        this.name = NAME;
        this.type = TYPE;
        this.url=URl;
        this.info=INFO;
    }

    public String getName(){return this.name;}
    public String getType(){return this.type;}
    public String getURL(){return this.url;}
    public String getInfo(){return this.info;}

    /**
     * @return
     */
    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
