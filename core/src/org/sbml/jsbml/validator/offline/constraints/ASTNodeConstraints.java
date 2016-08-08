/*
 * $IdASTNodeConstraints.java 17:11:18 roman $
 * $URLASTNodeConstraints.java $
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 * Copyright (C) 2009-2016 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 * 4. The University of California, San Diego, La Jolla, CA, USA
 * 5. The Babraham Institute, Cambridge, UK
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.validator.offline.constraints;

import java.util.HashSet;
import java.util.Set;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ASTNode.Type;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.MathContainer;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.validator.SBMLValidator.CHECK_CATEGORY;
import org.sbml.jsbml.validator.offline.ValidationContext;
import org.sbml.jsbml.validator.offline.constraints.helper.ValidationTools;

/**
 * @author Roman
 * @since 1.2
 * @date 06.08.2016
 */
public class ASTNodeConstraints extends AbstractConstraintDeclaration {

  /*
   * (non-Javadoc)
   * @see org.sbml.jsbml.validator.offline.constraints.ConstraintDeclaration#
   * addErrorCodesForCheck(java.util.Set, int, int,
   * org.sbml.jsbml.validator.SBMLValidator.CHECK_CATEGORY)
   */
  @Override
  public void addErrorCodesForCheck(Set<Integer> set, int level, int version,
    CHECK_CATEGORY category) {

    switch (category) {
    case GENERAL_CONSISTENCY:
      break;
    case IDENTIFIER_CONSISTENCY:
      break;
    case MATHML_CONSISTENCY:
      if (level > 1) {
        addRangeToSet(set, CORE_10208, CORE_10216);

        set.add(CORE_10218);
        
        if (level == 3 || version == 4)
        {
          set.add(CORE_10219);
          set.add(CORE_10221);
        }
        
        if (level == 2 && version == 4)
        {
          set.add(CORE_10219);
        }
        
        set.add(CORE_10222);
      }

      break;
    case MODELING_PRACTICE:
      break;
    case OVERDETERMINED_MODEL:
      break;
    case SBO_CONSISTENCY:
      break;
    case UNITS_CONSISTENCY:
      break;
    }

  }


  /*
   * (non-Javadoc)
   * @see org.sbml.jsbml.validator.offline.constraints.ConstraintDeclaration#
   * addErrorCodesForAttribute(java.util.Set, int, int, java.lang.String)
   */
  @Override
  public void addErrorCodesForAttribute(Set<Integer> set, int level,
    int version, String attributeName) {
    // TODO Auto-generated method stub

  }


  /*
   * (non-Javadoc)
   * @see org.sbml.jsbml.validator.offline.constraints.ConstraintDeclaration#
   * getValidationFunction(int)
   */
  @Override
  public ValidationFunction<?> getValidationFunction(int errorCode) {
    ValidationFunction<ASTNode> func = null;

    switch (errorCode) {
    case CORE_10208:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          // Lambda only allowed as first child of FunctionDefinitions
          if (node.isLambda()) {

            TreeNode p = node.getParent();
            return p instanceof FunctionDefinition;
          }

          return true;
        }
      };
      break;
    case CORE_10209:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          // In logical operators...
          if (node.isLogical()) {

            // all children must be booleans
            for (ASTNode n : node.getChildren()) {
              if (!n.isBoolean()) {
                return false;
              }
            }
          }

          return true;
        }
      };
      break;
      
    case CORE_10210:
      func = new ValidationFunction<ASTNode>() {

        private final Set<ASTNode.Type> set = createSet();


        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          // In operators...
          if (node.isOperator() || set.contains(node.getType())) {

            // all children must be numbers
            for (ASTNode n : node.getChildren()) {
              if (!n.isNumber()) {
                return false;
              }
            }
          }

          return true;
        }


        private Set<ASTNode.Type> createSet() {
          Set<ASTNode.Type> set = new HashSet<ASTNode.Type>();

          set.add(ASTNode.Type.FUNCTION_ROOT);
          set.add(ASTNode.Type.FUNCTION_ABS);
          set.add(ASTNode.Type.FUNCTION_EXP);
          set.add(ASTNode.Type.FUNCTION_LN);
          set.add(ASTNode.Type.FUNCTION_LOG);
          set.add(ASTNode.Type.FUNCTION_FLOOR);
          set.add(ASTNode.Type.FUNCTION_CEILING);
          set.add(ASTNode.Type.FUNCTION_FACTORIAL);

          set.add(ASTNode.Type.FUNCTION_SIN);
          set.add(ASTNode.Type.FUNCTION_COS);
          set.add(ASTNode.Type.FUNCTION_TAN);
          set.add(ASTNode.Type.FUNCTION_SEC);
          set.add(ASTNode.Type.FUNCTION_CSC);
          set.add(ASTNode.Type.FUNCTION_COT);

          set.add(ASTNode.Type.FUNCTION_SINH);
          set.add(ASTNode.Type.FUNCTION_COSH);
          set.add(ASTNode.Type.FUNCTION_TANH);
          set.add(ASTNode.Type.FUNCTION_SECH);
          set.add(ASTNode.Type.FUNCTION_CSCH);
          set.add(ASTNode.Type.FUNCTION_COTH);

          set.add(ASTNode.Type.FUNCTION_ARCSIN);
          set.add(ASTNode.Type.FUNCTION_ARCCOS);
          set.add(ASTNode.Type.FUNCTION_ARCTAN);
          set.add(ASTNode.Type.FUNCTION_ARCSEC);
          set.add(ASTNode.Type.FUNCTION_ARCCSC);
          set.add(ASTNode.Type.FUNCTION_ARCCOT);

          set.add(ASTNode.Type.FUNCTION_ARCSINH);
          set.add(ASTNode.Type.FUNCTION_ARCCOSH);
          set.add(ASTNode.Type.FUNCTION_ARCTANH);
          set.add(ASTNode.Type.FUNCTION_ARCSECH);
          set.add(ASTNode.Type.FUNCTION_ARCCSCH);
          set.add(ASTNode.Type.FUNCTION_ARCCOTH);

          return set;
        }
      };
      break;
    case CORE_10211:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          // In comparator...
          if (node.isRelational() && node.getChildCount() > 0) {

            byte dt = ValidationTools.getDataType(node.getChild(0));

            // all children must have same Type
            for (int i = 1; i < node.getNumChildren(); i++) {
              if (dt != ValidationTools.getDataType(node.getChild(i))) {
                return false;
              }
            }
          }

          return true;
        }
      };
      break;

    case CORE_10212:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          // If is piecewise...
          if (node.isPiecewise() && node.getNumChildren() > 0) {

            byte dt = ValidationTools.getDataType(node.getLeftChild());

            if (dt == ValidationTools.DT_STRING) {
              return false;
            }

            // all children must have same Type
            for (int i = 0; i < node.getNumChildren(); i += 2) {
              if (dt != ValidationTools.getDataType(node.getChild(i))) {
                return false;
              }
            }
          }

          return true;
        }
      };
      break;

    case CORE_10213:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          // If is piecewise...
          if (node.getType() == Type.FUNCTION_PIECEWISE) {
            
            // every second node must be a condition and therefore return a boolean
            for (int i = 1; i < node.getNumChildren(); i += 2)
            {
              if (!node.getChild(i).isBoolean())
              {
                return false;
              }
            }
          }

          return true;
        }
      };
      break;
    case CORE_10214:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          // If is piecewise...
          if (node.getType() == Type.FUNCTION) {

            Model m = node.getParentSBMLObject().getModel();

            if (m != null) {
              return m.getFunctionDefinition(node.getName()) != null;
            }
          }

          return true;
        }

      };
      break;

    case CORE_10215:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          // If it's a name
          if (node.isName()) {

            String name = node.getName();
            MathContainer parent = node.getParentSBMLObject();

            if (parent == null
              || ValidationTools.isLocalParameter(node, name)) {
              return true;
            }

            Model m = parent.getModel();

            if (m != null) {
              boolean allowReaction = true;
              boolean allowSpeciesRef = false;

              if (ctx.isLevelAndVersionEqualTo(2, 1)) {
                allowReaction = false;
              }

              if (ctx.getLevel() > 2) {
                allowSpeciesRef = true;
              }

              // If the name doesn't match anything the constraint is broken
              if (m.getCompartment(name) == null && m.getSpecies(name) == null
                && m.getParameter(name) == null
                && (!allowReaction || m.getReaction(name) == null)
                && (!allowSpeciesRef
                  || !ValidationTools.isSpeciesReference(m, name))) {
                return false;
              }
            }
          }

          return true;
        }

      };
      break;

    case CORE_10216:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          // If it's a name
          if (node.isName()) {

            String name = node.getName();
            MathContainer parent = node.getParentSBMLObject();

            if (parent == null) {
              return true;
            }

            Model m = parent.getModel();

            if (m != null && parent instanceof KineticLaw) {
              boolean allowReaction = true;
              boolean allowSpeciesRef = false;

              if (ctx.isLevelAndVersionEqualTo(2, 1)) {
                allowReaction = false;
              }

              if (ctx.getLevel() > 2) {
                allowSpeciesRef = true;
              }

              // If the name doesn't match anything so it must be a local
              // parameter
              if (m.getCompartment(name) == null && m.getSpecies(name) == null
                && m.getParameter(name) == null
                && (!allowReaction || m.getReaction(name) == null)
                && (!allowSpeciesRef
                  || !ValidationTools.isSpeciesReference(m, name))) {

                KineticLaw kl = (KineticLaw) parent;

                return kl.getLocalParameter(name) == null;

              }
            }
          }

          return true;
        }

      };
      break;

    case CORE_10218:
      ASTNodeConstraints cd = this;
      func = new ValidationFunction<ASTNode>() {

        private final Set<ASTNode.Type> unaries  = getUnaryTypes();
        private final Set<ASTNode.Type> binaries = getBinaryTypes();
        private final Set<ASTNode.Type> relations = getRelationTypes();

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          ASTNode.Type type = node.getType();

          // Unary functions
          if (unaries.contains(type)) {
            return node.isUnary();
          }
          // Binary functions
          else if (binaries.contains(type)) {
            return node.getNumChildren() == 2;
          }
          // Can't be empty
          else if (type == Type.FUNCTION_PIECEWISE) {
            if (node.getNumChildren() > 0)
            {
              // check if there is always a number followed by a boolean
              boolean shouldBeNumber = true;
              
              for (ASTNode child:node.getListOfNodes())
              {
                // Should be number but isn't
                if (shouldBeNumber == !child.isNumber())
                {
                  return false;
                }
                // Must be a boolean
                else if (!child.isBoolean())
                {
                  return false;
                }
                
                // Flip boolean
                shouldBeNumber = !shouldBeNumber;
              }
            }
            else
            {
              return false;
            }
          }
          // Can have one or two children
          else if (type == Type.FUNCTION_ROOT || type == Type.MINUS) {
            return node.getNumChildren() == 1 || node.getNumChildren() == 2;
          }
          // In MathML 2 these types must have at least 2 children
          else if (relations.contains(type))
          {
            return node.getNumChildren() > 1;
          }
          // Special case before l2v4
          else if (type == Type.FUNCTION
            && ctx.isLevelAndVersionLessThan(2, 4)) {

            @SuppressWarnings("unchecked")
            ValidationFunction<ASTNode> f2 =
              (ValidationFunction<ASTNode>) new ASTNodeConstraints().getValidationFunction(
                CORE_10219);

            return f2.check(ctx, node);
          }

          return true;
        }


        private Set<ASTNode.Type> getBinaryTypes() {
          Set<ASTNode.Type> set = new HashSet<ASTNode.Type>();

          set.add(ASTNode.Type.DIVIDE);
          set.add(ASTNode.Type.POWER);
          set.add(ASTNode.Type.RELATIONAL_NEQ);
          set.add(ASTNode.Type.FUNCTION_DELAY);
          set.add(ASTNode.Type.FUNCTION_POWER);
          set.add(ASTNode.Type.FUNCTION_LOG);

          return set;
        }

        private Set<ASTNode.Type> getRelationTypes() {
          Set<ASTNode.Type> set = new HashSet<ASTNode.Type>();
          set.add(ASTNode.Type.RELATIONAL_EQ);
          set.add(ASTNode.Type.RELATIONAL_GEQ);
          set.add(ASTNode.Type.RELATIONAL_GT);
          set.add(ASTNode.Type.RELATIONAL_LT);
          set.add(ASTNode.Type.RELATIONAL_LEQ);
          
          return set;
        }
        private Set<ASTNode.Type> getUnaryTypes() {
          Set<ASTNode.Type> set = new HashSet<ASTNode.Type>();

          set.add(ASTNode.Type.FUNCTION_ABS);
          set.add(ASTNode.Type.FUNCTION_EXP);
          set.add(ASTNode.Type.FUNCTION_LN);
          set.add(ASTNode.Type.LOGICAL_NOT);
          set.add(ASTNode.Type.FUNCTION_FLOOR);
          set.add(ASTNode.Type.FUNCTION_CEILING);
          set.add(ASTNode.Type.FUNCTION_FACTORIAL);

          set.add(ASTNode.Type.FUNCTION_SIN);
          set.add(ASTNode.Type.FUNCTION_COS);
          set.add(ASTNode.Type.FUNCTION_TAN);
          set.add(ASTNode.Type.FUNCTION_SEC);
          set.add(ASTNode.Type.FUNCTION_CSC);
          set.add(ASTNode.Type.FUNCTION_COT);

          set.add(ASTNode.Type.FUNCTION_SINH);
          set.add(ASTNode.Type.FUNCTION_COSH);
          set.add(ASTNode.Type.FUNCTION_TANH);
          set.add(ASTNode.Type.FUNCTION_SECH);
          set.add(ASTNode.Type.FUNCTION_CSCH);
          set.add(ASTNode.Type.FUNCTION_COTH);

          set.add(ASTNode.Type.FUNCTION_ARCSIN);
          set.add(ASTNode.Type.FUNCTION_ARCCOS);
          set.add(ASTNode.Type.FUNCTION_ARCTAN);
          set.add(ASTNode.Type.FUNCTION_ARCSEC);
          set.add(ASTNode.Type.FUNCTION_ARCCSC);
          set.add(ASTNode.Type.FUNCTION_ARCCOT);

          set.add(ASTNode.Type.FUNCTION_ARCSINH);
          set.add(ASTNode.Type.FUNCTION_ARCCOSH);
          set.add(ASTNode.Type.FUNCTION_ARCTANH);
          set.add(ASTNode.Type.FUNCTION_ARCSECH);
          set.add(ASTNode.Type.FUNCTION_ARCCSCH);
          set.add(ASTNode.Type.FUNCTION_ARCCOTH);

          return set;
        }

      };
      break;

    case CORE_10219:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          ASTNode.Type type = node.getType();

          if (type == Type.FUNCTION) {
            Model m = node.getParentSBMLObject().getModel();

            if (m != null) {
              FunctionDefinition fd = m.getFunctionDefinition(node.getName());

              if (fd != null) {
                return node.getNumChildren() == fd.getArgumentCount();
              }
            }
          }

          return true;
        }

      };
      break;

    case CORE_10221:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          ASTNode.Type type = node.getType();

          if (type == Type.FUNCTION) {

            String units = node.getUnits();

            if (units != null && !units.isEmpty()) {
              // Checks if the unit is predefined or defined in the model
              if (!(Unit.isUnitKind(units, ctx.getLevel(), ctx.getVersion()))
                && node.getUnitsInstance() == null) {
                return false;
              }
            }

          }

          return true;
        }

      };
      break;
      
    case CORE_10222:
      func = new ValidationFunction<ASTNode>() {

        @Override
        public boolean check(ValidationContext ctx, ASTNode node) {

          Model m = node.getParentSBMLObject().getModel();

          if (m != null && node.isName())
          {
            Compartment c = m.getCompartment(node.getName());
            
            if (c != null && c.getSpatialDimensions() == 0)
            {
              return false;
            }
          }

          return true;
        }

      };
      break;
    }

    return func;
  }

}