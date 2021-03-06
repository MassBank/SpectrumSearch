package jp.massbank.spectrumsearch.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.massbank.spectrumsearch.entity.db.Compound;
import jp.massbank.spectrumsearch.entity.db.Spectrum;
import jp.massbank.spectrumsearch.util.QueryBuilder;

@Deprecated
public class SpectrumAccessor extends AbstractDbAccessor<Spectrum> {

	@Override
	protected Spectrum convert(ResultSet rs) throws SQLException {
		Spectrum result = new Spectrum();
		result.setId(rs.getInt(Spectrum.Columns.SPECTRUM_ID));
		result.setTitle(rs.getString(Spectrum.Columns.TITLE));
		result.setIonMode(rs.getInt(Spectrum.Columns.ION_MODE));
		result.setPrecursorMz(rs.getFloat(Spectrum.Columns.PRECURSOR_MZ));
		result.setRecordId(rs.getString(Spectrum.Columns.COMPOUND_ID));
		return result;
	}
	
	public Spectrum getSpectrumByRecordId(String recordId) {
		String sql = "select * from " + Spectrum.TABLE +" where " + Spectrum.Columns.COMPOUND_ID +" = '" + recordId + "'";
		return uniqueGeneric(sql);
	}
	
	public Spectrum getSpectrumByRecordId(String recordId, int ionMode) {
		String sql = "select * from " + Spectrum.TABLE +" where " + Spectrum.Columns.ION_MODE + " > 0 " + Spectrum.Columns.COMPOUND_ID +" = '" + recordId + "'";
		return uniqueGeneric(sql);
	}
	
	public List<String> getSpectrumRecordIdListByInstanceId(int instanceId, int ionMode) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT S.RECORD_ID FROM " + Spectrum.TABLE +" S LEFT JOIN " + Compound.TABLE +" R ON S.RECORD_ID=R.RECORD_ID ");
		sb.append("WHERE ");
		if (ionMode > 0) {
			sb.append("S." + Spectrum.Columns.ION_MODE + " > 0 AND ");
		} else if (ionMode < 0) {
			sb.append("S." + Spectrum.Columns.ION_MODE + " < 0 AND ");
		}
		sb.append("R.INSTRUMENT_ID=" + instanceId + " ORDER BY 1");
		return listString(sb.toString());
	}
	
	public void addBatchInsert(Spectrum spectrum) {
		String insertQuery = "INSERT INTO " + Spectrum.TABLE + " " +
				"(" + 
				Spectrum.Columns.TITLE + "," + 
				Spectrum.Columns.ION_MODE + "," + 
				Spectrum.Columns.PRECURSOR_MZ + "," + 
				Spectrum.Columns.COMPOUND_ID + 
				") values (" +
				"'" + spectrum.getTitle() + "'," +
				spectrum.getIonMode() + "," + 
				spectrum.getPrecursorMz() + "," +
				"'" + spectrum.getRecordId() + "')";
		addBatch(insertQuery);
	}
	
	@Override
	public void insert(Spectrum spectrum) {
		String insertQuery = "INSERT INTO " + Spectrum.TABLE + " " +
				"(" + 
				Spectrum.Columns.TITLE + "," + 
				Spectrum.Columns.ION_MODE + "," + 
				Spectrum.Columns.PRECURSOR_MZ + "," + 
				Spectrum.Columns.COMPOUND_ID + 
				") values (" +
				"'" + spectrum.getTitle() + "'," +
					  spectrum.getIonMode() + "," + 
					  spectrum.getPrecursorMz() + "," +
				"'" + spectrum.getRecordId() + "')";
		insert(insertQuery);
	}
	
	@Override
	public void deleteAll() {
		delete(QueryBuilder.getDeleteAll(Spectrum.TABLE));
	}

	@Override
	public void dropTable() {
		executeStatement(QueryBuilder.getDropTable(Spectrum.TABLE));
	}

	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + Spectrum.TABLE +" ");
		sb.append("(");
		sb.append(Spectrum.Columns.SPECTRUM_ID + " INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT SPECTRUM_PK PRIMARY KEY,");
		sb.append(Spectrum.Columns.TITLE + " VARCHAR(255),");
		sb.append(Spectrum.Columns.ION_MODE + " SMALLINT,");
		sb.append(Spectrum.Columns.PRECURSOR_MZ + " FLOAT,");
		sb.append(Spectrum.Columns.COMPOUND_ID + " VARCHAR(20)");
		sb.append(")");
		executeStatement(sb.toString());
	}

}
