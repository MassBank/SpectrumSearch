package jp.massbank.spectrumsearch.logic;

import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.accessor.CompoundAccessor;
import jp.massbank.spectrumsearch.entity.db.Compound;

import org.apache.commons.lang3.StringUtils;

public class CompoundLogic {
	
	private CompoundAccessor compoundAccessor;
	
	public CompoundLogic() {
		this.compoundAccessor = new CompoundAccessor();
	}
	
	public List<Compound> getCompoundListByKeyword(String pattern) {
		List<Compound> result = new ArrayList<Compound>();
		if (StringUtils.isNotBlank(pattern)) {
			result = this.compoundAccessor.getCompoundsByName(pattern);
		} else {
			result = this.compoundAccessor.getAllCompounds();
		}
		return result;
	}
	
	public List<String> getInfo(String compoundId, String name) {
		// getCompoundInfo.cgi
		Compound compound = compoundAccessor.getCompoundById(compoundId);

		List<String> result = new ArrayList<String>();
//		result.add(String.format("---FORMULA:%s\n", record.getFormula()));
//		result.add(String.format("---EXACT_MASS:%s\n", Double.valueOf(record.getExactMass())));
		result.add(String.format("FORMULA:%s\n", compound.getFormula()));
		result.add(String.format("EXACT_MASS:%s\n", Double.valueOf(compound.getExactMass())));
		return result;
	}
	
}
