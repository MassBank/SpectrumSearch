package jp.massbank.spectrumsearch.db.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.massbank.spectrumsearch.db.entity.Record;
import jp.massbank.spectrumsearch.util.QueryBuilder;

public class RecordAccessor extends AbstractDbAccessor<Record> {

	@Override
	protected Record convert(ResultSet rs) throws SQLException {
		Record result = new Record();
		result.setId(rs.getString(Record.Columns.RECORD_ID));
		result.setTitle(rs.getString(Record.Columns.RECORD_TITLE));
		result.setMsType(rs.getString(Record.Columns.MS_TYPE));
		result.setFormula(rs.getString(Record.Columns.FORMULA));
		result.setExactMass(rs.getDouble(Record.Columns.EXACT_MASS));
		result.setInstrumentId(rs.getInt(Record.Columns.INSTRUMENT_ID));
		return result;
	}
	
	public Record getRecordById(String recordId) {
		String sql = "SELECT * FROM " + Record.TABLE + " WHERE " + Record.Columns.RECORD_ID + " = '" + recordId + "'";
		return uniqueGeneric(sql);
	}
	
	public List<Record> getRecords() {
		String sql = "SELECT * FROM " + Record.TABLE;
		return listGeneric(sql);
	}

	public List<Record> getRecordListByName(String searchName, String wcValue) {
		// TODO
		String sql = "SELECT * FROM " + Record.TABLE;
		return listGeneric(sql);
	}

	@Override
	public void insert(Record record) {
		String insertQuery = "INSERT INTO " + Record.TABLE + " " +
				"(" + 
					Record.Columns.RECORD_ID + "," + 
					Record.Columns.RECORD_TITLE + "," + 
					Record.Columns.MS_TYPE + "," + 
					Record.Columns.FORMULA + "," + 
					Record.Columns.EXACT_MASS + "," + 
					Record.Columns.INSTRUMENT_ID + 
				") values (" +
					"'" + record.getId() + "'," + 
					"'" + record.getTitle() + "'," +
					"'" + record.getMsType() + "'," +
					"'" + record.getFormula() + "'," +
					record.getExactMass() + "," +
					record.getInstrumentId() + ")";
		insert(insertQuery);
	}
	
	@Override
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + Record.TABLE;
		delete(deleteQuery);
	}
	
	@Override
	public void dropTable() {
		executeStatement(QueryBuilder.getDropTable(Record.TABLE));
	}

	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + Record.TABLE + " ");
		sb.append("(");
		sb.append(Record.Columns.RECORD_ID + " VARCHAR(20) NOT NULL CONSTRAINT RECORD_PK PRIMARY KEY,");
		sb.append(Record.Columns.RECORD_TITLE + " VARCHAR(255),");
		sb.append(Record.Columns.MS_TYPE + " VARCHAR(10),");
		sb.append(Record.Columns.FORMULA + " VARCHAR(255),");
		sb.append(Record.Columns.EXACT_MASS + " FLOAT,");
		sb.append(Record.Columns.INSTRUMENT_ID + " INT");
		sb.append(")");
		executeStatement(sb.toString());
	}

}
