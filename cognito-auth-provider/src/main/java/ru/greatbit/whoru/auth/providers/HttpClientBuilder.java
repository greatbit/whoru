package ru.greatbit.whoru.auth.providers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.java8.Java8CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

public class HttpClientBuilder {

    public static Retrofit.Builder builder(String endpoint, long timeout, Interceptor... interceptors) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        for (Interceptor interceptor : interceptors) {
            okHttpClientBuilder.addInterceptor(interceptor);
        }
        okHttpClientBuilder.followSslRedirects(true);

        ObjectMapper jacksonMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
                .configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true) ;
        jacksonMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());

        return (new Retrofit.Builder()).client(okHttpClientBuilder.build())
                .baseUrl(endpoint)
                .addConverterFactory(JacksonConverterFactory.create(jacksonMapper))
                .addCallAdapterFactory(Java8CallAdapterFactory.create());
    }
}
