package com.example.mahfuj.autocop;

public class OverSpeed {

    double speed;
    long time;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public OverSpeed() {

    }

    public OverSpeed(double speed, long time) {

        this.speed = speed;
        this.time = time;
    }
}
