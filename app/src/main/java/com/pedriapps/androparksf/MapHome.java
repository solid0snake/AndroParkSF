package com.pedriapps.androparksf;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MapHome
        extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = MapHome.class.getSimpleName();
    private Location lastLocation;
    private LatLng latLng;
    private GoogleMap theMap;

    private ParkLocation parkloc;
    private ArrayList<RateInfo> tempRates;
    private ArrayList<OperatingInfo> tempSchedule;
    private String RADIUS = "10"; // search radius in meters

    private AutoCompleteTextView mAutocompleteView;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SAN_FRANCISCO = new LatLngBounds(
            new LatLng(37.704887, -122.520512), new LatLng(37.834340, -122.350911));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_home);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.address_search);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);


        /* TODO
        creates NETWORK ERROR if using filters :/ , so just use null until Google fixes it.
        Ideally I want just to get addresses.
        */
        /*
        Collection<Integer> filterTypes = new ArrayList<Integer>();
        filterTypes.add(Place.TYPE_STREET_ADDRESS);
        AutocompleteFilter autocompleteFilter = AutocompleteFilter.create(filterTypes);
        */

        mAdapter = new PlaceAutocompleteAdapter(this,
                mGoogleApiClient, BOUNDS_GREATER_SAN_FRANCISCO, null);
        mAutocompleteView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        map.setMyLocationEnabled(true);
        map.setIndoorEnabled(false);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);

        // so the compass and myLocation buttons do not get blocked by address search
        map.setPadding(0, 200, 0, 0);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {

                View v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                TextView address = (TextView) v.findViewById(R.id.address_infoWindow);
                TextView capColor = (TextView) v.findViewById(R.id.cap_color_infoWindow);
                int windowBg;
                int txtColor = Color.WHITE;
                TextView sched = (TextView) findViewById(R.id.schedules);
                TextView ratesTextView = (TextView) findViewById(R.id.rates);
                SlidingUpPanelLayout panel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                LinearLayout ratesSchedule = (LinearLayout) findViewById(R.id.rates_schedules);

                // GET INFO and DISPLAY it
                try {

                    String requestUrl = makeParkMetersQuery(Double.toString(latLng.latitude),
                            Double.toString(latLng.longitude), RADIUS);
                    parkloc = new MetersHttpRequest(getApplicationContext()).execute(requestUrl).get();

                    Log.i(TAG, parkloc.getStName());
                    Log.i(TAG, parkloc.getStNum());
                    Log.i(TAG, parkloc.getCapColor());
                    Log.i(TAG, parkloc.getPostID());

                    address.setText(parkloc.getStNum() + " " + parkloc.getStName());
                    capColor.setText(parkloc.getCapColor() + " meter");

                    switch (parkloc.getCapColor()) {
                        case "Black":
                            windowBg = Color.BLACK;
                            break;
                        case "Brown":
                            windowBg = Color.rgb(139,69,19);
                            break;
                        case "Green":
                            windowBg = Color.GREEN;
                            txtColor = Color.BLACK;
                            break;
                        case "Purple":
                            windowBg = Color.rgb(128,0,128);
                            break;
                        case "Red":
                            windowBg = Color.RED;
                            txtColor = Color.BLACK;
                            break;
                        case "Yellow":
                            windowBg = Color.YELLOW;
                            txtColor = Color.BLACK;
                            break;
                        default:
                            windowBg = Color.GRAY;
                            break;
                    }

                    ratesSchedule.setBackgroundColor(windowBg);
                    address.setBackgroundColor(windowBg);
                    address.setTextColor(txtColor);
                    capColor.setBackgroundColor(windowBg);
                    capColor.setTextColor(txtColor);
                    sched.setTextColor(txtColor);
                    ratesTextView.setTextColor(txtColor);

                    requestUrl = makeParkScheduleQuery(parkloc.getPostID());
                    tempSchedule = new ScheduleHttpRequest(getApplicationContext()).execute(requestUrl).get();
                    parkloc.setSchedule(tempSchedule);

                    requestUrl = makeParkRatesQuery(parkloc.getPostID());
                    tempRates = new RatesHttpRequest(getApplicationContext()).execute(requestUrl).get();
                    parkloc.setRates(tempRates);

                    sched.setText(parkloc.getSchedule());
                    ratesTextView.setText(parkloc.getRates());

                } catch (Exception e) {}
                //***********************

                if (address.getText().toString().equals("No Data")) {
                    panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }

                return v;
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng newlatLng) {
                latLng = newlatLng;
                CameraPosition cameraPosition =
                        new CameraPosition.Builder().target(latLng).zoom(17).build();
                theMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if (lastLocation != null) {
                    theMap.clear();
                    Marker marker = theMap.addMarker(new MarkerOptions()
                            .anchor(0.5f, 0.0f)
                            .visible(true)
                            .position(latLng)
                            .draggable(false));
                    marker.showInfoWindow();
                    //make marker transparent
                    marker.setAlpha(0.0f);
                }

                mAutocompleteView.setText(null);
            }
        });

        /* If using marker in the middle and dragging map!!!
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                latLng = theMap.getCameraPosition().target;
                String text = latLng.toString();
                Log.i(TAG, text);
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                if (lastLocation != null) {
                    Calendar c = Calendar.getInstance();
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int year = c.get(Calendar.YEAR);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minutes = c.get(Calendar.MINUTE);
                    theMap.clear();
                    Marker marker = theMap.addMarker(new MarkerOptions()
                            .anchor(0.5f, 0.0f)
                            .visible(true)
                            .position(latLng)
                            .draggable(false)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .title("Parked here\n" +
                                    "on " + month + "/" + day + "/" + year + "\n" +
                                    "at " + hour + ":" + minutes));
                    marker.showInfoWindow();
                    marker.setAlpha(0.0f);
                }
            }
        });
        */
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        lastLocation =
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        theMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map))
                .getMap();
        theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        //if you want to animate the map traveling to your location when the app starts:
        //CameraPosition cameraPosition =
        //      new CameraPosition.Builder().target(latLng).zoom(15).build();
        //theMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //get LatLng on myLocation once app starts:
        //latLng = theMap.getCameraPosition().target;
        //toast LatLng
        //String text = latLng.toString();
        //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Location services failed. Please connect.");
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi
     *      #getPlaceById(com.google.android.gms.common.api.GoogleApiClient, String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);
            mAutocompleteView.setText(primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            /*
            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            */
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            latLng = place.getLatLng();
            CameraPosition cameraPosition =
                    new CameraPosition.Builder().target(latLng).zoom(17).build();
            theMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    /**
     * @return a string from URLMaker using the given parameters
     * @param latitude of the location.
     * @param longitude of the location.
     * @param radius of the search.
     */
    private String makeParkMetersQuery (String latitude, String longitude, String radius) {
        URLMaker temp = URLMaker.getInstance();
        return temp.makeParkingMetersURL(latitude, longitude, radius);
    }

    /**
     * @return a string from URLMaker using the given parameter
     * @param postID of the search.
     */
    private String makeParkScheduleQuery (String postID) {
        URLMaker temp = URLMaker.getInstance();
        return temp.makeOperScheduleURL(postID);
    }

    /**
     * @return a string from URLMaker using the given parameter
     * @param postID of the search.
     */
    private String makeParkRatesQuery (String postID) {
        URLMaker temp = URLMaker.getInstance();
        return temp.makeRateScheduleURL(postID);
    }
}
