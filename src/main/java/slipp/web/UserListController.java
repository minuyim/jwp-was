package slipp.web;

import java.util.Collection;
import java.util.Objects;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import slipp.db.DataBase;
import slipp.model.User;
import webserver.controller.AbstractController;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;
import webserver.http.session.HttpSession;

public class UserListController extends AbstractController {
    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        if (!isLogin(httpRequest.getSession())) {
            httpResponse.sendRedirect("/user/login.html");
            return;
        }

        Collection<User> users = DataBase.findAll();
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");
        loader.setSuffix(".html");
        Handlebars handlebars = new Handlebars(loader);
        handlebars.registerHelper("inc", (Helper<Integer>)(context, options) -> context + 1);

        Template template = handlebars.compile("user/list");

        httpResponse.writeBody(template.apply(users));
    }

    private boolean isLogin(HttpSession httpSession) {
        if (Objects.isNull(httpSession)) {
            return false;
        }
        return Objects.equals(httpSession.getAttribute("logined"), "true");
    }
}
