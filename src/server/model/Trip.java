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
public class Trip extends MVGObject
{
    private long date_assigned;
    private String quote_id;
    private String client_id;
    private int status;

    //Getters and setters

    public long getDate_assigned() 
    {
        return date_assigned;
    }

    public void setDate_assigned(long date_assigned) 
    {
        this.date_assigned = date_assigned;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getQuote_id()
    {
        return quote_id;
    }

    public void setQuote_id(String quote_id)
    {
        this.quote_id = quote_id;
    }

    public String getClient_id()
    {
        return client_id;
    }

    public void setClient_id(String client_id)
    {
        this.client_id = client_id;
    }

    @Override
    public String[] isValid()
    {
        if(getClient_id()==null)
            return new String[]{"false", "invalid client_id value."};
        /*if(getParent_id()==null)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "invalid parent_id value.");
            return false;
        }*/
        /*if(getEnquiry_id()==null)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "invalid enquiry_id value.");
            return false;
        }*/
        if(getClient_id()==null)
            return new String[]{"false", "invalid client_id value."};
        if(getQuote_id()==null)
            return new String[]{"false", "invalid quote_id value."};
        if(getCreator()==null)
            return new String[]{"false", "invalid creator value."};
        if(getDate_assigned()<0)
            return new String[]{"false", "invalid date_assigned value."};
        if(getStatus()<0)
            return new String[]{"false", "invalid status value."};
        if(getDate_logged()<0)
            return new String[]{"false", "invalid date_logged value."};

        return super.isValid();
    }

    /**
     * Method to parse Model attribute.
     * @param var Model attribute to be parsed.
     * @param val Model attribute value to be set.
     */
    @Override
    public void parse(String var, Object val)
    {
        super.parse(var, val);
        try
        {
            switch (var.toLowerCase())
            {
                case "quote_id":
                    quote_id = (String)val;
                    break;
                case "status":
                    status = Integer.parseInt(String.valueOf(val));
                    break;
                case "date_assigned":
                    date_assigned = Long.parseLong(String.valueOf(val));
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

    /**
     * @param var Model attribute whose value is to be returned.
     * @return Model attribute value.
     */
    @Override
    public Object get(String var)
    {
        switch (var.toLowerCase())
        {
            case "quote_id":
                return getQuote_id();
            case "status":
                return getStatus();
            case "date_assigned":
                return getDate_assigned();
        }
        return super.get(var);
    }

    /**
     * @return Trip model's endpoint URL.
     */
    @Override
    public String apiEndpoint()
    {
        return "/trips";
    }
}