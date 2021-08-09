package algonquin.cst2335.cst2335project;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


/**
 * This class initiates all the variables of a charging station.
 *
 */
public class ChargingInfo {
    String locationTitle;
    String latitude;
    String longitude;
    String telNumber;
    long id;

    /**
     * constructor
     * @param locationTitle location title of the station
     */

    public ChargingInfo(String locationTitle) {
        this.locationTitle=locationTitle;
    }


    /**
     * Constructor
     * @param locationTitle  title of the station location
     * @param latitude the latitude of the station
     * @param longitude the longitude of the station
     * @param telNumber the telphone number of the station
     */
    public ChargingInfo(String locationTitle, String latitude, String longitude, String telNumber) {
        this.locationTitle = locationTitle;
        this.latitude = latitude;
        this.longitude=longitude;
        this.telNumber=telNumber;
    }

    /**
     * Constructor
     * @param locationTitle  title of the station location
     * @param latitude  the latitude of the station
     * @param longitude the longitude of the station
     * @param telNumber the telphone number of the station
     * @param id  the id of the row
     */
    public ChargingInfo(String locationTitle, String latitude, String longitude, String telNumber, long id) {
        this.locationTitle = locationTitle;
        this.latitude = latitude;
        this.longitude=longitude;
        this.telNumber=telNumber;
        setId(id);
    }

    /**
     * setter of location id
     * @param l id of the station row
     */
    public void setId(long l){
        id = l;
    }
    /**
     * getter of location id
     */
    public long getId(){
        return id;
    }
    /**
     * getter of location title
     */
    public String getLocationTitle() {
        return locationTitle;
    }
    /**
     * setter of station title
     * @param locationTitle title of station
     */
    public void setLocationTitle(String locationTitle) {
        this.locationTitle= locationTitle;
    }
    /**
     * getter of latitude
     */
    public String getLatitude() {
        return latitude;
    }
    /**
     * setter of station latitude
     * @param latitude latitude of station
     */
    public void setLatitude(String latitude) {
        this.latitude= latitude;
    }
    /**
     * getter of longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * setter of station longitude
     * @param longitude longitude of station
     */
    public void setLongitude(String longitude) {
        this.longitude= longitude;
    }
    /**
     * getter of telphone number
     */
    public String getTelNumber() {
        return telNumber;
    }

    /**
     * setter of station telphone number
     * @param telNumber telphone number of station
     */
    public void setTelNumber(String telNumber) {
        this.telNumber= telNumber;
    }


}
