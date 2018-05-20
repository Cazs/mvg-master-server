/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.auxilary;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import server.controllers.CounterController;
import server.exceptions.InvalidMVGObjectException;
import server.model.MVGObject;
import server.model.Counter;
import server.model.Metafile;
import server.model.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;

/**
 *
 * @author ghost
 */
public class RemoteComms
{
    public static String host = "http://localhost:8080"; // "http://192.168.0.103:8083";//192.168.0.103//95.85.57.110
    public static final String TAG = "RemoteComms";
    public static String DB_IP = "localhost";
    public static int DB_PORT = 27017;
    public static String DB_NAME = "mvg";
    public static int TTL = 60*60*2;//2 hours in sec
    public static String SYSTEM_EMAIL = "no-reply@travelmvg.co.za";

    public static void setHost(String h)
    {
        host = h;
    }

    public static String commitToDatabase(MVGObject MVGObject, String collection, String timestamp_name)
    {
        if(MVGObject !=null)
        {
            String[] is_valid = MVGObject.isValid();
            if(is_valid==null)
            {
                IO.log(RemoteComms.class.getName(),IO.TAG_INFO, "invalid isValid() response from MVGObject{"+ MVGObject
                        .getClass().getName()+"}");
                return null;
            }
            if(is_valid.length!=2)
            {
                IO.log(RemoteComms.class.getName(),IO.TAG_INFO, "invalid isValid() response array length from MVGObject{"+ MVGObject
                        .getClass().getName()+"}");
                return null;
            }
            if (is_valid[0].toLowerCase().equals("true"))
            {
                IO.log(RemoteComms.class.getName(),IO.TAG_INFO, is_valid[1]);//print message from isValid() call
                IO.log(RemoteComms.class.getName(),IO.TAG_INFO, "committing MVGObject{"+ MVGObject
                        .getClass().getName()+"}: "+ MVGObject.toString());
                //commit MVGObject data to DB server
                if(collection!=null)
                    IO.getInstance().mongoOperations().save(MVGObject, collection);
                else return null;
                IO.log(RemoteComms.class.getName(),IO.TAG_INFO, "committed MVGObject:{"+ MVGObject
                        .getClass().getName()+"} ["+ MVGObject.get_id()+"]");
                //update respective timestamp
                if(timestamp_name!=null)
                    CounterController.commitCounter(new Counter(timestamp_name, System.currentTimeMillis()));
                else return null;
                return MVGObject.get_id();
            } else throw new InvalidMVGObjectException(is_valid[1]);
        } else throw new InvalidMVGObjectException("invalid[null] MVGObject.");
    }

    /**
     *
     * @param subject email subject
     * @param message email message
     * @param recipient_users recipient Employees
     * @param fileMetadata email attachment files
     * @return Mailjet email send response object
     * @throws MailjetSocketTimeoutException
     * @throws MailjetException
     */
    public static MailjetResponse emailWithAttachment(String subject, String message, User[] recipient_users, Metafile[] fileMetadata) throws MailjetSocketTimeoutException, MailjetException
    {
        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;

        //setup recipients
        JSONArray recipients = new JSONArray();
        for(User recipient: recipient_users)
            recipients.put(new JSONObject()
                    .put("Email", recipient.getEmail())
                    .put("Name", recipient.getFirstname()+" "+recipient.getLastname()));

        //setup files to be emailed
        JSONArray files = new JSONArray();
        for(Metafile file: fileMetadata)
            files.put(new JSONObject()
                    .put("ContentType", file.getContent_type())
                    .put("Filename", file.getFilename())
                    .put("Base64Content", file.getFile()));//"VGhpcyBpcyB5b3VyIGF0dGFjaGVkIGZpbGUhISEK"


        client = new MailjetClient("f8d3d1d74c95250bb2119063b3697082", "8304b30da4245632c878bf48f1d65d92", new ClientOptions("v3.1"));
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", SYSTEM_EMAIL)
                                        .put("Name", "MVG"))
                                .put(Emailv31.Message.TO, recipients)
                                .put(Emailv31.Message.SUBJECT, subject)
                                .put(Emailv31.Message.HTMLPART, message)
                                .put(Emailv31.Message.ATTACHMENTS, files)));
        response = client.post(request);
        IO.log(RemoteComms.class.getName(), IO.TAG_INFO, response.getStatus()+": " +response.getData());
        return response;
    }

    /**
     *
     * @param subject email subject
     * @param message email message
     * @param recipient_addresses recipient email addresses
     * @param fileMetadata email attachment files
     * @return Mailjet email send response object
     * @throws MailjetSocketTimeoutException
     * @throws MailjetException
     */
    public static MailjetResponse emailWithAttachment(String subject, String message, String[] recipient_addresses, Metafile[] fileMetadata) throws MailjetSocketTimeoutException, MailjetException
    {
        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;

        //setup recipients
        JSONArray recipients_json = new JSONArray();
        for(String recipient:recipient_addresses)
        {
            recipients_json.put(new JSONObject()
                    .put("Email", recipient));
            //.put("Name", recipient));//recipient.getFirstname()+" "+recipient.getLastname()
        }

        //setup files to be emailed
        JSONArray files = new JSONArray();
        for(Metafile file: fileMetadata)
            files.put(new JSONObject()
                    .put("ContentType", file.getContent_type())
                    .put("Filename", file.getFilename())
                    .put("Base64Content", file.getFile()));//"VGhpcyBpcyB5b3VyIGF0dGFjaGVkIGZpbGUhISEK"


        client = new MailjetClient("f8d3d1d74c95250bb2119063b3697082", "8304b30da4245632c878bf48f1d65d92", new ClientOptions("v3.1"));
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", SYSTEM_EMAIL)
                                        .put("Name", "BMS"))
                                .put(Emailv31.Message.TO, recipients_json)
                                .put(Emailv31.Message.SUBJECT, subject)
                                //.put(Emailv31.Message.TEXTPART, "Dear passenger 1, welcome to Mailjet! May the delivery force be with you!")
                                .put(Emailv31.Message.HTMLPART, message)
                                .put(Emailv31.Message.ATTACHMENTS, files)));
        response = client.post(request);
        IO.log(RemoteComms.class.getName(), IO.TAG_INFO, String.valueOf(response.getStatus()));
        IO.log(RemoteComms.class.getName(), IO.TAG_INFO, String.valueOf(response.getData()));
        return response;
    }
}
