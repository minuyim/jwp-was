package webserver.http.response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import webserver.http.Cookie;

public class ResponseCookies {
    private List<Cookie> cookies;

    public ResponseCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public void add(Cookie cookie) {
        cookies.add(cookie);
    }

    public void write(DataOutputStream dos) throws IOException {
        List<String> cookieStrings = cookies.stream()
            .map(this::cookieToString)
            .collect(Collectors.toList());
        for (String cookieString : cookieStrings) {
            dos.writeBytes("Set-Cookie: " + cookieString + System.lineSeparator());
        }
    }

    private String cookieToString(Cookie cookie) {
        String cookieString = cookie.getName() + "=" + cookie.getValue();
        if (Objects.nonNull(cookie.getPath())) {
            cookieString = cookieString + "; Path=" + cookie.getPath() + ";";
        }
        return cookieString;
    }
}
