package com.cool.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Created by codelover on 17/3/24.
 * Cookie访问和设置类
 */
public class CoolCookies {
    private HashMap<String,Cookie> mapCookies;
    public CoolCookies(Cookie[] cookies){
        mapCookies = new HashMap<>();
        if(cookies == null){
            return;
        }
        for (Cookie cookie :
                cookies) {
            mapCookies.put(cookie.getName(),cookie);
        }
    }
    public Cookie get(String name){
        return mapCookies.get(name);
    }

    public static String getCookieValue(HttpServletRequest request,String name){
        Cookie cookie = getCookie(request,name);
        if(cookie == null){
            return null;
        }
        return cookie.getValue();
    }

    public static Cookie getCookie(HttpServletRequest request, String name){
        return new CoolCookies(request.getCookies()).get(name);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        if(maxAge>0)  cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
