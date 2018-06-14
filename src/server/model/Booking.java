package server.model;

import server.auxilary.AccessLevels;
import server.auxilary.IO;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author ghost
 */
public class Booking extends MVGObject
{
    private long departure_date; // date (and time) they left
    private long arrival_date; // date (and time) they arrived
    private long date_scheduled;
    private long return_date;
    private long adult_count;
    private long children_count;
    private String client_id;
    public static final String TAG = "Booking";

    public Booking()
    {}

    public Booking(String _id)
    {
        super(_id);
    }

    @Override
    public AccessLevels getReadMinRequiredAccessLevel()
    {
        return AccessLevels.STANDARD;
    }

    @Override
    public AccessLevels getWriteMinRequiredAccessLevel()
    {
        return AccessLevels.STANDARD;
    }

    public long getDeparture_date()
    {
        return departure_date;
    }

    public void setDeparture_date(long departure_date)
    {
        this.departure_date = departure_date;
    }

    public long getArrival_date()
    {
        return arrival_date;
    }

    public void setArrival_date(long arrival_date)
    {
        this.arrival_date = arrival_date;
    }

    public String getClient_id()
    {
        return client_id;
    }

    public void setClient_id(String client_id)
    {
        this.client_id = client_id;
    }

    public long getReturn_date()
    {
        return return_date;
    }

    public void setReturn_date(long return_date)
    {
        this.return_date = return_date;
    }

    public long getAdult_count()
    {
        return adult_count;
    }

    public void setAdult_count(long adult_count)
    {
        this.adult_count = adult_count;
    }

    public long getChildren_count()
    {
        return children_count;
    }

    public void setChildren_count(long children_count)
    {
        this.children_count = children_count;
    }

    public long getDate_scheduled()
    {
        return date_scheduled;
    }

    public void setDate_scheduled(long date_scheduled)
    {
        this.date_scheduled = date_scheduled;
    }

    @Override
    public String[] isValid()
    {
        if(getDate_scheduled() <= 0)
            return new String[]{"false", "invalid scheduled date."};

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        // if(getDate_scheduled()/1000/60/60/24 <  calendar.getTimeInMillis()/1000/60/24)// new Date().getTime()/1000/60/60/24)
        //    return new String[]{"false", "invalid scheduled date - cannot be in the past."};
        if(getAdult_count() < 0)
            return new String[]{"false", "invalid adult count."};
        if(getChildren_count() < 0)
            return new String[]{"false", "invalid children count."};
        if(getAdult_count() <= 0 && getChildren_count() <= 0)
            return new String[]{"false", "invalid adult & children count."};
        if(getReturn_date() > 0)
            if(getDate_scheduled() > getReturn_date())
                return new String[]{"false", "invalid input, scheduled date cannot be after the return date."};
        /*if(getReceiver()==null)
            return new String[]{"false", "invalid client_id."};*/

        return super.isValid();
    }

    @Override
    public void parse(String var, Object val)
    {
        super.parse(var, val);
        try
        {
            switch (var.toLowerCase())
            {
                case "date_scheduled":
                    setDate_scheduled(Long.parseLong(String.valueOf(val)));
                    break;
                case "return_date":
                    setReturn_date(Integer.parseInt((String)val));
                    break;
                case "adult_count":
                    setAdult_count(Integer.parseInt((String)val));
                    break;
                case "children_count":
                    setChildren_count(Integer.parseInt((String)val));
                    break;
                case "client_id":
                    client_id = (String)val;
                    break;
                case "departure_date":
                    departure_date = Long.parseLong(String.valueOf(val));
                    break;
                case "arrival_date":
                    arrival_date = Long.parseLong(String.valueOf(val));
                    break;
                default:
                    IO.log(TAG, IO.TAG_WARN, String.format("unknown "+getClass().getName()+" attribute '%s'", var));
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
            case "date_scheduled":
                return date_scheduled;
            case "return_date":
                return return_date;
            case "adult_count":
                return adult_count;
            case "children_count":
                return children_count;
            case "client_id":
                return getClient_id();
            case "departure_date":
                return getDeparture_date();
            case "arrival_date":
                return getArrival_date();
        }
        return super.get(var);
    }

    @Override
    public String apiEndpoint()
    {
        return "/bookings";
    }
}

