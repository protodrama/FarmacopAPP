package pw.jfrodriguez.farmacopapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Juanfran on 09/05/2016.
 */
public class backgroundService extends Service {

    public static Boolean Started = false;
    public static Integer Wait = 1;
    public static Integer Wait2 = 3;
    public static ArrayList<String> ListId = new ArrayList<>();
    public static ArrayList<String> PreviousListId = new ArrayList<>();
    public static AlarmManager myAlarmManager;
    public static PendingIntent pendingIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Started = true;

        Log.i("milog","servicio activo");
        new Thread(){
            @Override
            public void run() {
                super.run();
                Log.i("milog", "hilo activo");
                StartListeningMessages();
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                Log.i("milog", "hilo2 activo");
                StartListeningControl();
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

    public void StartListeningMessages(){
        Log.i("milog", "escuchando");
        while(true) {
            try {
                RetrieveSesionData(true);
                Thread.sleep(1000 * 60 * Wait);
                Log.i("milog", "se duerme");
            } catch (InterruptedException e) {
            }

        }
    }

    public void RetrieveSesionData(boolean isMSGListener){
        Log.i("milog", "extrayendo datos de cuenta");
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION,0);
        String account = Preferences.getString(GenConf.ACCOUNT, null);
        String apikey = Preferences.getString(GenConf.APIKEY,null);

        if(account != null && apikey != null) {
            if(isMSGListener)
                GetNotReadedMessages(account,apikey);
            else
                GetControlToday(account,apikey);
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

    public void StartListeningControl(){
        while(true) {
            try {
                RetrieveSesionData(false);
                Thread.sleep(1000 * 60 * (Wait2 / 2));
                Log.i("milog", "se duerme");
            } catch (InterruptedException e) {
            }

        }
    }

    public void GetControlToday(String Account, String Apikey){
        try {
            Log.i("milog", "obteniendo control");
            SyncHttpClient cliente = new SyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("cuenta", Account);
            parametros.put("apikey", Apikey);

            cliente.get(GenConf.GetControlsURL, parametros, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        if (response.getString("status").equals("200")) {
                            ProgramAlarm(response.getJSONArray("data"));
                        }
                    } catch (JSONException e) {
                        Log.i("milog", e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.i("milog", "error obteniendo control");
                }
            });
        }
        catch (Exception e){
            Log.i("milog", "error obteniendo control: " + e.getMessage());
        }
    }

    public void ProgramAlarm(JSONArray Control){
        Log.i("milog", " control");
        ArrayList<String> Data = new ArrayList<>();
        Integer Hour = 24;
        Integer Minute = 60;
        try{
            ArrayList<JSONObject> ListControlToday = GetControlToday(Control);
            Log.i("milog", " tamaño de lista " + ListControlToday.size());
            if(ListControlToday.size() > 0){
                for(int i = 0; i < ListControlToday.size(); i++){
                    if(ListControlToday.get(i).getInt("Hora") < Hour)
                        Hour = ListControlToday.get(i).getInt("Hora");
                    if(ListControlToday.get(i).getInt("Hora") == Hour)
                            if(ListControlToday.get(i).getInt("Minuto") < Minute)
                                Minute = ListControlToday.get(i).getInt("Minuto");
                }
                Log.i("milog", "Hora set: " + Hour + " Minuto set: " + Minute);

                for(int i = 0; i < ListControlToday.size(); i++){
                    if(ListControlToday.get(i).getInt("Hora") == Hour && ListControlToday.get(i).getInt("Minuto") == Minute)
                        Data.add(ListControlToday.get(i).getString("med") + " -- " + ListControlToday.get(i).getString("Dosis"));
                }

                if(Data.size() > 0){
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(calendar.HOUR_OF_DAY, Hour);
                        calendar.set(calendar.MINUTE, Minute);
                        calendar.set(calendar.SECOND, 20);
                        Intent alarmAct = new Intent(this, Alarm.class);
                        alarmAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        alarmAct.putStringArrayListExtra("Data", Data);

                        if (myAlarmManager == null)
                            myAlarmManager = ((AlarmManager) getSystemService(ALARM_SERVICE));
                        else {
                            if (pendingIntent != null)
                                myAlarmManager.cancel(pendingIntent);
                        }
                        pendingIntent = PendingIntent.getActivity(this, 0, alarmAct, PendingIntent.FLAG_ONE_SHOT);
                        myAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Log.i("milog", " Alarma programada");
                    }
                    catch (Exception e){
                        Log.i("milog", e.getMessage());
                    }
                }
                else{
                    Log.i("milog", "No hay data");
                }
            }
        }
        catch (Exception e){

        }
    }

    public ArrayList<JSONObject> GetControlToday(JSONArray Control) throws JSONException{
        Log.i("milog", "filtrando fechas control");
        ArrayList<JSONObject> Data = new ArrayList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar myDate = Calendar.getInstance();
            String DateNow = format.format(myDate.getTime());
            Log.i("milog", "fecha actual" + DateNow);
            for (int i = 0; i < Control.length(); i++) {
                String dtStart = Control.getJSONObject(i).getString("Fecha");
                String date = dtStart;
                if (date.equals(DateNow)) {
                    if (Control.getJSONObject(i).getInt("Hora") >= myDate.get(Calendar.HOUR_OF_DAY)){
                        if(Control.getJSONObject(i).getInt("Hora") > myDate.get(Calendar.HOUR_OF_DAY)) {
                            Data.add(Control.getJSONObject(i));
                        }
                        else{
                            if(Control.getJSONObject(i).getInt("Minuto") >= myDate.get(Calendar.MINUTE))
                                Data.add(Control.getJSONObject(i));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.i("milog", e.getMessage());
        }
        return Data;
    }
}
