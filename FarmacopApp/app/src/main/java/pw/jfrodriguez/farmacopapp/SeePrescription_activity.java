package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SeePrescription_activity extends AppCompatActivity {

    prescription ThePrescription;
    TextView txtMed, txtFStart, txtFend,txtAmmount,txtMedic;
    ProgressDialog mdialog;
    ListView timeList;
    SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_prescription);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        mdialog = new ProgressDialog(this);
        mdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mdialog.setMessage("Obteniendo datos de la receta");
        mdialog.setCancelable(false);
        formatter = new SimpleDateFormat("dd/MM/yyyy");

        ThePrescription = (prescription)getIntent().getExtras().get("presc");
        if(ThePrescription != null) {
            txtMed = (TextView) findViewById(R.id.txtMedicam);
            txtMed.setText(ThePrescription.medicament);
            txtAmmount = (TextView)findViewById(R.id.txtDs);
            txtAmmount.setText("" + ThePrescription.ammount);
            txtFStart = (TextView)findViewById(R.id.txtFStart);
            txtFStart.setText(formatter.format(Date.valueOf(ThePrescription.startDate)));
            txtFend = (TextView)findViewById(R.id.txtFEnd);
            txtFend.setText(formatter.format(Date.valueOf(ThePrescription.endDate)));
            txtMedic = (TextView)findViewById(R.id.txtMedic);
            txtMedic.setText(ThePrescription.medic);
            timeList = (ListView) findViewById(R.id.mlist);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newmessage = new Intent(SeePrescription_activity.this, NewMessage_activity.class);
                    newmessage.putExtra("answerto", ThePrescription.medic);
                    startActivity(newmessage);
                }
            });
            if(GenConf.isNetworkAvailable(this))
                GetTimeFromPrescription();
            else
                ShowDialogAndClose("Error. Compruebe su conexión a internet.");
        }
    }

    public void CloseActivity(){
        finish();
    }

    public void GetTimeFromPrescription()
    {
        try {

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", Session.UserName);
            parametros.put("apikey", Session.Apikey);
            parametros.put("id",ThePrescription.ID);

            cliente.get(this,GenConf.GetTimeTableFromPrescription,parametros,new JsonHttpResponseHandler(){
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
                        setListAdapter(response.getJSONArray("data"));
                    } catch (JSONException e) {
                        GenConf.ShowMessageBox("Error al obtener la tabla de tiempos de la receta.",SeePrescription_activity.this);
                        Log.i("milog", e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    mdialog.cancel();
                    ShowDialogAndClose("Error al conectar con el servidor. Inténtelo de nuevo o compruebe su conexión a internet.");
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al obtener la tabla de tiempos de la receta.",SeePrescription_activity.this);
                }

                @Override
                public void onFinish() {
                    mdialog.cancel();
                    super.onFinish();
                }
            });


        }
        catch (Exception e){
            GenConf.ShowMessageBox("Error al obtener la tabla de tiempos de la receta", this);
        }

    }

    public void setListAdapter(JSONArray timetable) throws JSONException{
        ArrayList<String> Timetable = new ArrayList<>();

        for(int i = 0; i < timetable.length(); i++){
            Timetable.add(String.format("%02d",timetable.getJSONObject(i).getInt("Hora")) + ":" + String.format("%02d",timetable.getJSONObject(i).getInt("Minuto")));
        }

        timeList.setAdapter(new mAdapter(this,R.layout.controltext,Timetable,this.getLayoutInflater()));

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
