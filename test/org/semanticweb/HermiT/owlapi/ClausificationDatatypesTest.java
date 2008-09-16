package org.semanticweb.HermiT.owlapi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.HermiT;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.DLOntology;
import org.semanticweb.HermiT.model.DescriptionGraph;
import org.semanticweb.HermiT.owlapi.structural.OwlClausification;
import org.semanticweb.HermiT.owlapi.structural.OwlNormalization;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

public class ClausificationDatatypesTest extends AbstractOWLOntologyTest {

    public ClausificationDatatypesTest(String name) {
        super(name);
    }
    
    public void testDataPropertiesHasValue1() throws Exception {
        String axioms = "SubClassOf(Eighteen DataHasValue(hasAge \"18\"^^xsd:integer))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("atLeast(1 a:hasAge* (xsd:integer 18))(X) :- a:Eighteen(X)");
        assertContainsAll(clauses, expectedClauses);
    }

    public void testDataPropertiesHasValue2() throws Exception {
        String axioms = "SubClassOf(DataHasValue(hasAge \"18\"^^xsd:integer) Eighteen)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:Eighteen(X) v (not(xsd:integer 18))(Y) :- a:hasAge*(X,Y)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesAll1() throws Exception {
        String axioms = "SubClassOf(A DataAllValuesFrom(dp xsd:integer))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("(xsd:integer)(Y) :- a:A(X), a:dp*(X,Y)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesAll2() throws Exception {
        String axioms = "SubClassOf(DataAllValuesFrom(dp xsd:integer) A)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:A(X) v atLeast(1 a:dp* (not(xsd:integer)))(X) :- owl:Thing(X)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesSome1() throws Exception {
        String axioms = "SubClassOf(DataSomeValuesFrom(dp xsd:string) A)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:A(X) v (not(xsd:string))(Y) :- a:dp*(X,Y)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesSome2() throws Exception {
        String axioms = "SubClassOf(A DataSomeValuesFrom(dp xsd:string))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("atLeast(1 a:dp* (xsd:string))(X) :- a:A(X)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesDataOneOf1() throws Exception {
        String axioms = "SubClassOf(A DataAllValuesFrom(dp DataOneOf(\"Peter\"^^xsd:string \"19\"^^xsd:integer)))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("(xsd:integer 19)(Y) v (xsd:string Peter)(Y) :- a:A(X), a:dp*(X,Y)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesDataOneOf2() throws Exception {
        String axioms = "SubClassOf(DataAllValuesFrom(dp DataOneOf(\"18\"^^xsd:integer \"19\"^^xsd:integer)) A)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:A(X) v atLeast(1 a:dp* (not(xsd:integer 18)))(X) v atLeast(1 a:dp* (not(xsd:integer 19)))(X) :- owl:Thing(X)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesDataComplementOf1() throws Exception {
        String axioms = "SubClassOf(A DataAllValuesFrom(dp DataComplementOf(DataComplementOf(DataOneOf(\"18\"^^xsd:integer \"19\"^^xsd:integer)))))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("(xsd:integer 18)(Y) v (xsd:integer 19)(Y) :- a:A(X), a:dp*(X,Y)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesMax1() throws Exception {
        String axioms = "SubClassOf(A DataMaxCardinality(1 dp xsd:string))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("(not(xsd:string))(Y1) v (not(xsd:string))(Y2) v Y1 == Y2 :- a:A(X), a:dp*(X,Y1), a:dp*(X,Y2)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesMax2() throws Exception {
        String axioms = "SubClassOf(DataMaxCardinality(1 dp xsd:string) A)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:A(X) v atLeast(2 a:dp* (xsd:string))(X) :- owl:Thing(X)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesMax3() throws Exception {
        String axioms = "SubClassOf(A DataMaxCardinality(3 dp xsd:integer))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("(not(xsd:integer))(Y1) v (not(xsd:integer))(Y2) v (not(xsd:integer))(Y3) v (not(xsd:integer))(Y4) v Y1 == Y2 v Y1 == Y3 v Y1 == Y4 v Y2 == Y3 v Y2 == Y4 v Y3 == Y4 :- a:A(X), a:dp*(X,Y1), a:dp*(X,Y2), a:dp*(X,Y3), a:dp*(X,Y4)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesMax4() throws Exception {
        String axioms = "SubClassOf(DataMaxCardinality(3 dp xsd:integer) A)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:A(X) v atLeast(4 a:dp* (xsd:integer))(X) :- owl:Thing(X)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesMin1() throws Exception {
        String axioms = "SubClassOf(DataMinCardinality(1 dp xsd:string) A)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:A(X) v (not(xsd:string))(Y1) :- a:dp*(X,Y1)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesMin2() throws Exception {
        String axioms = "SubClassOf(DataMinCardinality(3 dp xsd:string) A)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:A(X) v (not(xsd:string))(Y1) v (not(xsd:string))(Y2) v (not(xsd:string))(Y3) v Y1 == Y2 v Y1 == Y3 v Y2 == Y3 :- a:dp*(X,Y1), a:dp*(X,Y2), a:dp*(X,Y3)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesMin3() throws Exception {
        String axioms = "SubClassOf(A DataMinCardinality(1 dp xsd:string))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("atLeast(1 a:dp* (xsd:string))(X) :- a:A(X)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesMin4() throws Exception {
        String axioms = "SubClassOf(A DataMinCardinality(5 dp xsd:string))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("atLeast(5 a:dp* (xsd:string))(X) :- a:A(X)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesExact1() throws Exception {
        String axioms = "SubClassOf(A DataExactCardinality(1 dp xsd:integer))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("(not(xsd:integer))(Y1) v (not(xsd:integer))(Y2) v Y1 == Y2 :- a:A(X), a:dp*(X,Y1), a:dp*(X,Y2)");
        expectedClauses.add("atLeast(1 a:dp* (xsd:integer))(X) :- a:A(X)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesExact2() throws Exception {
        String axioms = "SubClassOf(A DataExactCardinality(3 dp xsd:integer))";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("(not(xsd:integer))(Y1) v (not(xsd:integer))(Y2) v (not(xsd:integer))(Y3) v (not(xsd:integer))(Y4) v Y1 == Y2 v Y1 == Y3 v Y1 == Y4 v Y2 == Y3 v Y2 == Y4 v Y3 == Y4 :- a:A(X), a:dp*(X,Y1), a:dp*(X,Y2), a:dp*(X,Y3), a:dp*(X,Y4)");
        expectedClauses.add("atLeast(3 a:dp* (xsd:integer))(X) :- a:A(X)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesExact3() throws Exception {
        String axioms = "SubClassOf(DataExactCardinality(1 dp xsd:integer) A)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:A(X) v atLeast(2 a:dp* (xsd:integer))(X) v (not(xsd:integer))(Y1) :- a:dp*(X,Y1)");
        assertContainsAll(clauses, expectedClauses);
    }
    
    public void testDataPropertiesExact4() throws Exception {
        String axioms = "SubClassOf(DataExactCardinality(3 dp xsd:integer) A)";
        OWLOntology ontology = getOWLOntologyWithAxioms(axioms);
        Set<String> clauses = getDLClauses(ontology);
        Set<String> expectedClauses = new HashSet<String>();
        expectedClauses.add("a:A(X) v atLeast(4 a:dp* (xsd:integer))(X) v (not(xsd:integer))(Y1) v (not(xsd:integer))(Y2) v (not(xsd:integer))(Y3) v Y1 == Y2 v Y1 == Y3 v Y2 == Y3 :- a:dp*(X,Y1), a:dp*(X,Y2), a:dp*(X,Y3)");
        assertContainsAll(clauses, expectedClauses);
    }

    protected Set<OWLAxiom> getNormalizedAxioms(OWLOntology ontology) 
            throws Exception {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OwlNormalization normalization = new OwlNormalization(factory);
        normalization.processOntology(ontology);
        for (OWLDescription[] inclusion : normalization.getConceptInclusions()) {
            OWLDescription superDescription;
            if (inclusion.length == 1) {
                superDescription = inclusion[0];
            } else {
                superDescription = factory.getOWLObjectUnionOf(inclusion);
            }
            axioms.add(factory.getOWLSubClassAxiom(factory.getOWLThing(),
                    superDescription));
        }
        for (OWLObjectPropertyExpression[] inclusion : normalization.getObjectPropertyInclusions())
            axioms.add(factory.getOWLSubObjectPropertyAxiom(inclusion[0],
                    inclusion[1]));
        axioms.addAll(normalization.getFacts());
        return axioms;
    }
    
    protected Set<OWLAxiom> getNormalizedAxioms(String resourceName)
            throws Exception {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLOntology ontology = getOWLOntologyFromResource(resourceName);
        OwlNormalization normalization = new OwlNormalization(factory);
        normalization.processOntology(ontology);
        for (OWLDescription[] inclusion : normalization.getConceptInclusions()) {
            OWLDescription superDescription;
            if (inclusion.length == 1) {
                superDescription = inclusion[0];
            } else {
                superDescription = factory.getOWLObjectUnionOf(inclusion);
            }
            axioms.add(factory.getOWLSubClassAxiom(factory.getOWLThing(),
                    superDescription));
        }
        for (OWLObjectPropertyExpression[] inclusion : normalization.getObjectPropertyInclusions())
            axioms.add(factory.getOWLSubObjectPropertyAxiom(inclusion[0],
                    inclusion[1]));
        axioms.addAll(normalization.getFacts());
        return axioms;
    }

    protected void assertNormalization(String inputResourceName,
            String controlResourceName) throws Exception {
        Set<OWLAxiom> normlizedAxioms = getNormalizedAxioms(inputResourceName);
        assertEquals(normlizedAxioms, controlResourceName);
    }
    
    protected Set<String> getDLClauses(OWLOntology ontology) throws Exception {
        OwlClausification clausifier = new OwlClausification();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        Set<DescriptionGraph> noDescriptionGraphs = Collections.emptySet();
        DLOntology dlOntology = clausifier.clausify(new HermiT.Configuration(), ontology, factory,
                noDescriptionGraphs);
        Set<String> actualStrings = new HashSet<String>();
        org.semanticweb.HermiT.Namespaces namespaces = new org.semanticweb.HermiT.Namespaces();
        namespaces.registerPrefix("a", ontology.getURI() + "#");
        namespaces.registerStandardPrefixes();
        namespaces.registerInternalPrefixes(ontology.getURI().toString());
        for (DLClause dlClause : dlOntology.getDLClauses())
            actualStrings.add(dlClause.toString(namespaces));
        for (org.semanticweb.HermiT.model.Atom atom : dlOntology.getPositiveFacts())
            actualStrings.add(atom.toString(namespaces));
        for (org.semanticweb.HermiT.model.Atom atom : dlOntology.getNegativeFacts())
            actualStrings.add("not " + atom.toString(namespaces));
        return actualStrings;
    }
}