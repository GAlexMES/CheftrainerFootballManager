package de.brennecke.alexander.Test;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Element;

import Parsers.PlayerParser;
import Parsers.TeamParser;

/**
 * Hello world!
 *
 */
public class App {
	
	private static String rootURL = "http://www.ran.de/datenbank/fussball";
	
	public static void main(String[] args) {
		TeamParser tp = new TeamParser();
		List<Team> teamList = tp.getTeamlist(rootURL);
		PlayerParser pp = new PlayerParser();
		for(Team t : teamList){
			URL teamURL;
			try {
				teamURL = new URL(rootURL + t.getTeamUrl());
				t.setPlayerList(pp.getPlayers(teamURL));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}


}