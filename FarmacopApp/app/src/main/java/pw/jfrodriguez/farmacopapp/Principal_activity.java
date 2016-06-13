package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Principal_activity extends AppCompatActivity implements listDialogFragment.NoticeDialogListener,View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);
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
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                OpenNewMessageActivity();
            }
        });

        if(!backgroundService.Started){
            mybroadcast.StartServiceFromActivity(this);
        }

        try{
            this.getIntent().getExtras().get("tomessages");
            Intent messages = new Intent(this,messages_activity.class);
            startActivity(messages);
        }
        catch (Exception ex){}
    }

    //Abre el activity de mensajes si se abre la aplicación desde la notificación
    public void OpenNewMessageActivity(){
        Intent r = new Intent(this,NewMessage_activity.class);
        startActivity(r);
    }

    public void CloseActivity(){
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
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

    //Borra el usuario de la sesión y vuelve a la pantalla de login
    public void Logout(){
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION, 0);
        SharedPreferences.Editor mEditor = Preferences.edit();
        mEditor.putString(GenConf.ACCOUNT,null);
        mEditor.putString(GenConf.APIKEY, null);
        mEditor.apply();

        Intent i = new Intent(this,loginactivity.class);
        startActivity(i);
        this.finish();
    }

    //Diálogo de contacto
    @Override
    public void onDialogUserSelect(DialogFragment dialog, int which) {
        switch (which){
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

    //Controla cada una de las opciones del menú principal
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit:
                AskBeforeLogout();
                break;
            case R.id.showRecepies:
                Intent r = new Intent(this,prescriptions_activity.class);
                startActivity(r);
                break;
            case R.id.showcontrol:
                Intent a = new Intent(this,ControlTime_activity.class);
                startActivity(a);
                break;
            case R.id.showmessages:
                Intent m = new Intent(this,messages_activity.class);
                startActivity(m);
                break;
            case R.id.showprofile:
                Intent i = new Intent(this,Profile_activity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    //Pregunta antes de desconectar
    public void AskBeforeLogout(){
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View promptView = layoutInflater.inflate(R.layout.messagebox_layout, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(promptView);

            TextView textView = (TextView) promptView.findViewById(R.id.textViewtext);
            textView.setText("¿Desea desconectar y salir a la pantalla de conexión?");
            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Logout();
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

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
