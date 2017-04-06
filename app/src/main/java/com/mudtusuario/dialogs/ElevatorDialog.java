package com.mudtusuario.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mudtusuario.R;
import com.mudtusuario.adapters.MonthsAdapter;
import com.mudtusuario.adapters.YesNoAdapter;
import com.mudtusuario.fragments.InitMudFragment;

import java.util.ArrayList;

public class ElevatorDialog extends DialogFragment {

    private InitMudFragment fragment;
    private int flag;

    public static ElevatorDialog newInstance(InitMudFragment fragment, int flag) {
        ElevatorDialog elevatorDialog = new ElevatorDialog();
        elevatorDialog.fragment = fragment;

        Bundle bundle = new Bundle();
        bundle.putInt("flag", flag);
        elevatorDialog.setArguments(bundle);

        return elevatorDialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo;

        flag = getArguments().getInt("flag");

        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.elevator_dialog, null);
        builder.setView(v);

        ListView elev_list = (ListView) v.findViewById(R.id.elev_list);
        final YesNoAdapter adapter = new YesNoAdapter(getActivity(), getAdapter());
        elev_list.setAdapter(adapter);
        elev_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(flag == 0)
                    fragment.setElevCarg((String)adapter.getItem(i));
                else
                    fragment.setElevDesc((String)adapter.getItem(i));
                dismiss();
            }
        });

        return builder.create();
    }


    public ArrayList<String> getAdapter() {
        ArrayList<String> adapter = new ArrayList<>();
        adapter.add("Si");
        adapter.add("No");
        return adapter;
    }
}
