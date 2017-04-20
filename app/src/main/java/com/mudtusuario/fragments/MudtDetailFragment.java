package com.mudtusuario.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
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
import com.mudtusuario.objs.MudUsObj;
import com.mudtusuario.utils.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MudtDetailFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private int lay;
    private MudUsObj mudObj;
    private TextView status_txt, unit_desc, date, hour, loc, unid_a, unid_b, caps, piso, have, desc, date_b, hour_b, loc_b,
            dist, piso_b, have_b, precio, precioHint;
    private ImageView unit_pic, transparentImageView;
    private Button initPros;
    private GoogleMap googleMap;
    private ScrollView mainScrollView;
    private String ROUTE_MODE= "mode=driving";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
        mudObj = (MudUsObj)bundle.getSerializable("mudObj");
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
        Singleton.getActionText().setText("Confirmación");
        Singleton.getActionText().setVisibility(View.VISIBLE);
        Singleton.getToolLogo().setVisibility(View.GONE);
        Singleton.getMenuBtn().setImageResource(R.drawable.ic_back);

        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mudt_detail, container, false);

        status_txt = (TextView)rootView.findViewById(R.id.status_txt);

        unit_desc = (TextView)rootView.findViewById(R.id.unit_desc);
        unit_desc.setText(mudObj.unitObj.SGTipoUnidadNomen);

        date = (TextView)rootView.findViewById(R.id.date);
        date.setText(getDateString(getDateFromString(mudObj.fecha).getTime()));

        hour = (TextView)rootView.findViewById(R.id.hour);
        hour.setText(mudObj.hora);

        loc = (TextView)rootView.findViewById(R.id.loc);
        loc.setText(mudObj.carga_dir);

        unid_a = (TextView)rootView.findViewById(R.id.unid_a);
        unid_b = (TextView)rootView.findViewById(R.id.unid_b);
        unid_b.setText(mudObj.unitObj.SGTipoUnidadNomen);

        caps = (TextView)rootView.findViewById(R.id.caps);

        piso = (TextView)rootView.findViewById(R.id.piso);
        piso.setText("Piso "+mudObj.piso_carga);

        have = (TextView)rootView.findViewById(R.id.have);
        if(mudObj.elev_carga == 1)
            have.setText("Si tiene");
        else
            have.setText("No tiene");

        desc = (TextView)rootView.findViewById(R.id.desc);
        desc.setText(mudObj.desc);

        date_b = (TextView)rootView.findViewById(R.id.date_b);
        date_b.setText(mudObj.MudanzaFechaTentativaDescarga);
        hour_b = (TextView)rootView.findViewById(R.id.hour_b);
        hour_b.setText(mudObj.MudanzaHoraTentativaDescarga);

        loc_b = (TextView)rootView.findViewById(R.id.loc_b);
        loc_b.setText(mudObj.des_dir);

        dist = (TextView)rootView.findViewById(R.id.dist);

        piso_b = (TextView)rootView.findViewById(R.id.piso_b);
        piso_b.setText("Piso "+mudObj.piso_des);
        have_b = (TextView)rootView.findViewById(R.id.have_b);
        if(mudObj.elev_des == 1)
            have_b.setText("Si tiene");
        else
            have_b.setText("No tiene");

        precio = (TextView)rootView.findViewById(R.id.precio);
        precio.setText(mudObj.MudanzaCosto);
        precioHint = (TextView)rootView.findViewById(R.id.precioHint);

        initPros = (Button)rootView.findViewById(R.id.initPros);
        initPros.setOnClickListener(this);

        solbedIssue(rootView);

        SupportMapFragment map = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.initPros:
                break;
        }
    }

    public static Date getDateFromString(String fecha){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(fecha);
            //System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDateString(long time){
        String date = "";
        //date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").format(new Date(time));
        date = new SimpleDateFormat("dd MMMM").format(new Date(time));
        return date;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //LatLng latLng = new LatLng(Singleton.getLatitud(), Singleton.getLongitude());
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        LatLng latLng = new LatLng(mudObj.carga_lat, mudObj.carga_lon);
        LatLng latLngB = new LatLng(mudObj.des_lat, mudObj.des_lon);

        ArrayList<MarkerOptions> markers = new ArrayList<>();
        markers.add(setInitPoint(latLng));
        markers.add(setEndPoint(latLngB));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);
        ArrayList<LatLng> aux0 = new ArrayList<LatLng>();
        aux0.add(latLngB);
        drawLocationsRoutes(latLng, aux0);
    }

    private MarkerOptions setInitPoint(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Tú punto de carga");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_inicio));
        googleMap.addMarker(markerOptions);
        return markerOptions;
    }

    private MarkerOptions setEndPoint(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Tú punto de descarga");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_final));
        googleMap.addMarker(markerOptions);
        return markerOptions;
    }

    private void solbedIssue(View view){
        mainScrollView = (ScrollView) view.findViewById(R.id.mainScrollView);
        transparentImageView = (ImageView) view.findViewById(R.id.transparent_image);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
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
            MudtDetailFragment.DownloadTask downloadTask = new MudtDetailFragment.DownloadTask();
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

            MudtDetailFragment.ParserTask parserTask = new MudtDetailFragment.ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
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
