package com.example.nearblood;

/**
 * Created by DELL on 20-02-2018.
 */

public class Details {
    private String id;
    private String name;
    private String email;
    private String number;
    private String password;
    private String blood;
    private String age;
    private Double latitude;
    private Double longitude;

    
    public Details() {
    }

    public Details(String id, String name, String email, String number, String password1, String blood, String age, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
        this.password = password1;
        this.blood = blood;
        this.age = age;

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }



    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}

