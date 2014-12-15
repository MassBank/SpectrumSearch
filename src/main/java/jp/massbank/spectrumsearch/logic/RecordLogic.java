package jp.massbank.spectrumsearch.logic;

import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.accessor.RecordAccessor;
import jp.massbank.spectrumsearch.entity.db.Record;

import org.apache.commons.lang3.StringUtils;

public class RecordLogic {
	
	private RecordAccessor recordAccessor;
	
	public RecordLogic() {
		this.recordAccessor = new RecordAccessor();
	}
	
	public List<Record> getRecordListByKeyword(String keyword) {
		List<Record> result = new ArrayList<Record>();
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replace("*", "%");
			keyword = keyword.replace("?", "_");
			result = this.recordAccessor.getRecordsByName(keyword);
		} else {
			result = this.recordAccessor.getAllRecords();
		}
		return result;
	}

}
