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
package org.sbml.jsbml.xml.parsers;

import java.util.ArrayList;

import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.multi.InitialSpeciesInstance;
import org.sbml.jsbml.ext.multi.MultiSpecies;
import org.sbml.jsbml.xml.stax.SBMLObjectForXML;

/**
 * A MultiParser is used to parse the multi extension package elements and
 * attributes. The namespaceURI URI of this parser is
 * "http://www.sbml.org/sbml/level3/version1/multi/version1". This parser is
 * able to read and write elements of the multi package (implements
 * ReadingParser and WritingParser).
 * 
 * @author Marine Dumousseau
 * @since 1.0
 * @version $Rev$
 */
public class MultiParser extends AbstractReaderWriter {

	/**
	 * The namespace URI of this parser.
	 */
	private static final String namespaceURI = "http://www.sbml.org/sbml/level3/version1/multi/version1";

	/**
	 * 
	 * @return the namespaceURI of this parser.
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.WritingParser#getListOfSBMLElementsToWrite(Object
	 * sbase)
	 */
	public ArrayList<Object> getListOfSBMLElementsToWrite(Object sbase) {
		if (sbase instanceof Species) {
			// TODO return the listOf..
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.ReadingParser#processAttribute(String
	 * elementName, String attributeName, String value, String prefix, boolean
	 * isLastAttribute, Object contextObject)
	 */
	public void processAttribute(String elementName, String attributeName,
			String value, String prefix, boolean isLastAttribute,
			Object contextObject) {

		boolean isAttributeRead = false;
		if (contextObject instanceof SBase) {
			SBase sbase = (SBase) contextObject;
			try {
				isAttributeRead = sbase.readAttribute(attributeName, prefix,
						value);
			} catch (Throwable exc) {
				System.err.println(exc.getMessage());
			}
		} else if (contextObject instanceof Annotation) {
			Annotation annotation = (Annotation) contextObject;
			isAttributeRead = annotation.readAttribute(attributeName, prefix,
					value);
		}

		if (!isAttributeRead) {
			// TODO : throw new SBMLException ("The attribute " + attributeName
			// + " on the element " + elementName +
			// "is not part of the SBML specifications");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.ReadingParser#processAttribute(String
	 * elementName, String attributeName, String value, String prefix, boolean
	 * isLastAttribute, Object contextObject)
	 */
	public void processCharactersOf(String elementName, String characters,
			Object contextObject) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.ReadingParser#processEndDocument(SBMLDocument
	 * sbmlDocument)
	 */
	public void processEndDocument(SBMLDocument sbmlDocument) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.ReadingParser#processEndElement(String
	 * elementName, String prefix, boolean isNested, Object contextObject)
	 */
	public boolean processEndElement(String elementName, String prefix,
			boolean isNested, Object contextObject) {

		if (elementName.equals("listOfSpeciesInstances")) {
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.ReadingParser#processNamespace(String
	 * elementName, String URI, String prefix, String localName, boolean
	 * hasAttributes, boolean isLastNamespace, Object contextObject)
	 */
	public void processNamespace(String elementName, String URI, String prefix,
			String localName, boolean hasAttributes, boolean isLastNamespace,
			Object contextObject) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.ReadingParser#processStartElement(String
	 * elementName, String prefix, boolean hasAttributes, boolean hasNamespaces,
	 * Object contextObject)
	 */
	@SuppressWarnings("unchecked")
	public Object processStartElement(String elementName, String prefix,
			boolean hasAttributes, boolean hasNamespaces, Object contextObject) {

		// TODO 
		
		if (contextObject instanceof Species) {
			Species species = (Species) contextObject;
			if (elementName.equals("listOfInitialSpeciesInstances")) {
				ListOf<InitialSpeciesInstance> listOfInitialSpeciesInstances = new ListOf<InitialSpeciesInstance>();
				listOfInitialSpeciesInstances
						.setSBaseListType(ListOf.Type.other);

				MultiSpecies multiSpecies = new MultiSpecies(null);
				// multiSpecies.setListOfInitialSpeciesInstance(listOfInitialSpeciesInstances);
				species.addExtension(MultiParser.namespaceURI, multiSpecies);

				return listOfInitialSpeciesInstances;
			}
		} else if (contextObject instanceof ListOf<?>) {
			ListOf<SBase> listOf = (ListOf<SBase>) contextObject;

			if (elementName.equals("initialSpeciesInstance")) {
				InitialSpeciesInstance initialSpeciesInstance = new InitialSpeciesInstance();
				listOf.add(initialSpeciesInstance);

				return initialSpeciesInstance;
			}

		}
		return contextObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.WritingParser#writeAttributes(SBMLObjectForXML
	 * xmlObject, Object sbmlElementToWrite)
	 */
	public void writeAttributes(SBMLObjectForXML xmlObject,
			Object sbmlElementToWrite) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.WritingParser#writeCharacters(SBMLObjectForXML
	 * xmlObject, Object sbmlElementToWrite)
	 */
	public void writeCharacters(SBMLObjectForXML xmlObject,
			Object sbmlElementToWrite) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.WritingParser#writeElement(SBMLObjectForXML
	 * xmlObject, Object sbmlElementToWrite)
	 */
	public void writeElement(SBMLObjectForXML xmlObject,
			Object sbmlElementToWrite) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sbml.jsbml.xml.WritingParser#writeNamespaces(SBMLObjectForXML
	 * xmlObject, Object sbmlElementToWrite)
	 */
	public void writeNamespaces(SBMLObjectForXML xmlObject,
			Object sbmlElementToWrite) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getShortLabel() {
		return "multi";
	}

}