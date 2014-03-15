/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.indianhippy.jclip.data;

/**
 *
 * @author achava
 */
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Set;

public class ClipBoardHistoryStore {

    public void insertIntoTable(String tableName,String clipText,String timestamp) {
        try(
                Connection conn=    getConnection();
                PreparedStatement prep = conn.prepareStatement(
                "insert into "+tableName+"(cliptext, timestamp) values (?,?)");
           ){
        prep.setString(1, clipText);
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
    String systemDir = userHomeDir + "/.clipHistory";
        // Set the db system directory.
    System.setProperty("derby.system.home", systemDir);
}
    public  Connection getConnection(){
        setDBSystemDir();
        try {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection conn = null;
        String strUrl = "jdbc:derby:ClipHistory;create=true";
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
                ResultSet rs = stat.executeQuery("select tablename from sys.systables where tablename like 'CLIPHISTORY%' order by tablename desc");
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
            stat.executeUpdate("create table "+tableName+" (ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),cliptext CLOB,clipimage blob,timestamp VARCHAR(100))");
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
            String inputStreamString=rs.getString(2);
            clipsFromTableData.put(id,inputStreamString);
            clipsFromTableTimestamp.put(id,rs.getString(4));
        }
        LinkedHashMap output[]={clipsFromTableData,clipsFromTableTimestamp};
        return(output);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
     public LinkedHashMap[] getValuesFromTable(String tableName,String findStr){
         
        try(
             Statement stat = getConnection().createStatement();
             ResultSet rs = stat.executeQuery("select * from "+tableName+" WHERE lower(CAST(cliptext AS LONG VARCHAR)) LIKE lower('%"+findStr+"%') order by ID desc");    
           ){
        LinkedHashMap clipsFromTableData=new LinkedHashMap<String,String>();
        LinkedHashMap clipsFromTableTimestamp=new LinkedHashMap<String,String>();
        while (rs.next()) {
            String id=rs.getString(1);
            // InputStream bstream=rs.getBlob(2).getBinaryStream();
            //String inputStreamString = new Scanner(bstream,"UTF-8").useDelimiter("\\A").next();
            //String inputStreamString=new String(rs.getString(2),"UTF-8");
            clipsFromTableData.put(id,rs.getString(2));
            clipsFromTableTimestamp.put(id,rs.getString(4));
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
    public LinkedHashMap[] findClipInDB(String findStr){
        String tableNames[]=getTablesFromDB();
        LinkedHashMap clipsFromTableData=new LinkedHashMap<String,String>();
        LinkedHashMap clipsFromTableTimestamp=new LinkedHashMap<String,String>();
        for(int i=0;i<tableNames.length;i++){
            LinkedHashMap output[]=getValuesFromTable(tableNames[i],findStr);
            if(output!=null && output.length>1){
            LinkedHashMap<String,String> data=output[0];
            LinkedHashMap<String,String> timeStamp=output[1];
            Set<String> keys=data.keySet();
             for(String key:keys){
                String dataValue=data.get(key);
                String timeStampStr=timeStamp.get(key);
                try{
                    clipsFromTableData.put(key, dataValue);
                    clipsFromTableTimestamp.put(key,timeStampStr);
                
                }catch(Exception sqle){
                    sqle.printStackTrace();
                }
                }
            }
        }
        LinkedHashMap findResults[]={clipsFromTableData,clipsFromTableTimestamp};
        return(findResults);
    }
    
}
