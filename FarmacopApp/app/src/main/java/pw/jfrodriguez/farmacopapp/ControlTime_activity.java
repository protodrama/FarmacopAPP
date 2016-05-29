package pw.jfrodriguez.farmacopapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class ControlTime_activity extends AppCompatActivity {

    cPageAdapter adapter;
    ViewPager viewPager;
    ProgressDialog mdialog;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Hoy"));
        tabLayout.addTab(tabLayout.newTab().setText("Mañana"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        try {
            GetControl();
        }
        catch (Exception e){

        }
    }

    public void CloseActivity(){
        this.finish();
    }

    public void ShowLists(JSONArray data) throws JSONException {
        ArrayList<Control> controlList = GetControlTodayAndTomorrow(data);

        adapter = new cPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),this,controlList);
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
        viewPager.setCurrentItem(0);
        mdialog.cancel();
    }

    public void GetControl() throws JSONException{
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

        cliente.get(this, GenConf.GetControlsURL, parametros, new JsonHttpResponseHandler() {
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
                    GenConf.ShowMessageBox("Error al obtener los horarios de tomas.",ControlTime_activity.this);
                    CloseActivity();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mdialog.cancel();
                GenConf.ShowMessageBox("Error al obtener los horarios de tomas.",ControlTime_activity.this);
                CloseActivity();
            }

            @Override
            public void onFinish() {
                mdialog.cancel();
                super.onFinish();
            }
        });


    }
    catch (Exception e) {
        mdialog.cancel();
        GenConf.ShowMessageBox("Error al obtener los horarios de tomas.",this);
        CloseActivity();
    }
    }

    public ArrayList<Control> GetControlTodayAndTomorrow(JSONArray Controls) throws JSONException{
        Log.i("milog", "filtrando fechas control");
        ArrayList<Control> Data = new ArrayList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar myDate = Calendar.getInstance();
            String DateNow = format.format(myDate.getTime());
            myDate.add(Calendar.DAY_OF_MONTH, 1);
            String DateTomorrow = format.format(myDate.getTime());
            Log.i("milog", "fecha actual" + DateNow);
            for (int i = 0; i < Controls.length(); i++) {
                String date = Controls.getJSONObject(i).getString("Fecha");
                if (date.equals(DateNow) || date.equals(DateTomorrow)) {
                    Control temp = new Control();
                    temp.ammount = "" + Controls.getJSONObject(i).getInt("Dosis");
                    temp.medicament = Controls.getJSONObject(i).getString("med");
                    temp.date = date;
                    temp.time = String.format("%02d",Controls.getJSONObject(i).getInt("Hora")) + ":" + String.format("%02d",Controls.getJSONObject(i).getInt("Minuto"));
                    if(date.equals(DateNow)) {
                        if (Controls.getJSONObject(i).getInt("Hora") >= myDate.get(Calendar.HOUR_OF_DAY)) {
                            if (Controls.getJSONObject(i).getInt("Hora") > myDate.get(Calendar.HOUR_OF_DAY)) {
                                Data.add(temp);
                            } else {
                                if (Controls.getJSONObject(i).getInt("Minuto") >= myDate.get(Calendar.MINUTE)) {
                                    Data.add(temp);
                                }
                            }
                        }
                    }
                    else {
                        Data.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            Log.i("milog", e.getMessage());
        }
        return Data;
    }
}
