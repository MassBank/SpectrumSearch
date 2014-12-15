package jp.massbank.spectrumsearch.util;

import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.entity.xml.Site;

import org.apache.commons.lang3.StringUtils;

public class SiteUtil {
	
	public static final List<Site> SITES;
	
	static {
		SITES = FileUtil.getServers();
	}
	
	public static String[] getSiteNamesArray() {
		List<String> siteNames = new ArrayList<String>();
		for (Site site : SITES) {
			siteNames.add(site.getName());
		}
		return siteNames.toArray(new String[siteNames.size()]);
	}

	public static String getServerPrefixByRecordId(String recordId) {
		String[] part = recordId.split("(?<=\\D)(?=\\d)");
		return part[0];
	}
	
	public static String getSiteNameByRecordId(String recordId) {
		String prefix = getServerPrefixByRecordId(recordId);
		return getSiteNameByRecordIdPrefix(prefix);
	}
	
//	public static String getSiteLongNameByRecordId(String recordId) {
//		String prefix = getServerPrefixByRecordId(recordId);
//		return getSiteLongNameByRecordIdPrefix(prefix);
//	}
	
	public static String getSiteNameByRecordIdPrefix(String prefix) {
		Site site = getSiteByRecordIdPrefix(prefix);
		if (site != null) {
			return site.getName();
		}
		return StringUtils.EMPTY;
	}
	
//	public static String getSiteLongNameByRecordIdPrefix(String prefix) {
//		Site site = getServerByRecordIdPrefix(prefix);
//		if (site != null) {
//			return site.getLongName();
//		}
//		return StringUtils.EMPTY;
//	}
	
	public static Site getSiteByRecordId(String recordId) {
		for (Site site : SITES) {
			for (String prefix : site.getPrefixes()) {
				if (recordId.startsWith(prefix)) {
					return site;
				}
			}
		}
		return null;
	}
	
	private static Site getSiteByRecordIdPrefix(String prefix) {
		for (Site site : SITES) {
			for (String oPrefix : site.getPrefixes()) {
				if (oPrefix.equals(prefix)) {
					return site;
				}
			}
		}
		return null;
	}
	
//	public static Site getServerByLongName(String longName) {
//		for (Site site : SITES) {
//			if (site.getLongName().equals(longName)) {
//				return site;
//			}
//		}
//		return null;
//	}
	
}
