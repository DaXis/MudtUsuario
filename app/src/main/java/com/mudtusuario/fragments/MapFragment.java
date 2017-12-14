package com.mudtusuario.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.custom.CustomEditText;
import com.mudtusuario.interfaces.DrawableClickListener;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.objs.MudUsObj;
import com.mudtusuario.objs.UserObj;
import com.mudtusuario.utils.ConnectToServer;
import com.mudtusuario.utils.DirectionsJSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private int lay;
    private GoogleMap googleMap;
    //private TextView init_point_txt, end_point_txt;
    private CustomEditText init_point_txt, end_point_txt;
    private LinearLayout end_point, info_map;
    private Button req_btn;
    private boolean pointA, pointB;
    private LatLng pointIni, pointEnd;
    private String ROUTE_MODE = "mode=driving", addres;
    private MudUsObj mudObj;
    private Marker current;
    private View rootView;
    private boolean point, from;

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
    public void onResume() {
        super.onResume();
        Singleton.setCurrentFragment(this);

        Singleton.getActionButon().setVisibility(View.INVISIBLE);
        Singleton.getActionText().setVisibility(View.GONE);
        Singleton.getToolLogo().setVisibility(View.VISIBLE);
        Singleton.getMenuBtn().setImageResource(R.drawable.ic_menu);
        Singleton.getMenuBtn().setVisibility(View.VISIBLE);
        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.map_frag, container, false);
            Singleton.setMapView(rootView);
        } else if(Singleton.getMapView() != null){
            rootView = Singleton.getMapView();
        }

        mudObj = new MudUsObj();

        /*init_point_txt = (TextView) rootView.findViewById(R.id.init_point_txt);
        end_point_txt = (TextView) rootView.findViewById(R.id.end_point_txt);*/

        init_point_txt = (CustomEditText) rootView.findViewById(R.id.init_point_txt);
        init_point_txt.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                from = true;
                point = true;
                addres = init_point_txt.getText().toString();
                pointA = false;
                pointB = false;
                Singleton.hideKeyboard(MapFragment.this.getView());
                initGetLatLon();
            }
        });
        end_point_txt = (CustomEditText) rootView.findViewById(R.id.end_point_txt);
        end_point_txt.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                from = true;
                point = false;
                addres = end_point_txt.getText().toString();
                pointA = true;
                pointB = false;
                Singleton.hideKeyboard(MapFragment.this.getView());
                initGetLatLon();
            }
        });

        end_point = (LinearLayout) rootView.findViewById(R.id.end_point);
        req_btn = (Button) rootView.findViewById(R.id.req_btn);
        req_btn.setOnClickListener(this);
        info_map = (LinearLayout) rootView.findViewById(R.id.info_map);

        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(map != null)
            map.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.req_btn:
                initMud();
                break;
        }
    }

    private void initMud() {
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
        Log.d("googleMap", "onMapReady");
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable(){
                    public void run(){
                        LatLng latLng = new LatLng(Singleton.getLatitud(), Singleton.getLongitude());
                        MapFragment.this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    };
                }, 3500);
            }
        });

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
                current = marker;
                initDirConnection(marker.getPosition());
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(end_point.getVisibility() == View.GONE) {
                    if (!pointA) {
                        setInitPoint(latLng, false);
                        pointA = true;
                    }
                } else {
                    if (!pointB) {
                        setEndPoint(latLng, false);
                        pointB = true;
                    }
                }
            }
        });
    }

    private void setInitPoint(LatLng latLng, boolean flag){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Usar esta como ubicación inicial");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_inicio));
        googleMap.addMarker(markerOptions);


        if(flag) {
            LatLngBounds latLngBounds = new LatLngBounds(latLng,latLng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));
            current = googleMap.addMarker(markerOptions);
        } else
            googleMap.addMarker(markerOptions);
    }

    private void setEndPoint(LatLng latLng, boolean flag){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Punto de descarga");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_final));
        googleMap.addMarker(markerOptions);

        if(flag) {
            LatLngBounds latLngBounds = new LatLngBounds(latLng,latLng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));
            current = googleMap.addMarker(markerOptions);
        } else
            googleMap.addMarker(markerOptions);
    }

    private void initGetLatLon(){
        Singleton.showLoadDialog(getFragmentManager());
        addres = addres.replace(" ", "+");
        Singleton.showLoadDialog(getFragmentManager());
        Object[] objs = new Object[]{addres, 20, this};
        ConnectToServer connectToServer = new ConnectToServer(objs, 0.0);
    }

    public void getLatLonResponse(String arg){
        try {
            JSONObject jsonObject = new JSONObject(arg);
            JSONArray results = jsonObject.getJSONArray("results");
            JSONObject dir = results.getJSONObject(0);
            JSONObject geometry = dir.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            String aux = dir.getString("formatted_address");
            LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
            if(point && !pointA){
                //init_point_txt.setText(dir.getString("formatted_address"));
                setInitPoint(latLng, true);

                if(aux.contains("Error")) {
                    init_point_txt.setTextColor(getResources().getColor(R.color.divider_color));
                    init_point_txt.setText(aux);
                    end_point.setVisibility(View.GONE);
                } else {
                    init_point_txt.setTextColor(getResources().getColor(R.color.primary_text));
                    init_point_txt.setText(aux);
                    end_point.setVisibility(View.VISIBLE);
                    pointIni = current.getPosition();
                    mudObj.carga_dir = aux;
                    mudObj.carga_lat = current.getPosition().latitude;
                    mudObj.carga_lon = current.getPosition().longitude;
                }

                pointA = true;
            } else {
                //end_point_txt.setText(dir.getString("formatted_address"));
                setEndPoint(latLng, true);

                if(aux.contains("Error")) {
                    end_point_txt.setTextColor(getResources().getColor(R.color.divider_color));
                    end_point_txt.setText(aux);
                    req_btn.setVisibility(View.GONE);
                } else {
                    end_point_txt.setTextColor(getResources().getColor(R.color.primary_text));
                    end_point_txt.setText(aux);
                    req_btn.setVisibility(View.VISIBLE);
                    pointEnd = current.getPosition();
                    ArrayList<LatLng> aux0 = new ArrayList<LatLng>();
                    aux0.add(pointEnd);
                    drawLocationsRoutes(pointIni, aux0);
                    info_map.setVisibility(View.VISIBLE);
                    mudObj.des_dir = aux;
                    mudObj.des_lat = current.getPosition().latitude;
                    mudObj.des_lon = current.getPosition().longitude;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Singleton.dissmissLoad();
    }

    private void initDirConnection(LatLng latLng){
        Singleton.showLoadDialog(getFragmentManager());
        Object[] objs = new Object[]{latLng.latitude+","+latLng.longitude, 16, this};
        ConnectToServer connectToServer = new ConnectToServer(objs, "");
    }

    public void getDirFromLtLn(String result){
        String aux = "";
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray results = jsonObject.getJSONArray("results");
            JSONObject dir = results.getJSONObject(0);
            aux = dir.getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
            aux = "Error";
        }
        Singleton.dissmissLoad();

        if(current.getTitle().contains("Usar esta como ubicación inicial")){
            //String aux = getDirFromLtLn(marker.getPosition());
            if(aux.contains("Error")) {
                init_point_txt.setTextColor(getResources().getColor(R.color.divider_color));
                init_point_txt.setText(aux);
                end_point.setVisibility(View.GONE);
            } else {
                init_point_txt.setTextColor(getResources().getColor(R.color.primary_text));
                init_point_txt.setText(aux);
                end_point.setVisibility(View.VISIBLE);
                pointIni = current.getPosition();
                mudObj.carga_dir = aux;
                mudObj.carga_lat = current.getPosition().latitude;
                mudObj.carga_lon = current.getPosition().longitude;
            }
        } else {
            //String aux = getDirFromLtLn(marker.getPosition());
            if(aux.contains("Error")) {
                end_point_txt.setTextColor(getResources().getColor(R.color.divider_color));
                end_point_txt.setText(aux);
                req_btn.setVisibility(View.GONE);
            } else {
                end_point_txt.setTextColor(getResources().getColor(R.color.primary_text));
                end_point_txt.setText(aux);
                req_btn.setVisibility(View.VISIBLE);
                pointEnd = current.getPosition();
                ArrayList<LatLng> aux0 = new ArrayList<LatLng>();
                aux0.add(pointEnd);
                drawLocationsRoutes(pointIni, aux0);
                info_map.setVisibility(View.VISIBLE);
                mudObj.des_dir = aux;
                mudObj.des_lat = current.getPosition().latitude;
                mudObj.des_lon = current.getPosition().longitude;
            }
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
