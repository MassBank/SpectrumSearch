package jp.massbank.spectrumsearch.db.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.massbank.spectrumsearch.db.entity.MassSpectrometry;

public class MassSpectrometryAccessor extends AbstractDbAccessor<MassSpectrometry> {

	@Override
	protected MassSpectrometry convert(ResultSet rs) throws SQLException {
		MassSpectrometry result = new MassSpectrometry();
		result.setNo(rs.getInt(1));
		result.setType(rs.getString(2));
		result.setValue(rs.getString(3));
		result.setRecordId(rs.getString(4));
		return result;
	}
	
	public List<String> getValuesByType(String type) {
		String sql = "SELECT DISTINCT " + MassSpectrometry.Columns.MASS_SPECTROMETRY_VALUE + 
				" FROM " + MassSpectrometry.TABLE + 
				" WHERE " + MassSpectrometry.Columns.MASS_SPECTROMETRY_TYPE + " = '" + type + "'";
		return listString(sql);
	}
	
	public void insertMassSpectrometry(MassSpectrometry massSpectrometry) {
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
	
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + MassSpectrometry.TABLE;
		delete(deleteQuery);
	}

	public void dropTable() {
		execStmt("DROP TABLE MASS_SPECTROMETRY");
	}
	
	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE MASS_SPECTROMETRY ");
		sb.append("(");
		sb.append("MASS_SPECTROMETRY_NO INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT MASS_SPECTROMETRY_PK PRIMARY KEY,");
		sb.append("MASS_SPECTROMETRY_TYPE 	VARCHAR(255),");
		sb.append("MASS_SPECTROMETRY_VALUE 	VARCHAR(255),");
		sb.append("RECORD_ID VARCHAR(100)");
		sb.append(")");
		execStmt(sb.toString());
	}

}
