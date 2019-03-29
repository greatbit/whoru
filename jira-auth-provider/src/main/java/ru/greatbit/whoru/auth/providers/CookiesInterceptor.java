package ru.greatbit.whoru.auth.providers;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Stream;

public class CookiesInterceptor implements Interceptor {

    public CookiesInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    private final HttpServletRequest request;
    private final String SESSION_ID_COOKIE = "JSESSIONID";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (request != null && request.getCookies() != null){
            Stream.of(request.getCookies()).
                    forEach(cookie -> builder.addHeader("Cookie", cookie.getName() + "=" + cookie.getValue()));
        }
        return chain.proceed(builder.build());
    }
}
