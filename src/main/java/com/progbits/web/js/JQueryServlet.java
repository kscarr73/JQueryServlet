package com.progbits.web.js;

import aQute.bnd.annotation.component.Component;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
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
@Component(name="JQueryServlet", properties = { "alias=/jsjq", "name=JQueryServlet" }
, provide = HttpServlet.class)
public class JQueryServlet extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(JQueryServlet.class);

    private String _alias = "/jsjq";

    public String getAlias() {
        return _alias;
    }

    public void setAlias(String _alias) {
        this._alias = _alias;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String strPath = req.getPathInfo();

        Path path;

        try {
            URL url = getClass().getResource(strPath);

            URLConnection conn = url.openConnection();
            resp.addHeader("Cache-Control", "max-age=86400");
            resp.setContentLength(conn.getContentLength());

            String contentType = URLConnection.guessContentTypeFromName(strPath);
            
            if (contentType == null) {
                if (strPath.endsWith("js")) {
                    contentType = "application/javascript";
                } else if (strPath.endsWith("css")) {
                    contentType = "text/css";
                } else if (strPath.endsWith("png")) {
                    contentType = "image/png";
                } else if (strPath.endsWith("jpg")) {
                    contentType = "image/jpeg";
                }
            }
            
            resp.setContentType(contentType);
            
            log.debug("Content Type: " + contentType);
            
            long lSize;
            try (InputStream is = conn.getInputStream()) {
                lSize = ByteStreams.copy(is, resp.getOutputStream());
            }
            //resp.setContentType();

            resp.setStatus(200);
        } catch (Exception uex) {
            log.error("doGet", uex);
        }

    }

}
