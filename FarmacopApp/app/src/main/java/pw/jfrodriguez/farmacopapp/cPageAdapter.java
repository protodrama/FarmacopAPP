package pw.jfrodriguez.farmacopapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Juanfran on 24/05/2016.
 */
public class cPageAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    Context contexto;
    ArrayList<Control> Today,Tomorrow;

    public cPageAdapter(FragmentManager fm, int NumOfTabs,Context contexto,ArrayList<Control> listControl) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.contexto = contexto;
        CreateLists(listControl);
    }

    public void CreateLists(ArrayList<Control> controlList){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar myDate = Calendar.getInstance();
        String DateNow = format.format(myDate.getTime());
        Today = new ArrayList<>();
        Tomorrow = new ArrayList<>();

        for(int i = 0; i < controlList.size(); i++){
            Control temp = controlList.get(i);
            if(temp.date.equals(DateNow))
                Today.add(temp);
            else
                Tomorrow.add(temp);
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ctrl_fragment tab1 = new ctrl_fragment();
                tab1.ListToShow = Today;
                return tab1;
            case 1:
                ctrl_fragment tab2 = new ctrl_fragment();
                tab2.ListToShow = Tomorrow;
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
