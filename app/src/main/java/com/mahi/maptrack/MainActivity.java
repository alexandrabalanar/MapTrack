package com.mahi.maptrack;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, OnLocationUpdatedListener, OnActivityUpdatedListener, OnGeofencingTransitionListener {

    /**
     * Overlay that shows a short help text when first launched. It also provides an option to
     * exit the app.
     */
    // private DismissOverlayView mDismissOverlay;

    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);
    private static final int LOCATION_PERMISSION_ID = 1001;
    // GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";
    int speed;
    //added
    Polyline line; //added
    Date date1, date2;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    long mydistance;
    /**
     * The map. It is initialized when the map has been fully loaded and is ready to be used.
     *
     * @see #onMapReady(GoogleMap)
     */
    private GoogleMap mMap;
    private LocationManager locationManager;
    private com.mahi.maptrack.LocationManager locationManagerlocal;
    private Location mLocation;
    private Button startbutton;
    private ArrayList<LatLng> points;
    private boolean status = false;
    //private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private LocationGooglePlayServicesProvider provider;
    //added
    private double old_latitude, old_longitude, new_latitude, new_longitude;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setupMap();


        // Location permission not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }

        startLocation();


        points = new ArrayList<LatLng>();
        startbutton = (Button) findViewById(R.id.trackown);

        startbutton.bringToFront();

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status) {
                    date2 = new Date();
                    System.out.println(dateFormat.format(date2));

                    status = false;
                    startbutton.setText("start");
                    calculateData();

                } else {

                    date1 = new Date();
                    System.out.println(dateFormat.format(date1));

                    status = true;
                    startbutton.setText("stop");
                }
            }


        });

        showLast();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void calculateData() {

        // Toast.makeText(getBaseContext(),"speed"+speed,Toast.LENGTH_SHORT).show();
        int i = points.size();
        if (i > 2) {
            String distance = getDistance(points.get(0), points.get(i - 1));

            //long dis = Long.parseLong(distance);
            long TimeDiff = date2.getTime() - date1.getTime();
            long diffSeconds = TimeDiff / 1000 % 60;
            Log.e("bhoot", "sec" + diffSeconds);

            long f1 = mydistance;
            long f2 = diffSeconds;


            Log.e("bhoot", "sec" + (f1 / f2));
            // int diffTime = (int) diffSeconds;
            // int intdist = (int) mydistance;//Integer.parseInt(String.valueOf(mydistance));

            String speed;

       /* if (intdist > 1 )
        {*/
            speed = "" + (f1 / f2);

        /*}
        else {

            speed = "Not Calculate";
        }*/


            //int timm = (int) diffSeconds;

            // long Speed = dis / diffSeconds;
            Log.e("mahi", "distance:" + distance + "in m");
            Log.e("mahi", "time:" + diffSeconds + "in sec");
            Log.e("mahi", "speed:" + speed + "in m/s");

            Toast.makeText(getBaseContext(), "distance:" + distance + "/n" + "Time Duration:" + "" + diffSeconds + "sec" + "/n" + "speed" + speed + " m/s", Toast.LENGTH_SHORT).show();

        }
    }

    private void setupMap() {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    mMap = googleMap;
                    mMap.setIndoorEnabled(true);

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                    mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {

                            speed = (int) ((location.getSpeed() * 3600) / 1000);

                            Log.e("mahi ", "speed" + speed);

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(), location.getLongitude()), 15));

                            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());


                            if (status) {
                                points.add(latlng);

                                redrawLine();
                            }

                            System.out.println("mahi onLocation Changed Call");

                        }
                    });

                }
            });

            // Check if we were successful in obtaining the map.
            if (mMap != null) {

            }
        }
    }

/*
* smart location stop
*
* */

    private void stopLocation() {

        Log.e("mahi", "StopLocation Call");
        SmartLocation.with(this).location().stop();
        SmartLocation.with(this).activity().stop();
        SmartLocation.with(this).geofencing().stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocation();
        }
    }

    /*
    * smart Location Functioon
    *
    * */
    private void startLocation() {

        Log.e("mahi", "startLocation Call");

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);

        // Create some geofences
        GeofenceModel mestalla = new GeofenceModel.Builder("1").setTransition(Geofence.GEOFENCE_TRANSITION_ENTER).setLatitude(39.47453120000001).setLongitude(-0.358065799999963).setRadius(500).build();
        smartLocation.geofencing().add(mestalla).start(this);
    }


    private String getNameFromType(DetectedActivity activityType) {
        switch (activityType.getType()) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
            default:
                return "unknown";
        }
    }

    private String getTransitionNameFromType(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exit";
            default:
                return "dwell";
        }
    }

    private void showLast() {
        Log.e("mahi", "ShowLast Call");

        Location lastLocation = SmartLocation.with(this).location().getLastLocation();
        if (lastLocation != null) {

            try {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 15));
            } catch (Exception e) {
                e.printStackTrace();
            }
                /* locationText.setText(
                    String.format("[From Cache] Latitude %.6f, Longitude %.6f",
                            lastLocation.getLatitude(),
                            lastLocation.getLongitude())
            );*/
        }

        DetectedActivity detectedActivity = SmartLocation.with(this).activity().getLastActivity();
        if (detectedActivity != null) {
           /* activityText.setText(
                    String.format("[From Cache] Activity %s with %d%% confidence",
                            getNameFromType(detectedActivity),
                            detectedActivity.getConfidence())
            );*/
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        // Map is ready to be used.
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return TODO;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().isRotateGesturesEnabled();//rotateGesturesEnabled(true);
        mMap.getUiSettings().isTiltGesturesEnabled();//tiltGesturesEnabled(true);

        mMap.isBuildingsEnabled();
        mMap.isMyLocationEnabled();

        // Set the long click listener as a way to exit the map.
        mMap.setOnMapLongClickListener(this);
        mLocationRequest = LocationRequest.create();

        mLocationRequest.setInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(1000);

        initMap(mLocation);

    }

    private void initMap(Location location) {

        if (location != null && mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15));
            //   Toast.makeText(getContext(), "Found you!", Toast.LENGTH_SHORT).show();
        }
    }

    /*\
    * Location Update
    * */


    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    /*
    * Google Map Bearing for the Location
    * */
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);

        return -1;
    }


    /*
    * Google map Get Distance on The Map
    * */
    private String getDistance(LatLng my_latlong, LatLng frnd_latlong) {
        Location l1 = new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2 = new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        float distance = l1.distanceTo(l2);
        String dist = distance + " M";

        mydistance = (long) distance;

        if (distance > 1000.0f) {
            distance = distance / 1000.0f;
            dist = distance + " KM";
        }
        return dist;
    }


    @Override
    protected void onResume() {

        super.onResume();
    }


    private void redrawLine() {
        //  if(status) {
        Log.e("mahi", "redrawLine call");
        try {
            mMap.clear();  //clears all Markers and Polylines
        } catch (Exception e) {
            e.printStackTrace();
        }
        PolylineOptions options = new PolylineOptions();

        //.width(5).color(Color.BLUE).geodesic(true);
           /* for (int i = 0; i < points.size(); i++) {
                LatLng point = points.get(i);
                options.add(point);
            }*/
        options.addAll(points);
        options.width(6);
        options.color(Color.BLUE);
        // Drawing polyline in the Google Map for the i-th route
        mMap.addPolyline(options);

        // }

    }

    /*
    *
    * Google map Get Diraction on Map
    * */
    private String getDirection(LatLng my_latlong, LatLng frnd_latlong) {
        // TODO Auto-generated method stub
        double my_lat = my_latlong.latitude;
        double my_long = my_latlong.longitude;

        double frnd_lat = frnd_latlong.latitude;
        double frnd_long = frnd_latlong.longitude;

        double radians = getAtan2((frnd_long - my_long), (frnd_lat - my_lat));
        double compassReading = radians * (180 / Math.PI);

        String[] coordNames = {"North", "North-East", "East", "South-East", "South", "South-West", "West", "North-West", "North"};
        int coordIndex = (int) Math.round(compassReading / 45);

        if (coordIndex < 0) {
            coordIndex = coordIndex + 8;
        }
        ;

        return coordNames[coordIndex]; // returns the coordinate value
    }

    private double getAtan2(double longi, double lat) {
        return Math.atan2(longi, lat);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /*ShowLocation
    * Function
    * */

    private void showLocation(Location location) {
        if (location != null) {


            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
            //locationText.setText(text);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15));
            // We are going to get the address for the current position
            SmartLocation.with(this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                @Override
                public void onAddressResolved(Location original, List<Address> results) {
                    if (results.size() > 0) {
                        Address result = results.get(0);
                        StringBuilder builder = new StringBuilder(text);
                        builder.append("\n[Reverse Geocoding] ");
                        List<String> addressElements = new ArrayList<>();
                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
                            addressElements.add(result.getAddressLine(i));
                        }
                        builder.append(TextUtils.join(", ", addressElements));

                        Toast.makeText(getBaseContext(), "" + builder.toString(), Toast.LENGTH_SHORT).show();
                        //locationText.setText(builder.toString());
                    }
                }
            });
        } else {
            Toast.makeText(getBaseContext(), "Null", Toast.LENGTH_SHORT).show();

        }
    }

    private void showActivity(DetectedActivity detectedActivity) {
        if (detectedActivity != null) {

        } else {
            //activityText.setText("Null activity");
        }
    }

    private void showGeofence(Geofence geofence, int transitionType) {
        if (geofence != null) {
            //geofenceText.setText("Transition " + getTransitionNameFromType(transitionType) + " for Geofence with id = " + geofence.getRequestId());
        } else {
            // geofenceText.setText("Null geofence");
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        showLocation(location);

    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        showActivity(detectedActivity);
    }

    @Override
    public void onGeofenceTransition(TransitionGeofence geofence) {
        showGeofence(geofence.getGeofenceModel().toGeofence(), geofence.getTransitionType());
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.mahi.maptrack/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.mahi.maptrack/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public ArrayList<LatLng> getDirection(Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                for (int j = 0; j < arr.size(); j++) {
                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
                }

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
            }
        }
        return listGeopoints;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    /*\
    *
     * Google Map Request_Update Class
    *
    * */


  /*  public class Request_Update extends AsyncTask<Location, Void, Location> {

        Document doc;
        @Override
        protected void onPreExecute()
        {
            //Toast.makeText(getApplicationContext(), "onPreExecute()!", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Location doInBackground(Location... location) {
            // TODO Auto-generated method stub

            String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                    + "origin=" + location[0].getLatitude() + "," + location[0].getLongitude()
                    + "&destination=" + frnd_lat + "," + frnd_longi
                    + "&sensor=false&units=metric&mode="+direction; //direction="walking" or "driving"


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                InputStream in = response.getEntity().getContent();
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                doc = builder.parse(in);
            } catch (Exception e) {
            }

            return location[0];
        }

        @Override
        protected void onPostExecute(Location location)
        {
            if(doc!=null)
            {
                ArrayList<LatLng> directionPoint;
                directionPoint=getDirection(doc);

                int ii = 0;
                size_of_latlong=directionPoint.size();
                for( ; ii <size_of_latlong ; ii++) {
                    if(ii==0)
                    {
                        PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.RED);
                        rectLine.add(my_latlong,directionPoint.get(ii));
                        Polyline polyline=map.addPolyline(rectLine);
                        polylines.add(polyline);
                    }
                    else
                    {
                        PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.RED);
                        rectLine.add(directionPoint.get(ii-1),directionPoint.get(ii));
                        Polyline polyline=map.addPolyline(rectLine);
                        polylines.add(polyline);
                    }
                }
                PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.RED);
                rectLine.add(frnd_latlong,directionPoint.get(ii-1));
                Polyline polyline=map.addPolyline(rectLine);
                polylines.add(polyline);
                //map.addPolyline(rectLine);
            }
        }
    }*/

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(6);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}
