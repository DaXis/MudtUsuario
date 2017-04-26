package com.mudtusuario.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mudtusuario.R;
import com.mudtusuario.objs.PagoObj;

import java.util.ArrayList;

public class PagosAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;
    private ArrayList<PagoObj> array;
    private Activity activity;

    public PagosAdapter(Activity activity, ArrayList<PagoObj> array){
        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView = inflater.inflate(R.layout.pago_row, parent, false);

        TextView type_number = ViewHolder.get(convertView, R.id.type_number);
        type_number.setText(array.get(position).TipoTarjeta+" "+array.get(position).NumTarjeta);

        ImageView target_type = ViewHolder.get(convertView, R.id.target_type);
        if(array.get(position).TipoTarjeta.toLowerCase().contains("master")){
            target_type.setImageResource(R.drawable.domy_mc);
        } else if(array.get(position).TipoTarjeta.toLowerCase().contains("visa")){
            target_type.setImageResource(R.drawable.domy_visa);
        } else {
            target_type.setImageResource(R.drawable.domy_ae);
        }

        ImageView status = ViewHolder.get(convertView, R.id.status);
        if(array.get(position).Favorita){
            status.setVisibility(View.VISIBLE);
            status.setImageResource(R.drawable.ic_inicio);
        } else {
            status.setVisibility(View.INVISIBLE);
        }

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

    public void updateAdapter(ArrayList<PagoObj> array){
        this.array = array;
        notifyDataSetChanged();
    }
}
