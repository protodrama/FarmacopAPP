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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ActiveAccount extends AppCompatActivity implements View.OnClickListener{

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
                    CheckAccountName();
                break;
        }
    }

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

    public void CheckAccountName(){
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

            final String UserName = txtName.getText().toString();

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

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
                    super.onSuccess(statusCode, headers, response);
                    try {
                        mdialog.cancel();
                        CheckData(response.getJSONArray("data"),UserName);
                    } catch (JSONException e) {
                        GenConf.ShowMessageBox("El nombre de cuenta no coincide con ninguna cuenta que no esté validada.",ActiveAccount.this);
                        Log.i("milog", e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al comprobar la cuenta. Compruebe su conexión.",ActiveAccount.this);
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

    public void ActiveAccount(){
        try {
            final String UserName = txtName.getText().toString();
            final String Contrasena = GenConf.MD5(txtPass.getText().toString());
            mdialog.setMessage("Activando cuenta");

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("password", Contrasena);
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
                            ShowDialog();
                        else
                            throw new JSONException("");
                    } catch (JSONException e) {
                        GenConf.ShowMessageBox("Error al activar cuenta.",ActiveAccount.this);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al activar cuenta. Compruebe su conexión.",ActiveAccount.this);
                }

                @Override
                public void onFinish() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    mdialog.cancel();
                    super.onFinish();
                }
            });


        }
        catch (Exception e){
            GenConf.ShowMessageBox("Error al activar cuenta.",this);
        }
    }


    public void ShowDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(ActiveAccount.this);
        View promptView = layoutInflater.inflate(R.layout.txtviewdialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActiveAccount.this);
        alertDialogBuilder.setView(promptView);

        final TextView editText = (TextView) promptView.findViewById(R.id.textData);
        editText.setText("Cuenta activada correctamente");
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CloseActivity();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
