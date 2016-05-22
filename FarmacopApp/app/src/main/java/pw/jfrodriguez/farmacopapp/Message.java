package pw.jfrodriguez.farmacopapp;

import java.io.Serializable;

/**
 * Created by Juanfran on 21/05/2016.
 */
public class Message implements Serializable {

    public int ID;
    public String Writer,Receptor,Subject,Message;
    public Boolean isread;

    public Message(){}

    @Override
    public String toString() {
        if(Writer.equals(Sesion.NombreUsuario))
            return Receptor + " -- " + Subject;
        else
            return Writer + " -- " + Subject;
    }

    public String ShowInTextView(){
        if(Writer.equals(Sesion.NombreUsuario))
            return "Para " + Receptor;
        else
            return "De " + Writer;
    }

    public String GetSubjectToShow(){
        if(Subject.length() > 24)
            return Subject.substring(0,21) + "...";
        else
            return Subject;
    }
}
