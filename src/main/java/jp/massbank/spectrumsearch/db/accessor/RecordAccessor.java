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
		return result;
	}
	
	public List<Record> getRecordListByName(String searchName, String wcValue) {
		String sql = "SELECT " + 
				Record.Columns.RECORD_ID + "," + 
				Record.Columns.RECORD_TITLE + 
				" FROM " + Record.TABLE;
		return listGeneric(sql);
	}

	public void insertRecord(Record record) {
		String insertQuery = "INSERT INTO " + Record.TABLE + " " +
				"(" + 
					Record.Columns.RECORD_ID + "," + 
					Record.Columns.RECORD_TITLE + 
				") values ("
					+ "'" + record.getId() + "',"
					+ "'" + record.getTitle() + "')";
		insert(insertQuery);
	}
	
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + Record.TABLE;
		delete(deleteQuery);
	}
	
	@Override
	public void dropTable() {
		execStmt(QueryBuilder.getDropTable(Record.TABLE));
	}

	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + Record.TABLE + " ");
		sb.append("(");
		sb.append(Record.Columns.RECORD_ID + " VARCHAR(20) NOT NULL CONSTRAINT RECORD_PK PRIMARY KEY,");
		sb.append(Record.Columns.RECORD_TITLE + " VARCHAR(255)");
		sb.append(")");
		execStmt(sb.toString());
	}

}
