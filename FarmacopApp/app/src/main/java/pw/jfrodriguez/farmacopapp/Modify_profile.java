package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class Modify_profile extends AppCompatActivity implements View.OnClickListener {

    EditText txtName,txtFSur,txtSSur,txtEmail,txtFNac;
    SimpleDateFormat formatter,sqlformatter;
    DatePickerDialog fechaDialog;
    private Pattern pattern;
    private Matcher matcher;
    ProgressDialog dialogo;
    FloatingActionButton fab;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);

        txtName = (EditText)findViewById(R.id.txtName);
        txtFSur = (EditText)findViewById(R.id.txtFirstSur);
        txtSSur = (EditText)findViewById(R.id.txtSecondSur);
        txtFNac = (EditText)findViewById(R.id.txtFNac);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        sqlformatter = new SimpleDateFormat("yyyy-MM-dd");
        pattern = Pattern.compile(EMAIL_PATTERN);
        dialogo = new ProgressDialog(this);
        dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogo.setMessage("Actualizando datos");
        dialogo.setCancelable(false);

        txtName.setText(Session.Name);
        txtFSur.setText(Session.FirstSur);
        txtSSur.setText(Session.SecondSur);
        txtEmail.setText(Session.Email);
        txtFNac.setText(Session.FNac);
        txtFNac.setOnClickListener(this);
        txtFNac.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showdatedialog();
                }
            }
        });
    }

    public void CloseActivity(){
        finish();
    }

    public void UpdateUserData(){
        if(CheckEditText()){
            if(Emailvalidate(txtEmail.getText().toString())){
                if(CheckChanges())
                {
                    UpdateUserDBData();
                }
                else
                    CloseActivity();
            }
            else{
                MostrarAcceptDialog("El Email no está bien formado");
            }
        }
        else{
            MostrarAcceptDialog("Debes rellenar todos los campos");
        }
    }

    public void showdatedialog(){
        final Calendar newCalendar = Calendar.getInstance();
        fechaDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                if(newDate.getTime().after(newCalendar.getTime()))
                    MostrarAcceptDialog("La fecha no puede ser mayor a la fecha actual");
                else {
                    if(newCalendar.get(Calendar.YEAR) - newDate.get(Calendar.YEAR) >= 110)
                        MostrarAcceptDialog("Fecha indicada demasiado antigua. Máximo rango de diferencia: 110 años");
                    else
                        txtFNac.setText(sqlformatter.format(newDate.getTime()));
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fechaDialog.show();
    }

    public boolean CheckEditText(){
        return (!txtName.getText().toString().trim().equals("") && !txtFSur.getText().toString().trim().equals("")
                && !txtSSur.getText().toString().trim().equals("") && !txtFNac.getText().toString().trim().equals("")
                && !txtEmail.getText().toString().trim().equals(""));
    }

    public boolean Emailvalidate(String email) {

        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public boolean CheckChanges(){

            return (!txtName.getText().toString().equals(Session.Name) || !txtFSur.getText().toString().equals(Session.FirstSur)
                    || !txtSSur.getText().toString().equals(Session.SecondSur) || !txtFNac.getText().toString().equals(Session.FNac)
                    || !txtEmail.getText().toString().equals(Session.Email));
    }

    public void UpdateUserDBData(){
        try {

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            RequestParams parametros = new RequestParams();
            parametros.put("account", Session.UserName);
            parametros.put("apikey", Session.Apikey);
            parametros.put("name", txtName.getText().toString());
            parametros.put("date", txtFNac.getText().toString());
            parametros.put("fsur", txtFSur.getText().toString());
            parametros.put("ssur", txtSSur.getText().toString());
            parametros.put("email", txtEmail.getText().toString());

            cliente.put(this, GenConf.UpdateUserURL, parametros, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    dialogo.show();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        dialogo.cancel();
                        int status = response.getInt("status");
                        if(status == 200) {
                            UpdateSessionData();
                            Toast.makeText(Modify_profile.this, "Datos de la cuenta actualizados con éxito", Toast.LENGTH_LONG).show();
                            CloseActivity();
                        }
                        else
                            throw new JSONException("");
                    } catch (JSONException e) {
                        MostrarAcceptDialog("Error al actualizar.");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dialogo.cancel();
                    MostrarAcceptDialog("Error al actualizar. Compruebe su conexión.");
                }

                @Override
                public void onFinish() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    dialogo.cancel();
                    super.onFinish();
                }
            });


        }
        catch (Exception e){
            MostrarAcceptDialog("Error al actualizar.");
        }
    }

    public void UpdateSessionData(){
        Session.Name = txtName.getText().toString();
        Session.FirstSur = txtFSur.getText().toString();
        Session.SecondSur = txtSSur.getText().toString();
        Session.Email = txtEmail.getText().toString();
        Session.FNac  = txtFNac.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtFNac:
                showdatedialog();
                break;
            case R.id.fab:
                UpdateUserData();
                break;
        }
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
