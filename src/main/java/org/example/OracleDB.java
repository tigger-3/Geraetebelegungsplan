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

        //Im Connectionstring die Attribute der Klasse verwenden

        this.ConnectionString="jdbc:oracle:thin:@(DESCRIPTION="
                + "(ADDRESS=(PROTOCOL=tcp)(HOST=" + host + ")(PORT=" + port + "))"
                + "(CONNECT_DATA=(SERVICE_NAME=" + service + ")))";

        //Überflüssig
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

        return befehl.executeQuery(sql);

    }
    public boolean send(String sql) throws SQLException {
        Statement befehl = con.createStatement();

        return befehl.execute(sql);
    }

    //Rückgabewert wird nie verwendet
    public boolean insertTermin(Termin termin) throws SQLException {

        PreparedStatement stat = con.prepareStatement(
                "INSERT INTO SUS_FS191_eschulte.nutzung(kunden_id, geraete_id, datum, anfang, ende) " +
                        "VALUES(?,?,TO_DATE(?, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"'),TO_TIMESTAMP(?, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"'),TO_TIMESTAMP(?, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"'))");
        stat.setString(1,termin.benutzer.kundenummer);
        stat.setString(2,termin.ausgewaehltesGeraet.geraeteID);
        stat.setString(3,termin.datum.toInstant().toString());
        stat.setString(4,termin.uhrzeit.toString());
        stat.setString(5,termin.getEndzeit().toString());

        return stat.execute();
    }

    //Methode wird nicht verwendet
    public boolean insert(String sql) throws SQLException{
        return send(sql);
    }

    //Rückgabewert wird nie verwendet
    public boolean deleteTermin(Termin termin) throws SQLException{
        PreparedStatement stat = con.prepareStatement(
                "DELETE FROM SUS_FS191_eschulte.nutzung " +
                        "WHERE kunden_id = ? " +
                        "AND geraete_id = ? " +
                        "AND datum = TO_DATE(?, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') " +
                        "AND anfang = TO_TIMESTAMP(?, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') " +
                        "AND ende = TO_TIMESTAMP(?, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') "
        );

        //Duplikat von Zeile 68 (evtl. in eingene Methode auslagern)
        stat.setString(1,termin.benutzer.kundenummer);
        stat.setString(2,termin.ausgewaehltesGeraet.geraeteID);
        stat.setString(3,termin.datum.toInstant().toString());
        stat.setString(4,termin.uhrzeit.toString());
        stat.setString(5,termin.getEndzeit().toString());

        return stat.execute();
    }

    //Methode wird nicht verwendet
    public boolean delete(String sql) throws SQLException{
        return send(sql);
    }

    public void disconnect(){
        try {
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
