package jp.massbank.spectrumsearch.logic;

import java.util.List;

import jp.massbank.spectrumsearch.db.accessor.PeakAccessor;
import jp.massbank.spectrumsearch.db.accessor.RecordAccessor;
import jp.massbank.spectrumsearch.db.accessor.SpectrumAccessor;
import jp.massbank.spectrumsearch.db.entity.Peak;
import jp.massbank.spectrumsearch.db.entity.Record;
import jp.massbank.spectrumsearch.db.entity.Spectrum;

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
			if (StringUtils.isNotBlank(spectrum.getName())) {
				sb.append(String.format("\tname=%s\t", spectrum.getName()));
			}
			sb.append(String.format("\tprecursor=%s\t", spectrum.getPrecursorMz()));
		}
		
		sb.append(String.format("\tid=%s\t\n", recordId));
		return sb.toString();
	}

	public String getChildInfo(String recordId, int ionMode) {
		StringBuilder sb = new StringBuilder();
		Record record = this.recordAccessor.getRecordById(recordId);
			if (record != null) {
			List<String> spectrumRecordIdList = this.spectrumAccessor.getSpectrumRecordIdListByInstanceId(record.getInstrumentId(), ionMode);
			for (String oRecordId : spectrumRecordIdList) {
				sb.append(getChildInfo(oRecordId));
			}
		}
		return sb.toString();
//		String pattern = "^([^;]*; [^;]*;).*";
	}
	
	public static void main(String[] args) {
		System.out.println(Integer.parseInt("POSITIVE"));
	}

}
