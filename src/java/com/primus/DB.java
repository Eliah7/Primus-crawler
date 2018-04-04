/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primus;

import com.models.UrlModel;
import com.mysql.jdbc.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

/**
 *
 * @author eliahmbwilo
 */
public class DB {

    public DB() throws SQLException {
        DriverManager.registerDriver(new Driver());
    }
    private final String DB_URL = "jdbc:mysql://localhost:3306/primus";
    private final String USER = "root";
    private final String PASS = "";

    public List<UrlModel> getUrls() throws SQLException {
        Sql2o sql2o = new Sql2o(DB_URL, USER, PASS);
        

        String sql
                = "SELECT `publisher_id`,`name`,`url`,`type`,`status` "
                + "FROM `publisher`";

        try (Connection con = sql2o.open()) {
            
                return  con.createQuery(sql)
                        .executeAndFetch(UrlModel.class);
        }    }

    /*public List<UrlModel> getKeyWords() throws SQLException{
        Sql2o sql2o = new Sql2o(DB_URL,USER,PASS);
        List<UrlModel> tasks;
        
        String sql =
                "SELECT name,publisher_id,"
                +"FROM keyword";
        try(Connection con = sql2o.open()){
                tasks = con.createQuery(sql)
                    .executeAndFetch( KeyWordModel.class);
                  
        }
        return tasks;
    }
     */
}
