package com.mudtusuario.adapters;

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
import com.mudtusuario.fragments.HistorialFragment;
import com.mudtusuario.fragments.MainFragment;
import com.mudtusuario.fragments.SolicitudFragment;
import com.mudtusuario.objs.MudObj;

import java.util.ArrayList;

public class SoliAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;
    private ArrayList<MudObj> array;
    private Fragment fragment;

    public SoliAdapter(Fragment fragment, ArrayList<MudObj> array){
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
            convertView = inflater.inflate(R.layout.solis_row, parent, false);

        ImageView img = SoliAdapter.ViewHolder.get(convertView, R.id.img);
        ProgressBar progressBar = new ProgressBar(fragment.getActivity());
        Singleton.loadImage(array.get(position).ClienteFoto, img, progressBar);

        TextView name = SoliAdapter.ViewHolder.get(convertView, R.id.name);
        name.setText(array.get(position).ClienteNombre);

        TextView placas = SoliAdapter.ViewHolder.get(convertView, R.id.placas);
        placas.setText(array.get(position).MudanzaFolioServicio);

        TextView date = SoliAdapter.ViewHolder.get(convertView, R.id.date);
        date.setText(array.get(position).MudanzaFechaSolicitud);

        ImageView img_cat = SoliAdapter.ViewHolder.get(convertView, R.id.img_cat);

        final Button info = SoliAdapter.ViewHolder.get(convertView, R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SolicitudFragment)fragment).initDetail(array.get(position));
            }
        });

        TextView loc_ini = SoliAdapter.ViewHolder.get(convertView, R.id.loc_ini);
        loc_ini.setText(array.get(position).MudanzaDireccionCarga);

        TextView loc_end = SoliAdapter.ViewHolder.get(convertView, R.id.loc_end);
        loc_end.setText(array.get(position).MudanzaDireccionDescarga);

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
}
