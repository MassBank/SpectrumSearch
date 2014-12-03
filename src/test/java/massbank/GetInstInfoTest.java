package massbank;

import static org.junit.Assert.*;

import java.sql.SQLException;

import jp.massbank.spectrumsearch.db.DbAccessor;

import org.apache.log4j.Logger;
import org.junit.Test;

public class GetInstInfoTest {
  static final Logger LOGGER = Logger.getLogger(GetInstInfoTest.class);

  @Test
  public void testGetTypeGroup() throws SQLException {
    DbAccessor.getConnection();
    GetInstInfo sut = new GetInstInfo();
    LOGGER.info(sut.getTypeGroup());
  }

  @Test
  public void testGetMsAll() {
//    fail("Not yet implemented");
  }

}
