package slipp.web;

import java.util.Objects;

import slipp.db.DataBase;
import slipp.model.User;
import webserver.controller.AbstractController;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;
import webserver.http.response.HttpStatus;

public class LoginController extends AbstractController {
    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String userId = httpRequest.getParameter("userId");
        String password = httpRequest.getParameter("password");

        User user = DataBase.findUserById(userId);
        if (Objects.isNull(user) || !user.authenticate(password)) {
            httpResponse.sendError(HttpStatus.BAD_REQUEST, "id 또는 password가 잘못 입력되었습니다.");
            return;
        }

        httpResponse.setHeader("Set-Cookie", "logined=true; Path=/");
        httpResponse.sendRedirect("/index.html");
    }
}
