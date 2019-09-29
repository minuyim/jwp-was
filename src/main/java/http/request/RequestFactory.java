package http.request;

import http.cookie.CookieParser;
import http.cookie.Cookies;
import http.exception.InvalidRequestException;
import http.session.Session;
import http.session.SessionStorage;
import utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static http.session.SessionStorage.JSESSIONID;

public class RequestFactory {
    private static final String DELIMITER_OF_REQUEST = "\n";
    private static final int REQUEST_SIZE = 2;

    public static Request makeRequest(InputStream in) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String[] requestData = validateRequest(IOUtils.parseData(br));
        RequestLine requestLine = new RequestLine(requestData[0]);
        RequestHeader requestHeader = new RequestHeader(requestData[1]);

        Cookies cookies = new Cookies(CookieParser.parse(requestHeader.get("Cookie")));
        Session session = SessionStorage.getInstance().getSession(
                cookies.findCookie(JSESSIONID).getValue());

        if (requestLine.isPost()) {
            return makeRequestWithBody(br, requestLine, requestHeader, cookies, session);
        }
        return new Request(requestLine, requestHeader, cookies, session);
    }

    private static String[] validateRequest(String parsedData) {
        String[] requestData = parsedData.split(DELIMITER_OF_REQUEST, 2);

        if (requestData.length != REQUEST_SIZE) {
            throw new InvalidRequestException("Invalid Request");
        }

        return requestData;
    }

    private static Request makeRequestWithBody(BufferedReader br, RequestLine requestLine, RequestHeader requestHeader,
                                               Cookies cookie, Session session) throws IOException {
        String body = IOUtils.readData(br, Integer.parseInt(requestHeader.get("content-length")));
        RequestBody requestBody = new RequestBody(body);
        return new Request(requestLine, requestHeader, requestBody, cookie, session);
    }
}
