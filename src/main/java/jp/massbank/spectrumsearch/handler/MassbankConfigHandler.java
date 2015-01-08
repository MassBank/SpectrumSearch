package jp.massbank.spectrumsearch.handler;

import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.entity.xml.ResearchGroup;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MassbankConfigHandler extends DefaultHandler {

	private List<ResearchGroup> servers = new ArrayList<ResearchGroup>();
	private ResearchGroup server;
	private String content;

	@Override
	// Triggered when the start of tag is found.
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		switch (qName) {
		// Create a new Employee object when the start tag is found
		case "ResearchGroup":
			server = new ResearchGroup();
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		switch (qName) {
		case "ResearchGroup":
			servers.add(server);
			break;
		case "LongName":
			server.setLongName(content);
			break;
		case "Name":
			server.setName(content);
			break;
		case "Prefix":
			if (StringUtils.isNotBlank(content)) {
				server.setPrefixes(content.split(","));
			}
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content = String.copyValueOf(ch, start, length).trim();
	}

	public List<ResearchGroup> getServers() {
		return servers;
	}

}
