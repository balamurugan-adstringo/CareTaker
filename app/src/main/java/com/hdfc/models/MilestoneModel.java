package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class MilestoneModel {

    private int iMilestoneId;
    private String strMilestoneStatus;
    private String strMilestoneName;
    private String strMilestoneDate;

    private ArrayList<FieldModel> fieldModels = new ArrayList<>();

    public ArrayList<FieldModel> getFieldModels() {
        return fieldModels;
    }

    public void setFieldModel(FieldModel fieldModel) {
        this.fieldModels.add(fieldModel);
    }

    public int getiMilestoneId() {
        return iMilestoneId;
    }

    public void setiMilestoneId(int iMilestoneId) {
        this.iMilestoneId = iMilestoneId;
    }

    public String getStrMilestoneStatus() {
        return strMilestoneStatus;
    }

    public void setStrMilestoneStatus(String strMilestoneStatus) {
        this.strMilestoneStatus = strMilestoneStatus;
    }

    public String getStrMilestoneName() {
        return strMilestoneName;
    }

    public void setStrMilestoneName(String strMilestoneName) {
        this.strMilestoneName = strMilestoneName;
    }

    public String getStrMilestoneDate() {
        return strMilestoneDate;
    }

    public void setStrMilestoneDate(String strMilestoneDate) {
        this.strMilestoneDate = strMilestoneDate;
    }
}