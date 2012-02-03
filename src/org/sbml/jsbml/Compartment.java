/*
 * $Id$
 * $URL$
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2011 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */

package org.sbml.jsbml;

import java.util.Locale;
import java.util.Map;

import org.sbml.jsbml.util.StringTools;
import org.sbml.jsbml.util.TreeNodeChangeEvent;

/**
 * Represents the compartment in a model, i.e., a variable element with name,
 * identifier, unit, and value that may change during a simulation. This is the
 * container for reacting species.
 * 
 * @author Andreas Dr&auml;ger
 * @author Nicolas Rodriguez
 * @author Marine Dumousseau
 * @since 0.8
 * @version $Rev$
 */
public class Compartment extends Symbol {

	/**
	 * This message will be displayed if the user tries to set the spatial
	 * dimensions of this element to a value other than 0, 1, 2, or 3.
	 */
	private static final String ERROR_MESSAGE_INVALID_DIM = "Spatial dimensions must be within {0, 3}, but %f was given.";
	/**
	 * This is the error message to be displayed if an application tries to set
	 * units or size attribute for this compartment but the spatial dimensions
	 * have been set to zero.
	 */
	private static final String ERROR_MESSAGE_ZERO_DIM = "Cannot set %s for compartment %s if the spatial dimensions are zero.";
	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = -1117854029388326636L;
	/**
	 * Represents the compartmentType XML attribute of a compartment element. It
	 * matches a compartmentType id in the model.
	 */
	@Deprecated
	private String compartmentTypeID;
	/**
	 * Helper variable to check if spatial dimensions has been set by the user.
	 */
	private boolean isSetSpatialDimensions = false;
	/**
	 * Represents the outside XML attribute of a compartment element. It matches
	 * a compartment id in the model instance.
	 */
	@Deprecated
	private String outsideID;

	/**
	 * Represents the spatialDimensions XML attribute of a compartment element.
	 */
	private Double spatialDimensions;

	/**
	 * Creates a Compartment instance. By default, if the level is set and is
	 * superior or equal to 3, sets the compartmentType, outsideID and
	 * spatialDimension to null.
	 */
	public Compartment() {
		super();
		initDefaults();
	}

	/**
	 * Creates a Compartment instance from a given compartment.
	 * 
	 * @param compartment the compartment object to clone.
	 */
	public Compartment(Compartment compartment) {
		super(compartment);

		if (compartment.isSetCompartmentType()) {
			this.compartmentTypeID = new String(compartment
					.getCompartmentType());
		} else {
			compartmentTypeID = compartment.compartmentTypeID == null ? null : new String(compartment.compartmentTypeID);
		}
		
		if (compartment.isSetSpatialDimensions()) {
			setSpatialDimensions(Double.valueOf(compartment
					.getSpatialDimensions()));
		} else {
			spatialDimensions = compartment.spatialDimensions == null ? null : new Double(compartment.spatialDimensions);
		}
		
		if (compartment.isSetOutside()) {
			setOutside(new String(compartment.getOutside()));
		} else {
			outsideID = compartment.outsideID == null ? null : new String(compartment.outsideID);
		}
		
		// size is set in the super clone constructor
	}

	/**
	 * Creates a Compartment instance from a level and version. By default, if
	 * the level is set and is superior or equal to 3, sets the compartmentType,
	 * outsideID and spatialDimension to null.
	 * 
	 * @param level the SBML level
	 * @param version  the SBML level
	 */
	public Compartment(int level, int version) {
		this(null, null, level, version);
	}
	
	/**
	 * Creates a Compartment instance with the given id. 
	 * 
	 * @param id the compartment id.
	 */
	public Compartment(String id) {
		this();
		setId(id);
	}

	/**
	 * Creates a Compartment instance from an id, level and version. By default,
	 * sets the compartmentType, outsideID and spatialDimension to null.
	 * 
	 * @param id  the compartment id.
	 * @param level the SBML level
	 * @param version  the SBML level
	 */
	public Compartment(String id, int level, int version) {
		this(id, null, level, version);
	}

	/**
	 * Creates a Compartment instance from an id, name, level and version. By
	 * default, if the level is set and is superior or equal to 3, sets the
	 * compartmentType, outsideID and spatialDimension to null.
	 * 
	 * @param id  the compartment id.
	 * @param name the compartment name.
	 * @param level the SBML level
	 * @param version  the SBML level
	 */
	public Compartment(String id, String name, int level, int version) {
		super(id, name, level, version);
		initDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.Symbol#clone()
	 */
	@Override
	public Compartment clone() {
		return new Compartment(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sbml.jsbml.Symbol#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		boolean equals = super.equals(object);
		if (equals) {
			Compartment c = (Compartment) object;
			equals &= c.isSetOutside() == isSetOutside();
			if (equals && isSetOutside()) {
				equals &= c.getOutside().equals(getOutside());
			}
			equals &= c.isSetCompartmentType() == isSetCompartmentType();
			if (equals && isSetCompartmentType()) {
				equals &= c.getCompartmentType().equals(getCompartmentType());
			}
			// already checked by super class:
			// equals &= c.getSize() == getSize();
			equals &= Double.valueOf(c.getSpatialDimensions()).equals(getSpatialDimensions());
		}
		return equals;
	}
	
	/**
	 * Returns the compartmentType id of this compartment. Return an empty
	 * String if it is not set.
	 * 
	 * @return the compartmentType id of this compartment. Return an empty
	 *         String if it is not set.
	 * @deprecated Only defined in SBML Level 2 Versions 2 through 4.
	 */
	@Deprecated
	public String getCompartmentType() {
		return isSetCompartmentType() ? this.compartmentTypeID : "";
	}

	/**
	 * Returns the compartmentType instance in Model for this compartment
	 *         compartmentTypeID. Return null if there is no compartmentType
	 *         instance which matches this compartmentTypeID.
	 *         
	 * @return the compartmentType instance in Model for this compartment
	 *         compartmentTypeID. Return null if there is no compartmentType
	 *         instance which matches this compartmentTypeID.
	 */
	@Deprecated
	public CompartmentType getCompartmentTypeInstance() {
		Model m = getModel();
		return m != null ? m.getCompartmentType(this.compartmentTypeID) : null;
	}

	/**
	 * Returns the outside id of this compartment. Return "" if it is not set.
	 * 
	 * @return the outside id of this compartment. Return "" if it is not set.
	 */
	public String getOutside() {
		return isSetOutside() ? outsideID : "";
	}

	/**
	 * Returns the compartment instance which matches the outside id in Model.
	 *         Return null if no compartment instance matches the outside id.
	 * 
	 * @return the compartment instance which matches the outside id in Model.
	 *         Return null if no compartment instance matches the outside id.
	 */
	public Compartment getOutsideInstance() {
		Model m = getModel();
		return m != null ? m.getCompartment(outsideID) : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sbml.jsbml.AbstractNamedSBaseWithUnit#getPredefinedUnitID()
	 */
	public String getPredefinedUnitID() {
		if (getLevel() < 3) {
			if (getLevel() < 2) {
				return "volume";
			}
			switch ((short) getSpatialDimensions()) {
			case 3:
				return "volume";
			case 2:
				return "area";
			case 1:
				return "length";
			default:
				break;
			}
		}
		return null;
	}

	/**
	 * Returns the size of this compartment.
	 * 
	 * @return the size of this compartment.
	 */
	public double getSize() {
		return getValue();
	}

	/**
	 * Returns the {@link #spatialDimensions} of this {@link Compartment}. If it
	 * is not set and the level of this {@link Compartment} is either one or two
	 * it returns the default SBML Level 2 value, which is 3. In all other
	 * cases, {@link Double#NaN} will be returned.
	 * 
	 * @return the {@link #spatialDimensions} of this {@link Compartment} or 3
	 *         if {@link #spatialDimensions} is not set and level is 1 or 2.
	 */
	public double getSpatialDimensions() {
		if (isSetSpatialDimensions() && (spatialDimensions != null)) {
			return spatialDimensions.doubleValue();
		}
		if (getLevel() < 3) {
			// Although in Level 1, no spatial dimensions are defined, but
			// these are implicitly 3 due to the volume attribute.
			return 3d;
		}
		return Double.NaN;
	}

	/**
	 * 
	 * @return
	 * @deprecated use {@link #getSpatialDimensions()}
	 */
	@Deprecated
	public double getSpatialDimensionsAsDouble() {
		return getSpatialDimensions();
	}
	
	/**
	 * Gets the volume of this Compartment
	 * 
	 * This method is identical to getSize(). In SBML Level 1, compartments are
	 * always three-dimensional constructs and only have volumes, whereas in
	 * SBML Level 2, compartments may be other than three-dimensional and
	 * therefore the 'volume' attribute is named 'size' in Level 2. LibSBML
	 * provides both getSize() and getVolume() for easier compatibility between
	 * SBML Levels.
	 * 
	 * @return the volume of this {@link Compartment}
	 * @deprecated The volume attribute is only defined in SBML Level 1. Please
	 *             use {@link #getSize()}.
	 */
	@Deprecated
	public double getVolume() {
		return getSize();
	}

	/*
	 * (non-Javadoc)
	 * @see org.sbml.jsbml.Symbol#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 991;
		int hashCode = super.hashCode();
		if (isSetOutside()) {
			hashCode += prime * getOutside().hashCode();
		}
		if (isSetCompartmentType()) {
			hashCode += prime * getCompartmentType().hashCode();
		}
		if (isSetSpatialDimensions()) {
			hashCode += prime * spatialDimensions.hashCode();
		}
		return hashCode;
	}

	/**
	 * Initializes the default values using the current Level/Version configuration.
	 */
	public void initDefaults() {
		initDefaults(getLevel(), getVersion());
	}
	
	/**
	 * Initializes the default values using the current Level/Version configuration.
	 * 
	 * @param level
	 * @param version
	 */
	public void initDefaults(int level, int version) {
		compartmentTypeID = null;
		outsideID = null;
		unitsID = null;
		if (level < 3) {
			spatialDimensions = Double.valueOf(3d);
			if (level >= 2) {
				  constant = new Boolean(true);
			}
			else {
				 constant = null;
			}
		} else {
			spatialDimensions = null;
			constant = null;
		}

		if (level == 1) {
			value = Double.valueOf(1d);
		}
	}

	/**
	 * Returns true if the compartmentID of this compartment is not null.
	 * 
	 * @return true if the compartmentID of this compartment is not null.
	 */
	public boolean isSetCompartmentType() {
		return compartmentTypeID != null;
	}

	/**
	 * Returns true if the compartmentType instance which matches the
	 *         compartmentTypeID of this compartment is not null.
	 *         
	 * @return true if the compartmentType instance which matches the
	 *         compartmentTypeID of this compartment is not null.
	 */
	@Deprecated
	public boolean isSetCompartmentTypeInstance() {
		Model m = getModel();
		return m != null ? m.getCompartmentType(this.compartmentTypeID) != null
				: false;
	}

	/**
	 * Returns true if the outsideID of this compartment is not null.
	 * 
	 * @return true if the outsideID of this compartment is not null.
	 */
	public boolean isSetOutside() {
		return outsideID != null;
	}

	/**
	 * Returns true if the compartment instance which matches the outsideID of
	 *         this compartment is not null.
	 * 
	 * @return true if the compartment instance which matches the outsideID of
	 *         this compartment is not null.
	 */
	public boolean isSetOutsideInstance() {
		Model m = getModel();
		return m != null ? m.getCompartment(this.outsideID) != null : false;
	}

	/**
	 * Returns true if the size of this compartment has been set by a user.
	 * 
	 * @return true if the size of this compartment has been set by a user.
	 */
	public boolean isSetSize() {
		return isSetValue();
	}

	/**
	 * Returns true if the spatialDimensions of this compartment has been set by a user.
	 * 
	 * @return true if the spatialDimensions of this compartment has been set by a user.
	 */
	public boolean isSetSpatialDimensions() {
		return isSetSpatialDimensions;
	}

	/**
	 * <p>
	 * Returns true or false depending on whether this Compartment's 'volume'
	 * attribute has been set.
	 * </p>
	 * <p>
	 * Some words of explanation about the set/unset/isSet methods: SBML Levels
	 * 1 and 2 define certain attributes on some classes of objects as optional.
	 * This requires an application to be careful about the distinction between
	 * two cases: (1) a given attribute has never been set to a value, and
	 * therefore should be assumed to have the SBML-defined default value, and
	 * (2) a given attribute has been set to a value, but the value happens to
	 * be an empty string. LibSBML supports these distinctions by providing
	 * methods to set, unset, and query the status of attributes that are
	 * optional. The methods have names of the form setAttribute(...),
	 * unsetAttribute(), and isSetAttribute(), where Attribute is the the name
	 * of the optional attribute in question.
	 * </p>
	 * <p>
	 * This method is similar but not identical to {@link #isSetSize()}. The
	 * latter should not be used in the context of SBML Level 1 models because
	 * this method (isSetVolume()) performs extra processing to take into
	 * account the difference in default values between SBML Levels 1 and 2.
	 * </p>
	 * 
	 * @return true if the 'volume' attribute ('size' in L2) of this Compartment
	 *         has been set, false otherwise.
	 * @see #isSetSize()
	 * @jsbml.note In SBML Level 1, a compartment's volume has a default value (
	 *             1.0) and therefore this method will always return true. In
	 *             Level 2, a compartment's size (the equivalent of SBML Level
	 *             1's 'volume') is optional and has no default value, and
	 *             therefore may or may not be set.
	 * @deprecated The volume attribute is only defined in SBML Level 1. Please
	 *             use {@link #isSetSize()}
	 */
	@Deprecated
	public boolean isSetVolume() {
		return isSetSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.element.SBase#readAttribute(String attributeName,
	 * String prefix, String value)
	 */
	@Override
	public boolean readAttribute(String attributeName, String prefix,
			String value) {
		boolean isAttributeRead = super.readAttribute(attributeName, prefix,
				value);
		if (!isAttributeRead) {
			isAttributeRead = true;
			if (attributeName.equals("spatialDimensions")) {
				setSpatialDimensions(StringTools.parseSBMLDouble(value));
			} else if (attributeName.equals("units")) {
				setUnits(value);
			} else if (attributeName.equals("size")) {
				setSize(StringTools.parseSBMLDouble(value));
			} else if (attributeName.equals("volume")) {
				setVolume(StringTools.parseSBMLDouble(value));
			} else if (attributeName.equals("compartmentType")) {
				setCompartmentType(value);
			} else if (attributeName.equals("outside")) {
				setOutside(value);
			} else if (attributeName.equals("constant")) {
				setConstant(StringTools.parseSBMLBoolean(value));
			} else {
				isAttributeRead = false;
			}
		}
		
		return isAttributeRead;
	}

	/**
	 * Sets the compartmentTypeID value of this compartment to the id of
	 * 'compartmentType'
	 * 
	 * @param compartmentType
	 * @sbml.deprecated Level 3 Version 1
	 */
	@Deprecated
	public void setCompartmentType(CompartmentType compartmentType) {
		setCompartmentType(compartmentType != null ? compartmentType
				.getId() : null);
	}

	/**
	 * Sets the compartmentTypeID of this compartment to 'compartmentTypeID'
	 * 
	 * @param compartmentTypeID
	 * @deprecated Only valid in Level 2.
	 * @throws PropertyNotAvailableException if Level is not 2.
	 */
	@Deprecated
	public void setCompartmentType(String compartmentTypeID) {
		if (getLevel() != 2) {
			throw new PropertyNotAvailableException(TreeNodeChangeEvent.compartmentType,
					this);
		}
		String oldCompartmentTypeID = this.compartmentTypeID;
		this.compartmentTypeID = compartmentTypeID;
		firePropertyChange(TreeNodeChangeEvent.compartmentType,
				oldCompartmentTypeID, this.compartmentTypeID);
	}

	/**
	 * Sets the outsideID of this compartment to the id of 'outside'.
	 * 
	 * @param outside
	 * @sbml.deprecated Level 3 Version 1
	 */
	@Deprecated
	public void setOutside(Compartment outside) {
		setOutside(outside != null ? outside.getId() : null);
	}

	/**
	 * Sets the outsideID of this compartment to 'outside'.
	 * 
	 * @param outside
	 * @deprecated since Level 3 Version 1
	 * @throws PropertyNotAvailableException if Level greater than 2.
	 */
	@Deprecated
	public void setOutside(String outside) {
		if (getLevel() > 2) {
			throw new PropertyNotAvailableException(TreeNodeChangeEvent.outside, this);
		}
		String oldOutside = outsideID;
		if ((outside != null) && (outside.trim().length() == 0)) {
			this.outsideID = null;
		} else {
			this.outsideID = outside;
		}
		firePropertyChange(TreeNodeChangeEvent.outside, oldOutside, this.outsideID);
	}

	/**
	 * Sets the size of this compartment to 'size'.
	 * 
	 * @param size
	 * @throws PropertyNotAvailableException
	 *             in case of Level < 2.
	 */
	public void setSize(double size) {
		if (getLevel() < 2) {
			throw new PropertyNotAvailableException(TreeNodeChangeEvent.size, this);
		}
		setValue(size);
	}

	/**
	 * Sets the spatialDimensions of this compartment to 'i'.
	 * 
	 * @param spatialDimension
	 * @throws IllegalArgumentException
	 *             if spatialDimension < 0 or if spatialDimension > 3
	 * @throws PropertyNotAvailableException if Level < 2.
	 */
	public void setSpatialDimensions(double spatialDimension) {
		if (getLevel() < 2) {
			throw new PropertyNotAvailableException(
					TreeNodeChangeEvent.spatialDimensions, this);
		}
		if (((0d <= spatialDimension) && (spatialDimension <= 3d)  
				&& (((int) spatialDimension) - spatialDimension == 0d))
				|| (getLevel() > 2)) 
		{
			isSetSpatialDimensions = true;
			Double oldSpatialDimensions = this.spatialDimensions;
			this.spatialDimensions = Double.valueOf(spatialDimension);
			firePropertyChange(TreeNodeChangeEvent.spatialDimensions,
					oldSpatialDimensions, this.spatialDimensions);
		} else {
			throw new IllegalArgumentException(String.format(
					ERROR_MESSAGE_INVALID_DIM, spatialDimension));
		}
	}
	
	/**
	 * 
	 * @param spatialDimensions
	 */
	public void setSpatialDimensions(int spatialDimensions) {
		setSpatialDimensions((double) spatialDimensions);
	}

	/**
	 * Sets the spatialDimensions of this compartment to 'spatialDimensiosn'.
	 * 
	 * @param spatialDimensions
	 * @throws IllegalArgumentException
	 *             if spatialDimension < 0 or if spatialDimension > 3
	 */
	public void setSpatialDimensions(short spatialDimensions) {
		setSpatialDimensions((int) spatialDimensions);
	}

	/**
	 * Sets the unitsID of this {@link QuantityWithUnit}. Only valid unit
	 * kind names or identifiers of already existing {@link UnitDefinition}s are
	 * allowed arguments of this function.
	 * 
	 * @param units
	 *            the identifier of an already existing {@link UnitDefinition}
	 *            or an {@link Unit.Kind} identifier for the current
	 *            level/version combination of this unit. Passing a null value
	 *            to this method is equivalent to calling {@link #unsetUnits()}.
	 * 
	 * @throws IllegalArgumentException
	 *             if the unit is not valid or if spatialDimensions = 0.
	 */
	@Override
	public void setUnits(String units) {
		if ((units != null) && (units.trim().length() == 0)) {
			unsetUnits();
			return;
		}
		double specDim = getSpatialDimensions();
		if (Double.isNaN(specDim) || (0 < specDim)) {
			super.setUnits(units);
		} else {
			throw new IllegalArgumentException(String.format(
					ERROR_MESSAGE_ZERO_DIM, "units", getId()));
		}
	}

	/**
	 * Sets the {@link Unit} of this {@link Compartment}.
	 * 
	 * @param unit
	 * 
	 * @throws IllegalArgumentException
	 *             if spatialDimensions = 0.
	 */
	@Override
	public void setUnits(Unit unit) {
		if (0 < getSpatialDimensions()) {
			super.setUnits(unit);
		} else {
			throw new IllegalArgumentException(String.format(
					ERROR_MESSAGE_ZERO_DIM, "unit", getId()));
		}
	}

	/**
	 * Sets the unit of this Compartment.
	 * 
	 * A new Unit object will be created base on this kind.
	 * 
	 * @param unitKind
	 * 
	 * @throws IllegalArgumentException
	 *             if spatialDimensions = 0.
	 * 
	 */
	@Override
	public void setUnits(Unit.Kind unitKind) {
		if (0 < getSpatialDimensions()) {
			super.setUnits(unitKind);
		} else {
			throw new IllegalArgumentException(String.format(
					ERROR_MESSAGE_ZERO_DIM, "unit kind", getId()));
		}
	}

	/**
	 * Set the unit attribute of this Compartment to the given unit definition.
	 * 
	 * @param unitDefinition
	 * 
	 * @throws IllegalArgumentException
	 *             if spatialDimensions <= 0.
	 * 
	 */
	@Override
	public void setUnits(UnitDefinition unitDefinition) {
		/*
		 * No test for spatial dimensions is necessary here because the super
		 * method will finally refer to the other setUnits method in this class
		 * in which we will check the spatial dimensions.
		 */
		super.setUnits(unitDefinition);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sbml.jsbml.QuantityWithUnit#setValue(double)
	 */
	@Override
	public void setValue(double value) {
		double dim = getSpatialDimensions();
		if ((dim > 0d) || Double.isNaN(dim)) {
			super.setValue(value);
		} else {
			throw new IllegalArgumentException(String.format(
					ERROR_MESSAGE_ZERO_DIM, "size", getId()));
		}
	}

	/**
	 * <p>
	 * Sets the 'volume' attribute (or 'size' in SBML Level 2) of this
	 * Compartment.
	 * </p>
	 * <p>
	 * Some words of explanation about the set/unset/isSet methods: SBML Levels
	 * 1 and 2 define certain attributes on some classes of objects as optional.
	 * This requires an application to be careful about the distinction between
	 * two cases: (1) a given attribute has never been set to a value, and
	 * therefore should be assumed to have the SBML-defined default value, and
	 * (2) a given attribute has been set to a value, but the value happens to
	 * be an empty string. LibSBML supports these distinctions by providing
	 * methods to set, unset, and query the status of attributes that are
	 * optional. The methods have names of the form setAttribute(...),
	 * unsetAttribute(), and isSetAttribute(), where Attribute is the the name
	 * of the optional attribute in question.
	 * </p>
	 * <p>
	 * This method is identical to {@link #setSize(double)} and is provided for
	 * compatibility between SBML Level 1 and Level 2.
	 * </p>
	 * 
	 * @param value
	 *            a double representing the volume of this compartment instance
	 *            in whatever units are in effect for the compartment.
	 * @deprecated This method is only available for SBML Level 1. You should
	 *             either use {@link #setSize(double)} or
	 *             {@link #setValue(double)}.
	 * @throws PropertyNotAvailableException if Level is not 1.
	 */
	@Deprecated
	public void setVolume(double value) {
		if (getLevel() != 1) {
			throw new PropertyNotAvailableException(TreeNodeChangeEvent.volume, this);
		}
		setValue(value);
	}

	/**
	 * Sets the compartmentTypeID of this {@link Compartment} to null.
	 */
	public void unsetCompartmentType() {
		setCompartmentType((String) null);
	}

	/**
	 * Sets the outsideID of this compartment to null.
	 * 
	 * @deprecated since Level 3 Version 1
	 */
	@Deprecated
	public void unsetOutside() {
		setOutside((String) null);
	}

	/**
	 * <p>
	 * Unsets the value of the 'size' attribute of this Compartment.
	 * </p>
	 * <p>
	 * Some words of explanation about the set/unset/isSet methods: SBML Levels
	 * 1 and 2 define certain attributes on some classes of objects as optional.
	 * This requires an application to be careful about the distinction between
	 * two cases: (1) a given attribute has never been set to a value, and
	 * therefore should be assumed to have the SBML-defined default value, and
	 * (2) a given attribute has been set to a value, but the value happens to
	 * be an empty string. LibSBML supports these distinctions by providing
	 * methods to set, unset, and query the status of attributes that are
	 * optional. The methods have names of the form setAttribute(...),
	 * unsetAttribute(), and isSetAttribute(), where Attribute is the the name
	 * of the optional attribute in question.
	 * </p>
	 */
	public void unsetSize() {
		unsetValue();
	}

	/**
	 * Sets the spatialDimensions of this compartment to null.
	 */
	public void unsetSpatialDimensions() {
		Double oldSpatialDim = this.spatialDimensions;
		this.spatialDimensions = null;
		isSetSpatialDimensions = false;
		firePropertyChange(TreeNodeChangeEvent.spatialDimensions, oldSpatialDim,
				this.spatialDimensions);
	}

	/**
	 * <p>
	 * Unsets the value of the 'volume' attribute of this Compartment.
	 * </p>
	 * <p>
	 * Some words of explanation about the set/unset/isSet methods: SBML Levels
	 * 1 and 2 define certain attributes on some classes of objects as optional.
	 * This requires an application to be careful about the distinction between
	 * two cases: (1) a given attribute has never been set to a value, and
	 * therefore should be assumed to have the SBML-defined default value, and
	 * (2) a given attribute has been set to a value, but the value happens to
	 * be an empty string. LibSBML supports these distinctions by providing
	 * methods to set, unset, and query the status of attributes that are
	 * optional. The methods have names of the form setAttribute(...),
	 * unsetAttribute(), and isSetAttribute(), where Attribute is the the name
	 * of the optional attribute in question.
	 * </p>
	 * <p>
	 * In SBML Level 1, a Compartment volume has a default value (1.0) and
	 * therefore should always be set. In Level 2, 'size' is optional with no
	 * default value and as such may or may not be set.
	 * </p>
	 * 
	 * @deprecated The volume attribute is only defined in SBML Level 1. Please
	 *             use {@link #unsetSize()}
	 */
	@Deprecated
	public void unsetVolume() {
		unsetSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.element.SBase#writeXMLAttributes()
	 */
	@Override
	public Map<String, String> writeXMLAttributes() {
	  Map<String, String> attributes = super.writeXMLAttributes();
		int level = getLevel();
		Locale en = Locale.ENGLISH;
		if (level == 1) {
			if (isSetVolume()) {
				attributes.put("volume", StringTools.toString(en, getVolume()));
			}
		} else if (1 < level) {
			if (isSetSpatialDimensions()) {
				attributes.put("spatialDimensions", level < 3 ? Short
						.toString((short) getSpatialDimensions()) : StringTools
						.toString(en, getSpatialDimensions()));
				if ((level < 3)
						&& (((short) getSpatialDimensions())
								- getSpatialDimensions() != 0d)) {
					// TODO: Throw Exception?
					System.err.printf(
							"Illegal non-integer spatial dimensions %d.\n",
							getSpatialDimensions());
				}
			}
			if (isSetSize()) {
				attributes.put("size", StringTools.toString(en, getSize()));
			}
			if (isSetConstant()) {
				attributes.put("constant", Boolean.toString(getConstant()));
			}
			if (isSetUnits()) {
				attributes.put("units", getUnits());
			}
		}
		if (level == 2) {
			if (isSetCompartmentType()) {
				attributes.put("compartmentType", getCompartmentType());
			}
		}
		if (level < 3) {
			if (isSetOutside()) {
				attributes.put("outside", outsideID);
			}
		}
		return attributes;
	}

}