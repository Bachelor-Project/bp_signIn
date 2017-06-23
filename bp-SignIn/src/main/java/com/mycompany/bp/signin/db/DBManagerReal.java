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
import java.util.ArrayList;
import java.util.List;
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
        User result = null;
        try {
            Connection con = datasource.getConnection();
            CallableStatement stmt =  con.prepareCall("call authenticate(?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.execute();
            
            ResultSet rsSet = stmt.getResultSet();
            if(rsSet.next()){
                result = getUserWithRoles(con, username);
            }
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public User saveUser(SignUpRequestDTO request) throws GlobalException {
        User user = null;
        try {
            Connection con = datasource.getConnection();
            saveUserInDB(con, request);
            user = getUserWithRoles(con, request.getUsername());
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
    
    private void saveUserInDB(Connection con, SignUpRequestDTO request) throws GlobalException {
        try {
            CallableStatement stmt = con.prepareCall("call save(?, ?)");
            stmt.setString(1, request.getUsername());
            stmt.setString(2, request.getPassword());
            stmt.execute();
            
            GlobalException ex = new GlobalException();
            ResultSet set = stmt.getResultSet();
            set.next();
            int ans = set.getInt(1);
            if (ans == 400){
                ex.addError(new ViolationDTO("username", "ასეთი username უკვე არსებობს."));
                throw ex;
            }
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private User getUserWithRoles(Connection con, String username){
        User result = null;
        try {
            CallableStatement stmtUser = con.prepareCall("call select_user(?)");
            stmtUser.setString(1, username);
            stmtUser.execute();
            
            ResultSet rsSet = stmtUser.getResultSet();
            if (rsSet.next()){
                result = new User();
                result.setId(rsSet.getInt(1));
                result.setUsername(rsSet.getString(2));

                CallableStatement stmtRoles = con.prepareCall("call select_user_roles(?)");
                stmtRoles.setInt(1, result.getId());
                stmtRoles.execute();

                ResultSet rolesSet = stmtRoles.getResultSet();
                List<String> roles = new ArrayList<>();
                while(rolesSet.next()){
                    roles.add(rolesSet.getString(1));
                }
                
                result.setRoles(roles.toArray(new String[roles.size()]));
                stmtRoles.close();
            }
            stmtUser.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public void saveToken(String token, int id) {
        try {
            Connection con = datasource.getConnection();
            CallableStatement stmt = con.prepareCall("call save_token(?, ?)");
            stmt.setString(1, token);
            stmt.setInt(2, id);
            stmt.execute();
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void removeToken(String token) {
//        try {
//            Connection con = datasource.getConnection();
//            CallableStatement stmt = con.prepareCall("call remove_token(?)");
//            stmt.setString(1, token);
//            stmt.execute();
//            stmt.close();
//            con.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(DBManagerReal.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

}
