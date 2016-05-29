package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SeeMessage extends AppCompatActivity {

    Message TheMessage;
    TextView Writer,Reader,Subject,message;
    Boolean NeedUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NeedUpdate = false;
        setContentView(R.layout.activity_see_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newmessage = new Intent(SeeMessage.this,NewMessage.class);
                newmessage.putExtra("answerto",TheMessage.Writer);
                startActivity(newmessage);
            }
        });

        try{
            TheMessage = (Message)getIntent().getExtras().get("message");

            if(TheMessage.Writer.equals(Session.UserName)) {
                fab.hide();
                NeedUpdate = false;
            }
            else{
                if(!TheMessage.isread) {
                    NeedUpdate = true;
                }
            }

            Writer = (TextView)findViewById(R.id.txtWriter);
            Writer.setText(TheMessage.Writer);
            Reader = (TextView)findViewById(R.id.txtReader);
            Reader.setText(TheMessage.Receptor);
            Subject = (TextView)findViewById(R.id.txtSubject);
            Subject.setText(TheMessage.Subject);
            message = (TextView)findViewById(R.id.txtmessage);
            message.setText(TheMessage.Message);

            if(NeedUpdate)
                UpdateMessageToRead();
        }catch (Exception e)
        {
            NeedUpdate = false;
            GenConf.ShowMessageBox("Error inesperado al cargar la información del mensaje",this);
        }

    }

    public void CloseActivity(){
        if(!NeedUpdate){
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        CloseActivity();
    }

    public void UpdateMessageToRead(){
            try {
                AsyncHttpClient cliente = new AsyncHttpClient();
                cliente.setMaxRetriesAndTimeout(0, 10000);

                RequestParams parametros = new RequestParams();
                parametros.put("account", Session.UserName);
                parametros.put("apikey", Session.Apikey);
                parametros.put("id", TheMessage.ID);

                cliente.put(this, GenConf.ReadMessageURL, parametros, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        NeedUpdate = false;
                    }
                });
            }
            catch (Exception e) {

            }
    }
}
