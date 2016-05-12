package pw.jfrodriguez.farmacopapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;


/**
 * Created by Juanfran on 09/05/2016.
 */
public class backgroundService extends Service {

    public static Boolean Started = false;
    public static Integer Wait = 1;
    public static ArrayList<String> ListId = new ArrayList<>();
    public static ArrayList<String> PreviousListId = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Started = true;
        Log.i("milog","servicio activo");
        new Thread(){
            @Override
            public void run() {
                super.run();
                Log.i("milog", "hilo activo");
                startListenint();
            }
        }.start();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startListenint(){
        Log.i("milog", "escuchando");
        while(true) {
            try {
                RetrieveSesionData();
                Thread.sleep(1000 * 60 * Wait);
                Log.i("milog", "se duerme");
            } catch (InterruptedException e) {
            }

        }
    }

    public void RetrieveSesionData(){
        Log.i("milog", "extrayendo datos de cuenta");
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION,0);
        String account = Preferences.getString(GenConf.ACCOUNT, null);
        String apikey = Preferences.getString(GenConf.APIKEY,null);

        if(account != null && apikey != null) {
            GetNotReadedMessages(account,apikey);
        }
    }

    private void GetNotReadedMessages(String Account, String Apikey) {
        try {
            Log.i("milog", "obteniendo mensajes");
            SyncHttpClient cliente = new SyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("cuenta", Account);
            parametros.put("apikey", Apikey);

            cliente.get(GenConf.ServiceNotReadedMessagesURL, parametros, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        if (response.getString("status").equals("200")) {
                            CheckMessages(response.getJSONArray("data"));
                        }
                    } catch (JSONException e) {
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.i("milog", "error obteniendo mensajes");
                }
            });
        }
        catch (Exception e){
            Log.i("milog", "error obteniendo mensajes: " + e.getMessage());
        }
    }

    public void CheckMessages(JSONArray ListMensajes){
        Log.i("milog", "chequeando mensajes");
        boolean showmessage = false;
        try{
            PreviousListId.clear();
            PreviousListId.addAll(ListId);
            ListId.clear();

            if(ListMensajes.length() > 0) {
                for (int i = 0; i < ListMensajes.length(); i++){
                    ListId.add(ListMensajes.getJSONObject(i).getString("ID"));
                    if (!PreviousListId.contains(ListMensajes.getJSONObject(i).getString("ID"))){
                        showmessage = true;
                    }
                }

                if(showmessage && ListMensajes.length() > 0)
                    showMessage(ListMensajes.length());

            }

        }
        catch (Exception e){

        }
    }

    public void showMessage(int num){
        Log.i("milog", "notificacion!");
        try {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_appicon)
                                .setContentTitle("Mensajes")
                                .setContentText("Tienes " + num + " mensajes sin leer.")
                                .setAutoCancel(true);

                int NOTIFICATION_ID = 12345;

                Intent targetIntent = new Intent(this, StartActivity.class);
                targetIntent.putExtra(GenConf.SeeMessages,"");
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);
                NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification TheNotification = builder.build();
                TheNotification.defaults |= Notification.DEFAULT_VIBRATE;
                TheNotification.defaults |= Notification.DEFAULT_SOUND;
                nManager.notify(NOTIFICATION_ID, TheNotification);

        }
        catch (Exception ex){
            Log.i("milog",ex.getMessage());
        }
    }
}
