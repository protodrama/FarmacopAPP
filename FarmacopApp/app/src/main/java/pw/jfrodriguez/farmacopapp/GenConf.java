package pw.jfrodriguez.farmacopapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Juanfran on 02/05/2016.
 */
public class GenConf {

    //Esta clase contiene datos que son accedidos por múltiples elementos de la aplicación

    public static final String DEFAPIKEY = "eadmghacdg";
    public static final String LogURL = "https://jfrodriguez.pw/slimrest/api/Login";
    public static final String ValidationURL = "https://jfrodriguez.pw/slimrest/api/Validation";
    public static final String ValidateURL = "https://jfrodriguez.pw/slimrest/api/Validate";
    public static final String CheckUserAcURL = "https://jfrodriguez.pw/slimrest/api/checkUser";
    public static final String RestPassURL = "https://jfrodriguez.pw/slimrest/api/restpass";
    public static final String UserDataURL = "https://jfrodriguez.pw/slimrest/api/Userdata";
    public static final String ServiceNotReadedMessagesURL = "https://jfrodriguez.pw/slimrest/api/NotReadedMessages";
    public static final String GetControlsURL = "https://jfrodriguez.pw/slimrest/api/GetControl";
    public static final String UpdateControlsURL = "https://jfrodriguez.pw/slimrest/api/UpdateControl";
    public static final String GetAllMessagesURL = "https://jfrodriguez.pw/slimrest/api/GetMessages";
    public static final String ReadMessageURL = "https://jfrodriguez.pw/slimrest/api/ReadMessage";
    public static final String AddMessageURL = "https://jfrodriguez.pw/slimrest/api/AddMessage";
    public static final String UpdateUserURL = "https://jfrodriguez.pw/slimrest/api/UpdateUser";
    public static final String UpdatePasswordURL = "https://jfrodriguez.pw/slimrest/api/UpdatePassword";
    public static final String GetMyActivePrescriptionsURL = "https://jfrodriguez.pw/slimrest/api/GetMyActivePrescriptions";
    public static final String GetTimeTableFromPrescription = "https://jfrodriguez.pw/slimrest/api/GetPrescriptionsTimetable";


    public static final String SAVEDSESION = "sesion_data";
    public static final String ACCOUNT = "account";
    public static final String APIKEY = "apikey";
    public static final String SeeMessages = "SeeMensages";

    public static Boolean ShowingRecPassDialog = false;

    public static String MessageFromRecPassDialog = "";

    //Convierte la cadena recibida a su firma hash
    public static String MD5(String cadena) throws NoSuchAlgorithmException {

        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(cadena.getBytes());
        byte messageDigest[] = digest.digest();

        StringBuffer MD5Hash = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++)
        {
            String h = Integer.toHexString(0xFF & messageDigest[i]);
            while (h.length() < 2)
                h = "0" + h;
            MD5Hash.append(h);
        }
        return "" + MD5Hash;
    }

    //Muestra un mensaje de texto en un cuadro
    public static void ShowMessageBox(String message,Context contexto){
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(contexto);
            View promptView = layoutInflater.inflate(R.layout.messagebox_layout, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contexto);
            alertDialogBuilder.setView(promptView);

            TextView textView = (TextView) promptView.findViewById(R.id.textViewtext);
            textView.setText(message);
            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            // create an alert dialog
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
        catch (Exception e){

        }
    }

    //Comprueba si hay conexión a internet
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
