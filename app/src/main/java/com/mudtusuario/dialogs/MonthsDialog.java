package com.mudtusuario.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.adapters.MonthsAdapter;
import com.mudtusuario.fragments.InitMudFragment;
import com.mudtusuario.objs.MonthObj;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthsDialog extends DialogFragment {

    private InitMudFragment fragment;

    public static MonthsDialog newInstance(InitMudFragment fragment){
        MonthsDialog monthsDialog = new MonthsDialog();
        monthsDialog.fragment = fragment;
        return monthsDialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.moths_dialog, null);
        builder.setView(v);

        ListView months_list = (ListView)v.findViewById(R.id.months_list);
        final MonthsAdapter adapter = new MonthsAdapter(getActivity(), getAdapter());
        months_list.setAdapter(adapter);
        months_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //fragment.setMonth((MonthObj)adapter.getItem(i));
                dismiss();
            }
        });

        return builder.create();
    }

    private ArrayList<MonthObj> getAdapter(){
        ArrayList<MonthObj> monthObjs = new ArrayList<>();

        MonthObj monthObj1 = new MonthObj();
        monthObj1.id = Calendar.JANUARY;
        monthObj1.name = "Enero";
        monthObjs.add(monthObj1);

        MonthObj monthObj2 = new MonthObj();
        monthObj2.id = Calendar.FEBRUARY;
        monthObj2.name = "Febrero";
        monthObjs.add(monthObj2);

        MonthObj monthObj3 = new MonthObj();
        monthObj3.id = Calendar.MARCH;
        monthObj3.name = "Marzo";
        monthObjs.add(monthObj3);

        MonthObj monthObj4 = new MonthObj();
        monthObj4.id = Calendar.APRIL;
        monthObj4.name = "Abril";
        monthObjs.add(monthObj4);

        MonthObj monthObj5 = new MonthObj();
        monthObj5.id = Calendar.MAY;
        monthObj5.name = "Mayo";
        monthObjs.add(monthObj5);

        MonthObj monthObj6 = new MonthObj();
        monthObj6.id = Calendar.JUNE;
        monthObj6.name = "Junio";
        monthObjs.add(monthObj6);

        MonthObj monthObj7 = new MonthObj();
        monthObj7.id = Calendar.JULY;
        monthObj7.name = "Julio";
        monthObjs.add(monthObj7);

        MonthObj monthObj8 = new MonthObj();
        monthObj8.id = Calendar.AUGUST;
        monthObj8.name = "Agosto";
        monthObjs.add(monthObj8);

        MonthObj monthObj9 = new MonthObj();
        monthObj9.id = Calendar.SEPTEMBER;
        monthObj9.name = "Septiembre";
        monthObjs.add(monthObj9);

        MonthObj monthObj10 = new MonthObj();
        monthObj10.id = Calendar.OCTOBER;
        monthObj10.name = "Octubre";
        monthObjs.add(monthObj10);

        MonthObj monthObj11 = new MonthObj();
        monthObj11.id = Calendar.NOVEMBER;
        monthObj11.name = "Noviembre";
        monthObjs.add(monthObj11);

        MonthObj monthObj12 = new MonthObj();
        monthObj12.id = Calendar.DECEMBER;
        monthObj12.name = "Diciembre";
        monthObjs.add(monthObj12);

        return monthObjs;
    }

}