package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class messages_activity extends AppCompatActivity {

    mPagerAdapter adapter;
    ViewPager viewPager;
    ProgressDialog mdialog;
    TabLayout tabLayout;
    public static Integer TabShowing = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Nuevos"));
        tabLayout.addTab(tabLayout.newTab().setText("Leidos"));
        tabLayout.addTab(tabLayout.newTab().setText("Enviados"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
    }

    public void CloseActivity(){
        this.finish();
    }

    @Override
    protected void onStop() {
        TabShowing = tabLayout.getSelectedTabPosition();
        super.onStop();
    }

    @Override
    protected void onResume() {
        if(GenConf.isNetworkAvailable(this))
            GetMessages();
        else
            ShowDialogAndClose("Error. Compruebe su conexión a internet.");
        super.onResume();
    }

    //Obtiene los mensajes de la base de datos
    public void GetMessages(){
        try {
            mdialog = new ProgressDialog(this);
            mdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mdialog.setMessage("Actualizando mensajes");
            mdialog.setCancelable(false);

            final String UserName = Session.UserName;
            final String Apikey = Session.Apikey;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("apikey", Apikey);

            cliente.get(this,GenConf.GetAllMessagesURL,parametros,new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    mdialog.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        ShowLists(response.getJSONArray("data"));
                    } catch (JSONException e) {
                        mdialog.cancel();
                        GenConf.ShowMessageBox("Error al acceder a los datos de la cuenta.", messages_activity.this);
                        CloseActivity();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al acceder a los datos de la cuenta.", messages_activity.this);
                    CloseActivity();
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

        }
    }

    //Muestra el layout con tabulaciones
    public void ShowLists(JSONArray MessageList) throws JSONException{
        ArrayList<Message> messageList = ReadMessages(MessageList);

        adapter = new mPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),this,messageList);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(TabShowing);
        mdialog.cancel();
    }

    //Filtra los mensajes que se reciben desde el servidor
    public ArrayList<Message> ReadMessages(JSONArray MessageList) throws JSONException {

        ArrayList<Message> messageList =  new ArrayList<>();

        for(int i = 0; i < MessageList.length(); i++){
            JSONObject MensTemp = MessageList.getJSONObject(i);
            Message NewTemp = new Message();
            NewTemp.ID = MensTemp.getInt("ID");
            NewTemp.Subject = MensTemp.getString("Asunto");
            NewTemp.Message = MensTemp.getString("Mensaje").replace("[**]", "\r\n");
            NewTemp.Writer = MensTemp.getString("Emisor");
            NewTemp.Receptor = MensTemp.getString("Receptor");
            NewTemp.isread = MensTemp.getInt("Leido") == 1;
            messageList.add(NewTemp);
        }

        return messageList;

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
