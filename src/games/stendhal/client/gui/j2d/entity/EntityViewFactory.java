/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.client.Triple;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.wt.core.WtWindowManager;

/*
 * The entity views are generic, but we don't simply have sufficient data to
 * either use excessive casting at places, or using raw types. The latter keeps
 * the code more readable.
 */
@SuppressWarnings("rawtypes")
public class EntityViewFactory {
	/**
	 * Log4J.
	 */
	private static final Logger LOGGER = Logger.getLogger(EntityViewFactory.class);

	private static Map<Triple<String, String, String>, Class<? extends EntityView>> viewMap = new HashMap<Triple<String, String, String>, Class<? extends EntityView>>();

	/**
	 * Create an entity view factory.
	 */
	static {
		configure();
	}

	//
	// Entity2DViewFactory
	//

	/**
	 * Create an entity view from an entity.
	 *
	 * @param entity
	 *   An entity.
	 * @return
	 *   The corresponding view or `null`.
	 */
	@SuppressWarnings("unchecked")
	public static EntityView<IEntity> create(final IEntity entity) {
		try {
			// use User2DView for the active player
			if (entity.isUser()) {
				Entity2DView<IEntity> user2DView = new User2DView();
				user2DView.initialize(entity);
				return user2DView;
			}

			final String type = entity.getType();

			// lookup class
			final String eclass = entity.getEntityClass();
			final String ename = entity.getName();
			Class<? extends EntityView> entityClass = getViewClass(type, eclass, ename);
			if (entityClass == null) {
				// fallback to lookup via subclass instead of name
				final String subClass = entity.getEntitySubclass();
				entityClass = getViewClass(type, eclass, subClass);
				if (entityClass == null) {
					LOGGER.debug("No view for this entity. name: " + ename + ", type: " + type + ", class: " + eclass
							+ ", subclass: " + subClass);
					return null;
				}
			}

			// hack to hide blood
			if (entityClass == Blood2DView.class) {
				boolean showBlood = WtWindowManager.getInstance().getPropertyBoolean("gamescreen.blood", true);
				if (!showBlood) {
					return null;
				}
			}

			final EntityView<IEntity> en = entityClass.getDeclaredConstructor().newInstance();
			en.initialize(entity);

			return en;
		} catch (final Exception e) {
			LOGGER.error("Error creating entity for object: " + entity, e);
			return null;
		}

	}

	/**
	 * @param type
	 *   The entity's type such as item or creature.
	 * @param eclass
	 *   The entity's subtype such as book, drink, food, small_animal, huge_animal.
	 * @param name
	 *   Entity name or subclass.
	 * @return the java class of the Entity belonging to type and eclass
	 */
	static Class<? extends EntityView> getViewClass(final String type, final String eclass, final String name) {
		Class<? extends EntityView> result = viewMap.get(new Triple<String, String, String>(type, eclass, name));
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type, eclass, null));
		}
		if (result == null) {
			result = viewMap.get(new Triple<String, String, String>(type, null, null));
		}
		return result;
	}

	/**
	 * Retrieves a copy of the registered views.
	 */
	public static Map<Triple<String, String, String>, Class<? extends EntityView>> getViewMap() {
		final Map<Triple<String, String, String>, Class<? extends EntityView>> tmp = new HashMap<>();
		tmp.putAll(viewMap);
		return tmp;
	}

	/**
	 * Configure the view map.
	 */
	private static void configure() {
		// item
		register("item", null, null, Item2DView.class);
		register("item", "ammunition", null, StackableItem2DView.class);
		register("item", "book", "bestiary", UseableGenericItem2DView.class);
		register("item", "box", null, Box2DView.class);
		register("item", "club", "wizard_staff", UseableItem2DView.class);
		register("item", "container", null, StackableItem2DView.class);
		// FIXME: `games.stendhal.server.entity.item.ItemTest` copy constructor test fails
		//~ register("item", "documents", "coupon", StackableItem2DView.class);
		register("item", "drink", null, UseableItem2DView.class);
		register("item", "flower", null, StackableItem2DView.class);
		register("item", "food", null, UseableItem2DView.class);
		register("item", "herb", null, StackableItem2DView.class);
		register("item", "jewellery", null, StackableItem2DView.class);
		register("item", "misc", null, StackableItem2DView.class);
		register("item", "misc", "bulb", UseableItem2DView.class);
		register("item", "misc", "seed", UseableItem2DView.class);
		register("item", "misc", "snowglobe", UseableGenericItem2DView.class);
		register("item", "misc", "teddy", UseableGenericItem2DView.class);
		register("item", "missile", null, StackableItem2DView.class);
		register("item", "money", null, StackableItem2DView.class);
		register("item", "resource", null, StackableItem2DView.class);
		register("item", "ring", null, Ring2DView.class);
		register("item", "ring", "emerald ring", BreakableRing2DView.class);
		register("item", "ring", "wedding", UseableRing2DView.class);
		register("item", "scroll", null, UseableItem2DView.class);
		register("item", "special", null, StackableItem2DView.class);
		register("item", "special", "mithril clasp", Item2DView.class);
		register("item", "tool", "foodmill", UseableItem2DView.class);
		register("item", "tool", "metal detector", UseableGenericItem2DView.class);
		register("item", "tool", "rope", StackableItem2DView.class);
		register("item", "tool", "rotary cutter", UseableGenericItem2DView.class);
		register("item", "tool", "scroll eraser", UseableItem2DView.class);
		register("item", "tool", "sugarmill", UseableItem2DView.class);

		// grower
		register("growing_entity_spawner", null, null, GrainField2DView.class);
		register("growing_entity_spawner", "items/grower/carrot_grower", null, CarrotGrower2DView.class);
		register("growing_entity_spawner", "items/grower/wood_grower", null, CarrotGrower2DView.class);
		register("plant_grower", null, null, PlantGrower2DView.class);

		// sign
		register("blackboard", null, null, Sign2DView.class);
		register("rented_sign", null, null, Sign2DView.class);
		register("shop_sign", null, null, ShopSign2DView.class);
		register("sign", null, null, Sign2DView.class);

		// portal & door
		register("door", null, null, Door2DView.class);
		register("gate", null, null, Gate2DView.class);
		register("house_portal", null, null, HousePortal2DView.class);
		register("portal", null, null, Portal2DView.class);

		// NPC
		register("baby_dragon", null, null, Pet2DView.class);
		register("cat", null, null, Pet2DView.class);
		register("npc", null, null, NPC2DView.class);
		register("pet", null, null, Pet2DView.class);
		register("purple_dragon", null, null, Pet2DView.class);
		register("sheep", null, null, Sheep2DView.class);
		register("training_dummy", null, null, TrainingDummy2DView.class);

		// creature
		register("creature", null, null, Creature2DView.class);
		register("creature", "ent", null, BossCreature2DView.class);

		// resource sources
		register("fish_source", null, null, UseableEntity2DView.class);
		register("gold_source", null, null, UseableEntity2DView.class);
		register("well_source", null, null, UseableEntity2DView.class);

		// misc
		register("area", null, null, InvisibleEntity2DView.class);
		register("block", null, null, LookableEntity2DView.class);
		register("blood", null, null, Blood2DView.class);
		register("chest", null, null, Chest2DView.class);
		register("corpse", null, null, Corpse2DView.class);
		register("fire", null, null, UseableEntity2DView.class);
		register("flyover", null, null, FlyOverArea2DView.class);
		register("food", null, null, SheepFood2DView.class);
		register("game_board", null, null, GameBoard2DView.class);
		register("player", null, null, Player2DView.class);
		register("spell", null, null, Spell2DView.class);
		register("useable_entity", null, null, UseableEntity2DView.class);
		register("walkblocker", null, null, WalkBlocker2DView.class);
		register("wall", null, null, Wall2DView.class);
	}

	/**
	 * @param type
	 *   The entity's type such as item or creature.
	 * @param eclass
	 *   The entity's subtype such as book, drink, food, small_animal, huge_animal.
	 * @param name
	 *   Entity name or subclass.
	 * @param implementation
	 *   The java class of the Entity.
	 */
	private static void register(final String type, final String eclass, final String name,
			final Class<? extends EntityView> implementation) {
		viewMap.put(new Triple<String, String, String>(type, eclass, name), implementation);
	}
}
