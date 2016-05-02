package pw.jfrodriguez.farmacopapp;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class loginactivity extends AppCompatActivity implements View.OnClickListener,listDialogFragment.NoticeDialogListener {

    ProgressDialog dialogo;
    EditText txtName,txtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                CheckLogin(txtName.getText().toString(),txtPass.getText().toString());
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
                        GenConf.MostrarToast(loginactivity.this,"Error al acceder a los datos de las credenciales");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dialogo.cancel();
                    GenConf.MostrarToast(loginactivity.this, "Error al acceder a los datos de las credenciales");
                }
            });


        }
        catch (Exception e){
            GenConf.MostrarToast(this,"Error al comprobar el usuario: " + e.getMessage());
        }
    }

    public void ComprobarCredenciales(JSONArray Usuario, String Nombre, String Password) throws JSONException {
        if(Usuario.length() > 0) {
            JSONObject datos = Usuario.getJSONObject(0);
            if(datos.getString("Cuenta").equals(Nombre) && datos.getString("Contrasena").equals(Password)){
                Intent princ = new Intent(this,Principal.class);
                //OBTENER Y GUARDAR TODOS LOS DATOS DE LA CUENTA LOGUEADA Y OBTENER LOS DATOS DEL USUARIO
                startActivity(princ);
                this.finish();
                return;
            }
        }
        GenConf.MostrarToast(loginactivity.this,"El usuario o la contraseña no son correctos");
    }

    public void CheckAccountToRestPassAndSend(){

    }

    public void MostrarAlertDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(loginactivity.this);
        View promptView = layoutInflater.inflate(R.layout.restpass_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(loginactivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //COMPROBAR Y MANDAR CORREO CON LA NUEVA CONTRASEÑA
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
