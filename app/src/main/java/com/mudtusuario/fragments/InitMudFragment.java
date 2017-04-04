package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.objs.MudUsObj;
import com.mudtusuario.objs.UnitObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class InitMudFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private TextView carga_dir;
    private MudUsObj mudObj;
    private ArrayList<UnitObj> unitObjs = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
        mudObj = (MudUsObj)bundle.getSerializable("mudObj");
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
        Singleton.getActionText().setText("Requisitos");
        Singleton.getActionText().setVisibility(View.VISIBLE);
        Singleton.getToolLogo().setVisibility(View.GONE);

        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.init_mud_frag, container, false);

        initUnitsData();

        carga_dir = (TextView)rootView.findViewById(R.id.carga_dir);
        carga_dir.setText(mudObj.carga_dir);

        getDays();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

        }
    }

    private void initUnitsData(){
        JSONObject jsonObject = new JSONObject();
        Object[] objs = new Object[]{"GetTipoUnidades", 14, this, jsonObject};
        ConnectToServer connectToServer = new ConnectToServer(objs);
    }


    public void getResponse(String result) {
        try {
            unitObjs.clear();
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("ReturnError").equals("200")){
                JSONArray jsonArray = jsonObject.getJSONArray("TipoUnidades");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject unit = jsonArray.getJSONObject(i);
                    UnitObj unitObj = new UnitObj();
                    unitObj.unit_id = unit.getInt("TipoUnidadId");
                    unitObj.unit_desc = unit.getString("TipoUnidadDescrip");
                    unitObj.unit_pic = unit.getString("TipoUnidadFoto");
                    unitObjs.add(unitObj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDays(){
        int iYear = 2017;
        int iMonth = Calendar.FEBRUARY;
        int iDay = 1;
        Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d("dias del mes ------>", ""+daysInMonth);
    }

}
