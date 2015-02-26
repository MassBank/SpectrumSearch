package jp.massbank.spectrumsearch.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.massbank.spectrumsearch.entity.db.Precursor;
import jp.massbank.spectrumsearch.util.QueryBuilder;

public class PrecursorAccessor extends AbstractDbAccessor<Precursor>  {

	@Override
	protected Precursor convert(ResultSet rs) throws SQLException {
		Precursor result = new Precursor();
		result.setPrecursorId(rs.getInt(Precursor.Columns.PRECURSOR_ID));
		result.setPrecursorMz(rs.getDouble(Precursor.Columns.PRECURSOR_MZ));
		result.setCompoundId(rs.getString(Precursor.Columns.COMPOUND_ID));
		return result;
	}
	
	public void addBatchInsert(Precursor t) {
		addBatch(getInsertQuery(t));
	}
	
	public List<Precursor> getPrecursorsByCompoundId(String compoundId) {
		String sql = "select * from " + Precursor.TABLE +" where " + Precursor.Columns.COMPOUND_ID +" = '" + compoundId + "'";
		return listGeneric(sql);
	}

	@Override
	public void insert(Precursor t) {
		insert(getInsertQuery(t));
	}
	
	private String getInsertQuery(Precursor t) {
		return "INSERT INTO " + Precursor.TABLE + " " +
				"(" + 
					Precursor.Columns.PRECURSOR_MZ + "," + 
					Precursor.Columns.COMPOUND_ID + 
				") values (" +
					t.getPrecursorMz() + "," +
					"'" + t.getCompoundId() + "')";
	}

	@Override
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + Precursor.TABLE;
		delete(deleteQuery);
	}

	@Override
	public void dropTable() {
		executeStatement(QueryBuilder.getDropTable(Precursor.TABLE));
	}

	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + Precursor.TABLE + " ");
		sb.append("(");
		sb.append(Precursor.Columns.PRECURSOR_ID + " INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT PRECURSOR_PK PRIMARY KEY,");
		sb.append(Precursor.Columns.PRECURSOR_MZ + " FLOAT,");
		sb.append(Precursor.Columns.COMPOUND_ID + " VARCHAR(20)");
		sb.append(")");
		executeStatement(sb.toString());
	}

}
