package com.mudtusuario;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mudtusuario.dialogs.CustomDialog;
import com.mudtusuario.dialogs.LoadDialog;
import com.mudtusuario.objs.ActiveObj;
import com.mudtusuario.objs.TokenObj;
import com.mudtusuario.objs.UserObj;
import com.mudtusuario.utils.GpsConfiguration;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.nostra13.universalimageloader.core.assist.FailReason.FailType.IO_ERROR;
import static com.nostra13.universalimageloader.core.assist.FailReason.FailType.OUT_OF_MEMORY;

public class Singleton extends Application implements GpsConfiguration.OnGpsLocationListener {

    private static final Object TAG = Singleton.class.getName();
    private static Singleton m_Instance;

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    private static File cache;

    private static TextView actionText, califText;
    private static ImageView actionButon, menuBtn;
    private static ProgressBar actionProgress;
    private static DrawerLayout drawerLayout;
    private static Fragment fragment;

    private static ImageLoaderConfiguration config;
    private static DisplayImageOptions defaultOptions, defaultOptionsUnit;
    private static String baseUrl, image_url;
    private static LoadDialog load;
    private static TokenObj tokenObj;
    private static UserObj userObj;
    private static MainActivity activity;
    private static GpsConfiguration gps;
    private static double latitude, longitude;
    private static ActiveObj isActive;
    private static int currentapiVersion;
    private static ImageView tool_logo;
    public static final long dayLong = 86400000;
    public static final long hourLong = 3600000;
    public static final long minLong = 60000;
    private static CustomDialog custoDialog;

    //----------------------
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int KEEP_ALIVE = 10;

    private static View mapView;

    private static final BlockingQueue<Runnable> sWorkQueue =
            new LinkedBlockingQueue<Runnable>(KEEP_ALIVE);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "TrackingUserTask #" + mCount.getAndIncrement());
        }
    };

    private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);
    //----------------------

    public static Singleton getInstance() {
        if(m_Instance == null) {
            synchronized(Singleton.class) {
                if(m_Instance == null) new Singleton();
            }
        }
        return m_Instance;
    }

    public void onCreate() {
        super.onCreate();
        currentapiVersion = Build.VERSION.SDK_INT;
        baseUrl = getResources().getString(R.string.base_url);
        image_url = getResources().getString(R.string.image_url);
        initPreferences();
        initImageLoader(this);
        initImageLoaderUnit(this);
        genCacheDataCarpet();
        initGPSConfig(this);
    }

    private static void initGPSConfig(Context context){
        gps = new GpsConfiguration(context, false);
    }

    private static void genCacheDataCarpet(){
        if(cache == null)
            cache = new File(Environment.getExternalStorageDirectory(), "mudit");
        if (!cache.exists()) {
            cache.mkdirs();
        }
    }

    public static File getCacheCarpet(){
        return cache;
    }

    private void initPreferences(){
        if(settings == null)
            settings = getSharedPreferences("prefs_pesca", Context.MODE_PRIVATE);
        if(editor == null)
            editor = settings.edit();
    }

    public static SharedPreferences getSettings(){
        return settings;
    }

    public static SharedPreferences.Editor getEditor(){
        return editor;
    }

    public static int dpTpPx(Context context, int dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, int px){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static void setActionText(TextView arg){
        actionText = arg;
    }

    public static TextView getActionText(){
        return actionText;
    }

    public static void setActionButon(ImageView arg){
        actionButon = arg;
    }

    public static ImageView getActionButon(){
        return actionButon;
    }

    public static void setActionProgress(ProgressBar arg){
        actionProgress = arg;
    }

    public static ProgressBar getActionProgress(){
        return actionProgress;
    }

    public static void setCalifText(TextView arg){
        califText = arg;
    }

    public static TextView getCalifText(){
        return califText;
    }

    public static void setDrawerLayout(DrawerLayout arg){
        drawerLayout = arg;
    }

    public static DrawerLayout getDrawerLayout(){
        return drawerLayout;
    }

    public static void setCurrentFragment(Fragment arg){
        fragment = arg;
    }

    public static Fragment getCurrentFragment(){
        return fragment;
    }

    private static void initImageLoader(Context context) {
        if(defaultOptions == null)
            defaultOptions = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.avatar_empty_a)
                    .showImageOnFail(R.drawable.avatar_empty_a)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();


        config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(150 * 1024 * 1024) // 150 Mb
                .memoryCacheExtraOptions(480, 800)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .threadPoolSize(10)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions getDefaultOptions(){
        return defaultOptions;
    }

    public static void loadImage(final String url, ImageView imageView, final ProgressBar load){
        ImageLoader.getInstance().displayImage(url, imageView, defaultOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Error de entrada o de salida";
                        break;
                    case DECODING_ERROR:
                        message = "La imagen no pudo ser decodificada";
                        break;
                    case NETWORK_DENIED:
                        message = "La descarga fue denegada";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Error desconocido";
                        break;
                }
                Log.i(url, message);
                load.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                load.setVisibility(android.view.View.GONE);
            }
        });
    }

    private static void initImageLoaderUnit(Context context) {
        if(defaultOptionsUnit == null)
            defaultOptionsUnit = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.ic_mx2_on)
                    .showImageOnFail(R.drawable.ic_mx2_on)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();


        config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(150 * 1024 * 1024) // 150 Mb
                .memoryCacheExtraOptions(480, 800)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .threadPoolSize(10)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public static void loadUnitImage(final String url, ImageView imageView, final ProgressBar load){
        ImageLoader.getInstance().displayImage(url, imageView, defaultOptionsUnit, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Error de entrada o de salida";
                        break;
                    case DECODING_ERROR:
                        message = "La imagen no pudo ser decodificada";
                        break;
                    case NETWORK_DENIED:
                        message = "La descarga fue denegada";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Error desconocido";
                        break;
                }
                Log.i(url, message);
                load.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                load.setVisibility(android.view.View.GONE);
            }
        });
    }

    public static void clearImageCache(String url){
        MemoryCacheUtils.removeFromCache(url, ImageLoader.getInstance().getMemoryCache());
        DiskCacheUtils.removeFromCache(url, ImageLoader.getInstance().getDiskCache());
    }

    public static void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static ThreadPoolExecutor getsExecutor(){
        return sExecutor;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getImage_url(){
        return image_url;
    }

    public static LoadDialog showLoadDialog(FragmentManager manager){
        if(load == null){
            synchronized (Singleton.class){
                if(load == null){
                    load = LoadDialog.newInstance();
                    load.setCancelable(false);
                    try{
                        load.show(manager, "load dialog");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return load;
    }

    public static void dissmissLoad(){
        if(load != null) {
            try{
                load.dismiss();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            load = null;
        }
    }

    public static void setTokenObj(TokenObj arg){
        tokenObj = arg;
    }

    public static TokenObj getTokenObj(){
        return tokenObj;
    }

    public static void setUserObj(UserObj arg){
        userObj = arg;
    }

    public static UserObj getUSerObj(){
        return userObj;
    }

    public static void setMainActivity(MainActivity arg){
        activity = arg;
    }

    public static MainActivity getMainActivity(){
        return activity;
    }

    public static void setMenuBtn(ImageView arg){
        menuBtn = arg;
    }

    public static ImageView getMenuBtn(){
        return menuBtn;
    }

    public static GpsConfiguration getGpsConfig(){
        return gps;
    }

    public static double getLatitud(){
        return latitude;
    }

    public static double getLongitude(){
        return longitude;
    }

    @Override
    public void onGpsLocationInteraction(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public static void setIsActive(ActiveObj arg){
        isActive = arg;
    }

    public static ActiveObj getIsActive(){
        return isActive;
    }

    public static TimeZone getCurrentTZ(){
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        return tz;
    }

    public static void saveSettings(String arg0, String arg1){
        editor.putString(arg0, arg1);
        editor.commit();
    }

    public static void saveSettings(String arg0, int arg1){
        editor.putInt(arg0, arg1);
        editor.commit();
    }

    public static void saveSettings(String arg0, boolean arg1){
        editor.putBoolean(arg0, arg1);
        editor.commit();
    }

    public static int getCurrentApiVersion(){
        return currentapiVersion;
    }

    public static void setToolLogo(ImageView arg){
        tool_logo = arg;
    }

    public static ImageView getToolLogo(){
        return tool_logo;
    }

    public static void genToast(final Activity activity, final String msn){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "" + msn, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void setMapView(View arg){
        mapView = arg;
    }

    public static View getMapView(){
        return mapView;
    }

    public static CustomDialog showCustomDialog(FragmentManager manager, String title, String body, String action, int actionId){
        if(custoDialog == null){
            synchronized (Singleton.class){
                if(custoDialog == null){
                    custoDialog = CustomDialog.newInstance(title, body, action, actionId);
                    custoDialog.setCancelable(false);
                    try{
                        custoDialog.show(manager, "custom dialog");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return custoDialog;
    }

    public static void dissmissCustom(){
        try {
            if(custoDialog != null) {
                try{
                    custoDialog.dismiss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                custoDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
