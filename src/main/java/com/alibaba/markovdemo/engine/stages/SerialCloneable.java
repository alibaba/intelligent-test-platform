package com.alibaba.markovdemo.engine.stages;

import java.io.*;


public  class SerialCloneable implements Cloneable,Serializable
{
    @Override
    public Object clone()
    {
        try
        {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(this);
            out.close();

            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bin);
            Object ret = in.readObject();
            in.close();
            return ret;
        } catch (Exception e)
        {
            return null;
        }
    }
}

