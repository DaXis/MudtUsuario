package com.mudtusuario.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.dialogs.CustomDialog;
import com.mudtusuario.objs.DetailObj;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class ViajeDetailFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private MudObj mudObj;
    private ImageView user_pic;
    private TextView user_name, tel, date, hour, loc, unid_a, unid_b, caps, piso, have, desc, date_b, hour_b, loc_b,
            dist, piso_b, have_b, precio, precioHint;
    private Button initPros;
    private DetailObj detailObj;
    private String title;
    private LinearLayout telLay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
        mudObj = (MudObj)bundle.getSerializable("mudObj");
        title = bundle.getString("title");
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
        Singleton.getActionText().setText(title);
        Singleton.getMenuBtn().setImageResource(R.drawable.ic_back);
        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_frag, container, false);

        user_pic = (ImageView)rootView.findViewById(R.id.user_pic);

        user_name = (TextView)rootView.findViewById(R.id.user_name);
        tel = (TextView)rootView.findViewById(R.id.tel);
        date = (TextView)rootView.findViewById(R.id.date);
        hour = (TextView)rootView.findViewById(R.id.hour);
        loc = (TextView)rootView.findViewById(R.id.loc);
        unid_a = (TextView)rootView.findViewById(R.id.unid_a);
        unid_b = (TextView)rootView.findViewById(R.id.unid_b);
        caps = (TextView)rootView.findViewById(R.id.caps);
        piso = (TextView)rootView.findViewById(R.id.piso);
        have = (TextView)rootView.findViewById(R.id.have);
        desc = (TextView)rootView.findViewById(R.id.desc);
        date_b = (TextView)rootView.findViewById(R.id.date_b);
        hour_b = (TextView)rootView.findViewById(R.id.hour_b);
        loc_b = (TextView)rootView.findViewById(R.id.loc_b);
        dist = (TextView)rootView.findViewById(R.id.dist);
        piso_b = (TextView)rootView.findViewById(R.id.piso_b);
        have_b = (TextView)rootView.findViewById(R.id.have_b);
        precio = (TextView)rootView.findViewById(R.id.precio);
        precioHint = (TextView)rootView.findViewById(R.id.precioHint);

        initPros = (Button)rootView.findViewById(R.id.initPros);
        initPros.setOnClickListener(this);
        if(title.contains("Solicitud")) {
            initPros.setText(getResources().getString(R.string.pagar));
            precioHint.setText(Singleton.getMainActivity().getResources().getText(R.string.aprox));
        } else
            precioHint.setText(Singleton.getMainActivity().getResources().getText(R.string.aprox));

        telLay = (LinearLayout)rootView.findViewById(R.id.tel_lay);
        telLay.setOnClickListener(this);

        initConnection();

        return rootView;
    }

    private void initConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MudanzaFolioServicio", mudObj.MudanzaFolioServicio);
            Object[] objs = new Object[]{"GetMudanzaDetalleCliente", 3, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject DetalleMudanza = jsonObject.getJSONObject("DetalleMudanza");
            detailObj = new DetailObj();
            if(!DetalleMudanza.isNull("ClienteFoto"))
                detailObj.ClienteFoto = DetalleMudanza.getString("ClienteFoto");
            if(!DetalleMudanza.isNull("ClienteId"))
                detailObj.ClienteId = DetalleMudanza.getString("ClienteId");
            if(!DetalleMudanza.isNull("ClienteNombre"))
                detailObj.ClienteNombre = DetalleMudanza.getString("ClienteNombre");
            if(!DetalleMudanza.isNull("ClienteTelefono"))
                detailObj.ClienteTelefono = DetalleMudanza.getString("ClienteTelefono");

            detailObj.MudanzaCosto = DetalleMudanza.getString("MudanzaCosto");
            detailObj.MudanzaDescripcionMobiliario = DetalleMudanza.getString("MudanzaDescripcionMobiliario");
            detailObj.MudanzaDireccionDescarga = DetalleMudanza.getString("MudanzaDireccionDescarga");
            detailObj.MudanzaElevadorCargaCargar = DetalleMudanza.getInt("MudanzaElevadorCargaCargar");
            detailObj.MudanzaEstatusServicio = DetalleMudanza.getString("MudanzaEstatusServicio");
            detailObj.MudanzaFechaSolicitud = DetalleMudanza.getString("MudanzaFechaSolicitud");
            detailObj.MudanzaFolioServicio = DetalleMudanza.getString("MudanzaFolioServicio");
            detailObj.MudanzaHoraSolicitud = DetalleMudanza.getString("MudanzaHoraSolicitud");
            detailObj.MudanzaPisoDescarga = DetalleMudanza.getString("MudanzaPisoDescarga");
            detailObj.TipoUnidadDescrip = DetalleMudanza.getString("TipoUnidadDescrip");
            detailObj.UnidadPlacas = DetalleMudanza.getString("UnidadPlacas");
            detailObj.MudanzaDireccionCarga = DetalleMudanza.getString("MudanzaDireccionCarga");
            detailObj.MudanzaPisoCarga = DetalleMudanza.getString("MudanzaPisoCarga");
            detailObj.MudanzaFechaTentativaDescarga = DetalleMudanza.getString("MudanzaFechaTentativaDescarga");
            detailObj.MudanzaHoraTentativaDescarga = DetalleMudanza.getString("MudanzaHoraTentativaDescarga");
            detailObj.MudanzaDistanciaAproximada = DetalleMudanza.getString("MudanzaDistanciaAproximada");
            detailObj.MudanzaDirCarLatLong = DetalleMudanza.getString("MudanzaDirCarLatLong");
            detailObj.MudanzaDirDesLatLong = DetalleMudanza.getString("MudanzaDirDesLatLong");

            fillViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Singleton.dissmissLoad();
    }

    private void fillViews(){
        user_name.setText(detailObj.ClienteNombre);
        tel.setText(detailObj.ClienteTelefono);
        date.setText(detailObj.MudanzaFechaSolicitud);
        hour.setText(detailObj.MudanzaHoraSolicitud);
        loc.setText(detailObj.MudanzaDireccionCarga);
        unid_a.setText("Unidad");
        unid_b.setText(detailObj.UnidadPlacas);
        caps.setText(detailObj.TipoUnidadDescrip);
        piso.setText(detailObj.MudanzaPisoCarga);
        have.setText(getElevator(0));
        desc.setText(detailObj.MudanzaDescripcionMobiliario);
        date_b.setText(detailObj.MudanzaFechaTentativaDescarga);
        hour_b.setText(detailObj.MudanzaHoraTentativaDescarga);
        loc_b.setText(detailObj.MudanzaDireccionDescarga);
        dist.setText("Distancia "+detailObj.MudanzaDistanciaAproximada);
        piso_b.setText(detailObj.MudanzaPisoDescarga);
        have_b.setText(getElevator(detailObj.MudanzaElevadorCargaCargar));
        precio.setText(detailObj.MudanzaCosto);

        if(mudObj.MudanzaEstatusServicio == 6)
            initPros.setVisibility(View.INVISIBLE);

        ProgressBar progressBar = new ProgressBar(getContext());
        Singleton.loadImage(detailObj.ClienteFoto, user_pic, progressBar);
    }

    private String getElevator(int arg){
        if(arg == 0)
            return "No tiene";
        else
            return "Si tiene";
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.initPros:
                if(title.contains("Detalle")) {
                    if(mudObj.MudanzaEstatusServicio != 6) {
                        //if(CalculationByDistance() <= 100)
                        //initPagoConnection();
                        /*else
                            showCustomDialog("No puedes iniciar",
                                    "Debes estar por lo menos a 100 metros de distancia del punto de recogida para iniciar la mudanza",
                                    "Continuar");*/
                    } else
                        showCustomDialog("No puedes iniciar",
                                "Valida que no tengas una mudanza iniciada o que la fecha no sea próxima",
                                "Continuar");
                    /*if(Singleton.getIsActive() != null) {
                        if(Singleton.getIsActive().idMud.equals(detailObj.MudanzaFolioServicio))
                            initStatusConnection();
                        else
                            showCustomDialog("Espera un momento", "Tienes una mudanza activa y no puedes comenzar otra", "Aceptar");
                    } else
                        showCustomDialog("Espera un momento", "Tienes una mudanza activa y no puedes comenzar otra", "Aceptar");*/
                } else if(title.contains("Solicitud")){
                    initPagoConnection();
                }
                break;
            case R.id.tel_lay:
                callIntent(detailObj.ClienteTelefono);
                break;
        }
    }

    private void initPagoConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MudanzaFolioServicio", detailObj.MudanzaFolioServicio);
            jsonObject.put("ClienteId", Singleton.getUSerObj().GUID);
            jsonObject.put("FormatoPagoId", 1);
            Object[] objs = new Object[]{"PagarMudanza", 10, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPagoResponse(String result) {
        Singleton.dissmissLoad();
    }

    private void showCustomDialog(String title, String body, String action){
        CustomDialog custoDialog = CustomDialog.newInstance(title, body, action, 0);
        custoDialog.setCancelable(true);
        custoDialog.show(getFragmentManager(), "custom dialog");
    }

    private void callIntent(String tel){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + tel));
        startActivity(intent);
    }

    /*private void initAceptConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MudanzaFolioServicio", detailObj.MudanzaFolioServicio);
            jsonObject.put("OperadorGAMId", Singleton.getUSerObj().GUID);
            Object[] objs = new Object[]{"AcceptService", 12, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAceptResponse(String reponse){
        Singleton.dissmissLoad();
        getActivity().onBackPressed();
    }*/

    public double CalculationByDistance() {
        int Radius=6371;//radius of earth in Km
        /*double lat1 = StartP.getLatitudeE6()/1E6;
        double lat2 = EndP.getLatitudeE6()/1E6;
        double lon1 = StartP.getLongitudeE6()/1E6;
        double lon2 = EndP.getLongitudeE6()/1E6;*/

        LatLng latLng = getLatLon(detailObj.MudanzaDirCarLatLong);

        double lat1 = latLng.latitude;
        double lon1 = latLng.longitude;

        double lat2 = Singleton.getLatitud();
        double lon2 = Singleton.getLongitude();

        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        //double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        //int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult*100;
        Integer meterInDec = Integer.valueOf(newFormat.format(meter));
        //Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+ meterInDec);

        //return Radius * c;
        return meterInDec;
    }


    private LatLng getLatLon(String arg){
        String[] aux = arg.split("[,]");
        String aux0 =  aux[0].replace("null", "");
        String aux1 =  aux[1].replace("null", "");

        LatLng latLng = new LatLng(Double.parseDouble(aux0), Double.parseDouble(aux1));
        return latLng;
    }

}
