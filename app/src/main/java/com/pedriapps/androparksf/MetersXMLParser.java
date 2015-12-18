package com.pedriapps.androparksf;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Pedro on 12/3/2015.
 * The parser class that will parse the response from the server.
 * parse returns a ParkLocation object.
 * readFeed takes an XMLparser as an input and if it finds an entry,
 * it will call the readParkLocation method.
 * readParkLocation looks for the tags for different information in the xml file and
 * sets the data fields of the ParkLocation object.
 * returns the ParkLocation object.
 */
public class MetersXMLParser {
    //no namespaces
    private static final String ns = null;

    /**
     * sets up the parser to be sent to readfeed method
     * @param in the input stream to be used for the parser
     * @return a ParkLocation object.
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ParkLocation parse(InputStream in)
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
     * @return the ParkLocation object that has been filled in by readFeed1(parser).
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static ParkLocation readFeed(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        ParkLocation entry = new ParkLocation();

        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // looks for the 'row' tag
            if (name.equals("row")) {
                entry = readFeed1(parser);
            } else {
                skip(parser);
            }
        }

        return entry;
    }

    /**
     * looks for 'row' tag in XML file.
     * If success, calls readParkLocation to fill out the ParkLocation object entry.
     * @param parser the XmlPullParser to be used
     * @return the ParkLocation object that has been filled in by readParkLocation(parser).
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static ParkLocation readFeed1(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        ParkLocation entry = new ParkLocation();

        parser.require(XmlPullParser.START_TAG, ns, "row");
        while (parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // looks for the 'row' tag
            if (name.equals("row")) {
                entry = readParkLocation(parser);
            } else {
                skip(parser);
            }
        }

        return entry;
    }

    /**
     * looks for 'post_id', 'cap_color', 'street_num' and 'streetname' tags
     * and then sets those fields in a new ParkLocation object.
     * @param parser the XmlPullParser to be used
     * @return A ParkLocation object with fields filled in by data from the xml file.
     * @throws XmlPullParserException if the parser fails.
     * @throws IOException if it can't read the file.
     */
    private static ParkLocation readParkLocation(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "row");
        String post_id = "";
        String cap_color = "";
        String street_num = "";
        String streetname = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case "post_id":
                    post_id = readPostID(parser);
                    break;
                case "cap_color":
                    cap_color = readCapColor(parser);
                    break;
                case "street_num":
                    street_num = readStNum(parser);
                    break;
                case "streetname":
                    streetname = readStName(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new ParkLocation(streetname, street_num, cap_color, post_id);
    }

    /**
     * process post_id tags in the feed.
     * @param parser the XmlPullParser to be used
     * @return the String tagged by post_id.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readPostID(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "post_id");
        String type = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "post_id");
        return type;
    }

    /**
     * process cap_color tags in the feed
     * @param parser the XmlPullParser to be used
     * @return the String tagged by cap_color.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readCapColor(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "cap_color");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "cap_color");
        return name;
    }

    /**
     * process street_num tags in the feed
     * @param parser the XmlPullParser to be used
     * @return the String tagged by street_num
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readStNum(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "street_num");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "street_num");
        return name;
    }

    /**
     * process streetname tags in the feed
     * @param parser the XmlPullParser to be used
     * @return the String tagged by streetname
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String readStName(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "streetname");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "streetname");
        return name;
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