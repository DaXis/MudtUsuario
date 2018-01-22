package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.adapters.MudsAdapter;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProcessMudtFragment extends Fragment implements View.OnClickListener {

    private int lay, arg = 0;
    private ListView mudts;
    private ArrayList<MudObj> array = new ArrayList<>();
    private MudsAdapter adapter;
    private String title;
    //private Button btn_act, btn_ant;

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
        Singleton.setSubCurrentFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_frag, container, false);

        /*TextView title = (TextView)rootView.findViewById(R.id.title);
        title.setText(this.title);*/

        /*btn_act = (Button)rootView.findViewById(R.id.btn_act);
        btn_act.setOnClickListener(this);

        btn_ant = (Button)rootView.findViewById(R.id.btn_ant);
        btn_ant.setOnClickListener(this);*/

        mudts = (ListView)rootView.findViewById(R.id.mudts);
        adapter = new MudsAdapter(this, array);
        mudts.setAdapter(adapter);
        initConnection();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            /*case R.id.btn_act:
                //btnsBg(0);
                break;
            case R.id.btn_ant:
                //btnsBg(1);
                break;*/
        }
    }

    private void initConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ClienteId", Singleton.getUSerObj().GUID);
            jsonObject.put("Historial", arg);
            Object[] objs = new Object[]{"GetMudanzaListadoCliente", 2, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                mudObj.MudanzaEstatus = aux.getInt("MudanzaEstatus");
                mudObj.MudanzaFechaSolicitud = aux.getString("MudanzaFechaSolicitud");
                mudObj.MudanzaFolioServicio = aux.getString("MudanzaFolioServicio");
                mudObj.MudanzaHoraSolicitud = aux.getString("MudanzaHoraSolicitud");
                mudObj.TipoUnidadDescrip = aux.getString("SGTipoUnidadDesc");
                mudObj.UnidadFoto = aux.getString("SGTipoUnidadFoto");
                mudObj.UnidadPlacas = aux.getString("SGTipoUnidadId");
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
        bundle.putString("root", HistorialFragment.class.getName());
        ViajeDetailFragment newItemFragment = new ViajeDetailFragment();
        newItemFragment.setArguments(bundle);
        Singleton.setCurrentFragment(newItemFragment);
        getFragmentManager().beginTransaction()
                .replace(lay, newItemFragment)
                .addToBackStack(null)
                .commit();
    }

}