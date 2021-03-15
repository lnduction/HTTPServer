package Handler;

import Exceptions.HttpServerExceptions;
import Server.HttpHandler;
import Server.HttpRequest;
import Server.HttpResponse;
import Server.HttpServerContext;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestJDBCHandler implements HttpHandler {
    @Override
    public void handle(HttpServerContext context, HttpResponse response, HttpRequest request) throws IOException {
        try(Connection c = context.getDataSource().getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("select * from students")) {
            Map<String, Object> data = new LinkedHashMap<>(  );
            while(rs.next()){
                data.put( rs.getString( "id" ),
                        rs.getString( "first_name" ) + "," +
                                rs.getString( "last_name" ) + "," +
                                rs.getString( "age" ) );
            }
            response.setBody(context.getHtmlTemplateManager().processTemplate( "students.html", data ));

        } catch (SQLException e) {
            throw new HttpServerExceptions("Error with database: "+e.getMessage(), e);
        }
    }
}
