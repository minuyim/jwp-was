package webserver;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.http.Cookie;
import webserver.http.request.HttpRequest;
import webserver.http.request.HttpRequestFactory;
import webserver.http.response.HttpResponse;
import webserver.http.session.HttpSession;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest httpRequest = HttpRequestFactory.create(in);
            HttpResponse httpResponse = HttpResponse.ofVersion(httpRequest.getHttpVersion());
            RequestMapper.execute(httpRequest, httpResponse);
            setSession(httpRequest, httpResponse);
            httpResponse.write(dos);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void setSession(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpSession session = httpRequest.getSession(false);
        if (Objects.nonNull(session) && session.isNew()) {
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setPath("/");
            httpResponse.addCookie(cookie);
        }
    }
}
