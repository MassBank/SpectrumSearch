package jp.massbank.spectrumsearch.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.massbank.spectrumsearch.entity.db.MsType;
import jp.massbank.spectrumsearch.util.QueryBuilder;

import org.apache.log4j.Logger;

public class MsTypeAccessor extends AbstractDbAccessor<MsType> {

	private static final Logger LOGGER = Logger.getLogger(MsTypeAccessor.class);
	
	@Override
	protected MsType convert(ResultSet rs) throws SQLException {
		MsType result = new MsType();
		result.setId(rs.getInt(MsType.Columns.MS_TYPE_ID));
		result.setName(rs.getString(MsType.Columns.MS_TYPE_NAME));
		return result;
	}

	public List<MsType> getAllMsTypes() {
		String sql = String.format("SELECT * FROM %s", MsType.TABLE);
		return listGeneric(sql);
	}
	
	public MsType getMsTypeByName(String name) {
		String sql = String.format("SELECT * FROM %s WHERE %s='%s'", MsType.TABLE, MsType.Columns.MS_TYPE_NAME, name);
		return uniqueGeneric(sql);
	}
	
	@Override
	public void insert(MsType t) {
		String insertQuery = "INSERT INTO " + MsType.TABLE + " " +
				"(" +  MsType.Columns.MS_TYPE_NAME +  ") values (" + "'" + t.getName() + "')";
		insert(insertQuery);
	}

	@Override
	public void deleteAll() {
		delete(QueryBuilder.getDeleteAll(MsType.TABLE));
	}

	@Override
	public void dropTable() {
		executeStatement(QueryBuilder.getDropTable(MsType.TABLE));
	}

	@Override
	public void createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + MsType.TABLE +" ");
		sb.append("(");
		sb.append(MsType.Columns.MS_TYPE_ID + " INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT MS_TYPE_PK PRIMARY KEY,");
		sb.append(MsType.Columns.MS_TYPE_NAME + " VARCHAR(10)");
		sb.append(")");
		executeStatement(sb.toString());
	}
	
}
