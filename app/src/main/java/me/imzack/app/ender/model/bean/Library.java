package me.imzack.app.ender.model.bean;

public class Library {

    private String mName;
    private String mDeveloper;
    private String mLink;

    public Library(String name, String developer, String link) {
        mName = name;
        mDeveloper = developer;
        mLink = link;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDeveloper() {
        return mDeveloper;
    }

    public void setDeveloper(String developer) {
        mDeveloper = developer;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }
}
