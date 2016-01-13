package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.images.ImageController;
import de.szut.dqi12.cheftrainer.client.images.ImageUpdate;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers.AddPlayerToMarketController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers.OfferDialog;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.MarketPlayer;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

/**
 * This is the controller of the transfer-market gui-component.
 * 
 * @author Robin and Alexander
 *
 */

public class TransferMarketController implements ControllerInterface, ImageUpdate {
	// @FXML
	// GridPane market;
	@FXML
	TableView<MarketPlayer> marketTable;
	@FXML
	private TableColumn<MarketPlayer, Player> nameCol;
	@FXML
	private TableColumn<MarketPlayer, String> pointsCol;
	@FXML
	private TableColumn<MarketPlayer, String> worthCol;

	private MarketPlayer selectedMarketPlayer;

	private ImageController imageController;

	private boolean updateIsBlocked = false;

	private ObservableList<MarketPlayer> tableObservable;
	
	private Stage addPlayerStage;
	
	/**
	 * init() function, which comes from the {@link ControllerInterface}. It is
	 * not used here.
	 */
	@Override
	public void init() {
	}

	
	/**
	 * This functions loads the current {@link Market} from the {@link Session} and creates a {@link ObservableList} from it.
	 * @return a new {@link ObservableList} with all {@link Player}s, which are on the current {@link Market}.
	 */
	private ObservableList<MarketPlayer> getObservable() {
		tableObservable = FXCollections.observableArrayList();
		Community currentCommunity = Controller.getInstance().getSession().getCurrentCommunity();
		List<Player> playerList = currentCommunity.getMarket().getPlayers();
		for (Player p : playerList) {
			tableObservable.add(p.getMarketPlayer());
		}
		return tableObservable;
	}
	
	
	/**
	 * This function is the initialize function of FXML. It is called from FXML,
	 * when this view should be displayed. This function fetches the players,
	 * which are on the {@link Market}, for the selected {@link Community}.
	 * After that, the {@link Player} will be added to the table.
	 */
	@FXML
	public void initialize() {
		imageController = new ImageController(this);
		marketTable.setItems(getObservable());
		nameCol.setCellValueFactory(new PropertyValueFactory<>("Player"));
		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		worthCol.setCellValueFactory(data -> data.getValue().getWerth());

		marketTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> playerPressed(newValue));

		marketTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					onDoubleClick();
				}
			}
		});

		updateIsBlocked = true;

		// Sets the CellFactory to the image cells
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
		updateTableView();
	}

	/**
	 * Sets the visible of a element to false and back to true, because that
	 * triggers the update Event of the TableView.
	 */
	private void updateTableView() {
		TableColumn<MarketPlayer, ?> tc = marketTable.getColumns().get(0);
		if (tc != null) {
			tc.setVisible(false);
			tc.setVisible(true);
		}
	}

	private void onDoubleClick() {
		// TODO: INDEXE MUESSTEN MOEGLICHERWEISE UEBERARBEITET WERDEN
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sourcesFXML/PlayerDetailedFrame.fxml"));
			GridPane root = (GridPane) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setTitle(selectedMarketPlayer.getPlayer().getName());
			stage.setScene(new Scene(root));
			PlayerDetailedController pdc = fxmlLoader.getController();
			pdc.setPlayer(selectedMarketPlayer.getPlayer());
			pdc.showOffer();
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void playerPressed(MarketPlayer marketPlayer) {
		this.selectedMarketPlayer = marketPlayer;
	}

	/**
	 * Is called when the Button "add player" is clicked. Opens a dialog for
	 * adding a player to transfer-marked.
	 */
	@FXML
	public void addPlayer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dialogFXML/AddPlayerToTransfermarket.fxml"));
			GridPane root = (GridPane) fxmlLoader.load();
			AddPlayerToMarketController aptmc = ((AddPlayerToMarketController)fxmlLoader.getController());
			aptmc.setTmc(this);
			addPlayerStage = new Stage();
			addPlayerStage.setTitle("Your Players");
			addPlayerStage.setScene(new Scene(root));
			addPlayerStage.setResizable(true);
			addPlayerStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addPlayerToTable(MarketPlayer mp){
		tableObservable.add(mp);
	}
	
	public void removePlayerFromTable(MarketPlayer mp){
		tableObservable.remove(mp);
	}
	

	/**
	 * Is called when the Button "show offers" is called. Opens a dialog which
	 * shows all offers possibility's for every player.
	 */
	@FXML
	public void showOffers() {
		OfferDialog offerDialog = new OfferDialog();
		offerDialog.showOfferDialog();
	}

	@Override
	public void enterPressed() {

	}

	@Override
	public void updateImage(Image image, int id) {
		if (!updateIsBlocked) {
			updateTableView();
		}
	}

	@Override
	public void messageArrived(Boolean flag) {
		// TODO Auto-generated method stub
		
	}
}
