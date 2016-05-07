package pw.jfrodriguez.farmacopapp;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Principal extends AppCompatActivity implements listDialogFragment.NoticeDialogListener,View.OnClickListener {

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

        if(id == R.id.user_profile){

        }
        if (id == R.id.action_about) {
            i = new Intent(this,About.class);
            startActivity(i);
        }
        if (id == R.id.action_contact) {
            DialogFragment dialogo = new listDialogFragment();
            dialogo.show(getFragmentManager(),"Contacto");
        }
        if(id == R.id.action_logout){
            Logout();
        }

        return super.onOptionsItemSelected(item);
    }

    public void Logout(){
        SharedPreferences Preferences = getApplicationContext().getSharedPreferences(GenConf.SAVEDSESION,0);
        SharedPreferences.Editor mEditor = Preferences.edit();
        mEditor.putString(GenConf.ACCOUNT,null);
        mEditor.putString(GenConf.APIKEY, null);
        mEditor.apply();

        Intent i = new Intent(this,loginactivity.class);
        startActivity(i);
        this.finish();
    }

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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.exit){
            Logout();
        }
    }
}
