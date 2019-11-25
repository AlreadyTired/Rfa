package com.example.kimhyunwoo.runwithme;

import android.app.Application;

import com.example.kimhyunwoo.runwithme.MainActivity.LogStream;

import java.util.ArrayList;
import java.util.List;

public class UserInfo extends Application{
    private static String UserEmail="";
    private static String UserToken="";
    private static String UserNickname="";
    private static double Currentlang=35.1440851;
    private static double Currentlong=129.0355378;
    public static List<LogStream> logStreams;
    public static ArrayList<String> TemperateDateArray,No2DateArray,GasDateArray;
    public static ArrayList<Double> TemperateDataArray;
    public static ArrayList<Double> No2DataArray;
    public static ArrayList<Double> GasDataArray;

    public static double getUserLang()
    {
        return Currentlang;
    }

    public static double getUserLong()
    {
        return Currentlong;
    }

    public static void setUserNickname(String email)
    {
        UserNickname = email;
    }
    public static String getUserNickname()
    {
        return UserNickname;
    }

    public static void setUserEmail(String email)
    {
        UserEmail = email;
    }
    public static String getUserEmail()
    {
        return UserEmail;
    }

    public static void setUserToken(String token)
    {
        UserToken = token;
    }
    public static String getUserToken()
    {
        return UserToken;
    }

    public static void UserDataReset()
    {
        UserToken="";
        UserEmail="";
    }
}
