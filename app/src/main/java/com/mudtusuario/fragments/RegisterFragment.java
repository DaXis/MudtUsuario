package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private EditText name, app, apm, email, lada, movil, pass, user, pass_confirm;
    private Button regis_btn;

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
        lada = (EditText)rootView.findViewById(R.id.lada);
        movil = (EditText)rootView.findViewById(R.id.movil);
        pass = (EditText)rootView.findViewById(R.id.pass);
        user = (EditText)rootView.findViewById(R.id.user);
        pass_confirm = (EditText)rootView.findViewById(R.id.pass_confirm);

        regis_btn = (Button)rootView.findViewById(R.id.regis_btn);
        regis_btn.setOnClickListener(this);

        return rootView;
    }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.regis_btn:
                    initConnection();
                    break;
            }
        }

        private void initConnection(){
            Singleton.showLoadDialog(getFragmentManager());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Nombre", name.getText().toString());
                jsonObject.put("ApellidoPaterno", app.getText().toString());
                jsonObject.put("ApellidoMaterno", apm.getText().toString());
                jsonObject.put("Correo", email.getText().toString());
                jsonObject.put("Telefono", lada.getText().toString()+movil.getText().length());
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

}
