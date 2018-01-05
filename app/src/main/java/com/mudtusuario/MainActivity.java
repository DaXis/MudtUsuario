package com.mudtusuario;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mudtusuario.fragments.AboutFragment;
import com.mudtusuario.fragments.HistorialFragment;
import com.mudtusuario.fragments.InitMudFragment;
import com.mudtusuario.fragments.LoginFragment;
import com.mudtusuario.fragments.MainFragment;
import com.mudtusuario.fragments.MapFragment;
import com.mudtusuario.fragments.PagosFragment;
import com.mudtusuario.fragments.ProcessFragment;
import com.mudtusuario.fragments.RegisterFragment;
import com.mudtusuario.fragments.SolicitudFragment;
import com.mudtusuario.fragments.ViajeDetailFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DrawerLayout drawerLayout;
    private FrameLayout contenLay;
    private LoginFragment loginFragment;
    private MainFragment mainFragment;
    private HistorialFragment historialFragment;
    private SolicitudFragment solicitudFragment;
    private AboutFragment aboutFragment;
    private ImageView userPic;
    private TextView userName;
    private RegisterFragment registerFragment;
    //private MapFragment mapFragment;
    private PagosFragment pagosFragment;

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Singleton.setMainActivity(this);

        setToolbar();
        initView();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tool = inflater.inflate(R.layout.actionbar, null);
        toolbar.addView(tool);

        ImageView expanded_menu = (ImageView)tool.findViewById(R.id.expanded_menu);
        expanded_menu.setOnClickListener(this);
        Singleton.setMenuBtn(expanded_menu);

        TextView appText = (TextView)tool.findViewById(R.id.appText);
        Singleton.setActionText(appText);

        ImageView actionBtn = (ImageView)tool.findViewById(R.id.actionBtn);
        Singleton.setActionButon(actionBtn);
        actionBtn.setOnClickListener(this);

        ProgressBar actionProgress = (ProgressBar)tool.findViewById(R.id.actionProgress);
        Singleton.setActionProgress(actionProgress);

        TextView calif = (TextView)tool.findViewById(R.id.calif);
        calif.setOnClickListener(this);
        Singleton.setCalifText(calif);

        ImageView tool_logo = (ImageView)findViewById(R.id.tool_logo);
        Singleton.setToolLogo(tool_logo);
    }

    private void initView(){
        initFragments();

        contenLay = (FrameLayout)findViewById(R.id.mainContent);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Singleton.setDrawerLayout(drawerLayout);

        //cambiar a true si se pasa si login, false si se sigue el flujo normal
        if(Singleton.getSettings().getBoolean("login_flag", false)) {
            initMapFragment();
        } else {
            if(getIntent().getBooleanExtra("arg", true))
                initLoginFragment();
            else
                initRegisterFragment();
        }
    }

    private void initFragments() {
        loginFragment = new LoginFragment();
        mainFragment = new MainFragment();
        historialFragment = new HistorialFragment();
        solicitudFragment = new SolicitudFragment();
        aboutFragment = new AboutFragment();
        registerFragment = new RegisterFragment();
        //mapFragment = new MapFragment();
        pagosFragment = new PagosFragment();
    }

    private void removeFragments(){
        if(Singleton.getCurrentFragment() != null){
            Log.d("fragment remove", Singleton.getCurrentFragment().getClass().toString());

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(Singleton.getCurrentFragment()).commit();
        }
    }

    private void initLoginFragment(){
        if(Singleton.getCurrentFragment() != loginFragment){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.left_drawer));
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", contenLay.getId());
            if(loginFragment.getArguments() == null)
                loginFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(contenLay.getId(), loginFragment).commit();
        }
    }

    private void initRegisterFragment(){
        if(Singleton.getCurrentFragment() != registerFragment){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.left_drawer));
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", contenLay.getId());
            if(registerFragment.getArguments() == null)
                registerFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(contenLay.getId(), registerFragment).commit();
        }
    }

    public void initMainFragment(){
        if(Singleton.getCurrentFragment() != mainFragment){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.left_drawer));
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", contenLay.getId());
            bundle.putString("title", getResources().getString(R.string.next_mudt));
            if(mainFragment.getArguments() == null)
                mainFragment.setArguments(bundle);
            else
                mainFragment.getArguments().putString("title", getResources().getString(R.string.next_mudt));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(contenLay.getId(), mainFragment).commit();
        }
    }

    public void initMapFragment(){
        initUser();
        if(Singleton.getCurrentFragment() == null)
            gotoMap();
        else if(Singleton.getCurrentFragment().getClass() != MapFragment.class){
            gotoMap();
        }
    }

    private void gotoMap(){
        MapFragment mapFragment = new MapFragment();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.left_drawer));
        removeFragments();
        Bundle bundle = new Bundle();
        bundle.putInt("lay", contenLay.getId());
        bundle.putString("title", getResources().getString(R.string.next_mudt));
        if(mapFragment.getArguments() == null)
            mapFragment.setArguments(bundle);
        else
            mapFragment.getArguments().putString("title", getResources().getString(R.string.next_mudt));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(contenLay.getId(), mapFragment).commit();
    }

    public void initHistoryFragment(){
        if(Singleton.getCurrentFragment() != historialFragment){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.left_drawer));
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", contenLay.getId());
            bundle.putString("title", getResources().getString(R.string.hist_muds));
            if(historialFragment.getArguments() == null)
                historialFragment.setArguments(bundle);
            else
                historialFragment.getArguments().putString("title", getResources().getString(R.string.hist_muds));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(contenLay.getId(), historialFragment).commit();
        }
    }

    public void initAboutFragment(){
        if(Singleton.getCurrentFragment() != aboutFragment){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.left_drawer));
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", contenLay.getId());
            if(aboutFragment.getArguments() == null)
                aboutFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(contenLay.getId(), aboutFragment).commit();
        }
    }

    public void initPagosFragment(){
        if(Singleton.getCurrentFragment() != pagosFragment){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.left_drawer));
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", contenLay.getId());
            if(pagosFragment.getArguments() == null)
                pagosFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(contenLay.getId(), pagosFragment).commit();
        }
    }

    private void openCloseDrawer(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.expanded_menu:
                if(Singleton.getCurrentFragment().getClass() == MainFragment.class)
                    openCloseDrawer();
                else if(Singleton.getCurrentFragment().getClass() == MapFragment.class)
                    openCloseDrawer();
                else if(Singleton.getCurrentFragment().getClass() == ViajeDetailFragment.class)
                    onBackPressed();
                else if(Singleton.getCurrentFragment().getClass() == ProcessFragment.class)
                    onBackPressed();
                else if(Singleton.getCurrentFragment().getClass() == HistorialFragment.class)
                    onBackPressed();
                else if(Singleton.getCurrentFragment().getClass() == SolicitudFragment.class)
                    onBackPressed();
                else if(Singleton.getCurrentFragment().getClass() == AboutFragment.class)
                    onBackPressed();
                else if(Singleton.getCurrentFragment().getClass() == InitMudFragment.class)
                    onBackPressed();
                else if(Singleton.getCurrentFragment().getClass() == PagosFragment.class)
                    onBackPressed();
                break;
            case R.id.menu_solis:
                openCloseDrawer();
                initMapFragment();
            break;
            case R.id.menu_mi_mud:
                openCloseDrawer();
                initHistoryFragment();
            break;
            case R.id.menu_pago:
                openCloseDrawer();
                initPagosFragment();
            break;
            case R.id.menu_guia_mud:
                openCloseDrawer();
            break;
            case R.id.menu_about:
                openCloseDrawer();
                initAboutFragment();
            break;
            case R.id.menu_close:
                openCloseDrawer();
                Singleton.saveSettings("login_json", "");
                Singleton.saveSettings("login_flag", false);
                mainIntent();
            break;
        }
    }

    private void initUser(){
        userName = (TextView)findViewById(R.id.name);
        userName.setText(Singleton.getUSerObj().NombreCompleto);

        ProgressBar progressBar = new ProgressBar(this);
        userPic = (ImageView)findViewById(R.id.user_pic);
        Singleton.loadImage(Singleton.getUSerObj().Foto, userPic, progressBar);

        LinearLayout menu_prox = (LinearLayout)findViewById(R.id.menu_solis);
        menu_prox.setOnClickListener(this);
        LinearLayout menu_his = (LinearLayout)findViewById(R.id.menu_mi_mud);
        menu_his.setOnClickListener(this);
        LinearLayout menu_sol = (LinearLayout)findViewById(R.id.menu_pago);
        menu_sol.setOnClickListener(this);
        /*LinearLayout menu_guia = (LinearLayout)findViewById(R.id.menu_guia);
        menu_guia.setOnClickListener(this);*/
        LinearLayout menu_about = (LinearLayout)findViewById(R.id.menu_about);
        menu_about.setOnClickListener(this);
        LinearLayout menu_close = (LinearLayout)findViewById(R.id.menu_close);
        menu_close.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        if(Singleton.getCurrentFragment().getClass() == HistorialFragment.class)
            initMapFragment();
        else if(Singleton.getCurrentFragment().getClass() == SolicitudFragment.class)
            initMapFragment();
        else if(Singleton.getCurrentFragment().getClass() == AboutFragment.class)
            initMapFragment();
        else if(Singleton.getCurrentFragment().getClass() == ViajeDetailFragment.class)
            super.onBackPressed();
        else if(Singleton.getCurrentFragment().getClass() == RegisterFragment.class)
            initLoginFragment();
        else if(Singleton.getCurrentFragment().getClass() == PagosFragment.class)
            initMapFragment();
        else
            super.onBackPressed();
    }

    private void mainIntent(){
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
