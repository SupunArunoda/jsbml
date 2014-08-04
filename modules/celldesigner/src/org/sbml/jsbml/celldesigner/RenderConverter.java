/*
 * $Id$
 * $URL$
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 * 
 * Copyright (C) 2009-2014  jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 * 4. The University of California, San Diego, La Jolla, CA, USA
 * 5. The Babraham Institute, Cambridge, UK
 * 6. Marquette University, Milwaukee, WI, USA
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.celldesigner;

import jp.sbi.celldesigner.plugin.PluginCompartment;
import jp.sbi.celldesigner.plugin.PluginSpeciesAlias;

import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.render.ColorDefinition;
import org.sbml.jsbml.ext.render.Group;
import org.sbml.jsbml.ext.render.LocalRenderInformation;
import org.sbml.jsbml.ext.render.LocalStyle;
import org.sbml.jsbml.ext.render.RenderLayoutPlugin;


/**
 * @author Ibrahim Vazirabad
 * @version $Rev$
 * @since 1.0
 * @date Jul 3, 2014
 */
public class RenderConverter {

  public static void extractRenderInformation(PluginCompartment pCompartment, LocalRenderInformation renderInfo, Layout layout)
  {
    renderInfo.addColorDefinition(new ColorDefinition(pCompartment.getId(), pCompartment.getLineColor()));
    LocalStyle localStyle = renderInfo.getListOfLocalStyles().getFirst();
    String[] compartmentIDList = new String[1];
    compartmentIDList[0] = "cGlyph_"+pCompartment.getId();

    localStyle.setIDList(compartmentIDList);
    Group group = localStyle.getGroup();
    group.setStroke(pCompartment.getId());
    group.setStrokeWidth(pCompartment.getThickness());
  }

  public static void extractRenderInformation(PluginSpeciesAlias pSpeciesAlias, RenderLayoutPlugin render)
  {
    LocalRenderInformation LRI=render.getLocalRenderInformation(0);
    LRI.addColorDefinition(new ColorDefinition(pSpeciesAlias.getSpecies().getId()+"_Color", pSpeciesAlias.getColor()));
  }

}
