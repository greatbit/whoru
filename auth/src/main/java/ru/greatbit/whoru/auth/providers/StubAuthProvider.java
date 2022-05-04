package ru.greatbit.whoru.auth.providers;

import org.springframework.beans.factory.annotation.Value;
import ru.greatbit.whoru.auth.Person;
import ru.greatbit.whoru.auth.Session;
import ru.greatbit.whoru.auth.SessionProvider;
import ru.greatbit.whoru.auth.error.UnauthorizedException;
import ru.greatbit.whoru.auth.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.springframework.util.StringUtils.isEmpty;

public class StubAuthProvider extends BaseAuthProvider {

    @Autowired
    SessionProvider sessionProvider;

    @Value("${stub.login}")
    protected String loginProp;

    @Value("${stub.password}")
    protected String passwordProp;

    @Override
    public Session authImpl(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie sid = HttpUtils.findCookie(request, HttpUtils.SESSION_ID);
            if (sid == null || !sessionProvider.sessionExists(sid.getValue())){
                final String login = request.getParameter(PARAM_LOGIN);
                final String password = request.getParameter(PARAM_PASSWORD);

                if (isEmpty(login) || isEmpty(password) || (!login.equals(loginProp) && !password.equals(passwordProp))){
                    throw new UnauthorizedException("Login and password should match");
                }

                Session session = (Session) new Session().withId(UUID.randomUUID().toString())
                        .withTimeout(sessionTtl).withPerson(createPerson(login))
                        .withLogin(login);

                sessionProvider.addSession(session);
                response.addCookie(HttpUtils.createCookie(HttpUtils.SESSION_ID, session.getId(), authDomain, sessionTtl));
                return session;
            } else {
                response.addCookie(HttpUtils.createCookie(HttpUtils.SESSION_ID, sid.getValue(), authDomain, sessionTtl));
                return sessionProvider.getSessionById(sid.getValue());
            }
        } catch (Exception e){
            throw new UnauthorizedException(e);
        }
    }

    private Person createPerson(String login) {
        return new Person().withId(login).withFirstName("Walter")
                .withLastName("White")
                .withLogin("heisenberg")
                .withGroups("testers");
    }

    @Override
    public void doAuthByOnetimeToken(String token, HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getAllGroups(HttpServletRequest request) {
        return Stream.of(new String[]{"testers", "managers", "developers"}).collect(toSet());
    }

    @Override
    public Set<String> suggestGroups(HttpServletRequest request, String literal) {
        return getAllGroups(request).stream().filter(group -> group.contains(literal)).collect(toSet());
    }

    @Override
    public Set<String> getAllUsers(HttpServletRequest request) {
        return Stream.of(new String[]{"heisenberg", "tony", "margo"}).collect(toSet());
    }

    @Override
    public Set<String> suggestUser(HttpServletRequest request, String literal) {
        return getAllUsers(request).stream().filter(group -> group.contains(literal)).collect(toSet());
    }
}
