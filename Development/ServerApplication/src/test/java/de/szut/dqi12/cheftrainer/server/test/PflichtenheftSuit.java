package de.szut.dqi12.cheftrainer.server.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.szut.dqi12.cheftrainer.server.test.pflichtenheft.MarketTest;
import de.szut.dqi12.cheftrainer.server.test.pflichtenheft.ParserTest;
import de.szut.dqi12.cheftrainer.server.test.pflichtenheft.RegistrationTest;
import de.szut.dqi12.cheftrainer.server.test.pflichtenheft.SystemFunctionTest;

@RunWith(Suite.class)
@SuiteClasses({ MarketTest.class, RegistrationTest.class, SystemFunctionTest.class, ParserTest.class })
public class PflichtenheftSuit {

}
