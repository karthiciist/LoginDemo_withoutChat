package com.example.gaayathri.logindemo.model;

import java.util.HashMap;
import java.util.Map;

public class NoteHomeHorizontal {

    private String id;
    private String entryName;
    private String title;
    private String author;
    private String degree;
    private String specialization;
    private String mrp;
    private String price;
    private String location;
    private String timestamp;
    private String user;
    private String sellerMsg;
    private String downloadUri;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSellerMsg() {
        return sellerMsg;
    }

    public void setSellerMsg(String sellerMsg) {
        this.sellerMsg = sellerMsg;
    }

    public NoteHomeHorizontal(String id, String entryName, String title, String author, String degree, String specialization, String mrp, String price, String location, String timestamp, String user, String sellerMsg, String uid) {
        this.id = id;
        this.entryName = entryName;
        this.title = title;
        this.author = author;
        this.degree = degree;
        this.specialization = specialization;
        this.mrp = mrp;
        this.price = price;
        this.location = location;
        this.timestamp = timestamp;
        this.user = user;
        this.sellerMsg = sellerMsg;
        this.downloadUri = downloadUri;
        this.uid = uid;
    }

    public NoteHomeHorizontal() {
    }

    public NoteHomeHorizontal(String entryName, String title, String author, String degree, String specialization, String mrp, String price, String location, String timestamp, String user, String sellerMsg, String uid) {
        this.entryName = entryName;
        this.title = title;
        this.author = author;
        this.degree = degree;
        this.specialization = specialization;
        this.mrp = mrp;
        this.price = price;
        this.location = location;
        this.timestamp = timestamp;
        this.user = user;
        this.sellerMsg = sellerMsg;
        this.downloadUri = downloadUri;
        this.uid = uid;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("title", this.title);
        result.put("author", this.author);
        result.put("degree", this.degree);
        result.put("specialization", this.specialization);
        result.put("mrp", this.mrp);
        result.put("location", this.location);
        result.put("timestamp", this.timestamp);
        result.put("user", this.user);
        result.put("price", this.price);
        result.put("entryName", this.entryName);
        result.put("sellerMsg", this.sellerMsg);
        result.put("downloadUri", this.downloadUri);
        result.put("uid", this.uid);


        return result;
    }

}
