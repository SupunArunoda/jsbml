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
 * 6. The University of Toronto, Toronto, ON, Canada
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.math;

import java.text.MessageFormat;

import org.sbml.jsbml.ASTNode.Type;
import org.sbml.jsbml.MathContainer;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.math.compiler.ASTNode2Compiler;
import org.sbml.jsbml.util.TreeNodeChangeEvent;
import org.sbml.jsbml.util.compilers.ASTNodeValue;

/**
 * An Abstract Syntax Tree (AST) node representing an arithmetic
 * operator in a mathematical expression.
 * 
 * @author Victor Kofia
 * @version $Rev$
 * @since 1.0
 * @date May 30, 2014
 */
public class ASTArithmeticOperatorNode extends ASTFunction {
  
  /**
   * 
   */
  private static final long serialVersionUID = -7712374792704509306L;
  
  /**
   * Creates a new {@link ASTArithmeticOperatorNode} without a pointer
   * to its containing {@link MathContainer}.
   */
  public ASTArithmeticOperatorNode() {
    super();
  }
  
  /**
   * Copy constructor; Creates a deep copy of the given {@link ASTArithmeticOperatorNode}.
   * 
   * @param node
   *            the {@link ASTArithmeticOperatorNode} to be copied.
   */
  public ASTArithmeticOperatorNode(ASTArithmeticOperatorNode node) {
    super(node);
  }
  
  /**
   * Creates a new {@link ASTArithmeticOperatorNode} with a pointer
   * to the specified {@link MathContainer}..
   */
  public ASTArithmeticOperatorNode(MathContainer container) {
    super(container);
  }
  
  /**
   * Creates a new {@link ASTArithmeticOperatorNode} without a pointer
   * to its containing {@link MathContainer} but of the specified 
   * {@link Type}.
   */
  public ASTArithmeticOperatorNode(Type type) {
    this();
    setType(type);
  }

  /**
   * Creates a new {@link ASTArithmeticOperatorNode} of type
   * {@link Type} and container {@link MathContainer}.
   */
  public ASTArithmeticOperatorNode(Type type, MathContainer container) {
    this(container);
    setType(type);
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.math.ASTFunction#clone()
   */
  @Override
  public ASTArithmeticOperatorNode clone() {
    return new ASTArithmeticOperatorNode(this);
  }
  
  /* (non-Javadoc)
   * @see org.sbml.jsbml.math.ASTNode2#compile(org.sbml.jsbml.util.compilers.ASTNode2Compiler)
   */
  @Override
  public ASTNodeValue compile(ASTNode2Compiler compiler) {
    ASTNodeValue value = null;
    switch(getType()) {
    case PLUS:
      value = compiler.plus(getChildren());
      value.setUIFlag(getChildCount() <= 1);
      break;
    case MINUS:
      if (getChildCount() < 2) {
        value = compiler.uMinus(getChildAt(0));
        value.setUIFlag(true);
      } else {
        value = compiler.minus(getChildren());
        value.setUIFlag(false);
      }
      break;
    case TIMES:
      value = compiler.times(getChildren());
      value.setUIFlag(getChildCount() <= 1);
      break;
    case DIVIDE:
      int childCount = getChildCount();
      if (childCount != 2) {
        throw new SBMLException(MessageFormat.format(
          "Fractions must have one numerator and one denominator, here {0,number,integer} elements are given.",
          childCount));
      }
      value = compiler.frac(getChildAt(0), getChildAt(getChildCount() - 1));
      break;
    default: // UNKNOWN:
      value = compiler.unknownValue();
      break;
    }
    value.setType(getType());
    MathContainer parent = getParentSBMLObject();
    if (parent != null) {
      value.setLevel(parent.getLevel());
      value.setVersion(parent.getVersion());
    }
    return value;
  }
  
  /**
   * Sets the value of this {@link ASTArithmeticOperatorNode} to the given character. If 
   * character is one of +, -, *, / or ^, the node type will be set accordingly. 
   * For all other characters, the node type will be set to UNKNOWN.
   * 
   * @param value
   *            the character value to which the node's value should be set.
   */
  public void setCharacter(char value) {
    Type oldValue = type;
    switch (value) {
    case '+':
      type = Type.PLUS;
      break;
    case '-':
      type = Type.MINUS;
      break;
    case '*':
      type = Type.TIMES;
      break;
    case '/':
      type = Type.DIVIDE;
      break;
    case '^':
      type = Type.POWER;
      break;
    default:
      type = Type.UNKNOWN;
      break;
    }
    firePropertyChange(TreeNodeChangeEvent.value, oldValue, type);
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ASTArithmeticOperatorNode [listOfNodes=");
    builder.append(listOfNodes);
    builder.append(", parentSBMLObject=");
    builder.append(parentSBMLObject);
    builder.append(", strict=");
    builder.append(strict);
    builder.append(", type=");
    builder.append(type);
    builder.append(", id=");
    builder.append(id);
    builder.append(", style=");
    builder.append(style);
    builder.append(", listOfListeners=");
    builder.append(listOfListeners);
    builder.append(", parent=");
    builder.append(parent);
    builder.append("]");
    return builder.toString();
  }

}
