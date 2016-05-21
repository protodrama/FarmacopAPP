package pw.jfrodriguez.farmacopapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Juanfran on 20/05/2016.
 */
public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public IMyViewHolderClicks mListener;
    public TextView text,subject;
    public LinearLayout container;
    public Message TheMessage;

    public MyViewHolder(View view,IMyViewHolderClicks listener) {
        super(view);
        text = (TextView) view.findViewById(R.id.theUser);
        container = (LinearLayout)view.findViewById(R.id.element);
        subject = (TextView)view.findViewById(R.id.textSubject);

        mListener = listener;
        text.setOnClickListener(this);
        container.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        mListener.onItemClick(TheMessage);
    }


    public interface IMyViewHolderClicks {
        void onItemClick(Message item);
    }
}
