package com.example.reservation2;

public class Reservation {
    public String email;

    public String time;

    public String number;

    public String name;

    public Reservation(){}

    public Reservation( String email, String name,  String number , String time){
        this.email = email;
        this.time = time;
        this.number = number;
        this.name = name;
    }
}
