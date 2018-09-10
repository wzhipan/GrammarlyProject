package com.zhipan.grammarly.data.model;

import java.util.ArrayList;
import java.util.List;

public class Contact extends Avatar {

    private String displayName;
    private List<String> phoneNumberList;
    private List<String> emailList;

    public Contact(String id) {
        super(id);
        phoneNumberList = new ArrayList<>();
        emailList = new ArrayList<>();
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getPhoneNumberList() {
        return phoneNumberList;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhoneNumberList(List<String> phoneNumberList) {
        this.phoneNumberList = phoneNumberList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }
}
