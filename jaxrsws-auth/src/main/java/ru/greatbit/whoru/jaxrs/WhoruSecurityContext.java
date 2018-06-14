package ru.greatbit.whoru.jaxrs;


import ru.greatbit.whoru.auth.Session;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;


public class WhoruSecurityContext implements SecurityContext {

    final Session session;

    public WhoruSecurityContext(Session session) {
        this.session = session;
    }

    @Override
    public Session getUserPrincipal() {
        return session;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return HttpServletRequest.DIGEST_AUTH;
    }

}
