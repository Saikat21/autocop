package com.example.mahfuj.autocop;

public class RegistrationInfo {

    String first_name;
    String last_name;
    String phone_no;
    String email;
    String registration_no;

    public RegistrationInfo(String first_name, String last_name, String phone_no, String email) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_no = phone_no;
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistration_no() {
        return registration_no;
    }

    public void setRegistration_no(String registration_no) {
        this.registration_no = registration_no;
    }

    public RegistrationInfo(String first_name, String last_name, String phone_no, String email, String registration_no) {

        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_no = phone_no;
        this.email = email;
        this.registration_no = registration_no;
    }

    public RegistrationInfo() {

    }
}
