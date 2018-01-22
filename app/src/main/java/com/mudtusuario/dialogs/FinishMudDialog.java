package com.mudtusuario.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.objs.DetailObj;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FinishMudDialog extends DialogFragment {

    private MudObj mudObj;
    private DetailObj detailObj;
    private EditText edit_desc;

    public static FinishMudDialog newInstance(MudObj mudObj, DetailObj detailObj){
        FinishMudDialog finishMudDialog = new FinishMudDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable("mudObj", mudObj);
        bundle.putSerializable("detailObj", detailObj);
        finishMudDialog.setArguments(bundle);

        return finishMudDialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        mudObj = (MudObj)getArguments().getSerializable("mudObj");
        detailObj = (DetailObj) getArguments().getSerializable("detailObj");

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.finish_dialog, null);
        builder.setView(v);

        TextView client = (TextView)v.findViewById(R.id.client);
        client.setText(detailObj.ClienteNombre);

        edit_desc = (EditText)v.findViewById(R.id.edit_desc);

        ImageView client_pic = (ImageView)v.findViewById(R.id.client_pic);
        ProgressBar progressBar = new ProgressBar(getActivity());
        Singleton.loadImage(detailObj.ClienteFoto, client_pic, progressBar);

        Button ok_dialog = (Button)v.findViewById(R.id.ok_dialog);
        ok_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initConnection();
            }
        });

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB){
            Log.e("solved super error", "solved super error OK");
        } else
            super.onSaveInstanceState(outState);
    }

    private void initConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MudanzaFolioServicio", detailObj.MudanzaFolioServicio);
            jsonObject.put("CalificacionTipo", 1);
            jsonObject.put("CalificacionComentarios", edit_desc.getText().toString());
            jsonObject.put("CalificacionValor", 3);
            Object[] objs = new Object[]{"SetOperadorCalificacion", 8, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getResponse(String result) {
        Singleton.dissmissLoad();
        Singleton.getMainActivity().initMapFragment();
        dismiss();
    }

}
