package jp.massbank.spectrumsearch.db.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.db.entity.Instrument;
import jp.massbank.spectrumsearch.util.QueryBuilder;

public class InstrumentAccessor extends AbstractDbAccessor<Instrument> {

	@Override
	protected Instrument convert(ResultSet rs) throws SQLException {
		Instrument result = new Instrument();
		result.setId(rs.getInt(Instrument.Columns.INSTRUMENT_ID));
		result.setType(rs.getString(Instrument.Columns.INSTRUMENT_TYPE));
		result.setName(rs.getString(Instrument.Columns.INSTRUMENT_NAME));
		result.setRecordId(rs.getString(Instrument.Columns.RECORD_ID));
		return result;
	}
	
	public List<Instrument> getAllInstruments() {
		List<Instrument> result = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + Instrument.TABLE;
		result = listGeneric(selectQuery);
		return result;
	}

	public void insertInstrument(Instrument instrument) {
		String insertQuery = "INSERT INTO " + Instrument.TABLE + " " +
				"(" + 
					Instrument.Columns.INSTRUMENT_TYPE + "," + 
					Instrument.Columns.INSTRUMENT_NAME + "," +
					Instrument.Columns.RECORD_ID + 
				") values (" +
					"'" + instrument.getType() + "'," + 
					"'" + instrument.getName() + "'," +
					"'" + instrument.getRecordId() + "')";
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
	
//	public void bulkInsertInstrument(Instrument instrument) {
//		String insertQuery = "INSERT IGNORE INTO " + Instrument.TABLE + " " +
//				"(" + 
//				Instrument.Columns.INSTRUMENT_TYPE + "," + 
//				Instrument.Columns.INSTRUMENT_NAME + 
//				") values ('" + 
//				instrument.getType() + "','" + 
//				instrument.getName() + 
//				"')";
//		addBatch(insertQuery);
//	}
	
	public Instrument getInstrumentById(int no) {
		String selectQuery = "SELECT * FROM " + Instrument.TABLE + 
				" WHERE " + Instrument.Columns.INSTRUMENT_ID + " = " + no;
		return uniqueGeneric(selectQuery);
	}
	
	public Instrument getInstrumentByType(String type) {
		String selectQuery = "SELECT * FROM " + Instrument.TABLE + 
				" WHERE " + Instrument.Columns.INSTRUMENT_TYPE + " = '" + type + "'";
		return uniqueGeneric(selectQuery);
	}
	
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + Instrument.TABLE;
		delete(deleteQuery);
	}

	public void dropTable() {
		execStmt(QueryBuilder.getDropTable(Instrument.TABLE));
	}
	
	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + Instrument.TABLE +" ");
		sb.append("(");
		sb.append(Instrument.Columns.INSTRUMENT_ID + " INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT INSTRUMENT_PK PRIMARY KEY,");
		sb.append(Instrument.Columns.INSTRUMENT_TYPE + " VARCHAR(255),");
		sb.append(Instrument.Columns.INSTRUMENT_NAME + " VARCHAR(255),");
		sb.append(Instrument.Columns.RECORD_ID + " VARCHAR(20)");
		sb.append(")");
		execStmt(sb.toString());
	}
	
}
