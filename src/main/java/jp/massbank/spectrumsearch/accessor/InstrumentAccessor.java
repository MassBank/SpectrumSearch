package jp.massbank.spectrumsearch.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jp.massbank.spectrumsearch.entity.db.Instrument;
import jp.massbank.spectrumsearch.util.QueryBuilder;

public class InstrumentAccessor extends AbstractDbAccessor<Instrument> {

	@Override
	protected Instrument convert(ResultSet rs) throws SQLException {
		Instrument result = new Instrument();
		result.setId(rs.getInt(Instrument.Columns.INSTRUMENT_ID));
		result.setType(rs.getString(Instrument.Columns.INSTRUMENT_TYPE));
//		result.setName(rs.getString(Instrument.Columns.INSTRUMENT_NAME));
		return result;
	}
	
	public List<Instrument> getAllInstruments() {
		List<Instrument> result = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + Instrument.TABLE;
		result = listGeneric(selectQuery);
		return result;
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
	
	public List<Instrument> getInstrumentsByTypes(List<String> types) {
		String selectQuery = "SELECT * FROM " + Instrument.TABLE + 
				" WHERE " + Instrument.Columns.INSTRUMENT_TYPE + " IN ('" + StringUtils.join(types, "','") + "')";
		return listGeneric(selectQuery);
	}
	
	public Instrument getInstrument(String type) {
		String selectQuery = "SELECT * FROM " + Instrument.TABLE + 
				" WHERE " +  Instrument.Columns.INSTRUMENT_TYPE + " = '" + type + "'";
		return uniqueGeneric(selectQuery);
	}
	
//	public Instrument getInstrument(String type, String name) {
//		String selectQuery = "SELECT * FROM " + Instrument.TABLE + 
//				" WHERE " + 
//				Instrument.Columns.INSTRUMENT_TYPE + " = '" + type + "' and " + 
//				Instrument.Columns.INSTRUMENT_NAME + " = '" + name + "'";
//		return uniqueGeneric(selectQuery);
//	}
	
	@Override
	public void insert(Instrument instrument) {
		String insertQuery = "INSERT INTO " + Instrument.TABLE + " " +
				"(" + 
				Instrument.Columns.INSTRUMENT_TYPE + 
				") values (" + 
				"'" + instrument.getType() + "')";
		insert(insertQuery);
	}
	
	@Override
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + Instrument.TABLE;
		delete(deleteQuery);
	}

	@Override
	public void dropTable() {
		executeStatement(QueryBuilder.getDropTable(Instrument.TABLE));
	}
	
	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + Instrument.TABLE +" ");
		sb.append("(");
		sb.append(Instrument.Columns.INSTRUMENT_ID + " INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT INSTRUMENT_PK PRIMARY KEY,");
		sb.append(Instrument.Columns.INSTRUMENT_TYPE + " VARCHAR(255)");
//		sb.append(Instrument.Columns.INSTRUMENT_NAME + " VARCHAR(255)");
		sb.append(")");
		executeStatement(sb.toString());
	}
	
}
