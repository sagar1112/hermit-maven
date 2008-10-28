package org.semanticweb.HermiT.model.dataranges;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.semanticweb.HermiT.Namespaces;

public class DatatypeRestrictionOWLRealPlus 
        extends DatatypeRestriction 
        implements IntegerFacet, DoubleFacet {
    
    protected Set<DecimalInterval> intervals = new HashSet<DecimalInterval>();

    public DatatypeRestrictionOWLRealPlus(DT datatype) {
        this(datatype, true);
    }
    
    public DatatypeRestrictionOWLRealPlus(DT datatype, boolean allowSpecials) {
        this.datatype = datatype;
        this.supportedFacets = new HashSet<Facets>(
                Arrays.asList(new Facets[] {
                        Facets.MIN_INCLUSIVE, 
                        Facets.MIN_EXCLUSIVE, 
                        Facets.MAX_INCLUSIVE, 
                        Facets.MAX_EXCLUSIVE
                })
        );
        if (!allowSpecials) {
            notOneOf.addAll(DataConstant.numericSpecials);
        }
    }
    
    public CanonicalDataRange getNewInstance() {
        return new DatatypeRestrictionOWLRealPlus(this.datatype);
    }
    
    public boolean isFinite() {
        return isBottom() || !oneOf.isEmpty();
    }
    
    public void addFacet(Facets facet, String value) {
        if ("NaN".equalsIgnoreCase(value)) {
            isBottom = true;
        } else {
            BigDecimal valueDec = null;
            try {
                valueDec = new BigDecimal(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            switch (facet) {
            case MIN_INCLUSIVE: {
                // greater or equal X
                if (intervals.isEmpty()) {
                    intervals.add(new DecimalInterval(valueDec, null, false, true));
                } else {
                    for (DecimalInterval i : intervals) {
                        i.intersectWith(new DecimalInterval(valueDec, null, false, true));
                    }
                }
            } break;
            case MIN_EXCLUSIVE: {
                // greater than X
                if (intervals.isEmpty()) {
                    intervals.add(new DecimalInterval(valueDec, null, true, true));
                } else {
                    for (DecimalInterval i : intervals) {
                        i.intersectWith(new DecimalInterval(valueDec, null, true, true));
                    }
                }
            } break;
            case MAX_INCLUSIVE: {
                // smaller or equal X
                if (intervals.isEmpty()) {
                    intervals.add(new DecimalInterval(null, valueDec, true, false));
                } else {
                    for (DecimalInterval i : intervals) {
                        i.intersectWith(new DecimalInterval(null, valueDec, true, false));
                    }
                }
            } break;
            case MAX_EXCLUSIVE: {
                // smaller than X
                if (intervals.isEmpty()) {
                    intervals.add(new DecimalInterval(null, valueDec, true, true));
                } else {
                    for (DecimalInterval i : intervals) {
                        i.intersectWith(new DecimalInterval(null, valueDec, true, true));
                    }
                }
            } break;
            default:
                throw new IllegalArgumentException("Unsupported facet.");
            }
        }
    }
    
    public void conjoinFacetsFrom(DataRange range) {
        if (isNegated) {
            throw new RuntimeException("Cannot add facets to negated " +
                        "data ranges!");
        }
        // allow for integer and double data ranges to be handled
        if (!(range instanceof DatatypeRestrictionOWLRealPlus)) {
            throw new IllegalArgumentException("The given parameter is not " +
                    "an instance of DatatypeRestrictionOWLReal. It is only " +
                    "allowed to add facets from other owl real datatype " +
                    "restrictions.");
        }
        if (!isBottom()) {
            DatatypeRestrictionOWLRealPlus restr = (DatatypeRestrictionOWLRealPlus) range;
            if (restr.getIntervals().size() > 1) {
                throw new IllegalArgumentException("The given parameter " +
                        "contains more than one interval. ");
            }
            if (intervals.isEmpty()) {
                for (DecimalInterval i : restr.getIntervals()) {
                    if (restr.isNegated()) {
                        if (!i.isEmpty()) {
                            if (i.getMin() != null) {
                                intervals.add(new DecimalInterval(null, i.getMin(), true, !i.isOpenMin()));
                            }
                            if (i.getMax() != null) {
                                intervals.add(new DecimalInterval(i.getMax(), null, !i.isOpenMax(), true));
                            }
                        } // otherwise i is trivially satisfied 
                    } else {
                        intervals = restr.getIntervals();
                    }
                }
            } else {
                Set<DecimalInterval> newIntervals = new HashSet<DecimalInterval>();
                if (restr.isNegated()) {
                    for (DecimalInterval i : intervals) {
                        for (DecimalInterval iNew : restr.getIntervals()) {
                            if (!iNew.isEmpty()) {
                                if (iNew.getMin() != null) {
                                    DecimalInterval newInterval = i.getCopy();
                                    newInterval.intersectWith(new DecimalInterval(null, iNew.getMin(), true, !iNew.isOpenMin()));
                                    if (!newInterval.isEmpty()) {
                                        newIntervals.add(newInterval);
                                    }
                                } 
                                if (iNew.getMax() != null) {
                                    DecimalInterval newInterval = i.getCopy();
                                    newInterval.intersectWith(new DecimalInterval(iNew.getMax(), null, !iNew.isOpenMax(), true));
                                    if (!newInterval.isEmpty()) {
                                        newIntervals.add(newInterval);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (DecimalInterval i : intervals) {
                        for (DecimalInterval iNew : restr.getIntervals()) {
                            i.intersectWith(iNew);
                            if (!i.isEmpty()) newIntervals.add(i);
                        }
                    }
                }
                if (newIntervals.isEmpty()) {
                    isBottom = true;
                } else {
                    intervals = newIntervals;
                }
            }
        }
    }
    
    public boolean accepts(DataConstant constant) {
        if (!oneOf.isEmpty()) {
            return oneOf.contains(constant);
        }
        if (!notOneOf.isEmpty() && notOneOf.contains(constant)) {
            return false;
        } 
        if (intervals.isEmpty()) return true;
        BigDecimal decValue = new BigDecimal(constant.getValue());
        for (DecimalInterval i : intervals) {
            if (i.contains(decValue)) {
                return true;
            }
        }
        return false; 
    }
    
    public boolean hasMinCardinality(BigInteger n) {
        if (!oneOf.isEmpty()) {
            return (n.compareTo(new BigInteger("" + oneOf.size())) >= 0);
        }
        return true;
    }
    
    public BigInteger getEnumerationSize() {
        return new BigInteger("" + oneOf.size());
    }
    
    public DataConstant getSmallestAssignment() {
        if (!oneOf.isEmpty()) {
            SortedSet<DataConstant> sortedOneOfs = new TreeSet<DataConstant>(oneOf);
            return sortedOneOfs.first();
        }
        return null;
    }
    
    public Set<DecimalInterval> getIntervals() {
        return intervals;
    }
    
    public Set<IntegerInterval> getIntegerIntervals() {
        Set<IntegerInterval> integerIntervals = new HashSet<IntegerInterval>();
        if (!intervals.isEmpty()) {
            for (DecimalInterval i : intervals) {
                BigDecimal min = i.getMin();
                BigDecimal max = i.getMax();
                BigInteger minInt = null;
                BigInteger maxInt = null;
                Long minLong = null;
                Long maxLong = null;
                boolean hasBig = false;
                if (min != null) {
                    minInt = min.setScale(0, BigDecimal.ROUND_CEILING).toBigInteger();
                    if (i.isOpenMin() && min.equals(new BigDecimal(minInt))) {
                        minInt = minInt.add(BigInteger.ONE);
                    }
                    try {
                        minLong = new BigDecimal(minInt).longValueExact();
                    } catch (ArithmeticException e) {
                        hasBig = true;
                    }
                }
                if (max != null) {
                    maxInt = max.setScale(0, BigDecimal.ROUND_FLOOR).toBigInteger();
                    if (i.isOpenMax() && max.equals(new BigDecimal(maxInt))) {
                        maxInt = maxInt.subtract(BigInteger.ONE);
                    }
                    try {
                        maxLong = new BigDecimal(maxInt).longValueExact();
                    } catch (ArithmeticException e) {
                        hasBig = true;
                    }
                }
                IntegerInterval iInteger = hasBig ? 
                        new IntegerIntervalBig(minInt, maxInt) : 
                        new IntegerIntervalFin(minLong, maxLong);
                integerIntervals.add(iInteger);
            }
        }
        return integerIntervals;
    }

    public Set<DoubleInterval> getDoubleIntervals() {
        Set<DoubleInterval> doubleIntervals = new HashSet<DoubleInterval>();
        if (!intervals.isEmpty()) {
            for (DecimalInterval i : intervals) {
                BigDecimal min = i.getMin();
                BigDecimal max = i.getMax();
                double minD = -Double.MAX_VALUE;
                double maxD = Double.MAX_VALUE;
                boolean isEmpty = false;
                if (min.compareTo(new BigDecimal("" + Double.MIN_VALUE)) >= 0) {
                    if (min.compareTo(new BigDecimal("" + Double.MAX_VALUE)) <= 0) {
                        minD = min.doubleValue();
                        if (min.compareTo(new BigDecimal("" + minD)) > 0 
                                || (i.isOpenMin() && min.compareTo(new BigDecimal("" + minD)) == 0)) {
                            minD = DatatypeRestrictionDouble.nextDouble(minD);
                        }
                    } else {
                        isEmpty = true;
                    }
                } // minD = Double.Min_VALUE
                if (max.compareTo(new BigDecimal("" + Double.MAX_VALUE)) <= 0) {
                    if (max.compareTo(new BigDecimal("" + Double.MIN_VALUE)) >= 0) {
                        maxD = max.doubleValue();
                        if (max.compareTo(new BigDecimal("" + maxD)) < 0 
                                || (i.isOpenMax() && max.compareTo(new BigDecimal("" + maxD)) == 0)) {
                            maxD = DatatypeRestrictionDouble.previousDouble(maxD);
                        }
                    } else {
                        isEmpty = true;
                    }
                } // maxD = Double.MAX_VALUE
                if (isEmpty) {
                    minD = Double.MAX_VALUE;
                    maxD = Double.MIN_VALUE;
                }
                DoubleInterval iDouble = new DoubleInterval(minD, maxD);
                doubleIntervals.add(iDouble);
            }
        }
        return doubleIntervals;
    }
    
    protected String printExtraInfo(Namespaces namespaces) {
        boolean firstRun = true;
        StringBuffer buffer = new StringBuffer();
        for (DecimalInterval i : intervals) {
            if (!firstRun && !isNegated) {
                buffer.append(" or ");
            }
            if (i.getMin() != null) {
                buffer.append((i.isOpenMin() ? "> " : ">= ") + i.getMin());
            }
            if (i.getMax() != null) {
                if (i.getMin() != null) buffer.append(" ");
                buffer.append((i.isOpenMax() ? "< " : "<= ") + i.getMax());
            }
            firstRun = false;
        }
        return buffer.toString();
    }
    
    public boolean datatypeAccepts(DataConstant constant) {
        return DT.getSubTreeFor(DT.DECIMAL).contains(constant.getDatatype());
    }
    
    public boolean canHandleAll(Set<DT> datatypes) {
        Set<DT> dts = DT.getSubTreeFor(DT.OWLREALPLUS);
        dts.removeAll(DT.getSubTreeFor(DT.DECIMAL)); 
        dts.add(DT.DECIMAL);
        return dts.containsAll(datatypes);
    }
}
