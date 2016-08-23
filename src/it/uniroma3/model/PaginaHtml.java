package it.uniroma3.model;

import org.bson.Document;

/**
 * Created by Domenico on 23/08/16.
 */
public class PaginaHtml {

    private String url;
    private String urlVero;
    private String html;
    private String testo;
    private Document tagMe;
    private String topic;

    public Document getBsonDocument() {
        Document doc = new Document()
            .append("url", this.url)
            .append("urlVero", this.urlVero)
            .append("html", this.html)
            .append("testo", this.testo)
            .append("tagMe", this.tagMe)
            .append("topic", this.topic);
        return doc;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlVero() {
        return this.urlVero;
    }

    public void setUrlVero(String urlVero) {
        this.urlVero = urlVero;
    }

    public String getHtml() {
        return this.html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getTesto() {
        return this.testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Document getTagMe() {
        return this.tagMe;
    }

    public void setTagMe(Document tagMe) {
        this.tagMe = tagMe;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
