package com.mudtusuario.fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class AboutFragment extends Fragment implements View.OnClickListener {

    private int lay;

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
        Singleton.getActionText().setText("Acerca de");
        //Singleton.getMenuBtn().setImageResource(R.drawable.ic_back);
        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, getActivity().findViewById(R.id.left_drawer));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_frag, container, false);

        PackageInfo pInfo = null;
        String ver = "";
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            ver = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView version = (TextView)rootView.findViewById(R.id.version);
        version.setText("Transportes Aliquam V"+ver);

        String terms = loadFromResource(R.raw.about);
        WebView web = (WebView) rootView.findViewById(R.id.web);
        web.loadData(terms, "text/html; charset=utf-8", "UTF-8");

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

        }
    }

    private String loadFromResource(int resource){
        String data = "";
        String p = "";
        final InputStream rr = getResources().openRawResource(resource);
        try {
            final BufferedReader e0 = new BufferedReader(new InputStreamReader(rr, "UTF-8"));
            try {
                while ((p = e0.readLine()) != null){
                    data = data + p;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

}
