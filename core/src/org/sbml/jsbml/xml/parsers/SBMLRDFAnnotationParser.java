/*
 * $Id$
 * $URL$
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2013 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 * 4. The University of California, San Diego, La Jolla, CA, USA
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */

package org.sbml.jsbml.xml.parsers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.Creator;
import org.sbml.jsbml.History;
import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.xml.XMLAttributes;
import org.sbml.jsbml.xml.XMLNode;
import org.sbml.jsbml.xml.XMLTriple;
import org.w3c.util.DateParser;
import org.w3c.util.InvalidDateException;


// we could make some lists with the expected attributes and sub-elements ??

/**
 * Parses the RDF annotations as defined in the SBML specifications and put them into objects.
 * 
 * TODO - extends a lot the doc to describe the whole process of validation, reading and writing. 
 * 
 * @author Nicolas Rodriguez
 * @version $Rev$
 * @since 1.0
 */
public class SBMLRDFAnnotationParser implements AnnotationReader, AnnotationWriter {

	/**
	 * A Constant {@link String} that is used to store a {@link XMLNode}
	 * color in the user objects map.
	 */
	public static final String RDF_NODE_COLOR = "jsbml.rdf.node.color";
	/**
	 * A Constant {@link String} that is used to store any remaining children
	 * or attributes after having removed the standard SBML RDF elements and attributes.
	 * <p>It is stored as an {@link XMLNode} in the user objects map.
	 * 
	 */
	public static final String CUSTOM_RDF = "jsbml.custom.rdf"; 
	
	/**
	 * 
	 */
	enum NODE_COLOR {
		/**
		 * The SBML RDF block is valid.
		 */
		GREEN,
		/**
		 * The SBML RDF block is partially valid, some elements are added or removed.
		 */
		ORANGE, 
		/**
		 * The RDF block does not exist or is not of the form define in the SBML specifications.
		 */
		WHITE};

	
	/**
	 * Logger to be able to send informative, debug, warning or error messages.
	 * 
	 */
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public XMLNode writeAnnotation(SBase contextObject) 
	{
		logger.debug("writeAnnotation called ");
		
		if (contextObject.isSetAnnotation() && (contextObject.isSetHistory() || contextObject.getCVTermCount() > 0))
		{
			// write all the RDF part of the annotation
			writeSBMLRDF(contextObject);
		}
		return contextObject.getAnnotation().getNonRDFannotation();
	}

	@Override
	public void processAnnotation(SBase contextObject) 
	{		
		logger.debug("processAnnotation called ");
		
		boolean isValidSBMLRDF = isValidSBMLRDF(contextObject);

		if (isValidSBMLRDF) {					
			readSBMLRDF(contextObject);
		}
	}

	/**
	 * Returns true if the SBML RDF block is recognized as valid or partially valid. <p>Additional elements are allowed but
	 * what is defined in the SBML specifications should be there as well. It means that the
	 * RDF element needs to have the color {@link NODE_COLOR#GREEN} or {@link NODE_COLOR#ORANGE}.   
	 * 
	 * @param contextObject - the {@link SBase} where we want to check the {@link Annotation}.
	 * @return true if the SBML RDF block is recognized as valid or partially valid..
	 */
	private boolean isValidSBMLRDF(SBase contextObject) {
		
		if (!contextObject.isSetAnnotation()) {
			return false;
		}
		
		XMLNode annotationXMLNode = contextObject.getAnnotation().getAnnotationBuilder();	
		
		NODE_COLOR rdfColor = isValidRDF(annotationXMLNode); 
		
		return rdfColor.equals(NODE_COLOR.GREEN) || rdfColor.equals(NODE_COLOR.ORANGE);
	}

	/**
	 * Gets the first direct child element of the given {@link XMLNode} with the given local name and namespace.
	 * 
	 * <p>If <code>null</code> or an empty {@link String} is given for either the elementName or
	 * the elementURI, it is interpreted as any name or namespace. This is the same as passing the 
	 * special value "*".
	 * 
	 * @param xmlNode - the parent {@link XMLNode}
	 * @param elementName - The local name of the elements to match on. The special value "*" matches all local names.
	 * @param elementURI - The namespace URI of the elements to match on. The special value "*" matches all namespaces.
	 * @return the first direct child of the given {@link XMLNode} with the given local name and namespace.
	 */
	private XMLNode getChildElement(XMLNode xmlNode, String elementName, String elementURI) 
	{
		// checking the inputs
		if (xmlNode == null) {
			return null;
		}
		if (elementName == null || elementName.trim().length() == 0) {
			elementName = "*";
		}
		if (elementURI == null || elementURI.trim().length() == 0) {
			elementURI = "*";
		}
		
		for (int i = 0; i < xmlNode.getChildCount(); i++) {
			XMLNode child = xmlNode.getChildAt(i); 

			if (child.isElement() 
					&& (child.getName().equals(elementName) || elementName.equals("*"))
					&& (elementURI.equals("*") || elementURI.equals(child.getURI())))
			{
				return child;
			}
			
		}
		
		return null;
	}

	/**
	 * Gets all the direct children of the given {@link XMLNode} with the given local name and namespace.
	 * 
	 * <p>If <code>null</code> or an empty {@link String} is given for either the elementName or
	 * the elementURI, it is interpreted as any name or namespace. This is the same as passing the 
	 * special value "*".
	 * 
	 * @param xmlNode - the parent {@link XMLNode}
	 * @param elementName - The local name of the elements to match on. The special value "*" matches all local names.
	 * @param elementURI - The namespace URI of the elements to match on. The special value "*" matches all namespaces.
	 * @return all the direct children of the given {@link XMLNode} with the given local name and namespace.
	 */
	private List<XMLNode> getChildElements(XMLNode xmlNode, String elementName, String elementURI) 
	{
		// checking the inputs
		if (xmlNode == null) {
			return null;
		}
		if (elementName == null || elementName.trim().length() == 0) {
			elementName = "*";
		}
		if (elementURI == null || elementURI.trim().length() == 0) {
			elementURI = "*";
		}

		List<XMLNode> foundNodes = new ArrayList<XMLNode>();
		
		for (int i = 0; i < xmlNode.getChildCount(); i++) {
			XMLNode child = xmlNode.getChildAt(i); 
			
			if (child.isElement() 
					&& (child.getName().equals(elementName) || elementName.equals("*")) 
					&& (elementURI.equals(child.getURI()) || elementURI.equals("*")))
			{
				foundNodes.add(child);
			}
		}
		
		return foundNodes;
	}

	
	/**
	 * Returns a {@link NODE_COLOR} corresponding to the validity of the RDF block
	 * compared to the SBML specifications.
	 * 
	 * @param annotationXMLNode - the top level {@link XMLNode} from the {@link Annotation}.
	 * @return a {@link NODE_COLOR} corresponding to the validity of the RDF block.
	 */
	private NODE_COLOR isValidRDF(XMLNode annotationXMLNode) {

		if (annotationXMLNode == null) {
			return NODE_COLOR.WHITE;
		}
		
		XMLNode rdfNode = getChildElement(annotationXMLNode, "RDF", Annotation.URI_RDF_SYNTAX_NS);
		
		return isValidRDFDescription(rdfNode);
	}


	/**
	 * Returns a {@link NODE_COLOR} corresponding to the validity of the RDF Description element
	 * compared to the SBML specifications.
	 * 
	 * @param rdfNode - the rdf:RDF {@link XMLNode} from the {@link Annotation}.
	 * @return a {@link NODE_COLOR} corresponding to the validity of the RDF Description element.
	 */
	private NODE_COLOR isValidRDFDescription(XMLNode rdfNode) {

		if (rdfNode == null) {
			return NODE_COLOR.WHITE;
		}
		
		NODE_COLOR rdfNodeColor = null;
		List<XMLNode> descriptionNodes = getChildElements(rdfNode, "Description", Annotation.URI_RDF_SYNTAX_NS);
		
		if (descriptionNodes == null || descriptionNodes.size() == 0) {
			rdfNodeColor = NODE_COLOR.WHITE;
		} else  {
			XMLNode firstDescriptionNode = descriptionNodes.get(0);
			NODE_COLOR urisColor = isValidRDFURIs(firstDescriptionNode);
			NODE_COLOR historyColor = isValidRDFHistory(firstDescriptionNode); 
			
			rdfNodeColor = addColor(urisColor, historyColor);
			firstDescriptionNode.putUserObject(RDF_NODE_COLOR, rdfNodeColor);
			
			if (descriptionNodes.size() > 1 && (! rdfNodeColor.equals(NODE_COLOR.WHITE)))
			{
				rdfNodeColor = NODE_COLOR.ORANGE;

				// setting the color of the remaining Descriptions node to WHITE
				for (int i = 1; i < descriptionNodes.size(); i++) 
				{
					descriptionNodes.get(i).putUserObject(RDF_NODE_COLOR, NODE_COLOR.WHITE);
				}
			} 
			else if (! rdfNodeColor.equals(NODE_COLOR.WHITE)) 
			{
				// check if there are anything else, elements or attributes ??
			}			
		}
		
		rdfNode.putUserObject(RDF_NODE_COLOR, rdfNodeColor);
		
		return rdfNodeColor;
	}

	/**
	 * Adds the given colors and return the resulting color. 
	 * 
	 * @param color1 - the first {@link NODE_COLOR}
	 * @param color2 - the second {@link NODE_COLOR}
	 * @return the resulting color.
	 */
	private NODE_COLOR addColor(NODE_COLOR color1, NODE_COLOR color2) {

		if (color1.equals(color2)) {
			return color1;
		}
		
		return NODE_COLOR.ORANGE;
	}

	/**
	 * Returns a {@link NODE_COLOR} corresponding to the validity of the {@link History} elements
	 * compared to the SBML specifications.
	 *
	 * @param rdfNode - the rdf:RDF {@link XMLNode} from the {@link Annotation}.
	 * @return a {@link NODE_COLOR} corresponding to the validity of the {@link History} elements.
	 */
	private NODE_COLOR isValidRDFHistory(XMLNode xmlNode) {

		if (xmlNode == null) {
			return NODE_COLOR.WHITE;
		}

		NODE_COLOR creatorNodeColor = isValidCreator(getChildElement(xmlNode, "creator", JSBML.URI_PURL_ELEMENTS));
		NODE_COLOR createdNodeColor = isValidCreatedDate(getChildElement(xmlNode, "created", JSBML.URI_PURL_TERMS));
		NODE_COLOR modifiedNodesColor = areValidModifiedDates(getChildElements(xmlNode, "modified", JSBML.URI_PURL_TERMS));
		
		return addColor(createdNodeColor, addColor(creatorNodeColor, modifiedNodesColor)); 
	}
	

	/**
	 * Returns a {@link NODE_COLOR} corresponding to the validity of the {@link Creator} elements
	 * compared to the SBML specifications.
	 *
	 * @param creatorNode - the dc:Creator {@link XMLNode} from the RDF block.
	 * @return a {@link NODE_COLOR} corresponding to the validity of the {@link Creator} elements.
	 */
	private NODE_COLOR isValidCreator(XMLNode creatorNode) {

		if (creatorNode == null) {
			return NODE_COLOR.WHITE;
		}
		
		// dc:creator->rdf:Bag->rdf:li*->VCard:stuff* 
		NODE_COLOR wholeColor = NODE_COLOR.GREEN;
		XMLNode bagNode = getChildElement(creatorNode, "Bag", Annotation.URI_RDF_SYNTAX_NS);
		
		if (bagNode != null) {
			List<XMLNode> liNodes = getChildElements(bagNode, "li", Annotation.URI_RDF_SYNTAX_NS);
			
			if (liNodes != null && liNodes.size() > 0) {
				
				for (XMLNode liNode : liNodes) 
				{
					NODE_COLOR color = isValidVCard(liNode);
					wholeColor = addColor(wholeColor, color);
				}
			}
		}

		return wholeColor;
	}

	
	/**
	 * Returns {@link NODE_COLOR#GREEN} if at least one of these three nodes is present: vCard:N, vCard:EMAIL or vCard:ORG,
	 * meaning that we consider that the VCard is at least partially defined.
	 * 
	 * <p> The color is never {@link NODE_COLOR#ORANGE} as we do not check at the moment for any additional elements
	 * or attributes or for the proper structure of the vCard:N or vCard:ORG elements.
	 * The reading and writing should work fine whatever the content of the rdf:li element. 
	 * 
	 * @param xmlNode - the rdf:li {@link XMLNode} from the dc:creator block.
	 * @return {@link NODE_COLOR#GREEN} if at least one of these three nodes is present: vCard:N, vCard:EMAIL or vCard:ORG.
	 */
	private NODE_COLOR isValidVCard(XMLNode xmlNode) {

		if (xmlNode == null) {
			return NODE_COLOR.WHITE;
		}
		
		// nothing really mandatory there !! 
		
		// vCard:N->vCard:Family|vCard:given  vCard:EMAIL   vCard:ORG->vCard:Orgname
		XMLNode nameNode = getChildElement(xmlNode, "N", Creator.URI_RDF_VCARD_NS);
		XMLNode emailNode = getChildElement(xmlNode, "EMAIL", Creator.URI_RDF_VCARD_NS);
		XMLNode orgNode = getChildElement(xmlNode, "ORG", Creator.URI_RDF_VCARD_NS);
		
		// at least one of these three nodes is present
		if (nameNode != null || emailNode != null || orgNode != null) {

			// check each elements for additional stuff and set colors ??
			// Not needed at the moment.
			
			xmlNode.putUserObject(RDF_NODE_COLOR, NODE_COLOR.GREEN);
			
			return NODE_COLOR.GREEN;
		}
		
		xmlNode.putUserObject(RDF_NODE_COLOR, NODE_COLOR.WHITE);
		
		return NODE_COLOR.WHITE;
	}

	/**
	 * Returns a {@link NODE_COLOR} corresponding to the validity of the modified {@link Date}s.
	 * 
	 * <p> The color of one dcterms:modified element is never {@link NODE_COLOR#ORANGE} as we do not check at 
	 * the moment for any additional elements or attributes. If the dcterms:modified element contain the dcterms:W3CDTF
	 * child element then it's color is {@link NODE_COLOR#GREEN}, otherwise it is {@link NODE_COLOR#WHITE}. 
	 * The reading and writing should work fine whatever the content of the dcterms:modified elements. 
	 * 
	 * @param modifiedNodes - the dcterms:modified {@link XMLNode}s from the rdf:Description block.
	 * @return a {@link NODE_COLOR} corresponding to the validity of the modified {@link Date}s.
	 */
	private NODE_COLOR areValidModifiedDates(List<XMLNode> modifiedNodes) {

		if (modifiedNodes == null) {
			return NODE_COLOR.WHITE;
		}

		// dcterms:modified ---> dcterms:W3CDTF
		NODE_COLOR color = NODE_COLOR.GREEN;
		
		for (XMLNode xmlNode : modifiedNodes) {

			XMLNode w3cdtfNode = getChildElement(xmlNode, "W3CDTF", JSBML.URI_PURL_TERMS);

			if (w3cdtfNode != null) {
				// we found at least one modified date that look fine.
				xmlNode.putUserObject(RDF_NODE_COLOR, NODE_COLOR.GREEN);
				color = addColor(color, NODE_COLOR.GREEN);

				// check that it does not contain additional elements or attributes  for both modified and W3CDTF nodes ??
				
			} else {
				color = addColor(color, NODE_COLOR.WHITE);
				xmlNode.putUserObject(RDF_NODE_COLOR, NODE_COLOR.WHITE);
			}
			
		}
		
		return color;
	}

	
	/**
	 * Returns a {@link NODE_COLOR} corresponding to the validity of the created {@link Date}.
	 * 
	 * <p> The color of the dcterms:created element is never {@link NODE_COLOR#ORANGE} as we do not check at 
	 * the moment for any additional elements or attributes. If the dcterms:created element contain the dcterms:W3CDTF
	 * child element then it's color is {@link NODE_COLOR#GREEN}, otherwise it is {@link NODE_COLOR#WHITE}. 
	 * The reading and writing should work fine whatever the content of the dcterms:created element. 
	 * 
	 * @param createdNode - the dcterms:created {@link XMLNode} from the rdf:Description block.
	 * @return a {@link NODE_COLOR} corresponding to the validity of the created {@link Date}
	 */
	private NODE_COLOR isValidCreatedDate(XMLNode createdNode) {

		if (createdNode == null) {
			return NODE_COLOR.WHITE;
		}

		// dcterms:created ---> dcterms:W3CDTF

		XMLNode w3cdtfNode = getChildElement(createdNode, "W3CDTF", JSBML.URI_PURL_TERMS);
		NODE_COLOR color = NODE_COLOR.WHITE;
		
		if (w3cdtfNode != null) {
			color = NODE_COLOR.GREEN;
			createdNode.putUserObject(RDF_NODE_COLOR, NODE_COLOR.GREEN);
			// check for additional attributes and elements for both created and W3CDTF nodes ??	
			
		}
		else
		{
			createdNode.putUserObject(RDF_NODE_COLOR, NODE_COLOR.WHITE);
		}
		
		return color;
	}

	
	/**
	 * Returns a {@link NODE_COLOR} corresponding to the validity of the first bqmodel and bqbiol {@link CVTerm}.
	 * 
	 * <p> The color of the relation elements is never {@link NODE_COLOR#ORANGE} as we do not check at 
	 * the moment for any additional elements or attributes. If the bqmodel:* or bqbiol:* elements 
	 * contain a rdf:Bag that contain at least one rdf:li, then we consider the XMLNode to be
	 * at least partially valid and return {@link NODE_COLOR#GREEN}. Otherwise we return {@link NODE_COLOR#WHITE}. 
	 * The reading and writing should work fine whatever the content of the bqmodel:* or bqbiol:* element. 
	 * 
	 * @param rdfNode  - the rdf:Description {@link XMLNode} from the RDF block.
	 * @return a {@link NODE_COLOR} corresponding to the validity of the first bqmodel and bqbiol {@link CVTerm}.
	 */
	private NODE_COLOR isValidRDFURIs(XMLNode rdfNode) {

		if (rdfNode == null) {
			return NODE_COLOR.WHITE;
		}

		// Should we go over all the bqmodel/bqbiol nodes ?
		
		// XML structure bqmodel/bqbiol:qualifier->rdf:Bag->rdf:li*

		// get the first bqmodel:* and bqbiol:* children 
		XMLNode bqmodelNode = getChildElement(rdfNode, "*", CVTerm.URI_BIOMODELS_NET_BIOLOGY_QUALIFIERS);
		XMLNode bqbiolNode = getChildElement(rdfNode, "*", CVTerm.URI_BIOMODELS_NET_MODEL_QUALIFIERS);

		if (bqmodelNode != null) 
		{
			XMLNode bagNode = getChildElement(bqmodelNode, "Bag", Annotation.URI_RDF_SYNTAX_NS);
			
			if (bagNode != null) {
				List<XMLNode> liNodes = getChildElements(bagNode, "li", Annotation.URI_RDF_SYNTAX_NS);
				
				if (liNodes != null && liNodes.size() > 0) {
					// found a likely valid annotation
					return NODE_COLOR.GREEN;
				}
			}
			
		}

		if (bqbiolNode != null) 
		{
			XMLNode bagNode = getChildElement(bqbiolNode, "Bag", Annotation.URI_RDF_SYNTAX_NS);
			
			if (bagNode != null) {
				List<XMLNode> liNodes = getChildElements(bagNode, "li", Annotation.URI_RDF_SYNTAX_NS);
				
				if (liNodes != null && liNodes.size() > 0) {
					// found a likely valid annotation
					return NODE_COLOR.GREEN;
				}
			}
		}
		
		return NODE_COLOR.WHITE;
	}



	/**
	 * Reads the 'rdf:RDF' block, if present in the {@link Annotation}, to extract the {@link History} and the 
	 * {@link CVTerm}(s) as defined in the SBML specifications, section 6: 'A standard format for the annotation element'.
	 * 
	 * <p>This method will remove, from the XMLNode tree, all known elements and attributes. Any additional attributes or
	 * elements inside the RDF block are stored as a userObject with the key {@link #CUSTOM_RDF}.
	 * <br>Different parts of the XMLNode tree are stored in different objects. The additional XML from dc:creator/rdf:Bag/rdf:li
	 * are stored in each corresponding {@link Creator}. The additional XML from any of the relation elements (bqmodel:* and bqbiol:*)
	 * are stored in each corresponding {@link CVTerm}. All the rest is stored on the {@link Annotation} object. 
	 *  
	 * @param contextObject the {@link SBase} in which we will store the {@link History} and the {@link CVTerm}(s).
	 */
	private void readSBMLRDF(SBase contextObject) 
	{
		logger.debug("readSBMLRDF called");

		// This method should be called only if isValidSBMLRDF(SBase) return true		
		
		XMLNode annotationXMLNode = contextObject.getAnnotation().getAnnotationBuilder();	
		XMLNode rdfNode = getChildElement(annotationXMLNode, "RDF", Annotation.URI_RDF_SYNTAX_NS);

		if (rdfNode == null || rdfNode.getUserObject(RDF_NODE_COLOR) == null 
				|| rdfNode.getUserObject(RDF_NODE_COLOR).equals(NODE_COLOR.WHITE)) 
		{
			// print message that isValidSBMLRDF should be called beforehand
			System.out.println("readSBMLRDF - isValidSBMLRDF should be called beforehand !!! Won't try to extract the standard annotation.");
			return;
		}

		// From here, we assume that the RDF is defined and more or less well written.

		List<XMLNode> descriptionNodes = getChildElements(rdfNode, "Description", Annotation.URI_RDF_SYNTAX_NS);
		XMLNode descriptionNode = descriptionNodes.get(0);
		
		readRDFURIs(contextObject, descriptionNode);
		readRDFHistory(contextObject, descriptionNode); 

		contextObject.getAnnotation().setAbout(descriptionNode.getAttrValue("about", Annotation.URI_RDF_SYNTAX_NS));
		
		descriptionNode.removeAttr("about", Annotation.URI_RDF_SYNTAX_NS);
		removeXmlNodeIfEmpty(descriptionNode);
		
		// removing the usual namespace declarations
		rdfNode.removeNamespace("rdf");
		rdfNode.removeNamespace("dc");
		rdfNode.removeNamespace("dcterms");
		rdfNode.removeNamespace("vcard");
		rdfNode.removeNamespace("vCard");
		rdfNode.removeNamespace("bqbiol");
		rdfNode.removeNamespace("bqmodel");
		// boolean rdfNodeRemoved = 
		removeXmlNodeIfEmpty(rdfNode);
		
		/*
		if (rdfNodeRemoved)
		{
			// removing white space
			int nbChildElements = annotationXMLNode.getChildCount();
		
			for (int i = nbChildElements - 1; i >= 0; i--)
			{
				XMLNode childElement = annotationXMLNode.getChildAt(i);

				if (childElement.isText() && childElement.getCharacters().trim().length() == 0)
				{
					annotationXMLNode.removeChild(i); // - remove it only if the node before was also empty ?
				}
			}
			if (annotationXMLNode.getChildCount() == 0)
			{
				annotationXMLNode.removeFromParent();
			}
		} // we might need to clean a bit the white space even if the RDF node is not removed
		*/
	}

	/**
	 * Reads the 'rdf:Description' to extract the {@link History} part of the SBML RDF annotation.
	 * 
	 * @param contextSBase the {@link SBase} in which we will store the {@link History}.
	 * @param descriptionNode the 'rdf:Description' {@link XMLNode} that contain the definition
	 *  of the created and modified dates and the creators.
	 */
	private void readRDFHistory(SBase contextSBase, XMLNode descriptionNode) 
	{
		logger.debug("readRDFHistory - called");
		
		// get the dc:creator children 
		List<XMLNode> creatorNodes = getChildElements(descriptionNode, "creator", JSBML.URI_PURL_ELEMENTS);

		if (creatorNodes != null && creatorNodes.size() > 0)
		{
			for (XMLNode creator : creatorNodes)
			{
				readCreator(contextSBase, creator);
			}
		}
		
		// getting the dates children
		List<XMLNode> createdNodes = getChildElements(descriptionNode, "created", JSBML.URI_PURL_TERMS);
		List<XMLNode> modifiedNodes = getChildElements(descriptionNode, "modified", JSBML.URI_PURL_TERMS);

		if (createdNodes != null && createdNodes.size() == 1)
		{
			for (XMLNode createdNode : createdNodes)
			{
				readCreatedDate(contextSBase, createdNode);
			}		
		}
		
		if (modifiedNodes != null && modifiedNodes.size() > 0)
		{
			for (XMLNode modifiedNode : modifiedNodes)
			{
				readModifiedDate(contextSBase, modifiedNode);
			}				
		}
	}

	
	
	
	/**
	 * Reads the 'dcterms:modified' part of the SBML RDF annotation into java {@link Date}(s).
	 * 
	 * @param contextSBase the {@link SBase} in which we will add the modified {@link Date}(s).
	 * @param modifiedNode the 'dcterms:modified' {@link XMLNode} that contain the definition of the modified dates.
	 */
	private void readModifiedDate(SBase contextSBase, XMLNode modifiedNode)
	{
		if (modifiedNode == null || NODE_COLOR.WHITE.equals(modifiedNode.getUserObject(RDF_NODE_COLOR)))
		{
			return;
		}
		
		List<XMLNode> w3cdtfNodes = getChildElements(modifiedNode, "W3CDTF", JSBML.URI_PURL_TERMS);
		
		if (w3cdtfNodes != null && w3cdtfNodes.size() == 1)
		{
			XMLNode w3cdtfNode = w3cdtfNodes.get(0);

			int textNodeIndex = findFirstNonEmptyTextElementIndex(w3cdtfNode);

			if (textNodeIndex == -1) {
				return;
			}

			String w3cdtfDate = w3cdtfNode.getChildAt(textNodeIndex).getCharacters().trim();

			logger.debug("readModified - modified date = " + w3cdtfDate);

			try 
			{
				contextSBase.getHistory().addModifiedDate(DateParser.parse(w3cdtfDate));
				
				// if no more children or attributes, remove the XMLNode from the tree				
				w3cdtfNode.removeChild(textNodeIndex);
				removeXmlNodeIfEmpty(w3cdtfNode);
				// remove rdf:parseType attribute
				modifiedNode.removeAttr("parseType");
				removeXmlNodeIfEmpty(modifiedNode);
			}
			catch (InvalidDateException e) 
			{
				logger.warn(MessageFormat.format("Could not interpret the String ''{0}'' as a Date !!", w3cdtfDate));
				if (logger.isDebugEnabled()) {
					e.printStackTrace();
				}
			}
			catch (RuntimeException e) 
			{
				logger.warn(MessageFormat.format("Could not interpret the String ''{0}'' as a Date !!", w3cdtfDate));
				if (logger.isDebugEnabled()) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * Return the first child element of type text that is not empty. 
	 * 
	 * @param xmlNode
	 * @return the first child element of type text that is not empty.
	 */
	private int findFirstNonEmptyTextElementIndex(XMLNode xmlNode) 
	{
		int index = -1;
		int firstTextElementIndex = -1;
		
		if (xmlNode != null && xmlNode.getChildCount() > 0) 
		{
			for (int i = 0; i < xmlNode.getChildCount(); i++) 
			{
				XMLNode child = xmlNode.getChildAt(i);
				
				if (child.isText())
				{
					if (firstTextElementIndex == -1)
					{
						firstTextElementIndex = i;
					}
					
					if (child.getCharacters().trim().length() > 0)
					{
						index = i;
						break;
					}
				}					
			}
		}
			
		if (index == -1)
		{
			// in case the characters we are looking for are only spaces
			return firstTextElementIndex;
		}
		
		return index;
	}

	/**
	 * Reads the 'dcterms:created' part of the SBML RDF annotation into a java {@link Date}.
	 * 
	 * @param contextSBase the {@link SBase} in which we will add the created {@link Date}.
	 * @param createdNode the 'dcterms:created' {@link XMLNode} that contain the definition of the created date.
	 */
	private void readCreatedDate(SBase contextSBase, XMLNode createdNode) 
	{
		if (createdNode == null || NODE_COLOR.WHITE.equals(createdNode.getUserObject(RDF_NODE_COLOR)))
		{
			return;
		}

		List<XMLNode> w3cdtfNodes = getChildElements(createdNode, "W3CDTF", JSBML.URI_PURL_TERMS);
		
		if (w3cdtfNodes != null && w3cdtfNodes.size() == 1)
		{
			XMLNode w3cdtfNode = w3cdtfNodes.get(0);
			
			 int textNodeIndex = findFirstNonEmptyTextElementIndex(w3cdtfNode);

			 if (textNodeIndex == -1) {
				 return;
			 }
			 
			 String w3cdtfDate = w3cdtfNode.getChildAt(textNodeIndex).getCharacters().trim();
			
			logger.debug("readCreated - created date = " + w3cdtfDate);

			try 
			{
				contextSBase.getHistory().setCreatedDate(DateParser.parse(w3cdtfDate));
				logger.debug("readCreated - created java date = " + contextSBase.getHistory().getCreatedDate());

				// if no more children or attributes, remove the XMLNode from the tree				
				w3cdtfNode.removeChild(textNodeIndex);
				removeXmlNodeIfEmpty(w3cdtfNode);
				// remove rdf:parseType attribute
				createdNode.removeAttr("parseType");
				removeXmlNodeIfEmpty(createdNode);
			}
			catch (InvalidDateException e) 
			{
				logger.warn(MessageFormat.format("Could not interpret the String ''{0}'' as a Date !!", w3cdtfDate));
				if (logger.isDebugEnabled()) {
					e.printStackTrace();
				}
			}
			catch (RuntimeException e) 
			{
				logger.warn(MessageFormat.format("Could not interpret the String ''{0}'' as a Date !!", w3cdtfDate));
				if (logger.isDebugEnabled()) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Reads the 'dc:creator' part of the SBML RDF annotation into {@link Creator}s.
	 * 
	 * @param contextSBase the {@link SBase} in which we will add the {@link Creator}s.
	 * @param creatorNode the 'dc:creator' {@link XMLNode} that contain the definition of the creator vCards.
	 */
	private void readCreator(SBase contextSBase, XMLNode creatorNode) 
	{
		logger.debug("readCreator called");
		
		XMLNode bagNode = getChildElement(creatorNode, "Bag", Annotation.URI_RDF_SYNTAX_NS);
		List<XMLNode> liNodes = getChildElements(bagNode, "li", Annotation.URI_RDF_SYNTAX_NS);

		for (XMLNode liNode : liNodes) 
		{
			if (NODE_COLOR.WHITE.equals(liNode.getUserObject(RDF_NODE_COLOR)))
			{
				continue;
			}
			
			Creator creator = new Creator();
			contextSBase.getHistory().addCreator(creator);

			// get name information
			List<XMLNode> nameNodes = getChildElements(liNode, "N", Creator.URI_RDF_VCARD_NS);

			if (nameNodes != null && nameNodes.size() == 1)
			{
				XMLNode nameNode = nameNodes.get(0);

				// get family name information
				List<XMLNode> famillyNameNodes = getChildElements(nameNode, "Family", Creator.URI_RDF_VCARD_NS);

				if (famillyNameNodes != null & famillyNameNodes.size() == 1)
				{
					XMLNode famillyNode = famillyNameNodes.get(0);

					int textNodeIndex = findFirstNonEmptyTextElementIndex(famillyNode);
					
					if (textNodeIndex != -1)
					{
						String familyName = famillyNode.getChildAt(textNodeIndex).getCharacters();

						logger.debug("readCreator - family name = " + familyName);
						creator.setFamilyName(familyName);

						famillyNode.removeChild(textNodeIndex);
						removeXmlNodeIfEmpty(famillyNode);
					}
				}
				else
				{
					logger.debug("readCreator - did not found one and only one 'Family' node: " + famillyNameNodes);
				}
				
				// get first name information
				List<XMLNode> firstNameNodes = getChildElements(nameNode, "Given", Creator.URI_RDF_VCARD_NS);

				if (firstNameNodes != null & firstNameNodes.size() == 1)
				{
					XMLNode firstNameNode = firstNameNodes.get(0);

					int textNodeIndex = findFirstNonEmptyTextElementIndex(firstNameNode);

					if (textNodeIndex != -1)
					{
						String firstName = firstNameNode.getChildAt(textNodeIndex).getCharacters();

						logger.debug("readCreator - first name = " + firstName);
						creator.setGivenName(firstName);

						firstNameNode.removeChild(textNodeIndex);
						removeXmlNodeIfEmpty(firstNameNode);
					}
				}
				else
				{
					logger.debug("readCreator - did not found one and only one 'Given' node: " + firstNameNodes);
				}
				
				nameNode.removeAttr("parseType");
				removeXmlNodeIfEmpty(nameNode);
			}
			else 
			{
				logger.debug("readCreator - did not found one and only one 'N' node: " + nameNodes);
			}
			
			// get email information
			List<XMLNode> emailNodes = getChildElements(liNode, "EMAIL", Creator.URI_RDF_VCARD_NS);

			if (emailNodes != null && emailNodes.size() == 1)
			{
				XMLNode firstNode = emailNodes.get(0);

				int textNodeIndex = findFirstNonEmptyTextElementIndex(firstNode);

				if (textNodeIndex != -1)
				{
					String email = firstNode.getChildAt(textNodeIndex).getCharacters();

					logger.debug("readCreator - email = " + email);
					creator.setEmail(email);

					firstNode.removeChild(textNodeIndex);
					removeXmlNodeIfEmpty(firstNode);
				}
			}
			else 
			{
				logger.debug("readCreator - did not found one and only one 'EMAIL' node : " + emailNodes);
			}
			
			// get organization information
			List<XMLNode> orgNodes = getChildElements(liNode, "ORG", Creator.URI_RDF_VCARD_NS); 

			if (orgNodes != null && orgNodes.size() == 1)
			{
				XMLNode firstNode = orgNodes.get(0);

				// get Organization name information
				List<XMLNode> orgNameNodes = getChildElements(firstNode, "Orgname", Creator.URI_RDF_VCARD_NS);

				if (orgNameNodes != null & orgNameNodes.size() == 1)
				{
					XMLNode orgNameNode = orgNameNodes.get(0);

					int textNodeIndex = findFirstNonEmptyTextElementIndex(orgNameNode);

					if (textNodeIndex != -1)
					{
						String orgName = orgNameNode.getChildAt(textNodeIndex).getCharacters();

						logger.debug("readCreator - orgname = " + orgName);
						creator.setOrganisation(orgName);

						orgNameNode.removeChild(textNodeIndex);
						removeXmlNodeIfEmpty(orgNameNode);
					}
				}
				else
				{
					logger.debug("readCreator - did not found one and only one 'Orgname' node : " + orgNameNodes);
				}
				
				firstNode.removeAttr("parseType");
				removeXmlNodeIfEmpty(firstNode);
			}
			else 
			{
				logger.debug("readCreator - did not found one and only one 'ORG' node : " + orgNodes);
			}
		
			liNode.removeAttr("parseType");
			boolean nodeRemoved = removeXmlNodeIfEmpty(liNode);
			
			if (!nodeRemoved)
			{
				// remove and store the 'li' XMLNode to be able to keep any additional information part of the SBML specifications or not.
				liNode.removeFromParent();
				creator.putUserObject(CUSTOM_RDF, liNode);
			}
		}
		
		removeXmlNodeIfEmpty(bagNode);
		removeXmlNodeIfEmpty(creatorNode);
	}

	
	/**
	 * Reads the relation elements part of the SBML RDF annotation into {@link CVTerm}s.
	 * 
	 * @param contextSBase the {@link SBase} in which we will add the created {@link CVTerm}s.
	 * @param descriptionNode the rdf:Description {@link XMLNode} that contain the qualifiers and URIs.
	 */
	private void readRDFURIs(SBase contextSBase, XMLNode descriptionNode) {
		
		logger.debug("readRDFURIs called");
		
		// get the first bqmodel:* and bqbiol:* children 
		List<XMLNode> bqmodelNodes = getChildElements(descriptionNode, "*", CVTerm.URI_BIOMODELS_NET_MODEL_QUALIFIERS);
		List<XMLNode> bqbiolNodes = getChildElements(descriptionNode, "*", CVTerm.URI_BIOMODELS_NET_BIOLOGY_QUALIFIERS);

		if (logger.isDebugEnabled()) 
		{
			logger.debug("readRDFURIs - nb bqmodel found = " + bqmodelNodes.size());
			logger.debug("readRDFURIs - nb bqbiol found = " + bqbiolNodes.size());
		}

		for (XMLNode bqmodelNode : bqmodelNodes) 
		{
			XMLNode bagNode = getChildElement(bqmodelNode, "Bag", Annotation.URI_RDF_SYNTAX_NS);
			List<XMLNode> liNodes = getChildElements(bagNode, "li", Annotation.URI_RDF_SYNTAX_NS);
			List<String> resources = new ArrayList<String>();

			if (liNodes == null)
			{
				// something is wrong, going directly to the next bqmodelNode
				if (logger.isDebugEnabled()) {
					logger.debug("Did not found any valid children for " + bqmodelNode);
				}
				continue;
			}
			
			for (XMLNode liNode : liNodes) 
			{
				int resourceAttrIndex = liNode.getAttrIndex("resource", Annotation.URI_RDF_SYNTAX_NS);
				
				if (resourceAttrIndex >= 0) 
				{
					resources.add(liNode.getAttrValue(resourceAttrIndex));
				}
				if (logger.isDebugEnabled()) 
				{
					for (int i = 0; i < liNode.getAttributesLength(); i++) 
					{
						System.out.println(liNode.getAttrName(i) + " = " + liNode.getAttrValue(i) + " (" + liNode.getAttrURI(i) + ")");
					}
				}
				
				liNode.removeAttr("resource", Annotation.URI_RDF_SYNTAX_NS);
				removeXmlNodeIfEmpty(liNode);
			}

			if (logger.isDebugEnabled()) 
			{
				logger.debug("readRDFURIs - qualifier name = " + bqmodelNode.getName() + "\nresources = " + resources);
			}
			
			CVTerm.Qualifier qualifier = CVTerm.Qualifier.getModelQualifierFor(bqmodelNode.getName());			
			CVTerm cvTerm = new CVTerm(CVTerm.Type.MODEL_QUALIFIER, qualifier, resources.toArray(new String[resources.size()]));
			contextSBase.addCVTerm(cvTerm);
			
			removeXmlNodeIfEmpty(bagNode);
			boolean nodeRemoved = removeXmlNodeIfEmpty(bqmodelNode);
			
			if (!nodeRemoved)
			{
				cvTerm.putUserObject(CUSTOM_RDF, bqmodelNode);
				bqmodelNode.removeFromParent();
			}
		}


		for (XMLNode bqbiolNode : bqbiolNodes) 
		{
			XMLNode bagNode = getChildElement(bqbiolNode, "Bag", Annotation.URI_RDF_SYNTAX_NS);
			List<XMLNode> liNodes = getChildElements(bagNode, "li", Annotation.URI_RDF_SYNTAX_NS);
			List<String> resources = new ArrayList<String>();

			if (liNodes == null)
			{
				// something is wrong, going directly to the next bqbiolNode 
				if (logger.isDebugEnabled()) {
					logger.debug("Did not found any valid children for " + bqbiolNode);
				}
				continue;
			}
			
			for (XMLNode liNode : liNodes) 
			{
				int resourceAttrIndex = liNode.getAttrIndex("resource", Annotation.URI_RDF_SYNTAX_NS);
				if (resourceAttrIndex >= 0) 
				{
					resources.add(liNode.getAttrValue(resourceAttrIndex));
				}

				liNode.removeAttr("resource", Annotation.URI_RDF_SYNTAX_NS);
				removeXmlNodeIfEmpty(liNode);
			}

			if (logger.isDebugEnabled()) 
			{
				logger.debug("readRDFURIs - qualifier name = " + bqbiolNode.getName());
			}
			
			CVTerm.Qualifier qualifier = CVTerm.Qualifier.getBiologicalQualifierFor(bqbiolNode.getName());			
			CVTerm cvTerm = new CVTerm(CVTerm.Type.BIOLOGICAL_QUALIFIER, qualifier, resources.toArray(new String[resources.size()]));
			contextSBase.addCVTerm(cvTerm);
			
			removeXmlNodeIfEmpty(bagNode);
			boolean nodeRemoved = removeXmlNodeIfEmpty(bqbiolNode);
			
			if (!nodeRemoved)
			{
				cvTerm.putUserObject(CUSTOM_RDF, bqbiolNode);
				bqbiolNode.removeFromParent();
			}
		}
	}

	/**
	 * Removes an {@link XMLNode} from its parent if it has no child  elements, no attributes
	 * and no namespaces declared any more.
	 * 
	 * @param xmlNode
	 * @return true if the node was removed.
	 */
	private boolean removeXmlNodeIfEmpty(XMLNode xmlNode) 
	{
		if (xmlNode == null)
		{
			return false;
		}
		
		int nbChildElements = xmlNode.getChildCount();
		int nbAttributes = xmlNode.getAttributesLength();
		int nbNamespaces = xmlNode.getNamespacesLength();

		if (nbChildElements > 0)
		{
			int nbRealChildElements = 0;
			
			for (int i = 0 ; i < nbChildElements; i++)
			{
				XMLNode childElement = xmlNode.getChildAt(i);

				if (childElement.isText() && childElement.getCharacters().trim().length() == 0)
				{
					continue;
				}
				nbRealChildElements++;
			}
			nbChildElements = nbRealChildElements;
		}
		
		if (nbChildElements > 0 || nbAttributes > 0 || nbNamespaces > 0)
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("removeXmlNodeIfEmpty - cannot remove - '" + xmlNode + "' " + nbChildElements + " children, "
						+ nbAttributes + " attributes, " + nbNamespaces + " namespaces.");
			}

			return false;
		}

		// if the node preceding the node to remove is empty text, removing it as well to try to preserve the formatting as much as possible
		int nodeIndex = xmlNode.getParent().getIndex(xmlNode);
		
		if (nodeIndex > 0)
		{
			XMLNode precedingChild = (XMLNode) xmlNode.getParent().getChildAt(nodeIndex - 1);
			
			if (precedingChild.isText() && precedingChild.getCharacters().trim().length() == 0)
			{
				((XMLNode) xmlNode.getParent()).removeChild(nodeIndex - 1);
			}
		}
		
		return ((XMLNode) xmlNode.getParent()).removeChild(nodeIndex - 1) != null;
		
	}

	/**
	 * @param contextObject
	 */
	private void writeSBMLRDF(SBase contextObject) 
	{
		// 
		if ((contextObject == null) || (!contextObject.isSetAnnotation()))
		{
			return;
		}
		// TODO - make use of the potential XML stores using the CUSTOM_RDF user object  
		
		// TODO : add a created date or modified date automatically ??
		
		// gets or creates the RDF and Description XMLNode
		
		XMLNode annotationXMLNode = contextObject.getAnnotation().getAnnotationBuilder();
		XMLNode rdfNode = getOrCreate(annotationXMLNode, "RDF", Annotation.URI_RDF_SYNTAX_NS, "rdf");
		// TODO - write only the needed namespaces
		rdfNode.addNamespace(Annotation.URI_RDF_SYNTAX_NS, "rdf");
		rdfNode.addNamespace(JSBML.URI_PURL_ELEMENTS, "dc");
		rdfNode.addNamespace(JSBML.URI_PURL_TERMS, "dcterms");
		rdfNode.addNamespace(Creator.URI_RDF_VCARD_NS, "vCard");
		rdfNode.addNamespace(CVTerm.URI_BIOMODELS_NET_BIOLOGY_QUALIFIERS, "bqbiol");
		rdfNode.addNamespace(CVTerm.URI_BIOMODELS_NET_MODEL_QUALIFIERS, "bqmodel");
		
		XMLNode descriptionNode = getOrCreateDescription(rdfNode);
		
		// check if the rdf:about value is correct
		checkAbout(contextObject);
		descriptionNode.addAttr("about", contextObject.getAnnotation().getAbout(), Annotation.URI_RDF_SYNTAX_NS, "rdf");
		
		writeHistory(contextObject, descriptionNode);
		writeURIs(contextObject, descriptionNode);
	}

	
	/**
	 * Checks and Sets about {@link String}, that correspond to the value of the 
	 * rdf:about attribute.
	 * 
	 * <p>A metaid is created and set if it was not set before.
	 * 
	 * @param contextObject
	 */
	private void checkAbout(SBase contextObject)
	{
		String about = contextObject.getAnnotation().getAbout();
		String metaid = null;
		
		if (contextObject.isSetMetaId())
		{
			metaid = contextObject.getMetaId(); 
		}
		else if (contextObject.getLevel() > 1)
		{
			SBMLDocument doc = contextObject.getSBMLDocument();
			contextObject.setMetaId(doc != null ? doc.nextMetaId() : null);
			metaid = contextObject.getMetaId();
		}
		else
		{
			return;
		}
		
		String expectedRDFAbout = "#" + metaid;
		
		if (about == null || (! about.equals(expectedRDFAbout)))
		{
			contextObject.getAnnotation().setAbout(expectedRDFAbout);
		}
	}

	/**
	 * 
	 * 
	 * @param annotationXMLNode
	 * @return
	 */
	private XMLNode getOrCreateDescription(XMLNode rdfNode) 
	{
		if (rdfNode == null)
		{
			throw new IllegalArgumentException("The RDF XMLNode cannot be null !!");
		}
		
		List<XMLNode> descriptionNodes = getChildElements(rdfNode, "Description", Annotation.URI_RDF_SYNTAX_NS);
		XMLNode descriptionNode = null;
		
		if (descriptionNodes != null && descriptionNodes.size() > 0) 
		{
			descriptionNode = descriptionNodes.get(0);
			
			if (descriptionNode.getUserObject(RDF_NODE_COLOR) == null
					|| descriptionNode.getUserObject(RDF_NODE_COLOR).equals(NODE_COLOR.WHITE))
			{
				descriptionNode = null;
			}
		}

		if (descriptionNode == null)
		{
			int index = 0;
			
			if (rdfNode.getChildCount() == 0)
			{
				rdfNode.addChild(new XMLNode("\n\t"));
				index++;
			} 
//			else if (rdfNode.getChildAt(0)) TODO check that there is a first child that is empty text, including \n
//			{
//				
//			}

			descriptionNode = rdfNode.insertChild(index, new XMLNode(new XMLTriple("Description", Annotation.URI_RDF_SYNTAX_NS, "rdf"), new XMLAttributes()));
			rdfNode.insertChild(index + 1, new XMLNode("\n\t"));
		}

		return descriptionNode;
	}

	/**
	 * 
	 * 
	 * @param parent
	 * @param elementName
	 * @param elementNamespaceURI
	 * @param prefix
	 * @return
	 */
	private XMLNode getOrCreate(XMLNode parent, String elementName, String elementNamespaceURI, String prefix) 
	{
		// logger.info("getOrCreate called");
		XMLNode child = getChildElement(parent, elementName, elementNamespaceURI);
				
		if (child == null)
		{
			child = new XMLNode(new XMLTriple(elementName, elementNamespaceURI, prefix), new XMLAttributes());
			
			if (parent.getChildCount() == 0)
			{
				parent.addChild(new XMLNode("\n\t"));
			}

			parent.addChild(child);
			parent.addChild(new XMLNode("\n\t"));
		}
		return child;
	}

	
	/**
	 * 
	 * @param parent
	 * @param elementName
	 * @param elementNamespaceURI
	 * @param prefix
	 * @param precedingElementName
	 * @param precedingElementNamespaceURI
	 * @return
	 */
	private XMLNode getOrCreateAfter(XMLNode parent, String elementName,
			String elementNamespaceURI, String prefix, String precedingElementName,
			String precedingElementNamespaceURI)
	{
		// logger.info("getOrCreateAfter called");
		XMLNode child = getChildElement(parent, elementName, elementNamespaceURI);
				
		if (child == null)
		{
			int trueIndex = getLastIndexOf(parent, precedingElementName, precedingElementNamespaceURI);
			
			child = new XMLNode(new XMLTriple(elementName, elementNamespaceURI, prefix), new XMLAttributes());
			
			if (parent.getChildCount() == 0)
			{
				parent.addChild(new XMLNode("\n\t"));
			}

			// System.out.println("getOrCreateAfter - trueIndex = " + trueIndex);			
			
			if (trueIndex == -1)
			{
				parent.addChild(child);
			}
			else
			{
				parent.insertChild(trueIndex + 2, child);
			}
			
			parent.addChild(new XMLNode("\n\t"));
		}
		
		return child;
	}


	/**
	 * 
	 * 
	 * @param parent
	 * @param elementName
	 * @param elementNamespaceURI
	 * @param prefix
	 * @return
	 */
	private XMLNode getOrCreate(XMLNode parent, int index, String elementName, String elementNamespaceURI, String prefix) 
	{
		if (index < 0)
		{
			throw new IllegalArgumentException("Index should be positive or zero.");
		}
		
		// logger.info("getOrCreate called");
		List<XMLNode> children = getChildElements(parent, elementName, elementNamespaceURI);
		XMLNode child = null;		

		if (children != null && children.size() > index)
		{
			child = children.get(index);
		}
		else
		{
			int trueIndex = getTrueIndexOf(parent, elementName, elementNamespaceURI, index - 1);
			
			if (trueIndex != -1 && trueIndex < parent.getChildCount()) 
			{
				XMLNode nextNode = parent.getChildAt(trueIndex + 1);
				
				if (!(nextNode.isText() && nextNode.getCharacters().trim().length() == 0)) 
				{
					parent.insertChild(trueIndex, new XMLNode("\n\t"));
				}
				trueIndex += 2;
				index = trueIndex;
			}
			
			child = new XMLNode(new XMLTriple(elementName, elementNamespaceURI, prefix), new XMLAttributes());
			
			if (parent.getChildCount() == 0)
			{
				parent.insertChild(index, new XMLNode("\n\t"));
				index++;
			}
			
			parent.insertChild(index, child);
			parent.insertChild(index + 1, new XMLNode("\n\t"));
		}
		
		return child;
	}

	
	/**
	 * 
	 * @param xmlNode
	 * @param elementName
	 * @param elementURI
	 * @return
	 */
	private int getLastIndexOf(XMLNode xmlNode, String elementName, String elementURI) 
	{
		// checking the inputs
		if (xmlNode == null) {
			return -1;
		}
		if (elementName == null || elementName.trim().length() == 0) {
			elementName = "*";
		}
		if (elementURI == null || elementURI.trim().length() == 0) {
			elementURI = "*";
		}

		int lastIndex = -1;
		
		for (int i = 0; i < xmlNode.getChildCount(); i++) {
			XMLNode child = xmlNode.getChildAt(i); 
			
			if (child.isElement() 
					&& (child.getName().equals(elementName) || elementName.equals("*")) 
					&& (elementURI.equals(child.getURI()) || elementURI.equals("*")))
			{
				lastIndex = i;
			}
		}
		
		return lastIndex;
	}
	
	/**
	 * 
	 * @param xmlNode
	 * @param elementName
	 * @param elementURI
	 * @param index
	 * @return
	 */
	private int getTrueIndexOf(XMLNode xmlNode, String elementName, String elementURI, int index) 
	{
		// checking the inputs
		if (xmlNode == null || index == -1) {
			return -1;
		}
		if (elementName == null || elementName.trim().length() == 0) {
			elementName = "*";
		}
		if (elementURI == null || elementURI.trim().length() == 0) {
			elementURI = "*";
		}

		int trueIndex = -1;
		int elementCount = 0;
		
		for (int i = 0; i < xmlNode.getChildCount(); i++) {
			XMLNode child = xmlNode.getChildAt(i); 
			
			if (child.isElement() 
					&& (child.getName().equals(elementName) || elementName.equals("*")) 
					&& (elementURI.equals(child.getURI()) || elementURI.equals("*")))
			{
				if (elementCount == index)
				{
					trueIndex = i;
				}
			
				elementCount++;
			}
		}
		
		return trueIndex;
	}

	/**
	 * 
	 * 
	 * @param contextObject
	 * @param descriptionNode
	 */
	private void writeHistory(SBase contextObject, XMLNode descriptionNode) 
	{
		if (!contextObject.isSetHistory())
		{
			return;
		}

		writeCreators(contextObject.getHistory(), descriptionNode);
		writeDates(contextObject.getHistory(), descriptionNode);
	}

	/**
	 * 
	 * @param contextObject
	 * @param descriptionNode
	 */
	private void writeDates(History history, XMLNode descriptionNode) 
	{		
		// created date
		if (history.isSetCreatedDate())
		{
			XMLNode createdNode = getOrCreate(descriptionNode, "created", JSBML.URI_PURL_TERMS, "dcterms");
			createdNode.addAttr("parseType", "Resource", Annotation.URI_RDF_SYNTAX_NS, "rdf");

			XMLNode w3cdtfNode = getOrCreate(createdNode, "W3CDTF", JSBML.URI_PURL_TERMS, "dcterms");
			w3cdtfNode.insertChild(0, new XMLNode(DateParser.getIsoDateNoMillis(history.getCreatedDate())));
		}

		// modified dates
		if (history.getModifiedDateCount() > 0)
		{
			List<XMLNode> modifiedNodes = getChildElements(descriptionNode, "modified", JSBML.URI_PURL_TERMS);
			int i = 0;
			
			for (Date modifiedDate : history.getListOfModifiedDates())
			{
				XMLNode modifiedNode = null;

				if (modifiedNodes != null && modifiedNodes.size() > i)
				{
					modifiedNode = modifiedNodes.get(i);
				}				
				else if (i == 0) 
				{
					modifiedNode = getOrCreateAfter(descriptionNode, "modified", JSBML.URI_PURL_TERMS, "dcterms", "created", JSBML.URI_PURL_TERMS);
				} 
				else
				{
					modifiedNode = getOrCreate(descriptionNode, i, "modified", JSBML.URI_PURL_TERMS, "dcterms");
				}

				modifiedNode.addAttr("parseType", "Resource", Annotation.URI_RDF_SYNTAX_NS, "rdf");

				XMLNode w3cdtfNode = getOrCreate(modifiedNode, "W3CDTF", JSBML.URI_PURL_TERMS, "dcterms");
				w3cdtfNode.insertChild(0, new XMLNode(DateParser.getIsoDateNoMillis(modifiedDate)));
				
				i++;
			}
		}

	}


	/**
	 * @param history
	 * @param descriptionNode
	 */
	private void writeCreators(History history, XMLNode descriptionNode) 
	{
		if (history.getCreatorCount() > 0)
		{
			// creator node
			XMLNode creatorNode = getOrCreate(descriptionNode, "creator", JSBML.URI_PURL_ELEMENTS, "dc");
			XMLNode bagNode = getOrCreate(creatorNode, "Bag", Annotation.URI_RDF_SYNTAX_NS, "rdf");
			int i = 0;
			
			for (Creator creator : history.getListOfCreators())
			{
				// rdf:li node
				XMLNode liNode = getOrCreate(bagNode, i, "li", Annotation.URI_RDF_SYNTAX_NS, "rdf");
				liNode.addAttr("parseType", "Resource", Annotation.URI_RDF_SYNTAX_NS, "rdf");
				
				if (creator.isSetFamilyName() || creator.isSetGivenName())
				{
					XMLNode vCardNNode = getOrCreate(liNode, "N", Creator.URI_RDF_VCARD_NS, "vCard");
					vCardNNode.addAttr("parseType", "Resource", Annotation.URI_RDF_SYNTAX_NS, "rdf");
					
					if (creator.isSetFamilyName())
					{
						XMLNode vCardFamilyNode = getOrCreate(vCardNNode, "Family", Creator.URI_RDF_VCARD_NS, "vCard");
						vCardFamilyNode.insertChild(0, new XMLNode(creator.getFamilyName()));
					}
					
					if (creator.isSetGivenName())
					{
						XMLNode vCardGivenNode = getOrCreate(vCardNNode, "Given", Creator.URI_RDF_VCARD_NS, "vCard");
						vCardGivenNode.insertChild(0, new XMLNode(creator.getGivenName()));
					}
				}
				
				if (creator.isSetEmail())
				{
					XMLNode vCardEmailNode = getOrCreate(liNode, "EMAIL", Creator.URI_RDF_VCARD_NS, "vCard");
					vCardEmailNode.insertChild(0, new XMLNode(creator.getEmail()));					
				}
				
				if (creator.isSetOrganisation())
				{
					XMLNode vCardOrgNode = getOrCreate(liNode, "ORG", Creator.URI_RDF_VCARD_NS, "vCard");
					vCardOrgNode.addAttr("parseType", "Resource", Annotation.URI_RDF_SYNTAX_NS, "rdf");
					
					XMLNode vCardOrgNameNode = getOrCreate(vCardOrgNode, "Orgname", Creator.URI_RDF_VCARD_NS, "vCard");					
					vCardOrgNameNode.insertChild(0, new XMLNode(creator.getOrganisation()));										
				}
				
				i++;
			}
		}		
	}

	/**
	 * @param contextObject
	 * @param descriptionNode
	 */
	private void writeURIs(SBase contextObject, XMLNode descriptionNode) 
	{
		if (contextObject.getCVTermCount() > 0)
		{
			int cvtermIndex = 0;
			String precedingElementName = null;
			String precedingElementNamespaceURI = JSBML.URI_PURL_TERMS;
			
			if (contextObject.getHistory().isSetModifiedDate())
			{
				precedingElementName = "modified";
			}
			else if (contextObject.getHistory().isSetCreatedDate())
			{
				precedingElementName = "created";
			}
			else if (contextObject.getHistory().getCreatorCount() > 0)
			{
				precedingElementName = "creator";
				precedingElementNamespaceURI = JSBML.URI_PURL_ELEMENTS;
			}
			
			for (CVTerm cvterm : contextObject.getAnnotation().getListOfCVTerms())
			{
				XMLNode qualifierNode = null;
				
				// TODO - Need to do a special method for CVTerm. We always add the child at the end of the rdf:Description
				// if there is a XMLNode in the user object map, we take it. Otherwise we create it.
				
				if (cvterm.isBiologicalQualifier())
				{
					if (cvtermIndex == 0 && precedingElementName != null)
					{
						qualifierNode = getOrCreateAfter(descriptionNode, cvterm.getBiologicalQualifierType().getElementNameEquivalent(),
								CVTerm.URI_BIOMODELS_NET_BIOLOGY_QUALIFIERS, "bqbiol", precedingElementName, precedingElementNamespaceURI);
					} 
					else
					{
						qualifierNode = getOrCreate(descriptionNode, cvtermIndex, cvterm.getBiologicalQualifierType().getElementNameEquivalent(),
								CVTerm.URI_BIOMODELS_NET_BIOLOGY_QUALIFIERS, "bqbiol");	
					}
				}
				else
				{
					if (cvtermIndex == 0 && precedingElementName != null)
					{
						qualifierNode = getOrCreateAfter(descriptionNode, cvterm.getModelQualifierType().getElementNameEquivalent(),
								CVTerm.URI_BIOMODELS_NET_MODEL_QUALIFIERS, "bqmodel", precedingElementName, precedingElementNamespaceURI);
					} 
					else
					{
						qualifierNode = getOrCreate(descriptionNode, cvtermIndex, cvterm.getModelQualifierType().getElementNameEquivalent(),
								CVTerm.URI_BIOMODELS_NET_MODEL_QUALIFIERS, "bqmodel");
					}
				}

				XMLNode bagNode = getOrCreate(qualifierNode, "Bag", Annotation.URI_RDF_SYNTAX_NS, "rdf");
				
				int liIndex = 0;
				for (String uri : cvterm.getResources())
				{
					// rdf:li node
					XMLNode liNode = getOrCreate(bagNode, liIndex, "li", Annotation.URI_RDF_SYNTAX_NS, "rdf");
					liNode.addAttr("resource", uri, Annotation.URI_RDF_SYNTAX_NS, "rdf");
					
					liIndex++;
				}
				
				cvtermIndex++;
			}
		}
	}


}