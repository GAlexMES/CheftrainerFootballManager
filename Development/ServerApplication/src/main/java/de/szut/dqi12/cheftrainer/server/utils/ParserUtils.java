package de.szut.dqi12.cheftrainer.server.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * This class has some useful methods, which are used by the parser classes.
 * @author Alexander Brennecke
 *
 */
public class ParserUtils {

	/**
	 * Tries to find a HTML table in the given string.
	 * @param file return the content of the HTML table
	 * @return
	 */
	public static String getTableOfHTML(String file) {
		String pattern = "(<table>.*<\\/table>)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(file);

		if (m.find()) {
			return m.group(0);
		}
		return null;
	}

	/**
	 * This method creates a SAXBuilder for the given String
	 * @param xmlString The html string, that should be used with the SAXBuilder
	 * @return a List of Element, which are the childs of the root of the given String.
	 */
	public static List<Element> parseXmlTableString(String xmlString) {
		SAXBuilder saxBuilder = new SAXBuilder();
		List<Element> nodeList = new ArrayList<Element>();
		try {
			Document doc = saxBuilder.build(new StringReader(xmlString));
			nodeList = doc.getRootElement().getChildren();
		} catch (JDOMException |IOException e) {
		}
		return nodeList;
	}
}