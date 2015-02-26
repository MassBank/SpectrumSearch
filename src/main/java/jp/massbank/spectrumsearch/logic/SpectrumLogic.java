package jp.massbank.spectrumsearch.logic;

import java.util.List;

import jp.massbank.spectrumsearch.accessor.CompoundAccessor;
import jp.massbank.spectrumsearch.accessor.PeakAccessor;
import jp.massbank.spectrumsearch.accessor.PrecursorAccessor;
import jp.massbank.spectrumsearch.entity.db.Compound;
import jp.massbank.spectrumsearch.entity.db.Peak;
import jp.massbank.spectrumsearch.entity.db.Precursor;
import jp.massbank.spectrumsearch.util.CommonUtil;

import org.apache.commons.lang3.StringUtils;

public class SpectrumLogic {
	
	private PeakAccessor peakAccessor;
//	private SpectrumAccessor spectrumAccessor;
	private CompoundAccessor compoundAccessor;
	private PrecursorAccessor precursorAccessor;
	
	public SpectrumLogic() {
		this.peakAccessor = new PeakAccessor();
//		this.spectrumAccessor = new SpectrumAccessor();
		this.compoundAccessor = new CompoundAccessor();
		this.precursorAccessor = new PrecursorAccessor();
	}
	
	// get child spectrum info
	public String getChildInfo(String compoundId) {
		StringBuilder sb = new StringBuilder();
		
		List<Peak> peakList = this.peakAccessor.getOrderedPeakListByRecordId(compoundId);
		if (peakList.size() == 0) {
			sb.append("0\t0\t\t");
		} else {
			for (Peak peak : peakList) {
				sb.append(String.format("%s\t%s\t\t", peak.getMz(), peak.getRelativeIntensity()));
			}
		}
		
		// follow record info
		sb.append("::");
		
		Compound compound = this.compoundAccessor.getCompoundById(compoundId);
		if (compound != null) {
			if (StringUtils.isNotBlank(compound.getTitle())) {
				sb.append(String.format("\tname=%s\t", compound.getTitle()));
			}
			List<Precursor> precursors = precursorAccessor.getPrecursorsByCompoundId(compoundId);
			if (precursors.size() > 0) {
				Precursor precursor = precursors.get(precursors.size() - 1);
				sb.append(String.format("\tprecursor=%s\t", precursor.getPrecursorMz()));
			}
		}
		
//		Spectrum spectrum = this.spectrumAccessor.getSpectrumByRecordId(recordId);
//		if (spectrum != null) {
//			if (StringUtils.isNotBlank(spectrum.getTitle())) {
//				sb.append(String.format("\tname=%s\t", spectrum.getTitle()));
//			}
//			sb.append(String.format("\tprecursor=%s\t", spectrum.getPrecursorMz()));
//		}
		
		sb.append(String.format("\tid=%s\t\n", compoundId));
		return sb.toString();
	}

	// get child spectrum info & get relation parent spectrum info
	public String getSpectrumData(String compoundId, int ionMode, boolean relation) {
		StringBuilder sb = new StringBuilder();
		if (! relation) {
			sb.append(getChildInfo(compoundId));
		} else {
			Compound compound = this.compoundAccessor.getCompoundById(compoundId);
			if (compound != null) {
				String title = compound.getTitle();
				title = CommonUtil.getGroupsByRegEx(title, "^([^;]*; [^;]*;) .*").get(0);
				title = title.replace("'", "\'");
				
				List<Compound> compounds = this.compoundAccessor.getCompoundsByInstrumentIdAndTitleTerm(compound.getInstrumentId(), title, ionMode);
				for (Compound compound2 : compounds) {
					sb.append(getChildInfo(compound2.getId()));
				}
			}
			
//			List<Map<Integer, Object>> result = DbAccessor.execResultQuery("SELECT S.TITLE, R.INSTRUMENT_ID FROM SPECTRUM S LEFT JOIN RECORD R ON S.RECORD_ID=R.RECORD_ID WHERE S.RECORD_ID='" + recordId + "'");
//			for (Map<Integer, Object> rowResult : result) {
//				String title = String.valueOf(rowResult.get(1));
//				int instanceId = Integer.parseInt(String.valueOf(rowResult.get(2)));
//				if (StringUtils.isNotBlank(title)) {
//					title = CommonUtil.getGroupsByRegEx(title, "^([^;]*; [^;]*;) .*").get(0);
//					title = title.replace("'", "\'");
//					
//					List<String> spectrumRecordIdList = this.spectrumAccessor.getSpectrumRecordIdListByInstanceId(instanceId, ionMode);
//					for (String specRecordId : spectrumRecordIdList) {
//						sb.append(getChildInfo(specRecordId));
//					}
//				}
//			}
		}
		return sb.toString();
	}

}
