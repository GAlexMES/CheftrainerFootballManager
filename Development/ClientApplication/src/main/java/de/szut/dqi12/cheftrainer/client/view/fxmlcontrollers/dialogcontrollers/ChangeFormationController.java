package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.images.ImageController;
import de.szut.dqi12.cheftrainer.client.images.ImageUpdate;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.LineUpController;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.MarketPlayer;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;

public class ChangeFormationController implements ImageUpdate {

	@FXML
	TableView<MarketPlayer> playerTable;
	@FXML
	private TableColumn<MarketPlayer, Player> nameCol;
	@FXML
	private TableColumn<MarketPlayer, String> pointsCol;
	@FXML
	private TableColumn<MarketPlayer, String> worthCol;

	private ImageController imageController;

	private boolean updateIsBlocked = false;

	private LineUpController luc;
	private Manager manager;
	private Player selectedPlayer;
	private ObservableList<MarketPlayer> tableObservable;

	public ChangeFormationController() {
		Controller controller = Controller.getInstance();
		manager = controller.getSession().getCurrentCommunity().getUsersManager();

	}

	private ObservableList<MarketPlayer> getObservable() {
		tableObservable = FXCollections.observableArrayList();
		List<Player> playerList = manager.getPlayers();
		for (Player p : playerList) {
			tableObservable.add(p.getMarketPlayer());
		}
		return tableObservable;
	}

	@FXML
	public void initialize() {
		imageController = new ImageController(this);
		getObservable();
		playerTable.setItems(tableObservable);

		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		worthCol.setCellValueFactory(data -> data.getValue().getWorth());

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
		luc.setPlayer(marketPlayer.getPlayer(),selectedPlayer);
		Stage s = (Stage)playerTable.getScene().getWindow();
		s.close();
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

	public void setSelectedPlayer(Player player, List<Player> notPlayingPlayer) {
		selectedPlayer = player;
		tableObservable.clear();
		notPlayingPlayer.forEach(p -> {
			if (p.getPosition().equals(player.getPosition()))
				tableObservable.add(p.getMarketPlayer());
		});
	}

	public void setLUP(LineUpController luc) {
		this.luc = luc;
	}
}
