package de.szut.dqi12.cheftrainer.server.test.pflichtenheft;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewOfferMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewPlayerOnMarketMessage;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.callables.TransferMarketUpdate;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
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
	
	private static TransferMarketUpdate tmUpdate;
	
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
	
	@BeforeClass
	public static void prepareDatabase() throws IOException{
		Controller controller = Controller.getInstance();
		controller.creatDatabaseCommunication(false);
		sqlCon = controller.getSQLConnection();
		TestUtils.prepareDatabase(sqlCon);

		user = new User();
		user.setFirstName(USER_NAME);
		user.setLastName(USER_SURNAME);
		user.setUserName(USER_LOGIN);
		user.setPassword(USER_PASSWORD);
		user.seteMail(USER_EMAIL);
		user.setUserId(1);
		
		TestUtils.preparePlayerTable(sqlCon);
		DatabaseRequests.registerNewUser(user);
		DatabaseRequests.createNewCommunity(COMMUNITY_NAME,COMMUNITY_PASSWORD, 0);
		DatabaseRequests.enterCommunity(COMMUNITY_NAME, COMMUNITY_PASSWORD, user.getUserID());
		
		market = Mockito.mock(Market.class);
		Mockito.when(market.getPlayers()).thenReturn(generatePlayerList());
		community = Mockito.mock(Community.class);
		Mockito.when(community.getMarket()).thenReturn(market);
		Mockito.when(community.getCommunityID()).thenReturn(1);
		
		tmUpdate = new TransferMarketUpdate();
		tmUpdate.setMessageController(messageController);
	}
	
	/**
	 * Will be called after the test class is finished.
	 * Closes the database connection.
	 */
	@AfterClass
	public static void closeDatabase(){
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

		
		try {
			tmUpdate.messageArrived(nfMessage);
		} catch (NullPointerException npe) {
			// valid null pointer, because there is no client, where the answer
			// can be sent to
		}
		
		Community con = DatabaseRequests.getCummunitiesForUser(user.getUserID()).get(0);
		Market m = con.getMarket();
		
		assertEquals(1,m.getTransactions().size());
		
		Transaction dtr = m.getTransactions().get(0);
		compareTransactions(tr,dtr);
	}

	/**
	 * Tests, if a {@link Player} can be added to the {@link MarketTest}
	 * 
	 * @see /T0224/
	 */
	@Test
	public void addPlayerToMarket() {
		Community con = DatabaseRequests.getCummunitiesForUser(user.getUserID()).get(0);
		Manager m = con.getManagers().get(0);
		List<Player> players = m.getPlayers();
		Player p = players.get(0);
		
		NewPlayerOnMarketMessage npomMessage = new NewPlayerOnMarketMessage();
		npomMessage.setAddPlayer(true);
		npomMessage.setPlayer(p);
		npomMessage.setManagerID(m.getID());
		npomMessage.setCommunityID(con.getCommunityID());
		npomMessage.setMessageContent(npomMessage.createJSON());;
		
		
		try {
			tmUpdate.messageArrived(npomMessage);
		} catch (NullPointerException npe) {
			// valid null pointer, because there is no client, where the answer
			// can be sent to
		}
		
		Community dCon = DatabaseRequests.getCummunitiesForUser(user.getUserID()).get(0);
		Market dMarket = dCon.getMarket();
		Map<Integer,Player> playerMap = dMarket.getPlayerMap();
		
		assertTrue(playerMap.keySet().contains(p.getSportalID()));
		
		Manager dManager = dCon.getManagers().get(0);
		List<Player> playerList = dManager.getPlayers();
		boolean managerOwnsPlayer = false;
		for(Player pl : playerList){
			if(pl.getSportalID()==p.getSportalID()){
				managerOwnsPlayer = true;
				break;
			}
		}
		
		assertTrue(managerOwnsPlayer);
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
	
	private void compareTransactions(Transaction tr, Transaction dtr){
		assertEquals(tr.getCommunityID(),dtr.getCommunityID());
		assertEquals(1, dtr.getManagerID());
		assertEquals(tr.getOfferedPrice(), dtr.getOfferedPrice());
		assertEquals(tr.getPlayerSportalID(), dtr.getPlayerSportalID());
	}
}
