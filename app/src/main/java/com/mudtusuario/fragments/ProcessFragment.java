package com.mudtusuario.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.mudtusuario.dialogs.FinishMudDialog;
import com.mudtusuario.dialogs.LoadDialog;
import com.mudtusuario.dialogs.PicDialog;
import com.mudtusuario.objs.DetailObj;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;
import com.mudtusuario.utils.DirectionsJSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class ProcessFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private int lay, process = 1;
    private MudObj mudObj;
    private DetailObj detailObj;
    private ImageView user_pic, current, transparentImageView, unidad, step1, step2, step3, step4, step5;
    private TextView user_name, tel, ubic_a, ubic_b, hour_a, hour_b, hour_c, hour_d, hour_e, currentTV, unidad_txt, placa;
    private Button endMud;
    private LinearLayout init_process, second_step, trhid_step, fourth_step, fifth_step, tel_lay;
    private ScrollView mainScrollView;
    //private SupportMapFragment map;
    private GoogleMap googleMap;
    private static final String ROUTE_MODE = "mode=driving";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
        mudObj = (MudObj)bundle.getSerializable("mudObj");
        detailObj = (DetailObj)bundle.getSerializable("detailObj");
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
        Singleton.getActionText().setText("Proceso del viaje");
        Singleton.getMenuBtn().setImageResource(R.drawable.ic_back);
        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.process_frag, container, false);

        user_pic = (ImageView)rootView.findViewById(R.id.user_pic);
        ProgressBar progressBar = new ProgressBar(getContext());
        Singleton.loadImage(detailObj.ClienteFoto, user_pic, progressBar);

        unidad = (ImageView)rootView.findViewById(R.id.unidad);

        step1 = (ImageView)rootView.findViewById(R.id.step1);
        step2 = (ImageView)rootView.findViewById(R.id.step2);
        step3 = (ImageView)rootView.findViewById(R.id.step3);
        step4 = (ImageView)rootView.findViewById(R.id.step4);
        step5 = (ImageView)rootView.findViewById(R.id.step5);

        user_name = (TextView)rootView.findViewById(R.id.user_name);
        tel = (TextView)rootView.findViewById(R.id.tel);
        user_name.setText(detailObj.ClienteNombre);
        tel.setText(detailObj.ClienteTelefono);

        ubic_a = (TextView)rootView.findViewById(R.id.ubic_a);
        ubic_a.setText(detailObj.MudanzaDireccionCarga);

        ubic_b = (TextView)rootView.findViewById(R.id.ubic_b);
        ubic_b.setText(detailObj.MudanzaDireccionDescarga);

        hour_a = (TextView)rootView.findViewById(R.id.hour_a);
        hour_a.setText(detailObj.MudanzaHoraSolicitud);

        hour_b = (TextView)rootView.findViewById(R.id.hour_b);
        hour_c = (TextView)rootView.findViewById(R.id.hour_c);
        hour_d = (TextView)rootView.findViewById(R.id.hour_d);

        hour_e = (TextView)rootView.findViewById(R.id.hour_e);
        hour_e.setText(detailObj.MudanzaHoraTentativaDescarga);

        unidad_txt = (TextView)rootView.findViewById(R.id.unidad_txt);
        unidad_txt.setText(detailObj.TipoUnidadDescrip);

        placa = (TextView)rootView.findViewById(R.id.placa);
        placa.setText(detailObj.UnidadPlacas);

        endMud = (Button)rootView.findViewById(R.id.endMud);
        endMud.setOnClickListener(this);

        init_process = (LinearLayout)rootView.findViewById(R.id.init_process);
        second_step = (LinearLayout)rootView.findViewById(R.id.second_step);
        second_step.setOnClickListener(this);
        trhid_step = (LinearLayout)rootView.findViewById(R.id.trhid_step);
        trhid_step.setOnClickListener(this);
        fourth_step = (LinearLayout)rootView.findViewById(R.id.fourth_step);
        fourth_step.setOnClickListener(this);
        fifth_step = (LinearLayout)rootView.findViewById(R.id.fifth_step);
        tel_lay = (LinearLayout)rootView.findViewById(R.id.tel_lay);
        tel_lay.setOnClickListener(this);

        solbedIssue(rootView);

        SupportMapFragment map = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.endMud:

                break;
            case R.id.second_step:

                break;
            case R.id.trhid_step:

                break;
            case R.id.fourth_step:

                break;
            case R.id.tel_lay:
                //callIntent(detailObj.ClienteTelefono);
                break;
        }
    }

    private void showEndDialog(){
        FinishMudDialog finishMudDialog = FinishMudDialog.newInstance(mudObj, detailObj);
        finishMudDialog.setCancelable(true);
        finishMudDialog.show(getFragmentManager(), "finish dialog");
    }

    private void initConnection(int id){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MudanzaFolioServicio", detailObj.MudanzaFolioServicio);
            jsonObject.put("MudanzaEstatusServicio", id);

            //JSONObject jsonObject1 = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject0 = new JSONObject();
            jsonObject0.put("LocalizacionLatitud", Singleton.getLatitud());
            jsonObject0.put("LocalizacionLongitud", Singleton.getLongitude());
            jsonArray.put(jsonObject0);
            //jsonObject1.put("cGeolocations", jsonArray);

            jsonObject.put("cGeolocations", jsonArray);
            Object[] objs = new Object[]{"SetGeolocation", 6, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getResponse(String result) {
        onUIThread(currentTV, System.currentTimeMillis());
        Singleton.dissmissLoad();
        process++;
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

    private void setPoints(){
        Marker marker1, marker2;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(getLatLon(detailObj.MudanzaDirCarLatLong));
        markerOptions.title("Punto de carga");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_inicio));
        marker1 = googleMap.addMarker(markerOptions);

        MarkerOptions markerOptions0 = new MarkerOptions();
        markerOptions0.position(getLatLon(detailObj.MudanzaDirDesLatLong));
        markerOptions0.title("Punto de descarga");
        markerOptions0.draggable(true);
        markerOptions0.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_final));
        marker2 = googleMap.addMarker(markerOptions0);

        LatLng pointIni = marker1.getPosition(), pointEnd = marker2.getPosition();
        ArrayList<LatLng> aux0 = new ArrayList<LatLng>();
        aux0.add(pointEnd);
        drawLocationsRoutes(pointIni, aux0);
    }

    private LatLng getLatLon(String arg){
        String[] aux = arg.split("[,]");
        String aux0 =  aux[0].replace("null", "");
        String aux1 =  aux[1].replace("null", "");

        LatLng latLng = new LatLng(Double.parseDouble(aux0), Double.parseDouble(aux1));
        return latLng;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("googleMap","onMapReady");
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        setPoints();
        LatLng latLng = new LatLng(Singleton.getLatitud(), Singleton.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
    }

    private void onUIThread(final TextView tv, final long time){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(getDateString(time));
            }
        });
    }

    public static String getDateString(long time){
        String date = "";
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy hh:mm aa");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        sdf.setTimeZone(Singleton.getCurrentTZ());
        date = sdf.format(new Date(time));
        //date = new SimpleDateFormat("dd 'de' MMMM',' hh:mm a").format(new Date(time));
        return date;
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
            ProcessFragment.DownloadTask downloadTask = new ProcessFragment.DownloadTask();
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

            ProcessFragment.ParserTask parserTask = new ProcessFragment.ParserTask();

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
