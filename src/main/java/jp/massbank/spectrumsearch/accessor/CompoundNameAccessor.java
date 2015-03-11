package jp.massbank.spectrumsearch.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.massbank.spectrumsearch.entity.db.CompoundName;
import jp.massbank.spectrumsearch.util.QueryBuilder;

public class CompoundNameAccessor extends AbstractDbAccessor<CompoundName> {

	@Override
	protected CompoundName convert(ResultSet rs) throws SQLException {
		CompoundName result = new CompoundName();
		result.setCompoundNameId(rs.getInt(CompoundName.Columns.COMPOUND_NAME_ID));
		result.setName(rs.getString(CompoundName.Columns.NAME));
		result.setCompoundId(rs.getString(CompoundName.Columns.COMPOUND_ID));
		return result;
	}
	
	public void addBatchInsert(CompoundName compoundName) {
		addBatch(getInsertQuery(compoundName));
	}

	@Override
	public void insert(CompoundName compoundName) {
		insert(getInsertQuery(compoundName));
	}

	@Override
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + CompoundName.TABLE;
		delete(deleteQuery);
	}

	@Override
	public void dropTable() {
		executeStatement(QueryBuilder.getDropTable(CompoundName.TABLE));
	}

	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + CompoundName.TABLE + " ");
		sb.append("(");
		sb.append(CompoundName.Columns.COMPOUND_NAME_ID + " INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT COMPOUND_NAME_PK PRIMARY KEY,");
		sb.append(CompoundName.Columns.NAME + " VARCHAR(255),");
		sb.append(CompoundName.Columns.COMPOUND_ID + " VARCHAR(20)");
		sb.append(")");
		executeStatement(sb.toString());
	}
	
	private String getInsertQuery(CompoundName compoundName) {
		return "INSERT INTO " + CompoundName.TABLE + " " +
				"(" + 
				CompoundName.Columns.NAME + "," + 
				CompoundName.Columns.COMPOUND_ID + 
			") values (" +
				"'" + compoundName.getName() + "'," +
				"'" + compoundName.getCompoundId() + "')";
	}

}
