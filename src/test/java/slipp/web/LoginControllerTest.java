package slipp.web;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import slipp.db.DataBase;
import slipp.model.User;
import webserver.http.Cookie;
import webserver.http.request.HttpRequest;
import webserver.http.request.RequestBody;
import webserver.http.request.RequestHeaders;
import webserver.http.request.RequestLine;
import webserver.http.request.RequestMethod;
import webserver.http.request.RequestUrl;
import webserver.http.response.HttpResponse;
import webserver.http.session.HttpSession;

class LoginControllerTest {
    private LoginController loginController = new LoginController();

    @AfterEach
    void tearDown() {
        DataBase.truncate();
    }

    @DisplayName("로그인 요청을 수행한다.")
    @Test
    void doPost() throws Exception {
        DataBase.addUser(new User("hello", "password", "myname", "is@name.com"));

        RequestLine requestLine = new RequestLine(RequestMethod.POST, RequestUrl.from("/user/login"),
            "HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(new HashMap<>());
        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders,
            RequestBody.from("userId=hello&password=password"));
        HttpResponse httpResponse = HttpResponse.ofVersion(httpRequest.getHttpVersion());

        loginController.doPost(httpRequest, httpResponse);

        HttpSession session = httpRequest.getSession();

        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setPath("/");

        assertAll(
            () -> assertThat(httpResponse)
                .extracting("responseStatus")
                .extracting("httpStatus")
                .extracting("statusCode").isEqualTo(302),
            () -> assertThat(session.getAttribute("logined")).isEqualTo("true")
        );
    }

    @DisplayName("잘못된 유저 정보를 입력시 예외 처리한다.")
    @Test
    void doPostWithWrongUserInfo() throws Exception {
        DataBase.addUser(new User("hello", "password", "myname", "is@name.com"));

        RequestLine requestLine = new RequestLine(RequestMethod.POST, RequestUrl.from("/user/login"),
            "HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(new HashMap<>());
        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders,
            RequestBody.from("userId=wrong&password=password"));
        HttpResponse httpResponse = HttpResponse.ofVersion(httpRequest.getHttpVersion());

        loginController.doPost(httpRequest, httpResponse);

        assertThat(httpResponse)
            .extracting("responseStatus")
            .extracting("httpStatus")
            .extracting("statusCode").isEqualTo(400);
    }
}