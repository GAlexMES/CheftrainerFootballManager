package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

/**
 * 
 * @author Robin
 *
 */
public class Transaction {
	
	private Double price;
	private boolean outgoing;
	private String tenderer;
	private String receiver;
	private Player player;
	
	public Transaction(Double price, boolean outgoing, String tenderer,
			String receiver, Player player) {
		this.price = price;
		this.outgoing = outgoing;
		this.tenderer = tenderer;
		this.receiver = receiver;
		this.player = player;
	}
	public Double getPrice() {
		return price;
	}
	public boolean isOutgoing() {
		return outgoing;
	}
	public String getTenderer() {
		return tenderer;
	}
	public String getReceiver() {
		return receiver;
	}
	public Player getPlayer() {
		return player;
	}

}
