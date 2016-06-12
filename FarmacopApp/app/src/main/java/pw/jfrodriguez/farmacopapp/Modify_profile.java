package pw.jfrodriguez.farmacopapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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
    ProgressDialog mdialog;

    FloatingActionButton fab;
    static boolean showingDialog = false;
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
        mdialog = new ProgressDialog(this);
        mdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mdialog.setMessage("Actualizando datos");
        mdialog.setCancelable(false);

        txtName.setText(Session.Name);
        txtFSur.setText(Session.FirstSur);
        txtSSur.setText(Session.SecondSur);
        txtEmail.setText(Session.Email);
        txtFNac.setText(formatter.format(Date.valueOf(Session.FNac)));
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

    @Override
    protected void onResume() {
        super.onResume();
        if(showingDialog){
            fechaDialog.show();
        }
    }

    public void CloseActivity(){
        finish();
    }

    //Realiza las comprobaciones antes de actualizar los datos del usuario
    public void UpdateUserData(){
        if(CheckEditText()){
            if(Emailvalidate(txtEmail.getText().toString())){
                if(CheckChanges())
                {
                    if(GenConf.isNetworkAvailable(this))
                        UpdateUserDBData();
                    else
                        GenConf.ShowMessageBox("Error. Compruebe su conexión a internet.", this);
                }
                else
                    CloseActivity();
            }
            else{
                GenConf.ShowMessageBox("El Email no está bien formado", this);
            }
        }
        else{
            GenConf.ShowMessageBox("Debes rellenar todos los campos",this);
        }
    }

    //Muestra un diálogo de selección de fecha
    public void showdatedialog(){
        showingDialog = true;
        final Calendar newCalendar = Calendar.getInstance();
        fechaDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                if(newDate.getTime().after(newCalendar.getTime()))
                    GenConf.ShowMessageBox("La fecha no puede ser mayor a la fecha actual",Modify_profile.this);
                else {
                    if(newCalendar.get(Calendar.YEAR) - newDate.get(Calendar.YEAR) >= 110)
                        GenConf.ShowMessageBox("Fecha indicada demasiado antigua. Máximo rango de diferencia: 110 años",Modify_profile.this);
                    else {
                        txtFNac.setText(formatter.format(newDate.getTime()));
                    }
                }
                showingDialog = false;
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fechaDialog.show();
    }

    //Comprueba que todos los datos han sido introducidos
    public boolean CheckEditText(){
        return (!txtName.getText().toString().trim().equals("") && !txtFSur.getText().toString().trim().equals("")
                && !txtSSur.getText().toString().trim().equals("") && !txtFNac.getText().toString().trim().equals("")
                && !txtEmail.getText().toString().trim().equals(""));
    }

    //Comprueba el formato del email
    public boolean Emailvalidate(String email) {

        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    //Comprueba si hay cambios a realizar
    public boolean CheckChanges(){
            return (!txtName.getText().toString().equals(Session.Name) || !txtFSur.getText().toString().equals(Session.FirstSur)
                    || !txtSSur.getText().toString().equals(Session.SecondSur) || !txtFNac.getText().toString().equals(formatter.format(Date.valueOf(Session.FNac)))
                    || !txtEmail.getText().toString().equals(Session.Email));
    }

    //Actualiza los datos del usuario en la base de datos
    public void UpdateUserDBData(){
        try {

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            RequestParams parametros = new RequestParams();
            Calendar mcal = Calendar.getInstance();
            mcal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(txtFNac.getText().toString().split("/")[0]));
            mcal.set(Calendar.MONTH,Integer.parseInt(txtFNac.getText().toString().split("/")[1]));
            mcal.set(Calendar.YEAR,Integer.parseInt(txtFNac.getText().toString().split("/")[2]));
            parametros.put("account", Session.UserName);
            parametros.put("apikey", Session.Apikey);
            parametros.put("name", txtName.getText().toString());
            parametros.put("date", sqlformatter.format(mcal.getTime()));
            parametros.put("fsur", txtFSur.getText().toString());
            parametros.put("ssur", txtSSur.getText().toString());
            parametros.put("email", txtEmail.getText().toString());

            cliente.put(this, GenConf.UpdateUserURL, parametros, new JsonHttpResponseHandler() {
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
                        int status = response.getInt("status");
                        if(status == 200) {
                            UpdateSessionData();
                            Toast.makeText(Modify_profile.this, "Datos de la cuenta actualizados con éxito", Toast.LENGTH_LONG).show();
                            CloseActivity();
                        }
                        else
                            throw new JSONException("");
                    } catch (JSONException e) {
                        GenConf.ShowMessageBox("Error al actualizar.", Modify_profile.this);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al actualizar. Compruebe su conexión.", Modify_profile.this);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al conectar con el servidor. Inténtelo de nuevo o compruebe su conexión a internet.",Modify_profile.this);
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFinish() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    mdialog.cancel();
                    super.onFinish();
                }
            });


        }
        catch (Exception e){
            GenConf.ShowMessageBox("Error al actualizar.", this);
        }
    }

    //Cambia los datos del usuario en la aplicación
    public void UpdateSessionData(){
        Session.Name = txtName.getText().toString();
        Session.FirstSur = txtFSur.getText().toString();
        Session.SecondSur = txtSSur.getText().toString();
        Session.Email = txtEmail.getText().toString();
        Calendar mcal = Calendar.getInstance();
        mcal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(txtFNac.getText().toString().split("/")[0]));
        mcal.set(Calendar.MONTH,Integer.parseInt(txtFNac.getText().toString().split("/")[1]));
        mcal.set(Calendar.YEAR,Integer.parseInt(txtFNac.getText().toString().split("/")[2]));
        Session.FNac  = sqlformatter.format(mcal.getTime());
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
}
