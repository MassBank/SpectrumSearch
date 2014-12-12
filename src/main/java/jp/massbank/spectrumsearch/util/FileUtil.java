package jp.massbank.spectrumsearch.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jp.massbank.spectrumsearch.entity.constant.Constant;
import jp.massbank.spectrumsearch.entity.xml.Site;
import jp.massbank.spectrumsearch.handler.MassbankConfigHandler;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class FileUtil {

	private static final Logger LOGGER = Logger.getLogger(FileUtil.class);
	
	public static List<Site> getServers() {
		List<Site> result = new ArrayList<Site>();
		try {
			SAXParserFactory parserFactor = SAXParserFactory.newInstance();
			SAXParser parser = parserFactor.newSAXParser();
			MassbankConfigHandler handler = new MassbankConfigHandler();
			parser.parse(ClassLoader.getSystemResourceAsStream(Constant.SERVERS_CONFIG_FILE_NAME), handler);
			
			return handler.getServers();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return result;
	}
}
