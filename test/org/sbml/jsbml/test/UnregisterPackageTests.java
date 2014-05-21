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
package org.sbml.jsbml.test;


import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Constraint;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ext.arrays.ArraysConstants;
import org.sbml.jsbml.ext.arrays.ArraysSBasePlugin;
import org.sbml.jsbml.ext.comp.CompConstants;
import org.sbml.jsbml.ext.comp.CompModelPlugin;
import org.sbml.jsbml.ext.comp.CompSBMLDocumentPlugin;
import org.sbml.jsbml.ext.comp.ModelDefinition;
import org.sbml.jsbml.ext.comp.Port;
import org.sbml.jsbml.ext.fbc.FBCConstants;
import org.sbml.jsbml.ext.fbc.FBCModelPlugin;
import org.sbml.jsbml.ext.fbc.FBCSpeciesPlugin;
import org.sbml.jsbml.ext.fbc.FluxBound;
import org.sbml.jsbml.ext.fbc.FluxBound.Operation;
import org.sbml.jsbml.ext.fbc.Objective;
import org.sbml.jsbml.ext.groups.Group;
import org.sbml.jsbml.ext.groups.GroupsConstants;
import org.sbml.jsbml.ext.groups.GroupsModelPlugin;
import org.sbml.jsbml.ext.groups.Member;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.LayoutModelPlugin;
import org.sbml.jsbml.ext.qual.QualConstants;
import org.sbml.jsbml.ext.qual.QualModelPlugin;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.ext.render.RenderConstants;
import org.sbml.jsbml.ext.render.RenderLayoutPlugin;
import org.sbml.jsbml.ext.render.RenderListOfLayoutsPlugin;
import org.sbml.jsbml.ext.spatial.Geometry;
import org.sbml.jsbml.ext.spatial.SpatialConstants;
import org.sbml.jsbml.ext.spatial.SpatialModelPlugin;

/**
 * Tests the registration and un-registration of global or local id using package elements.
 * 
 * @version $Rev$
 * @since 1.0
 */
public class UnregisterPackageTests {

  SBMLDocument doc;
  Model model;
  CompModelPlugin compMainModel;
  
  @BeforeClass public static void initialSetUp() {}
  /**
   * 
   */
  @Before public void setUp() {
    doc = new SBMLDocument(3, 1);
    model = doc.createModel("model");

    compMainModel = (CompModelPlugin) model.getPlugin("comp");
    compMainModel.createSubmodel("submodel1");
    
    Compartment comp = model.createCompartment("cell");
    comp.setMetaId("cell");

    Species s1 = model.createSpecies("S1", comp);
    s1.setMetaId("S1");

    Species s2 = model.createSpecies("S2", comp);
    s2.setMetaId("S2");

    Reaction r1 = model.createReaction("R1");
    r1.setMetaId("R1");

    SpeciesReference reactant = model.createReactant("SP1");
    reactant.setMetaId("SP1");
    reactant.setSpecies(s1);

    SpeciesReference product = model.createProduct("SP2");
    product.setMetaId("SP2");
    product.setSpecies(s2);

    LocalParameter lp1 = r1.createKineticLaw().createLocalParameter("LP1");
    lp1.setMetaId("LP1");

    Constraint c1 = model.createConstraint();
    c1.setMetaId("c0");
  }



  @Test public void testCompPort() {

    Species s3 = new Species();
    s3.setId("S3");
    model.addSpecies(s3);

    assertTrue(compMainModel.getPortCount() == 0);
    
    // create a Port with the same id as any existing one from the SIdRef scope.
    Port portS3 = null;
    try {
    	portS3 = compMainModel.createPort("S3");
    	assertTrue(true);
    } catch (IllegalArgumentException e) {
    	assertTrue("We should be allowed to create a Port with the same id as a Species in the model", false);
    }

    assertTrue(compMainModel.getPortCount() == 1);
    
    // create a second Port with the same id as the first Port and check that this is not allowed
    try {
    	compMainModel.createPort("S3");
    	assertTrue("We should not be allowed to have several Port with the same id inside the same model", false);
    } catch (IllegalArgumentException e) {
    	assertTrue(true);
    }

    assertTrue(compMainModel.getPortCount() == 1);
    
    // create a second Port with a different id. Create a core element with the same id and check that it is allowed
    Port portPo1 = null;
    try {
    	portPo1 = compMainModel.createPort("Po1");
    	assertTrue(true);

    	model.createParameter("Po1");
    	assertTrue(true);
    } catch (IllegalArgumentException e) {
    	assertTrue("We should be allowed to create a core element with the same id as an existing Port", false);
    }

    assertTrue(compMainModel.getPortCount() == 2);
    
    assertTrue(compMainModel.getPort("Po1") != null);
    assertTrue(compMainModel.getPort("Po1").equals(portPo1));
    assertTrue(compMainModel.getPort("S3") != null);
    assertTrue(compMainModel.getPort("S3").equals(portS3));
    
    assertTrue(! portS3.equals(portPo1));
    
    CompSBMLDocumentPlugin compDoc = (CompSBMLDocumentPlugin) doc.getPlugin("comp");
    
    ModelDefinition modelDef = (ModelDefinition) compDoc.createModelDefinition("modelDef1");
    
    CompModelPlugin compModelDef = (CompModelPlugin) modelDef.getPlugin("comp");
    
    // create a ModelDefinition with a portId and id the same as in the main model
    compModelDef.createPort("S3");
    modelDef.createSpecies("S3");

    ModelDefinition modelDef2 = (ModelDefinition) compDoc.createModelDefinition("modelDef2"); 
    
    // modelDefinition id belong to the id namespace of the main model
    // TODO - add test for those when implementation is done
    // TODO - problem here, if a SBMLDocument is created with the main model element defined
    
    CompModelPlugin compModelDef2 = (CompModelPlugin) modelDef2.getPlugin("comp");
    
    // create a ModelDefinition with a portId and id the same as in the main model
    modelDef2.createSpecies("S3");
    compModelDef2.createPort("S3");

    assertTrue(modelDef.getSpecies("S3") != modelDef2.getSpecies("S3"));
    assertTrue(modelDef.getSpecies("S3").equals(modelDef2.getSpecies("S3")));
    
    // create a second Port with the same id as the first Port in a ModelDefinition and check that this is not allowed
    try {
    	compModelDef2.createPort("S3");
    	assertTrue("We should not be allowed to have several Port with the same id inside the same modelDefinition", false);
    } catch (IllegalArgumentException e) {
    	assertTrue(true);
    }

    // create a second Species with the same id as the first Species in a ModelDefinition and check that this is not allowed
    try {
    	modelDef2.createSpecies("S3");
    	assertTrue("We should not be allowed to have several Species with the same id inside the same modelDefinition", false);
    } catch (IllegalArgumentException e) {
    	assertTrue(true);
    }
        
    assertTrue(modelDef2.getSpeciesCount() == 1);
    assertTrue(compModelDef2.getPortCount() == 1);
    
    compModelDef2.createPort("test1");
    compModelDef2.createPort("test2");
    compModelDef2.createSubmodel("subM1");
    compModelDef2.createSubmodel("subM2");
    
    assertTrue(modelDef2.findUniqueNamedSBase("subM1") != null);
    assertTrue(model.findUniqueNamedSBase("subM1") == null);
    assertTrue(modelDef.findUniqueNamedSBase("subM1") == null);
    
    System.out.println("BEFORE CLONING");
    
    SBMLDocument clonedDoc = doc.clone();
    Model clonedModel = clonedDoc.getModel(); 
    CompModelPlugin compMainModelPluginCloned = (CompModelPlugin) clonedModel.getPlugin("comp");
    ModelDefinition clonedModelDef2 = ((CompSBMLDocumentPlugin) clonedDoc.getPlugin("comp")).getListOfModelDefinitions().get("modelDef2");
    CompModelPlugin clonedModelDef2Plugin = (CompModelPlugin) clonedModelDef2.getPlugin("comp");
    
    System.out.println("AFTER CLONING");
    
    assertTrue(model.getSpeciesCount() == 3);
        
    assertTrue(compMainModelPluginCloned.getPortCount() == 2);
    assertTrue(clonedModel.getSpeciesCount() == 3);
    assertTrue(clonedModel.findUniqueNamedSBase("S2") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("S3") != null);
    assertTrue(compMainModelPluginCloned.getPort(0).getId().equals("S3"));
    assertTrue(compMainModelPluginCloned.getPort("S3") != null);
    
    assertTrue(clonedModelDef2.findUniqueNamedSBase("subM1") != null);
    assertTrue(clonedModelDef2Plugin.getPortCount() == 3);
    assertTrue(clonedModelDef2Plugin.getPort("test1") != null);
    
    // Testing un-registration of Port
    compMainModel.removePort(0);
    
    assertTrue(compMainModel.getPortCount() == 1);
    assertTrue(compMainModel.getPort("Po1") != null);
    assertTrue(compMainModel.getPort("S3") == null);

    compMainModel.removePort(portPo1);
    
    assertTrue(compMainModel.getPortCount() == 0);
    assertTrue(compMainModel.getPort("Po1") == null);

    // Testing un-registration of SubModel
    
    compModelDef2.removeSubmodel("subM1");
    assertTrue(modelDef2.findUniqueNamedSBase("subM1") == null);

    compModelDef2.unsetListOfSubmodels();
    compModelDef2.unsetListOfPorts();
    assertTrue(modelDef2.findUniqueNamedSBase("subM2") == null);
    assertTrue(compModelDef2.getPortCount() == 0);
    assertTrue(compModelDef2.getPort("test1") == null);
        
  }  
  
  // TODO - add tests using ReplacedBy as it is using the method firePropertyChange. 
  
  
  @Test public void testFbc() {
    
    FBCModelPlugin fbcModel = (FBCModelPlugin) model.getPlugin(FBCConstants.namespaceURI_L3V1V1);
    
    FluxBound fluxBound1 = fbcModel.createFluxBound("FB1");
    fluxBound1.setMetaId("FB1");
    fluxBound1.setReaction("R1");
    fluxBound1.setOperation(FluxBound.Operation.GREATER_EQUAL);
    fluxBound1.setValue(800);
    
    fbcModel.createFluxBound("FB2");
    fbcModel.createFluxBound("FB3");
    fbcModel.createObjective("O1");
    fbcModel.createObjective("O2");
    
    assertTrue(fbcModel.getFluxBoundCount() == 3);
    assertTrue(fbcModel.getObjectiveCount() == 2);
    
    assertTrue(fbcModel.getFluxBound(0).getLevel() == 3);
    assertTrue(fbcModel.getFluxBound(0).getVersion() == 1);
    assertTrue(fbcModel.getListOfFluxBounds().getLevel() == 3);
    assertTrue(fbcModel.getListOfFluxBounds().getVersion() == 1);
    
    assertTrue(model.findUniqueNamedSBase("FB2") != null);
    assertTrue(model.findUniqueNamedSBase("O1") != null);
    
    assertTrue(doc.findSBase("FB1").equals(fluxBound1));
    
    Species s3 = model.createSpecies("S3");
    
    FBCSpeciesPlugin s3FbcPlugin = (FBCSpeciesPlugin) s3.getPlugin("fbc");
    s3FbcPlugin.setCharge(8);
    s3FbcPlugin.setChemicalFormula("H20");
    s3.setMetaId("S3");
    
    
    // trying to add a fluxbound/objective with an existing id, metaid
    try {
      fbcModel.createFluxBound("O1");
      assertTrue("We should not be allowed to have several element with the same id inside the same model", false);
    } catch (IllegalArgumentException e) {
        assertTrue(true);
        assertTrue(fbcModel.getFluxBoundCount() == 3);
    }    
    
    try {
      FluxBound f4 = new FluxBound("FB4");
      f4.setMetaId("FB1");
      fbcModel.addFluxBound(f4);
      assertTrue("We should not be allowed to have several element with the same metaid inside the same model", false);
    } catch (IllegalArgumentException e) {
        assertTrue(true);
        assertTrue(fbcModel.getFluxBoundCount() == 3);
    }    
    
    try {
      Objective o3 = new Objective("O3");
      o3.setMetaId("S2");
      fbcModel.addObjective(o3);
      assertTrue("We should not be allowed to have several element with the same metaid inside the same model", false);
    } catch (IllegalArgumentException e) {
        assertTrue(true);
        assertTrue(fbcModel.getObjectiveCount() == 2);
    }    
    
    
    // Cloning the FBCModelPlugin
    FBCModelPlugin clonedFbcModel = fbcModel.clone(); 
    assertTrue(clonedFbcModel.getListOfFluxBounds().getLevel() == 3);
    assertTrue(clonedFbcModel.getListOfFluxBounds().getVersion() == 1);
    
    model.unsetPlugin("fbc");
    
//    assertTrue(model.findUniqueNamedSBase("FB2") == null); // TODO - fix firePropertyChange to register/un-register elements before putting that again
//    assertTrue(model.findUniqueNamedSBase("O1") == null);
//  assertTrue(doc.findSBase("FB1") == null);

    Model clonedModel = model.clone();    
    clonedModel.addExtension("fbc", clonedFbcModel);
    
    SBMLDocument newDoc = new SBMLDocument(3, 1);
    newDoc.setModel(clonedModel);
    
    assertTrue(clonedFbcModel.getFluxBoundCount() == 3);
    assertTrue(clonedFbcModel.getObjectiveCount() == 2);
    
    assertTrue(clonedFbcModel.getFluxBound(0).getLevel() == 3);
    assertTrue(clonedFbcModel.getFluxBound(0).getVersion() == 1);
    assertTrue(clonedFbcModel.getListOfFluxBounds().getLevel() == 3);
    assertTrue(clonedFbcModel.getListOfFluxBounds().getVersion() == 1);
    
    assertTrue(clonedModel.findUniqueNamedSBase("FB2") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("O1") != null);

    assertTrue(newDoc.findSBase("FB1").equals(fluxBound1));
    
    FluxBound clonedFluxBound1 = (FluxBound) newDoc.findSBase("FB1"); 
 
    assertTrue(clonedFluxBound1.getReaction().equals("R1"));
    clonedFluxBound1.setValue(550);
    assertTrue(!clonedFluxBound1.equals(fluxBound1));
    assertTrue(clonedFluxBound1.hashCode() != fluxBound1.hashCode());
    
    clonedFluxBound1.setReaction("R2");
    clonedFluxBound1.setOperation(Operation.EQUAL);
    
    assertTrue(clonedFluxBound1.getValue() == 550);
    assertTrue(fluxBound1.getValue() == 800);
    assertTrue(clonedFluxBound1.getReaction().equals("R2"));
    assertTrue(fluxBound1.getReaction().equals("R1"));
    assertTrue(clonedFluxBound1.getOperation().equals(Operation.EQUAL));
    assertTrue(fluxBound1.getOperation().equals(Operation.GREATER_EQUAL));

    
  }

  
  /**
   * Create a SBasePlugin by hand without extenddeSBase set, add it to an element and check that the ids are registered.
   * 
   */
  @Test public void testQual() {
    
    QualModelPlugin qualModel = new QualModelPlugin((Model) null);
    QualitativeSpecies qs1 = qualModel.createQualitativeSpecies("QS1");
    qs1.setMetaId("QS1");
    qs1.setInitialLevel(1);
    qs1.setMaxLevel(4);
    qs1.setCompartment(model.getCompartment(0));
    qs1.setConstant(false);
    
    qualModel.createQualitativeSpecies("QS2");
    qualModel.createQualitativeSpecies("QS3");
    qualModel.createQualitativeSpecies("QS4");
    
    qualModel.createTransition("T1");
    qualModel.createTransition("T2");
    
    model.addPlugin(QualConstants.shortLabel, qualModel);
    
    assertTrue(model.findUniqueNamedSBase("QS1").equals(qs1));
    assertTrue(model.findUniqueNamedSBase("QS3") != null);
    assertTrue(model.findUniqueNamedSBase("T2") != null);
    assertTrue(doc.findSBase("QS1") != null);

    // cloning the whole document
    SBMLDocument clonedDoc = doc.clone();
    Model clonedModel = clonedDoc.getModel();
    QualModelPlugin clonedQualModel = (QualModelPlugin) clonedModel.getExtension(QualConstants.shortLabel);
    
    assertTrue(clonedModel.isSetPlugin(QualConstants.shortLabel) == true);
    assertTrue(clonedModel.isSetPlugin(LayoutConstants.shortLabel) == false);
    assertTrue(clonedModel.isSetPlugin(CompConstants.shortLabel) == true);
    assertTrue(clonedDoc.isPackageEnabled(QualConstants.shortLabel) == true);
    assertTrue(clonedDoc.isPackageEnabled(LayoutConstants.shortLabel) == false);
    assertTrue(clonedDoc.isPackageEnabled(CompConstants.shortLabel) == true);
    
    assertTrue(clonedQualModel.getQualitativeSpeciesCount() == 4);
    assertTrue(clonedModel.findUniqueNamedSBase("QS1").equals(qs1));
    assertTrue(clonedModel.findUniqueNamedSBase("QS3") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("T2") != null);
    assertTrue(clonedDoc.findSBase("QS1") != null);

    // test qualitativeSpecies clone method
    QualitativeSpecies clonedQs1 = clonedQualModel.getQualitativeSpecies("QS1");
    
    assertTrue(qs1.hashCode() == clonedQs1.hashCode());
    assertTrue(qs1.equals(clonedQs1));
    
    clonedQs1.setInitialLevel(0);
    assertTrue(qs1.hashCode() != clonedQs1.hashCode());
  }
  
  @Test public void testGroups() {
    
    GroupsModelPlugin groupsModel = (GroupsModelPlugin) model.getPlugin(GroupsConstants.shortLabel);
    Group group = groupsModel.createGroup();
    group.setId("G1");
    
    // TODO - add it later when setId register and unregister the id - ((ListOfMemberConstraint) group.getListOfMemberConstraints()).setId("GLMC1");
    
    group.createMember("GM1");
    group.createMemberWithIdRef("GM2", "S2");
    Member member3 = group.createMemberWithIdRef("GM3", "S1");
    member3.setMetaId("GM3");
    group.createMemberConstraint("GMC1");
    
    assertTrue(groupsModel.getGroupCount() == 1);
    assertTrue(group.getMemberCount() == 3);
    assertTrue(group.getMemberConstraintCount() == 1);
    
    try {
      Group g2 = groupsModel.createGroup();
      g2.setId("G1");
      assertTrue("We should not be allowed to have several element with the same id inside the same model", false);
    } catch (IllegalArgumentException e) {
        assertTrue(true);
    } 
    try {
      groupsModel.createGroup("GM1");
      assertTrue("We should not be allowed to have several element with the same id inside the same model", false);
    } catch (IllegalArgumentException e) {
        assertTrue(true);
    } 
    try {
      groupsModel.createGroup("GMC1");
      assertTrue("We should not be allowed to have several element with the same id inside the same model", false);
    } catch (IllegalArgumentException e) {
        assertTrue(true);
    } 
//    try {
//      groupsModel.createGroup("GLMC1");
//      assertTrue("We should not be allowed to have several element with the same id inside the same model", false);
//    } catch (IllegalArgumentException e) {
//        assertTrue(true);
//    } 
    assertTrue(groupsModel.getGroupCount() == 2);

    try {
      group.createMember("S1");
      assertTrue("We should not be allowed to have several element with the same id inside the same model", false);
    } catch (IllegalArgumentException e) {
        assertTrue(true);
    } 
    try {
      group.createMember("G1");
      assertTrue("We should not be allowed to have several element with the same id inside the same model", false);
    } catch (IllegalArgumentException e) {
        assertTrue(true);
    } 
    assertTrue(group.getMemberCount() == 3);

    SBMLDocument clonedDoc = doc.clone();
    Model clonedModel = clonedDoc.getModel();
    GroupsModelPlugin clonedGroupModelPlugin = (GroupsModelPlugin) clonedModel.getPlugin("groups");
    
    assertTrue(clonedGroupModelPlugin.getGroupCount() == 2);
    assertTrue(clonedDoc.findSBase("GM3") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("GM3") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("G1") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("GMC1") != null);
    
  }
  
  @Test public void testLayout() {
    
    LayoutModelPlugin layoutModel = (LayoutModelPlugin) model.getPlugin(LayoutConstants.shortLabel);
    Layout layout = layoutModel.createLayout("L1");
    
    layout.createCompartmentGlyph("LCG1");
    layout.createGeneralGlyph("LGG1");
    layout.createReactionGlyph("LRG1");
    layout.createSpeciesGlyph("LSG1");
    layout.createTextGlyph("LTG1");
    
    assertTrue(model.findUniqueNamedSBase("L1") != null);
    assertTrue(model.findUniqueNamedSBase("LCG1") != null);
    assertTrue(model.findUniqueNamedSBase("LGG1") != null);
    assertTrue(model.findUniqueNamedSBase("LRG1") != null);
    assertTrue(model.findUniqueNamedSBase("LSG1") != null);
    assertTrue(model.findUniqueNamedSBase("LTG1") != null);
    
    SBMLDocument clonedDoc = doc.clone();
    Model clonedModel = clonedDoc.getModel();

    assertTrue(clonedModel.findUniqueNamedSBase("LRG1") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("L1") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("submodel1") != null);
  
    
    layout.removeReactionGlyph(0);
    layout.removeSpeciesGlyph("LSG1");
    layout.removeTextGlyph(layout.getTextGlyph(0));
    
    assertTrue(model.findUniqueNamedSBase("LCG1") != null);
    assertTrue(model.findUniqueNamedSBase("LGG1") != null);
    assertTrue(model.findUniqueNamedSBase("LRG1") == null);
    assertTrue(model.findUniqueNamedSBase("LSG1") == null);
    assertTrue(model.findUniqueNamedSBase("LTG1") == null);

    assertTrue(clonedModel.findUniqueNamedSBase("LCG1") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("LGG1") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("LRG1") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("LSG1") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("LTG1") != null);

  }
  
  @Test public void testRender() {
   
    LayoutModelPlugin layoutModel = (LayoutModelPlugin) model.getPlugin(LayoutConstants.shortLabel);
    Layout layout = layoutModel.createLayout("L1");

    RenderListOfLayoutsPlugin renderListOfLayoutsPlugin = (RenderListOfLayoutsPlugin) layoutModel.getListOfLayouts().getPlugin(RenderConstants.shortLabel);
    RenderLayoutPlugin renderLayoutPlugin = (RenderLayoutPlugin) layout.createPlugin(RenderConstants.shortLabel);
    
    renderListOfLayoutsPlugin.createGlobalRenderInformation("RGRI1");
    
    renderLayoutPlugin.createLocalRenderInformation("RLRI1");
    renderLayoutPlugin.createLocalRenderInformation("RLRI2");

    assertTrue(model.findUniqueNamedSBase("L1") != null);
    assertTrue(model.findUniqueNamedSBase("RGRI1") != null);
    assertTrue(model.findUniqueNamedSBase("RLRI1") != null);
    
    SBMLDocument clonedDoc = doc.clone();
    Model clonedModel = clonedDoc.getModel();

    assertTrue(clonedModel.findUniqueNamedSBase("L1") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("RLRI1") != null);
    assertTrue(clonedModel.findUniqueNamedSBase("RGRI1") != null);
    
  }
  
  @Test public void testSpatial() {
    SpatialModelPlugin spatialModel  = (SpatialModelPlugin) model.getPlugin(SpatialConstants.shortLabel);
    
    Geometry geometry = spatialModel.createGeometry();
    geometry.setMetaId("SG1");
    
    // TODO - create plenty of spatial new objects to be able to test their id or metaid
    
    assertTrue(geometry.getParent() != null);
    assertTrue(doc.findSBase("SG1").equals(geometry));
    // TODO - add some tests for the id and metaids created
        
    // TODO - add some tests trying to add a new element using an existing id or metaid.
    
//    try {
//      Group g2 = groupsModel.createGroup();
//      g2.setId("G1");
//      assertTrue("We should not be allowed to have several element with the same id inside the same model", false);
//    } catch (IllegalArgumentException e) {
//        assertTrue(true);
//    } 

    
    // cloning to test if the ids or metaids are still registered properly
    SpatialModelPlugin clonedSpatialModel  = spatialModel.clone();
    SBMLDocument newDoc = new SBMLDocument(3, 1);
    Model clonedModel = newDoc.createModel("clonedModel");
    clonedModel.addExtension(SpatialConstants.shortLabel, clonedSpatialModel);
    
    Geometry clonedGeometry = clonedSpatialModel.getGeometry();
    
    assertTrue(clonedGeometry.equals(geometry));
    assertTrue(clonedGeometry.getParent() != null); // parent should be set by the firePropertyChange method. id or metaid not registered at the moment when cloning

    assertTrue(newDoc.findSBase("SG1").equals(geometry));    
    // TODO - add again the same tests for the id and metaids on the new SBMLDocument and Model
  }

  
  @Test public void testMulti() {
    // TODO - when the package code is updated
  }
  
  @Test public void testArrays() {
    // TODO - when the package code is done
    
    ArraysSBasePlugin arraysTestPlugin = (ArraysSBasePlugin) model.getPlugin(ArraysConstants.shortLabel);
    
    arraysTestPlugin.createDimension("AD1");
    
    assertTrue(model.findNamedSBase("AD1") != null);
    
  }

}