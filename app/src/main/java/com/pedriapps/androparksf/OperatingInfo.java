package com.pedriapps.androparksf;

/**
 * Created by Pedro on 12/7/2015.
 * this object contains information about the operating schedule of a meter.
 */
public class OperatingInfo {
    private String type;
    private String days;
    private String from;
    private String to;
    private String limit;

    /**
     * Constructor.
     * @param schedType type of schedule (Operating Schedule, Alternate or Tow)
     * @param weekdays a String with the applied days separated by commas.
     * @param fromTime the start time.
     * @param toTime the end time.
     * @param timeLimit maximum time allowed
     */
    public OperatingInfo(String schedType, String weekdays, String fromTime, String toTime, String timeLimit) {
        String temp;
        this.type = schedType;
        this.days = weekdays;
        this.from = fromTime;
        this.to = toTime;
        //remove string " minutes" from timeLimit
        this.limit = timeLimit.substring(0, timeLimit.length() - 8);
    }

    /*
    GETTERS
     */
    public String getType() {
        return type;
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

    public String getLimit() {
        return limit;
    }

    /*
    SETTERS
     */
    public void setType(String value) {
        this.type = value;
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

    public void setLimit(String value) {
        this.limit = value;
    }

    /**
     * builds string to output object
     * @return a string representation of the OperatingInfo object.
     */
    public String toString() {
        StringBuilder temp = new StringBuilder();
        temp.append("");

        if (type.equals("Alternate")) {
        }
        else if (type.equals("Tow")) {
            temp.append("Tow zone from ");
        }
        else {
            temp.append(limit);
            temp.append("min max ");
        }

        if (!type.equals("Alternate")) {
            temp.append(from);
            temp.append("-");
            temp.append(to);
            temp.append(" on ");
            temp.append(days);
            temp.append("\n");
        }

        return temp.toString();
    }
}
