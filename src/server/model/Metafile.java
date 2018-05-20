package server.model;

import server.auxilary.AccessLevels;
import server.auxilary.IO;

/**
 * Created by ghost on 2017/02/24.
 */
public class Metafile extends MVGObject
{
    private String filename;
    private String content_type;
    private String file;//Base64 String representation of file
    public static final String TAG = "Metafile";

    public Metafile()
    {}

    public Metafile(String _id)
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
        return AccessLevels.STANDARD;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getContent_type()
    {
        return content_type;
    }

    public void setContent_type(String type)
    {
        this.content_type = type;
    }

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    @Override
    public String[] isValid()
    {
        if(getFilename()==null)
            return new String[]{"false", "invalid filename value."};
        if(getContent_type()==null)
            return new String[]{"false", "invalid content_type value."};

        return super.isValid();
    }

    @Override
    public void parse(String var, Object val)
    {
        super.parse(var, val);
        switch (var.toLowerCase())
        {
            case "description":
            case "documentDescription":
            case "document_description":
            case "fileDescription":
            case "file_description":
                setOther((String)val);
                break;
            case "path":
            case "filename":
                setFilename((String)val);
                break;
            case "contentType":
            case "content_type":
                setContent_type((String)val);
                break;
            case "file":
                setFile((String)val);
                break;
            default:
                IO.log(TAG, IO.TAG_ERROR, "unknown "+TAG+" attribute '" + var + "'");
                break;
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
                case "path":
                case "filename":
                    return getFilename();
                case "contentType":
                case "content_type":
                    return getContent_type();
                case "file":
                    return getFile();
                case "description":
                case "documentDescription":
                case "document_description":
                case "fileDescription":
                case "file_description":
                    return getOther();
                default:
                    return null;
            }
        } else return val;
    }

    @Override
    public String toString()
    {
        return super.toString() + " = "  + getFilename();
    }

    /**
     * @return this model's root endpoint URL.
     */
    @Override
    public String apiEndpoint()
    {
        return "/metafile";
    }
}