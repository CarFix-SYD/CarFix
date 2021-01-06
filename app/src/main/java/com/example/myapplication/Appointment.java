package com.example.myapplication;

import java.util.Date;

public class Appointment {
    private String dateNtime;
    private String PrivateID;
    private String BusinessID;
    private String description;


    public Appointment(String datetime,String PID,String BID){
        this.dateNtime = datetime;
        this.PrivateID = PID;
        this.BusinessID = BID;
        this.description = "No description for now";

    }
    public Appointment(){}

    public String getDescription(){
        return this.description;
    }

    public void setString(String des){
        this.description = des;
    }

    public void setDate(String date) {
        this.dateNtime = date;
    }

    public void setPrivateID(String privateID) {
        this.PrivateID = privateID;
    }

    public void setBusinessID(String businessID) {
        this.BusinessID = businessID;
    }

    public String getDate() {
        return this.dateNtime;
    }

    public String getPrivateID() {
        return this.PrivateID;
    }

    public String getBusinessID() {
        return this.BusinessID;
    }



}
