package Implementations;

import Configs.HttpRequestDispatcher;
import Server.HttpRequest;
import Server.HttpResponse;
import Server.HttpServerContext;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DefaultHttpHandler implements HttpRequestDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger( DefaultHttpRequestDispatcher.class );

    @Override
    public void handle(HttpServerContext context, HttpResponse response, HttpRequest request) throws IOException {
        File uriFile = getFile( request.getUri(), context );

        if (uriFile == null) {
            response.setStatus( 404 );
            return;
        }
        if (Objects.requireNonNull( uriFile ).isDirectory())
            response.setBody( getDirectoryFiles( uriFile, context ) );
        else if (Objects.requireNonNull( uriFile ).isFile()) {
            response.setBody( new FileInputStream( uriFile ));
            response.setHeader( "Content-Type", new MimetypesFileTypeMap().getContentType(uriFile) );
            response.setHeader( "Last-Modify", uriFile.lastModified() );
            String expires = FilenameUtils.getExtension(uriFile.getAbsolutePath());
            Integer expiresDays = context.getExpiresDaysForResource( expires );
            if (expiresDays != null)
            response.setHeader( "Expires", new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis( expiresDays ) ));
        }


    }

    private String getDirectoryFiles(File directory, HttpServerContext context) {
        String root = context.getRootPath().toString();
        StringBuilder htmlBody = new StringBuilder();
        for (File file : Objects.requireNonNull( directory.listFiles() ))
            htmlBody.append("<a href=\"").append(getHref(root, file)).append("\">").append(file.getName()).append("</a><br>\r\n");

        Map<String, Object> args = buildMap(new Object[][] {
                { "TITLE",   "File list for " + directory.getName() },
                { "HEADER", "File list for " + directory.getName() },
                { "BODY", 	 htmlBody }
        });
        return context.getHtmlTemplateManager().processTemplate("simple.html", args);
    }

    private File getFile(String uri, HttpServerContext context) {
        File uriFile = new File( context.getRootPath() + uri );
        ArrayList<File> fileList = (ArrayList<File>) getListFiles( context.getRootPath().toString() );
        for (File file : fileList) if (file.getName().equals( uriFile.getName() )) return file;
        return null;
    }

    private List<File> getListFiles(String rootFile) {
        ArrayList<File> listWithFileNames = new ArrayList<>();
        listWithFileNames.add( new File( rootFile ) );
        File file = new File( rootFile );
        try {
            for (File fileDir : Objects.requireNonNull( file.listFiles() ))
                if (fileDir.isFile()) listWithFileNames.add( fileDir );
                else if (fileDir.isDirectory()) {
                    listWithFileNames.add( fileDir );
                    listWithFileNames.addAll( getListFiles( fileDir.getAbsolutePath() ) );
                }
        } catch (Exception e) {
            LOGGER.error( "cant to see all the files in root directory" );
        }
        return listWithFileNames;
    }

    private String getHref(String root, File file) {
        return file.toString().replace(root, "");
    }

    private  Map<String, Object> buildMap(Object[][] data) {
        Map<String, Object> map = new HashMap<>();
        for(Object[] row : data) {
            map.put(String.valueOf(row[0]), row[1]);
        }
        return Collections.unmodifiableMap(map);
    }
}
