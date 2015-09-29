package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.util.List;
import java.util.Map;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;

public class SchedulePointManagement {

	private SQLConnection sqlCon;
	private ScheduleParser scheduleParser;
	public SchedulePointManagement(SQLConnection sqlCon) {
		this.sqlCon=sqlCon;
		scheduleParser = new ScheduleParser();
	}
	
	public int getCurrentSeasonFromSportal(){
		return scheduleParser.getCurrentSeason();
	}

	public void initializeScheduleForSeason(int currentSeason) {
		Map<Integer,List<Match>> matchDays = scheduleParser.getMatchesForSeason(currentSeason);
	}

}
