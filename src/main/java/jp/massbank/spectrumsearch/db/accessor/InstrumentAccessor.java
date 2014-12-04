package jp.massbank.spectrumsearch.db.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.db.entity.Instrument;

public class InstrumentAccessor extends AbstractDbAccessor<Instrument> {

	@Override
	protected Instrument convert(ResultSet rs) throws SQLException {
		Instrument result = new Instrument();
		result.setNo(rs.getInt(1));
		result.setType(rs.getString(2));
		result.setName(rs.getString(3));
		return result;
	}
	
	public List<Instrument> getAllInstruments() {
		List<Instrument> result = new ArrayList<>();
		String selectQuery = "SELECT " + 
				Instrument.Columns.INSTRUMENT_NO + ", " + 
				Instrument.Columns.INSTRUMENT_TYPE + ", " + 
				Instrument.Columns.INSTRUMENT_NAME + 
				" FROM " + Instrument.TABLE;
		result = listGeneric(selectQuery);
		return result;
	}

	public void insertInstrument(Instrument instrument) {
		String insertQuery = "INSERT INTO " + Instrument.TABLE + " " +
				"(" + 
					Instrument.Columns.INSTRUMENT_TYPE + "," + 
					Instrument.Columns.INSTRUMENT_NAME + 
				") values ('" + 
					instrument.getType() + "','" + 
					instrument.getName() + 
				"')";
		insert(insertQuery);
	}
	
//	public Instrument insertInstrument(Instrument instrument) {
//		String insertQuery = "INSERT INTO " + Instrument.TABLE + " " +
//				"(" + 
//				Instrument.Columns.INSTRUMENT_TYPE + "," + 
//				Instrument.Columns.INSTRUMENT_NAME + 
//				") values ('" + 
//				instrument.getType() + "','" + 
//				instrument.getName() + 
//				"')";
//		int no = insertWithReturn(insertQuery);
//		return getInstrumentById(no);
//	}
	
	public void bulkInsertInstrument(Instrument instrument) {
		String insertQuery = "INSERT IGNORE INTO " + Instrument.TABLE + " " +
				"(" + 
				Instrument.Columns.INSTRUMENT_TYPE + "," + 
				Instrument.Columns.INSTRUMENT_NAME + 
				") values ('" + 
				instrument.getType() + "','" + 
				instrument.getName() + 
				"')";
		addBatch(insertQuery);
	}
	
	public Instrument getInstrumentById(int no) {
		String selectQuery = "SELECT " + 
				Instrument.Columns.INSTRUMENT_NO + ", " + 
				Instrument.Columns.INSTRUMENT_TYPE + ", " + 
				Instrument.Columns.INSTRUMENT_NAME + 
				" FROM " + Instrument.TABLE + 
				" WHERE " + Instrument.Columns.INSTRUMENT_NO + " = " + no;
		return uniqueGeneric(selectQuery);
	}
	
	public Instrument getInstrumentByType(String type) {
		String selectQuery = "SELECT " + 
				Instrument.Columns.INSTRUMENT_NO + ", " + 
				Instrument.Columns.INSTRUMENT_TYPE + ", " + 
				Instrument.Columns.INSTRUMENT_NAME + 
				" FROM " + Instrument.TABLE + 
				" WHERE " + Instrument.Columns.INSTRUMENT_TYPE + " = '" + type + "'";
		return uniqueGeneric(selectQuery);
	}
	
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + Instrument.TABLE;
		delete(deleteQuery);
	}

	public void dropTable() {
		execStmt("DROP TABLE INSTRUMENT");
	}
	
	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE INSTRUMENT ");
		sb.append("(");
		sb.append("INSTRUMENT_NO INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT INSTRUMENT_PK PRIMARY KEY,");
		sb.append("INSTRUMENT_TYPE 	VARCHAR(255),");
		sb.append("INSTRUMENT_NAME 	VARCHAR(255)");
		sb.append(")");
		execStmt(sb.toString());
	}
	
}
