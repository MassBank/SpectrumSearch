package jp.massbank.spectrumsearch.logic;

import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.accessor.CompoundAccessor;
import jp.massbank.spectrumsearch.entity.db.Compound;

import org.apache.commons.lang3.StringUtils;

@Deprecated
public class RecordLogic {
	
	private CompoundAccessor recordAccessor;
	
	public RecordLogic() {
		this.recordAccessor = new CompoundAccessor();
	}
	
	public List<Compound> getRecordListByKeyword(String pattern) {
		List<Compound> result = new ArrayList<Compound>();
		if (StringUtils.isNotBlank(pattern)) {
			result = this.recordAccessor.getCompoundsByName(pattern);
		} else {
			result = this.recordAccessor.getAllCompounds();
		}
		return result;
	}
	
//	public List<Record> getRecordListByKeyword(String keyword) {
//		List<Record> result = new ArrayList<Record>();
//		if (StringUtils.isNotBlank(keyword)) {
//			keyword = keyword.replace("*", "%");
//			keyword = keyword.replace("?", "_");
//			result = this.recordAccessor.getRecordsByName(keyword);
//		} else {
//			result = this.recordAccessor.getAllRecords();
//		}
//		return result;
//	}

}
