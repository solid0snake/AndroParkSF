package com.pedriapps.androparksf;

/**
 * Created by Pedro on 12/6/2015.
 * This object holds information about price rates for a meter parking
 */
public class RateInfo {
    private String type;
    private String rate;
    private String days;
    private String from;
    private String to;

    /**
     * Constructor
     * @param rateType Either 'Base Rate' or 'Override to Base rate'.
     * @param rates The rate in US dollars.
     * @param weekdays a String with the applied days separated by commas.
     * @param fromTime the start time.
     * @param toTime the end time.
     */
    public RateInfo(String rateType, String rates, String weekdays,String fromTime,String toTime) {
        this.type = rateType;
        this.rate = rates;
        this.days = weekdays;
        this.from = fromTime;
        this.to = toTime;
    }

    /*
    GETTERS
     */
    public String getType() {
        return type;
    }

    public String getRate() {
        return rate;
    }

    public String getDays() {
        return days;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    /*
    SETTERS
     */
    public void setType(String value) {
        this.type = value;
    }

    public void setRate(String value) {
        // value is in whole dollars (e.g. 1, 2, 3, etc.)
        if(value.length() == 1) {
            StringBuilder temp = new StringBuilder();
            temp.append(value);
            temp.append(".00");
            value = temp.toString();
        }
        //otherwise it already is in the form 0.00 (e.g. 0.25, 0.50, etc.)
        this.rate = value;
    }

    public void setDays(String value) {
        this.days = value;
    }

    public void setFrom(String value) {
        this.from = value;
    }

    public void setTo(String value) {
        this.to = value;
    }

    /**
     * Builds a String to output.
     * @return a string representation of the RateInfo object.
     */
    public String toString() {

        StringBuilder temp = new StringBuilder();
        temp.append(" $");
        temp.append(rate);
        temp.append("/hr.");

        if(!type.equals("Base Rate")) {
            temp.append(" fr ");
            temp.append(from);
            temp.append(" to ");
            temp.append(to);
            temp.append(" on ");
            temp.append(days);
        }

        return temp.toString();
    }
}