package jp.massbank.spectrumsearch.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jp.massbank.spectrumsearch.entity.db.Instrument;

import org.apache.log4j.Logger;


public class OldDbAccessor {
  static final Logger LOGGER = Logger.getLogger(OldDbAccessor.class);
  static Connection conn = null;

  public static Connection getConnection() throws SQLException {

    Properties connectionProps = new Properties();
//    connectionProps.put("user", "root");
//    connectionProps.put("password", "");

    conn = DriverManager.getConnection("jdbc:derby:./src/test/testdata/massbankdb", connectionProps);
    // TODO this url should be changed to refer inside of this project.
//    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/keio", connectionProps);

    LOGGER.info("Connected to database");
    DatabaseMetaData metadata = conn.getMetaData();
    LOGGER.info(metadata.getDatabaseProductName());
    LOGGER.info(metadata.getURL());

    return conn;
  }


  public static List<Instrument> getAllInstrument() throws SQLException {
    // long numberOfRows = -1;
    // StringBuilder sb = new StringBuilder();
    List<Instrument> result = new ArrayList<>();
    try (PreparedStatement ps =
        conn.prepareStatement("SELECT INSTRUMENT_NO, INSTRUMENT_TYPE, INSTRUMENT_NAME FROM INSTRUMENT")) {

      ResultSet rs = ps.executeQuery();
      while (rs.next()) {

        Instrument ins = new Instrument();
        ins.setId(rs.getInt(1));
        ins.setType(rs.getString(2));
        result.add(ins);
      }
    }
    return result;
  }

  public static List<String> getMsType() throws SQLException {
    List<String> result = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT MS_TYPE FROM RECORD")) {
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        result.add(rs.getString(1));
      }
    }
    return result;
  }

  public static List<String> getSpectrumNameByName(String name, String match) throws SQLException {
    LOGGER.info("name   "+ name);
    LOGGER.info("match  "+ match);
    List<String> result = new ArrayList<>();
    StringBuilder sql = new StringBuilder("select NAME, ID from SPECTRUM");

    if (name != null && !"".equals(name)) {
      sql.append("  where NAME ");
      // JavaDB does not support left() and instr(). we need change table structure.
      // devide name column by ';' . 
//      sql.append("  where left(NAME,instr(NAME,';')-1) ");
      if (match == null) {
        sql.append("= ?");
      } else {
        sql.append("like ?");
      }
    }
//    sql.append("  limit 20");
    LOGGER.info(sql);
    try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {


      if ("both".equals(match)) {
        ps.setString(1, "%" + name + "%");
//        sql.append("like '%$name%'");
      } else if ("start".equals(match)) {
        ps.setString(1, "%" + name );
        // $sql .= "like '\%$name'";
//        sql.append("like '%$name'");
      } else if ("end".equals(match)) {
        ps.setString(1,  name + "%");
//        sql.append("like '$name%'");
        // $sql .= "like '$name\%'";
      } else   if (name != null && !name.isEmpty()) {
        ps.setString(1,  name );
        // $sql .= "='$name'";
      }

      
      
      
      
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        result.add(String.format("%s\t%s\t0", rs.getString(1), rs.getString(2) ));
      }
    }
    return result;
  }

  public static String getSpectrumData(String id) throws SQLException { 
    return getChildInfo(id);
    
  }  
  // TODO implement this as GetSpectrumData.cgi
  public static String getSpectrumData(String id, boolean relation, int ion) throws SQLException { 
    if ( !relation) {
      return getChildInfo(id);
  }else{
    // TODO do for this case.
    
  }
    
    
    return "";
  }
  static String getChildInfo(String id)throws SQLException {
   StringBuilder sb = new StringBuilder();  
    try (PreparedStatement ps = conn.prepareStatement("select MZ, RELATIVE_ from PEAK where ID=? order by MZ")) {
      ps.setString(1,  id );
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        sb.append(String.format("%s\t%s\t\t", rs.getString(1), rs.getString(2)));
      }
    }
    if(sb.toString().equals("")){
      sb.append("0\t0\t\t");
    }
    sb.append("::");
    
    try (PreparedStatement ps = conn.prepareStatement("select NAME, PRECURSOR_MZ from SPECTRUM where ID = ?")) {
      ps.setString(1,  id );
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        
        if(!rs.getString(1).equals("")){
          sb.append(String.format("\tname=%s\t",rs.getString(1) ));
        }
        if(!rs.getString(2 ).equals("")){
          sb.append(String.format("\tprecursor=%s\t",rs.getString(2) ));
        }
        sb.append(String.format("%s\t%s\t\t", rs.getString(1), rs.getString(2)));
      }
      sb.append(String.format("\tid=%s\t\n", id));
    }
    return sb.toString();
    
  }
}
