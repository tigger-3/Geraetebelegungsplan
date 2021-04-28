package org.example;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OracleDB {
    private String user;
    private String passwd;
    private String host;
    private String port;
    private String service;
    private String connectionString;

    private Connection dbCon;

    public OracleDB(String user, String passwd, String host, String port, String service) {
        this.user = user;
        this.passwd = passwd;
        this.host = host;
        this.port = port;
        this.service = service;
        this.connectionString="jdbc:oracle:thin:@(DESCRIPTION="
                + "(ADDRESS=(PROTOCOL=tcp)(HOST=" + host + ")(PORT=" + port + "))"
                + "(CONNECT_DATA=(SERVICE_NAME=" + service + ")))";
    }

    public void connect(){
        try {
            OracleDataSource dataSource = new OracleDataSource();

            dataSource.setURL(connectionString);
            dataSource.setUser(user);
            dataSource.setPassword(passwd);

            dbCon = dataSource.getConnection();

            //System.out.println("Verbindung Steht.");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            disconnect();
        }
    }

    public void disconnect(){
        try {
            dbCon.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
