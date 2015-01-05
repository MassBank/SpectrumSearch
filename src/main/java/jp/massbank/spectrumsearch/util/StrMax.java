package jp.massbank.spectrumsearch.util;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.derby.agg.Aggregator;

public class StrMax<V extends Comparable<V>> implements Aggregator<V,V, StrMax<V>> {

	private static final long serialVersionUID = -1207529747839707745L;

	private ArrayList<V> _values;
	
	@Override
	public void accumulate(V value) {
		_values.add(value);
	}

	@Override
	public void init() {
		_values = new ArrayList<V>();
	}

	@Override
	public void merge(StrMax<V> other) {
		_values.addAll(other._values);
	}

	@Override
	public V terminate() {
		Collections.sort(_values);

        int count = _values.size();
        
        if (count == 0) {return null;}
        else {return _values.get(count/2 );}
	}


}
