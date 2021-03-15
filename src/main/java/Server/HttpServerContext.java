package Server;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;

public interface HttpServerContext {

    ServerInfo getServerInfo();
    Collection<String>getSupportedRequestMethods();
    Properties getSupportedResponseStatuses();
    DataSource getDataSource();
    Path getRootPath();
    String contentType(String extension);
    HtmlTemplateManager getHtmlTemplateManager();
    Integer getExpiresDaysForResource(String extension);
}
