package ru.greatbit.whoru.auth.providers;

import ru.greatbit.whoru.auth.Session;
import ru.greatbit.whoru.auth.SessionProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class InmemSessionProvider implements SessionProvider {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public Session getSessionById(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public Session getSessionIfExists(Session session) {
        return this.sessions.entrySet().stream().map(entry -> entry.getValue()).filter(sessionItem ->
            sessionItem.getName().equals(session.getName())
                    && sessionItem.getPerson().equals(session.getPerson())
        ).findFirst().orElse(null);
    }

    @Override
    public void addSession(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session removeSession(String sessionId) {
        return sessions.remove(sessionId);
    }

    @Override
    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    @Override
    public List<Session> getSessionsByPersonsLogin(String ... personsLogin) {
        return sessions.entrySet().stream()
                .filter(entry-> Arrays.asList(personsLogin).contains(entry.getValue().getPerson().getLogin()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public void replaceSession(Session session) {
        addSession(session);
    }

    @Override
    public Map<String, Session> getAllSessions() {
        return sessions;
    }
}