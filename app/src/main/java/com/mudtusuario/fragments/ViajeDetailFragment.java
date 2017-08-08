package com.mudtusuario.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.dialogs.CustomDialog;
import com.mudtusuario.objs.DetailObj;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ShippingAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ViajeDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Paypal";
    private int lay;
    private MudObj mudObj;
    private ImageView user_pic;
    private TextView user_name, tel, date, hour, loc, unid_a, unid_b, caps, piso, have, desc, date_b, hour_b, loc_b,
            dist, piso_b, have_b, precio, precioHint;
    private Button initPros;
    private DetailObj detailObj;
    private String title;
    private LinearLayout telLay;

    //**************************************
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    private static final String CONFIG_CLIENT_ID = "credentials from developer.paypal.com";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Ejemplo mudanzas")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    //**************************************

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
                    //initPagoConnection();
                    paypalIntent();
                }
                break;
            case R.id.tel_lay:
                callIntent(detailObj.ClienteTelefono);
                break;
        }
    }

    private void paypalIntent(){
        Log.d("paypal intent", "paypalIntent()");
        Intent intent = new Intent(Singleton.getMainActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        Singleton.getMainActivity().startService(intent);
    }

    //*********************************************************************
    public void onBuyPressed(View pressed) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        Intent intent = new Intent(Singleton.getMainActivity(), PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal("0.01"), "USD", "sample item",
                paymentIntent);
    }

    /*
     * This method shows use of optional payment details and item list.
     */
    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items =
                {
                        new PayPalItem("sample item #1", 2, new BigDecimal("87.50"), "USD",
                                "sku-12345678"),
                        new PayPalItem("free sample item #2", 1, new BigDecimal("0.00"),
                                "USD", "sku-zero-price"),
                        new PayPalItem("sample item #3 with a longer name", 6, new BigDecimal("37.99"),
                                "USD", "sku-33333")
                };
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal("7.21");
        BigDecimal tax = new BigDecimal("4.67");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, "USD", "sample item", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }

    /*
     * Add app-provided shipping address to payment
     */
    private void addAppProvidedShippingAddress(PayPalPayment paypalPayment) {
        ShippingAddress shippingAddress =
                new ShippingAddress().recipientName("Mom Parker").line1("52 North Main St.")
                        .city("Austin").state("TX").postalCode("78729").countryCode("US");
        paypalPayment.providedShippingAddress(shippingAddress);
    }

    /*
     * Enable retrieval of shipping addresses from buyer's PayPal account
     */
    private void enableShippingAddressRetrieval(PayPalPayment paypalPayment, boolean enable) {
        paypalPayment.enablePayPalShippingAddressesRetrieval(enable);
    }

    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(Singleton.getMainActivity(), PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    public void onProfileSharingPressed(View pressed) {
        Intent intent = new Intent(Singleton.getMainActivity(), PayPalProfileSharingActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());

        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS) );
        return new PayPalOAuthScopes(scopes);
    }

    protected void displayResultText(String result) {
        //((TextView)findViewById(R.id.txtResult)).setText("Result : " + result);
        Toast.makeText(
                Singleton.getMainActivity(),
                result, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        displayResultText("PaymentConfirmation info received from PayPal");


                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
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

                        sendAuthorizationToServer(auth);
                        displayResultText("Future Payment code received from PayPal");

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

                        sendAuthorizationToServer(auth);
                        displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid " +
                                "PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */

    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(Singleton.getMainActivity());

        Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId);

        // TODO: Send metadataId and transaction details to your server for processing with
        // PayPal...
        displayResultText("Client Metadata Id received from SDK");
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        Singleton.getMainActivity().stopService(new Intent(Singleton.getMainActivity(), PayPalService.class));
        super.onDestroy();
    }

    //*********************************************************************

    /*private void initPagoConnection(){
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
    }*/

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
