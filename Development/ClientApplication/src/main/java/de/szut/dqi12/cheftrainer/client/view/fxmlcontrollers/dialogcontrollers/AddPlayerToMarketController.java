package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers;

import java.util.List;
import java.util.Map;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.images.ImageController;
import de.szut.dqi12.cheftrainer.client.images.ImageUpdate;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.TransferMarketController;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.MarketPlayer;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewPlayerOnMarketMessage;

public class AddPlayerToMarketController implements ImageUpdate {

	public final String ADD_TO_MARKET = "add to market";
	public final String REMOVE_FROM_MARKET = "remove from market";

	@FXML
	TableView<MarketPlayer> playerTable;
	@FXML
	private TableColumn<MarketPlayer, Player> nameCol;
	@FXML
	private TableColumn<MarketPlayer, String> pointsCol;
	@FXML
	private TableColumn<MarketPlayer, String> worthCol;
	@FXML
	private TableColumn<MarketPlayer, Player> actionCol;

	private ImageController imageController;

	private boolean updateIsBlocked = false;

	private Community currentCommunity;
	private Market market;
	private TransferMarketController tmc;

	private ObservableList<MarketPlayer> tableObservable;

	public AddPlayerToMarketController() {
		Controller controller = Controller.getInstance();
		currentCommunity = controller.getSession().getCurrentCommunity();
		market = currentCommunity.getMarket();

	}

	private ObservableList<MarketPlayer> getObservable() {
		tableObservable = FXCollections.observableArrayList();
		Manager manager = currentCommunity.getUsersManager();
		List<Player> playerList = manager.getPlayers();
		for (Player p : playerList) {
			tableObservable.add(p.getMarketPlayer());
		}
		return tableObservable;
	}

	private void refreshTable() {
		playerTable.getColumns().get(0).setVisible(false);
		playerTable.getColumns().get(0).setVisible(true);
	}

	private void triggerStatus(Player mp) {
		if (mp.isOnMarket()) {
			sentMessageToServer(mp, false);
			market.removePlayer(mp);
			tmc.removePlayerFromTable(mp.getMarketPlayer());
		} else {
			sentMessageToServer(mp, true);
			market.addPlayer(mp);
			tmc.addPlayerToTable(mp.getMarketPlayer());
		}

		refreshTable();
	}

	private void sentMessageToServer(Player p, boolean addPlayer) {
		NewPlayerOnMarketMessage npomm = new NewPlayerOnMarketMessage();
		npomm.setPlayer(p);
		int managerID = currentCommunity.getUsersManager().getID();
		npomm.setManagerID(managerID);
		int communityID = currentCommunity.getCommunityID();
		npomm.setCommunityID(communityID);
		npomm.setAddPlayer(addPlayer);

		Client clientSocket = Controller.getInstance().getSession().getClientSocket();
		clientSocket.sendMessage(npomm);
	}

	private boolean isPlayerOnMarket(int sportalId) {
		Map<Integer, Player> playerMap = market.getPlayerMap();
		return playerMap.keySet().contains(sportalId);
	}

	@FXML
	public void initialize() {
		imageController = new ImageController(this);
		getObservable();
		playerTable.setItems(tableObservable);

		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		worthCol.setCellValueFactory(data -> data.getValue().getWorth());

		actionCol.setCellValueFactory(new PropertyValueFactory<>("Player"));
		actionCol.setCellFactory(params -> {
			Button editButton = new Button(ADD_TO_MARKET);
			TableCell<MarketPlayer, Player> cell = new TableCell<MarketPlayer, Player>() {
				@Override
				public void updateItem(Player player, boolean empty) {
					if (player != null) {
						int sportalID = player.getSportalID();
						boolean isOnMarket = isPlayerOnMarket(sportalID);
						player.setOnMarket(isOnMarket);
						super.updateItem(player, empty);
						if (empty) {
							setGraphic(null);
						} else {
							if (isOnMarket) {
								editButton.setText(REMOVE_FROM_MARKET);
							} else {
								editButton.setText(ADD_TO_MARKET);
							}
							setGraphic(editButton);
						}
					}
				}
			};

			editButton.setOnAction(e -> triggerStatus(cell.getItem()));

			return cell;
		});

		updateIsBlocked = true;

		// Sets the CellFactory to the image cells
		nameCol.setCellValueFactory(new PropertyValueFactory<>("Player"));
		nameCol.setCellFactory(params -> {
			TableCell<MarketPlayer, Player> cell = new TableCell<MarketPlayer, Player>() {
				ImageView imageview = new ImageView();

				@Override
				public void updateItem(Player item, boolean empty) {
					if (item != null) {
						HBox box = new HBox();
						box.setSpacing(10);
						VBox vbox = new VBox();
						vbox.getChildren().add(new Label(item.getName()));

						imageview.setFitHeight(100);
						imageview.setFitWidth(100);

						Image image = imageController.getPicture(item);
						imageview.setImage(image);

						box.getChildren().addAll(imageview, vbox);

						setGraphic(box);
					}
				}
			};
			return cell;
		});
		updateIsBlocked = false;
		playerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> playerPressed(newValue));
		updateTableView();
	}

	private void playerPressed(MarketPlayer marketPlayer) {
		// this.selectedMarketPlayer = marketPlayer;
	}

	/**
	 * Sets the visible of a element to false and back to true, because that
	 * triggers the update Event of the TableView.
	 */
	private void updateTableView() {
		TableColumn<MarketPlayer, ?> tc = playerTable.getColumns().get(0);
		if (tc != null) {
			tc.setVisible(false);
			tc.setVisible(true);
		}
	}

	@Override
	public void updateImage(Image image, int id) {
		if (!updateIsBlocked) {
			updateTableView();
		}

	}

	public void setTmc(TransferMarketController tmc) {
		this.tmc = tmc;
	}
}
