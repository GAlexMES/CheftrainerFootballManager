package de.szut.dqi12.cheftrainer.server.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.server.parsing.PointsParser;
import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;

public class ScheduleParsingTest {

	@Test
	public void test() {
		ScheduleParser sp = new ScheduleParser();
		try {
			List<Match> matches = sp.createSchedule(7, 2015);
			for (Match m : matches) {
				m.setSportalMatchID(ScheduleParser.getSportalID(m.getDetailURL()));
				System.out.println(m.getSportalMatchID());
				PointsParser.getPlayerPoints(2015, 7, m.getSportalMatchID());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
