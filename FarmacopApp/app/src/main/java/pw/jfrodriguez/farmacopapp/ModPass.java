package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;

public class ModPass extends AppCompatActivity implements View.OnClickListener{

    ProgressDialog dialogo;
    EditText txtOriginal,txtNewPass,txtNewPass2;
    boolean showMessageBoxUpdatedTrue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_pass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });

        dialogo = new ProgressDialog(this);
        dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogo.setMessage("Realizando operación");
        dialogo.setCancelable(false);

        txtOriginal = (EditText)findViewById(R.id.txtOriginalPass);
        txtNewPass = (EditText)findViewById(R.id.txtNewPass);
        txtNewPass2 = (EditText)findViewById(R.id.txtNewPass2);
    }

    public void CloseActivity(){
        this.finish();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnModify){
            if(CheckEditTexts()){
                try {
                    if (CheckOriginalPass()) {
                        if(CheckNewPassWords()){
                            if(CheckNewWithOld()) {
                                try {
                                    UpdatePassWord();
                                } catch (Exception e) {
                                    MostrarAcceptDialog("Error al actualizar la contraseña");
                                }
                            }
                            else {
                                MostrarAcceptDialog("La nueva contraseña no puede ser igual que la antigua");
                            }
                        }
                        else {
                            MostrarAcceptDialog("La nueva contraseña no coincide");
                        }
                    } else {
                        MostrarAcceptDialog("La contraseña original no coincide con la actual");
                    }
                }
                catch (Exception e){
                    MostrarAcceptDialog("Error al comprobar la contraseña original");
                }
            }
            else
                MostrarAcceptDialog("Debes rellenar todos los campos");
        }
    }

    public boolean CheckEditTexts(){
        return (!txtOriginal.getText().toString().trim().equals("") && !txtNewPass.getText().toString().trim().equals("") && !txtNewPass2.getText().toString().trim().equals(""));
    }

    public boolean CheckOriginalPass() throws NoSuchAlgorithmException{
        return GenConf.MD5(txtOriginal.getText().toString()).equals(Session.Pass);
    }

    public boolean CheckNewPassWords(){
        return txtNewPass.getText().toString().equals(txtNewPass2.getText().toString());
    }

    public boolean CheckNewWithOld(){
        return !txtOriginal.getText().toString().equals(txtNewPass.getText().toString());
    }

    public void UpdatePassWord() throws NoSuchAlgorithmException {
        final String NewPassword = GenConf.MD5(txtNewPass.getText().toString());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.setMaxRetriesAndTimeout(0, 10000);

        RequestParams parametros = new RequestParams();
        parametros.put("account", Session.UserName);
        parametros.put("apikey", Session.Apikey);
        parametros.put("password",NewPassword);

        cliente.put(this, GenConf.UpdatePasswordURL, parametros, new JsonHttpResponseHandler() {
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
                    int status = response.getInt("status");
                    if(status == 200) {
                        Toast.makeText(ModPass.this,"Contraseña actualizada con éxito",Toast.LENGTH_LONG).show();
                        Session.Pass = NewPassword;
                        CloseActivity();
                    }
                    else
                        throw new JSONException("");
                } catch (JSONException e) {
                    MostrarAcceptDialog("Error al actualizar la contraseña");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dialogo.cancel();
                MostrarAcceptDialog("Error al actualizar la contraseña");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }

            @Override
            public void onFinish() {
                dialogo.cancel();
                super.onFinish();
            }
        });
    }

    public void MostrarAcceptDialog(String message){
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View promptView = layoutInflater.inflate(R.layout.messagebox_layout, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
        catch (Exception e){

        }
    }
}
