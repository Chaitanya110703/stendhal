package games.stendhal.server.core.rule;

import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.spell.Spell;

import java.util.Collection;
/**
 * Ruleset Interface for resolving Entities in Stendhal.
 * 
 * @author Matthias Totz
 */
public interface EntityManager {

	boolean addItem(DefaultItem item);

	boolean addCreature(DefaultCreature creature);

	/**
	 * @return a list of all Creatures that are used at least once.
	 */
	Collection<Creature> getCreatures();

	/**
	 * @return a list of all Items that are being used at least once.
	 */
	Collection<Item> getItems();

	/**
	 * Returns the entity or <code>null</code> if the class is unknown.
	 * 
	 * @param clazz
	 *            the creature class, must not be <code>null</code>
	 * @return the entity or <code>null</code>
	 * 
	 */
	Entity getEntity(String clazz);

	/**
	 * returns the creature or <code>null</code> if the id is unknown.
	 * @param tileset 
	 * 
	 * @param id
	 *            the tile id
	 * @return the creature or <code>null</code>
	 */
	Creature getCreature(String tileset, int id);

	/**
	 * returns the creature or <code>null</code> if the clazz is unknown.
	 * 
	 * @param clazz
	 *            the creature class, must not be <code>null</code>
	 * @return the creature or <code>null</code>
	 * 
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	Creature getCreature(String clazz);

	/**
	 * Returns the DefaultCreature or <code>null</code> if the clazz is
	 * unknown.
	 * 
	 * @param clazz
	 *            the creature class
	 * @return the creature or <code>null</code>
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	DefaultCreature getDefaultCreature(String clazz);

	/**
	 * Return true if the Entity is a creature.
	 * @param tileset 
	 * 
	 * @param id
	 *            the tile id
	 * @return true if it is a creature, false otherwise
	 */
	boolean isCreature(String tileset, int id);

	/**
	 * Return true if the Entity is a creature.
	 * 
	 * @param clazz
	 *            the creature class, must not be <code>null</code>
	 * @return true if it is a creature, false otherwise
	 * 
	 */
	boolean isCreature(String clazz);

	/**
	 * Return true if the Entity is a Item.
	 * 
	 * @param clazz
	 *            the Item class, must not be <code>null</code>
	 * @return true if it is a Item, false otherwise
	 * 
	 */
	boolean isItem(String clazz);

	/**
	 * Returns the item or <code>null</code> if the clazz is unknown.
	 * 
	 * @param clazz
	 *            the item class, must not be <code>null</code>
	 * @return the item or <code>null</code>
	 * 
	 */
	Item getItem(String clazz);
	
	/**
	 * Retrieves a Spell or null if the spell is unknown.
	 * 
	 * @param spell
	 * @return the spell or null if spell is unknown
	 */
	Spell getSpell(String spell);
	
	/**
	 * checks if spellName points to an existing spell
	 * @param spellName
	 * @return true iff a spell with that name exists
	 */
	boolean isSpell(String spellName);

	/**
	 * @return a collection of spells that are used at least once
	 */
	Collection<Spell> getSpells();

}
