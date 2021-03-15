package Server;

import java.util.Map;

public interface HtmlTemplateManager {

    String processTemplate(String templateName, Map<String, Object> args);
}
