package com.hdfc.models;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class CustomerModel {

    private String strName = "";
    private String strPaytm = "";
    private String strImgUrl = "";
    private String strImgPath = "";
    private String strAddress = "";
    private String strContacts = "";
    private String strEmail = "";

    private int iCustomerID;

    public CustomerModel() {
    }

    public CustomerModel(String strName, String strPaytm, String strImgUrl, String strAddress,
                         String strContacts, String strEmail, int iCustomerID, String strImgPath) {
        this.strName = strName;
        this.strPaytm = strPaytm;
        this.strImgUrl = strImgUrl;
        this.strAddress = strAddress;
        this.strContacts = strContacts;
        this.strEmail = strEmail;
        this.iCustomerID = iCustomerID;
        this.strImgPath = strImgPath;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrPaytm() {
        return strPaytm;
    }

    public String getStrImgUrl() {
        return strImgUrl;
    }

    public String getStrAddress() {
        return strAddress;
    }

    public String getStrContacts() {
        return strContacts;
    }

    public String getStrEmail() {
        return strEmail;
    }

    public int getiCustomerID() {
        return iCustomerID;
    }

    public String getStrImgPath() {
        return strImgPath;
    }
}
