package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class prescriptions_activity extends AppCompatActivity {

    ProgressDialog mdialog;
    RecyclerView mRecyclerView;
    TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);
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
        mdialog.setMessage("Obteniendo recetas");
        mdialog.setCancelable(false);

        mRecyclerView = (RecyclerView)findViewById(R.id.mrecycler);
        empty = (TextView)findViewById(R.id.textEmpty);

        if(GenConf.isNetworkAvailable(this))
            GetAllPrescriptions();
        else
            ShowDialogAndClose("Error. Compruebe su conexión a internet.");
    }

    public void CloseActivity()
    {
        finish();
    }

    //Obtiene la lista de recetas activas para el usuario
    public void GetAllPrescriptions(){
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", Session.UserName);
            parametros.put("apikey", Session.Apikey);

            cliente.get(this,GenConf.GetMyActivePrescriptionsURL,parametros,new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    mdialog.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        SetAdapter(response.getJSONArray("data"));
                    } catch (JSONException e) {
                        mdialog.cancel();
                        GenConf.ShowMessageBox("Error al obtener las recetas", prescriptions_activity.this);
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
                    GenConf.ShowMessageBox("Error al obtener las recetas", prescriptions_activity.this);
                }

                @Override
                public void onFinish() {
                    mdialog.cancel();
                    super.onFinish();
                }
            });


        }
        catch (Exception e){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    //Obtiene los datos de la lista recibida
    public void SetAdapter(JSONArray listPrescriptions) throws JSONException {

        ArrayList<prescription> prescriptionsList = new ArrayList<>();

        for(int i = 0; i < listPrescriptions.length();i++){
            prescription temp = new prescription();
            temp.ID = listPrescriptions.getJSONObject(i).getInt("ID");
            temp.ammount = listPrescriptions.getJSONObject(i).getInt("Dosis");
            temp.medicament = listPrescriptions.getJSONObject(i).getString("Nombre");
            temp.medic = listPrescriptions.getJSONObject(i).getString("Medico");
            temp.startDate = listPrescriptions.getJSONObject(i).getString("FechaInic");
            temp.endDate = listPrescriptions.getJSONObject(i).getString("FechaFin");
            prescriptionsList.add(temp);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(new PrecAdapter(prescriptionsList, new PrecAdapter.IPresAdapterOnClick() {
            @Override
            public void onClickListener(prescription theprescription) {
                StartSeePrescription(theprescription);
            }
        }));

        mdialog.cancel();

        if(prescriptionsList.size() == 0)
            empty.setVisibility(View.VISIBLE);
        else
            empty.setVisibility(View.INVISIBLE);

    }

    //Lanza el activity para ver los datos de la receta
    public void StartSeePrescription(prescription theprescription){
        Intent i = new Intent(this,SeePrescription_activity.class);
        i.putExtra("presc", theprescription);
        startActivity(i);
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
