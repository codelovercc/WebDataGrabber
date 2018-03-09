package com.cool.grabber;

/**
 * Created by codelover on 18/3/7.
 */
public class GrabberConfig {
    private String name;
    private String beanName;
    private String startLink;
    private String referer;

    public GrabberConfig() {
    }

    public GrabberConfig(String name, String beanName, String startLink, String referer) {
        this.name = name;
        this.beanName = beanName;
        this.startLink = startLink;
        this.referer = referer;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getStartLink() {
        return startLink;
    }

    public void setStartLink(String startLink) {
        this.startLink = startLink;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
