package com.mudtusuario.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mudtusuario.MainActivity;
import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.dialogs.CustomDialog;
import com.mudtusuario.objs.DetailObj;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ViajeDetailFragment extends Fragment implements View.OnClickListener {
    
    private int lay;
    private MudObj mudObj;
    private ImageView user_pic;
    private TextView user_name, tel, date, hour, loc, unid_a, unid_b, caps, piso, have, desc, date_b, hour_b, loc_b,
            dist, piso_b, have_b, precio, precioHint;
    private Button initPros;
    private DetailObj detailObj;
    private String title, root;
    private LinearLayout telLay, user_lay, desing_lay;

    //******************************
    private static final String TAG = "paymentExample";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "AY3Btp31GxUmhMyBUdhsqlfbWiaosu6f1WgWRHtCnc_H4ssMpad0IAhk-" +
            "_3iYQwig5QkUQhamD9FtFD0";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("MUDT")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    //******************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
        mudObj = (MudObj)bundle.getSerializable("mudObj");
        title = bundle.getString("title");
        root = bundle.getString("root");
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

        user_lay = (LinearLayout)rootView.findViewById(R.id.user_lay);
        desing_lay = (LinearLayout)rootView.findViewById(R.id.desing_lay);
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
            else
                detailObj.ClienteFoto = DetalleMudanza.getString("OperadorFoto");

            if(!DetalleMudanza.isNull("ClienteId"))
                detailObj.ClienteId = DetalleMudanza.getString("ClienteId");
            else
                detailObj.ClienteId = DetalleMudanza.getString("OperadorGAMId");

            if(!DetalleMudanza.isNull("ClienteNombre"))
                detailObj.ClienteNombre = DetalleMudanza.getString("ClienteNombre");
            else
                detailObj.ClienteNombre = DetalleMudanza.getString("OperadorNombreCompleto");

            if(!DetalleMudanza.isNull("ClienteTelefono"))
                detailObj.ClienteTelefono = DetalleMudanza.getString("ClienteTelefono");
            else
                detailObj.ClienteTelefono = DetalleMudanza.getString("OperadorTelefono");

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
            if(!DetalleMudanza.isNull("MudanzaEstatus"))
                detailObj.MudanzaEstatus = DetalleMudanza.getInt("MudanzaEstatus");

            fillViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Singleton.dissmissLoad();
    }

    private void fillViews(){
        if(detailObj.ClienteNombre.length() == 0 && detailObj.ClienteTelefono.length() == 0) {
            user_lay.setVisibility(View.GONE);
            desing_lay.setVisibility(View.VISIBLE);
        }

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

        Log.d("root", root);
        if(root.equals("com.mudtusuario.fragments.HistorialFragment")){
            if(detailObj.MudanzaEstatus == 5 || detailObj.MudanzaEstatus == 6)
                initPros.setText(getResources().getString(R.string.pagar));
            else if(detailObj.MudanzaEstatus == 0)
                initPros.setText(getResources().getString(R.string.pendiente));
            else if(detailObj.MudanzaEstatus == 1)
                initPros.setText(getResources().getString(R.string.pagar));
            else if(detailObj.MudanzaEstatus == 2)
                initPros.setText(getResources().getString(R.string.curso));
            else if(detailObj.MudanzaEstatus == 3)
                initPros.setText(getResources().getString(R.string.cancelado));
            else if(detailObj.MudanzaEstatus == 4)
                initPros.setText(getResources().getString(R.string.concluido));
            else if(detailObj.MudanzaEstatus == 8)
                initPros.setText(getResources().getString(R.string.pagada));
                //initPros.setVisibility(View.INVISIBLE);
        } else if(mudObj.MudanzaEstatusServicio == 6)
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

                    } else
                        showCustomDialog("No puedes iniciar",
                                "Valida que no tengas una mudanza iniciada o que la fecha no sea próxima",
                                "Continuar");
                } else if(title.contains("Solicitud")){
                    if(detailObj.MudanzaEstatus == 5 || detailObj.MudanzaEstatus == 6 || detailObj.MudanzaEstatus == 1)
                        paypalIntent();
                }
                break;
            case R.id.tel_lay:
                callIntent(detailObj.ClienteTelefono);
                break;
        }
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

    //*********************************************
    private void paypalIntent(){
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(Singleton.getMainActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        //return new PayPalPayment(new BigDecimal("0.01"), "USD", "sample item", paymentIntent);
        String costo = detailObj.MudanzaCosto.replace(",",".");
        costo = costo.replace("$", "");
        costo = costo.replace("MXN", "");
        costo = costo.replace(" ", "");
        return new PayPalPayment(new BigDecimal(costo), "MXN", "Mudanza", paymentIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        JSONObject jsonObject = new JSONObject(confirm.toJSONObject().toString(4));
                        JSONObject response = jsonObject.getJSONObject("response");
                        initSendPayment(response.getString("id"));
                        //Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        //displayResultText("PaymentConfirmation info received from PayPal");
                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        /*sendAuthorizationToServer(auth);
                        displayResultText("Future Payment code received from PayPal");*/

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        /*sendAuthorizationToServer(auth);
                        displayResultText("Profile Sharing code received from PayPal");*/

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void initSendPayment(String payment){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MudanzaFolioServicio", mudObj.MudanzaFolioServicio);
            jsonObject.put("MudanzaIdPago", payment);
            Object[] objs = new Object[]{"PagarMudanza", 21, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPaymentResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(Integer.parseInt(jsonObject.getString("ReturnError")) == 200){
                Singleton.showCustomDialog(getFragmentManager(),
                        "¡Excelente!", jsonObject.getString("Mensaje"), "Continuar", 2);
            } else {
                Singleton.showCustomDialog(getFragmentManager(),
                        "¡Atención!", jsonObject.getString("Mensaje"), "Continuar", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Singleton.dissmissLoad();
    }
    //*********************************************

}
