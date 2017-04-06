package com.mudtusuario.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.fragments.SolicitudFragment;
import com.mudtusuario.objs.MonthObj;
import com.mudtusuario.objs.MudObj;

import java.util.ArrayList;

public class MonthsAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;
    private ArrayList<MonthObj> array;
    private Activity activity;

    public MonthsAdapter(Activity activity, ArrayList<MonthObj> array){
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
            convertView = inflater.inflate(R.layout.generic_row, parent, false);

        TextView name = SoliAdapter.ViewHolder.get(convertView, R.id.name);
        name.setText(array.get(position).name);

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

    public void updateAdapter(ArrayList<MonthObj> array){
        this.array = array;
        notifyDataSetChanged();
    }
}