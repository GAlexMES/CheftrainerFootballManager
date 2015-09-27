package de.szut.dqi12.cheftrainer.server.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;

public class ScheduleParsingTest {

	@Test
	public void test() {
		ScheduleParser sp = new ScheduleParser();
		try {
			sp.createSchedule(7,2015);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
