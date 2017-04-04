package com.mudtusuario.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.mudtusuario.R;
import com.mudtusuario.fragments.ProcessFragment;

public class PicDialog extends DialogFragment implements View.OnClickListener {

    private ProcessFragment fragment;
    private int id;

    public static PicDialog newInstance(ProcessFragment fragment, int id){
        PicDialog picDialog = new PicDialog();

        picDialog.fragment = fragment;
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        picDialog.setArguments(bundle);

        return picDialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        id = getArguments().getInt("id");
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.pic_dialog, null);
        builder.setView(v);

        LinearLayout camera = (LinearLayout)v.findViewById(R.id.camera);
        camera.setOnClickListener(this);

        LinearLayout gallery = (LinearLayout)v.findViewById(R.id.gallery);
        gallery.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.camera:
                fragment.initTakePic(1, "1");
                dismiss();
                break;
            case R.id.gallery:
                fragment.galleryIntent();
                dismiss();
                break;
        }
    }

}
