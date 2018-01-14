package server.controllers;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import server.auxilary.IO;
import server.auxilary.RemoteComms;
import server.model.Counter;

import java.util.List;

@RepositoryRestController
@RequestMapping("/counters")
public class CounterController
{
    public static long getCount(String counter_name)
    {
        List<Counter> timestamps = IO.getInstance().mongoOperations().find(new Query(Criteria.where("counter_name").is(counter_name)), Counter.class, "counters");
        if(timestamps!=null)
            if(!timestamps.isEmpty())
                return timestamps.get(0).getCount();
        return -1;
    }

    public static Counter getCounter(String counter_name)
    {
        List<Counter> timestamps = IO.getInstance().mongoOperations().find(new Query(Criteria.where("counter_name").is(counter_name)), Counter.class, "counters");
        if(timestamps!=null)
            if(!timestamps.isEmpty())
                return timestamps.get(0);
        return null;
    }

    public static Counter commitCounter(Counter counter)
    {
        if(counter!=null)
        {
            if(counter.getCounter_name()==null)
            {
                IO.log(CounterController.class.getName(), IO.TAG_ERROR, "invalid counter {name:"+counter.getCounter_name()+", count:"+counter.getCount()+"}");
                return null;
            }
            //get Counter record from DB
            Counter db_counter = getCounter(counter.getCounter_name());
            if(db_counter!=null)
            {
                //exists so update Counter
                db_counter.setCount(counter.getCount());
                IO.getInstance().mongoOperations().save(db_counter, "counters");
                IO.log(RemoteComms.class.getName(), IO.TAG_INFO, "updated counter[" +
                        counter.getCounter_name() + "] to: [" + counter.getCount() + "]\n");
                return db_counter;
            } else {
                //create new counter document
                return createCounter(counter);
            }
        } else IO.log(CounterController.class.getName(), IO.TAG_ERROR, "invalid counter.");
        return null;
    }

    public static Counter createCounter(Counter counter)
    {
        if(counter!=null)
        {
            if(counter.getCounter_name()==null)
            {
                IO.log(CounterController.class.getName(), IO.TAG_ERROR, "invalid counter {name:"+counter.getCounter_name()+", count:"+counter.getCount()+"}");
                return null;
            }
            IO.getInstance().mongoOperations().insert(counter, "counters");
            IO.log(RemoteComms.class.getName(), IO.TAG_INFO, "created new counter["+counter.getCounter_name() + "] to: [" + counter.getCount() + "]\n");
            return counter;
        }
        IO.log(CounterController.class.getName(), IO.TAG_ERROR, "invalid counter.");
        return null;
    }

    @GetMapping("/{counter_name}")
    public String getCounterRouteHandler(@RequestParam String counter_name)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Counter GET request ["+counter_name+"].");
        return String.valueOf(CounterController.getCount(counter_name));
    }
}
