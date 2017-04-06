package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.objs.MudUsObj;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MudtDetailFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private int lay;
    private MudUsObj mudObj;
    private TextView status_txt, unit_desc, date, hour, loc, unid_a, unid_b, caps, piso, have, desc, date_b, hour_b, loc_b,
            dist, piso_b, have_b, precio, precioHint;
    private ImageView unit_pic;
    private Button initPros;
    private GoogleMap googleMap;

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
        unit_desc.setText(mudObj.unitObj.unit_desc);

        date = (TextView)rootView.findViewById(R.id.date);
        date.setText(getDateString(getDateFromString(mudObj.fecha).getTime()));

        hour = (TextView)rootView.findViewById(R.id.hour);
        hour.setText(mudObj.hora);

        loc = (TextView)rootView.findViewById(R.id.loc);
        loc.setText(mudObj.carga_dir);

        unid_a = (TextView)rootView.findViewById(R.id.unid_a);
        unid_b = (TextView)rootView.findViewById(R.id.unid_b);
        unid_b.setText(mudObj.unitObj.unit_desc);

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
        hour_b = (TextView)rootView.findViewById(R.id.hour_b);

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
        precioHint = (TextView)rootView.findViewById(R.id.precioHint);

        initPros = (Button)rootView.findViewById(R.id.initPros);
        initPros.setOnClickListener(this);

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

}
