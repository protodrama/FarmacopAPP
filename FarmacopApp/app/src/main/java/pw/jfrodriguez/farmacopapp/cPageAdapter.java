package pw.jfrodriguez.farmacopapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Juanfran on 24/05/2016.
 */
public class cPageAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    Context contexto;
    ArrayList<Message> Readed,News,Sended;

    public cPageAdapter(FragmentManager fm, int NumOfTabs,Context contexto) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.contexto = contexto;
        //CreateLists(messageList);
    }

    public void CreateLists(ArrayList<Message> messageList){

        Readed = new ArrayList<>();
        News = new ArrayList<>();
        Sended = new ArrayList<>();

        for(int i = 0; i < messageList.size(); i++){

            Message Temp = messageList.get(i);
            if(Temp.Receptor.equals(Session.UserName)){
                if(Temp.isread)
                    Readed.add(Temp);
                else
                    News.add(Temp);
            }
            else{
                Sended.add(Temp);
            }
        }
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                men_fragment tab1 = new men_fragment();
                return tab1;
            case 1:
                men_fragment tab2 = new men_fragment();
                return tab2;
            case 2:
                men_fragment tab3 = new men_fragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
