package uk.ac.newcastle.enterprisemiddleware;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.h2.tools.Server;

import java.sql.SQLException;

@QuarkusMain
public class Application {

    private static Server server;
    public static void main(String[] args) throws SQLException {
        // Start H2 in server mode to allow remote connections (DBeaver)
        /*try {
            server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers", "-ifNotExists").start();
        } catch (SQLException e) {
            throw new RuntimeException("Could not start H2 server", e);
        }*/
        // Start the Quarkus app
        Quarkus.run(args);
    }

}
