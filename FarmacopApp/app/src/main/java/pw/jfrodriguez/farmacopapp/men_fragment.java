package pw.jfrodriguez.farmacopapp;


import android.content.Context;
import android.content.Intent;
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
public class men_fragment extends Fragment {

    //este fragment contiene la lista de mensajes que se muestran en el RecyclerView

    RecyclerView miLista;
    TextView empty;
    public ArrayList<Message> ListToShow;
    public Context contexto;
    MenAdapter myadapter;

    public men_fragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.recycler_layout, container, false);

        myadapter = new MenAdapter(ListToShow, new MenAdapter.IAdapterOnClick() {
            @Override
            public void onClickListener(Message message) {
                OpenToSeeMessage(message);
            }
        });

       miLista = (RecyclerView)layout.findViewById(R.id.mlist);
        empty = (TextView)layout.findViewById(R.id.textEmpty);
        if(ListToShow.size() == 0)
            empty.setVisibility(View.VISIBLE);
        else
            empty.setVisibility(View.INVISIBLE);

       RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(contexto);
       miLista.setLayoutManager(mLayoutManager);
       miLista.setItemAnimator(new DefaultItemAnimator());
       miLista.setAdapter(myadapter);

        return layout;
    }

    public void OpenToSeeMessage(Message message){
        Intent in = new Intent(contexto,SeeMessage_activity.class);
        in.putExtra("message",message);
        startActivity(in);
    }
}
