package ru.greatbit.whoru.auth.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by azee on 24.10.18.
 */
public class HttpUtils {

    public final static String SESSION_ID = "whoruSessionId";
    public final static String TOKEN_KEY = "Whoru-Api-Token";

    public static String getRemoteAddr(HttpServletRequest hsr, String ipHeader) {
        String remoteAdd = hsr.getRemoteAddr();
        String forward = hsr.getHeader(ipHeader);
        if (forward != null && !forward.isEmpty()) {
            remoteAdd = forward;
        }
        return remoteAdd;
    }

    public static Cookie findCookie(HttpServletRequest hsr, String name)  {
        Cookie[] cookies = hsr.getCookies();
        if (cookies == null)
            return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    public static Cookie createCookie(String key, String value, String domain, int ttl) {
        final Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(ttl);
        cookie.setDomain(domain);
        cookie.setHttpOnly(false);
        return cookie;
    }

    public static boolean isTokenAccessRequest(HttpServletRequest request) {
        return request.getHeader(TOKEN_KEY) != null;
    }

    public static String getValueFromHeaders(HttpServletRequest request, String key) {
        Object value = request.getHeader(key);
        return value != null ? value.toString() : "";
    }

    public static String getTokenValueFromHeaders(HttpServletRequest request) {
        return getValueFromHeaders(request, TOKEN_KEY);
    }
}
