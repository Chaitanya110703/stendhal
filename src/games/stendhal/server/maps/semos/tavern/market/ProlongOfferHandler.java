package games.stendhal.server.maps.semos.tavern.market;

import java.util.Map;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;

public class ProlongOfferHandler {
	private Offer offer;
	
	public void add(SpeakerNPC npc) {
		npc.add(ConversationStates.ATTENDING, "prolong", null, ConversationStates.ATTENDING, null, 
				new ProlongOfferChatAction());
		npc.add(ConversationStates.SERVICE_OFFERED, ConversationPhrases.YES_MESSAGES, 
				ConversationStates.ATTENDING, null, new ConfirmProlongOfferChatAction());
		npc.add(ConversationStates.SERVICE_OFFERED, ConversationPhrases.NO_MESSAGES, null, 
				ConversationStates.ATTENDING, "Ok, how else may I help you?", null);
	}
	
	protected class ProlongOfferChatAction extends KnownOffersChatAction {

		public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
			if (sentence.hasError()) {
				npc.say("Sorry, I did not understand you. "
						+ sentence.getErrorString());
			} else if (sentence.getExpressions().iterator().next().toString().equals("prolong")){
				handleSentence(player, sentence, npc);
			}
		}

		private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
			MarketManagerNPC manager = (MarketManagerNPC) npc;
			try {
				String offerNumber = getOfferNumberFromSentence(sentence).toString();
				
				Map<String,Offer> offerMap = manager.getOfferMap().get(player.getName());
				if (offerMap == null) {
					npc.say("Please check your offers first.");
					return;
				}
				if(offerMap.containsKey(offerNumber)) {
					Offer o = offerMap.get(offerNumber);
					if(o.getOfferer().equals(player.getName())) {
						setOffer(o);
						int quantity = getQuantity(o.getItem());
						npc.say("Do you want to prolong your offer of " 
								+ Grammar.quantityplnoun(quantity, 
								o.getItem().getName()) + " for " 
								+ TradingUtility.calculateFee(player, o.getPrice()).intValue() + " money?");
						npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
					} else {
						npc.say("You can only prolong your own offers. Please say #show #mine to see only your offers.");
					}
				} else {
					npc.say("Sorry, please choose a number from those I told you to prolong your offer.");
					return;
				}
			} catch (NumberFormatException e) {
				npc.say("Sorry, please say #remove #number");
			}
		}
	}
	
	protected class ConfirmProlongOfferChatAction implements ChatAction {
		public void fire (Player player, Sentence sentence, SpeakerNPC npc) {
			Offer offer = getOffer();
			if (!wouldOverflowMaxOffers(player, offer)) {
				Integer fee = Integer.valueOf(TradingUtility.calculateFee(player, offer.getPrice()).intValue());
				if (player.isEquipped("money", fee)) { 
					if (prolongOffer(player, offer)) {
						TradingUtility.substractTradingFee(player, offer.getPrice());
						npc.say("I prolonged your offer and took the fee of "+fee.toString()+" again.");
					} else {
						npc.say("Sorry, that offer has already been removed from the market.");
					}
					// Changed the status, or it has been changed by expiration. Obsolete the offers
					((MarketManagerNPC) npc).getOfferMap().put(player.getName(), null);
				} else {
					npc.say("You cannot afford the trading fee of "+fee.toString());
				}
			} else {
				npc.say("Sorry, you can have only " + TradingUtility.MAX_NUMBER_OFF_OFFERS
						+ " active offers at a time.");
			}
		}
		
		/**
		 * Check if prolonging an offer would result the player having too many active offers on market.
		 * 
		 * @param player the player to be checked
		 * @param offer the offer the player wants to prolong
		 * @return true if prolonging the offer should be denied
		 */
		private boolean wouldOverflowMaxOffers(Player player, Offer offer) {
			Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
			
			if ((market.countOffersOfPlayer(player) == TradingUtility.MAX_NUMBER_OFF_OFFERS)
					&& market.getExpiredOffers().contains(offer)) {
				return true;
			}
			
			return false;
		}
		
		private boolean prolongOffer(Player player, Offer o) {
			Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
			if (market != null) {
				if (market.prolongOffer(o) != null) {
					String messageNumberOfOffers = "You now have put "+Integer.valueOf(market.countOffersOfPlayer(player)).toString()+" offers.";
					player.sendPrivateText(messageNumberOfOffers);
					
					return true;
				}
			}
			
			return false;
		}
	}
	
	private void setOffer(Offer offer) {
		this.offer = offer;
	}
	
	private Offer getOffer() {
		return offer;
	}
	
	private int getQuantity(Item item) {
		int quantity = 1;
		if (item instanceof StackableItem) {
			quantity = ((StackableItem) item).getQuantity();
		}
		
		return quantity;
	}
}
