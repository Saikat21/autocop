package com.example.mahfuj.autocop;

public class RegistrationInfo {
    String full_name;
    String father;
    String mobile_number;
    String email;
    String vehicle_no;
    String pass;

    public RegistrationInfo(String full_name, String father, String mobile_number, String email, String vehicle_no, String pass) {
        this.full_name = full_name;
        this.father = father;
        this.mobile_number = mobile_number;
        this.email = email;
        this.vehicle_no = vehicle_no;
        this.pass=pass;
    }
    public RegistrationInfo(){

    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}

