package de.szut.dqi12.cheftrainer.server.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * This class contains all Table Queries, which will be executed, when the database file was created.
 * @author Alexander Brennecke
 *
 */
public class TableQueries {

	
	public static final String GEBOTE_QUERY = "CREATE TABLE `Gebote` (`Manager_ID`	INTEGER,`Spieler_ID` INTEGER,`Gebot`INTEGER,`Spielrunde_ID`	INTEGER)";
	public static final String LIGA_QUERY = "CREATE TABLE `Liga` (`ID`	INTEGER PRIMARY KEY AUTOINCREMENT, `Name` TEXT,	`Land`TEXT)";
	public static final String MANAGER_QUERY = "CREATE TABLE `Manager` (`ID`INTEGER PRIMARY KEY AUTOINCREMENT,`Nutzer_ID` INTEGER,`Spielrunde_ID` INTEGER,`Budget` INTEGER DEFAULT 0,`Punkte` INTEGER DEFAULT 0,`Platz` INTEGER DEFAULT 0, `Anzahl_Stuermer` INTEGER DEFAULT 0, `Anzahl_Mittelfeld` INTEGER DEFAULT 0, `Anzahl_Abwehr` INTEGER DEFAULT 0)";
	public static final String MANAGER_STATISTIC_QUERY = "CREATE TABLE `Manager_Statistik` (`Spieltag` INTEGER, `Manager_ID` INTEGER,`Punkte` INTEGER)";
	public static final String MANNSCHAFT_QUERY = "CREATE TABLE `Mannschaft` ( `Manager_ID` INTEGER, `Spieler_ID` INTEGER,`Aufgestellt` INTEGER)";
	public static final String MANNSCHAFT_COPY_QUERY = "CREATE TABLE `Mannschaft Copy` ( `Manager_ID` INTEGER, `Spieler_ID` INTEGER,`Punkte` INTEGER)";
	public static final String NUTZER_QUERY = "CREATE TABLE `Nutzer` (`ID`INTEGER PRIMARY KEY AUTOINCREMENT,`Vorname`TEXT,`Nachname`TEXT,`Nutzername`TEXT,`EMail`TEXT,`Passwort`TEXT)";
	public static final String PROPERTIES_QUERY = "CREATE TABLE `ServerProperties` (`Name`TEXT,`Wert`TEXT)";
	public static final String SPIELER_QUERY = "CREATE TABLE `Spieler` (`ID`INTEGER PRIMARY KEY AUTOINCREMENT,`Name`TEXT,`Verein_ID`INTEGER,`Position`TEXT,`Punkte`INTEGER, `Marktwert` INTEGER, `Nummer` INTEGER, `SportalID` INTEGER, `Birthday` TEXT, `PicturePath` TEXT)";
	public static final String SPILER_STATISTIC_QUERY = "CREATE TABLE `Spieler_Statistik` (`Spieltag`INTEGER,`Spieler_ID`INTEGER,`Punkte` INTEGER)";
	public static final String SPIELRUNDE_QUERY = "CREATE TABLE `Spielrunde` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT, `Name` TEXT, `Liga_ID` INTEGER, `Administrator_ID` INTEGER, `Passwort` TEXT)";
	public static final String SPIELTAG_QUERY = "CREATE TABLE `Spieltag` (`ID`INTEGER PRIMARY KEY AUTOINCREMENT,`Saison`INTEGER,`Spieltag` INTEGER,`Datum` INTEGER,  `Heim_Verein_ID` INTEGER, `Gast_Verein_ID` INTEGER, `Ergebnis` TEXT, `URL` TEXT)";
	public static final String TRANSFER_STATISTIk_QUERY ="CREATE TABLE `Transfer_Statistik` (`Datum` INTEGER, `Verkauefer_ID`INTEGER,`kaufer_ID`INTEGER,`Preis` INTEGER,`Spieler_ID` INTEGER)";
	public static final String TRANSFERMARKT_QUERY ="CREATE TABLE `Transfermarkt` (`Spielrunde_ID` INTEGER, `Spieler_ID` INTEGER, `Min_Preis` INTEGER, `Inhaber_ID` INTEGER)";
	public static final String VEREIN_QUERY = "CREATE TABLE `Verein` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT,`Vereinsname` INTEGER,`LogoPath` TEXT,`Liga_ID` INTEGER)";
	/**
	 * This function creates a {@link List} of all Queries defined in this class.
	 * @return a {@link List} of Strings, which represents a Query.
	 */
	public static List<String> getTableQueries(){
		List<String> retval = new ArrayList<>();
		Field[] fields=TableQueries.class.getFields(); 
		for(Field field:fields){
		   if(field.getType().equals(String.class)){ 
		    try {
				retval.add((String) field.get(TableQueries.class));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		   }
		}
		return retval;
	}
}
