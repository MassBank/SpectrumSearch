package jp.massbank.spectrumsearch.handler;

import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.entity.xml.Site;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MassbankConfigHandler extends DefaultHandler {

	private List<Site> servers = new ArrayList<Site>();
	private Site server;
	private String content;

	@Override
	// Triggered when the start of tag is found.
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		switch (qName) {
		// Create a new Employee object when the start tag is found
		case "MyServer":
		case "Related":
			server = new Site();
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		switch (qName) {
		case "MyServer":
		case "Related":
			servers.add(server);
			break;
		case "Name":
			server.setName(content);
			break;
		case "LongName":
			server.setLongName(content);
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

	public List<Site> getServers() {
		return servers;
	}

}
