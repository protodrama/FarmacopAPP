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
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class NewMessage extends AppCompatActivity implements View.OnClickListener{

    static String TargetUser = "FarmacopAT";
    EditText txtSubject,txtMessage;
    ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });

        try{
            TargetUser = getIntent().getExtras().getString("answerto");
        }catch (Exception e){
            TargetUser = "FarmacopAT";
        }
        getSupportActionBar().setTitle("Mensaje para " + TargetUser);

        txtSubject = (EditText)findViewById(R.id.txtSubject);
        txtMessage = (EditText)findViewById(R.id.txtmessage);
        mdialog = new ProgressDialog(this);
        mdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mdialog.setMessage("Enviando mensaje");
        mdialog.setCancelable(false);
    }

    public void CloseActivity(){
        finish();
    }

    @Override
    public void onClick(View v) {

        if(!txtSubject.getText().toString().trim().equals("") && !txtMessage.getText().toString().trim().equals("")){
            String subject = txtSubject.getText().toString();
            String message = txtMessage.getText().toString().replace("\n\r","[**]");
            SendMessage(subject,message);
        }
        else{
            GenConf.ShowMessageBox("Debes rellenar todos los campos",this);
        }
    }

    public void SendMessage(String subject, String message){
        try {
            final String Username = Session.UserName;
            final String Apikey = Session.Apikey;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            RequestParams parametros = new RequestParams();
            parametros.put("account", Username);
            parametros.put("apikey", Apikey);
            parametros.put("to", TargetUser);
            parametros.put("subject", subject);
            parametros.put("message", message);

            cliente.post(this, GenConf.AddMessageURL, parametros, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    mdialog.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    mdialog.cancel();
                    CloseActivity();
                    Toast.makeText(NewMessage.this,"Mensaje enviado con éxito",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    Toast.makeText(NewMessage.this,"Error al enviar el mensaje",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFinish() {
                    mdialog.cancel();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    super.onFinish();

                }
            });


        }
        catch (Exception e){}
    }

}
