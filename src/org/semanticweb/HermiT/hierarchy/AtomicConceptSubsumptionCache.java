/* Copyright 2008, 2009, 2010 by the Oxford University Computing Laboratory

   This file is part of HermiT.

   HermiT is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   HermiT is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with HermiT.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.semanticweb.HermiT.hierarchy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Prefixes;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.model.AtomicConcept;
import org.semanticweb.HermiT.tableau.ExtensionTable;
import org.semanticweb.HermiT.tableau.Node;
import org.semanticweb.HermiT.tableau.Tableau;

/**
 * A cache for concept subsumption and concept satisfiability tests. This class also maintains the set of known and possible subsumers
 * for a concept. This information can be used to optimize classification.
 */
public class AtomicConceptSubsumptionCache implements Serializable,SubsumptionCache<AtomicConcept> {
    private static final long serialVersionUID = 5380180660934814631L;
    protected final Tableau m_tableau;
    protected final Map<AtomicConcept,AtomicConceptInfo> m_atomicConceptInfos;

    public AtomicConceptSubsumptionCache(Reasoner reasoner) {
        m_tableau=reasoner.getTableau();
        m_atomicConceptInfos=new HashMap<AtomicConcept,AtomicConceptInfo>();
    }
    public Set<AtomicConcept> getAllKnownSubsumers(AtomicConcept atomicConcept) {
        boolean isSatisfiable=isSatisfiable(atomicConcept,false);
        AtomicConceptInfo conceptInfo=m_atomicConceptInfos.get(atomicConcept);
        if (isSatisfiable) {
            if (!conceptInfo.m_allSubsumersKnown)
                throw new IllegalStateException("Not all subsumers are known for '"+atomicConcept.getIRI()+"'.");
            return conceptInfo.m_knownSubsumers;
        }
        else
            return null;
    }
    public boolean isSatisfiable(AtomicConcept concept) {
        return isSatisfiable(concept,true);
    }
    protected boolean isSatisfiable(AtomicConcept concept,boolean updatePossibleSubsumers) {
        if (AtomicConcept.NOTHING.equals(concept))
            return false;
        AtomicConceptInfo conceptInfo=getAtomicConceptInfo(concept);
        if (conceptInfo.m_isSatisfiable==null) {
            boolean isSatisfiable=m_tableau.isSatisfiable(concept);
            conceptInfo.m_isSatisfiable=(isSatisfiable ? Boolean.TRUE : Boolean.FALSE);
            if (isSatisfiable) {
                updateKnownSubsumers(concept);
                if (updatePossibleSubsumers)
                    updatePossibleSubsumers();
            }
        }
        return conceptInfo.m_isSatisfiable;
    }
    public boolean isSubsumedBy(AtomicConcept subconcept,AtomicConcept superconcept) {
        if (AtomicConcept.THING.equals(superconcept) || AtomicConcept.NOTHING.equals(subconcept))
            return true;
        AtomicConceptInfo subconceptInfo=getAtomicConceptInfo(subconcept);
        if (Boolean.FALSE.equals(subconceptInfo.m_isSatisfiable))
            return true;
        else if (AtomicConcept.NOTHING.equals(superconcept) || (m_atomicConceptInfos.containsKey(superconcept) && Boolean.FALSE.equals(m_atomicConceptInfos.get(superconcept).m_isSatisfiable)))
            return !isSatisfiable(subconcept,true);
        if (subconceptInfo.isKnownSubsumer(superconcept))
            return true;
        else if (subconceptInfo.isKnownNotSubsumer(superconcept))
            return false;
        // Perform the actual satisfiability test
        if (!m_tableau.isDeterministic()) {
            // A -> B?
            boolean isSubsumedBy=m_tableau.isSubsumedBy(subconcept,superconcept);
            // try and build a model for A and not B
            if (m_tableau.getExtensionManager().containsClash() && m_tableau.getExtensionManager().getClashDependencySet().isEmpty()) {
                // Tableau.isSubsumedBy() adds not B with a dummy nonempty dependency set. Therefore, if not B contributes to the clash,
                // the clash dependency set will not be empty, and we will not be in this case. In other words, if the clash dependency set
                // is empty, then we know that the clash does not depend on not B, so A is unsatisfiable.
                subconceptInfo.m_isSatisfiable=Boolean.FALSE;
            }
            else if (!isSubsumedBy) {
                subconceptInfo.m_isSatisfiable=Boolean.TRUE; // A is satisfiable since A and not B has a model
                updateKnownSubsumers(subconcept);
                updatePossibleSubsumers();
            }
            else
                subconceptInfo.addKnownSubsumer(superconcept);
            return isSubsumedBy;
        }
        else {
            isSatisfiable(subconcept,true);
            assert subconceptInfo.m_allSubsumersKnown;
            return subconceptInfo.isKnownSubsumer(superconcept);
        }
    }
    protected void updateKnownSubsumers(AtomicConcept subconcept) {
        Node checkedNode=m_tableau.getCheckedNode0();
        AtomicConceptInfo subconceptInfo=getAtomicConceptInfo(subconcept);
        if (checkedNode.getCanonicalNodeDependencySet().isEmpty()) {
            checkedNode=checkedNode.getCanonicalNode();
            subconceptInfo.addKnownSubsumer(AtomicConcept.THING);
            ExtensionTable.Retrieval retrieval=m_tableau.getExtensionManager().getBinaryExtensionTable().createRetrieval(new boolean[] { false,true },ExtensionTable.View.TOTAL);
            retrieval.getBindingsBuffer()[1]=checkedNode;
            retrieval.open();
            while (!retrieval.afterLast()) {
                Object concept=retrieval.getTupleBuffer()[0];
                if (concept instanceof AtomicConcept && retrieval.getDependencySet().isEmpty() && !Prefixes.isInternalIRI( ((AtomicConcept)concept).getIRI()))
                    subconceptInfo.addKnownSubsumer((AtomicConcept)concept);
                retrieval.next();
            }
            if (m_tableau.isCurrentModelDeterministic())
                subconceptInfo.setAllSubsumersKnown();
        }
        else if (subconceptInfo.m_knownSubsumers==null)
        	subconceptInfo.m_knownSubsumers=new HashSet<AtomicConcept>();
    }
    protected void updatePossibleSubsumers() {
        ExtensionTable.Retrieval retrieval=m_tableau.getExtensionManager().getBinaryExtensionTable().createRetrieval(new boolean[] { false,false },ExtensionTable.View.TOTAL);
        retrieval.open();
        Object[] tupleBuffer=retrieval.getTupleBuffer();
        while (!retrieval.afterLast()) {
            Object conceptObject=tupleBuffer[0];
            if (conceptObject instanceof AtomicConcept) {
                AtomicConcept atomicConcept=(AtomicConcept)conceptObject;
                if( !Prefixes.isInternalIRI( atomicConcept.getIRI() ) ){
	                Node node=(Node)tupleBuffer[1];
	                if (node.isActive() && !node.isBlocked())
	                    getAtomicConceptInfo(atomicConcept).updatePossibleSubsumers(m_tableau,node);
                }
            }
            retrieval.next();
        }
    }
    protected AtomicConceptInfo getAtomicConceptInfo(AtomicConcept atomicConcept) {
        AtomicConceptInfo result=m_atomicConceptInfos.get(atomicConcept);
        if (result==null) {
            result=new AtomicConceptInfo();
            m_atomicConceptInfos.put(atomicConcept,result);
        }
        return result;
    }

    protected static final class AtomicConceptInfo {
        protected Boolean m_isSatisfiable;
        protected Set<AtomicConcept> m_knownSubsumers;
        protected Set<AtomicConcept> m_possibleSubsumers;
        protected boolean m_allSubsumersKnown;

        public boolean isKnownSubsumer(AtomicConcept potentialSubsumer) {
            return m_knownSubsumers!=null && m_knownSubsumers.contains(potentialSubsumer);
        }
        public void addKnownSubsumer(AtomicConcept atomicConcept) {
            if (m_knownSubsumers==null)
                m_knownSubsumers=new HashSet<AtomicConcept>();
            m_knownSubsumers.add(atomicConcept);
        }
        public void setAllSubsumersKnown() {
            m_allSubsumersKnown=true;
            m_possibleSubsumers=m_knownSubsumers;
        }
        public boolean isKnownNotSubsumer(AtomicConcept potentialSubsumer) {
            return (!isKnownSubsumer(potentialSubsumer) && m_allSubsumersKnown) || (m_possibleSubsumers!=null && !m_possibleSubsumers.contains(potentialSubsumer));
        }
        public void updatePossibleSubsumers(Tableau tableau,Node node) {
            if (!m_allSubsumersKnown) {
                if (m_possibleSubsumers==null) {
                    m_possibleSubsumers=new HashSet<AtomicConcept>();
                    ExtensionTable.Retrieval retrieval=tableau.getExtensionManager().getBinaryExtensionTable().createRetrieval(new boolean[] { false,true },ExtensionTable.View.TOTAL);
                    retrieval.getBindingsBuffer()[1]=node;
                    retrieval.open();
                    while (!retrieval.afterLast()) {
                        Object concept=retrieval.getTupleBuffer()[0];
                        if (concept instanceof AtomicConcept  && !Prefixes.isInternalIRI(((AtomicConcept)concept).getIRI()))
                            m_possibleSubsumers.add((AtomicConcept)concept);
                        retrieval.next();
                    }
                }
                else {
                    Iterator<AtomicConcept> iterator=m_possibleSubsumers.iterator();
                    while (iterator.hasNext()) {
                        AtomicConcept atomicConcept=iterator.next();
                        if (!tableau.getExtensionManager().containsConceptAssertion(atomicConcept,node))
                            iterator.remove();
                    }
                }
            }
        }
    }
}
