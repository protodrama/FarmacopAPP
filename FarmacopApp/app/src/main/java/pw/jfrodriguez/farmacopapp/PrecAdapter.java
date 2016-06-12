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
public class PrecAdapter extends RecyclerView.Adapter<MyPresViewHolder> {

    //Adapter de la lista de recetas para el recycler view del activity prescriptions_activity

    private List<prescription> data;
    IPresAdapterOnClick mListener;

    public PrecAdapter(ArrayList<prescription> data, IPresAdapterOnClick listener)     {
        this.data = data;
        mListener = listener;
    }

    @Override
    public MyPresViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_prescription_layout, parent, false);

        MyPresViewHolder vh = new MyPresViewHolder(itemView, new MyPresViewHolder.IPresAdapterOnClick() {
            @Override
            public void onItemClick(prescription theprescription) {
                mListener.onClickListener(theprescription);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(MyPresViewHolder holder, int position) {
        prescription temp = data.get(position);
        holder.textMed.setText(temp.medicament);
        holder.ThePrescription = temp;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface IPresAdapterOnClick {
        void onClickListener(prescription theprescrioption);
    }
}