/*
 * $Id$
 * $URL$
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 * Copyright (C) 2009-2012 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.ext.render;

/**
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 * @since 1.0
 * @date 16.05.2012
 */
public interface FontRenderStyle {

  /**
   * @return the value of fontFamily
   */
  public abstract FontFamily getFontFamily();


  /**
   * @return whether fontFamily is set 
   */
  public abstract boolean isSetFontFamily();


  /**
   * Set the value of fontFamily
   */
  public abstract void setFontFamily(FontFamily fontFamily);


  /**
   * Unsets the variable fontFamily 
   * @return <code>true</code>, if fontFamily was set before, 
   *         otherwise <code>false</code>
   */
  public abstract boolean unsetFontFamily();


  /**
   * @return the value of fontSize
   */
  public abstract short getFontSize();


  /**
   * @return whether fontSize is set 
   */
  public abstract boolean isSetFontSize();


  /**
   * Set the value of fontSize
   */
  public abstract void setFontSize(short fontSize);


  /**
   * Unsets the variable fontSize 
   * @return <code>true</code>, if fontSize was set before, 
   *         otherwise <code>false</code>
   */
  public abstract boolean unsetFontSize();


  /**
   * @return the value of fontWeightBold
   */
  public abstract boolean isFontWeightBold();


  /**
   * @return whether fontWeightBold is set 
   */
  public abstract boolean isSetFontWeightBold();


  /**
   * Set the value of fontWeightBold
   */
  public abstract void setFontWeightBold(boolean fontWeightBold);


  /**
   * Unsets the variable fontWeightBold 
   * @return <code>true</code>, if fontWeightBold was set before, 
   *         otherwise <code>false</code>
   */
  public abstract boolean unsetFontWeightBold();


  /**
   * @return the value of fontStyleItalic
   */
  public abstract boolean isFontStyleItalic();


  /**
   * @return whether fontStyleItalic is set 
   */
  public abstract boolean isSetFontStyleItalic();


  /**
   * Set the value of fontStyleItalic
   */
  public abstract void setFontStyleItalic(boolean fontStyleItalic);


  /**
   * Unsets the variable fontStyleItalic 
   * @return <code>true</code>, if fontStyleItalic was set before, 
   *         otherwise <code>false</code>
   */
  public abstract boolean unsetFontStyleItalic();


  /**
   * @return the value of textAnchor
   */
  public abstract TextAnchor getTextAnchor();


  /**
   * @return whether textAnchor is set 
   */
  public abstract boolean isSetTextAnchor();


  /**
   * Set the value of textAnchor
   */
  public abstract void setTextAnchor(TextAnchor textAnchor);


  /**
   * Unsets the variable textAnchor 
   * @return <code>true</code>, if textAnchor was set before, 
   *         otherwise <code>false</code>
   */
  public abstract boolean unsetTextAnchor();


  /**
   * @return the value of VTextAnchor
   */
  public abstract VTextAnchor getVTextAnchor();


  /**
   * @return whether VTextAnchor is set 
   */
  public abstract boolean isSetVTextAnchor();


  /**
   * Set the value of VTextAnchor
   */
  public abstract void setVTextAnchor(VTextAnchor vTextAnchor);


  /**
   * Unsets the variable VTextAnchor 
   * @return <code>true</code>, if VTextAnchor was set before, 
   *         otherwise <code>false</code>
   */
  public abstract boolean unsetVTextAnchor();

}
