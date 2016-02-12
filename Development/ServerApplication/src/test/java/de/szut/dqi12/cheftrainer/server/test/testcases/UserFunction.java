package de.szut.dqi12.cheftrainer.server.test.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.beans.binding.When;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.CommunityAuthenticationMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewOfferMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewPlayerOnMarketMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.UserAuthenticationMessage;
import de.szut.dqi12.cheftrainer.server.callables.CommunityAuthentication;
import de.szut.dqi12.cheftrainer.server.callables.TransferMarketUpdate;
import de.szut.dqi12.cheftrainer.server.callables.UserAuthentication;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;

/**
 * This TestCase will Test all user functions.
 * 
 * @see 'Pflichtenheft 8.1 Benutzerfunktionen'
 * @author Alexander Brennecke
 *
 */
public class UserFunction {

	private static SQLConnection sqlCon;
	private static User user;
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

	@Mock
	private static MessageController messageController;

	@BeforeClass
	public static void init() throws IOException {
		sqlCon = new SQLConnection(true);
		prepareDatabase();

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

	private static void clearTable(String tableName) {
		String query = "DELETE FROM " + tableName;
		sqlCon.sendQuery(query);
	}

	/**
	 * Tests the registration of a new {@link User}.
	 * 
	 * @see '/T0010/'
	 */
	@Test
	public void userRegistration() {
		UserAuthenticationMessage uaMessage = new UserAuthenticationMessage();
		uaMessage.setAuthentificationType(MIDs.REGISTRATION);
		uaMessage.setUser(user);

		UserAuthentication userAuthentication = new UserAuthentication();
		try {
			userAuthentication.messageArrived(uaMessage);
		} catch (NullPointerException npe) {
			// there will be a NPE, because there is no client, where the server
			// can send the UserAuthentificationAckMessage.
		}

		String getUsers = "SELECT * FROM Nutzer";
		ResultSet rs = sqlCon.sendQuery(getUsers);
		int userCounter = 0;
		try {
			while (rs.next()) {
				userCounter++;
				testUser(rs);
			}
		} catch (SQLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		assertEquals(1, userCounter);
	}

	/**
	 * Tests the login of an existing{@link User}.
	 * 
	 * @see '/T0020/'
	 */
	@Test
	public void userLogin() {
		UserAuthenticationMessage uaMessage = new UserAuthenticationMessage();
		uaMessage.setAuthentificationType(MIDs.LOGIN);
		uaMessage.setUser(user);

		// Do this to encrypt the password. Is required for assert.
		UserAuthenticationMessage receivingMessage = new UserAuthenticationMessage(new JSONObject(uaMessage.getMessageContent()));

		HashMap<String, Boolean> dbInfo = DatabaseRequests.loginUser(receivingMessage.getUser());
		for (String s : dbInfo.keySet()) {
			assertTrue(s + " was false", dbInfo.get(s));
		}
	}

	/**
	 * Tests the registration of a new {@link Community}.
	 * 
	 * @see '/T0120/'
	 */
	@Test
	public void communityRegistration() {
		User u = DatabaseRequests.getUserData(USER_LOGIN);
		Session s = new Session();
		s.setUserID(u.getUserID());

		messageController = Mockito.mock(MessageController.class);
		Mockito.when(messageController.getSession()).thenReturn(s);

		CommunityAuthenticationMessage message = new CommunityAuthenticationMessage(MIDs.CREATION);
		message.setName(COMMUNITY_NAME);
		message.setPassword(COMMUNITY_PASSWORD);

		try {
			CommunityAuthentication communityAuthentication = new CommunityAuthentication();
			communityAuthentication.setMessageController(messageController);
			communityAuthentication.messageArrived(message);
		} catch (NullPointerException npe) {
			// there will be a NPE, because there is no client, where the server
			// can send the UserAuthentificationAckMessage.
		}

		String getCommunities = "SELECT * FROM Spielrunde";
		ResultSet rs = sqlCon.sendQuery(getCommunities);
		try {
			while (rs.next()) {
				assertEquals(rs.getString("Name"), COMMUNITY_NAME);
				String hashPassword = CipherFactory.getMD5(COMMUNITY_PASSWORD);
				assertEquals(hashPassword, rs.getString("Passwort"));
			}
		} catch (SQLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String getManager = "SELECT * FROM Manager INNER JOIN Nutzer where Manager.Nutzer_ID=Nutzer.ID";
		rs = sqlCon.sendQuery(getManager);
		int communityCounter = 0;
		try {
			while (rs.next()) {
				communityCounter++;
				testUser(rs);
				assertEquals(rs.getLong("Budget"), 15000000);
				assertEquals(rs.getInt("Anzahl_Stuermer"), 2);
				assertEquals(rs.getInt("Anzahl_Mittelfeld"), 4);
				assertEquals(rs.getInt("Anzahl_Abwehr"), 4);
			}
		} catch (SQLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		assertEquals(1, communityCounter);
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

		clearTable("Manager");
	}

	/**
	 * Tests, if a {@link Player} can be added to the {@link Market}
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

	private void testUser(ResultSet rs) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
		assertEquals(rs.getString("Vorname"), USER_NAME);
		assertEquals(rs.getString("Nachname"), USER_SURNAME);
		assertEquals(rs.getString("Nutzername"), USER_LOGIN);
		assertEquals(rs.getString("EMail"), USER_EMAIL);

		String hashPassword = CipherFactory.getMD5(USER_PASSWORD);
		assertEquals(hashPassword, rs.getString("Passwort"));
	}

	private static void prepareDatabase() {
		clearTable("NUTZER");
		clearTable("Spielrunde");
		clearTable("Manager");
		clearTable("Mannschaft");
		clearTable("Transfermarkt");
		clearTable("Gebote");
	}

	private void addPlayerToMarket(int communityID) {
		String addPlayerTOMarket = "INSERT INTO Transfermarkt (Spielrunde_ID,Spieler_ID,Min_Preis,Inhaber_ID) VALUES (" + communityID + "," + PLAYER_SPORTAL_ID + "," + PLAYER_WORTH + ",-1)";
		sqlCon.sendQuery(addPlayerTOMarket);
	}

}
