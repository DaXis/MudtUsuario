package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.adapters.MudsAdapter;
import com.mudtusuario.adapters.SoliAdapter;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SolicitudFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private ListView mudts;
    private ArrayList<MudObj> array = new ArrayList<>();
    private SoliAdapter adapter;
    private String title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
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
        Singleton.getActionText().setText("Solicitudes");
        //Singleton.getMenuBtn().setImageResource(R.drawable.ic_back);
        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_frag, container, false);

        TextView title = (TextView)rootView.findViewById(R.id.title);
        title.setText(this.title);

        mudts = (ListView)rootView.findViewById(R.id.mudts);
        adapter = new SoliAdapter(this, array);
        mudts.setAdapter(adapter);
        initConnection();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

        }
    }

    private void initConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        JSONObject jsonObject = new JSONObject();
        Object[] objs = new Object[]{"GetMudanzaPendienteListado", 5, this, jsonObject};
        ConnectToServer connectToServer = new ConnectToServer(objs);
    }

    public void getResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("ListadoMudanzas");
            array.clear();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject aux = jsonArray.getJSONObject(i);
                MudObj mudObj = new MudObj();
                mudObj.ClienteFoto = aux.getString("ClienteFoto");
                mudObj.ClienteId = aux.getString("ClienteId");
                mudObj.ClienteNombre = aux.getString("ClienteNombre");
                mudObj.MudanzaDireccionCarga = aux.getString("MudanzaDireccionCarga");
                mudObj.MudanzaDireccionDescarga = aux.getString("MudanzaDireccionDescarga");
                mudObj.MudanzaEstatusServicio = aux.getInt("MudanzaEstatusServicio");
                mudObj.MudanzaFechaSolicitud = aux.getString("MudanzaFechaSolicitud");
                mudObj.MudanzaFolioServicio = aux.getString("MudanzaFolioServicio");
                mudObj.MudanzaHoraSolicitud = aux.getString("MudanzaHoraSolicitud");
                mudObj.TipoUnidadDescrip = aux.getString("TipoUnidadDescrip");
                mudObj.UnidadPlacas = aux.getString("UnidadPlacas");
                array.add(mudObj);
            }
            adapter.updateAdapter(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Singleton.dissmissLoad();
    }

    public void initDetail(MudObj mudObj){
        Bundle bundle = new Bundle();
        bundle.putSerializable("mudObj", mudObj);
        bundle.putInt("lay", lay);
        bundle.putString("title", "Solicitud");
        bundle.putString("root", SolicitudFragment.class.getName());
        ViajeDetailFragment newItemFragment = new ViajeDetailFragment();
        newItemFragment.setArguments(bundle);
        Singleton.setCurrentFragment(newItemFragment);
        getFragmentManager().beginTransaction()
                .replace(lay, newItemFragment)
                .addToBackStack(null)
                .commit();
    }
}
