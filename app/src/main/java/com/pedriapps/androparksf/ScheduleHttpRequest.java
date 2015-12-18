package com.pedriapps.androparksf;

/*
 * Created by Pedro on 12/6/2015.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 *  makes an http request through the internet and reads the text on that page into a buffer.
 *  implemented by extending the AsyncTask class, so that it can run asynchronously.
 *  Returns ArrayList<OperatingInfo> object
 */
public class ScheduleHttpRequest extends AsyncTask<String, Void, ArrayList<OperatingInfo>>{

    private Context context;

    public ScheduleHttpRequest(Context context){
        this.context = context;
    }

    /**
     * Checks Internet connection and shows a toast if unable to connect.
     */
    private void checkInternetConnection(){
        ConnectivityManager check =
                (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(check == null)
            Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
    }


    /**
     * accepts the URL to be used as its first parameter.
     * doInBackground opens an internet connection,
     * reads the text on the resulting page on the url to a buffer.
     * after reading a line, it appends it to the String webPage,
     * which will hold the text of the XML file received from loadXmlFromNetwork.
     * @return the ArrayList<OperatingInfo> object created from the XML file info.
     */
    @Override
    protected ArrayList<OperatingInfo> doInBackground(String... urls) {

        ArrayList<OperatingInfo> temp = null;
        try {
            temp = loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
            Log.e("ERROR: ", context.getResources().getString(R.string.connection_error));
        } catch (XmlPullParserException e) {
            Log.e("ERROR: ", context.getResources().getString(R.string.xml_error));
        }
        return temp;
    }


    /**
     * Downloads XML from data.sfgov.org and parses it.
     * @param urlString the URL to use in the request from the network.
     * @return the ArrayList<OperatingInfo> with data filled in by ScheduleXMLParser.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private ArrayList<OperatingInfo> loadXmlFromNetwork(String urlString)
            throws XmlPullParserException, IOException {
        InputStream stream = null;
        ArrayList<OperatingInfo> temp;

        try {
            stream = downloadUrl(urlString);
            temp = ScheduleXMLParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return temp;
    }

    /**
     * Given a string representation of a URL,
     * sets up a connection and gets an input stream.
     * @param urlString the string that represents the URL.
     * @return return value of HttpURLConnection.getInputStream(): an input stream from the url.
     * @throws IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
    //checks internet connection before proceeding with execution.
    protected void onPreExecute(){ checkInternetConnection();}
}
