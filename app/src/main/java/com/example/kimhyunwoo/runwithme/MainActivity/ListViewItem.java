package com.example.kimhyunwoo.runwithme.MainActivity;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private Drawable iconDrawble;
    private String Nickname;
    private String Email;

    public void setIcon(Drawable icon)
    {
        iconDrawble = icon;
    }
    public void setNickname(String nickname)
    {
        Nickname = nickname;
    }
    public void setEmail(String email)
    {
        Email = email;
    }

    public Drawable getIcon()
    {
        return this.iconDrawble;
    }
    public String getNickname()
    {
        return this.Nickname;
    }
    public String getEmail()
    {
        return this.Email;
    }
}
