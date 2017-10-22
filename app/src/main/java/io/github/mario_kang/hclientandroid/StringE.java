package io.github.mario_kang.hclientandroid;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


class StringE {
    String replacingOccurrences(String str) {
        String str1 = str;
        str1 = str1.replace("\"acg","\"dj");
        str1 = str1.replace("\"cg","\"dj");
        str1 = str1.replace("\"anime","\"dj");
        str1 = str1.replace("\"manga","\"dj");
        return str1;
    }

    String[] SplitString(String str, String spl) {
        return str.split(Pattern.quote(spl));
    }

    static JSONArray remove(final int idx, final JSONArray from) {
        final List<String> objs = asList(from);
        objs.remove(idx);
        final JSONArray ja = new JSONArray();
        for (final String obj : objs)
            ja.put(obj);
        Log.v("jsonC", ja.toString());
        return ja;
    }

    private static List<String> asList(JSONArray ja) {
        int len = ja.length();
        ArrayList<String> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            String obj = ja.optString(i);
            if (obj != null)
                result.add(obj);
        }
        return result;
    }

}
