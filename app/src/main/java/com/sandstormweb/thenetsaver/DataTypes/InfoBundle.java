package com.sandstormweb.thenetsaver.DataTypes;

import java.util.ArrayList;

public class InfoBundle
{
    private ArrayList<InfoItem> data = new ArrayList();

    public InfoBundle(ArrayList<InfoItem> data) {
        this.data = data;
    }

    public synchronized InfoItem getInfoItemByName(String name)
    {
        try{
            for(int i = 0; i < data.size(); i++)
            {
                if(data.get(i).getName().equals(name)) return data.get(i);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public synchronized String getContentByName(String name)
    {
        try{
            for(int i = 0; i < data.size(); i++)
            {
                if(data.get(i).getName().equals(name)) return data.get(i).getContent();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void setContentByName(String name, String content)
    {
        try{
            for(int i = 0; i < data.size(); i++)
            {
                if(data.get(i).getName().equals(name)) data.get(i).setContent(content);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public synchronized int getSize()
    {
        try{
            return  this.data.size();
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public synchronized InfoItem getInfoItem(int index)
    {
        try{
            return data.get(index);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
