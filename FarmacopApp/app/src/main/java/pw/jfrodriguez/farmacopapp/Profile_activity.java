package pw.jfrodriguez.farmacopapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;


public class Profile_activity extends AppCompatActivity {

    TextView txtUserName,txtName,txtFSur,txtSSur,txtFNac,txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });

        txtUserName = (TextView)findViewById(R.id.txtAccount);
        txtName = (TextView)findViewById(R.id.txtName);
        txtFSur = (TextView)findViewById(R.id.txtFirstSur);
        txtSSur = (TextView)findViewById(R.id.txtSecondSur);
        txtFNac = (TextView)findViewById(R.id.txtFNac);
        txtEmail = (TextView)findViewById(R.id.txtEmail);
    }

    //Recarga los datos de la sesión conectada
    @Override
    protected void onResume() {
        super.onResume();
        txtUserName.setText(Session.UserName);
        txtName.setText(Session.Name);
        txtFSur.setText(Session.FirstSur);
        txtSSur.setText(Session.SecondSur);
        txtEmail.setText(Session.Email);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        txtFNac.setText(formatter.format(Date.valueOf(Session.FNac)));
    }

    public void CloseActivity(){
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    //Opciones del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i;

        if (id == R.id.action_modify) {
            i = new Intent(this,Modify_profile.class);
            startActivity(i);
        }
        if(id == R.id.action_pass){
            i = new Intent(this,ModPass_activity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

}
