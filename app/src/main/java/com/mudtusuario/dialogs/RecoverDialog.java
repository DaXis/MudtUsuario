package com.mudtusuario.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.utils.ConnectToServer;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverDialog extends DialogFragment implements View.OnClickListener {

    EditText user;

    public static RecoverDialog newInstance(){
        RecoverDialog recoverDialog = new RecoverDialog();
        return recoverDialog;
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
        View v = inflater.inflate(R.layout.recover_dialog, null);
        builder.setView(v);

        Button cancel = (Button)v.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        Button action = (Button)v.findViewById(R.id.action);
        action.setOnClickListener(this);

        user = (EditText)v.findViewById(R.id.user);

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.action:
                if(user.getText().length() > 0)
                    recoverConnection();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    private void recoverConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserName", user.getText().toString());
            Object[] objs = new Object[]{"RecoverPassword", 9, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getRecoverResponse(String arg){
        try {
            JSONObject jsonObject = new JSONObject(arg);
            if(jsonObject.get("ReturnError").equals("200")) {
                Singleton.dissmissLoad();
                dismiss();
            } else
                Singleton.dissmissLoad();
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.dissmissLoad();
        }
    }

}
