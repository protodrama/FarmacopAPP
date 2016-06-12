package pw.jfrodriguez.farmacopapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juanfran on 21/05/2016.
 */
public class CtrlAdapter extends RecyclerView.Adapter<MyCtlrViewHolder> {

    //Esta clase se encarga de asignar los valores de los controles
    //a los diferentes campos del layout en el que se muestra

    private List<Control> data;

    public CtrlAdapter(ArrayList<Control> data)     {
        this.data = data;
    }

    @Override
    public MyCtlrViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_control_layout, parent, false);

        MyCtlrViewHolder vh = new MyCtlrViewHolder(itemView);

        return vh;
    }

    @Override
    public void onBindViewHolder(MyCtlrViewHolder holder, int position) {
        Control temp = data.get(position);
        holder.TextInfo.setText(temp.medicament + " -- " + temp.ammount + " mg");
        holder.TextTime.setText(temp.time);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}