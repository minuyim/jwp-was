package webserver.http.request;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import webserver.http.Cookie;
import webserver.http.session.HttpSession;
import webserver.http.session.InMemorySessionRepository;

public class HttpRequest {
    private static final String COOKIE = "Cookie";
    private static final String SPACE = " ";
    private static final String KEY_VALUE_DELIMETER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String SESSION_ID = "SESSIONID";

    private RequestLine requestLine;
    private RequestHeaders requestHeaders;
    private RequestBody requestBody = RequestBody.from("");
    private HttpSession httpSession;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        if (requestLine.allowBody()) {
            this.requestBody = requestBody;
        }
    }

    public RequestMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getUrl() {
        return getRequestUrl().getUrl();
    }

    public String getParameter(String key) {
        List<String> parameters = getParameters(key);
        if (Objects.isNull(parameters) || parameters.isEmpty()) {
            return null;
        }
        return parameters.get(0);
    }

    private List<String> getParameters(String key) {
        if (Objects.isNull(requestBody)) {
            return getRequestParameter().getParameters(key);
        }
        return Stream.of(getRequestParameter().getParameters(key), requestBody.getParameters(key))
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public List<Cookie> getCookies() {
        String cookieString = requestHeaders.get(COOKIE);
        if (Objects.isNull(cookieString)) {
            return Collections.emptyList();
        }
        String[] cookieStrings = cookieString.split(SPACE);
        return Arrays.stream(cookieStrings)
            .map(cookie -> cookie.split(KEY_VALUE_DELIMETER))
            .map(cookie -> new Cookie(cookie[KEY_INDEX], cookie[VALUE_INDEX].replace(";", "")))
            .collect(Collectors.toList());
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public HttpSession getSession(boolean create) {
        if (Objects.nonNull(httpSession)) {
            return httpSession;
        }
        httpSession = InMemorySessionRepository.getSession(getSessionId(), create);
        return httpSession;
    }

    private String getSessionId() {
        return requestHeaders.get(SESSION_ID);
    }

    public RequestUrl getRequestUrl() {
        return requestLine.getHttpUrl();
    }

    public RequestParams getRequestParameter() {
        return getRequestUrl().getHttpRequestParams();
    }

    public String getBody() {
        return requestBody.getBody();
    }
}
