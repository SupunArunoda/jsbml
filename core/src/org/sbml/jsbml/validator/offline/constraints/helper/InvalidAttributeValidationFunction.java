/*
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2017 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 * 4. The University of California, San Diego, La Jolla, CA, USA
 * 5. The Babraham Institute, Cambridge, UK
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.validator.offline.constraints.helper;

import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.util.TreeNodeWithChangeSupport;
import org.sbml.jsbml.validator.offline.ValidationContext;
import org.sbml.jsbml.validator.offline.constraints.ValidationFunction;
import org.sbml.jsbml.xml.XMLNode;


/**
 * Class used to check if any invalid XML attributes where found.
 * 
 * @author rodrigue
 * @since 1.3
 */
public class InvalidAttributeValidationFunction<T extends TreeNodeWithChangeSupport> implements ValidationFunction<T> {

  /**
   * the attribute name to check
   */
  private String attributeName;
  
  /**
   * Creates a new {@link InvalidAttributeValidationFunction} instance.
   * 
   * @param attributeName the attribute name to check
   */
  public InvalidAttributeValidationFunction(String attributeName) {
    this.attributeName = attributeName;
  }
  
  @Override
  public boolean check(ValidationContext ctx, T t) {

    if (t.isSetUserObjects() && t.getUserObject(JSBML.INVALID_XML) != null)
    {
      XMLNode invalidNode = (XMLNode) t.getUserObject(JSBML.INVALID_XML);

      // System.out.println("InvalidAttributeValidationFunction - attributes.length = " + invalidNode.getAttributesLength());

      if (invalidNode.getAttrIndex(attributeName) >= 0) {
        // the attribute is found in the invalid XMLNode
        return false;
      }
    }

    return true;
  }
}
