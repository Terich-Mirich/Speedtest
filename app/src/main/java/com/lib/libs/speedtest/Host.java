package com.lib.libs.speedtest;

import java.io.Serializable;

public class Host implements Serializable, Comparable<Host> {

    private int id;
    private String host;
    private Double ping;
    private String upLoadAddress;
    private String downLoadAddress;
    private String longitudeCoordinates;
    private String latitudeCoordinates;
    private String cityHost;
    private String countryHost;
    private String countryCodHost;
    private String providerHost;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getLongitudeCoordinates() {
        return longitudeCoordinates;
    }

    public void setLongitudeCoordinates(String longitudeCoordinates) {
        this.longitudeCoordinates = longitudeCoordinates;
    }

    public String getLatitudeCoordinates() {
        return latitudeCoordinates;
    }

    public void setLatitudeCoordinates(String latitudeCoordinates) {
        this.latitudeCoordinates = latitudeCoordinates;
    }

    public String getCityHost() {
        return cityHost;
    }

    public void setCityHost(String cityHost) {
        this.cityHost = cityHost;
    }

    public String getCountryHost() {
        return countryHost;
    }

    public void setCountryHost(String countryHost) {
        this.countryHost = countryHost;
    }

    public String getCountryCodHost() {
        return countryCodHost;
    }

    public void setCountryCodHost(String countryCodHost) {
        this.countryCodHost = countryCodHost;
    }

    public String getProviderHost() {
        return providerHost;
    }

    public void setProviderHost(String providerHost) {
        this.providerHost = providerHost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Double getPing() {
        return ping;
    }

    public void setPing(Double ping) {
        this.ping = ping;
    }

    public String getUpLoadAddress() {
        return upLoadAddress;
    }

    public void setUpLoadAddress(String upLoadAddress) {
        this.upLoadAddress = upLoadAddress;
    }

    public String getDownLoadAddress() {
        return downLoadAddress;
    }

    public void setDownLoadAddress(String downLoadAddress) {
        this.downLoadAddress = downLoadAddress;
    }


    @Override
    public int compareTo(Host host) {
        if (getPing() == null || host.getPing() == null) {
            return 0;
        }
        return getPing().compareTo(host.getPing());
    }
}


