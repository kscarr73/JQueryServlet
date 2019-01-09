package com.progbits.web.js;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scarr
 */
@Component(name = "JQueryServlet",
        property = {"alias=/jsjq", "name=JQueryServlet"},
        service = {HttpServlet.class}
)
public class JQueryServlet extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(JQueryServlet.class);

    private final DateTimeFormatter _dateHeader = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

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
            sendFile(_servlet, "/jsjq", req, resp);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    public void sendFile(ServletSetup setup, String path, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String fileName = pullPath(req.getRequestURI(), path);
        fileName = URLDecoder.decode(fileName, "UTF-8");

        URL url;

        if (setup.getLoader() != null) {
            url = setup.getLoader().getResource(fileName);
        } else if (setup.getBasePath() != null) {
            url = new File(setup.getBasePath() + fileName).toURI().toURL();
        } else {
            throw new Exception("BasePath or Loader MUST be set");
        }

        URLConnection conn = url.openConnection();

        resp.setContentType(setup.getContext().getMimeType(fileName));
        resp.setContentLength(conn.getContentLength());
        resp.setHeader("Cache-Control", "max-age=" + setup.getCacheTime());

        if (conn.getLastModified() > 0L) {
            resp.setHeader("Last-Modified", _dateHeader.print(conn.getLastModified()));
        }

        if ("HEAD".equalsIgnoreCase(req.getMethod())) {
            // HEAD does not need content
        } else {
            writeFile(conn.getInputStream(), resp.getOutputStream());
        }
    }

    public void writeFile(InputStream is, OutputStream out) throws Exception {
        byte[] buffer = new byte[4096]; // tweaking this number may increase performance  
        int len;

        while ((len = is.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        out.flush();
    }

    public String pullPath(String path, String type) {
        String strRet = path;

        int iLoc = strRet.indexOf(type);

        if (iLoc > -1) {
            strRet = strRet.substring(iLoc);
        }

        return strRet;
    }

}
