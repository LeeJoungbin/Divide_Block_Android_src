package com.pnu.cse.termspring2018;

public class User {
    private static User instance;
    private static boolean isLogin = false;
    private String userName = "";

    public static User getInstance() {

        if(instance == null) {
            instance = new User();
        }

        return instance;
    }

    public void setUser(String name) {
        userName = name;
        isLogin = true;
    }

    public static boolean isLogin() {
        return isLogin;
    }

    public String getUserName() {
        return userName;
    }
}
