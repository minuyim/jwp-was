package webserver.http.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private String id;
    private Map<String, Object> attribute;
    private boolean isNew;

    public HttpSession(String id) {
        this.id = id;
        attribute = new HashMap<>();
        isNew = true;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return attribute.get(name);
    }

    public void setAttribute(String name, Object value) {
        attribute.put(name, value);
    }

    public void removeAttribute(String name) {
        attribute.remove(name);
    }

    public void invalidate() {
        InMemorySessionRepository.removeSession(id);
    }

    public boolean isNew() {
        return isNew;
    }

    public void setOld() {
        isNew = false;
    }
}
