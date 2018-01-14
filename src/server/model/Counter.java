package server.model;

import org.springframework.data.annotation.Id;

/**
 * Created by ghost on 2017/09/14.
 */
public class Counter
{
    @Id
    private String _id;
    private String counter_name;
    private long count;

    public Counter(String counter_name, long count)
    {
        setCounter_name(counter_name);
        setCount(count);
    }

    public String get_id()
    {
        return _id;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public String getCounter_name()
    {
        return counter_name;
    }

    public void setCounter_name(String counter_name)
    {
        this.counter_name = counter_name;
    }

    public long getCount()
    {
        return count;
    }

    public void setCount(long count)
    {
        this.count = count;
    }

    @Override
    public String toString()
    {
        return "{\"counter_name\":\""+counter_name+"\", "+
                "\"count\":\""+count+"\"}";
    }
}
