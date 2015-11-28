package com.sandstormweb.thenetsaver.DataTypes;

public class InfoItem
{
    private String name;
    private String content;

    public InfoItem(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
