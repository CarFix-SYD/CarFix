package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class businessUser implements Parcelable {

    public String Email = "";
    public String businessName = "";
    public String Password = "";
    public String KindOfBusiness = "";
    public String carsToTreat ="";
    public String address = "";
    public String City = "";
    public String PhoneNumber = "";
    public String type;

    public businessUser(String Email,String businessName,String Password,String KindOfBusiness,String carsToTreat,String address,String city,String Phone){
        this.Email = Email;
        this.businessName = businessName;
        this.Password = Password;
        this.KindOfBusiness = KindOfBusiness;
        this.carsToTreat = carsToTreat;
        this.address = address;
        this.City = city;
        this.PhoneNumber = Phone;
        this.type = "BusinessUser";

    }

    public businessUser(){}


    //////parcel using
    public businessUser(Parcel in){
        String[]data = new String[9];
        in.readStringArray(data);
        this.Email = data[0];
        this.businessName = data[1];
        this.Password = data[2];
        this.KindOfBusiness = data[3];
        this.carsToTreat = data[4];
        this.address = data[5];
        this.City = data[6];
        this.PhoneNumber = data[7];
        this.type = data[8];
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.Email,this.businessName,this.Password,this.KindOfBusiness,this.carsToTreat,this.address,this.City,this.PhoneNumber,this.type});
    }

    public static final Parcelable.Creator<businessUser> CREATOR = new Creator<businessUser>() {
        @Override
        public businessUser createFromParcel(Parcel source) {
            return new businessUser(source);
        }

        @Override
        public businessUser[] newArray(int size) {
            return new businessUser[size];
        }
    };
}
