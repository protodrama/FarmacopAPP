package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class NewMessage extends AppCompatActivity implements View.OnClickListener{

    static String TargetUser = "FarmacopAT";
    EditText txtSubject,txtMessage;
    ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });

        setSupportActionBar(toolbar);

        try{
            TargetUser = getIntent().getExtras().getString("answerto");
        }catch (Exception e){
            TargetUser = "FarmacopAT";
        }
        getSupportActionBar().setTitle("Mensaje para " + TargetUser);

        txtSubject = (EditText)findViewById(R.id.txtSubject);
        txtMessage = (EditText)findViewById(R.id.txtmessage);
        dialogo = new ProgressDialog(this);
        dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogo.setMessage("Enviando mensaje");
        dialogo.setCancelable(false);
    }

    public void CloseActivity(){
        this.finish();
    }

    @Override
    public void onClick(View v) {

        if(!txtSubject.getText().toString().trim().equals("") && !txtMessage.getText().toString().trim().equals("")){
            String subject = txtSubject.getText().toString();
            String message = txtMessage.getText().toString().replace("\n\r","[**]");
            SendMessage(subject,message);
        }
        else{
            MostrarAcceptDialog("Debes rellenar todos los campos");
        }
    }

    public void SendMessage(String subject, String message){
        try {
            final String Username = Session.UserName;
            final String Apikey = Session.Apikey;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", Username);
            parametros.put("apikey", Apikey);
            parametros.put("to", TargetUser);
            parametros.put("subject", subject);
            parametros.put("message", message);

            cliente.post(this,GenConf.AddMessageURL,parametros,new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    dialogo.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                        dialogo.cancel();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dialogo.cancel();
                }

                @Override
                public void onFinish() {
                    dialogo.cancel();
                    super.onFinish();
                    CloseActivity();
                }
            });


        }
        catch (Exception e){}
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
