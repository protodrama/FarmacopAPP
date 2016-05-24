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

import java.util.ArrayList;

/**
 * Created by Juanfran on 21/05/2016.
 */
public class ctrl_fragment extends Fragment {

    RecyclerView miLista;
    public ArrayList<Control> ListToShow;
    public Context contexto;
    CtrlAdapter myadapter;

    public ctrl_fragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.recycler_layout, container, false);

        myadapter = new CtrlAdapter(ListToShow);

        miLista = (RecyclerView)layout.findViewById(R.id.mlista);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(contexto);
        miLista.setLayoutManager(mLayoutManager);
        miLista.setItemAnimator(new DefaultItemAnimator());
        miLista.setAdapter(myadapter);

        return layout;
    }
}
