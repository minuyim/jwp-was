package webserver.http.session;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionRepository {
    private static final Map<String, HttpSession> inMemorySession = new ConcurrentHashMap<>();

    private InMemorySessionRepository() {
    }

    private static HttpSession makeSession() {
        String id = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(id);
        inMemorySession.put(id, httpSession);
        return httpSession;
    }

    public static void removeSession(String id) {
        inMemorySession.remove(id);
    }

    public static HttpSession getSession(String id, boolean create) {
        if (Objects.isNull(id) && create) {
            return makeSession();
        }
        HttpSession httpSession = inMemorySession.get(id);
        if (Objects.isNull(httpSession)) {
            return null;
        }
        httpSession.setOld();
        return httpSession;
    }
}
