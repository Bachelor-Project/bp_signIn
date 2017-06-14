/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bp.signin.db;

import com.mycompany.bp.signin.dao.User;
import com.mycompany.bp.signin.dto.SignUpRequestDTO;
import com.mycompany.bp.signin.dto.ViolationDTO;
import com.mycompany.bp.signin.exceptions.GlobalException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 *
 * @author Dato
 */
public class DBManagerReal implements DBManager {

    private final String connectionState = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState";
    private final String statementFinalizer = "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";
    private DataSource datasource;
    
    public static DBManagerReal instance = new DBManagerReal();
    
    private DBManagerReal(){
        initDB();
    }
    
    private void initDB(){
        PoolProperties pool = new PoolProperties();
        pool.setUrl(DBData.DB_PATH);
        pool.setDriverClassName(DBData.JDBC_DRIVER);
        pool.setUsername(DBData.USERNAME);
        pool.setPassword(DBData.PASSWORD);
        pool.setMaxActive(256);
        pool.setInitialSize(16);
        pool.setMaxWait(10000);
        pool.setJdbcInterceptors(connectionState + ";" + statementFinalizer);
        
        datasource = new DataSource();
        datasource.setPoolProperties(pool);
    }
    
    @Override
    public User getUser(String username, String password) {
        return null;
    }

    @Override
    public User saveUser(SignUpRequestDTO request) throws GlobalException {
        User result = null;
        try {
            Connection con = datasource.getConnection();
            CallableStatement stmt = con.prepareCall("call save(?, ?, ?)");
            stmt.setString(1, request.getUsername());
            stmt.setString(2, request.getPassword());
            stmt.setString(3, request.getEmail());
            stmt.execute();
            
            GlobalException ex = new GlobalException();
            ResultSet set = stmt.getResultSet();
            set.next();
            int ans = set.getInt(1);
            if (ans == 401){
                ex.addError(new ViolationDTO("", ""));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
}
