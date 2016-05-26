package pw.jfrodriguez.farmacopapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Juanfran on 20/05/2016.
 */

public class MyPresViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public IPresAdapterOnClick mListener;
    public TextView textMed;
    public LinearLayout container;
    public prescription ThePrescription;

    public MyPresViewHolder(View view, IPresAdapterOnClick listener) {
        super(view);
        textMed = (TextView) view.findViewById(R.id.txtMedicament);
        container = (LinearLayout)view.findViewById(R.id.element);

        mListener = listener;
        container.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(ThePrescription);
    }


    public interface IPresAdapterOnClick {
        void onItemClick(prescription item);
    }
}
