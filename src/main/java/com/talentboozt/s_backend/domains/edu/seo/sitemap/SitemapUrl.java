package com.talentboozt.s_backend.domains.edu.seo.sitemap;

import java.util.List;

public class SitemapUrl {
    private String loc;
    private String lastmod;
    private String changefreq;
    private double priority;
    private List<String> images;

    public SitemapUrl(String loc, String lastmod, String changefreq, double priority) {
        this.loc = loc;
        this.lastmod = lastmod;
        this.changefreq = changefreq;
        this.priority = priority;
    }

    public SitemapUrl(String loc, String lastmod, String changefreq, double priority, List<String> images) {
        this(loc, lastmod, changefreq, priority);
        this.images = images;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getLastmod() {
        return lastmod;
    }

    public void setLastmod(String lastmod) {
        this.lastmod = lastmod;
    }

    public String getChangefreq() {
        return changefreq;
    }

    public void setChangefreq(String changefreq) {
        this.changefreq = changefreq;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
