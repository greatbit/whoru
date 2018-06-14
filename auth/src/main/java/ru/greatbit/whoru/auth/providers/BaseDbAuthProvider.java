package ru.greatbit.whoru.auth.providers;

import ru.greatbit.whoru.auth.Person;
import ru.greatbit.whoru.auth.Session;
import ru.greatbit.whoru.auth.error.UnauthorizedException;
import ru.greatbit.whoru.auth.utils.HttpUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.isEmpty;
import static ru.greatbit.whoru.auth.utils.AuthUtil.getMd5;
import static ru.greatbit.whoru.auth.utils.HttpUtils.getTokenValueFromHeaders;
import static ru.greatbit.whoru.auth.utils.HttpUtils.isTokenAccessRequest;

public abstract class BaseDbAuthProvider extends BaseAuthProvider{

    @Override
    public void doAuth(HttpServletRequest request, HttpServletResponse response) {
        if (isTokenAccessRequest(request)) {
            authByToken(request, response);
            return;
        }

        try {
            Cookie sid = HttpUtils.findCookie(request, HttpUtils.SESSION_ID);
            if (sid == null || !sessionProvider.sessionExists(sid.getValue())
                    || sessionProvider.getSessionById(sid.getValue()).getPerson().getLogin() != request.getParameter(PARAM_LOGIN)) {
                logger.info("No session found. Auth by login/password ip={}", HttpUtils.getRemoteAddr(request, IP_HEADER));
                authByLoginPassword(request, response);
            } else {
                logger.info("Updating session for user with ip={}", HttpUtils.getRemoteAddr(request, IP_HEADER));
                response.addCookie(HttpUtils.createCookie(HttpUtils.SESSION_ID, sid.getValue(), authDomain, sessionTtl));
            }
            sendRedirect(request, response);
        } catch (UnauthorizedException e){
            throw e;
        } catch (Exception e){
            logger.error("Can't authenticate user", e);
            throw new UnauthorizedException(e);
        }
    }

    public void authByToken(HttpServletRequest request, HttpServletResponse response) {
        String token = getTokenValueFromHeaders(request);

        if (!isEmpty(token)) {
            Person person;
            if (token.equals(this.adminToken)){
                person = getAdminPerson(token);
            } else {
                person = findPersonByApiToken(token);
            }
            if (person != null){
                Session existedSession = sessionProvider.getSessionById(token);
                if (existedSession == null) {
                    Session session = new Session().withTimeout(sessionTtl).withId(token).withName(token).withPerson(person);
                    sessionProvider.addSession(session);
                }
            }
        }
    }

    protected abstract Person getAdminPerson(String token);
    protected abstract Person getAdminPerson(String login, String password);

    protected abstract Person findPersonByApiToken(String token);
    protected abstract Person findPersonByLogin(String login);


    private void authByLoginPassword(HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException {
        final String login = request.getParameter(PARAM_LOGIN);
        final String password = request.getParameter(PARAM_PASSWORD);

        Person person;
        if (login.equalsIgnoreCase(this.adminLogin) && getMd5(password, login).equals(getMd5(this.adminPassword, this.adminLogin))) {
            person = getAdminPerson(login, password);
        } else {
            person = findPersonByLogin(login);
        }

        if (person!= null
                && login.equals(person.getLogin())
                && person.isActive()
                && getMd5(password, login).equals(person.getPassword())){

            authAs(login, response, person);
        } else throw new UnauthorizedException("Incorrect login or password");
    }

    private Session authAs(String login, HttpServletResponse response, Person person) {
        if (person.getPasswordExpirationTime() > 0 && System.currentTimeMillis() > person.getPasswordExpirationTime()){
            throw new UnauthorizedException(format("Temporary password has expired for user %s. Please contact administrator to set a new one.", person.getLogin()));
        }
        Session session = new Session().withId(UUID.randomUUID().toString()).withTimeout(sessionTtl).withName(login).withPerson(person);
        Session existedSession = sessionProvider.getSessionIfExists(session);
        if (existedSession == null) {
            sessionProvider.addSession(session);
            response.addCookie(HttpUtils.createCookie(HttpUtils.SESSION_ID, session.getId(), authDomain, sessionTtl));
        }
        else
            response.addCookie(HttpUtils.createCookie(HttpUtils.SESSION_ID, existedSession.getId(), authDomain, sessionTtl));
        return session;
    }


    @Override
    public void doAuthByOnetimeToken(String token, HttpServletRequest request, HttpServletResponse response) throws Exception {

    }


}
