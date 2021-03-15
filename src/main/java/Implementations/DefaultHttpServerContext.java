package Implementations;

import Configs.HttpServerConfig;
import Exceptions.HttpServerConfigException;
import Server.Constants;
import Server.HtmlTemplateManager;
import Server.HttpServerContext;
import Server.ServerInfo;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;

class DefaultHttpServerContext extends AbstractHttpConfigurableComponent implements HttpServerContext {

    DefaultHttpServerContext(HttpServerConfig httpServerConfig) {
        super( httpServerConfig );
    }

    private DefaultHttpServerConfig getHttpServerConfig() {
        return (DefaultHttpServerConfig) httpServerConfig;
    }

    @Override
    public ServerInfo getServerInfo() {
        return httpServerConfig.getServerInfo();
    }

    @Override
    public Collection<String> getSupportedRequestMethods() {
        return Constants.ALLOWED_METHODS;
    }

    @Override
    public Properties getSupportedResponseStatuses() {
        Properties prop = new Properties();
        prop.putAll(getHttpServerConfig().getStatusesProperties());
        return prop;
    }

    @Override
    public DataSource getDataSource() {
        if (getHttpServerConfig().getDataSource() != null) {
            return getHttpServerConfig().getDataSource();
        } else throw new HttpServerConfigException("Datasource is not configured for this context");
    }

    @Override
    public Path getRootPath() {
        return getHttpServerConfig().getRootPath();
    }

    @Override
    public String contentType(String extension) {
        String result = getHttpServerConfig().getMimeTypesProperties().getProperty(extension);
        return result != null ? result : "text/plain";
    }

    @Override
    public HtmlTemplateManager getHtmlTemplateManager() {
        return getHttpServerConfig().getHtmlTemplateManager();
    }

    @Override
    public Integer getExpiresDaysForResource(String extension) {
        if(getHttpServerConfig().getStaticExpiresExtensions().contains(extension)) {
            return getHttpServerConfig().getStaticExpiresDays();
        } else return null;
    }
}
