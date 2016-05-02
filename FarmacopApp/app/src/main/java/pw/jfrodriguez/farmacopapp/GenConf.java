package pw.jfrodriguez.farmacopapp;

import android.content.Context;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Juanfran on 02/05/2016.
 */
public class GenConf {

    public static final String DEFAPIKEY = "eadmghacdg";
    public static final String LogURL = "https://jfrodriguez.pw/slimrest/api/Login";

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

    public static void MostrarToast(Context contexto, String mensaje){
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
    }

}
