package slipp.web;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import slipp.db.DataBase;
import slipp.model.User;
import webserver.http.request.HttpRequest;
import webserver.http.request.RequestBody;
import webserver.http.request.RequestHeaders;
import webserver.http.request.RequestLine;
import webserver.http.request.RequestMethod;
import webserver.http.request.RequestUrl;
import webserver.http.response.HttpResponse;
import webserver.http.session.HttpSession;
import webserver.http.session.InMemorySessionRepository;

class UserListControllerTest {
    private UserListController userListController = new UserListController();

    @AfterEach
    void tearDown() {
        DataBase.truncate();
    }

    @DisplayName("전체 유저 목록을 조회한다.")
    @Test
    void doGet() throws Exception {
        User user = new User("testId", "password", "test", "test@email.com");
        DataBase.addUser(user);
        HttpSession preSession = InMemorySessionRepository.getSession(null, true);
        preSession.setAttribute("logined", "true");

        RequestLine requestLine = new RequestLine(RequestMethod.GET, RequestUrl.from("/user/list"),
            "HTTP/1.1");
        HashMap<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("SESSIONID", preSession.getId());
        RequestHeaders requestHeaders = new RequestHeaders(httpHeaders);
        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, RequestBody.from(""));
        HttpResponse httpResponse = HttpResponse.ofVersion(httpRequest.getHttpVersion());

        userListController.service(httpRequest, httpResponse);

        String expected = "testId";
        assertAll(
            () -> assertThat(httpResponse)
                .extracting("responseStatus")
                .extracting("httpStatus")
                .extracting("statusCode").isEqualTo(200),
            () -> assertThat(httpResponse)
                .extracting("responseBody")
                .extracting("body", BYTE_ARRAY)
                .containsSequence(expected.getBytes())
        );
    }

    @DisplayName("로그인 상태가 아닌 경우 로그인 페이지로 리다이렉트 한다.")
    @Test
    void doGetWithNotLogin() throws Exception {
        User user = new User("testId", "password", "test", "test@email.com");
        DataBase.addUser(user);

        RequestLine requestLine = new RequestLine(RequestMethod.GET, RequestUrl.from("/user/list"),
            "HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(new HashMap<>());
        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, RequestBody.from(""));
        HttpResponse httpResponse = HttpResponse.ofVersion(httpRequest.getHttpVersion());

        userListController.service(httpRequest, httpResponse);

        assertAll(
            () -> assertThat(httpResponse)
                .extracting("responseStatus")
                .extracting("httpStatus")
                .extracting("statusCode").isEqualTo(302),
            () -> assertThat(httpResponse)
                .extracting("responseHeaders")
                .extracting("headers", MAP)
                .extractingByKey("Location").isEqualTo("/user/login.html")
        );
    }
}