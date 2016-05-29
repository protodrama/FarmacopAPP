package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
    ProgressDialog dialogo;
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
        GetMessages();
        super.onResume();
    }

    public void GetMessages(){
        try {
            dialogo = new ProgressDialog(this);
            dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialogo.setMessage("Actualizando mensajes");
            dialogo.setCancelable(false);

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
                    dialogo.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        ShowLists(response.getJSONArray("data"));
                    } catch (JSONException e) {
                        dialogo.cancel();
                        MostrarAcceptDialog("Error al acceder a los datos de la cuenta.");
                        CloseActivity();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dialogo.cancel();
                    MostrarAcceptDialog("Error al acceder a los datos de la cuenta.");
                    CloseActivity();
                }

                @Override
                public void onFinish() {
                    dialogo.cancel();
                    super.onFinish();
                }
            });


        }
        catch (Exception e){

        }
    }

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
        dialogo.cancel();
    }

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

    public void MostrarAcceptDialog(String message){
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(messages_activity.this);
            View promptView = layoutInflater.inflate(R.layout.messagebox_layout, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(messages_activity.this);
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
