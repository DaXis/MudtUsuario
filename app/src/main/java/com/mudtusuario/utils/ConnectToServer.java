package com.mudtusuario.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.mudtusuario.Singleton;
import com.mudtusuario.dialogs.FinishMudDialog;
import com.mudtusuario.dialogs.PagoDialog;
import com.mudtusuario.dialogs.RecoverDialog;
import com.mudtusuario.fragments.HistorialFragment;
import com.mudtusuario.fragments.InitMudFragment;
import com.mudtusuario.fragments.LoginFragment;
import com.mudtusuario.fragments.MainFragment;
import com.mudtusuario.fragments.MapFragment;
import com.mudtusuario.fragments.MudtDetailFragment;
import com.mudtusuario.fragments.PagosFragment;
import com.mudtusuario.fragments.ProcessFragment;
import com.mudtusuario.fragments.RegisterFragment;
import com.mudtusuario.fragments.SolicitudFragment;
import com.mudtusuario.fragments.ViajeDetailFragment;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyStore;

public class ConnectToServer {

    /**
     * @param args
     * 0) url
     * 1) int identify provider class
     * 2) provider class
     * 3) JSONObject
     */

    public ConnectToServer(Object[] args){
        new ConnectAsync().executeOnExecutor(Singleton.getsExecutor(), args);
    }

    private class ConnectAsync extends AsyncTask<Object[], String, String> {

        private Object[] aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.gc();
        }

        @Override
        protected String doInBackground(Object[]... params) {
            aux = params[0];
            String sUrl = (String)aux[0], result = "";
            sUrl = Singleton.getBaseUrl()+sUrl;
            JSONObject json = (JSONObject)aux[3];
            Log.d("ConnectToServer_URL --->", sUrl);

            //with timeout
            //HttpClient httpclient = new DefaultHttpClient(setTimeOut());

            //without timeout
            //HttpClient httpclient = new DefaultHttpClient();

            //with https
            HttpClient httpclient = getNewHttpClient();

            HttpPost httppost = new HttpPost(sUrl);

                try {
                    Log.d("json send", json.toString());
                    StringEntity se = new StringEntity(json.toString(), HTTP.UTF_8);

                    httppost.setEntity(se);
                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("Content-type", "application/json");
                    if(Singleton.getTokenObj() != null)
                        httppost.setHeader("Authorization", "OAuth "+Singleton.getTokenObj().access_token);
                    else
                        httppost.setHeader("Authorization", "OAuth ");
                    httppost.setHeader("GENEXUS-AGENT", "SmartDevice Application");

                    // Execute HTTP Post Request
                    org.apache.http.HttpResponse response = httpclient.execute(httppost);
                    result = getResponse(response);

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }

            return result;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ConnectToServer_onPostExecute "+aux[0], " "+result);

            decideMethod((int)aux[1], aux[2], result);

            System.gc();
        }
    }

    public ConnectToServer(Object[] args, boolean flag){
        new ConnectAsyncLogin().executeOnExecutor(Singleton.getsExecutor(), args);
    }

    private class ConnectAsyncLogin extends AsyncTask<Object[], String, String> {

        private Object[] aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.gc();
        }

        @Override
        protected String doInBackground(Object[]... params) {
            aux = params[0];
            String sUrl = (String)aux[0], result = "";
            //sUrl = Singleton.getBaseUrl()+sUrl;
            //JSONObject json = (JSONObject)aux[3];
            Log.d("ConnectToServer_URL --->", sUrl);

            HttpClient httpclient = getNewHttpClient();

            HttpPost httppost = new HttpPost(sUrl);

            try {
                //Log.d("json send", json.toString());
                String body = "client_id=75377bb5821d4717850ca0171d0376e8&granttype=password&scope=FullControl&username="
                        +aux[3]+"&password="+aux[4];
                Log.d("body send", body);
                StringEntity se = new StringEntity(body);

                httppost.setEntity(se);
                //httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/x-www-form-urlencoded");

                // Execute HTTP Post Request
                org.apache.http.HttpResponse response = httpclient.execute(httppost);
                result = getResponse(response);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            return result;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ConnectToServer_onPostExecute "+aux[0], " "+result);

            decideMethod((int)aux[1], aux[2], result);

            System.gc();
        }
    }

    public ConnectToServer(Object[] args, int flag){
        new UpAsync().executeOnExecutor(Singleton.getsExecutor(), args);
    }

    private class UpAsync extends AsyncTask<Object[], String, String> {

        private Object[] aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.gc();
        }

        @Override
        protected String doInBackground(Object[]... params) {
            aux = params[0];
            String sUrl = (String)aux[0], result = "";
            sUrl = Singleton.getImage_url()+sUrl;
            File file = (File)aux[3];
            Log.d("ConnectToServer_URL --->", sUrl);

            HttpClient httpclient = getNewHttpClient();
            HttpPost httppost = new HttpPost(sUrl);

            Log.d("file path ", file.getAbsolutePath());

            httppost.setHeader("Authorization", "OAuth "+Singleton.getTokenObj().access_token);
            httppost.setHeader("GENEXUS-AGENT", "SmartDevice Application");
            //httppost.setHeader("Content-Disposition", "form-data; name=imagen.jpg; filename=imagen.jpg");
            //FileEntity entity = new FileEntity(new FileBody(file, "image/jpeg").getFile(), "application/jpeg");
            FileEntity entity = new FileEntity(file, "image/jpeg; Content-Disposition: form-data; name=imagen.jpg; filename=imagen.jpg");

            for(int i = 0; i < httppost.getAllHeaders().length; i++){
                Log.d("httppost", httppost.getAllHeaders()[i].toString());
            }
            Log.d("entity", entity.getContentType().toString());

            httppost.setEntity(entity);

            try {
                org.apache.http.HttpResponse response = httpclient.execute(httppost);
                //result = ""+response.getStatusLine().getStatusCode();
                Log.d("pic response code", ""+response.getStatusLine().getStatusCode());
                result = ""+getResponse(response);
                Log.d("pic response", result);
            } catch (ClientProtocolException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ConnectToServer_onPostExecute "+aux[0]+" id: "+aux[1], " "+result);
            decideMethod((int)aux[1], aux[2], result);
            System.gc();
        }
    }

    //++++++++++++++++++
    public ConnectToServer(Object[] args, String flag){
        new ConnectDirAsync().executeOnExecutor(Singleton.getsExecutor(), args);
    }

    private class ConnectDirAsync extends AsyncTask<Object[], String, String> {

        private Object[] aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.gc();
        }

        @Override
        protected String doInBackground(Object[]... params) {
            aux = params[0];
            String sUrl = (String)aux[0], result = "";
            sUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+sUrl+"&sensor=true";
            //JSONObject json = (JSONObject)aux[3];
            Log.d("ConnectToServer_URL --->", sUrl);

            //with timeout
            //HttpClient httpclient = new DefaultHttpClient(setTimeOut());

            //without timeout
            //HttpClient httpclient = new DefaultHttpClient();

            //with https
            HttpClient httpclient = getNewHttpClient();

            HttpPost httppost = new HttpPost(sUrl);

            try {
                //Log.d("json send", json.toString());
                //StringEntity se = new StringEntity(json.toString());

                //httppost.setEntity(se);
                /*httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                if(Singleton.getTokenObj() != null)
                    httppost.setHeader("Authorization", "OAuth "+Singleton.getTokenObj().access_token);
                else
                    httppost.setHeader("Authorization", "OAuth ");
                httppost.setHeader("GENEXUS-AGENT", "SmartDevice Application");*/

                // Execute HTTP Post Request
                org.apache.http.HttpResponse response = httpclient.execute(httppost);
                result = getResponse(response);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            return result;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ConnectToServer_onPostExecute "+aux[0], " "+result);

            decideMethod((int)aux[1], aux[2], result);

            System.gc();
        }
    }
    //++++++++++++++++++

    //---------------------------
    public ConnectToServer(Object[] args, double flag){
        new ConnectLatLonAsync().executeOnExecutor(Singleton.getsExecutor(), args);
    }

    private class ConnectLatLonAsync extends AsyncTask<Object[], String, String> {

        private Object[] aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.gc();
        }

        @Override
        protected String doInBackground(Object[]... params) {
            aux = params[0];
            String sUrl = (String)aux[0], result = "";
            sUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="+sUrl+"&sensor=true_or_false";
            //sUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+sUrl+"&sensor=true";
            //JSONObject json = (JSONObject)aux[3];
            Log.d("ConnectToServer_URL --->", sUrl);

            HttpClient httpclient = getNewHttpClient();

            HttpPost httppost = new HttpPost(sUrl);

            try {
                org.apache.http.HttpResponse response = httpclient.execute(httppost);
                result = getResponse(response);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            return result;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ConnectToServer_onPostExecute "+aux[0], " "+result);
            decideMethod((int)aux[1], aux[2], result);
            System.gc();
        }
    }
    //---------------------------

    private String getResponse(org.apache.http.HttpResponse response){
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
            String line = null;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return "Error";
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        catch (Exception e) { e.printStackTrace(); }

        return sb.toString();
    }

    private HttpParams setTimeOut(){
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 60000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 60000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        return httpParameters;
    }

    public HttpClient getNewHttpClient(){
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            int timeoutConnection = 60000;
            HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
            int timeoutSocket = 60000;
            HttpConnectionParams.setSoTimeout(params, timeoutSocket);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return new DefaultHttpClient(setTimeOut());
        }
    }

    private void decideMethod(int i, Object o, String result) {
        switch(i){
            case 0:
                ((LoginFragment) o).getResponse(result);
                break;
            case 1:
                ((LoginFragment) o).getResponseData(result);
                break;
            case 2:
                ((MainFragment) o).getResponse(result);
                break;
            case 3:
                ((ViajeDetailFragment) o).getResponse(result);
                break;
            case 4:
                ((HistorialFragment) o).getResponse(result);
                break;
            case 5:
                ((SolicitudFragment) o).getResponse(result);
                break;
            case 6:
                ((ProcessFragment) o).getResponse(result);
                break;
            case 7:
                //((ProcessFragment) o).getPicResponse(result);
                break;
            case 8:
                ((FinishMudDialog) o).getResponse(result);
                break;
            case 9:
                ((RecoverDialog) o).getRecoverResponse(result);
                break;
            case 10:
                //((ViajeDetailFragment) o).getPagoResponse(result);
                break;
            case 11:
                //((ProcessFragment) o).getEndResponse(result);
                break;
            case 12:
                //((ViajeDetailFragment) o).getAceptResponse(result);
                break;
            case 13:
                ((RegisterFragment) o).getResponse(result);
                break;
            case 14:
                ((InitMudFragment) o).getResponse(result);
                break;
            case 15:
                ((InitMudFragment) o).getMudtResponse(result);
                break;
            case 16:
                ((MapFragment) o).getDirFromLtLn(result);
                break;
            case 17:
                ((MudtDetailFragment) o).getResponse(result);
                break;
            case 18:
                ((PagoDialog) o).getResponse(result);
                break;
            case 19:
                ((PagosFragment) o).getResponse(result);
                break;
            case 20:
                ((MapFragment) o).getLatLonResponse(result);
                break;
        }
    }

}