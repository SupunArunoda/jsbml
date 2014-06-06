/*
 * $Id:  SBMLExportPlugin.java 1776 Jun 3, 2014 4:43:55 PM yvazirabad $
 * $URL: https://svn.code.sf.net/p/jsbml/code/trunk/modules/celldesigner/test/org/sbml/jsbml/celldesigner/SBMLExportPlugin.java $
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

import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import jp.sbi.celldesigner.plugin.PluginMenu;
import jp.sbi.celldesigner.plugin.PluginMenuItem;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.gui.SBMLLayoutVisualizer;


/**
 * @author Ibrahim Vazirabad
 * @version $Rev 1776$
 * @since 1.0
 * @date Jun 3, 2014
 */
public class SBMLExportPlugin extends AbstractCellDesignerPlugin  {

  public static final String ACTION = "Display SBML Representation";
  public static final String APPLICATION_NAME = "SBML Export";

  /**
   * Creates a new CellDesigner plug-in with an entry in the menu bar.
   */
  public SBMLExportPlugin() {
    super();
    addPluginMenu();
  }

  /* (non-Javadoc)
   * @see jp.sbi.celldesigner.plugin.CellDesignerPlug#addPluginMenu()
   */
  @Override
  public void addPluginMenu() {
    // Initializing CellDesigner's menu entries
    PluginMenu menu = new PluginMenu(APPLICATION_NAME);
    PluginMenuItem menuItem = new PluginMenuItem(
      ACTION, new SBMLExportPluginAction(this));
    menuItem.setToolTipText("Displays the raw SBML representation of this model");
    menu.add(menuItem);
    addCellDesignerPluginMenu(menu);
  }

  /**
   * Performs the action for which this plug-in is designed.
   *
   * @throws XMLStreamException If the given SBML model contains errors.
   */
  public void startPlugin() throws XMLStreamException {
    // Synchronize changes from this plug-in to CellDesigner:
    final PluginChangeListener changeListener = new PluginChangeListener(this);
    SwingWorker<Model, Void> worker = new SwingWorker<Model, Void>() {
      @Override
      protected Model doInBackground() throws Exception {
        return getReader().convertModel(getSelectedModel());
      }

      @Override
      protected void done() {

        try {
          Model model = get();
          SBMLDocument doc = new SBMLDocument(model.getLevel(), model.getVersion());
          doc.setModel(model);
          doc.addTreeNodeChangeListener(changeListener);
          new SBMLLayoutVisualizer(doc);
          super.done();
        } catch (Throwable exc) {
          exc.printStackTrace();
        }
      }
    };
    worker.execute();
  }
}