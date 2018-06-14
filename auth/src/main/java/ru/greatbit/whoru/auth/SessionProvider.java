package ru.greatbit.whoru.auth;


import java.util.Collection;
import java.util.Map;

/**
 * Created by azee on 14.01.17.
 */
public interface SessionProvider {

    public Session getSessionById(String sessionId);
    public Session getSessionIfExists(Session session);
    public void addSession(Session session);
    public Session removeSession(String sessionId);
    public boolean sessionExists(String sessionId);
    Collection<Session> getSessionsByPersonsLogin(String ... personsId);
    void replaceSession(Session session);
    Map<String, Session> getAllSessions();
}
