package com.mudtusuario;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mudtusuario.objs.TokenObj;
import com.mudtusuario.objs.UserObj;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private final int DURACION_SPLASH = 3000; // 3 segundos
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12, MY_WRITE_EXTERNAL_STORAGE = 13, MY_READ_EXTERNAL_STORAGE = 14,
            MY_READ_PHONE_STATE = 15;
    private LinearLayout splash_login_lay;
    private Button login_btn, reg_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        splash_login_lay = (LinearLayout)findViewById(R.id.splash_login_lay);
        login_btn = (Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        reg_btn = (Button)findViewById(R.id.reg_btn);
        reg_btn.setOnClickListener(this);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M)
            checkGPSPermission();
        else
            onUIThread();
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkWritePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_WRITE_EXTERNAL_STORAGE);
            return;
        } else {
            checkReadPermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkReadPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_READ_EXTERNAL_STORAGE);
            return;
        } else {
            //onUIThread();
            checkReadPhonePermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkGPSPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
            return;
        } else {
            Singleton.getGpsConfig().configuracionLocationManager();
            checkWritePermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkReadPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    MY_READ_PHONE_STATE);
            return;
        } else {
            onUIThread();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Singleton.getGpsConfig().configuracionLocationManager();
                    checkWritePermission();
                }
                break;
            case MY_WRITE_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkReadPermission();
                }
                break;
            case MY_READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkReadPhonePermission();
                }
                break;
            case MY_READ_PHONE_STATE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onUIThread();
                }
                break;
        }
    }

    private void mainIntent(boolean arg){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("arg", arg);
        startActivity(intent);
        finish();
    }

    private void onUIThread(){
        if(Singleton.getSettings().getBoolean("login_flag", false)){
            parseLogin();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            splash_login_lay.setVisibility(View.VISIBLE);
                        }

                        ;
                    }, DURACION_SPLASH);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.login_btn:
                mainIntent(true);
                break;
            case R.id.reg_btn:
                mainIntent(false);
                break;
        }
    }

    private void parseLogin(){
        try {
            JSONObject jsonObject = new JSONObject(Singleton.getSettings().getString("token_json", ""));
            TokenObj tokenObj = new TokenObj();
            tokenObj.access_token = jsonObject.getString("access_token");
            tokenObj.scope = jsonObject.getString("scope");
            tokenObj.refresh_token = jsonObject.getString("refresh_token");
            tokenObj.user_guid = jsonObject.getString("user_guid").replace("    ","");
            Singleton.setTokenObj(tokenObj);
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.dissmissLoad();
        }
        try {
            JSONObject jsonObject = new JSONObject(Singleton.getSettings().getString("login_json", ""));
            if(jsonObject.has("SDTRestLogin")){
                JSONObject SDTRestLogin = jsonObject.getJSONObject("SDTRestLogin");
                UserObj userObj = new UserObj();
                userObj.Error = SDTRestLogin.getString("Error");
                userObj.Foto = SDTRestLogin.getString("Foto");
                userObj.GUID = SDTRestLogin.getString("GUID");
                userObj.NombreCompleto = SDTRestLogin.getString("NombreCompleto");
                userObj.Rol = SDTRestLogin.getString("Rol");
                Singleton.setUserObj(userObj);
                mainIntent(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.dissmissLoad();
        }
    }

}