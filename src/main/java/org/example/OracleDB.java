package org.example;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.*;

public class OracleDB {

    private String user;
    private String passwd;
    private String host;
    private String port;
    private String service;
    private String ConnectionString;
    Connection con;

    public OracleDB(String user, String passwd, String host, String port, String service) {
        this.user = user;
        this.passwd = passwd;
        this.host = host;
        this.port = port;
        this.service = service;
        this.ConnectionString="jdbc:oracle:thin:@(DESCRIPTION="
                + "(ADDRESS=(PROTOCOL=tcp)(HOST=" + host + ")(PORT=" + port + "))"
                + "(CONNECT_DATA=(SERVICE_NAME=" + service + ")))";
        con = null;
    }

    public void connect(){
        try {

            OracleDataSource dataSource = new OracleDataSource();
            dataSource.setURL(ConnectionString);
            dataSource.setUser(user);
            dataSource.setPassword(passwd);
            con = dataSource.getConnection();
            System.out.println("Verbindung steht");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getList(String sql) throws SQLException {

            Statement befehl = con.createStatement();

            ResultSet ergebnisse = befehl.executeQuery(sql);

            return ergebnisse;

    }

    public void disconnect(){
        try {
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
