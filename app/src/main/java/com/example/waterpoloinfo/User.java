package com.example.waterpoloinfo;

import android.widget.EditText;
import android.widget.RadioGroup;

public class User {

    private String user_name;
    private String user_email;
    private String selected_account_type;

    public User(String user_name, String user_email, String selected_account_type) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.selected_account_type = selected_account_type;
    }

    public String getUser_name() {return user_name;}
    public void setUser_name(String user_name) {this.user_name = user_name;}
    public String getUser_email() {return user_email;}
    public void setUser_email(String user_email) {this.user_email = user_email;}
    public String getSelected_account_type() {return selected_account_type;}
    public void setSelected_account_type(String selected_account_type) {this.selected_account_type = selected_account_type;}
}
