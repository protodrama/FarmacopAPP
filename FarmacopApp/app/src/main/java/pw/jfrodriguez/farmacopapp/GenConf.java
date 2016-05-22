package pw.jfrodriguez.farmacopapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Juanfran on 02/05/2016.
 */
public class GenConf {

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


    public static final String SAVEDSESION = "sesion_data";

    public static final String ACCOUNT = "cuenta";
    public static final String APIKEY = "apikey";

    public static final String SeeMessages = "SeeMensages";

    public static Boolean OpenedToSeeMessages = false;
    public static Boolean ShowingRecPassDialog = false;

    public static String MessageFromRecPassDialog = "";

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

}
