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

/**
 * An error that indicates that a property of an {@link SBase} is
 * not available for the current SBML Level/Version combination.
 * 
 * @author Andreas Dr&auml;ger
 * @date 2010-11-21
 */
public class PropertyNotAvailableError extends SBMLError {

	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = 3030431702957624218L;

	/**
	 * Message to indicate that a certain property cannot be set for the current level/version combination.
	 */
	public static final String PROPERTY_UNDEFINED_EXCEPTION_MSG = "Property %s is not defined for Level %s and Version %s";
	
	/**
	 * Creates an error message pointing out that the property of the given name is not defined
	 * in the Level/Version combination of the given {@link SBase}.
	 * 
	 * @param property
	 * @param sbase
	 * @return
	 */
	public static String propertyUndefinedMessage(String property, SBase sbase) {
		return String.format(PROPERTY_UNDEFINED_EXCEPTION_MSG, property,
				Integer.valueOf(sbase.getLevel()), Integer.valueOf(sbase.getVersion()));
	}
	
	/**
	 * 
	 * @param property
	 * @param sbase
	 */
	public PropertyNotAvailableError(String property, SBase sbase) {
		super(propertyUndefinedMessage(property, sbase));
	}
	
	/* (non-Javadoc)
	 * @see org.sbml.jsbml.SBMLError#toString()
	 */
	public String toString() {
		return getMessage();
	}
}
