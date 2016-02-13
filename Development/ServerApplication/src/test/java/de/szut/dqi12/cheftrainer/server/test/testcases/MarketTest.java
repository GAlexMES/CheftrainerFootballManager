package de.szut.dqi12.cheftrainer.server.test.testcases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewOfferMessage;
import de.szut.dqi12.cheftrainer.server.callables.TransferMarketUpdate;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.test.utils.TestUtils;

public class MarketTest {
	
	private static SQLConnection sqlCon;
	private static User user;

	@Mock
	private static MessageController messageController;
	@InjectMocks
	private static Community community;
	@Mock
	private static Market market;
	
	private final static String USER_NAME = "Kurt";
	private final static String USER_SURNAME = "Testi";
	private final static String USER_LOGIN = "Testuser";
	private final static String USER_PASSWORD = "123456";
	private final static String USER_EMAIL = "kurt@testi.de";

	private final static String COMMUNITY_NAME = "Testrunde";
	private final static String COMMUNITY_PASSWORD = "654321";

	private final static int PLAYER_SPORTAL_ID = 999999;
	private final static String PLAYER_NAME = "Papi";
	private final static int PLAYER_WORTH = 7;
	
	/**
	 * Does preparations for the tests in this class.
	 * @throws IOException
	 */
	@Before
	public void prepareTests() throws IOException{
		sqlCon = new SQLConnection(true);
		TestUtils.prepareDatabase(sqlCon);

		user = new User();
		user.setFirstName(USER_NAME);
		user.setLastName(USER_SURNAME);
		user.setUserName(USER_LOGIN);
		user.setPassword(USER_PASSWORD);
		user.seteMail(USER_EMAIL);
		
		market = Mockito.mock(Market.class);
		Mockito.when(market.getPlayers()).thenReturn(generatePlayerList());
		community = Mockito.mock(Community.class);
		Mockito.when(community.getMarket()).thenReturn(market);
		Mockito.when(community.getCommunityID()).thenReturn(99);
	}
	
	@After
	public void closeDatabase(){
		sqlCon.close();
	}
	
	/**
	 * Tests the creation of an offer/{@link Transaction}.
	 * 
	 * @see /T0212/
	 */
	@Test
	public void testCreateOffer() {
		Player player = market.getPlayers().get(0);
		int playerWorth = player.getWorth();
		Transaction tr = new Transaction(playerWorth + 10, player.getSportalID(), community.getCommunityID(), user.getUserID());

		addPlayerToMarket(community.getCommunityID());

		NewOfferMessage nfMessage = new NewOfferMessage(tr);
		nfMessage.setMessageContent(nfMessage.createJSON());

		String addManager = "INSERT INTO Manager (Nutzer_ID,Spielrunde_ID,Budget) VALUES (" + user.getUserID() + "," + community.getCommunityID() + "15000000)";
		sqlCon.sendQuery(addManager);

		TransferMarketUpdate tmUpdate = new TransferMarketUpdate();
		tmUpdate.setMessageController(messageController);
		try {
			tmUpdate.messageArrived(nfMessage);
		} catch (NullPointerException npe) {
			// valid null pointer, because there is no client, where the answer
			// can be sent to
		}

		TestUtils.clearTable(sqlCon,"Manager");
	}

	/**
	 * Tests, if a {@link Player} can be added to the {@link MarketTest}
	 * 
	 * @see /T0224/
	 */
	@Test
	public void addPlayerToMarket() {

	}
	
	private static List<Player> generatePlayerList() {
		List<Player> retval = new ArrayList<>();
		Player p = new Player();
		p.setSportalID(PLAYER_SPORTAL_ID);
		p.setName(PLAYER_NAME);
		p.setWorth(PLAYER_WORTH);
		retval.add(p);
		return retval;
	}
	
	private void addPlayerToMarket(int communityID) {
		String addPlayerTOMarket = "INSERT INTO Transfermarkt (Spielrunde_ID,Spieler_ID,Min_Preis,Inhaber_ID) VALUES (" + communityID + "," + PLAYER_SPORTAL_ID + "," + PLAYER_WORTH + ",-1)";
		sqlCon.sendQuery(addPlayerTOMarket);
	}
}
