package com.example.lab5_starter;

import java.io.Serializable;

// City model class
public class City implements Serializable {
    private String cityName;
    private String province;

    // Firestore requires an empty constructor
    public City() {}

    public City(String cityName, String province) {
        this.cityName = cityName;
        this.province = province;
    }

    public String getCityName() {
        return cityName;
    }

    public String getProvince() {
        return province;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
