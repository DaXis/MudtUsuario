package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.adapters.PagosAdapter;
import com.mudtusuario.dialogs.PagoDialog;
import com.mudtusuario.objs.PagoObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PagosFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private LinearLayout empty;
    private ListView pagos_list;
    private Button add_pago;
    private ArrayList<PagoObj> array = new ArrayList<>();
    private PagosAdapter adapter;

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
        Singleton.getActionText().setText("Pago");
        Singleton.getActionText().setVisibility(View.VISIBLE);
        Singleton.getToolLogo().setVisibility(View.GONE);
        Singleton.getMenuBtn().setImageResource(R.drawable.ic_back);

        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pagos_frag, container, false);

        empty = (LinearLayout)rootView.findViewById(R.id.empty);

        pagos_list = (ListView)rootView.findViewById(R.id.pagos_list);
        adapter = new PagosAdapter(getActivity(), array);
        pagos_list.setAdapter(adapter);
        pagos_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        add_pago = (Button)rootView.findViewById(R.id.add_pago);
        add_pago.setOnClickListener(this);

        initConnection();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.add_pago:
                showPagosDialog();
                break;
        }
    }

    private void showPagosDialog(){
        PagoDialog pagoDialog = PagoDialog.newInstance(this);
        pagoDialog.setCancelable(true);
        pagoDialog.show(getFragmentManager(), "pago dialog");
    }

    public void initConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ClienteId", Singleton.getUSerObj().GUID);
            Object[] objs = new Object[]{"GetFormasPago", 19, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray FormasPago = jsonObject.getJSONArray("FormasPago");
            array.clear();
            for(int i = 0; i < FormasPago.length(); i++){
                JSONObject pago = FormasPago.getJSONObject(i);
                PagoObj pagoObj = new PagoObj();
                pagoObj.FormaPagoId = pago.getInt("FormaPagoId");
                pagoObj.NumTarjeta = pago.getString("NumTarjeta");
                pagoObj.TipoTarjeta = pago.getString("TipoTarjeta");
                pagoObj.Favorita = pago.getBoolean("Favorita");
                array.add(pagoObj);
            }
            if(array.isEmpty()){
                empty.setVisibility(View.VISIBLE);
                pagos_list.setVisibility(View.GONE);
            } else {
                empty.setVisibility(View.GONE);
                pagos_list.setVisibility(View.VISIBLE);
                adapter.updateAdapter(array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Singleton.dissmissLoad();
    }

}
