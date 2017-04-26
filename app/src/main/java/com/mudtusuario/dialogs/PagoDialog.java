package com.mudtusuario.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.fragments.PagosFragment;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PagoDialog extends DialogFragment implements View.OnClickListener {

    private PagosFragment fragment;
    private EditText titular, tarjeta, fecha, cvv, pais;
    private ImageView target_type;

    public static PagoDialog newInstance(PagosFragment fragment){
        PagoDialog pagoDialog = new PagoDialog();
        pagoDialog.fragment = fragment;
        return pagoDialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.pagos_dialog, null);
        builder.setView(v);

        target_type = (ImageView)v.findViewById(R.id.target_type);

        titular = (EditText)v.findViewById(R.id.titular);
        tarjeta = (EditText)v.findViewById(R.id.tarjeta);
        fecha = (EditText)v.findViewById(R.id.fecha);
        cvv = (EditText)v.findViewById(R.id.cvv);
        pais = (EditText)v.findViewById(R.id.pais);

        tarjeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 1){
                    target_type.setVisibility(View.VISIBLE);
                    int aux0 = Integer.parseInt(""+s.toString().charAt(0));
                    if(aux0 == 4)
                        target_type.setImageResource(R.drawable.domy_visa);
                    else if(aux0 == 5)
                        target_type.setImageResource(R.drawable.domy_mc);
                    else
                        target_type.setImageResource(R.drawable.domy_ae);
                } else if(s.length() == 0)
                    target_type.setVisibility(View.GONE);
            }
        });

        fecha.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 2){
                    fecha.setText(fecha.getText().toString()+"/");
                } else if(s.length() < 2 && fecha.getText().toString().contains("/"))
                    fecha.setText(fecha.getText().toString().replace("/",""));
            }
        });

        Button cancel = (Button)v.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        Button add = (Button)v.findViewById(R.id.add);
        add.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cancel:
                dismiss();
                break;
            case R.id.add:
                initConnection();
                break;
        }
    }

    private void initConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject pago = new JSONObject();
            pago.put("Mode", "INS");
            pago.put("ClienteId", Singleton.getUSerObj().GUID);
            pago.put("Titular", titular.getText().toString());
            pago.put("NumTarjeta", tarjeta.getText().toString());
            pago.put("CVV", cvv.getText().toString());
            String[] aux = fecha.getText().toString().split("[/]");
            pago.put("Mes", aux[0]);
            pago.put("Anio", aux[1]);

            /*int aux0 = Integer.parseInt(""+tarjeta.getText().toString().charAt(0));
            if(aux0 == 4)
                pago.put("TipoTarjeta", "Visa");
            else if(aux0 == 5)
                pago.put("TipoTarjeta", "Mastercard");
            else if(aux0 == 3)
                pago.put("TipoTarjeta", "American Express");
            else
                pago.put("TipoTarjeta", "Otra");*/

            //pago.put("Favorita", false);
            jsonObject.put("DatosFormaPago", pago);
            Object[] objs = new Object[]{"FormasPago", 18, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("ReturnError").contains("200")){
                Singleton.genToast(getActivity(), jsonObject.getString("Mensaje"));
                fragment.initConnection();
            }
            Singleton.dissmissLoad();
            dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.dissmissLoad();
        }
    }

}
