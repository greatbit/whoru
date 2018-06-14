package ru.greatbit.whoru.example;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import static java.lang.String.format;

public class Application extends ResourceConfig {

    public Application() {
        register(RequestContextFilter.class);
        register(JacksonFeature.class);
        register(RolesAllowedDynamicFeature.class);
        registerFinder(packageScanner(".resources"));
        registerFinder(packageScanner(".security"));
    }

    private PackageNamesScanner packageScanner(String path) {
        return new PackageNamesScanner(new String[]{format("%s%s", getClass().getPackage().getName(), path)}, true);
    }

}
