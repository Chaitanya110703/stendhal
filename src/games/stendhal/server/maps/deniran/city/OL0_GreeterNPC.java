package games.stendhal.server.maps.deniran.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

/**
 * Builds the city greeter NPC.
 *
 * @author timothyb89
 */
public class OL0_GreeterNPC implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();


	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}


	//
	// OL0_GreeterNPC
	//

	private void buildNPC(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC GreeterNPC = new SpeakerNPC("Xhiphin Zohos") {
			@Override
					protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(39, 28));
				nodes.add(new Path.Node(23, 28));
				nodes.add(new Path.Node(23, 20));
				nodes.add(new Path.Node(40, 20));
				setPath(nodes, true);
					}
	
					@Override
							protected void createDialog() {
						addGreeting("Hello! Welcome to Deniran City! Would you like to #learn about Deniran?");
						addReply("learn", "Deniran City is the jewl of the Faiumoni empire. It has a very important trade route with Orril and Semos to the North and #Sikhw to the South.");
						addReply("sikhw", "Sikhw is an old city that was conqured a long time ago. It is now nearly unreachable.");
						addJob("I greet all of the new-comers to Denirian.");
						addHelp("You can head into the tavern to buy food, drinks, and other items.You can also visit the people in the houses, or visit hte blacksmith or the city hotel.");
						//addSeller(new SellerBehaviour(shops.get("food&drinks")));
						addGoodbye("Bye.");
							}
		};
		npcs.add(GreeterNPC);
		zone.assignRPObjectID(GreeterNPC);
		GreeterNPC.put("outfit", "05010601");
		GreeterNPC.set(39, 28);
		GreeterNPC.initHP(1000);
		zone.addNPC(GreeterNPC);
	}
}
