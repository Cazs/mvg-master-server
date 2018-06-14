package server.model;

import server.auxilary.IO;

/**
 *
 * @author ghost
 */
public class TripBooking extends Booking
{
    private String pickup_location;
    private String destination;
    private String trip_type;
    private long date_driver_assigned; // to driver
    private final String TRIP_TYPE_ONE_WAY = "One-Way";
    private final String TRIP_TYPE_RETURN = "Return";
    public static final String TAG = "TripBooking";

    public TripBooking()
    {}

    public TripBooking(String _id)
    {
        super(_id);
    }

    public String getPickup_location()
    {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location)
    {
        this.pickup_location = pickup_location;
    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public String getTrip_type()
    {
        return trip_type;
    }

    /* public String getTripType()
    {
        return getTrip_type() == TRIP_TYPE_ONE_WAY ? "One-Way" : "Return";
    } */

    public void setTrip_type(String trip_type)
    {
        this.trip_type = trip_type;
    }

    public long getDate_driver_assigned()
    {
        return date_driver_assigned;
    }

    public void setDate_driver_assigned(long date_driver_assigned)
    {
        this.date_driver_assigned = date_driver_assigned;
    }

    @Override
    public String[] isValid()
    {
        if(getPickup_location() == null || getPickup_location().isEmpty())
            return new String[]{"false", "invalid pickup address."};
        if(getDestination() == null || getPickup_location().isEmpty())
            return new String[]{"false", "invalid destination address."};
        if(getTrip_type() == null || getTrip_type().isEmpty())
            return new String[]{"false", "invalid trip_type."};
        if(getTrip_type().equals(TRIP_TYPE_RETURN) && getReturn_date() <= 0)
            return new String[]{"false", "invalid return date, scheduled date cannot be after the return date."};
        if(getTrip_type().equals(TRIP_TYPE_RETURN) && getReturn_date() < getDate_scheduled())
            return new String[]{"false", "invalid return date, return date cannot be before the scheduled date."};
        if(getDate_driver_assigned() < 0)
            return new String[]{"false", "invalid date_driver_assigned value."};

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
                case "pickup_location":
                    setPickup_location((String)val);
                    break;
                case "destination":
                    setDestination((String)val);
                    break;
                case "trip_type":
                    setTrip_type((String)val);
                    break;
                case "date_driver_assigned":
                    date_driver_assigned = Long.parseLong(String.valueOf(val));
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
            case "pickup_location":
                return pickup_location;
            case "destination":
                return destination;
            case "trip_type":
                return trip_type;
            case "date_driver_assigned":
                return getDate_driver_assigned();
        }
        return super.get(var);
    }

    @Override
    public String toString()
    {
        //return String.format("[id = %s, firstname = %s, lastname = %s]", get_id(), getFirstname(), getLastname());
        return "{"+(get_id()==null?"":"\"_id\":\""+get_id()+"\", ")+
                ",\"client_id\":\""+getClient_id()+"\""+
                ",\"destination\":\""+getDestination()+"\""+
                ",\"pickup_location\":\""+getPickup_location()+"\""+
                ",\"trip_type\":\""+getTrip_type()+"\""+
                ",\"date_scheduled\":\""+getDate_scheduled()+"\""+
                ",\"creator\":\""+getCreator()+"\""+
                ",\"date_logged\":\""+getDate_logged()+"\""+
                "\"other\":\""+getOther()+"\"}";
    }

    @Override
    public String apiEndpoint()
    {
        return "/bookings/trips";
    }
}

