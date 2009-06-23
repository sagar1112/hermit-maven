// Copyright 2008 by Oxford University; see license.txt for details
package org.semanticweb.HermiT.structural;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.owl.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owl.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomVisitor;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLClassExpression;
import org.semanticweb.owl.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owl.model.OWLDataAllValuesFrom;
import org.semanticweb.owl.model.OWLDataComplementOf;
import org.semanticweb.owl.model.OWLDataExactCardinality;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataHasValue;
import org.semanticweb.owl.model.OWLDataIntersectionOf;
import org.semanticweb.owl.model.OWLDataMaxCardinality;
import org.semanticweb.owl.model.OWLDataMinCardinality;
import org.semanticweb.owl.model.OWLDataOneOf;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataSomeValuesFrom;
import org.semanticweb.owl.model.OWLDataUnionOf;
import org.semanticweb.owl.model.OWLDataVisitorEx;
import org.semanticweb.owl.model.OWLDatatype;
import org.semanticweb.owl.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owl.model.OWLDatatypeRestriction;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFacetRestriction;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLHasKeyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectAllValuesFrom;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinality;
import org.semanticweb.owl.model.OWLObjectHasSelf;
import org.semanticweb.owl.model.OWLObjectHasValue;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinality;
import org.semanticweb.owl.model.OWLObjectMinCardinality;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLPropertyExpression;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualAxiom;
import org.semanticweb.owl.model.OWLStringLiteral;
import org.semanticweb.owl.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owl.model.OWLSubClassOfAxiom;
import org.semanticweb.owl.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owl.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owl.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTypedLiteral;
import org.semanticweb.owl.model.SWRLRule;

/**
 * This class implements the structural transformation from our new tableau paper. This transformation departs in the following way from the paper: it keeps the concepts of the form \exists R.{ a_1, ..., a_n }, \forall R.{ a_1, ..., a_n }, and \forall R.\neg { a } intact. These concepts are then clausified in a more efficient way.
 */
public class OWLNormalization {
    protected final OWLDataFactory m_factory;
    protected final Map<OWLClassExpression,OWLClassExpression> m_definitions; 
    protected final Map<OWLObjectOneOf,OWLClass> m_definitionsForNegativeNominals;
    protected final OWLAxioms m_axioms;
    protected final ExpressionManager m_expressionManager;
    protected final PLVisitor m_plVisitor;

    public OWLNormalization(OWLDataFactory factory,OWLAxioms axioms) {
        m_factory=factory;
        m_definitions=new HashMap<OWLClassExpression,OWLClassExpression>();
        m_definitionsForNegativeNominals=new HashMap<OWLObjectOneOf,OWLClass>();
        m_axioms=axioms;
        m_expressionManager=new ExpressionManager(m_factory);
        m_plVisitor=new PLVisitor();
    }

    public void processOntology(Configuration config,OWLOntology ontology) {
        // Each entry in the inclusions list represents a disjunction of
        // concepts -- that is, each OWLClassExpression in an entry contributes a
        // disjunct. It is thus not really inclusions, but rather a disjunction
        // of concepts that represents an inclusion axiom.
        m_axioms.m_classes.addAll(ontology.getReferencedClasses());
        m_axioms.m_objectProperties.addAll(ontology.getReferencedObjectProperties());
        m_axioms.m_dataProperties.addAll(ontology.getReferencedDataProperties());
        m_axioms.m_namedIndividuals.addAll(ontology.getReferencedIndividuals());
        AxiomVisitor axiomVisitor=new AxiomVisitor();
        for (OWLAxiom axiom : ontology.getAxioms())
            axiom.accept(axiomVisitor);
        // now all axioms are in NNF and converted into disjunctions wherever possible
        // exact cardinalities are rewritten into at least and at most cardinalities etc
        // in normalization, we now simplyfy the disjuncts where possible (eliminate 
        // unnecessary conjuncts/disjuncts) and introduce fresh atomic concepts for complex 
        // concepts
        // m_axioms.m_conceptInclusions contains the normalized axioms after the normalization
        normalizeInclusions(axiomVisitor.m_inclusionsAsDisjunctions);
    }
    protected void addFact(OWLIndividualAxiom axiom) {
        m_axioms.m_facts.add(axiom);
    }
    protected void addHasKey(OWLHasKeyAxiom axiom) {
        m_axioms.m_hasKeys.add(axiom);
    }
    protected void addInclusion(OWLObjectPropertyExpression subObjectPropertyExpression,OWLObjectPropertyExpression superObjectPropertyExpression) {
        m_axioms.m_simpleObjectPropertyInclusions.add(new OWLObjectPropertyExpression[] { subObjectPropertyExpression.getSimplified(),superObjectPropertyExpression.getSimplified() });
    }
    protected void addInclusion(OWLObjectPropertyExpression[] subObjectPropertyExpressions,OWLObjectPropertyExpression superObjectPropertyExpression) {
        for (int index=subObjectPropertyExpressions.length-1;index>=0;--index)
            subObjectPropertyExpressions[index]=subObjectPropertyExpressions[index].getSimplified();
        m_axioms.m_complexObjectPropertyInclusions.add(new OWLAxioms.ComplexObjectPropertyInclusion(subObjectPropertyExpressions,superObjectPropertyExpression.getSimplified()));
    }
    protected void addInclusion(OWLDataPropertyExpression subDataPropertyExpression,OWLDataPropertyExpression superDataPropertyExpression) {
        m_axioms.m_dataPropertyInclusions.add(new OWLDataPropertyExpression[] { subDataPropertyExpression,superDataPropertyExpression });
    }
    protected void makeTransitive(OWLObjectPropertyExpression objectPropertyExpression) {
        m_axioms.m_complexObjectPropertyInclusions.add(new OWLAxioms.ComplexObjectPropertyInclusion(objectPropertyExpression.getSimplified()));
    }
    protected void makeReflexive(OWLObjectPropertyExpression objectPropertyExpression) {
        m_axioms.m_reflexiveObjectProperties.add(objectPropertyExpression.getSimplified());
    }
    protected void makeIrreflexive(OWLObjectPropertyExpression objectPropertyExpression) {
        m_axioms.m_irreflexiveObjectProperties.add(objectPropertyExpression.getSimplified());
    }
    protected void makeAsymmetric(OWLObjectPropertyExpression objectPropertyExpression) {
        m_axioms.m_asymmetricObjectProperties.add(objectPropertyExpression.getSimplified());
    }
    protected static boolean isSimple(OWLClassExpression description) {
        return description instanceof OWLClass || (description instanceof OWLObjectComplementOf && ((OWLObjectComplementOf)description).getOperand() instanceof OWLClass);
    }
    protected static boolean isLiteral(OWLDataRange dr) {
        return isAtomic(dr) || isNegatedAtomic(dr);
    }
    protected static boolean isAtomic(OWLDataRange dr) {
        return dr instanceof OWLDatatype || dr instanceof OWLDatatypeRestriction || dr instanceof OWLDataOneOf;
    }
    protected static boolean isNegatedAtomic(OWLDataRange dr) {
        return dr instanceof OWLDataComplementOf && isAtomic(((OWLDataComplementOf)dr).getDataRange());
    }
    protected static boolean isNominal(OWLClassExpression description) {
        return description instanceof OWLObjectOneOf;
    }
    protected static boolean isNegatedOneNominal(OWLClassExpression description) {
        if (!(description instanceof OWLObjectComplementOf))
            return false;
        OWLClassExpression operand=((OWLObjectComplementOf)description).getOperand();
        if (!(operand instanceof OWLObjectOneOf))
            return false;
        return ((OWLObjectOneOf)operand).getIndividuals().size()==1;
    }
    protected void normalizeInclusions(List<OWLClassExpression[]> inclusions) {
        NormalizationVisitor normalizer=new NormalizationVisitor(inclusions);
        // simplify data ranges for custom defined datatypes
        for (OWLDatatype dt : m_axioms.m_customDatatypeDefinitions.keySet()) {
            m_axioms.m_customDatatypeDefinitions.put(dt, m_expressionManager.getSimplified(m_axioms.m_customDatatypeDefinitions.get(dt)));
        }
        while (!inclusions.isEmpty()) {
            OWLClassExpression simplifiedDescription=m_expressionManager.getSimplified(m_factory.getOWLObjectUnionOf(inclusions.remove(inclusions.size()-1)));
            if (!simplifiedDescription.isOWLThing()) {
                if (simplifiedDescription instanceof OWLObjectUnionOf) {
                    OWLObjectUnionOf objectOr=(OWLObjectUnionOf)simplifiedDescription;
                    OWLClassExpression[] descriptions=new OWLClassExpression[objectOr.getOperands().size()];
                    objectOr.getOperands().toArray(descriptions);
                    if (!distributeUnionOverAnd(descriptions,inclusions) && !optimizedNegativeOneOfTranslation(descriptions,m_axioms.m_facts)) {
                        for (int index=0;index<descriptions.length;index++)
                            descriptions[index]=descriptions[index].accept(normalizer);
                        m_axioms.m_conceptInclusions.add(descriptions);
                    }
                }
                else if (simplifiedDescription instanceof OWLObjectIntersectionOf) {
                    OWLObjectIntersectionOf objectAnd=(OWLObjectIntersectionOf)simplifiedDescription;
                    for (OWLClassExpression conjunct : objectAnd.getOperands())
                        inclusions.add(new OWLClassExpression[] { conjunct });
                } else {
                    OWLClassExpression normalized=simplifiedDescription.accept(normalizer);
                    m_axioms.m_conceptInclusions.add(new OWLClassExpression[] { normalized });
                }
            }
        }
        // normalize data range inclusions
        DataRangeNormalizationVisitor drNormalizer=new DataRangeNormalizationVisitor(normalizer.m_newDataRangeInclusions);
        while (!normalizer.m_newDataRangeInclusions.isEmpty()) {
            OWLDataRange simplifiedDescription=m_expressionManager.getSimplified(m_factory.getOWLDataUnionOf(normalizer.m_newDataRangeInclusions.remove(normalizer.m_newDataRangeInclusions.size()-1)));
            if (!simplifiedDescription.isTopDatatype()) {
                if (simplifiedDescription instanceof OWLDataUnionOf) {
                    OWLDataUnionOf dataOr=(OWLDataUnionOf)simplifiedDescription;
                    OWLDataRange[] descriptions=new OWLDataRange[dataOr.getOperands().size()];
                    dataOr.getOperands().toArray(descriptions);
                    if (!distributeUnionOverAnd(descriptions,normalizer.m_newDataRangeInclusions)) {
                        for (int index=0;index<descriptions.length;index++)
                            descriptions[index]=descriptions[index].accept(drNormalizer);
                        m_axioms.m_dataRangeInclusions.add(descriptions);
                    }
                } else if (simplifiedDescription instanceof OWLDataIntersectionOf) {
                    OWLDataIntersectionOf dataAnd=(OWLDataIntersectionOf)simplifiedDescription;
                    for (OWLDataRange conjunct : dataAnd.getOperands()) {
                        normalizer.m_newDataRangeInclusions.add(new OWLDataRange[] { conjunct});
                    }
                } else {
                    OWLDataRange normalized=simplifiedDescription.accept(drNormalizer);
                    m_axioms.m_dataRangeInclusions.add(new OWLDataRange[] { normalized });
                }
            }
        }
    }
    protected boolean distributeUnionOverAnd(OWLClassExpression[] descriptions,List<OWLClassExpression[]> inclusions) {
        int andIndex=-1;
        for (int index=0;index<descriptions.length;index++) {
            OWLClassExpression description=descriptions[index];
            if (!isSimple(description))
                if (description instanceof OWLObjectIntersectionOf) {
                    if (andIndex==-1)
                        andIndex=index;
                    else
                        return false;
                }
                else
                    return false;
        }
        if (andIndex==-1)
            return false;
        OWLObjectIntersectionOf objectAnd=(OWLObjectIntersectionOf)descriptions[andIndex];
        for (OWLClassExpression description : objectAnd.getOperands()) {
            OWLClassExpression[] newDescriptions=descriptions.clone();
            newDescriptions[andIndex]=description;
            inclusions.add(newDescriptions);
        }
        return true;
    }
    protected boolean distributeUnionOverAnd(OWLDataRange[] descriptions,List<OWLDataRange[]> inclusions) {
        int andIndex=-1;
        for (int index=0;index<descriptions.length;index++) {
            OWLDataRange description=descriptions[index];
            if (!isLiteral(description))
                if (description instanceof OWLDataIntersectionOf) {
                    if (andIndex==-1)
                        andIndex=index;
                    else
                        return false;
                }
                else
                    return false;
        }
        if (andIndex==-1)
            return false;
        OWLDataIntersectionOf dataAnd=(OWLDataIntersectionOf)descriptions[andIndex];
        for (OWLDataRange description : dataAnd.getOperands()) {
            OWLDataRange[] newDescriptions=descriptions.clone();
            newDescriptions[andIndex]=description;
            inclusions.add(newDescriptions);
        }
        return true;
    }
    protected boolean optimizedNegativeOneOfTranslation(OWLClassExpression[] descriptions,Collection<OWLIndividualAxiom> facts) {
        if (descriptions.length==2) {
            OWLObjectOneOf nominal=null;
            OWLClassExpression other=null;
            if (descriptions[0] instanceof OWLObjectComplementOf && ((OWLObjectComplementOf)descriptions[0]).getOperand() instanceof OWLObjectOneOf) {
                nominal=(OWLObjectOneOf)((OWLObjectComplementOf)descriptions[0]).getOperand();
                other=descriptions[1];
            }
            else if (descriptions[1] instanceof OWLObjectComplementOf && ((OWLObjectComplementOf)descriptions[1]).getOperand() instanceof OWLObjectOneOf) {
                other=descriptions[0];
                nominal=(OWLObjectOneOf)((OWLObjectComplementOf)descriptions[1]).getOperand();
            }
            if (nominal!=null && (other instanceof OWLClass || (other instanceof OWLObjectComplementOf && ((OWLObjectComplementOf)other).getOperand() instanceof OWLClass))) {
                for (OWLIndividual individual : nominal.getIndividuals())
                    facts.add(m_factory.getOWLClassAssertionAxiom(other,individual));
                return true;
            }
        }
        return false;
    }
    protected OWLClassExpression getDefinitionFor(OWLClassExpression description,boolean[] alreadyExists,boolean forcePositive) {
        OWLClassExpression definition=m_definitions.get(description);
        if (definition==null || (forcePositive && !(definition instanceof OWLClass))) {
            definition=m_factory.getOWLClass(URI.create("internal:def#"+m_definitions.size()));
            if (!forcePositive && !description.accept(m_plVisitor))
                definition=m_factory.getOWLObjectComplementOf(definition);
            m_definitions.put(description,definition);
            alreadyExists[0]=false;
        }
        else
            alreadyExists[0]=true;
        return definition;
    }
    protected OWLDatatype getDefinitionFor(OWLDataRange dr,boolean[] alreadyExists) {
        OWLDatatype definition = m_axioms.m_dataRangeDefinitions.get(dr);
        if (definition==null) {
            definition=m_factory.getOWLDatatype(URI.create("internal:defdata#"+m_axioms.m_dataRangeDefinitions.size()));
            m_axioms.m_dataRangeDefinitions.put(dr,definition);
            alreadyExists[0]=false;
        } else {
            alreadyExists[0]=true;
        }
        return definition;
    }
    protected OWLClassExpression getDefinitionFor(OWLClassExpression description,boolean[] alreadyExists) {
        return getDefinitionFor(description,alreadyExists,false);
    }
    protected OWLClass getClassFor(OWLClassExpression description,boolean[] alreadyExists) {
        return (OWLClass)getDefinitionFor(description,alreadyExists,true);
    }
    protected OWLClass getDefinitionForNegativeNominal(OWLObjectOneOf nominal,boolean[] alreadyExists) {
        OWLClass definition=m_definitionsForNegativeNominals.get(nominal);
        if (definition==null) {
            definition=m_factory.getOWLClass(URI.create("internal:nnq#"+m_definitionsForNegativeNominals.size()));
            m_definitionsForNegativeNominals.put(nominal,definition);
            alreadyExists[0]=false;
        }
        else
            alreadyExists[0]=true;
        return definition;
    }
    protected OWLClassExpression positive(OWLClassExpression description) {
        return m_expressionManager.getNNF(m_expressionManager.getSimplified(description));
    }
    protected OWLClassExpression negative(OWLClassExpression description) {
        return m_expressionManager.getComplementNNF(m_expressionManager.getSimplified(description));
    }
    protected OWLDataRange positive(OWLDataRange dataRange) {
        return m_expressionManager.getNNF(m_expressionManager.getSimplified(dataRange));
    }
    protected OWLDataRange negative(OWLDataRange dataRange) {
        return m_expressionManager.getComplementNNF(m_expressionManager.getSimplified(dataRange));
    }
    
    protected class AxiomVisitor implements OWLAxiomVisitor {
        protected final List<OWLClassExpression[]> m_inclusionsAsDisjunctions;
        protected final boolean[] m_alreadyExists;

        public AxiomVisitor() {
            m_inclusionsAsDisjunctions=new ArrayList<OWLClassExpression[]>();
            m_alreadyExists=new boolean[1];
        }

        // Semantic-less axioms
        
        public void visit(OWLImportsDeclaration axiom) {
        }
        public void visit(OWLDeclarationAxiom axiom) {
        }
        public void visit(OWLAnnotationAssertionAxiom axiom) {
        }
        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        }
        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
        }
        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        }

        // Class axioms
        
        public void visit(OWLSubClassOfAxiom axiom) {
            m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { negative(axiom.getSubClass()),positive(axiom.getSuperClass()) });
        }
        public void visit(OWLEquivalentClassesAxiom axiom) {
            if (axiom.getClassExpressions().size()>1) {
                Iterator<OWLClassExpression> iterator=axiom.getClassExpressions().iterator();
                OWLClassExpression first=iterator.next();
                OWLClassExpression last=first;
                while (iterator.hasNext()) {
                    OWLClassExpression next=iterator.next();
                    m_inclusionsAsDisjunctions.add(new OWLClassExpression[] {negative(last),positive(next) });
                    last=next;
                }
                m_inclusionsAsDisjunctions.add(new OWLClassExpression[] {negative(last),positive(first) });
            }
        }
        public void visit(OWLDisjointClassesAxiom axiom) {
            if (axiom.getClassExpressions().size() <= 1) {
                throw new IllegalArgumentException("Error: Parsed " + axiom.toString() + ". A DisjointClasses axiom in OWL 2 DL must have at least two classes as parameters. ");
            }
            OWLClassExpression[] descriptions=new OWLClassExpression[axiom.getClassExpressions().size()];
            axiom.getClassExpressions().toArray(descriptions);
            for (int i=0;i<descriptions.length;i++)
                descriptions[i]=m_expressionManager.getComplementNNF(descriptions[i]);
            for (int i=0;i<descriptions.length;i++)
                for (int j=i+1;j<descriptions.length;j++)
                    m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { descriptions[i],descriptions[j] });
        }
        public void visit(OWLDisjointUnionAxiom axiom) {
            // DisjointUnion(C CE1 ... CEn)
            // 1. add C implies CE1 or ... or CEn, which is { not C or CE1 or ... or CEn }
            Set<OWLClassExpression> inclusion=new HashSet<OWLClassExpression>(axiom.getClassExpressions());
            inclusion.add(m_expressionManager.getComplementNNF(axiom.getOWLClass()));
            OWLClassExpression[] inclusionArray=new OWLClassExpression[axiom.getClassExpressions().size()+1];
            inclusion.toArray(inclusionArray);
            m_inclusionsAsDisjunctions.add(inclusionArray);
            // 2. add CEi implies CEn implies C, which is { not CEi or C }
            for (OWLClassExpression description : axiom.getClassExpressions())
                m_inclusionsAsDisjunctions.add(new OWLClassExpression[] {negative(description),axiom.getOWLClass() });
            // 3. add CEi and CEj implies bottom (not CEi or not CEj) for 1 <= i < j <= n
            OWLClassExpression[] descriptions=new OWLClassExpression[axiom.getClassExpressions().size()];
            axiom.getClassExpressions().toArray(descriptions);
            for (int i=0;i<descriptions.length;i++)
                descriptions[i]=m_expressionManager.getComplementNNF(descriptions[i]);
            for (int i=0;i<descriptions.length;i++)
                for (int j=i+1;j<descriptions.length;j++)
                    m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { descriptions[i],descriptions[j] });
        }

        // Object property axioms
        
        public void visit(OWLSubObjectPropertyOfAxiom axiom) {
            addInclusion(axiom.getSubProperty(),axiom.getSuperProperty());
        }
        public void visit(OWLSubPropertyChainOfAxiom axiom) {
            List<OWLObjectPropertyExpression> subPropertyChain=axiom.getPropertyChain();
            OWLObjectPropertyExpression superObjectPropertyExpression=axiom.getSuperProperty();
            if (subPropertyChain.size()==1)
                addInclusion(subPropertyChain.get(0),superObjectPropertyExpression);
            else if (subPropertyChain.size()==2 && subPropertyChain.get(0).equals(superObjectPropertyExpression) && subPropertyChain.get(1).equals(superObjectPropertyExpression))
                makeTransitive(axiom.getSuperProperty());
            else {
                OWLObjectPropertyExpression[] subObjectProperties=new OWLObjectPropertyExpression[subPropertyChain.size()];
                subPropertyChain.toArray(subObjectProperties);
                addInclusion(subObjectProperties,superObjectPropertyExpression);
            }
        }
        public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            if (axiom.getProperties().size()>1) {
                Iterator<OWLObjectPropertyExpression> iterator=axiom.getProperties().iterator();
                OWLObjectPropertyExpression first=iterator.next();
                OWLObjectPropertyExpression last=first;
                while (iterator.hasNext()) {
                    OWLObjectPropertyExpression next=iterator.next();
                    addInclusion(last,next);
                    last=next;
                }
                addInclusion(last,first);
            }
        }
        public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
            OWLObjectPropertyExpression[] objectProperties=new OWLObjectPropertyExpression[axiom.getProperties().size()];
            axiom.getProperties().toArray(objectProperties);
            for (int i=0;i<objectProperties.length;i++)
                objectProperties[i]=objectProperties[i].getSimplified();
            m_axioms.m_disjointObjectProperties.add(objectProperties);
        }
        public void visit(OWLInverseObjectPropertiesAxiom axiom) {
            OWLObjectPropertyExpression first=axiom.getFirstProperty();
            OWLObjectPropertyExpression second=axiom.getSecondProperty();
            addInclusion(first,second.getInverseProperty());
            addInclusion(second,first.getInverseProperty());
        }
        public void visit(OWLObjectPropertyDomainAxiom axiom) {
            OWLObjectAllValuesFrom allPropertyNohting=m_factory.getOWLObjectAllValuesFrom(axiom.getProperty().getSimplified(),m_factory.getOWLNothing());
            m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { positive(axiom.getDomain()),allPropertyNohting });
        }
        public void visit(OWLObjectPropertyRangeAxiom axiom) {
            OWLObjectAllValuesFrom allPropertyRange=m_factory.getOWLObjectAllValuesFrom(axiom.getProperty().getSimplified(),positive(axiom.getRange()));
            m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { allPropertyRange });
        }
        public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
            m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { m_factory.getOWLObjectMaxCardinality(axiom.getProperty().getSimplified(),1) });
        }
        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { m_factory.getOWLObjectMaxCardinality(axiom.getProperty().getSimplified().getInverseProperty(),1) });
        }
        public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
            makeReflexive(axiom.getProperty());
        }
        public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            makeIrreflexive(axiom.getProperty());
        }
        public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
            OWLObjectPropertyExpression objectProperty=axiom.getProperty();
            addInclusion(objectProperty,objectProperty.getInverseProperty());
        }
        public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            makeAsymmetric(axiom.getProperty());
        }
        public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
            makeTransitive(axiom.getProperty());
        }

        // Data property axioms
        
        public void visit(OWLDatatypeDefinitionAxiom axiom) {
            m_axioms.m_customDatatypeDefinitions.put(axiom.getDatatype(), axiom.getDataRange());
        }
        
        public void visit(OWLSubDataPropertyOfAxiom axiom) {
            OWLDataPropertyExpression subDataProperty=axiom.getSubProperty();
            OWLDataPropertyExpression superDataProperty=axiom.getSuperProperty();
            if (subDataProperty.isOWLTopDataProperty()) {
                throw new IllegalArgumentException("Error: In OWL 2 DL, owl:topDataProperty is only allowed to occur in the super property position of SubDataPropertyOf axioms, but the ontology contains an axiom SubDataPropertyOf(owl:topDataProperty " + subDataProperty.asOWLDataProperty().getIRI() + ").");
            } else if (!superDataProperty.isOWLTopDataProperty()) {
                addInclusion(subDataProperty,superDataProperty);
            }
        }
        public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
            if (axiom.getProperties().size()>1) {
                Iterator<OWLDataPropertyExpression> iterator=axiom.getProperties().iterator();
                OWLDataPropertyExpression first=iterator.next();
                OWLDataPropertyExpression last=first;
                while (iterator.hasNext()) {
                    OWLDataPropertyExpression next=iterator.next();
                    addInclusion(last,next);
                    last=next;
                }
                addInclusion(last,first);
            }
        }
        public void visit(OWLDisjointDataPropertiesAxiom axiom) {
            OWLDataPropertyExpression[] dataProperties=new OWLDataPropertyExpression[axiom.getProperties().size()];
            axiom.getProperties().toArray(dataProperties);
            m_axioms.m_disjointDataProperties.add(dataProperties);
        }
        public void visit(OWLDataPropertyDomainAxiom axiom) {
            OWLDataRange dataNothing=m_factory.getOWLDataComplementOf(m_factory.getTopDatatype());
            OWLDataAllValuesFrom allPropertyDataNothing=m_factory.getOWLDataAllValuesFrom(axiom.getProperty(),dataNothing);
            m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { positive(axiom.getDomain()),allPropertyDataNothing });
        }
        public void visit(OWLDataPropertyRangeAxiom axiom) {
            OWLDataAllValuesFrom allPropertyRange=m_factory.getOWLDataAllValuesFrom(axiom.getProperty(),positive(axiom.getRange()));
            m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { allPropertyRange });
        }
        public void visit(OWLFunctionalDataPropertyAxiom axiom) {
            m_inclusionsAsDisjunctions.add(new OWLClassExpression[] { m_factory.getOWLDataMaxCardinality(axiom.getProperty(),1) });
        }

        // Keys
        
        public void visit(OWLHasKeyAxiom axiom) {
            OWLClassExpression description=positive(axiom.getClassExpression());
            if (!isSimple(description)) {
                OWLClassExpression definition=getDefinitionFor(description,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_inclusionsAsDisjunctions.add(new OWLClassExpression[] {negative(definition),description });
                description=definition;
            }
            if (description==axiom.getClassExpression())
                addHasKey(axiom);
            else {
                // Construct a new axiom that uses the concept definition.
                Set<OWLPropertyExpression<OWLObjectPropertyExpression,OWLClassExpression>> objProps = new HashSet<OWLPropertyExpression<OWLObjectPropertyExpression,OWLClassExpression>>(axiom.getObjectPropertyExpressions());
                if (!objProps.isEmpty()) {
                    addHasKey(m_factory.getOWLHasKeyAxiom(description, objProps));
                } else {
                    Set<OWLPropertyExpression<OWLDataPropertyExpression,OWLDataRange>> dataProps = new HashSet<OWLPropertyExpression<OWLDataPropertyExpression,OWLDataRange>>(axiom.getDataPropertyExpressions());
                    addHasKey(m_factory.getOWLHasKeyAxiom(description, dataProps));
                }
            }
        }

        
        // Assertions
        
        public void visit(OWLSameIndividualAxiom axiom) {
            addFact(axiom);
        }
        public void visit(OWLDifferentIndividualsAxiom axiom) {
            addFact(axiom);
        }
        public void visit(OWLClassAssertionAxiom axiom) {
            OWLClassExpression description=positive(axiom.getClassExpression());
            if (!isSimple(description)) {
                OWLClassExpression definition=getDefinitionFor(description,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_inclusionsAsDisjunctions.add(new OWLClassExpression[] {negative(definition),description });
                description=definition;
            }
            if (description==axiom.getClassExpression())
                addFact(axiom);
            else
                addFact(m_factory.getOWLClassAssertionAxiom(description,axiom.getIndividual()));
        }
        public void visit(OWLObjectPropertyAssertionAxiom axiom) {
            addFact(m_factory.getOWLObjectPropertyAssertionAxiom(axiom.getSubject(),axiom.getProperty().getSimplified(),axiom.getObject()));
        }
        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            OWLObjectOneOf nominal=m_factory.getOWLObjectOneOf(axiom.getObject());
            OWLClassExpression notNominal=m_factory.getOWLObjectComplementOf(nominal);
            OWLClassExpression allNotNominal=m_factory.getOWLObjectAllValuesFrom(axiom.getProperty().getSimplified(),notNominal);
            OWLClassExpression definition=getDefinitionFor(allNotNominal,m_alreadyExists);
            if (!m_alreadyExists[0])
                m_inclusionsAsDisjunctions.add(new OWLClassExpression[] {negative(definition),allNotNominal });
            addFact(m_factory.getOWLClassAssertionAxiom(definition,axiom.getSubject()));
        }
        public void visit(OWLDataPropertyAssertionAxiom axiom) {
            addFact(axiom);
        }
        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            addFact(axiom);
//            OWLDataOneOf oneOf=m_factory.getOWLDataOneOf(axiom.getObject());
//            OWLDataRange notOneOf=m_factory.getOWLDataComplementOf(oneOf);
//            OWLClassExpression allNotOneOf=m_factory.getOWLDataAllValuesFrom(axiom.getProperty(),notOneOf);
//            OWLClassExpression definition=getDefinitionFor(allNotOneOf,m_alreadyExists);
//            if (!m_alreadyExists[0])
//                m_inclusionsAsDisjunctions.add(new OWLClassExpression[] {negative(definition),allNotOneOf });
//            addFact(m_factory.getOWLClassAssertionAxiom(axiom.getSubject(),definition));
        }

        // Rules
        
        public void visit(SWRLRule rule) {
            throw new IllegalArgumentException("SWRL rules are not supported yet.");
        }
    }

    protected class NormalizationVisitor implements OWLClassExpressionVisitorEx<OWLClassExpression> {
        protected final Collection<OWLClassExpression[]> m_newInclusions;
        protected final List<OWLDataRange[]> m_newDataRangeInclusions;
        protected final boolean[] m_alreadyExists;

        public NormalizationVisitor(Collection<OWLClassExpression[]> newInclusions) {
            m_newInclusions=newInclusions;
            m_newDataRangeInclusions=new ArrayList<OWLDataRange[]>();
            m_alreadyExists=new boolean[1];
        }
        public OWLClassExpression visit(OWLClass object) {
            return object;
        }
        public OWLClassExpression visit(OWLObjectIntersectionOf object) {
            OWLClassExpression definition=getDefinitionFor(object,m_alreadyExists);
            if (!m_alreadyExists[0])
                for (OWLClassExpression description : object.getOperands())
                    m_newInclusions.add(new OWLClassExpression[] {negative(definition),description });
            return definition;
        }
        public OWLClassExpression visit(OWLObjectUnionOf object) {
            throw new IllegalStateException("OR should be broken down at the outermost level");
        }
        public OWLClassExpression visit(OWLObjectComplementOf object) {
            if (isNominal(object.getOperand())) {
                OWLObjectOneOf objectOneOf=(OWLObjectOneOf)object.getOperand();
                OWLClass definition=getDefinitionForNegativeNominal(objectOneOf,m_alreadyExists);
                if (!m_alreadyExists[0])
                    for (OWLIndividual individual : objectOneOf.getIndividuals())
                        addFact(m_factory.getOWLClassAssertionAxiom(definition,individual));
                return m_factory.getOWLObjectComplementOf(definition);
            }
            else
                return object;
        }
        public OWLClassExpression visit(OWLObjectOneOf object) {
            return object;
        }
        public OWLClassExpression visit(OWLObjectSomeValuesFrom object) {
            OWLClassExpression filler=object.getFiller();
            if (isSimple(filler) || isNominal(filler))
                // The ObjectOneof cases is an optimization.
                return object;
            else {
                OWLClassExpression definition=getDefinitionFor(filler,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_newInclusions.add(new OWLClassExpression[] {negative(definition),filler });
                return m_factory.getOWLObjectSomeValuesFrom(object.getProperty(),definition);
            }
        }
        public OWLClassExpression visit(OWLObjectAllValuesFrom object) {
            OWLClassExpression filler=object.getFiller();
            if (isSimple(filler) || isNominal(filler) || isNegatedOneNominal(filler))
                // The nominal cases are optimizations.
                return object;
            else {
                OWLClassExpression definition=getDefinitionFor(filler,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_newInclusions.add(new OWLClassExpression[] {negative(definition),filler });
                return m_factory.getOWLObjectAllValuesFrom(object.getProperty(),definition);
            }
        }
        public OWLClassExpression visit(OWLObjectHasValue object) {
            throw new IllegalStateException("Internal error: object value restrictions should have been simplified.");
        }
        public OWLClassExpression visit(OWLObjectHasSelf object) {
            return object;
        }
        public OWLClassExpression visit(OWLObjectMinCardinality object) {
            OWLClassExpression filler=object.getFiller();
            if (isSimple(filler))
                return object;
            else {
                OWLClassExpression definition=getDefinitionFor(filler,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_newInclusions.add(new OWLClassExpression[] {negative(definition),filler });
                return m_factory.getOWLObjectMinCardinality(object.getProperty(),object.getCardinality(),definition);
            }
        }
        public OWLClassExpression visit(OWLObjectMaxCardinality object) {
            OWLClassExpression filler=object.getFiller();
            if (isSimple(filler))
                return object;
            else {
                OWLClassExpression complementDescription=m_expressionManager.getComplementNNF(filler);
                OWLClassExpression definition=getDefinitionFor(complementDescription,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_newInclusions.add(new OWLClassExpression[] {negative(definition),complementDescription });
                return m_factory.getOWLObjectMaxCardinality(object.getProperty(),object.getCardinality(),m_expressionManager.getComplementNNF(definition));
            }
        }
        public OWLClassExpression visit(OWLObjectExactCardinality object) {
            throw new IllegalStateException("Internal error: exact object cardinality restrictions should have been simplified.");
        }
        protected OWLDataRange replaceCustomDatatype(OWLDataRange range){
            if (range instanceof OWLDatatype && m_axioms.m_customDatatypeDefinitions.containsKey((OWLDatatype) range)) {
                return m_axioms.m_customDatatypeDefinitions.get(range);
            } else if (range instanceof OWLDataComplementOf) {
                OWLDataRange dr = ((OWLDataComplementOf)range).getDataRange();
                if (dr instanceof OWLDatatype && m_axioms.m_customDatatypeDefinitions.containsKey((OWLDatatype) dr)) {
                    return m_expressionManager.getNNF(m_factory.getOWLDataComplementOf(m_axioms.m_customDatatypeDefinitions.get((OWLDatatype) dr)));
                }
            }
            return range;
        }
        public OWLClassExpression visit(OWLDataSomeValuesFrom object) {
            OWLDataRange filler=replaceCustomDatatype(object.getFiller());
            if (isLiteral(filler)) {
                return m_factory.getOWLDataSomeValuesFrom(object.getProperty(),filler);
            } else {
                OWLDatatype definition=getDefinitionFor(filler,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_newDataRangeInclusions.add(new OWLDataRange[] { negative(definition),filler } );
                return m_factory.getOWLDataSomeValuesFrom(object.getProperty(),definition);
            }
        }
        public OWLClassExpression visit(OWLDataAllValuesFrom object) {
            OWLDataRange filler=replaceCustomDatatype(object.getFiller());
            if (isLiteral(filler)) {
                return m_factory.getOWLDataAllValuesFrom(object.getProperty(),filler);
            } else {
                OWLDatatype definition=getDefinitionFor(filler,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_newDataRangeInclusions.add(new OWLDataRange[] { negative(definition),filler } );
                return m_factory.getOWLDataAllValuesFrom(object.getProperty(),definition);
            }
        }
        public OWLClassExpression visit(OWLDataHasValue object) {
            throw new IllegalStateException("Internal error: data value restrictions should have been simplified.");
        }
        public OWLClassExpression visit(OWLDataMinCardinality object) {
            OWLDataRange filler=replaceCustomDatatype(object.getFiller());
            if (isLiteral(filler))
                return m_factory.getOWLDataMinCardinality(object.getProperty(),object.getCardinality(),filler);
            else {
                OWLDatatype definition=getDefinitionFor(filler,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_newDataRangeInclusions.add(new OWLDataRange[] { negative(definition),filler } );
                return m_factory.getOWLDataMinCardinality(object.getProperty(),object.getCardinality(),definition);
            }
        }
        public OWLClassExpression visit(OWLDataMaxCardinality object) {
            OWLDataRange filler=replaceCustomDatatype(object.getFiller());
            if (isLiteral(filler))
                return m_factory.getOWLDataMaxCardinality(object.getProperty(),object.getCardinality(),filler);
            else {
                OWLDataRange complementDescription=m_expressionManager.getComplementNNF(filler);
                OWLDatatype definition=getDefinitionFor(complementDescription,m_alreadyExists);
                if (!m_alreadyExists[0])
                    m_newDataRangeInclusions.add(new OWLDataRange[] { negative(definition),filler } );
                return m_factory.getOWLDataMaxCardinality(object.getProperty(),object.getCardinality(),m_expressionManager.getComplementNNF(definition));
            }
        }
        public OWLClassExpression visit(OWLDataExactCardinality object) {
            throw new IllegalStateException("Internal error: exact data cardinality restrictions should have been simplified.");
        }
    }
    
    protected class DataRangeNormalizationVisitor implements OWLDataVisitorEx<OWLDataRange> {
        protected final List<OWLDataRange[]> m_newDataRangeInclusions;
        protected final boolean[] m_alreadyExists;

        public DataRangeNormalizationVisitor(List<OWLDataRange[]> newInclusions) {
            m_newDataRangeInclusions=newInclusions;
            m_alreadyExists=new boolean[1];
        }
        public OWLDataRange visit(OWLDatatype node) {
            return node;
        }
        public OWLDataRange visit(OWLDataComplementOf node) {
            return node;
        }
        public OWLDataRange visit(OWLDataOneOf node) {
            return node;
        }
        public OWLDataRange visit(OWLDataIntersectionOf object) {
            OWLDataRange definition=getDefinitionFor(object,m_alreadyExists);
            if (!m_alreadyExists[0])
                for (OWLDataRange description : object.getOperands())
                    m_newDataRangeInclusions.add(new OWLDataRange[] {negative(definition),description });
            return definition;
        }
        public OWLDataRange visit(OWLDataUnionOf node) {
            throw new IllegalStateException("OR should be broken down at the outermost level");
        }
        public OWLDataRange visit(OWLDatatypeRestriction node) {
            return node;
        }
        public OWLDataRange visit(OWLTypedLiteral node) {
            throw new IllegalStateException("Internal error: We shouldn't visit typed literals during normalization. ");
        }

        public OWLDataRange visit(OWLStringLiteral node) {
            throw new IllegalStateException("Internal error: We shouldn't visit typed literals during normalization. ");
        }

        public OWLDataRange visit(OWLFacetRestriction node) {
            throw new IllegalStateException("Internal error: We shouldn't visit facet restrictions during normalization. ");
        }
    }

    /**
     * checks the polarity
     */
    protected class PLVisitor implements OWLClassExpressionVisitorEx<Boolean> {

        public Boolean visit(OWLClass object) {
            if (object.isOWLThing())
                return Boolean.FALSE;
            else if (object.isOWLNothing())
                return Boolean.FALSE;
            else
                return Boolean.TRUE;
        }
        public Boolean visit(OWLObjectIntersectionOf object) {
            for (OWLClassExpression desc : object.getOperands())
                if (desc.accept(this))
                    return Boolean.TRUE;
            return Boolean.FALSE;
        }
        public Boolean visit(OWLObjectUnionOf object) {
            for (OWLClassExpression desc : object.getOperands())
                if (desc.accept(this))
                    return Boolean.TRUE;
            return Boolean.FALSE;
        }
        public Boolean visit(OWLObjectComplementOf object) {
            return Boolean.FALSE;
        }
        public Boolean visit(OWLObjectOneOf object) {
            return Boolean.TRUE;
        }
        public Boolean visit(OWLObjectSomeValuesFrom object) {
            return Boolean.TRUE;
        }
        public Boolean visit(OWLObjectAllValuesFrom object) {
            return object.getFiller().accept(this);
        }
        public Boolean visit(OWLObjectHasValue object) {
            return Boolean.TRUE;
        }
        public Boolean visit(OWLObjectHasSelf object) {
            return Boolean.TRUE;
        }
        public Boolean visit(OWLObjectMinCardinality object) {
            return object.getCardinality()>0;
        }
        public Boolean visit(OWLObjectMaxCardinality object) {
            return object.getCardinality()>0 ? Boolean.TRUE : m_expressionManager.getComplementNNF(object.getFiller()).accept(this);
        }
        public Boolean visit(OWLObjectExactCardinality object) {
            return object.getCardinality()>0 ? Boolean.TRUE : m_expressionManager.getComplementNNF(object.getFiller()).accept(this);
        }
        public Boolean visit(OWLDataSomeValuesFrom desc) {
            return Boolean.TRUE;
        }
        public Boolean visit(OWLDataAllValuesFrom desc) {
            return Boolean.TRUE;
        }
        public Boolean visit(OWLDataHasValue desc) {
            return Boolean.TRUE;
        }
        public Boolean visit(OWLDataMinCardinality desc) {
            return Boolean.TRUE;
        }
        public Boolean visit(OWLDataMaxCardinality desc) {
            return Boolean.TRUE;
        }
        public Boolean visit(OWLDataExactCardinality desc) {
            return Boolean.TRUE;
        }
    }
}
