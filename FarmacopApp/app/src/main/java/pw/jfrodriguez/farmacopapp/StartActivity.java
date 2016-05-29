package pw.jfrodriguez.farmacopapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Bundle extras = getIntent().getExtras();
        try{
            extras.getString(GenConf.SeeMessages);
            GenConf.OpenedToSeeMessages = true;
        }
        catch (Exception e){}
        RetrieveSesionData();
    }

    public void RetrieveSesionData(){
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION,0);
        String account = Preferences.getString(GenConf.ACCOUNT, null);
        String apikey = Preferences.getString(GenConf.APIKEY,null);

        if(account != null && apikey != null){
            GetUserData(account,apikey);
        }
        else {
            GenConf.OpenedToSeeMessages = false;
            StartLoginActivity();
        }
    }

    public void GetUserData(String Nombre,String apikey){
        try {
            final String UserName = Nombre;
            final String Apikey = apikey;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("apikey", Apikey);

            cliente.get(this,GenConf.UserDataURL,parametros,new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        GetAllUserData(response.getJSONArray("data").getJSONObject(0));
                        StartMainActivity();
                    } catch (JSONException e) {
                        StartLoginActivity();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    StartLoginActivity();
                }
            });
        }
        catch (Exception e){
            StartLoginActivity();
        }
    }

    public void GetAllUserData(JSONObject data) throws  JSONException{
        Session.UserName = data.getString("Cuenta");
        Session.Name = data.getString("Nombre");
        Session.Email = data.getString("correo");
        Session.FirstSur = data.getString("Apellido1");
        Session.SecondSur = data.getString("Apellido2");
        Session.Apikey = data.getString("APIKEY");
        Session.FNac = data.getString("FechaNac");
        Session.Pass = data.getString("Contrasena");
    }

    public void StartLoginActivity(){
        Intent princ = new Intent(this,loginactivity.class);
        startActivity(princ);
        this.finish();
    }

    public void StartMainActivity(){
        Intent princ = new Intent(this,Principal.class);
        startActivity(princ);
        this.finish();
    }

}
