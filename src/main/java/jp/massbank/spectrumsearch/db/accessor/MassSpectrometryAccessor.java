package jp.massbank.spectrumsearch.db.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.massbank.spectrumsearch.db.entity.MassSpectrometry;
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
	
	public List<String> getValuesByType(String type) {
		String sql = "SELECT DISTINCT " + MassSpectrometry.Columns.MASS_SPECTROMETRY_VALUE + 
				" FROM " + MassSpectrometry.TABLE + 
				" WHERE " + MassSpectrometry.Columns.MASS_SPECTROMETRY_TYPE + " = '" + type + "'";
		return listString(sql);
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
