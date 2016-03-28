package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class FeedBackModel implements Serializable {

    private String strFeedBackMessage;
    private String strFeedBackBy;
    private String strFeedBackTime;
    private String strFeedBackByType;

    private int intFeedBackRating;
    private String strFeedBackID;

    private boolean bFeedBackReport;

    public FeedBackModel(String strFeedBackMessage, String strFeedBackBy, int intFeedBackRating,
                         boolean bFeedBackReport, String strFeedBackTime, String strFeedBackByType,
                         String strFeedBackID) {
        this.strFeedBackMessage = strFeedBackMessage;
        this.strFeedBackBy = strFeedBackBy;
        this.intFeedBackRating = intFeedBackRating;
        this.bFeedBackReport = bFeedBackReport;
        this.strFeedBackTime = strFeedBackTime;
        this.strFeedBackByType = strFeedBackByType;
        this.strFeedBackID = strFeedBackID;
    }

    public String getStrFeedBackMessage() {
        return strFeedBackMessage;
    }

    public String getStrFeedBackBy() {
        return strFeedBackBy;
    }

    public int getIntFeedBackRating() {
        return intFeedBackRating;
    }

    public boolean getBoolFeedBackReport() {
        return bFeedBackReport;
    }

    public String getStrFeedBackByType() {
        return strFeedBackByType;
    }

    public String getStrFeedBackTime() {
        return strFeedBackTime;
    }

    public String getStrFeedBackID() {
        return strFeedBackID;
    }

    public boolean isbFeedBackReport() {
        return bFeedBackReport;
    }
}
