package jp.massbank.spectrumsearch.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jp.massbank.spectrumsearch.entity.db.Instrument;

import org.apache.log4j.Logger;
import org.junit.Test;

public class DbAccessorTest {
  static final Logger LOGGER = Logger.getLogger(DbAccessorTest.class);

  @Test
  public void testGetConnection() throws SQLException {

    Connection conn = OldDbAccessor.getConnection();
    LOGGER.info(conn.getMetaData().getURL());
  }

  @Test
  public void testGetAllInstrument() throws SQLException {
    OldDbAccessor.getConnection();
    List<Instrument> result = OldDbAccessor.getAllInstrument();
    LOGGER.info(result);
  }
  
  @Test
  public void testGetMsType() throws SQLException {
    OldDbAccessor.getConnection();
    List<String> result = OldDbAccessor.getMsType();
    LOGGER.info(result);
  }
  
  @Test
  public void testGetSpectrumNameByName() throws SQLException {
    OldDbAccessor.getConnection();
    List<String> result = OldDbAccessor.getSpectrumNameByName(null, null);
    LOGGER.info(result.size());
    for (String str : result){
      
      LOGGER.info(str);
    }
    result = OldDbAccessor.getSpectrumNameByName("GABA", "end");
    LOGGER.info(result.size());
    for (String str : result){
      LOGGER.info(str);
    }
    result = OldDbAccessor.getSpectrumNameByName("[M+H]+", "start");
    LOGGER.info(result.size());
    for (String str : result){
      LOGGER.info(str);
    }

  }
  
  @Test
  public void testGetSpectrumData() throws SQLException {
    OldDbAccessor.getConnection();
    String result = OldDbAccessor.getSpectrumData("KO000001", false, 0);
    LOGGER.info(result);
  }
}
