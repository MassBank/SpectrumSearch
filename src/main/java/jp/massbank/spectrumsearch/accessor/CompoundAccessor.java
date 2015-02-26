package jp.massbank.spectrumsearch.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.entity.db.Compound;
import jp.massbank.spectrumsearch.entity.db.Precursor;
import jp.massbank.spectrumsearch.util.QueryBuilder;

import org.apache.commons.lang3.StringUtils;

public class CompoundAccessor extends AbstractDbAccessor<Compound> {

	@Override
	protected Compound convert(ResultSet rs) throws SQLException {
		Compound result = new Compound();
		result.setId(rs.getString(Compound.Columns.COMPOUND_ID));
		result.setTitle(rs.getString(Compound.Columns.TITLE));
		result.setFormula(rs.getString(Compound.Columns.FORMULA));
		result.setExactMass(rs.getDouble(Compound.Columns.EXACT_MASS));
		result.setIonMode(rs.getInt(Compound.Columns.ION_MODE));
		result.setInstrumentId(rs.getInt(Compound.Columns.INSTRUMENT_ID));
		result.setMsId(rs.getInt(Compound.Columns.MS_TYPE_ID));
		return result;
	}
	
	public Compound getCompoundById(String compoundId) {
		String sql = "SELECT * FROM " + Compound.TABLE + " WHERE " + Compound.Columns.COMPOUND_ID + " = '" + compoundId + "'";
		return uniqueGeneric(sql);
	}
	
	public List<Compound> getCompoundsByInstrumentIdAndTitleTerm(int instrumentId, String titleTerm, int ionMode) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM " + Compound.TABLE + " WHERE ");
		sb.append(Compound.Columns.TITLE + " LIKE '%" + titleTerm + "%' ");
		if (ionMode > 0) {
			sb.append("AND " + Compound.Columns.ION_MODE + " > 0 ");
		} else if (ionMode < 0) {
			sb.append("AND " + Compound.Columns.ION_MODE + " < 0 ");
		}
		sb.append("AND " + Compound.Columns.INSTRUMENT_ID + " == " + instrumentId);
		sb.append(" ORDER BY 1");
		return listGeneric(sb.toString());
	}
	
	public List<Compound> getCompoundList(Integer precursor1, Integer precursor2, 
			int ionMode, List<Integer> instrumentIds, List<Integer> msTypeIds) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM " + Compound.TABLE + " C");
		
		List<String> whereClauses = new ArrayList<String>();
		
		if (precursor1 != null && precursor2 != null) {
			sb.append(", " + Precursor.TABLE + " P");
			sb.append(" WHERE");
			sb.append(" C." + Compound.Columns.COMPOUND_ID + " = P." + Precursor.Columns.COMPOUND_ID);
			
			whereClauses.add("P." + Precursor.Columns.PRECURSOR_MZ + " BETWEEN " + precursor1 + " AND " + precursor2);
		}
		
		if (ionMode != 0) {
			whereClauses.add("C." + Compound.Columns.ION_MODE + " = " + ionMode);
		}
		if (instrumentIds != null && instrumentIds.size() > 0) {
			whereClauses.add("C." + Compound.Columns.INSTRUMENT_ID + " in (" + StringUtils.join(instrumentIds, ",") + ")");
		}
		if (msTypeIds != null && msTypeIds.size() > 0) {
			whereClauses.add("C." + Compound.Columns.MS_TYPE_ID + " in (" + StringUtils.join(msTypeIds, ",") + ")");
		}
		if (whereClauses.size() > 0) {
			sb.append(" WHERE ");
			sb.append(StringUtils.join(whereClauses, " AND "));
		}
		sb.append(" ORDER BY C." + Compound.Columns.COMPOUND_ID);
		String sql = sb.toString();
		return listGeneric(sql);
	}
	
	public List<Compound> getAllCompounds() {
		String sql = "SELECT * FROM " + Compound.TABLE;
		return listGeneric(sql);
	}
	
	public List<Compound> getCompoundsByName(String pattern) {
		String sql = String.format("SELECT * FROM %s WHERE REGEXP_LIKE (%s, '%s')", 
				Compound.TABLE, Compound.Columns.TITLE, pattern);
		return listGeneric(sql);
	}
	
//	public List<Record> getRecordsByName(String searchName) {
//		String sql = String.format("SELECT * FROM %s WHERE UPPER(%s) LIKE '%s'", 
//				Record.TABLE, Record.Columns.RECORD_TITLE, searchName.toUpperCase());
//		return listGeneric(sql);
//	}

	public void addBatchInsert(Compound compound) {
		addBatch(getInsertQuery(compound));
	}
	
	@Override
	public void insert(Compound compound) {
		insert(getInsertQuery(compound));
	}
	
	private String getInsertQuery(Compound compound) {
		return "INSERT INTO " + Compound.TABLE + " " +
				"(" + 
				Compound.Columns.COMPOUND_ID + "," + 
				Compound.Columns.TITLE + "," + 
				Compound.Columns.FORMULA + "," + 
				Compound.Columns.EXACT_MASS + "," + 
				Compound.Columns.ION_MODE + "," + 
				Compound.Columns.INSTRUMENT_ID + "," + 
				Compound.Columns.MS_TYPE_ID + 
			") values (" +
				"'" + compound.getId() + "'," + 
				"'" + compound.getTitle() + "'," +
				"'" + compound.getFormula() + "'," +
				compound.getExactMass() + "," +
				compound.getIonMode() + "," +
				compound.getInstrumentId() + "," +
				compound.getMsId() + ")";
	}
	
	@Override
	public void deleteAll() {
		String deleteQuery = "DELETE FROM " + Compound.TABLE;
		delete(deleteQuery);
	}
	
	@Override
	public void dropTable() {
		executeStatement(QueryBuilder.getDropTable(Compound.TABLE));
	}

	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + Compound.TABLE + " ");
		sb.append("(");
		sb.append(Compound.Columns.COMPOUND_ID + " VARCHAR(20) NOT NULL CONSTRAINT COMPOUND_PK PRIMARY KEY,");
		sb.append(Compound.Columns.TITLE + " VARCHAR(255),");
		sb.append(Compound.Columns.FORMULA + " VARCHAR(255),");
		sb.append(Compound.Columns.EXACT_MASS + " FLOAT,");
		sb.append(Compound.Columns.ION_MODE + " INT,");
		sb.append(Compound.Columns.INSTRUMENT_ID + " INT,");
		sb.append(Compound.Columns.MS_TYPE_ID + " INT");
		sb.append(")");
		executeStatement(sb.toString());
	}

}
