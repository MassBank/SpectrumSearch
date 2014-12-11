package jp.massbank.spectrumsearch.logic;

import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.db.accessor.RecordAccessor;
import jp.massbank.spectrumsearch.db.entity.Record;

public class CompoundLogic {
	
	private RecordAccessor recordAccessor;
	
	public CompoundLogic() {
		this.recordAccessor = new RecordAccessor();
	}
	
	public List<String> getInfo(String recordId, String name, int site) {
		List<String> result = new ArrayList<String>();
		
		Record record = recordAccessor.getRecordById(recordId);
		result.add(String.format("---FORMULA:%s\n", record.getFormula()));
		result.add(String.format("---EXACT_MASS:%s\n", Double.valueOf(record.getExactMass())));
		
		return result;
	}

}
