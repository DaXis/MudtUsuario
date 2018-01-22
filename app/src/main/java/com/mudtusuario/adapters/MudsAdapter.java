package com.mudtusuario.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.fragments.FinishedMudtFragment;
import com.mudtusuario.fragments.HistorialFragment;
import com.mudtusuario.fragments.MainFragment;
import com.mudtusuario.fragments.ProcessMudtFragment;
import com.mudtusuario.objs.MudObj;

import java.util.ArrayList;

public class MudsAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;
    private ArrayList<MudObj> array;
    private Fragment fragment;

    public MudsAdapter(Fragment fragment, ArrayList<MudObj> array){
        this.fragment = fragment;
        inflater = (LayoutInflater)fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.muds_row, parent, false);

        ImageView img = ViewHolder.get(convertView, R.id.img);
        ProgressBar progressBar = new ProgressBar(fragment.getActivity());
        Singleton.loadImage(array.get(position).ClienteFoto, img, progressBar);

        TextView name = ViewHolder.get(convertView, R.id.name);
        name.setText(array.get(position).ClienteNombre);

        TextView placas = ViewHolder.get(convertView, R.id.placas);
        placas.setText(array.get(position).MudanzaFolioServicio);

        TextView date = ViewHolder.get(convertView, R.id.date);
        date.setText(array.get(position).MudanzaFechaSolicitud+" "+array.get(position).MudanzaHoraSolicitud);

        TextView status = ViewHolder.get(convertView, R.id.status);
        if(array.get(position).MudanzaEstatusServicio == 5 ||
                array.get(position).MudanzaEstatusServicio == 6 || array.get(position).MudanzaEstatusServicio == 7)
            status.setText(Singleton.getMainActivity().getResources().getString(R.string.pagar));
        else if(array.get(position).MudanzaEstatusServicio == 0)
            status.setText(Singleton.getMainActivity().getResources().getString(R.string.pendiente));
        else if(array.get(position).MudanzaEstatusServicio == 1)
            status.setText(Singleton.getMainActivity().getResources().getString(R.string.pagar));
        else if(array.get(position).MudanzaEstatusServicio == 2)
            status.setText(Singleton.getMainActivity().getResources().getString(R.string.curso));
        else if(array.get(position).MudanzaEstatusServicio == 3)
            status.setText(Singleton.getMainActivity().getResources().getString(R.string.cancelado));
        else if(array.get(position).MudanzaEstatusServicio == 4)
            status.setText(Singleton.getMainActivity().getResources().getString(R.string.concluido));
        else if(array.get(position).MudanzaEstatusServicio == 8)
            status.setText(Singleton.getMainActivity().getResources().getString(R.string.pagada));

        ImageView img_cat = ViewHolder.get(convertView, R.id.img_cat);

        final Button info = ViewHolder.get(convertView, R.id.info);

        /*if(fragment.getClass() == HistorialFragment.class){
            if(array.get(position).MudanzaEstatusServicio != 6){
                info.setBackgroundResource(R.drawable.btn_bg);
                info.setTextColor(fragment.getActivity().getResources().getColor(R.color.text_icons));
            } else {
                info.setBackgroundResource(R.drawable.wth_btn);
                info.setTextColor(fragment.getActivity().getResources().getColor(R.color.primary_color));
            }
        }*/

        info.setBackgroundResource(R.drawable.btn_bg);
        info.setTextColor(fragment.getActivity().getResources().getColor(R.color.text_icons));

        /*info.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        info.setTextColor(fragment.getActivity().getResources().getColor(R.color.text_icons));
                        break;
                    case MotionEvent.ACTION_UP:
                        info.setTextColor(fragment.getActivity().getResources().getColor(R.color.primary_color));
                        break;
                }
                return false;
            }
        });*/
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Singleton.getCurrentFragment().getClass() == HistorialFragment.class) {
                    //((HistorialFragment) Singleton.getHistFragment()).hideBtnsLay();
                }
                if(fragment.getClass() == ProcessMudtFragment.class) {
                    ((ProcessMudtFragment) fragment).initDetail((MudObj) getItem(position));
                }
                if(fragment.getClass() == FinishedMudtFragment.class) {
                    ((FinishedMudtFragment) fragment).initDetail((MudObj) getItem(position));
                }
            }
        });

        TextView estatus = ViewHolder.get(convertView, R.id.estatus);
        if(array.get(position).MudanzaEstatus == 5 || array.get(position).MudanzaEstatus == 6 || array.get(position).MudanzaEstatus == 7)
            estatus.setText(Singleton.getMainActivity().getResources().getString(R.string.pagar));
        else if(array.get(position).MudanzaEstatus == 0)
            estatus.setText(Singleton.getMainActivity().getResources().getString(R.string.pendiente));
        else if(array.get(position).MudanzaEstatus == 1)
            estatus.setText(Singleton.getMainActivity().getResources().getString(R.string.pagar));
        else if(array.get(position).MudanzaEstatus == 2)
            estatus.setText(Singleton.getMainActivity().getResources().getString(R.string.curso));
        else if(array.get(position).MudanzaEstatus == 3)
            estatus.setText(Singleton.getMainActivity().getResources().getString(R.string.cancelado));
        else if(array.get(position).MudanzaEstatus == 4)
            estatus.setText(Singleton.getMainActivity().getResources().getString(R.string.concluido));
        else if(array.get(position).MudanzaEstatus == 8)
            estatus.setText(Singleton.getMainActivity().getResources().getString(R.string.pagada));

        return convertView;
    }

    public static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

    public void updateAdapter(ArrayList<MudObj> array){
        this.array = array;
        notifyDataSetChanged();
    }

    //cambios
}
