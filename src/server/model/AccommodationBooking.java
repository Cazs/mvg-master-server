package server.model;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import server.auxilary.IO;
import java.util.List;

/**
 *
 * @author ghost
 */
public class AccommodationBooking extends Booking
{
    private String accommodation_destination_id;
    public static final String TAG = "AccommodationBooking";

    public AccommodationBooking()
    {}

    public AccommodationBooking(String _id)
    {
        super(_id);
    }

    public String getAccommodation_destination_id()
    {
        return accommodation_destination_id;
    }

    public void setAccommodation_destination_id(String accommodation_destination_id)
    {
        this.accommodation_destination_id = accommodation_destination_id;
    }

    public AccommodationDestination getAccommodationDestination()
    {
        //get User from this Session object
        List<AccommodationDestination> accommodationDestinations = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(getAccommodation_destination_id())), AccommodationDestination.class, "accommodation_destinations");
        if(accommodationDestinations==null)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "getAccommodationDestination()> could not find a user associated with the username ["+getAccommodation_destination_id()+"]");
            return null;
        }
        if(accommodationDestinations.size()!=1)//should never happen
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "getAccommodationDestination()> could not find a valid accommodation destinations ["+getAccommodation_destination_id()+"]");
            return null;
        }

        return accommodationDestinations.get(0);
    }

    @Override
    public String[] isValid()
    {
        if(getAccommodation_destination_id() == null || getAccommodation_destination_id().isEmpty())
            return new String[]{"false", "invalid accommodation destination."};

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
                case "accommodation_destination_id":
                    setAccommodation_destination_id((String)val);
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
            case "accommodation_destination_id":
                return accommodation_destination_id;
        }
        return super.get(var);
    }

    @Override
    public String toString()
    {
        return "booked accommodation: " + getAccommodation_destination_id() + " with " + getChildren_count() + " children and " + getAdult_count() + " adults returning on " + getReturn_date() + " days.";
    }

    @Override
    public String apiEndpoint()
    {
        return "/bookings/accommodation";
    }
}

