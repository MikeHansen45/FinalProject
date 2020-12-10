package com.example.finalproject;

public class Covid {

    String country;
    String countryCode;
    String province;
    String city;
    String cityCode;
    String status;
    String date;
    int lat, lon, cases;
    long id;


    Covid(String country, String countryCode, String province, String city, int cases, String date, long id){
        this.country = country;
        this.countryCode = countryCode;
        this.province = province;
        this.city = city;
        this.cases = cases;
        this.date = date;
        this.id = id;
    }

    Covid(String country, String countryCode, String province, String city, String cityCode, int lat, int lon, int cases,  String status, String date){
        this.country = country;
        this.countryCode = countryCode;
        this.province = province;
        this.city = city;
        this.cityCode = cityCode;
        this.lat = lat;
        this.lon = lon;
        this.cases = cases;
        this.status = status;
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String convertDate(){
       return date.replaceAll("[TZ]", " ");
    }

    @Override
    public String toString(){
        if (!province.equals("") || !city.equals("")) {
            return countryCode + ", " + province + ", " + city + " " + cases + " cases, " + convertDate();
        }else if (province.equals("") && city.equals("") && (cases !=0)) {
            return country+ ", " + cases + " cases, " + convertDate();}
        return null;
    }

    public String displayCityProvince(){
        if (city.equals("")) {
            return province;}
        else if(!city.equals("") && !province.equals("")){
            return province +", "+city;
        }
        else{
            return city;
        }
    }

}