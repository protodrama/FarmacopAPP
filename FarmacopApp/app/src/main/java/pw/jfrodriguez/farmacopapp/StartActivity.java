package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class StartActivity extends AppCompatActivity {

    boolean opentoseemessages = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Bundle extras = getIntent().getExtras();
        try{
            //Si recibe este extra significa que la aplicación ha sido abierta desde la notificación de mensajes
            extras.getString(GenConf.SeeMessages);
            opentoseemessages = true;
        }
        catch (Exception e){}
        RetrieveSesionData();
    }

    public void CloseActivity() {
        this.finish();
    }

    //Obtiene los datos de la sesión guardada
    public void RetrieveSesionData(){
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION,0);
        String account = Preferences.getString(GenConf.ACCOUNT, null);
        String apikey = Preferences.getString(GenConf.APIKEY, null);

        if(account != null && apikey != null){
            GetUserData(account,apikey);
        }
        else {
            StartLoginActivity();
        }
    }

    //Obtiene los datos del usuario de la sesion
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
                        Logout();
                        StartLoginActivity();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    ShowDialogAndClose("Error al conectar con el servidor. Inténtelo de nuevo o compruebe su conexión a internet.");
                    super.onFailure(statusCode, headers, throwable, errorResponse);
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

    //Borra la cuenta conectada en el caso de que falle la obtención de sus datos
    public void Logout(){
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION, 0);
        SharedPreferences.Editor mEditor = Preferences.edit();
        mEditor.putString(GenConf.ACCOUNT,null);
        mEditor.putString(GenConf.APIKEY, null);
        mEditor.apply();
    }

    //Lee los datos de la cuenta recibidos por el servidor
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

    //Abre la activity de login
    public void StartLoginActivity(){
        Intent princ = new Intent(this,loginactivity.class);
        startActivity(princ);
        this.finish();
    }

    //Abre la actividad del menú principal
    public void StartMainActivity(){
        Intent princ = new Intent(this,Principal_activity.class);
        if(opentoseemessages)
            princ.putExtra("tomessages","");
        startActivity(princ);
        this.finish();
    }

    //Muestra un diálogo y cierra el activity
    public void ShowDialogAndClose(String message){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.txtviewdialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final TextView editText = (TextView) promptView.findViewById(R.id.textData);
        editText.setText(message);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        CloseActivity();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
