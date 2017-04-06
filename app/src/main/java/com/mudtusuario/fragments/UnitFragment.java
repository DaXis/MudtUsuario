package com.mudtusuario.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.objs.UnitObj;

public class UnitFragment extends Fragment {

    private UnitObj unitObj;

    public static UnitFragment newAvisoItem(UnitObj unitObj) {
        UnitFragment unitFragment = new UnitFragment();
        unitFragment.unitObj = unitObj;
        return unitFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.unit, container, false);

        ImageView imageFull = (ImageView)rootView.findViewById(R.id.unit_pic);
        ProgressBar progress = (ProgressBar)rootView.findViewById(R.id.progress);
        Singleton.loadUnitImage(unitObj.unit_pic, imageFull, progress);

        TextView unidad_type = (TextView)rootView.findViewById(R.id.unidad_type);
        unidad_type.setText(unitObj.unit_desc);

        TextView unidad_cap = (TextView)rootView.findViewById(R.id.unidad_cap);
        unidad_cap.setText("3.5 Toneladas");

        return rootView;
    }

}
