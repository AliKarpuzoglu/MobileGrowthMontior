package de.hs_mannheim.planb.mobilegrowthmonitor.database;

import java.io.Serializable;


/**
 * Created by eikood on 05.05.2016.
 *
 * Class provides Java Object for data set in FeedProfile table.
 *
 */

public class ProfileData implements Serializable {
    public String lastname, firstname, birthday, profilepic;
    public int sex, index;
}


