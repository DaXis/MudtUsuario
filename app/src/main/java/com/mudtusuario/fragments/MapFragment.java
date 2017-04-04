package com.mudtusuario.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.objs.MudUsObj;
import com.mudtusuario.objs.UserObj;
import com.mudtusuario.utils.ConnectToServer;
import com.mudtusuario.utils.DirectionsJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private int lay;
    private GoogleMap googleMap;
    private TextView init_point_txt, end_point_txt;
    private LinearLayout end_point, info_map;
    private Button req_btn;
    private boolean pointA, pointB;
    private LatLng pointIni, pointEnd;
    private String ROUTE_MODE= "mode=driving";
    private MudUsObj mudObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume(){
        super.onResume();
        Singleton.setCurrentFragment(this);

        Singleton.getActionButon().setVisibility(View.INVISIBLE);
        Singleton.getActionText().setVisibility(View.GONE);
        Singleton.getToolLogo().setVisibility(View.VISIBLE);
        Singleton.getMenuBtn().setImageResource(R.drawable.ic_menu);
        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_frag, container, false);

        mudObj = new MudUsObj();
        init_point_txt = (TextView)rootView.findViewById(R.id.init_point_txt);
        end_point_txt = (TextView)rootView.findViewById(R.id.end_point_txt);
        end_point = (LinearLayout)rootView.findViewById(R.id.end_point);
        req_btn = (Button)rootView.findViewById(R.id.req_btn);
        req_btn.setOnClickListener(this);
        info_map = (LinearLayout)rootView.findViewById(R.id.info_map);

        SupportMapFragment map = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.req_btn:
                initMud();
                break;
        }
    }

    private void initMud(){
        Bundle bundle = new Bundle();
        bundle.putInt("lay", lay);
        bundle.putSerializable("mudObj", mudObj);
        InitMudFragment initMudFragment = new InitMudFragment();
        initMudFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(lay, initMudFragment)
                .addToBackStack(this.getClass().getName())
                .commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("googleMap","onMapReady");
        this.googleMap = googleMap;
        LatLng latLng = new LatLng(Singleton.getLatitud(), Singleton.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        //setInitPoint(latLng);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(marker.getTitle().contains("Usar esta como ubicación inicial"))
                    pointIni = marker.getPosition();
                else
                    pointEnd = marker.getPosition();
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTitle().contains("Usar esta como ubicación inicial")){
                    String aux = getDirFromLtLn(marker.getPosition());
                    if(aux.contains("Error")) {
                        init_point_txt.setTextColor(getResources().getColor(R.color.divider_color));
                        init_point_txt.setText(aux);
                        end_point.setVisibility(View.GONE);
                    } else {
                        init_point_txt.setTextColor(getResources().getColor(R.color.primary_text));
                        init_point_txt.setText(aux);
                        end_point.setVisibility(View.VISIBLE);
                        pointIni = marker.getPosition();
                        mudObj.carga_dir = aux;
                        mudObj.carga_lat = marker.getPosition().latitude;
                        mudObj.carga_lon = marker.getPosition().longitude;
                    }
                } else {
                    String aux = getDirFromLtLn(marker.getPosition());
                    if(aux.contains("Error")) {
                        end_point_txt.setTextColor(getResources().getColor(R.color.divider_color));
                        end_point_txt.setText(aux);
                        req_btn.setVisibility(View.GONE);
                    } else {
                        end_point_txt.setTextColor(getResources().getColor(R.color.primary_text));
                        end_point_txt.setText(aux);
                        req_btn.setVisibility(View.VISIBLE);
                        pointEnd = marker.getPosition();
                        ArrayList<LatLng> aux0 = new ArrayList<LatLng>();
                        aux0.add(pointEnd);
                        drawLocationsRoutes(pointIni, aux0);
                        info_map.setVisibility(View.VISIBLE);
                        mudObj.des_dir = aux;
                        mudObj.des_lat = marker.getPosition().latitude;
                        mudObj.des_lon = marker.getPosition().longitude;
                    }
                }
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(end_point.getVisibility() == View.GONE) {
                    if (!pointA) {
                        setInitPoint(latLng);
                        pointA = true;
                    }
                } else {
                    if (!pointB) {
                        setEndPoint(latLng);
                        pointB = true;
                    }
                }
            }
        });
    }

    private void setInitPoint(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Usar esta como ubicación inicial");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_inicio));
        googleMap.addMarker(markerOptions);
    }

    private void setEndPoint(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Punto de descarga");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_final));
        googleMap.addMarker(markerOptions);
    }

    private String getDirFromLtLn(LatLng latLng){
        try {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        return address+", "+city;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    //**************************************************
    /***
     * Draw Locations routes
     * @param curLocation
     * @param locationsArray
     */
    private boolean cont;
    private void drawLocationsRoutes(LatLng curLocation, ArrayList<LatLng> locationsArray) {
        //tvLoading.setText("loading routes...");

        // Checks, whether start and end locations are captured
        cont = false;
        LatLng origin ;
        LatLng dest ;
        for (int i = 0; i < locationsArray.size(); i++) {
            if (i==0) {
                origin=curLocation;
                dest=locationsArray.get(i);
            }
            else {
                origin=locationsArray.get(i-1);
                dest=locationsArray.get(i);
            }
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);
            DownloadTask downloadTask = new DownloadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+ROUTE_MODE;

        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        return url;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
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

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";

            if(result != null){
                if(result.size()<1){
                    //if(result.isEmpty() || result != null){
                    //Toast.makeText(Memory.getAppContext(), "No Points", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Traversing through all the routes
                for(int i=0;i<result.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        if(j==0){	// Get distance from the list
                            distance = (String)point.get("distance");
                            continue;
                        }else if(j==1){ // Get duration from the list
                            duration = (String)point.get("duration");
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);

                    if (!cont){
                        lineOptions.color(getResources().getColor(R.color.color_accent));
                        cont = true;
                    }
                    else {
                        lineOptions.color(getResources().getColor(R.color.color_accent));
                    }
                    //tvLoading.setText("ok...");

                }

                // Drawing polyline in the Google Map for the i-th route
                if(googleMap != null && lineOptions != null)
                    googleMap.addPolyline(lineOptions);
            }
        }
    }
    //**************************************************

}
