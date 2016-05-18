package pw.jfrodriguez.farmacopapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Juanfran on 17/05/2016.
 */
public class mAdapter  extends ArrayAdapter<String> {

    //inflador para cada elemento de la lista (para darle un layout)
    private LayoutInflater miInflater;
    public ViewHolder Container;

    public mAdapter(Context context, int resource, ArrayList<String> objects, LayoutInflater inflater) {
        super(context, resource,objects);
        this.miInflater = inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //objeto que estará asignado al layout
        final String element = getItem(position);
        //Si el convertView que viene está vacío significa que el elemento es nuevo por lo tanto le
        // asignamos la clase contenedora d elos diferentes elementos del layout para poder hacerle referencia
        if(convertView == null){
            convertView = miInflater.inflate(R.layout.controltext,null);
            Container = new ViewHolder();
                        Container.Data = (TextView)convertView.findViewById(R.id.TextView);
            convertView.setTag(Container);
        }
        else
            Container = (ViewHolder)convertView.getTag();

        Container.Data.setText(element);
        return convertView;
    }

    private static class ViewHolder {
        TextView Data;
    }
}
