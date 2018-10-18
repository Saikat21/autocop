package com.example.mahfuj.autocop;

public class profile_info {
    private String etEmail, etFirstName, etLastName, etPhn, etReg;

    public profile_info() {
    }

    public String getEtEmail() {
        return etEmail;
    }

    public void setEtEmail(String etEmail) {
        this.etEmail = etEmail;
    }

    public String getEtFirstName() {
        return etFirstName;
    }

    public void setEtFirstName(String etFirstName) {
        this.etFirstName = etFirstName;
    }

    public String getEtLastName() {
        return etLastName;
    }

    public void setEtLastName(String etLastName) {
        this.etLastName = etLastName;
    }

    public String getEtPhn() {
        return etPhn;
    }

    public void setEtPhn(String etPhn) {
        this.etPhn = etPhn;
    }

    public String getEtReg() {
        return etReg;
    }

    public void setEtReg(String etReg) {
        this.etReg = etReg;
    }

    public profile_info(String etEmail, String etFirstName, String etLastName, String etPhn, String etReg) {
        this.etEmail = etEmail;
        this.etFirstName = etFirstName;
        this.etLastName = etLastName;
        this.etPhn = etPhn;
        this.etReg = etReg;

    }
}
