package com.progbits.web.js;

import aQute.bnd.annotation.component.Component;
import com.progbits.web.ServletSetup;
import com.progbits.web.SsWebUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scarr
 */
@Component(name = "JQueryServlet", properties = {"alias=/jsjq", "name=JQueryServlet"}, provide = HttpServlet.class)
public class JQueryServlet extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(JQueryServlet.class);

    private ServletSetup _servlet = null;

    private String _alias = "/jsjq";

    @Override
    public void init(ServletConfig config) throws ServletException {
        _servlet = new ServletSetup();
        _servlet.setBasePath("/jsjq");
        _servlet.setCacheTime(500);
        _servlet.setContext(config.getServletContext());
        _servlet.setLoader(this.getClass());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            SsWebUtils.sendFile(_servlet, "/jsjq", req, resp);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
