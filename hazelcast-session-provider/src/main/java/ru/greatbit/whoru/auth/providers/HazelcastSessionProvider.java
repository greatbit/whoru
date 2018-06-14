package ru.greatbit.whoru.auth.providers;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import ru.greatbit.whoru.auth.Session;
import ru.greatbit.whoru.auth.SessionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


@Component
public class HazelcastSessionProvider implements SessionProvider {

    private final static String MAP_NAME = "whoru_sessions";

    @Autowired
    private HazelcastInstance instance;

    @Override
    public Session getSessionById(String sessionId) {
        return getMap().get(sessionId);
    }

    @Override
    public Session getSessionIfExists(Session session) {
        return getMap().entrySet().stream().map(entry -> entry.getValue()).filter(sessionItem ->
                sessionItem.getName().equals(session.getName())
                        && sessionItem.getPerson().equals(session.getPerson())
        ).findFirst().orElse(null);
    }


    @Override
    public void addSession(Session session) {
        getMap().put(session.getId(), session);
    }

    @Override
    public Session removeSession(String sessionId) {
        return getMap().remove(sessionId);
    }

    @Override
    public boolean sessionExists(String sessionId) {
        return getMap().containsKey(sessionId);
    }

    private IMap<String, Session> getMap() {
        return instance.getMap(MAP_NAME);
    }

    @Override
    public Collection<Session> getSessionsByPersonsLogin(String ... personsLogin) {
        EntryObject entryObject = new PredicateBuilder().getEntryObject();
        Predicate predicate = Arrays.stream(personsLogin)
                .map(login -> entryObject.get("person.login").equal(login))
                .reduce(PredicateBuilder::or).orElse(null);

        return getMap().values(predicate);
    }

    @Override
    public void replaceSession(Session session) {
        addSession(session);
    }

    @Override
    public Map<String, Session> getAllSessions() {
        return getMap();
    }
}
