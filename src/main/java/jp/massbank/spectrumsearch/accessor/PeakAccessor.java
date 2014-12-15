package jp.massbank.spectrumsearch.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.massbank.spectrumsearch.entity.db.Peak;
import jp.massbank.spectrumsearch.util.QueryBuilder;

public class PeakAccessor extends AbstractDbAccessor<Peak> {

	@Override
	protected Peak convert(ResultSet rs) throws SQLException {
		Peak result = new Peak();
		result.setId(rs.getInt(Peak.Columns.PEAK_ID));
		result.setMz(rs.getDouble(Peak.Columns.MZ));
		result.setIntensity(rs.getFloat(Peak.Columns.INTENSITY));
		result.setRelativeIntensity(rs.getInt(Peak.Columns.RELATIVE_INTENSITY));
		result.setRecordId(rs.getString(Peak.Columns.RECORD_ID));
		return result;
	}
	
	public List<Peak> getOrderedPeakListByRecordId(String recordId) {
		String sql = "SELECT * from PEAK where " + Peak.Columns.RECORD_ID + "='" + recordId + "' order by " + Peak.Columns.MZ;
		return listGeneric(sql);
	}
	
	@Override
	public void insert(Peak peak) {
		String insertQuery = "INSERT INTO " + Peak.TABLE + " " +
				"(" + 
				Peak.Columns.MZ + "," + 
				Peak.Columns.INTENSITY + "," + 
				Peak.Columns.RELATIVE_INTENSITY + "," +  
				Peak.Columns.RECORD_ID + 
				") values (" +
				peak.getMz() + "," + 
				peak.getIntensity() + "," + 
				peak.getRelativeIntensity() + "," +
				"'" + peak.getRecordId() + "')";
		insert(insertQuery);
	}
	
	@Override
	public void deleteAll() {
		delete(QueryBuilder.getDeleteAll(Peak.TABLE));
	}

	@Override
	public void dropTable() {
		executeStatement(QueryBuilder.getDropTable(Peak.TABLE));
	}

	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + Peak.TABLE +" ");
		sb.append("(");
		sb.append(Peak.Columns.PEAK_ID + " INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT PEAK_PK PRIMARY KEY,");
		sb.append(Peak.Columns.MZ + " FLOAT,");
		sb.append(Peak.Columns.INTENSITY + " FLOAT,");
		sb.append(Peak.Columns.RELATIVE_INTENSITY + " INT,");
		sb.append(Peak.Columns.RECORD_ID + " VARCHAR(20)");
		sb.append(")");
		executeStatement(sb.toString());
	}

}
