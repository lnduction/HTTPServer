package Implementations;

import Exceptions.HttpServerExceptions;
import Server.HtmlTemplateManager;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class DefaultHtmlTemplateManager implements HtmlTemplateManager {
    private final Map<String, String> templates = new HashMap<>();

    @Override
    public String processTemplate(String templateName, Map<String, Object> args) {
        String template = getTemplate(templateName);
        if (templateName.equals( "students.html" )) return populateJDBCTemplate( template, args );
        return populateTemplate(template, args);
    }

    protected InputStream getClasspathResource(String name) {
        return DefaultHtmlTemplateManager.class.getClassLoader().getResourceAsStream(name);
    }

    protected String getTemplate(String templateName) {
        String template = templates.get(templateName);
        if (template == null)
            try (InputStream in = getClasspathResource("html/templates/" + templateName)) {
                if (in == null) throw new HttpServerExceptions("Classpath resource \"html/templates/" + templateName + "\" not found");
                template = IOUtils.toString(in, StandardCharsets.UTF_8);
                templates.put(templateName, template);
            } catch (IOException e) {
                throw new HttpServerExceptions("Can't load template: " + templateName, e);
            }
        return template;
    }

    protected String populateTemplate(String template, Map<String, Object> args) {
        String html = template;
        for (Map.Entry<String, Object> entry : args.entrySet())
            html = html.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        return html;
    }

    protected String populateJDBCTemplate(String template, Map<String, Object> args){
        StringBuilder table = new StringBuilder(  );
        for (Map.Entry<String, Object> entry : args.entrySet()){
            table.append( "<tr><td>" ).append( entry.getKey() ).append( "</td>" );
            for(String value: entry.getValue().toString().split( "," )) table.append( "<td>" ).append( value ).append( "</td>" );
            table.append( "</tr>\r" );
        }
        table = new StringBuilder(template.replace( "${TABLE_BODY}", table.toString() ));
        return table.toString();
    }
}