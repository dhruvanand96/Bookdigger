package com.example.hp.bookseller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by HP on 09-08-2017.
 */

public class UserBean implements Serializable
{

    int id;
    String name;
    String phone;
    String email;
    String username;
    String password;
    String collegename;
    String traitname;
    ArrayList<String> chats;

    public ArrayList<String> getChats()
    {

        return chats;
    }

    public UserBean(int id, String name, String phone, String email,
                    String username, String password, String collegename,
                    String traitname, String uid, String token) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.collegename = collegename;
        this.traitname = traitname;
        this.uid = uid;
        this.token = token;
    }

    @Override
    public String toString()
    {
        return "UserBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", collegename='" + collegename + '\'' +
                ", traitname='" + traitname + '\'' +
                ", chats=" + String.valueOf(chats) +
                ", uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public void setChats(ArrayList<String> chats)
    {
        this.chats = chats;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    String uid;



    String token;

    public UserBean()
    {

    };

    public UserBean(int id, String name, String phone, String email, String username, String password, String collegename, String traitname) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.collegename = collegename;
        this.traitname = traitname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCollegename() {
        return collegename;
    }

    public void setCollegename(String collegename) {
        this.collegename = collegename;
    }

    public String getTraitname() {
        return traitname;
    }

    public void setTraitname(String traitname) {
        this.traitname = traitname;
    }



}
