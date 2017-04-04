package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.dialogs.CustomDialog;
import com.mudtusuario.dialogs.RecoverDialog;
import com.mudtusuario.objs.TokenObj;
import com.mudtusuario.objs.UserObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private EditText editUser, editPass;
    private Button login_btn;
    private TextView forget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
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
        Singleton.getActionText().setText(getResources().getString(R.string.splash_btn));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_frag, container, false);

        editUser = (EditText)rootView.findViewById(R.id.editUser);
        editUser.setText("Chicho");
        editPass = (EditText)rootView.findViewById(R.id.editPass);
        editPass.setText("1234");
        editPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    Singleton.hideKeyboard(editPass);
                    initLogin();
                    handled = true;
                }
                return handled;
            }
        });

        login_btn = (Button)rootView.findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);

        forget = (TextView)rootView.findViewById(R.id.forget);
        forget.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.login_btn:
                initLogin();
                break;
            case R.id.forget:
                showRecoverDialog();
                break;
        }
    }

    private void initLogin(){
        Singleton.showLoadDialog(getFragmentManager());
        Object[] objs = new Object[]{"http://69.167.152.217:8080/MudanzasJavaEnvironment/oauth/access_token",
                0, this, editUser.getText().toString(), editPass.getText().toString()};
        ConnectToServer connectToServer = new ConnectToServer(objs, false);
    }

    public void getResponse(String result) {
        Log.d("login token", result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.has("error")){
                Singleton.dissmissLoad();
                JSONObject error = jsonObject.getJSONObject("error");
                showCustomDialog("¡Ups!", error.getString("message"), "Aceptar");
            } else {
                TokenObj tokenObj = new TokenObj();
                tokenObj.access_token = jsonObject.getString("access_token");
                tokenObj.scope = jsonObject.getString("scope");
                tokenObj.refresh_token = jsonObject.getString("refresh_token");
                tokenObj.user_guid = jsonObject.getString("user_guid").replace("    ","");
                Singleton.setTokenObj(tokenObj);
                Singleton.saveSettings("token_json", result);
                initLoginData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.dissmissLoad();
        }
    }

    private void initLoginData(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_guid", Singleton.getTokenObj().user_guid);
            Object[] objs = new Object[]{"GetDataLogin", 1, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getResponseData(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.has("error")){
                JSONObject error = jsonObject.getJSONObject("error");
                showCustomDialog("Error", error.getString("message"), "Continuar");
            }
            if(jsonObject.has("SDTRestLogin")){
                JSONObject SDTRestLogin = jsonObject.getJSONObject("SDTRestLogin");
                UserObj userObj = new UserObj();
                userObj.Error = SDTRestLogin.getString("Error");
                userObj.Foto = SDTRestLogin.getString("Foto");
                userObj.GUID = SDTRestLogin.getString("GUID");
                userObj.NombreCompleto = SDTRestLogin.getString("NombreCompleto");
                userObj.Rol = SDTRestLogin.getString("Rol");
                Singleton.setUserObj(userObj);
                Singleton.saveSettings("login_json", result);
                Singleton.saveSettings("login_flag", true);
                Singleton.dissmissLoad();
                Singleton.getMainActivity().initMapFragment();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.dissmissLoad();
        }
    }

    private void showCustomDialog(String title, String body, String action){
        CustomDialog custoDialog = CustomDialog.newInstance(title, body, action);
        custoDialog.setCancelable(true);
        custoDialog.show(getFragmentManager(), "custom dialog");
    }

    private void showRecoverDialog(){
        RecoverDialog custoDialog = RecoverDialog.newInstance();
        custoDialog.setCancelable(true);
        custoDialog.show(getFragmentManager(), "recover dialog");
    }

}
