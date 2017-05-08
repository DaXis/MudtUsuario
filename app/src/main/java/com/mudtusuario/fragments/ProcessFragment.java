package com.mudtusuario.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.dialogs.FinishMudDialog;
import com.mudtusuario.dialogs.LoadDialog;
import com.mudtusuario.dialogs.PicDialog;
import com.mudtusuario.objs.DetailObj;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ProcessFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private int lay, process = 1;
    private MudObj mudObj;
    private DetailObj detailObj;
    private ImageView user_pic, current, pic1, pic2, transparentImageView;
    private TextView user_name, tel, ubic_a, ubic_b, hour_a, hour_b, hour_c, hour_d, hour_e, currentTV;
    private Button endMud;
    private LinearLayout init_process, second_step, trhid_step, fourth_step, fifth_step, tel_lay;
    private static final int ACTION_TAKE_PHOTO = 1, ACTION_GET_CONTENT = 2;
    private String img_path;
    private File file;
    private boolean flag;
    private ScrollView mainScrollView;
    private SupportMapFragment map;
    private GoogleMap googleMap;

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
        pic1 = (ImageView)rootView.findViewById(R.id.pic1);
        pic1.setOnClickListener(this);
        pic2 = (ImageView)rootView.findViewById(R.id.pic2);
        pic2.setOnClickListener(this);

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
            case R.id.pic1:
                break;
            case R.id.pic2:
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
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(getLatLon(detailObj.MudanzaDirCarLatLong));
        markerOptions.title("Punto de carga");
        googleMap.addMarker(markerOptions);

        MarkerOptions markerOptions0 = new MarkerOptions();
        markerOptions0.position(getLatLon(detailObj.MudanzaDirDesLatLong));
        markerOptions0.title("Punto de descarga");
        googleMap.addMarker(markerOptions0);
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

}
