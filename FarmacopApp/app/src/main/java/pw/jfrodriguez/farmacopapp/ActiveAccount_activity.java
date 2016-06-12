package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public class ActiveAccount_activity extends AppCompatActivity implements View.OnClickListener{

    ProgressDialog mdialog;
    EditText txtName,txtPass,txtPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });
        txtName = (EditText)findViewById(R.id.txtNameAct);
        txtPass = (EditText)findViewById(R.id.txtPassAct);
        txtPass2 = (EditText)findViewById(R.id.txtPassAct2);
        mdialog = new ProgressDialog(this);
        mdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mdialog.setMessage("Comprobando cuenta");
        mdialog.setCancelable(false);
    }

    public void CloseActivity(){
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnValidar:
                if(CheckTextBoxes())
                    if(GenConf.isNetworkAvailable(this)) {
                        CheckAccountName();
                    }
                    else{
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                        ShowDialogAndClose("Compruebe su conexión a internet.");
                    }
                break;
        }
    }

    //Comprueba que todos los datos han sido introducidos
    public Boolean CheckTextBoxes(){
        if(!txtName.getText().toString().trim().equals("") && !txtPass.getText().toString().trim().equals("") && !txtPass2.getText().toString().trim().equals("")){
            if(txtPass.getText().toString().equals(txtPass2.getText().toString())){
                return true;
            }
            else{
                GenConf.ShowMessageBox("Las contraseñas deben coincidir",this);
                return false;
            }
        }
        else{
            GenConf.ShowMessageBox("Se deben rellenar todos los datos",this);
            return false;
        }
    }

    //Comprueba que el nombre de cuenta seleccionado existe y es una cuenta inactiva
    public void CheckAccountName(){
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

            final String UserName = txtName.getText().toString();

            final AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(1, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("apikey", GenConf.DEFAPIKEY);

            cliente.get(this,GenConf.ValidationURL,parametros,new JsonHttpResponseHandler(){

                @Override
                public void onStart() {
                    mdialog.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        mdialog.cancel();
                        CheckData(response.getJSONArray("data"),UserName);
                    } catch (JSONException e) {
                        GenConf.ShowMessageBox("El nombre de cuenta no coincide con ninguna cuenta que no esté validada.",ActiveAccount_activity.this);
                        Log.i("milog", e.getMessage());
                    }
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                    super.onPreProcessResponse(instance, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al comprobar la cuenta.", ActiveAccount_activity.this);
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    mdialog.cancel();
                    ShowDialogAndClose("Error al conectar con el servidor. Inténtelo de nuevo o compruebe su conexión a internet.");
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFinish() {
                    mdialog.cancel();
                    super.onFinish();
                }

            });


        }
        catch (Exception e){
            GenConf.ShowMessageBox("Error al comprobar el usuario",this);
        }
    }

    public void CheckData(JSONArray data,String name) throws JSONException {
        String username = data.getJSONObject(0).getString("Cuenta");
        if(username.equals(name))
            ActiveAccount();
        else
            GenConf.ShowMessageBox("La cuenta indicada no es correcta",this);
    }

    //Activa la cuenta indicada
    public void ActiveAccount(){
        try {
            final String UserName = txtName.getText().toString();
            final String Password = GenConf.MD5(txtPass.getText().toString());
            mdialog.setMessage("Activando cuenta");

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("password", Password);
            parametros.put("apikey", "eadmghacdg");

            cliente.put(this, GenConf.ValidateURL, parametros, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    mdialog.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        mdialog.cancel();
                        int status = response.getInt("status");
                        if(status == 200)
                            ShowDialogAndClose("Cuenta activada con éxito");
                        else
                            throw new Exception("");
                    } catch (Exception e) {
                        GenConf.ShowMessageBox("Error al activar cuenta.",ActiveAccount_activity.this);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al activar cuenta.", ActiveAccount_activity.this);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    mdialog.cancel();
                    ShowDialogAndClose("Error al conectar con el servidor. Inténtelo de nuevo o compruebe su conexión a internet.");
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFinish() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    mdialog.cancel();
                    super.onFinish();
                }
            });


        }
        catch (Exception e){
            GenConf.ShowMessageBox("Error al activar cuenta.",this);
        }
    }


    //Muestra un diálogo y cierra el activity
    public void ShowDialogAndClose(String message){
        LayoutInflater layoutInflater = LayoutInflater.from(ActiveAccount_activity.this);
        View promptView = layoutInflater.inflate(R.layout.txtviewdialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActiveAccount_activity.this);
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
