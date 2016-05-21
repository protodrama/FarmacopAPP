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

import java.util.ArrayList;

/**
 * Created by Juanfran on 21/05/2016.
 */
public class men_fragment extends Fragment {

    RecyclerView miLista;
    ArrayList<Message> ListToShow;
    Context contexto;
    MenAdapter myadapter;

    public men_fragment(){}

    public men_fragment(Context contexto,ArrayList<Message> messageList){
        this.ListToShow = messageList;
        this.contexto = contexto;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.message_layout, container, false);

        myadapter = new MenAdapter(ListToShow, new MenAdapter.IAdapterOnClick() {
            @Override
            public void onClickListener(Message message) {
                OpenToSeeMessage(message);
            }
        });

       miLista = (RecyclerView)layout.findViewById(R.id.mlista);

       RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(contexto);
       miLista.setLayoutManager(mLayoutManager);
       miLista.setItemAnimator(new DefaultItemAnimator());
       miLista.setAdapter(myadapter);

        return layout;
    }

    public void OpenToSeeMessage(Message message){
        Intent in = new Intent(contexto,SeeMessage.class);
        in.putExtra("message",message);
        startActivity(in);
    }
}
