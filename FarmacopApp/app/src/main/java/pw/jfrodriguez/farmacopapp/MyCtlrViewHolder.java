package pw.jfrodriguez.farmacopapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Juanfran on 20/05/2016.
 */
public class MyCtlrViewHolder extends RecyclerView.ViewHolder {
    public TextView TextInfo,TextTime;

    public MyCtlrViewHolder(View view) {
        super(view);
        TextTime = (TextView) view.findViewById(R.id.txtTime);
        TextInfo = (TextView)view.findViewById(R.id.txtMedicament);
    }

}
