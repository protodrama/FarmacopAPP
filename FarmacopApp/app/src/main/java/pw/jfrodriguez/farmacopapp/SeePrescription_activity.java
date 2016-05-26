package pw.jfrodriguez.farmacopapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SeePrescription_activity extends AppCompatActivity {

    prescription ThePrescription;
    TextView txtMedic,txtAmmount;
    ProgressDialog mdialog;
    ListView timeList;

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
        mdialog.setMessage("Obteniendo recetas");
        mdialog.setCancelable(false);

        ThePrescription = (prescription)getIntent().getExtras().get("presc");
        if(ThePrescription != null) {
            txtMedic = (TextView) findViewById(R.id.txtMedicam);
            txtMedic.setText(ThePrescription.medicament);
            txtAmmount = (TextView)findViewById(R.id.txtDs);
            txtAmmount.setText("" + ThePrescription.ammount);
            timeList = (ListView) findViewById(R.id.mlist);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newmessage = new Intent(SeePrescription_activity.this,NewMessage.class);
                    newmessage.putExtra("answerto",ThePrescription.medic);
                    startActivity(newmessage);
                }
            });
            GetTimeFromPrescription();
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

}
