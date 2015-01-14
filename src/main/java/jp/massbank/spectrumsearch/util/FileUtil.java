package jp.massbank.spectrumsearch.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jp.massbank.spectrumsearch.entity.constant.Constant;
import jp.massbank.spectrumsearch.entity.xml.ResearchGroup;
import jp.massbank.spectrumsearch.handler.MassbankConfigHandler;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class FileUtil {

	private static final Logger LOGGER = Logger.getLogger(FileUtil.class);
	
	// TODO system.properties read and write
	
	public static List<ResearchGroup> getServers() {
		List<ResearchGroup> result = new ArrayList<ResearchGroup>();
		try {
			SAXParserFactory parserFactor = SAXParserFactory.newInstance();
			SAXParser parser = parserFactor.newSAXParser();
			MassbankConfigHandler handler = new MassbankConfigHandler();
			parser.parse(ClassLoader.getSystemResourceAsStream("config/" + Constant.SERVERS_CONFIG_FILE_NAME), handler);
			
			return handler.getServers();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return result;
	}
	
}
