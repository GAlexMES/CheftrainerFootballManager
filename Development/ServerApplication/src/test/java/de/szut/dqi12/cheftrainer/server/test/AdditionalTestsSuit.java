package de.szut.dqi12.cheftrainer.server.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.szut.dqi12.cheftrainer.server.test.additionaltests.DatabaseTest;
import de.szut.dqi12.cheftrainer.server.test.additionaltests.ScheduleParsingTest;
import de.szut.dqi12.cheftrainer.server.test.additionaltests.TimeTaskTest;

@RunWith(Suite.class)
@SuiteClasses({ DatabaseTest.class, ScheduleParsingTest.class, TimeTaskTest.class })
public class AdditionalTestsSuit {

}
