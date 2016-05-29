package pw.jfrodriguez.farmacopapp;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
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

    ProgressDialog mdialog;
    EditText txtOriginal,txtNewPass,txtNewPass2;

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

        mdialog = new ProgressDialog(this);
        mdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mdialog.setMessage("Realizando operación");
        mdialog.setCancelable(false);

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
                                    GenConf.ShowMessageBox("Error al actualizar la contraseña", this);
                                }
                            }
                            else {
                                GenConf.ShowMessageBox("La nueva contraseña no puede ser igual que la antigua",this);
                            }
                        }
                        else {
                            GenConf.ShowMessageBox("La nueva contraseña no coincide",this);
                        }
                    } else {
                        GenConf.ShowMessageBox("La contraseña original no coincide con la actual",this);
                    }
                }
                catch (Exception e){
                    GenConf.ShowMessageBox("Error al comprobar la contraseña original",this);
                }
            }
            else
                GenConf.ShowMessageBox("Debes rellenar todos los campos",this);
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
                mdialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    mdialog.cancel();
                    int status = response.getInt("status");
                    if(status == 200) {
                        Toast.makeText(ModPass.this,"Contraseña actualizada con éxito",Toast.LENGTH_LONG).show();
                        Session.Pass = NewPassword;
                        CloseActivity();
                    }
                    else
                        throw new JSONException("");
                } catch (JSONException e) {
                    GenConf.ShowMessageBox("Error al actualizar la contraseña",ModPass.this);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mdialog.cancel();
                GenConf.ShowMessageBox("Error al actualizar la contraseña",ModPass.this);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }

            @Override
            public void onFinish() {
                mdialog.cancel();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                super.onFinish();
            }
        });
    }
}
