package pw.jfrodriguez.farmacopapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Alarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView mListView = (ListView)findViewById(R.id.mListView);
        ArrayList<String> Data = getIntent().getExtras().getStringArrayList("Data");
        if(Data != null)
            mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Data));

    }

    @Override
    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    @Override
    public void onBackPressed() {
        //nothing
    }

    @Override
    protected void onDestroy() {
        //Tambien llamar a actualizar toma
        super.onDestroy();
    }
}
