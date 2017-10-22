package io.github.mario_kang.hclientandroid;

import android.graphics.drawable.Drawable;

class ListViewItem {
    private Drawable iconDrawable;
    private String str;
    private String str2;
    private String str3;
    private String str4;
    private String str5;
    private String str6;
    private int type;

    void setIcon(Drawable icon) {
        iconDrawable = icon;
    }

    void setStr(String _str) {
        str = _str;
    }

    void setStr2(String _str) {
        str2 = _str;
    }

    void setStr3(String _str) {
        str3 = _str;
    }

    void setStr4(String _str) {
        str4 = _str;
    }

    void setStr5(String _str) {
        str5 = _str;
    }

    void setStr6(String _str) {
        str6 = _str;
    }

    void setType(int type) {
        this.type = type;
    }

    Drawable getIcon() {
        return this.iconDrawable;
    }

    String getStr() {
        return this.str;
    }

    String getStr2() {
        return this.str2;
    }

    String getStr3() {
        return this.str3;
    }

    String getStr4() {
        return this.str4;
    }

    String getStr5() {
        return this.str5;
    }

    String getStr6() {
        return this.str6;
    }

    int getType() {
        return this.type;
    }




}
