package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class loginactivity extends AppCompatActivity implements View.OnClickListener,listDialogFragment.NoticeDialogListener {

    ProgressDialog mdialog;
    EditText txtName, txtPass;
    EditText messageBoxText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadActivity();
    }

    public void LoadActivity() {
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });
        mdialog = new ProgressDialog(this);
        mdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mdialog.setMessage("Comprobando credenciales");
        mdialog.setCancelable(false);
        txtName = (EditText) findViewById(R.id.txtName);
        txtPass = (EditText) findViewById(R.id.txtPass);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GenConf.ShowingRecPassDialog) {
            ShowRecPasDialog();
        }
    }

    public void SaveUserAccount(String User, String Apikey) {
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION, 0);
        SharedPreferences.Editor mEditor = Preferences.edit();
        mEditor.putString(GenConf.ACCOUNT, User);
        mEditor.putString(GenConf.APIKEY, Apikey);
        mEditor.apply();
    }

    public void CloseActivity() {
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i = null;
        if (id == R.id.action_about) {
            i = new Intent(this, About.class);
            startActivity(i);
        }
        if (id == R.id.action_contact) {
            DialogFragment dialogo = new listDialogFragment();
            dialogo.show(getFragmentManager(), "Contacto");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEntrar:

                CheckLogin(txtName.getText().toString(), txtPass.getText().toString());
                break;
            case R.id.activeAccount:
                Intent acc = new Intent(this, ActiveAccount.class);
                startActivity(acc);
                break;
            case R.id.restartPass:
                ShowRecPasDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        if (GenConf.ShowingRecPassDialog)
            GenConf.MessageFromRecPassDialog = messageBoxText.getText().toString();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDialogUserSelect(DialogFragment dialog, int which) {
        switch (which) {
            case 0:
                String email = getResources().getString(R.string.correo);
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Enviar email a " + email));
                break;
            case 1:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String telf = getResources().getString(R.string.telefono);
                intent.setData(Uri.parse("tel:" + telf));
                startActivity(intent);
                break;
        }
    }

    public void CheckLogin(String Name, String Password) {
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            final String UserName = Name;
            final String Pass = GenConf.MD5(Password);

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("apikey", "eadmghacdg");

            cliente.get(this, GenConf.LogURL, parametros, new JsonHttpResponseHandler() {
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
                        ComprobarCredenciales(response.getJSONArray("data"), UserName, Pass);
                    } catch (JSONException e) {
                        GenConf.ShowMessageBox("Error al acceder a los datos de las credenciales",loginactivity.this);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    GenConf.ShowMessageBox("Error al acceder a los datos de las credenciales. Compruebe su conexión",loginactivity.this);
                }
            });
        } catch (Exception e) {
            GenConf.ShowMessageBox("Error al comprobar el usuario: " + e.getMessage(), loginactivity.this);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    public void ComprobarCredenciales(JSONArray User, String Name, String Password) throws JSONException {
        if (User.length() > 0) {
            JSONObject datos = User.getJSONObject(0);
            if (datos.getString("Cuenta").equals(Name) && datos.getString("Contrasena").equals(Password)) {
                SaveUserAccount(Name, datos.getString("APIKEY"));
                GetUserData(Name, datos.getString("APIKEY"));
                return;
            }
        }
        GenConf.ShowMessageBox("El usuario o la contraseña no son correctos",this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public void CheckAccountToRestPassAndSend(String name) {
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            final String UserName = name;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("apikey", "eadmghacdg");

            mdialog.setMessage("Comprobando cuenta de usuario");

            cliente.get(this, GenConf.CheckUserAcURL, parametros, new JsonHttpResponseHandler() {
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
                        CheckData(response.getJSONArray("data"), UserName);
                    } catch (JSONException e) {
                        GenConf.ShowMessageBox("La cuenta indicada no es correcta",loginactivity.this);
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al comprobar la cuentas",loginactivity.this);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }

                @Override
                public void onFinish() {
                    mdialog.cancel();
                    super.onFinish();
                }
            });


        } catch (Exception e) {
            GenConf.ShowMessageBox("Error al comprobar el usuario",this);
        }

    }

    public void CheckData(JSONArray data, String name) throws JSONException {
        String username = data.getJSONObject(0).getString("Cuenta");
        if (username.equals(name))
            RestPassAndSend(name);
        else
            GenConf.ShowMessageBox("La cuenta indicada no es correcta",this);
    }

    public void RestPassAndSend(String name) {
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            final String UserName = name;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);
            Random rnd = new Random();
            Integer Low = 100000;
            Integer High = 999999;
            Integer Result = rnd.nextInt(High - Low) + Low;
            String temp = "" + Result;
            String codigo = GenConf.MD5(temp);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("apikey", GenConf.DEFAPIKEY);
            parametros.put("code", Result);
            parametros.put("npass", codigo);

            mdialog.setMessage("Procesando...");

            cliente.get(this, GenConf.RestPassURL, parametros, new JsonHttpResponseHandler() {
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
                        if (response.getBoolean("code"))
                            GenConf.ShowMessageBox("Se ha enviado un correo a su cuenta de correo con la contraseña nueva. Puede tardar unos minutos.",loginactivity.this);
                    } catch (JSONException e) {
                        GenConf.ShowMessageBox("Error al generar la contraseña",loginactivity.this);
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    GenConf.ShowMessageBox("Error al generar la contraseña",loginactivity.this);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            });


        } catch (Exception e) {
            GenConf.ShowMessageBox("Error al generar la contraseña",this);
        }
    }

    public void GetUserData(String Name, String apikey) {
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            final String UserName = Name;
            final String Apikey = apikey;

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.setMaxRetriesAndTimeout(0, 10000);

            RequestParams parametros = new RequestParams();
            parametros.put("account", UserName);
            parametros.put("apikey", Apikey);

            cliente.get(this, GenConf.UserDataURL, parametros, new JsonHttpResponseHandler() {
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
                        GetAllUserData(response.getJSONArray("data").getJSONObject(0));
                        StartMainActivity();
                    } catch (JSONException e) {
                        GenConf.ShowMessageBox("Error al acceder a los datos de la cuenta.",loginactivity.this);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mdialog.cancel();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }

                @Override
                public void onFinish() {
                    mdialog.cancel();
                    super.onFinish();
                }
            });


        } catch (Exception e) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    public void GetAllUserData(JSONObject data) throws JSONException {
        Session.UserName = data.getString("Cuenta");
        Session.Name = data.getString("Nombre");
        Session.Email = data.getString("correo");
        Session.FirstSur = data.getString("Apellido1");
        Session.SecondSur = data.getString("Apellido2");
        Session.Apikey = data.getString("APIKEY");
        Session.FNac = data.getString("FechaNac");
        Session.Pass = data.getString("Contrasena");
    }

    public void StartMainActivity() {
        Intent princ = new Intent(this, Principal.class);
        startActivity(princ);
        this.finish();
    }

    public void ShowRecPasDialog() {
        GenConf.ShowingRecPassDialog = true;
        LayoutInflater layoutInflater = LayoutInflater.from(loginactivity.this);
        View promptView = layoutInflater.inflate(R.layout.restpass_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(loginactivity.this);
        alertDialogBuilder.setView(promptView);

        messageBoxText = (EditText) promptView.findViewById(R.id.edittext);
        messageBoxText.setText(GenConf.MessageFromRecPassDialog);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //COMPROBAR Y MANDAR CORREO CON LA NUEVA CONTRASEÑA
                        GenConf.ShowingRecPassDialog = false;
                        GenConf.MessageFromRecPassDialog = "";
                        CheckAccountToRestPassAndSend(messageBoxText.getText().toString());
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                GenConf.ShowingRecPassDialog = false;
                                GenConf.MessageFromRecPassDialog = "";
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}