package com.github.atomicblom.finishingtouch.model;

import com.google.gson.annotations.SerializedName;

public class Artist {
    @SerializedName("Artist")
    private String name;
    @SerializedName("Site")
    private String site;
    @SerializedName("SiteName")
    private String siteName;
    @SerializedName("Decals")
    private ArtistDecal[] decals;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public ArtistDecal[] getDecals() {
        return decals;
    }

    public void setDecals(ArtistDecal[] decals) {
        this.decals = decals;
    }
}
