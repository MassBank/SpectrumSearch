package jp.massbank.spectrumsearch.logic;

import java.util.List;
import java.util.Map;

import jp.massbank.spectrumsearch.accessor.DbAccessor;
import jp.massbank.spectrumsearch.accessor.PeakAccessor;
import jp.massbank.spectrumsearch.accessor.RecordAccessor;
import jp.massbank.spectrumsearch.accessor.SpectrumAccessor;
import jp.massbank.spectrumsearch.entity.db.Peak;
import jp.massbank.spectrumsearch.entity.db.Spectrum;
import jp.massbank.spectrumsearch.util.CommonUtil;

import org.apache.commons.lang3.StringUtils;

public class SpectrumLogic {
	
	private PeakAccessor peakAccessor;
	private SpectrumAccessor spectrumAccessor;
	private RecordAccessor recordAccessor;
	
	public SpectrumLogic() {
		this.peakAccessor = new PeakAccessor();
		this.spectrumAccessor = new SpectrumAccessor();
		this.recordAccessor = new RecordAccessor();
	}
	
	public String getChildInfo(String recordId) {
		StringBuilder sb = new StringBuilder();
		
		List<Peak> peakList = this.peakAccessor.getOrderedPeakListByRecordId(recordId);
		if (peakList.size() == 0) {
			sb.append("0\t0\t\t");
		} else {
			for (Peak peak : peakList) {
				sb.append(String.format("%s\t%s\t\t", peak.getMz(), peak.getRelativeIntensity()));
			}
		}
		
		// follow record info
		sb.append("::");
		
		Spectrum spectrum = this.spectrumAccessor.getSpectrumByRecordId(recordId);
		if (spectrum != null) {
			if (StringUtils.isNotBlank(spectrum.getTitle())) {
				sb.append(String.format("\tname=%s\t", spectrum.getTitle()));
			}
			sb.append(String.format("\tprecursor=%s\t", spectrum.getPrecursorMz()));
		}
		
		sb.append(String.format("\tid=%s\t\n", recordId));
		return sb.toString();
	}

	public String getSpectrumData(String recordId, int ionMode, boolean relation) {
		StringBuilder sb = new StringBuilder();
		if (! relation) {
			sb.append(getChildInfo(recordId));
		} else {
			List<Map<Integer, Object>> result = DbAccessor.execResultQuery("SELECT S.TITLE, R.INSTRUMENT_ID FROM SPECTRUM S LEFT JOIN RECORD R ON S.RECORD_ID=R.RECORD_ID WHERE S.RECORD_ID='" + recordId + "'");
			for (Map<Integer, Object> rowResult : result) {
				String title = String.valueOf(rowResult.get(1));
				int instanceId = Integer.parseInt(String.valueOf(rowResult.get(2)));
				if (StringUtils.isNotBlank(title)) {
					title = CommonUtil.getGroupsByRegEx(title, "^([^;]*; [^;]*;) .*").get(0);
					title = title.replace("'", "\'");
					
					List<String> spectrumRecordIdList = this.spectrumAccessor.getSpectrumRecordIdListByInstanceId(instanceId, ionMode);
					for (String specRecordId : spectrumRecordIdList) {
						sb.append(getChildInfo(specRecordId));
					}
				}
			}
		}
		return sb.toString();
	}

}
