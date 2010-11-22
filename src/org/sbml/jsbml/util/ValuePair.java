/*
 * $Id: ValuePair.java 369 2010-10-12 10:36:35Z andreas-draeger $
 * $URL: https://jsbml.svn.sourceforge.net/svnroot/jsbml/trunk/src/org/sbml/jsbml/util/ValuePair.java $
 *
 *
 *==================================================================================
 * Copyright (c) 2009-2010 the copyright is held jointly by the individual
 * authors. See the file AUTHORS for the list of authors.
 *
 * This file is part of jsbml, the pure java SBML library. Please visit
 * http://sbml.org for more information about SBML, and http://jsbml.sourceforge.net/
 * to get the latest version of jsbml.
 *
 * jsbml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * jsbml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jsbml.  If not, see <http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html>.
 *
 *===================================================================================
 *
 */
package org.sbml.jsbml.util;

import java.io.Serializable;

/**
 * A pair of two values with type parameters. This data object is useful
 * whenever exactly two values are required for a specific task.
 * 
 * @author Andreas Dr&auml;ger
 * @date 2010-09-01
 */
public class ValuePair<L extends Comparable<L>, V extends Comparable<V>>
		implements Cloneable, Comparable<ValuePair<L, V>>, Serializable {

	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = -4230267902609475128L;
	/**
	 * 
	 */
	private L l;
	/**
	 * 
	 */
	private V v;

	/**
	 * Creates a new {@link ValuePair} with both attributes set to null.
	 */
	public ValuePair() {
		this(null, null);
	}

	/**
	 * 
	 * @param l
	 * @param v
	 */
	public ValuePair(L l, V v) {
		this.setL(l);
		this.setV(v);
	}

	/**
	 * 
	 * @param valuePair
	 */
	public ValuePair(ValuePair<L, V> valuePair) {
		this.l = valuePair.getL();
		this.v = valuePair.getV();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ValuePair<L, V> clone() {
		return new ValuePair<L, V>(this);
	}

	/**
	 * Convenient method to compare two values to this {@link ValuePair}.
	 * 
	 * @param l
	 * @param v
	 * @return a negative integer, zero, or a positive integer as this
	 *         {@link ValuePair} is less than, equal to, or greater than the
	 *         combination of the two given values.
	 * @see #compareTo(ValuePair)
	 */
	public int compareTo(L l, V v) {
		return compareTo(new ValuePair<L, V>(l, v));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ValuePair<L, V> v) {
		if (equals(v)) {
			return 0;
		}
		if (!isSetL()) {
			return Integer.MIN_VALUE;
		}
		if (!v.isSetL()) {
			return Integer.MAX_VALUE;
		}
		int comp = getL().compareTo(v.getL());
		if (comp == 0) {
			if (!isSetV()) {
				return -1;
			}
			if (!v.isSetV()) {
				return 1;
			}
			return getV().compareTo(v.getV());
		}
		return comp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (o instanceof ValuePair) {
			try {
				ValuePair<L, V> v = (ValuePair<L, V>) o;
				boolean equal = true;
				equal &= isSetL() == v.isSetL();
				equal &= isSetV() == v.isSetV();
				if (equal && isSetL() && isSetV()) {
					equal &= v.getL().equals(getL()) && v.getV().equals(getV());
				}
				return equal;
			} catch (ClassCastException exc) {
				return false;
			}
		}
		return false;
	}

	/**
	 * @return the a
	 */
	public L getL() {
		return l;
	}

	/**
	 * @return the b
	 */
	public V getV() {
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return l.hashCode() + v.hashCode();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSetL() {
		return l != null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSetV() {
		return v != null;
	}

	/**
	 * @param l
	 *            the l to set
	 */
	public void setL(L l) {
		this.l = l;
	}

	/**
	 * @param v
	 *            the v to set
	 */
	public void setV(V v) {
		this.v = v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("[%s, %s]", getL().toString(), getV().toString());
	}
}
