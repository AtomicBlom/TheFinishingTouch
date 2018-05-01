package com.github.atomicblom.finishingtouch.model;

import com.google.gson.annotations.SerializedName;

public class ArtistDecal {
    @SerializedName("Name")
    private String name;
    @SerializedName("Location")
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
