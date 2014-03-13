/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.indianhippy.data;

/**
 *
 * @author achava
 */
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class ClipBoardHistoryStore {

    public void insertIntoTable(String tableName,String clipBoard,String timestamp) {
        try(
                Connection conn=    getConnection();
                PreparedStatement prep = conn.prepareStatement(
                "insert into "+tableName+"(clipboard, timestamp) values (?,?)");
           ){
        prep.setBytes(1, clipBoard.getBytes());
        prep.setString(2, timestamp);
        prep.addBatch();
        conn.setAutoCommit(false);
        prep.executeBatch();
        conn.setAutoCommit(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void setDBSystemDir() {
    String userHomeDir = System.getProperty("user.home", ".");
    String systemDir = userHomeDir + "/.clipboardHistry";
        // Set the db system directory.
    System.setProperty("derby.system.home", systemDir);
}
    public  Connection getConnection(){
        setDBSystemDir();
        try {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection conn = null;
        String strUrl = "jdbc:derby:ClipBoardHistoy;create=true";
            conn = DriverManager.getConnection(strUrl);
            return conn;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public String[] getTablesFromDB(){
        try(
                Statement stat = getConnection().createStatement();
                ResultSet rs = stat.executeQuery("select tablename from sys.systables where tablename like 'CLIPHISTORY%'");
           ){
                StringBuffer tableNames=new StringBuffer();
               
                while(rs.next()){
                    tableNames.append(rs.getString("tablename"));
                    tableNames.append(",");
                    
                }
                if(tableNames.length()>0){
                    String tableNamesStr=tableNames.substring(0,tableNames.length()-1);
                    
                    return(tableNamesStr.split(","));
                }
                
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void createTable(String tableName){
        try(
                Statement stat = getConnection().createStatement();
            ){
            stat.executeUpdate("create table "+tableName+" (ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),clipboard BLOB,timestamp VARCHAR(100))");
        }catch(Exception e){
            System.out.println("Table already exists");
        }
    }
    public LinkedHashMap[] getValuesFromTable(String tableName){
        try(
                Statement stat = getConnection().createStatement();
                ResultSet rs = stat.executeQuery("select * from "+tableName+" order by ID desc");
           ){
        LinkedHashMap clipsFromTableData=new LinkedHashMap<String,String>();
        LinkedHashMap clipsFromTableTimestamp=new LinkedHashMap<String,String>();
        while (rs.next()) {
            String id=rs.getString(1);
            // InputStream bstream=rs.getBlob(2).getBinaryStream();
            //String inputStreamString = new Scanner(bstream,"UTF-8").useDelimiter("\\A").next();
            String inputStreamString=new String(rs.getBytes(2),"UTF-8");
            clipsFromTableData.put(id,inputStreamString);
            clipsFromTableTimestamp.put(id,rs.getString(3));
        }
        LinkedHashMap output[]={clipsFromTableData,clipsFromTableTimestamp};
        return(output);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void deleteRowFromTable(String tableName,String clipBoard,String timestamp) {
        try(
                Connection conn=    getConnection();
                PreparedStatement prep = conn.prepareStatement(
                "delete from "+tableName+" where timestamp=?");
           ){
        
        prep.setString(1, timestamp);
        prep.addBatch();
        conn.setAutoCommit(false);
        prep.executeBatch();
        conn.setAutoCommit(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
      public void deleteRowFromTableBasedOnID(String tableName,String id) {
        try(
                Connection conn=    getConnection();
                PreparedStatement prep = conn.prepareStatement(
                "delete from "+tableName+" where id=?");
           ){
        
        prep.setString(1, id);
        prep.addBatch();
        conn.setAutoCommit(false);
        prep.executeBatch();
        conn.setAutoCommit(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void deleteRowsFromTable(String tableName){
        try(
                Connection conn=    getConnection();
                Statement st = conn.createStatement();
           ){
        
       
        st.execute("delete from "+tableName);
        conn.setAutoCommit(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
