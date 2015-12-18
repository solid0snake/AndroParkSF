package com.pedriapps.androparksf;

/*
 * Created by Pedro on 12/7/2015.
 */

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * The parser class that will parse the response from the server.
 * parse returns an ArrayList of RateInfo objects.
 * readFeed takes an XMLparser as an input and if it finds an entry,
 * it will call the readRates method.
 * readRates looks for the tags for different information in the xml file and
 * sets the data fields of the RateInfo objects.
 * Returns the ArrayList of RateInfo objects.
 */
public class RatesXMLParser {
    //no namespaces
    private static final String ns = null;

    /**
     * sets up the parser to be sent to readFeed method
     * @param in the input stream to be used for the parser
     * @return an ArrayList<RateInfo> object.
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ArrayList<RateInfo> parse(InputStream in)
            throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    /**
     * looks for 'row' tag in XML file.  If success, calls readFeed1.
     * @param parser the XmlPullParser to be used
     * @return an ArrayList<RateInfo> object.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static ArrayList<RateInfo> readFeed(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        ArrayList<RateInfo> entry = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // looks for the row tag
            if (name.equals("row")) {
                entry = readFeed1(parser);
            } else {
                skip(parser);
            }
        }

        return entry;
    }

    /**
     * looks for row tag in XML file.
     * If success, calls readRates to fill out the ArrayList object entryList.
     * @param parser the XmlPullParser to be used
     * @return the ArrayList<RateInfo> object.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static ArrayList<RateInfo> readFeed1(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        ArrayList<RateInfo> entryList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "row");
        while (parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // looks for the row tag
            if (name.equals("row")) {
                entryList.add(readRates(parser));
            } else {
                skip(parser);
            }
        }

        return entryList;
    }

    /**
     * looks for rate_type, rate, days_applied, from_time and to_time tags and
     * then sets those fields in a new RateInfo object.
     * @param parser the XmlPullParser to be used
     * @return A RateInfo object.
     * @throws XmlPullParserException if the parser fails.
     * @throws IOException if it can't read the file.
     */
    private static RateInfo readRates(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "row");
        String type = "";
        String rate = "";
        String days = "";
        String from = "";
        String to = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case "rate_type":
                    type = readType(parser);
                    break;
                case "rate":
                    rate = readRate(parser);
                    break;
                case "days_applied":
                    days = readDays(parser);
                    break;
                case "from_time":
                    from = readFrom(parser);
                    break;
                case "to_time":
                    to = readTo(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new RateInfo(type, rate, days, from, to);
    }

    /**
     * process rate_type tags in the feed.
     * @param parser the XmlPullParser to be used
     * @return the String tagged by rate_type.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readType(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "rate_type");
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "rate_type");
        return value;
    }

    /**
     * process rate tags in the feed.
     * @param parser the XmlPullParser to be used
     * @return the String tagged by rate.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readRate(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "rate");
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "rate");
        return value;
    }

    /**
     * process days_applied tags in the feed.
     * @param parser the XmlPullParser to be used
     * @return the String tagged by days_applied.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readDays(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "days_applied");
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "days_applied");
        return value;
    }

    /**
     * process from_time tags in the feed.
     * @param parser the XmlPullParser to be used
     * @return the String tagged by from_time.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readFrom(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "from_time");
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "from_time");
        return value;
    }

    /**
     * process to_time tags in the feed.
     * @param parser the XmlPullParser to be used
     * @return the String tagged by to_time.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readTo(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "to_time");
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "to_time");
        return value;
    }

    /**
     * gets TEXT from the current tag in the feed.
     * @param parser the XmlPullParser to be used
     * @return the String of text.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readText(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * skip unneeded tags.
     * @param parser the XmlPullParser to be used
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static void skip(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}