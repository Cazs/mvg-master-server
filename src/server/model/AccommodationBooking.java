package server.model;

import server.auxilary.IO;

/**
 *
 * @author ghost
 */
public class AccommodationBooking extends Booking
{
    private String accommodation_id;
    public static final String TAG = "AccommodationBooking";

    public AccommodationBooking()
    {}

    public AccommodationBooking(String _id)
    {
        super(_id);
    }

    public String getAccommodation_id()
    {
        return accommodation_id;
    }

    public void setAccommodation_id(String accommodation_id)
    {
        this.accommodation_id = accommodation_id;
    }


    @Override
    public void parse(String var, Object val)
    {
        super.parse(var, val);
        try
        {
            switch (var.toLowerCase())
            {
                case "accommodation_id":
                    setAccommodation_id((String)val);
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
            case "accommodation_id":
                return accommodation_id;
        }
        return super.get(var);
    }

    @Override
    public String toString()
    {
        return "booked accommodation: " + getAccommodation_id() + " with " + getChildren_count() + " children and " + getAdult_count() + " adults returning on " + getReturn_date() + " days.";
    }

    @Override
    public String apiEndpoint()
    {
        return "/bookings/accommodation";
    }
}

