package com.mudtusuario.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mudtusuario.R;
import com.mudtusuario.Singleton;
import com.mudtusuario.adapters.UnitiesAdapter;
import com.mudtusuario.dialogs.ElevatorDialog;
import com.mudtusuario.dialogs.LoadDialog;
import com.mudtusuario.dialogs.MonthsDialog;
import com.mudtusuario.objs.MonthObj;
import com.mudtusuario.objs.MudObj;
import com.mudtusuario.objs.MudUsObj;
import com.mudtusuario.objs.UnitObj;
import com.mudtusuario.utils.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class InitMudFragment extends Fragment implements View.OnClickListener {

    private int lay;
    public static TextView carga_dir, day_past, day_mes_past, day, day_mes, day_next, day_mes_next, hour_past, hour, hour_next, min_past,
        min, min_next, desc_dir, elev_carga, elev_desc, completeDate;
    private MudUsObj mudObj;
    private ArrayList<UnitObj> unitObjs = new ArrayList<>();
    private MonthObj monthObj;
    private LinearLayout past_lay, current_lay, next_lay;
    public static String dayOfWeek, dayMonth;
    public static long currentLong;
    public static int currentHour, currentMin=-1;
    private EditText piso_carga, mudt_desc, piso_desc;
    private Button solic_btn;
    private ViewPager subContent;
    private UnitiesAdapter adapter;
    private UnitObj unitObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
        mudObj = (MudUsObj)bundle.getSerializable("mudObj");
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
        Singleton.getActionText().setText("Registro");
        Singleton.getActionText().setVisibility(View.VISIBLE);
        Singleton.getToolLogo().setVisibility(View.GONE);
        Singleton.getMenuBtn().setImageResource(R.drawable.ic_back);

        Singleton.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                getActivity().findViewById(R.id.left_drawer));

        if(subContent != null)
            initUnitsData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.init_mud_frag, container, false);

        subContent = (ViewPager)rootView.findViewById(R.id.subContent);
        initUnitsData();
        subContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(adapter != null)
                    unitObj = adapter.getUnitItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        piso_carga = (EditText)rootView.findViewById(R.id.piso_carga);
        mudt_desc = (EditText)rootView.findViewById(R.id.mudt_desc);
        piso_desc = (EditText)rootView.findViewById(R.id.piso_desc);

        elev_carga = (TextView)rootView.findViewById(R.id.elev_carga);
        elev_carga.setText("No");
        elev_carga.setOnClickListener(this);
        elev_desc = (TextView)rootView.findViewById(R.id.elev_desc);
        elev_desc.setText("No");
        elev_desc.setOnClickListener(this);

        carga_dir = (TextView)rootView.findViewById(R.id.carga_dir);
        carga_dir.setText(mudObj.carga_dir);
        desc_dir = (TextView)rootView.findViewById(R.id.desc_dir);
        desc_dir.setText(mudObj.des_dir);

        day_past = (TextView)rootView.findViewById(R.id.day_past);
        day_mes_past = (TextView)rootView.findViewById(R.id.day_mes_past);
        day = (TextView)rootView.findViewById(R.id.day);
        day_mes = (TextView)rootView.findViewById(R.id.day_mes);
        day_next = (TextView)rootView.findViewById(R.id.day_next);
        day_mes_next = (TextView)rootView.findViewById(R.id.day_mes_next);
        completeDate = (TextView)rootView.findViewById(R.id.completeDate);

        hour_past = (TextView)rootView.findViewById(R.id.hour_past);
        hour_past.setOnClickListener(this);
        hour = (TextView)rootView.findViewById(R.id.hour);
        hour.setOnClickListener(this);
        hour_next = (TextView)rootView.findViewById(R.id.hour_next);
        hour_next.setOnClickListener(this);

        min_past = (TextView)rootView.findViewById(R.id.min_past);
        min_past.setOnClickListener(this);
        min = (TextView)rootView.findViewById(R.id.min);
        min.setOnClickListener(this);
        min_next = (TextView)rootView.findViewById(R.id.min_next);
        min_next.setOnClickListener(this);

        past_lay = (LinearLayout)rootView.findViewById(R.id.past_lay);
        past_lay.setOnClickListener(this);
        current_lay = (LinearLayout)rootView.findViewById(R.id.current_lay);
        current_lay.setOnClickListener(this);
        next_lay = (LinearLayout)rootView.findViewById(R.id.next_lay);
        next_lay.setOnClickListener(this);

        currentLong = System.currentTimeMillis();
        completeDate.setText(getCompleteDateString(currentLong));
        day.setText(getDayOfWeekString(currentLong));
        day_mes.setText(getDateString(currentLong));
        setOthersDays();

        solic_btn = (Button)rootView.findViewById(R.id.solic_btn);
        solic_btn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.past_lay:
                break;
            case R.id.current_lay:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.next_lay:
                break;
            case R.id.hour_past:
                break;
            case R.id.hour:
                DialogFragment newFragment1 = new TimePickerFragment();
                newFragment1.show(getFragmentManager(), "timePicker");
                break;
            case R.id.hour_next:
                break;
            case R.id.min_past:
                break;
            case R.id.min:
                DialogFragment newFragment2 = new TimePickerFragment();
                newFragment2.show(getFragmentManager(), "timePicker");
                break;
            case R.id.min_next:
                break;
            case R.id.elev_carga:
                showYesNoDialog(0);
                break;
            case R.id.elev_desc:
                showYesNoDialog(1);
                break;
            case R.id.solic_btn:
                validations();
                break;
        }
    }

    private void initUnitsData(){
        Singleton.showLoadDialog(getFragmentManager());
        JSONObject jsonObject = new JSONObject();
        Object[] objs = new Object[]{"GetTipoUnidades", 14, this, jsonObject};
        ConnectToServer connectToServer = new ConnectToServer(objs);
    }


    public void getResponse(String result) {
        try {
            unitObjs.clear();
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("ReturnError").equals("200")){
                JSONArray jsonArray = jsonObject.getJSONArray("TipoUnidades");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject unit = jsonArray.getJSONObject(i);
                    UnitObj unitObj = new UnitObj();
                    unitObj.unit_id = unit.getInt("TipoUnidadId");
                    unitObj.unit_desc = unit.getString("TipoUnidadDescrip");
                    unitObj.unit_pic = unit.getString("TipoUnidadFoto");
                    unitObj.SGTipoUnidadNomen = unit.getString("TipoUnidadNomenclatura");
                    unitObjs.add(unitObj);
                }
            }
            unitObj = unitObjs.get(0);
            adapter = new UnitiesAdapter(getFragmentManager(), unitObjs);
            subContent.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Singleton.dissmissLoad();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), R.style.DialogTheme, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String dateStg = "";
            month = month+1;
            if(month < 10)
                dateStg = day+" 0"+month+" "+year;
            else
                dateStg = day+" "+month+" "+year;

            Date date = InitMudFragment.getDateFromString(dateStg);
            InitMudFragment.completeDate.setText(getCompleteDateString(date.getTime()));
            InitMudFragment.day.setText(getDayOfWeekString(date.getTime()));
            InitMudFragment.day_mes.setText(getDateString(date.getTime()));
            currentLong = date.getTime();
            setOthersDays();
        }
    }

    public static String getDayOfWeekString(long time){
        String date = "";
        //date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").format(new Date(time));
        date = new SimpleDateFormat("EEEE").format(new Date(time));
        return date;
    }

    public static String getDateString(long time){
        String date = "";
        //date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").format(new Date(time));
        date = new SimpleDateFormat("dd MMMM").format(new Date(time));
        return date;
    }

    public static String getHourString(long time){
        String date = "";
        //date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").format(new Date(time));
        date = new SimpleDateFormat("hh").format(new Date(time));
        return date;
    }

    public static String getMinsString(long time){
        String date = "";
        //date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").format(new Date(time));
        date = new SimpleDateFormat("mm").format(new Date(time));
        return date;
    }

    public static String getCompleteDateString(long time){
        String date = "";
        //date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").format(new Date(time));
        date = new SimpleDateFormat("EEE dd MMMM, hh:mm").format(new Date(time));
        return date;
    }

    public static String getDateForService(long time){
        String date = "";
        //date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").format(new Date(time));
        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
        return date;
    }

    public static Date getDateFromString(String fecha){
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        Date date = null;
        try {
            date = format.parse(fecha);
            //System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void setOthersDays(){
        long pastDay = currentLong - Singleton.dayLong;
        day_past.setText(getDayOfWeekString(pastDay));
        day_mes_past.setText(getDateString(pastDay));

        long nextDay = currentLong + Singleton.dayLong;
        day_next.setText(getDayOfWeekString(nextDay));
        day_mes_next.setText(getDateString(nextDay));
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), R.style.DialogTheme, this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            currentHour = hourOfDay;
            currentMin = minute;

            hour.setText(""+hourOfDay);
            min.setText(""+minute);
            setOtherHours(hourOfDay, minute);
        }
    }

    public static void setOtherHours(int hour, int min){
        //long currentH = hour*Singleton.hourLong;
        //long pastH = currentH - Singleton.hourLong;
        //long nextH = currentH + Singleton.hourLong;
        int pastH = hour -1;
        int nextH = hour +1;

        if(hour == 0)
            hour_past.setText("11");
        else
            hour_past.setText(""+pastH);

        if(hour == 24)
            hour_next.setText("0");
        else
            hour_next.setText(""+nextH);

        long currentM = min*Singleton.minLong;
        long pastM = currentM - Singleton.minLong;
        long nextM = currentM + Singleton.minLong;
        min_past.setText(getMinsString(pastM));
        min_next.setText(getMinsString(nextM));
    }

    private void showYesNoDialog(int flag){
        ElevatorDialog elevatorDialog = ElevatorDialog.newInstance(this, flag);
        elevatorDialog.setCancelable(false);
        elevatorDialog.show(getFragmentManager(), "elevatorDialog");
    }

    public void setElevCarg(String arg){
        elev_carga.setText(arg);
    }

    public void setElevDesc(String arg){
        elev_desc.setText(arg);
    }

    private boolean validations(){
        if(piso_carga.getText().length() != 0){
            if(elev_carga.getText().length() == 2){
                if(currentHour != 0){
                    if(currentMin >= 0){
                        if(piso_desc.getText().length() != 0){
                            if(elev_desc.getText().length() == 2){
                                if(mudt_desc.getText().length() != 0){
                                    if(unitObj != null){
                                        Singleton.showCustomDialog(getFragmentManager(),
                                                unitObj.unit_desc, "¿Desea continuar con el tipo de unidad seleccionada?", "Continuar", 3);
                                    } else
                                        Singleton.showCustomDialog(getFragmentManager(),
                                                "¡Atención!", "Debes seleccionar el tipo de unidad que requieres", "Continuar", 0);
                                } else
                                    Singleton.showCustomDialog(getFragmentManager(),
                                            "¡Atención!", "Debes proporcionarnos una descripción del inmueble", "Continuar", 0);
                            } else
                                Singleton.showCustomDialog(getFragmentManager(),
                                        "¡Atención!", "Debes indicarnos si se tiene elevador de descarga", "Continuar", 0);
                        } else
                            Singleton.showCustomDialog(getFragmentManager(),
                                    "¡Atención!", "Debes proporcionar el piso de descarga", "Continuar", 0);
                    } else
                        Singleton.showCustomDialog(getFragmentManager(),
                                "¡Atención!", "Debes indicar el minuto en el que iniciara tu servicio", "Continuar", 0);
                } else
                    Singleton.showCustomDialog(getFragmentManager(),
                            "¡Atención!", "Debes seleccionar la hora en la que iniciara tu servicio", "Continuar", 0);
            } else
                Singleton.showCustomDialog(getFragmentManager(),
                        "¡Atención!", "Debes indicarnos si se tiene elevador de carga", "Continuar", 0);
        } else
            Singleton.showCustomDialog(getFragmentManager(),
                    "¡Atención!", "Debes proporcionar el piso de carga", "Continuar", 0);
        return false;
    }

    public void initAltaMudtConnect(){
        Singleton.showLoadDialog(getFragmentManager());
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject mudt = new JSONObject();
            mudt.put("MudanzaFechaInicio", getDateForService(currentLong));
            mudObj.fecha = getDateForService(currentLong);

            mudt.put("MudanzaHoraInicio", currentHour+":"+currentMin);
            mudObj.hora = currentHour+":"+currentMin;

            mudt.put("MudanzaDireccionCarga", mudObj.carga_dir);
            mudt.put("MudanzaDireccionDescarga", mudObj.des_dir);

            mudt.put("MudanzaPisoCarga", Integer.parseInt(piso_carga.getText().toString()));
            mudObj.piso_carga = Integer.parseInt(piso_carga.getText().toString());

            if(elev_carga.getText().toString().contains("Si")) {
                mudt.put("MudanzaElevadorCargaCargar", 1);
                mudObj.elev_carga = 1;
            } else {
                mudt.put("MudanzaElevadorCargaCargar", 2);
                mudObj.elev_carga = 2;
            }

            if(elev_desc.getText().toString().contains("Si")) {
                mudt.put("MudanzaElevadorCargaDescargar", 1);
                mudObj.elev_des = 1;
            } else {
                mudt.put("MudanzaElevadorCargaDescargar", 2);
                mudObj.elev_des = 2;
            }

            mudt.put("MudanzaPisoDescarga", Integer.parseInt(piso_desc.getText().toString()));
            mudObj.piso_des = Integer.parseInt(piso_desc.getText().toString());

            mudt.put("MudanzaDescripcionMobiliario", mudt_desc.getText().toString());
            mudObj.desc = mudt_desc.getText().toString();

            mudt.put("ClienteId", Singleton.getUSerObj().GUID);

            mudt.put("SGTipoUnidadId", unitObj.unit_id);
            mudObj.unitObj = unitObj;

            mudt.put("MudanzaDirCarLatLong", mudObj.carga_lat+","+mudObj.carga_lon);
            mudt.put("MudanzaDirDesLatLong", mudObj.des_lat+","+mudObj.des_lon);

            jsonObject.put("DatosMudanza", mudt);

            //Object[] objs = new Object[]{"RegistroMudanza", 15, this, jsonObject};
            Object[] objs = new Object[]{"PreRegistroMudanza", 15, this, jsonObject};
            ConnectToServer connectToServer = new ConnectToServer(objs);
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.dissmissLoad();
        }
    }

    public void getMudtResponse(String result) {
        Singleton.dissmissLoad();
        try {
            mudObj.response = result;
            JSONObject jsonObject = new JSONObject(result);
            JSONObject preReg = jsonObject.getJSONObject("PreRegistro");
            mudObj.MudanzaCosto = preReg.getString("MudanzaCosto");
            mudObj.MudanzaDistanciaAproximada = preReg.getString("MudanzaDistanciaAproximada");
            mudObj.MudanzaFechaAprox = preReg.getString("MudanzaFechaAprox");
            mudObj.MudanzaFechaTentativaDescarga = preReg.getString("MudanzaFechaTentativaDescarga");
            mudObj.MudanzaHoraTentativaDescarga = preReg.getString("MudanzaHoraTentativaDescarga");
            initMudDetail();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initMudDetail(){
        Bundle bundle = new Bundle();
        bundle.putInt("lay", lay);
        bundle.putSerializable("mudObj", mudObj);
        MudtDetailFragment mudtDetailFragment = new MudtDetailFragment();
        mudtDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(lay, mudtDetailFragment)
                .addToBackStack(this.getClass().getName())
                .commit();
    }

}
