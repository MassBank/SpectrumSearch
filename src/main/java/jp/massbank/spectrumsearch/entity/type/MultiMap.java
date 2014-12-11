package jp.massbank.spectrumsearch.entity.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MultiMap extends HashMap {

	private static final long serialVersionUID = -9145022530624375133L;

	//
	// The Collection class that holds the values
	//
	private Class _collectionClass = null;

	//
	// The number of KEYS in the map - we need to track this ourselves
	// because size() is overridden to return the number of VALUES (the
	// number of values is the sum of Collection.size()).
	//
	private int _keySize = 0;

	/**
	 * 
	 * The default constructor creates a new map using a HashSet as the value
	 * collection type.
	 * 
	 * @see MultiMap#MultiMap(Class)
	 * 
	 */
	public MultiMap() {
		this(HashSet.class);
	}

	/**
	 *
	 * @param collectionClass
	 *            The Collection type to use for the map's values. The Class
	 *            must be an implementation of java.util.Collection.
	 *
	 */
	public MultiMap(Class collectionClass) {
		_collectionClass = collectionClass;
	}

	public final void clear() {
		super.clear();
		_keySize = 0;
	}

	/**
	 * 
	 * @param value
	 * 
	 * @return True if one or more of the keys in this map points to the given
	 *         value.
	 * 
	 */
	public final boolean containsValue(Object value) {
		Iterator i = values().iterator();

		//
		// look through each map entry's set of values to see if
		// the given value is present
		//
		while (i.hasNext()) {
			Collection values = (Collection) i.next();

			if (values.contains(value))
				return true;
		}

		return false;
	}

	/**
	 * 
	 * @return The number of keys in the map, <b>not</b> the number of entries.
	 * 
	 * @see #size()
	 *
	 */
	public final int keySetSize() {
		return _keySize;
	}

	/**
	 *
	 * Associates the value with the given key. If the key is not already in the
	 * map, it is added; otherwise, the value is simply added to the key's
	 * collection.
	 * 
	 * @return The value reference on success or null if the value was already
	 *         associated with the key.
	 *
	 */
	public final Object put(Object key, Object value) {
		Collection values = (Collection) get(key);

		//
		// if the key wasn't found - add it!
		//
		if (values == null) {
			values = (Collection) newInstance(_collectionClass);
			super.put(key, values);
			++_keySize;
		}

		//
		// try to add the value to the key's set
		//
		boolean success = values.add(value);
		return success ? value : null;
	}

	/**
	 * 
	 * Invokes the Class.newInstance() method on the given Class.
	 * 
	 * @param theClass
	 *            The type to instantiate.
	 * 
	 * @return An object of the given type, created with the default
	 *         constructor. A RuntimeException is thrown if the object could not
	 *         be created.
	 * 
	 */
	public static Object newInstance(Class theClass) {
		try {
			return theClass.newInstance();
		}

		catch (InstantiationException error) {
			Object[] filler = { theClass };
			String message = "ObjectCreationFailed";
			throw new RuntimeException(message);
		}

		catch (IllegalAccessException error) {
			Object[] filler = { theClass };
			String message = "DefaultConstructorHidden";
			throw new RuntimeException(message);
		}
	}

	/**
	 *
	 * Adds all of the entries in a generic map to the multimap. <br>
	 * <br>
	 * NOTE: Because we cannot guarantee that the implementation of the base
	 * class putAll(Map) simply calls the put method in a loop, we must override
	 * it here to ensure this happens. Otherwise, HashMap might associate the
	 * values directly with the keys, breaking the unofficial key -> Collection
	 * system.
	 * 
	 * @param map
	 *            The Map to copy - this does not have to be another MultiMap.
	 * 
	 */
	public final void putAll(Map map) {
		Set keys = map.keySet();
		Iterator i = keys.iterator();

		//
		// if the argument is a multi-map, we want to add all of the
		// values individually, otherwise each key will have a set
		// of values whose only value is... the collection of values.
		// that is, instead of key -> collection<value>, we'd have
		// key -> collection<collection<value>>
		//
		if (map instanceof MultiMap) {
			while (i.hasNext()) {
				Object key = i.next();
				Object value = map.get(key);

				Collection setOfValues = (Collection) value;
				Iterator j = setOfValues.iterator();

				while (j.hasNext())
					put(key, j.next());
			}
		}

		//
		// it's a "normal" map - just add the name-value pairs
		//
		else {
			while (i.hasNext()) {
				Object key = i.next();
				put(key, map.get(key));
			}
		}
	}

	/**
	 * 
	 * Removes <b>all</b> values associated with the given key.
	 * 
	 * @param key
	 * 
	 * @return The Collection of values previously associated with the key.
	 * 
	 */
	public final Object remove(Object key) {
		Object value = super.remove(key);
		--_keySize;
		return value;
	}

	/**
	 *
	 * NOTE: This method takes O(n) time, where n is the number of keys in the
	 * map. It would be more efficient to keep a counter for the size, but this
	 * would require overriding more methods and dealing with the complicated
	 * issue of map integrity and entrySet(). This implementation is the most
	 * robust when you consider that all Maps allow users to modify their
	 * contents without using the interface directly.
	 *
	 * @return The sum of the sizes of all the map entries (value collections).
	 * 
	 */
	public final int size() {
		Iterator i = values().iterator();
		int count = 0;

		//
		// for each key, add the number of values to the count
		//
		while (i.hasNext()) {
			Collection values = (Collection) i.next();
			count += values.size();
		}

		return count;
	}

}
