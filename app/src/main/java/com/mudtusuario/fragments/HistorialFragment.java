package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.adapters.MudsAdapter;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistorialFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private String title;
    private Button procss, fnshd;
    private FrameLayout subContent;
    private ProcessMudtFragment processMudtFragment;
    private FinishedMudtFragment finishedMudtFragment;
    private LinearLayout btns_lay;

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
        Singleton.setHistFragment(this);

        Singleton.getActionButon().setVisibility(View.INVISIBLE);
        Singleton.getActionText().setText("");
        Singleton.getMenuBtn().setImageResource(R.drawable.ic_menu);
        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, getActivity().findViewById(R.id.left_drawer));

        if(processMudtFragment == null || finishedMudtFragment == null){
            processMudtFragment = new ProcessMudtFragment();
            finishedMudtFragment = new FinishedMudtFragment();
        }
    }

    /*@Override
    public void onPause(){
        super.onPause();
        if(btns_lay != null)
            btns_lay.setVisibility(View.GONE);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_frag_cont, container, false);

        processMudtFragment = new ProcessMudtFragment();
        finishedMudtFragment = new FinishedMudtFragment();

        procss = (Button)rootView.findViewById(R.id.procss);
        procss.setOnClickListener(this);

        fnshd = (Button)rootView.findViewById(R.id.fnshd);
        fnshd.setOnClickListener(this);

        btns_lay = (LinearLayout)rootView.findViewById(R.id.btns_lay);

        subContent = (FrameLayout)rootView.findViewById(R.id.subContent);
        initProcessFragment();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.procss:
                procss.setBackgroundResource(R.drawable.osc_gray_rect);
                fnshd.setBackgroundResource(R.drawable.gray_rect);
                initProcessFragment();
                break;
            case R.id.fnshd:
                fnshd.setBackgroundResource(R.drawable.osc_gray_rect);
                procss.setBackgroundResource(R.drawable.gray_rect);
                initFinishFragment();
                break;
        }
    }

    private void removeFragments(){
        if(Singleton.getSubCurrentFragment() != null){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(Singleton.getSubCurrentFragment()).commit();
        }
    }

    private void initProcessFragment(){
        if(Singleton.getSubCurrentFragment() != processMudtFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", lay);
            if(processMudtFragment.getArguments() == null)
                processMudtFragment.setArguments(bundle);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(subContent.getId(), processMudtFragment).commit();
        }
    }

    private void initFinishFragment(){
        if(Singleton.getSubCurrentFragment() != finishedMudtFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", lay);
            if(finishedMudtFragment.getArguments() == null)
                finishedMudtFragment.setArguments(bundle);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(subContent.getId(), finishedMudtFragment).commit();
        }
    }

    /*public void showBtnsLay(){
        if(btns_lay != null)
            btns_lay.setVisibility(View.VISIBLE);
    }

    public void hideBtnsLay(){
        if(btns_lay != null)
            btns_lay.setVisibility(View.GONE);
    }*/
}
