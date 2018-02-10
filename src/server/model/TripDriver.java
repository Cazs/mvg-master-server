package server.model;

import server.auxilary.IO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by ghost on 2017/02/03.
 */
public class TripDriver extends MVGObject
{
    private String trip_id;
    private String usr;
    public static final String TAG = "TripDriver";

    private StringProperty trip_idProperty(){return new SimpleStringProperty(trip_id);}

    public String getTrip_id()
    {
        return trip_id;
    }

    public void setTrip_id(String trip_id)
    {
        this.trip_id = trip_id;
    }

    private StringProperty usrProperty(){return new SimpleStringProperty(usr);}

    public String getUsr()
    {
        return usr;
    }

    public void setUsr(String usr)
    {
        this.usr = usr;
    }

    @Override
    public void parse(String var, Object val)
    {
        super.parse(var, val);
        try
        {
            switch (var.toLowerCase())
            {
                case "trip_id":
                    trip_id = String.valueOf(val);
                    break;
                case "usr":
                    usr = String.valueOf(val);
                    break;
                default:
                    IO.log(getClass().getName(), IO.TAG_ERROR, "unknown "+getClass().getName()+" attribute '" + var + "'.");
                    break;
            }
        } catch (NumberFormatException e)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public Object get(String var)
    {
        switch (var.toLowerCase())
        {
            case "trip_id":
                return trip_id;
            case "usr":
                return usr;
        }
        return super.get(var);
    }

    @Override
    public String apiEndpoint()
    {
        return "/trips/users";
    }
}
