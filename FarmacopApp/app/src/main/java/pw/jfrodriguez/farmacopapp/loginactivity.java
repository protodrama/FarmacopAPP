package pw.jfrodriguez.farmacopapp;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Credentials;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class loginactivity extends AppCompatActivity implements View.OnClickListener,listDialogFragment.NoticeDialogListener {

    ProgressDialog dialogo;
    EditText txtName,txtPass;
    EditText messageBoxText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadActivity();
    }

    public void LoadActivity(){
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });
        dialogo = new ProgressDialog(this);
        dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogo.setMessage("Comprobando credenciales");
        dialogo.setCancelable(false);
        txtName = (EditText)findViewById(R.id.txtName);
        txtPass = (EditText)findViewById(R.id.txtPass);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GenConf.ShowingRecPassDialog){
            MostrarAlertDialog();
        }
    }

    public void SaveUserAccount(String User, String Apikey){
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION,0);
        SharedPreferences.Editor mEditor = Preferences.edit();
        mEditor.putString(GenConf.ACCOUNT, User);
        mEditor.putString(GenConf.APIKEY,Apikey);
        mEditor.apply();
    }

    public void CloseActivity(){
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i = null;
        if (id == R.id.action_about) {
            i = new Intent(this,About.class);
            startActivity(i);
        }
        if (id == R.id.action_contact) {
            DialogFragment dialogo = new listDialogFragment();
            dialogo.show(getFragmentManager(),"Contacto");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEntrar:
                CheckLogin(txtName.getText().toString(), txtPass.getText().toString());
                break;
            case R.id.activeAccount:
                Intent acc = new Intent(this,ActiveAccount.class);
                startActivity(acc);
                break;
            case R.id.restartPass:
                MostrarAlertDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        if(GenConf.ShowingRecPassDialog)
            GenConf.MessageFromRecPassDialog = messageBoxText.getText().toString();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDialogUserSelect(DialogFragment dialog, int which) {
        switch (which){
            case 0:
                String email = getResources().getString(R.string.correo);
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Enviar email a " + email));
                break;
            case 1:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String telf = getResources().getString(R.string.telefono);
                intent.setData(Uri.parse("tel:" + telf));
                startActivity(intent);
                break;
        }
    }

    public void CheckLogin(String Nombre, String Password){
        try {
            final String NombreUsuario = Nombre;
            final String Pass = GenConf.MD5(Password);

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("cuenta", NombreUsuario);
            parametros.put("apikey", "eadmghacdg");

            cliente.get(this,GenConf.LogURL,parametros,new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    dialogo.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        dialogo.cancel();
                        ComprobarCredenciales(response.getJSONArray("data"),NombreUsuario,Pass);
                    } catch (JSONException e) {
                        MostrarAcceptDialog("Error al acceder a los datos de las credenciales");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dialogo.cancel();
                    MostrarAcceptDialog("Error al acceder a los datos de las credenciales");
                }
            });


        }
        catch (Exception e){
            MostrarAcceptDialog("Error al comprobar el usuario: " + e.getMessage());
        }
    }

    public void ComprobarCredenciales(JSONArray Usuario, String Nombre, String Password) throws JSONException {
        if(Usuario.length() > 0) {
            JSONObject datos = Usuario.getJSONObject(0);
            if(datos.getString("Cuenta").equals(Nombre) && datos.getString("Contrasena").equals(Password)){

                //OBTENER Y GUARDAR TODOS LOS DATOS DE LA CUENTA LOGUEADA Y OBTENER LOS DATOS DEL USUARIO
                SaveUserAccount(Nombre, datos.getString("APIKEY"));
                GetUserData(Nombre,datos.getString("APIKEY"));
                return;
            }
        }
        MostrarAcceptDialog("El usuario o la contraseña no son correctos");
    }

    public void CheckAccountToRestPassAndSend(String name) {
        try {
            final String NombreUsuario = name;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("cuenta", NombreUsuario);
            parametros.put("apikey", "eadmghacdg");

            dialogo.setMessage("Comprobando cuenta de usuario");

            cliente.get(this,GenConf.CheckUserAcURL,parametros,new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    dialogo.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        dialogo.cancel();
                        CheckData(response.getJSONArray("data"),NombreUsuario);
                    } catch (JSONException e) {
                        MostrarAcceptDialog("La cuenta indicada no es correcta");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dialogo.cancel();
                    MostrarAcceptDialog( "Error al comprobar la cuentas");
                }
            });


        }
        catch (Exception e){
            MostrarAcceptDialog("Error al comprobar el usuario");
        }

    }

    public void CheckData(JSONArray data,String name) throws JSONException {
        String username = data.getJSONObject(0).getString("Cuenta");
        if(username.equals(name))
            RestPassAndSend(name);
        else
            MostrarAcceptDialog("La cuenta indicada no es correcta");
    }

    public void RestPassAndSend(String name){
        try {
            final String NombreUsuario = name;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);
            Random rnd = new Random();
            Integer Low = 100000;
            Integer High = 999999;
            Integer Result = rnd.nextInt(High-Low) + Low;
            String temp = "" + Result;
            String codigo = GenConf.MD5(temp);

            RequestParams parametros = new RequestParams();
            parametros.put("cuenta", NombreUsuario);
            parametros.put("apikey", GenConf.DEFAPIKEY);
            parametros.put("codigo",Result);
            parametros.put("npass",codigo);

            dialogo.setMessage("Procesando...");

            cliente.get(this,GenConf.RestPassURL,parametros,new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    dialogo.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        dialogo.cancel();
                        if(response.getBoolean("code"))
                           MostrarAcceptDialog("Se ha enviado un correo a su cuenta de correo con la contraseña nueva. Puede tardar unos minutos.");
                    } catch (JSONException e) {
                        MostrarAcceptDialog("Error al generar la contraseña");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dialogo.cancel();
                    MostrarAcceptDialog("Error al generar la contraseña");
                }
            });


        }
        catch (Exception e){
            MostrarAcceptDialog("Error al generar la contraseña");
        }
    }

    public void GetUserData(String Nombre,String apikey){
        try {
            final String NombreUsuario = Nombre;
            final String Apikey = apikey;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("cuenta", NombreUsuario);
            parametros.put("apikey", Apikey);

            cliente.get(this,GenConf.UserDataURL,parametros,new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    dialogo.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        dialogo.cancel();
                        GetAllUserData(response.getJSONArray("data").getJSONObject(0));
                        StartMainActivity();
                    } catch (JSONException e) {
                        MostrarAcceptDialog("Error al acceder a los datos de la cuenta.");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dialogo.cancel();
                }
            });


        }
        catch (Exception e){

        }
    }

    public void GetAllUserData(JSONObject data) throws  JSONException{
        Sesion.NombreUsuario = data.getString("Cuenta");
        Sesion.Nombre = data.getString("Nombre");
        Sesion.Correo = data.getString("correo");
        Sesion.Apellido1 = data.getString("Apellido1");
        Sesion.Apellido2 = data.getString("Apellido2");
        Sesion.Apikey = data.getString("APIKEY");
        Sesion.FNac = data.getString("FechaNac");
        Sesion.Pass = data.getString("Contrasena");
    }

    public void StartMainActivity(){
        Intent princ = new Intent(this,Principal.class);
        startActivity(princ);
        this.finish();
    }

    public void MostrarAlertDialog(){
        GenConf.ShowingRecPassDialog = true;
        LayoutInflater layoutInflater = LayoutInflater.from(loginactivity.this);
        View promptView = layoutInflater.inflate(R.layout.restpass_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(loginactivity.this);
        alertDialogBuilder.setView(promptView);

        messageBoxText = (EditText) promptView.findViewById(R.id.edittext);
        messageBoxText.setText(GenConf.MessageFromRecPassDialog);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //COMPROBAR Y MANDAR CORREO CON LA NUEVA CONTRASEÑA
                        GenConf.ShowingRecPassDialog = false;
                        GenConf.MessageFromRecPassDialog = "";
                        CheckAccountToRestPassAndSend(messageBoxText.getText().toString());
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                GenConf.ShowingRecPassDialog = false;
                                GenConf.MessageFromRecPassDialog = "";
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void MostrarAcceptDialog(String message){
        LayoutInflater layoutInflater = LayoutInflater.from(loginactivity.this);
        View promptView = layoutInflater.inflate(R.layout.messagebox_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(loginactivity.this);
        alertDialogBuilder.setView(promptView);

        TextView textView = (TextView) promptView.findViewById(R.id.textViewtext);
        textView.setText(message);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
