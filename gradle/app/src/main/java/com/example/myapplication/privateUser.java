package com.example.myapplication;

public class privateUser {
    public String email,Password,carCompany,CarNumber,CarYear,type;

    public  privateUser(String Email,String password,String carcompany,String carnumber,String caryear){
        this.email = Email;
        this.Password= password;
        this.carCompany = carcompany;
        this.CarNumber = carnumber;
        this.CarYear = caryear;
        this.type = "PrivateUser";
    }
}
