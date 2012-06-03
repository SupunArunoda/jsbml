/*
 * $Id$
 * $URL$
 *
 * ---------------------------------------------------------------------------- 
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML> 
 * for the latest version of JSBML and more information about SBML. 
 * 
 * Copyright (C) 2009-2012 jointly by the following organizations: 
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
package org.sbml.jsbml.ext.render;

import java.text.MessageFormat;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.PropertyUndefinedError;
import org.sbml.jsbml.SBase;


/**
 * @author Eugen Netz
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Jan Rudolph
 * @version $Rev$
 * @since 1.0
 * @date 08.05.2012
 */
public class Polygon extends GraphicalPrimitive2D {
	/**
   * 
   */
  private static final long serialVersionUID = 9207043017589271103L;

  /**
   * 
   */
  private ListOf<RenderPoint> listOfElements;

  /**
   * 
   */
  private ListOf<RenderPoint> listOfRenderPoints;


  /**
   * Creates an Polygon instance 
   */
  public Polygon() {
    super();
    initDefaults();
  }
  
  /**
   * Clone constructor
   */
  public Polygon(Polygon obj) {
    super(obj);
    listOfElements = obj.listOfElements;
  }

  /**
   * @param renderPoint
   */
  public boolean addRenderPoint(RenderPoint renderPoint) {
    return getListOfRenderPoints().add(renderPoint);
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.ext.render.GraphicalPrimitive2D#clone()
   */
  @Override
  public Polygon clone() {
    return new Polygon(this);
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.ext.render.GraphicalPrimitive1D#getAllowsChildren()
   */
  @Override
  public boolean getAllowsChildren() {
    return true;
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.ext.render.GraphicalPrimitive1D#getChildAt(int)
   */
  @Override
  public SBase getChildAt(int childIndex) {
    if (childIndex < 0) {
      throw new IndexOutOfBoundsException(childIndex + " < 0");
    }
    int pos = 0;
    if (isSetListOfElements()) {
      if (pos == childIndex) {
        return getListOfElements();
      }
      pos++;
    }
    throw new IndexOutOfBoundsException(MessageFormat.format(
      "Index {0,number,integer} >= {1,number,integer}", childIndex,
      +((int) Math.min(pos, 0))));
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.ext.render.GraphicalPrimitive1D#getChildCount()
   */
  @Override
  public int getChildCount() {
    int count = 0;
    if (isSetListOfElements()) {
      count++;
    }
    return count;
  }

  /**
   * @return the value of listOfElements
   */
  public ListOf<RenderPoint> getListOfElements() {
    if (isSetListOfElements()) {
      return listOfElements;
    }
    // This is necessary if we cannot return null here.
    throw new PropertyUndefinedError(RenderConstants.listOfElements, this);
  }

  /**
   * @return the listOfRenderPoints
   */
  public ListOf<RenderPoint> getListOfRenderPoints() {
    if (!isSetListOfRenderPoints()) {
      listOfRenderPoints = new ListOf<RenderPoint>(getLevel(), getVersion());
      listOfRenderPoints.addNamespace(RenderConstants.namespaceURI);
      listOfRenderPoints.setSBaseListType(ListOf.Type.other);
      registerChild(listOfRenderPoints);
    }
    return listOfRenderPoints;
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.ext.render.GraphicalPrimitive2D#initDefaults()
   */
  @Override
  public void initDefaults() {
    addNamespace(RenderConstants.namespaceURI);
  }

  /**
   * @return whether listOfElements is set 
   */
  public boolean isSetListOfElements() {
    return this.listOfElements != null;
  }

  /**
   * @return <code>true</code>, if listOfRenderPoints contains at least one element, 
   *         otherwise <code>false</code>
   */
  public boolean isSetListOfRenderPoints() {
    if ((listOfRenderPoints == null) || listOfRenderPoints.isEmpty()) {
      return false;
    }
    return true;
  }

  /**
   * @param i
   */
  public void removeRenderPoint(int i) {
    if (!isSetListOfRenderPoints()) {
      throw new IndexOutOfBoundsException(Integer.toString(i));
    }
    getListOfRenderPoints().remove(i);
  }

  /**
   * @param renderPoint
   */
  public boolean removeRenderPoint(RenderPoint renderPoint) {
    if (isSetListOfRenderPoints()) {
      return getListOfRenderPoints().remove(renderPoint);
    }
    return false;
  }

  /**
   * Set the value of listOfElements
   */
  public void setListOfElements(ListOf<RenderPoint> listOfElements) {
    ListOf<RenderPoint> oldListOfElements = this.listOfElements;
    this.listOfElements = listOfElements;
    firePropertyChange(RenderConstants.listOfElements, oldListOfElements, this.listOfElements);
  }

  /**
   * @param listOfRenderPoints
   */
  public void setListOfRenderPoints(ListOf<RenderPoint> listOfRenderPoints) {
    unsetListOfRenderPoints();
    this.listOfRenderPoints = listOfRenderPoints;
    registerChild(this.listOfRenderPoints);
  }

  /**
   * Unsets the variable listOfElements 
   * @return <code>true</code>, if listOfElements was set before, 
   *         otherwise <code>false</code>
   */
  public boolean unsetListOfElements() {
    if (isSetListOfElements()) {
      ListOf<RenderPoint> oldListOfElements = this.listOfElements;
      this.listOfElements = null;
      firePropertyChange(RenderConstants.listOfElements, oldListOfElements, this.listOfElements);
      return true;
    }
    return false;
  }

  /**
   * @return <code>true</code>, if listOfRenderPoints contained at least one element, 
   *         otherwise <code>false</code>
   */
  public boolean unsetListOfRenderPoints() {
    if (isSetListOfRenderPoints()) {
      ListOf<RenderPoint> oldRenderPoints = this.listOfRenderPoints;
      this.listOfRenderPoints = null;
      oldRenderPoints.fireNodeRemovedEvent();
      return true;
    }
    return false;
  }

}