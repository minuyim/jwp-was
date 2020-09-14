package webserver.http.request;

import java.util.Arrays;
import java.util.List;

public class RequestStartLine {
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_URL_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private RequestMethod requestMethod;
    private RequestUrl requestUrl;
    private String httpVersion;

    public RequestStartLine(RequestMethod requestMethod, RequestUrl requestUrl, String httpVersion) {
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.httpVersion = httpVersion;
    }

    public static RequestStartLine from(String startLine) {
        List<String> startLineElements = Arrays.asList(startLine.split(" "));

        RequestMethod requestMethod = RequestMethod.of(startLineElements.get(HTTP_METHOD_INDEX));
        RequestUrl requestUrl = RequestUrl.from(startLineElements.get(HTTP_URL_INDEX));
        String httpVersion = startLineElements.get(HTTP_VERSION_INDEX);
        return new RequestStartLine(requestMethod, requestUrl, httpVersion);
    }

    public RequestMethod getHttpMethod() {
        return requestMethod;
    }

    public String getUrl() {
        return requestUrl.getUrl();
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public boolean allowBody() {
        return requestMethod.allowBody();
    }
}
