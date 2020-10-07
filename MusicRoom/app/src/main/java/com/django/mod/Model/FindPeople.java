package com.django.mod.Model;

public class FindPeople {

    public String email,username;


    public FindPeople(){

    }

    public FindPeople(String email, String username) {
            this.email = email;
            this.username = username;
        }







    public void setEmail(String email) {
        this.email = email;
    }



    public void setUsername(String username) {
        this.username = username;
    }




    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
