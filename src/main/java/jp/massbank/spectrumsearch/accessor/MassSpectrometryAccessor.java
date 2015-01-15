package jp.massbank.spectrumsearch.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.massbank.spectrumsearch.entity.db.MassSpectrometry;
import jp.massbank.spectrumsearch.entity.db.Peak;
import jp.massbank.spectrumsearch.util.QueryBuilder;

public class MassSpectrometryAccessor extends AbstractDbAccessor<MassSpectrometry> {

	@Override
	protected MassSpectrometry convert(ResultSet rs) throws SQLException {
		MassSpectrometry result = new MassSpectrometry();
		result.setId(rs.getInt(MassSpectrometry.Columns.MASS_SPECTROMETRY_ID));
		result.setType(rs.getString(MassSpectrometry.Columns.MASS_SPECTROMETRY_TYPE));
		result.setValue(rs.getString(MassSpectrometry.Columns.MASS_SPECTROMETRY_VALUE));
		result.setRecordId(rs.getString(MassSpectrometry.Columns.RECORD_ID));
		return result;
	}
	
	public List<String> getDistinctValuesBySubTag(String type) {
		String sql = "SELECT DISTINCT " + MassSpectrometry.Columns.MASS_SPECTROMETRY_VALUE + 
				" FROM " + MassSpectrometry.TABLE + 
				" WHERE " + MassSpectrometry.Columns.MASS_SPECTROMETRY_TYPE + " = '" + type + "'";
		return listString(sql);
	}
	
	public void addBatchInsert(MassSpectrometry massSpectrometry) {
		String insertQuery = "INSERT INTO " + MassSpectrometry.TABLE + " " +
				"(" + 
				MassSpectrometry.Columns.MASS_SPECTROMETRY_TYPE + "," + 
				MassSpectrometry.Columns.MASS_SPECTROMETRY_VALUE + "," +
				MassSpectrometry.Columns.RECORD_ID + 
				") values (" +
				"'" + massSpectrometry.getType() + "'," +
				"'" + massSpectrometry.getValue() + "'," +
				"'" + massSpectrometry.getRecordId() + "'" +
				")";
		addBatch(insertQuery);
	}
	
	public void addBatchInsert(List<MassSpectrometry> massSpectrometries) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + MassSpectrometry.TABLE + " ");
		sb.append("(");
		sb.append(MassSpectrometry.Columns.MASS_SPECTROMETRY_TYPE + ",");
		sb.append(MassSpectrometry.Columns.MASS_SPECTROMETRY_VALUE + ",");
		sb.append(MassSpectrometry.Columns.RECORD_ID);
		sb.append(") values ");
		for (MassSpectrometry massSpectrometry : massSpectrometries) {
			sb.append("(");
			sb.append("'" + massSpectrometry.getType() + "',");
			sb.append("'" + massSpectrometry.getValue() + "',");
			sb.append("'" + massSpectrometry.getRecordId() + "'");
			sb.append("),");
		}
		String sql = sb.toString().trim();
		String insertQuery = sql.substring(0, sql.length() - 1);
		addBatch(insertQuery);
	}
	
	public void executeBatchInsert(List<MassSpectrometry> massSpectrometries) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + MassSpectrometry.TABLE + " ");
		sb.append("(");
		sb.append(MassSpectrometry.Columns.MASS_SPECTROMETRY_TYPE + ",");
		sb.append(MassSpectrometry.Columns.MASS_SPECTROMETRY_VALUE + ",");
		sb.append(MassSpectrometry.Columns.RECORD_ID);
		sb.append(") values (?, ?, ?)");
		for(int i = 1; i < massSpectrometries.size(); i++) {
			sb.append(",(?, ?, ?)");
		}
		
		createPreparedStatement(sb.toString());
		int i = 1;
		for(MassSpectrometry massSpectrometry : massSpectrometries) {
			pstmt.setString(i++, massSpectrometry.getType());
			pstmt.setString(i++, massSpectrometry.getValue());
			pstmt.setString(i++, massSpectrometry.getRecordId());
		}
		executeAndClosePreparedStatement();
	}
	
	@Override
	public void insert(MassSpectrometry massSpectrometry) {
		String insertQuery = "INSERT INTO " + MassSpectrometry.TABLE + " " +
				"(" + 
					MassSpectrometry.Columns.MASS_SPECTROMETRY_TYPE + "," + 
					MassSpectrometry.Columns.MASS_SPECTROMETRY_VALUE + "," +
					MassSpectrometry.Columns.RECORD_ID + 
				") values (" +
					"'" + massSpectrometry.getType() + "'," +
					"'" + massSpectrometry.getValue() + "'," +
					"'" + massSpectrometry.getRecordId() + "'" +
				")";
		insert(insertQuery);
	}
	
	@Override
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + MassSpectrometry.TABLE;
		delete(deleteQuery);
	}

	@Override
	public void dropTable() {
		executeStatement(QueryBuilder.getDropTable(MassSpectrometry.TABLE));
	}
	
	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE MASS_SPECTROMETRY ");
		sb.append("(");
		sb.append(MassSpectrometry.Columns.MASS_SPECTROMETRY_ID + " INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT MASS_SPECTROMETRY_PK PRIMARY KEY,");
		sb.append(MassSpectrometry.Columns.MASS_SPECTROMETRY_TYPE + " VARCHAR(255),");
		sb.append(MassSpectrometry.Columns.MASS_SPECTROMETRY_VALUE + " VARCHAR(255),");
		sb.append(MassSpectrometry.Columns.RECORD_ID + " VARCHAR(20)");
		sb.append(")");
		executeStatement(sb.toString());
	}

}
