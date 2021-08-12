package com.test.aws.lambda.feed.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Country {
    private String id;
    private String name;
    private String brand;
    private String shortDescription;
    private String longDescription;
    private String detailPageName;
    private String[] ports;
    private String[] destinations;
    private String status;
    @SerializedName(":nameHint")
    private String nameHint;
    @SerializedName("jcr:primaryType")
    private final String primaryType = "cq:Page";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        setNameHint(id.toLowerCase());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String[] getPorts() {
        return ports;
    }

    public void setPorts(String[] ports) {
        this.ports = ports;
    }

    public String[] getDestinations() {
        return destinations;
    }

    public void setDestinations(String[] destinations) {
        this.destinations = destinations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getDetailPageName() {
        return detailPageName;
    }

    public void setDetailPageName(String detailPageName) {
        this.detailPageName = detailPageName;
    }

    public String getNameHint() {
        return getId();
    }

    public void setNameHint(String nameHint) {
        this.nameHint = nameHint;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", longDescription='" + longDescription + '\'' +
                ", detailPageName='" + detailPageName + '\'' +
                ", ports=" + Arrays.toString(ports) +
                ", destinations=" + Arrays.toString(destinations) +
                ", status='" + status + '\'' +
                ", nameHint='" + nameHint + '\'' +
                ", primaryType='" + primaryType + '\'' +
                '}';
    }
}
