/*
 * $Id$
 * $URL$
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2014 jointly by the following organizations:
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
package org.sbml.jsbml.ext.render;

import java.util.Arrays;
import java.util.Map;

import org.sbml.jsbml.PropertyUndefinedError;
import org.sbml.jsbml.util.StringTools;

/**
 * @author Eugen Netz
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Jan Rudolph
 * @version $Rev$
 * @since 1.0
 * @date 08.05.2012
 */
public class GraphicalPrimitive1D extends Transformation2D {
  /**
   * Generated serial version identifier
   */
  private static final long serialVersionUID = 3705246334810811216L;
  protected String stroke;
  protected Short[] strokeDashArray;
  protected Double strokeWidth;


  /**
   * @return the value of strokeDashArray
   */
  public Short[] getStrokeDashArray() {
    if (isSetStrokeDashArray()) {
      return strokeDashArray;
    }
    // This is necessary if we cannot return null here.
    throw new PropertyUndefinedError(RenderConstants.strokeDashArray, this);
  }


  /**
   * @return whether strokeDashArray is set
   */
  public boolean isSetStrokeDashArray() {
    return strokeDashArray != null;
  }


  /**
   * Set the value of strokeDashArray
   */
  public void setStrokeDashArray(Short[] strokeDashArray) {
    Short[] oldStrokeDashArray = this.strokeDashArray;
    this.strokeDashArray = strokeDashArray;
    firePropertyChange(RenderConstants.strokeDashArray, oldStrokeDashArray, this.strokeDashArray);
  }


  /**
   * Unsets the variable strokeDashArray
   * @return {@code true}, if strokeDashArray was set before,
   *         otherwise {@code false}
   */
  public boolean unsetStrokeDashArray() {
    if (isSetStrokeDashArray()) {
      Short[] oldStrokeDashArray = strokeDashArray;
      strokeDashArray = null;
      firePropertyChange(RenderConstants.strokeDashArray, oldStrokeDashArray, strokeDashArray);
      return true;
    }
    return false;
  }

  /**
   * Creates an GraphicalPrimitive1D instance
   */
  public GraphicalPrimitive1D() {
    super();
    initDefaults();
  }
    
  /**
   * Creates an GraphicalPrimitive1D instance
   * 
   * @param level the SBML level
   * @param version the SBML version
   */
  public GraphicalPrimitive1D(int level, int version) {
    super(level, version);
    initDefaults();
  }


  /**
   * Clone constructor
   */
  public GraphicalPrimitive1D(GraphicalPrimitive1D obj) {
    super();
    stroke = obj.stroke;
    strokeWidth = obj.strokeWidth;

    if (obj.isSetStrokeDashArray()) {
      setStrokeDashArray(obj.strokeDashArray.clone());
    }
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.ext.render.Transformation2D#clone()
   */
  @Override
  public GraphicalPrimitive1D clone() {
    return new GraphicalPrimitive1D(this);
  }



  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 3181;
    int result = super.hashCode();
    result = prime * result + ((stroke == null) ? 0 : stroke.hashCode());
    result = prime * result + Arrays.hashCode(strokeDashArray);
    result = prime * result
      + ((strokeWidth == null) ? 0 : strokeWidth.hashCode());
    return result;
  }


  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    GraphicalPrimitive1D other = (GraphicalPrimitive1D) obj;
    if (stroke == null) {
      if (other.stroke != null) {
        return false;
      }
    } else if (!stroke.equals(other.stroke)) {
      return false;
    }
    if (!Arrays.equals(strokeDashArray, other.strokeDashArray)) {
      return false;
    }
    if (strokeWidth == null) {
      if (other.strokeWidth != null) {
        return false;
      }
    } else if (!strokeWidth.equals(other.strokeWidth)) {
      return false;
    }
    return true;
  }

  /**
   * @return the value of stroke
   */
  public String getStroke() {
    if (isSetStroke()) {
      return stroke;
    }
    // This is necessary if we cannot return null here.
    throw new PropertyUndefinedError(RenderConstants.stroke, this);
  }

  /**
   * @return the value of strokeWidth
   */
  public Double getStrokeWidth() {
    if (isSetStrokeWidth()) {
      return strokeWidth;
    }
    // This is necessary if we cannot return null here.
    throw new PropertyUndefinedError(RenderConstants.strokeWidth, this);
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.ext.render.Transformation2D#initDefaults()
   */
  @Override
  public void initDefaults() {
    setNamespace(RenderConstants.namespaceURI);
  }

  /**
   * @return whether stroke is set
   */
  public boolean isSetStroke() {
    return stroke != null;
  }

  /**
   * @return whether strokeWidth is set
   */
  public boolean isSetStrokeWidth() {
    return strokeWidth != null;
  }
  /**
   * Set the value of stroke
   */
  public void setStroke(String stroke) {
    String oldStroke = this.stroke;
    this.stroke = stroke;
    firePropertyChange(RenderConstants.stroke, oldStroke, this.stroke);
  }

  /**
   * Set the value of strokeWidth
   */
  public void setStrokeWidth(Double strokeWidth) {
    Double oldStrokeWidth = this.strokeWidth;
    this.strokeWidth = strokeWidth;
    firePropertyChange(RenderConstants.strokeWidth, oldStrokeWidth, this.strokeWidth);
  }

  /**
   * Unsets the variable stroke
   * @return {@code true}, if stroke was set before,
   *         otherwise {@code false}
   */
  public boolean unsetStroke() {
    if (isSetStroke()) {
      String oldStroke = stroke;
      stroke = null;
      firePropertyChange(RenderConstants.stroke, oldStroke, stroke);
      return true;
    }
    return false;
  }

  /**
   * Unsets the variable strokeWidth
   * @return {@code true}, if strokeWidth was set before,
   *         otherwise {@code false}
   */
  public boolean unsetStrokeWidth() {
    if (isSetStrokeWidth()) {
      Double oldStrokeWidth = strokeWidth;
      strokeWidth = null;
      firePropertyChange(RenderConstants.strokeWidth, oldStrokeWidth, strokeWidth);
      return true;
    }
    return false;
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.AbstractSBase#writeXMLAttributes()
   */
  @Override
  public Map<String, String> writeXMLAttributes() {
    Map<String, String> attributes = super.writeXMLAttributes();
    if (isSetStroke()) {
      attributes.remove(RenderConstants.stroke);
      attributes.put(RenderConstants.shortLabel + ':' + RenderConstants.strokeWidth,
        getStroke());
    }
    if (isSetStrokeDashArray()) {
      attributes.remove(RenderConstants.strokeDashArray);
      attributes.put(RenderConstants.shortLabel + ':' + RenderConstants.strokeWidth,
        XMLTools.encodeArrayShortToString(getStrokeDashArray()));
    }
    if (isSetStrokeWidth()) {
      attributes.remove(RenderConstants.strokeWidth);
      attributes.put(RenderConstants.shortLabel + ':' + RenderConstants.strokeWidth,
        getStrokeWidth().toString().toLowerCase());
    }
    return attributes;
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.AbstractSBase#readAttribute(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public boolean readAttribute(String attributeName, String prefix, String value) {
    boolean isAttributeRead = super.readAttribute(attributeName, prefix, value);
    if (!isAttributeRead) {
      isAttributeRead = true;
      // TODO: catch Exception if Enum.valueOf fails, generate logger output
      if (attributeName.equals(RenderConstants.stroke)) {
        setStroke(value);
      }
      else if (attributeName.equals(RenderConstants.strokeDashArray)) {
        setStrokeDashArray(XMLTools.decodeStringToArrayShort(value));
      }
      else if (attributeName.equals(RenderConstants.strokeWidth)) {
        setStrokeWidth(StringTools.parseSBMLDouble(value));
      }
      else {
        isAttributeRead = false;
      }
    }
    return isAttributeRead;
  }

}
