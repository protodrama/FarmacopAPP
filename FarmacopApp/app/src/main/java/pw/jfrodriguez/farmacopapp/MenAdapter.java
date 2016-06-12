package pw.jfrodriguez.farmacopapp;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juanfran on 21/05/2016.
 */
public class MenAdapter extends RecyclerView.Adapter<MyViewHolder> {

    //Esta clase es el contenedor del layout que muestra los datos de los mensajes
    //en los recyclerview de los fragment de messages_activity.

    private List<Message> data;
    IAdapterOnClick mListener;

    public MenAdapter(ArrayList<Message> data,IAdapterOnClick listener)     {
        this.data = data;
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_message_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(itemView, new MyViewHolder.IMyViewHolderClicks() {
            @Override
            public void onItemClick(Message message) {
                mListener.onClickListener(message);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message temp = data.get(position);
        holder.subject.setText(temp.GetSubjectToShow());
        holder.text.setText(temp.ShowInTextView() + ":");
        holder.TheMessage = temp;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //Esta interfaz se utiliza para capturar la pulsación sobre el mensaje
    public interface IAdapterOnClick {
        void onClickListener(Message message);
    }
}