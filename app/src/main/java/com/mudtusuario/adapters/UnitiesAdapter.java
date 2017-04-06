package com.mudtusuario.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mudtusuario.fragments.UnitFragment;
import com.mudtusuario.objs.UnitObj;

import java.util.ArrayList;

public class UnitiesAdapter extends FragmentPagerAdapter {

    protected static final String[] TITLES = new String[]{"img_0", "img_1", "img_2", "img_3"};

    private static ArrayList<Fragment> frags = new ArrayList<>();
    private static ArrayList<UnitObj> units = new ArrayList<>();

    public UnitiesAdapter(FragmentManager fm, ArrayList<UnitObj> units){
        super(fm);
        UnitiesAdapter.units = units;

        frags.clear();
        for(int i = 0; i < units.size(); i++){
            frags.add(UnitFragment.newAvisoItem(units.get(i)));
        }
    }

    @Override
    public Fragment getItem(int arg0) {
        return frags.get(arg0);
    }

    public UnitObj getUnitItem(int arg){
        return units.get(arg);
    }

    @Override
    public int getCount() {
        return frags.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    public void updateAdapter(){
        notifyDataSetChanged();
    }

}
