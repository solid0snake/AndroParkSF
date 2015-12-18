package com.pedriapps.androparksf;

import java.util.ArrayList;

/**
 * Created by Pedro on 12/3/2015.
 * this object stores data about a meter.
 */

public class ParkLocation {
    private String streetname = "";
    private String street_num = "";
    private String cap_color = "";
    private String post_id = "";
    private ArrayList<RateInfo> rates;
    private ArrayList<OperatingInfo> schedule;

    /**
     * Default constructor
     */
    public ParkLocation () {}

    /**
     * Constructor for meters
     * @param stName Street name
     * @param stNum Street number
     * @param capColor Color of the meter cap
     * @param postID ID of the meter in database
     */
    public ParkLocation(String stName, String stNum, String capColor, String postID) {
        this.streetname = stName;
        this.street_num = stNum;
        this.cap_color = capColor;
        this.post_id = postID;
    }

    /*
    GETTERS
     */
    public String getStName(){
        return streetname;
    }

    public String getStNum() {
        return street_num.substring(0,street_num.length()-11);
    }

    public String getCapColor() {
        return cap_color;
    }

    public String getPostID() {
        return post_id;
    }

    /**
     * Builds a String to output.
     * @return a String showing rates.
     */
    public String getRates(){
        StringBuilder result = new StringBuilder();
        result.append("\nBASE RATE:\n");
        result.append(rates.get(0).toString());
        result.append("\n");

        // base rate is always first. If more than 1 means
        // it has some overrides to that base rate.
        if (rates.size() > 1) {
            result.append("\nOVERRIDE TO BASE RATE:\n");
            // Loop through elements.
            for (int i = 1; i < rates.size(); i++) {
                result.append(rates.get(i).toString());
                result.append("\n");
            }
        }

        return result.toString();
    }

    /**
     * Builds a String to output.
     * @return a String showing the schedule.
     */
    public String getSchedule() {
        StringBuilder result = new StringBuilder();
        result.append ("OPERATING SCHEDULE:\n");

        for(OperatingInfo obj: schedule) {
            result.append(obj.toString());
        }

        return result.toString();
    }

    /*
    SETTERS
     */
    public void setStreetname(String name) {
        this.streetname = name;
    }

    public void setStreet_num(String num) {
        this.street_num = num;
    }

    public void setCap_color(String color) {
        this.cap_color = color;
    }

    public void setPost_id(String id) {
        this.post_id = id;
    }

    public void setRates(ArrayList<RateInfo> values) {
        this.rates = values;
    }

    public void setSchedule(ArrayList<OperatingInfo> values) {
        this.schedule = values;
    }
}