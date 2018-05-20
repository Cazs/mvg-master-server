/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import server.auxilary.AccessLevels;
import server.auxilary.IO;

/**
 *
 * @author ghost
 */
public class Invoice extends MVGObject
{
    private String trip_id;
    private String client_id;
    private String quote_id;
    private double cash_received;

    public Invoice()
    {}

    public Invoice(String _id)
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
        return AccessLevels.ADMIN;
    }

    public double getCash_received()
    {
        return cash_received;
    }

    public String getClient_id()
    {
        return client_id;
    }

    public void setClient_id(String client_id)
    {
        this.client_id = client_id;
    }

    public void setCash_received(double cash_received)
    {
        this.cash_received = cash_received;
    }

    public String getTrip_id()
    {
        return trip_id;
    }

    public void setTrip_id(String trip_id)
    {
        this.trip_id = trip_id;
    }

    public String getQuote_id()
    {
        return quote_id;
    }

    public void setQuote_id(String quote_id)
    {
        this.quote_id = quote_id;
    }

    @Override
    public String[] isValid()
    {
        if(getTrip_id()==null)
            return new String[]{"false", "invalid trip_id value."};
        if(getClient_id()==null)
            return new String[]{"false", "invalid client_id value."};
        if(getCash_received()<0)
            return new String[]{"false", "invalid cash_received value."};

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
                case "trip_id":
                    setTrip_id(String.valueOf(val));
                    break;
                case "quote_id":
                    setQuote_id(String.valueOf(val));
                    break;
                case "client_id":
                    setClient_id(String.valueOf(val));
                    break;
                case "creator":
                    setCreator(String.valueOf(val));
                    break;
                case "cash_received":
                    setCash_received(Double.valueOf(String.valueOf(val)));
                    break;
                default:
                    IO.log(getClass().getName(), IO.TAG_ERROR, "unknown "+getClass().getName()+" attribute '" + var + "'.");
                    break;
            }
        }catch (NumberFormatException e)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public Object get(String var)
    {
        Object val = super.get(var);
        if(val==null)
        {
            switch (var.toLowerCase())
            {
                case "trip_id":
                    return getTrip_id();
                case "client_id":
                    return getClient_id();
                case "quote_id":
                    return getQuote_id();
                case "status":
                    return getStatus();
                case "cash_received":
                    return getCash_received();
                default:
                    IO.log(getClass().getName(), IO.TAG_ERROR, "unknown " + getClass()
                            .getName() + " attribute '" + var + "'.");
                    return null;
            }
        } else return val;
    }

    @Override
    public String apiEndpoint()
    {
        return "/invoices";
    }
}
