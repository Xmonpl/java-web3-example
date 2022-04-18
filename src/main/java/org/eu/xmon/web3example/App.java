package org.eu.xmon.web3example;

import com.dieselpoint.norm.Database;
import com.ftpix.sparknnotation.Sparknotation;
import org.eu.xmon.web3example.config.Settings;

import static spark.Spark.*;

public class App {

    public static final Database database = new Database();

    public static void main(String[] args) throws Exception {
        final Settings settings = new Settings().load();
        database.setJdbcUrl(settings.getJdbcURL());
        database.setUser(settings.getUsername());
        database.setPassword(settings.getPassword());
        port(8081);
        staticFiles.location("/public");
        System.out.println("==================");
        System.out.println("Private address: http://127.0.0.1:8081");
        System.out.println("Public address: http://" + getExternalIPAddress() + ":8081");
        System.out.println("==================");
        Sparknotation.init();
        enableCORS("*", "*", "*");

    }
    private static void enableCORS(final String origin, final String methods, final String headers) {

        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
    private static String getExternalIPAddress() throws Exception {
        String ip = "";
        java.util.Scanner s = new java.util.Scanner(new java.net.URL("http://checkip.amazonaws.com/").openStream());
        ip = s.nextLine();
        s.close();
        return ip;
    }

}
