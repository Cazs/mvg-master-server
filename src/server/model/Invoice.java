/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

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
    private double receivable;
    private int status;

    public double getReceivable()
    {
        return receivable;
    }

    public String getClient_id()
    {
        return client_id;
    }

    public void setClient_id(String client_id)
    {
        this.client_id = client_id;
    }

    public void setReceivable(double receivable)
    {
        this.receivable = receivable;
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

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    @Override
    public String[] isValid()
    {
        if(getTrip_id()==null)
            return new String[]{"false", "invalid trip_id value."};
        if(getClient_id()==null)
            return new String[]{"false", "invalid client_id value."};
        if(getReceivable()<0)
            return new String[]{"false", "invalid receivable value."};

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
                case "status":
                    status = Integer.parseInt(String.valueOf(val));
                    break;
                case "client_id":
                    setClient_id(String.valueOf(val));
                    break;
                case "creator":
                    setCreator(String.valueOf(val));
                    break;
                case "receivable":
                    setReceivable(Double.valueOf(String.valueOf(val)));
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
                case "receivable":
                    return getReceivable();
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
