package de.hs_mannheim.planb.mobilegrowthmonitor.database;

import java.io.Serializable;

/**
 * Created by Laura on 23.05.2016.
 *
 * Class provides Java Object for data set in feedMeasurement table.
 */
public class MeasurementData implements Serializable {

    public int index;
    public double weight, height;
    public String image, date,edited;

    @Override
    public String toString(){
        return index + ","+ date + "," + height + "," + weight + "," +image;
    }

}
