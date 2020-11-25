package webserver.http.request;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import webserver.http.Cookie;

public class HttpRequestTest {
    @DisplayName("파라미터를 올바르게 가지고 오는 지 확인한다.")
    @Test
    void getParameter() throws Exception {
        String request = "POST /index.html?a=1&b=2 HTTP/1.1" + System.lineSeparator()
            + "Host: localhost:8080" + System.lineSeparator()
            + "Connection: keep-alive" + System.lineSeparator()
            + "Content-Length: 8" + System.lineSeparator()
            + "Accept: */*" + System.lineSeparator()
            + System.lineSeparator()
            + "i'm body";

        HttpRequest httpRequest = HttpRequestFactory.create(new ByteArrayInputStream(request.getBytes()));

        assertThat(httpRequest.getParameter("a")).isEqualTo("1");
    }

    @DisplayName("Get 요청 시, 파라미터를 올바르게 가지고 오는 지 확인한다.")
    @Test
    void getParameterWithGet() throws Exception {
        String request = "GET /index.html?a=2 HTTP/1.1" + System.lineSeparator()
            + "Host: localhost:8080" + System.lineSeparator()
            + "Connection: keep-alive" + System.lineSeparator()
            + "Accept: */*" + System.lineSeparator()
            + System.lineSeparator()
            + "b=2";

        HttpRequest httpRequest = HttpRequestFactory.create(new ByteArrayInputStream(request.getBytes()));

        assertAll(
            () -> assertThat(httpRequest.getParameter("a")).isEqualTo("2"),
            () -> assertThat(httpRequest.getParameter("b")).isNull()
        );
    }

    @DisplayName("Post 요청 시, 파라미터를 올바르게 가지고 오는 지 확인한다.")
    @Test
    void getParameterWithPost() throws Exception {
        String request = "POST /index.html?b=2 HTTP/1.1" + System.lineSeparator()
            + "Host: localhost:8080" + System.lineSeparator()
            + "Connection: keep-alive" + System.lineSeparator()
            + "Content-Length: 3" + System.lineSeparator()
            + "Accept: */*" + System.lineSeparator()
            + System.lineSeparator()
            + "a=2";

        HttpRequest httpRequest = HttpRequestFactory.create(new ByteArrayInputStream(request.getBytes()));

        assertAll(
            () -> assertThat(httpRequest.getParameter("a")).isEqualTo("2"),
            () -> assertThat(httpRequest.getParameter("b")).isEqualTo("2")
        );
    }

    @DisplayName("파라미터가 없을 경우 null을 반환한다.")
    @Test
    void getParameterWithEmpty() throws Exception {
        String request = "POST /index.html HTTP/1.1" + System.lineSeparator()
            + "Host: localhost:8080" + System.lineSeparator()
            + "Connection: keep-alive" + System.lineSeparator()
            + "Accept: */*" + System.lineSeparator()
            + System.lineSeparator();

        HttpRequest httpRequest = HttpRequestFactory.create(new ByteArrayInputStream(request.getBytes()));

        assertThat(httpRequest.getParameter("a")).isNull();
    }

    @DisplayName("요청에서 쿠키 정보를 가지고 온다.")
    @Test
    void getCookies() throws IOException {
        String request = "POST /index.html HTTP/1.1" + System.lineSeparator()
            + "Host: localhost:8080" + System.lineSeparator()
            + "Connection: keep-alive" + System.lineSeparator()
            + "Accept: */*" + System.lineSeparator()
            + "Cookie: logined=true; abc=a"
            + System.lineSeparator();

        HttpRequest httpRequest = HttpRequestFactory.create(new ByteArrayInputStream(request.getBytes()));
        List<Cookie> cookies = httpRequest.getCookies();

        assertAll(
            () -> assertThat(cookies).hasSize(2),
            () -> assertThat(cookies).element(0).extracting(Cookie::getName).isEqualTo("logined"),
            () -> assertThat(cookies).element(0).extracting(Cookie::getValue).isEqualTo("true"),
            () -> assertThat(cookies).element(1).extracting(Cookie::getName).isEqualTo("abc"),
            () -> assertThat(cookies).element(1).extracting(Cookie::getValue).isEqualTo("a")
        );
    }

    @DisplayName("쿠기 정보가 헤더에 없으면 빈 리스트를 가져온다.")
    @Test
    void getCookiesWithEmpty() throws IOException {
        String request = "POST /index.html HTTP/1.1" + System.lineSeparator()
            + "Host: localhost:8080" + System.lineSeparator()
            + "Connection: keep-alive" + System.lineSeparator()
            + "Accept: */*" + System.lineSeparator()
            + System.lineSeparator();

        HttpRequest httpRequest = HttpRequestFactory.create(new ByteArrayInputStream(request.getBytes()));
        List<Cookie> cookies = httpRequest.getCookies();

        assertThat(cookies).isEmpty();
    }
}
