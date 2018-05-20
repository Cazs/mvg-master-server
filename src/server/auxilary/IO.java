package server.auxilary;

import org.springframework.data.mongodb.core.MongoOperations;
import server.AppConfig;
import server.model.MVGObject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Arrays;

/**
 * Created by ghost on 2017/01/28.
 */
public class IO<T extends MVGObject>
{
    public static final String TAG_VERBOSE = "verbose";
    public static final String TAG_INFO = "info";
    public static final String TAG_WARN = "warning";
    public static final String TAG_ERROR = "error";
    private static final String TAG = "IO";
    private MongoOperations mongoOperations;
    private static IO io = new IO();

    private IO()
    {
        try
        {
            mongoOperations = new AppConfig().mongoOperations();
        } catch (UnknownHostException e)
        {
            IO.log("UnknownHost Error", IO.TAG_ERROR, e.getMessage());
        }
    }

    public static IO getInstance(){return io;}

    public MongoOperations mongoOperations(){return mongoOperations;}

    public static String generateRandomString(int len)
    {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String str="";
        for(int i=0;i<len;i++)
            str+=chars.charAt((int)(Math.floor(Math.random()*chars.length())));
        return str;
    }

    public static AbstractMap.SimpleEntry<Integer, LocalDateTime> isEpochSecondOrMilli(long epoch_date)
    {
        switch (String.valueOf(epoch_date).length())
        {
            case 10: // is using epoch seconds
                return new AbstractMap.SimpleEntry<>(0, LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch_date), ZoneId.systemDefault()));
            case 13: // is using epoch millis
                return new AbstractMap.SimpleEntry<>(1, LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch_date), ZoneId.systemDefault()));
            default:
                IO.log(IO.class.getName(), IO.TAG_ERROR, "unknown date format ["+epoch_date+"] - should be epoch millis or epoch seconds.");
                return new AbstractMap.SimpleEntry<>(3, LocalDateTime.of(1970, Month.JANUARY.getValue(), 1, 0, 0));
        }
    }

    public static String getYyyyMMddFormmattedDate(LocalDateTime date)
    {
        return date.getYear() +
                "-" + (date.getMonth().getValue() >= 10 ? date.getMonth().getValue() : "0" + String.valueOf((date.getMonth().getValue()))) +
                "-" + (date.getDayOfMonth() >= 10 ? date.getDayOfMonth() : "0" + String.valueOf(date.getDayOfMonth()));
    }

    public static String getEncryptedHexString(String message) throws Exception
    {
        StringBuilder str = new StringBuilder();
        for(byte b: hash(message))
            str.append(Integer.toHexString(0xFF & b));
        return str.toString();
    }

    //TODO: use blowfish/bcrypt
    public static byte[] hash(String plaintext) throws Exception
    {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(plaintext.getBytes());
        return m.digest();
    }

    //TODO: use blowfish/bcrypt
    public static byte[] encrypt(String digest, String message) throws Exception
    {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(digest.getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = message.getBytes("utf-8");
        final byte[] cipherText = cipher.doFinal(plainTextBytes);

        return cipherText;
    }

    //TODO: use blowfish/bcrypt
    public static String decrypt(String digest, byte[] message) throws Exception
    {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(digest.getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        for (int j = 0, k = 16; j < 8;)
        {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        final byte[] plainText = decipher.doFinal(message);

        return new String(plainText, "UTF-8");
    }

    public void quickSort(T arr[], int left, int right, String comparator)
    {
        int index = partition(arr, left, right, comparator);
        if (left < index - 1)
            quickSort(arr, left, index - 1, comparator);
        if (index < right)
            quickSort(arr, index, right, comparator);
    }

    public int partition(T arr[], int left, int right, String comparator) throws ClassCastException
    {
        int i = left, j = right;
        T tmp;
        double pivot = (Double) arr[(left + right) / 2].get(comparator);

        while (i <= j)
        {
            while ((Double) arr[i].get(comparator) < pivot)
                i++;
            while ((Double) arr[j].get(comparator) > pivot)
                j--;
            if (i <= j)
            {
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        }
        return i;
    }

    public static void writeAttributeToConfig(String key, String value) throws IOException
    {
        //TO_Consider: add meta data for [key,value] to meta records.
        File f = new File("config.cfg");
        StringBuilder result = new StringBuilder();
        boolean rec_found=false;
        if(f.exists())
        {
            String s = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            int line_read_count=0;
            while ((s = in.readLine())!=null)
            {
                if(s.contains("="))
                {
                    String k = s.split("=")[0];
                    String val = s.split("=")[1];
                    //If the record exists, change it
                    if(k.equals(key))
                    {
                        val = value;//Update record value
                        rec_found=true;
                    }
                    result.append(k+"="+val+"\n");//Append existing record.
                    line_read_count++;
                } else IO.log(TAG, IO.TAG_ERROR, "Config file may be corrupted.");
            }
            if(!rec_found)//File exists but no key was found - write new line.
                result.append(key+"="+value+"\n");
            /*if(in!=null)
                in.close();*/
        } else result.append(key+"="+value+"\n");//File DNE - write new line.

        IO.log(TAG, IO.TAG_INFO, "writing attribute to config: " + key + "=" + value);

        /*if(!rec_found)//File exists but record doesn't exist - create new record
            result.append(key+"="+value+"\n");*/

        //Write to disk.
        PrintWriter out = new PrintWriter(f);
        out.print(result);
        out.flush();
        out.close();
    }

    public static String readAttributeFromConfig(String key) throws IOException
    {
        File f = new File("config.cfg");
        if(f.exists())
        {
            String s = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            while ((s = in.readLine())!=null)
            {
                if(s.contains("="))
                {
                    String var = s.split("=")[0];
                    String val = s.split("=")[1];
                    if(var.equals(key))
                    {
                        /*if(in!=null)
                            in.close();*/
                        return val;
                    }
                }
            }
        }
        return null;
    }

    public static String readStream(InputStream stream) throws IOException
    {
        //Get message from input stream
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        if(in!=null)
        {
            StringBuilder msg = new StringBuilder();
            String line;
            while ((line = in.readLine())!=null)
            {
                msg.append(line + "\n");
            }
            in.close();

            return msg.toString();
        }else IO.log(TAG, IO.TAG_ERROR, "could not read error stream from server response.");
        return null;
    }

    public static void log(String src, String tag, String msg)
    {
        switch (tag.toLowerCase())
        {
            case TAG_VERBOSE:
                if (Globals.DEBUG_VERBOSE.getValue().toLowerCase().equals("on"))
                    System.out.println(String.format("%s> %s: %s", src, tag, msg));
                break;
            case TAG_INFO:
                if (Globals.DEBUG_INFO.getValue().toLowerCase().equals("on"))
                    System.out.println(String.format("%s> %s: %s", src, tag, msg));
                break;
            case TAG_WARN:
                if (Globals.DEBUG_WARNINGS.getValue().toLowerCase().equals("on"))
                    System.out.println(String.format("%s> %s: %s", src, tag, msg));
                break;
            case TAG_ERROR:
                if (Globals.DEBUG_ERRORS.getValue().toLowerCase().equals("on"))
                    System.err.println(String.format("%s> %s: %s", src, tag, msg));
                break;
            default://fallback for custom tags
                System.out.println(String.format("%s> %s: %s", src, tag, msg));
                break;
        }
    }
}
