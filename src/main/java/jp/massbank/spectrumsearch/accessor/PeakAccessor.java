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
		result.setCompoundId(rs.getString(Peak.Columns.COMPOUND_ID));
		return result;
	}
	
	public List<Peak> getOrderedPeakListByRecordId(String recordId) {
		String sql = "SELECT * from PEAK where " + Peak.Columns.COMPOUND_ID + "='" + recordId + "' order by " + Peak.Columns.MZ;
		return listGeneric(sql);
	}
	
	public void addBatchInsert(Peak peak) {
		addBatch(getInsertQuery(peak));
	}
	
	public void addBatchInsert(List<Peak> peaks) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + Peak.TABLE + " ");
		sb.append("(");
		sb.append(Peak.Columns.MZ + ",");
		sb.append(Peak.Columns.INTENSITY + ",");
		sb.append(Peak.Columns.RELATIVE_INTENSITY + ",");
		sb.append(Peak.Columns.COMPOUND_ID);
		sb.append(") values ");
		for (Peak peak : peaks) {
			sb.append("(");
			sb.append(peak.getMz() + ",");
			sb.append(peak.getIntensity() + ",");
			sb.append(peak.getRelativeIntensity() + ",");
			sb.append("'" + peak.getCompoundId() + "'");
			sb.append("), ");
		}
		String sql = sb.toString().trim();
		String insertQuery = sql.substring(0, sql.length() - 1);
		addBatch(insertQuery);
	}
	
	public void executeBatchInsert(List<Peak> peaks) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + Peak.TABLE + " ");
		sb.append("(");
		sb.append(Peak.Columns.MZ + ",");
		sb.append(Peak.Columns.INTENSITY + ",");
		sb.append(Peak.Columns.RELATIVE_INTENSITY + ",");
		sb.append(Peak.Columns.COMPOUND_ID);
		sb.append(") values (?, ?, ?, ?)");
		
		createPreparedStatement(sb.toString());
		int i = 0;
		for(Peak peak : peaks) {
			pstmt.setDouble(1, peak.getMz());
			pstmt.setDouble(2, peak.getIntensity());
			pstmt.setInt(3, peak.getRelativeIntensity());
			pstmt.setString(4, peak.getCompoundId());
			pstmt.addBatch();
			if ((i + 1) % 100 == 0) {
				pstmt.executeBatch();
		    }
			i++;
		}
		executeBatchAndClosePreparedStatement();
	}
	
//	public void executeBatchInsert(List<Peak> peaks) throws SQLException {
//		StringBuilder sb = new StringBuilder();
//		sb.append("INSERT INTO " + Peak.TABLE + " ");
//		sb.append("(");
//		sb.append(Peak.Columns.MZ + ",");
//		sb.append(Peak.Columns.INTENSITY + ",");
//		sb.append(Peak.Columns.RELATIVE_INTENSITY + ",");
//		sb.append(Peak.Columns.RECORD_ID);
//		sb.append(") values (?, ?, ?, ?)");
//		for(int i = 1; i < peaks.size(); i++) {
//			sb.append(",(?, ?, ?, ?)");
//		}
//		
//		createPreparedStatement(sb.toString());
//		int i = 1;
//		for(Peak peak : peaks) {
//			pstmt.setDouble(i++, peak.getMz());
//			pstmt.setDouble(i++, peak.getIntensity());
//			pstmt.setInt(i++, peak.getRelativeIntensity());
//			pstmt.setString(i++, peak.getRecordId());
//		}
//		executeAndClosePreparedStatement();
//	}
	
	@Override
	public void insert(Peak peak) {
		insert(getInsertQuery(peak));
	}
	
	private String getInsertQuery(Peak peak) {
		return "INSERT INTO " + Peak.TABLE + " " +
				"(" + 
				Peak.Columns.MZ + "," + 
				Peak.Columns.INTENSITY + "," + 
				Peak.Columns.RELATIVE_INTENSITY + "," +  
				Peak.Columns.COMPOUND_ID + 
				") values (" +
				peak.getMz() + "," + 
				peak.getIntensity() + "," + 
				peak.getRelativeIntensity() + "," +
				"'" + peak.getCompoundId() + "')";
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
		sb.append(Peak.Columns.COMPOUND_ID + " VARCHAR(20)");
		sb.append(")");
		executeStatement(sb.toString());
	}

}
