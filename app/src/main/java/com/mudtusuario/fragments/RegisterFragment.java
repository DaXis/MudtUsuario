package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private EditText name, app, apm, email, movil, pass, user, pass_confirm;
    private Button regis_btn;
    private ImageView pic;

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
        Singleton.getMenuBtn().setVisibility(View.INVISIBLE);
        Singleton.getActionText().setText(getResources().getString(R.string.regis_title));
        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.regis_frag, container, false);

        name = (EditText)rootView.findViewById(R.id.name);
        app = (EditText)rootView.findViewById(R.id.app);
        apm = (EditText)rootView.findViewById(R.id.apm);
        email = (EditText)rootView.findViewById(R.id.email);
        movil = (EditText)rootView.findViewById(R.id.movil);
        pass = (EditText)rootView.findViewById(R.id.pass);
        user = (EditText)rootView.findViewById(R.id.user);
        pass_confirm = (EditText)rootView.findViewById(R.id.pass_confirm);

        regis_btn = (Button)rootView.findViewById(R.id.regis_btn);
        regis_btn.setOnClickListener(this);

        pic = (ImageView)rootView.findViewById(R.id.pic);
        pic.setOnClickListener(this);

        return rootView;
    }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.regis_btn:
                    validations();
                    break;
                case R.id.pic:
                    break;
            }
        }

        private void validations(){
            if(user.getText().length() > 0){
                if(name.getText().length() > 0){
                    if(app.getText().length() > 0){
                        if(apm.getText().length() > 0){
                            if(email.getText().length() > 0){
                                if(validarEmail(email.getText().toString())){
                                    if(movil.getText().length() > 0 && movil.getText().length() == 10){
                                        if(pass.getText().length() > 0){
                                            if(pass_confirm.getText().length() > 0){
                                                if(pass_confirm.getText().toString().equals(pass.getText().toString())){
                                                    initConnection();
                                                } else
                                                    Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                                                            "La confirmación de tu contraseña no coincide con la proporcionada", "Aceptar", 0);
                                            } else
                                                Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                                                        "Debes confirmar tu contraseña", "Aceptar", 0);
                                        } else
                                            Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                                                    "Debes introducir una contraseña", "Aceptar", 0);
                                    } else
                                        Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                                                "El numero telefónico debe ser de 10 dígitos", "Aceptar", 0);
                                } else
                                    Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                                            "Debes introducir un correo electrónico valido", "Aceptar", 0);
                            } else
                                Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                                        "Debes introducir un correo electrónico", "Aceptar", 0);
                        } else
                            Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                                    "Debes itroducir tu apellido materno", "Aceptar", 0);
                    } else
                        Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                                "Debes introducir tu apellido paterno", "Aceptar", 0);
                } else
                    Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                            "Debes introducir tu nombre", "Aceptar", 0);
            } else
                Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                        "Debes introducir un nombre de usuario", "Aceptar", 0);
        }

        private void initConnection(){
            Singleton.showLoadDialog(getFragmentManager());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Nombre", name.getText().toString());
                jsonObject.put("ApellidoPaterno", app.getText().toString());
                jsonObject.put("ApellidoMaterno", apm.getText().toString());
                jsonObject.put("Correo", email.getText().toString());
                jsonObject.put("Telefono", movil.getText().length());
                jsonObject.put("NombreUsuario", user.getText().toString());
                jsonObject.put("Contrasenia", pass.getText().toString());
                JSONObject ClienteDatos  = new JSONObject();
                ClienteDatos.put("ClienteDatos", jsonObject);
                Object[] objs = new Object[]{"AltaCliente", 13, this, ClienteDatos};
                ConnectToServer connectToServer = new ConnectToServer(objs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void getResponse(String arg){
            Singleton.dissmissLoad();
            try {
                JSONObject jsonObject = new JSONObject(arg);
                if(jsonObject.getString("ReturnError").equals("200")){
                    Singleton.genToast(getActivity(), jsonObject.getString("Mensaje"));
                    getActivity().onBackPressed();
                } else
                    Singleton.genToast(getActivity(), jsonObject.getString("Mensaje"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    private boolean validarEmail(String email){
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);

        if (mather.find() == true)
            return true;
        else
            return false;
    }

}
