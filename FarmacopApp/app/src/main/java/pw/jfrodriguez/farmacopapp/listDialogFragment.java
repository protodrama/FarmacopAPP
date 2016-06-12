package pw.jfrodriguez.farmacopapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Juanfran on 02/05/2016.
 */
public class listDialogFragment extends DialogFragment {

    //Este diálogo es el que muestra la lista de contacto al pulsar
    //sobre la opción "contáctanos" en el menú de las activities.

    AlertDialog.Builder constructor;

    //definimos como va a ser el dialogo en el método (onCreateDialog)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        constructor = new AlertDialog.Builder(getActivity());
        constructor.setTitle("Contáctanos");
        constructor.setItems(R.array.contact_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //accion realizara al pulsar cualquier boton
                //mListener es el notificador y coje la clase listDialog con una opcion which
                mListener.onDialogUserSelect(listDialogFragment.this, which);
            }
        });
        return constructor.create();
    }


    public interface NoticeDialogListener {
        public void onDialogUserSelect(DialogFragment dialog, int which);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //unimos la actividad con la interfaz para que se pueda coger el evento
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (Exception e) {
            throw new ClassCastException(activity.toString());
        }
    }
}
