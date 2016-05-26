package pw.jfrodriguez.farmacopapp;

import java.io.Serializable;

/**
 * Created by Juanfran on 25/05/2016.
 */
public class prescription implements Serializable {

    int ID;
    String medicament;
    int ammount;
    String startDate;
    String endDate;
    String medic;

    public void prescription(){}
}
