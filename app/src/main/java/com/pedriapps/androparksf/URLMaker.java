package com.pedriapps.androparksf;

import android.net.Uri;
import android.util.Log;

/*
 * Created by Pedro on 12/3/2015.
 */
public class URLMaker {

    private static URLMaker instance = new URLMaker();

    private URLMaker() {}

    /**
     * retrieves the instance of URLMaker
     * @return the instance of the URLMaker
     */
    public static URLMaker getInstance() {
        return instance;
    }

    /**
     * Creates an URL string for the SFgov OpenData API to lookup a parking meter
     * @param latitude of the location.
     * @param longitude of the location.
     * @param radius of the search in meters.
     * @return the URL string.
     */
    public String makeParkingMetersURL (String latitude, String longitude, String radius) {
        String response = "xml";
        String query =
                "within_circle(location, " + latitude + ", " + longitude + ", " + radius + ")";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("data.sfgov.org")
                .appendPath("resource")
                .appendPath("28my-4796." + response)
                .appendQueryParameter("$where", query);
        String myUrl = builder.build().toString();

        Log.d("URLMaker", myUrl);

        return myUrl;
    }

    /**
     * Creates an URL string for the SFgov OpenData API to get operating schedule
     * @param postID - ID for the meter
     * @return the URL string.
     */
    public String makeOperScheduleURL (String postID) {
        String response = "xml";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("data.sfgov.org")
                .appendPath("resource")
                .appendPath("6cqg-dxku." + response)
                .appendQueryParameter("post_id", postID);
        String myUrl = builder.build().toString();

        Log.d("URLMaker", myUrl);

        return myUrl;
    }

    /**
     * Creates an URL string for the SFgov OpenData API to get rate schedule
     * @param postID - ID for the meter
     * @return the URL string.
     */
    public String makeRateScheduleURL (String postID) {
        String response = "xml";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("data.sfgov.org")
                .appendPath("resource")
                .appendPath("fwjx-32uk." + response)
                .appendQueryParameter("post_id", postID);
        String myUrl = builder.build().toString();

        Log.d("URLMaker", myUrl);

        return myUrl;
    }
}
