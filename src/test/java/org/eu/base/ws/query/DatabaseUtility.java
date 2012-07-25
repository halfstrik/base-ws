package org.eu.base.ws.query;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.fail;

public class DatabaseUtility {
    public static Connection startup() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            return DriverManager.getConnection("jdbc:hsqldb:mem:unit-testing-jpa", "SA", "");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during HSQL database startup.");
            return null;
        }
    }

    public static void shutdown(Connection connection) {
        try {
            connection.createStatement().execute("SHUTDOWN");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during HSQL database shutdown.");
        }
    }
}
