package com.tracker.covid.models;

public class ConfirmedData extends CommonData {

    private int latestConfirmedCases;

    public int getLatestConfirmedCases() {
        return latestConfirmedCases;
    }

    public void setLatestConfirmedCases(int latestConfirmedCases) {
        this.latestConfirmedCases = latestConfirmedCases;
    }
}
