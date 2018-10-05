package com.example.mahfuj.autocop;

public class RegInfo {
    String name, fname, mobNo, eMail, vehNo, imgUrl;

    public RegInfo() {
    }

    public RegInfo(String name, String fname, String mobNo, String eMail, String vehNo, String imgUrl) {
        this.name = name;
        this.fname = fname;
        this.mobNo = mobNo;
        this.eMail = eMail;
        this.vehNo = vehNo;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getVehNo() {
        return vehNo;
    }

    public void setVehNo(String vehNo) {
        this.vehNo = vehNo;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
