package pw.jfrodriguez.farmacopapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Juanfran on 21/05/2016.
 */
public class ctrl_fragment extends Fragment {

    RecyclerView miList;
    public ArrayList<Control> ListToShow;
    public Context contexto;
    CtrlAdapter myadapter;
    TextView empty;

    public ctrl_fragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.recycler_layout, container, false);

        myadapter = new CtrlAdapter(ListToShow);

        miList = (RecyclerView)layout.findViewById(R.id.mlist);
        empty = (TextView)layout.findViewById(R.id.textEmpty);

        if(ListToShow.size() == 0)
            empty.setVisibility(View.VISIBLE);
        else
            empty.setVisibility(View.INVISIBLE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(contexto);
        miList.setLayoutManager(mLayoutManager);
        miList.setItemAnimator(new DefaultItemAnimator());
        miList.setAdapter(myadapter);

        return layout;
    }
}
