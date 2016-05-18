package pw.jfrodriguez.farmacopapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Alarm extends AppCompatActivity implements View.OnClickListener{

    MediaPlayer mp;
    String UserName,Hour,Minute,Apikey,Date;
    ArrayList<String> Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView mListView = (ListView)findViewById(R.id.mListView);
        try {
            Data = getIntent().getExtras().getStringArrayList("Data");
            UserName = getIntent().getExtras().getString("Username");
            Hour = getIntent().getExtras().getString("Hour");
            Minute = getIntent().getExtras().getString("Minute");
            Date = getIntent().getExtras().getString("Date");
        }
        catch (Exception ex){
            CloseActivity();
        }

        if(RetrieveSesionData()) {
            if (Data != null)
                mListView.setAdapter(new mAdapter(this, R.layout.controltext, Data, this.getLayoutInflater()));

            try {
                Uri uri = Uri.parse("android.resource://pw.jfrodriguez.farmacopapp/" + R.raw.alarm_sound);
                AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_ALARM);
                mp.setDataSource(this, uri);
                mp.setLooping(true);
                mp.prepare();
                mp.start();
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
            } catch (Exception e) {
                CloseActivity();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    public boolean RetrieveSesionData(){
        Log.i("milog", "extrayendo datos de cuenta");
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION,0);
        String account = Preferences.getString(GenConf.ACCOUNT, null);
        String apikey = Preferences.getString(GenConf.APIKEY, null);

        if(account != null && apikey != null) {
            if(account.equals(UserName)){
                Apikey = apikey;
                return true;
            }
            return false;
        }
        else{
            CloseActivity();
        }

        return false;
    }

    public void CloseActivity(){
        this.finish();
    }

    @Override
    public void onBackPressed() {
        //nothing
    }

    @Override
    protected void onDestroy() {
        //Tambien llamar a actualizar toma
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        mp.stop();
        UpdateAllControl();
    }

    public void UpdateAllControl(){
        try {
            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("apikey", Apikey);
            parametros.put("date", Date);
            parametros.put("hour", Hour);
            parametros.put("minute", Minute);

            cliente.put(this, GenConf.UpdateControlsURL, parametros, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    CloseActivity();
                }
            });
        }
        catch (Exception e){

        }
    }
}
