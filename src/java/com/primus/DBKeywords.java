/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primus;

import com.models.KeyWordModel;
import com.mysql.jdbc.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

/**
 *
 * @author Windows
 */

// define a generic class for all database entries with table columns as arguments in the constructor

public class DBKeywords {

    public DBKeywords() throws SQLException {
        DriverManager.registerDriver(new Driver());
    }
    private final String DB_URL = "jdbc:mysql://localhost:3306/primus";
    private final String USER = "root";
    private final String PASS = "";

    public List<KeyWordModel> getKeywords() throws SQLException {
        Sql2o sql21 = new Sql2o(DB_URL, USER, PASS);

        String sql
                = "SELECT `keyword_id`,`name`,`type`"
                + "FROM `keyword`";

        try (Connection con = sql21.open()) {
            return con.createQuery(sql)
                    .executeAndFetch(KeyWordModel.class);
        }
    }

}
