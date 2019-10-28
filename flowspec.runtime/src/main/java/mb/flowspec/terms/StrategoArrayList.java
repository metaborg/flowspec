package mb.flowspec.terms;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.RandomAccess;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.StrategoList;
import org.spoofax.terms.StrategoTerm;

public class StrategoArrayList extends StrategoTerm implements IStrategoList, RandomAccess, TermIndexed {
    private final IStrategoTerm[] terms;
    final int offset;
    private final int subtermCount;
    private final TermIndex termIndex;

    public StrategoArrayList(IStrategoTerm... terms) {
        this(terms, null, 0);
    }

    public StrategoArrayList(IStrategoTerm[] terms, IStrategoList annotations) {
        this(terms, annotations, 0);
    }

    protected StrategoArrayList(IStrategoTerm[] terms, IStrategoList annotations, int offset) {
        super(annotations, IStrategoTerm.SHARABLE);
        this.termIndex = TermIndexed.filterAnnos(this);
        this.terms = terms;
        this.offset = offset;
        this.subtermCount = terms.length - offset;
    }

    public static StrategoArrayList fromList(Collection<? extends IStrategoTerm> terms) {
        return new StrategoArrayList(terms.toArray(new IStrategoTerm[terms.size()]));
    }

    @Override public int getSubtermCount() {
        return subtermCount;
    }

    @Override public IStrategoTerm getSubterm(int index) {
        return terms[offset + index];
    }

    @Override public IStrategoTerm[] getAllSubterms() {
        return Arrays.copyOfRange(terms, offset, terms.length);
    }

    @Override public int getTermType() {
        return IStrategoTerm.LIST;
    }

    @Deprecated @Override public void prettyPrint(ITermPrinter pp) {
        if(!isEmpty()) {
            pp.println("[");
            pp.indent(2);
            Iterator<IStrategoTerm> iter = iterator();
            iter.next().prettyPrint(pp);
            while(iter.hasNext()) {
                IStrategoTerm element = iter.next();
                pp.print(",");
                pp.nextIndentOff();
                element.prettyPrint(pp);
                pp.println("");
            }
            pp.println("");
            pp.print("]");
            pp.outdent(2);

        } else {
            pp.print("[]");
        }
        printAnnotations(pp);
    }

    @Override public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append('[');
        if(!isEmpty()) {
            if(maxDepth == 0) {
                output.append("...");
            } else {
                Iterator<IStrategoTerm> iter = iterator();
                iter.next().writeAsString(output, maxDepth - 1);
                while(iter.hasNext()) {
                    IStrategoTerm element = iter.next();
                    output.append(',');
                    element.writeAsString(output, maxDepth - 1);
                }
            }
        }
        output.append(']');
        appendAnnotations(output, maxDepth);
    }

    @Override public Iterator<IStrategoTerm> iterator() {
        return new StrategoArrayListIterator(this);
    }

    @Deprecated @Override public IStrategoTerm get(int index) {
        return getSubterm(index);
    }

    @Deprecated @Override public int size() {
        return getSubtermCount();
    }

    @Deprecated @Override public IStrategoList prepend(IStrategoTerm prefix) {
        return new StrategoList(prefix, this, null, Math.min(prefix.getStorageType(), this.getStorageType()));
    }

    @Override public IStrategoTerm head() {
        return getSubterm(0);
    }

    @Override public IStrategoList tail() {
        return new StrategoArrayList(terms, null, offset + 1);
    }

    @Override public boolean isEmpty() {
        return getSubtermCount() == 0;
    }

    @Override protected boolean doSlowMatch(IStrategoTerm second, int commonStorageType) {
        if(this == second) {
            return true;
        }
        if(second.getTermType() != IStrategoTerm.LIST) {
            return false;
        }
        if(this.getSubtermCount() != second.getSubtermCount()) {
            return false;
        }

        if(second instanceof StrategoArrayList) {
            StrategoArrayList other = (StrategoArrayList) second;

            if(!this.getAnnotations().match(other.getAnnotations())) {
                return false;
            }

            if(this.terms == other.terms) {
                return this.offset == other.offset;
            }

            Iterator<IStrategoTerm> termsThis = this.iterator();
            Iterator<IStrategoTerm> termsOther = other.iterator();

            if(!this.isEmpty()) {
                for(IStrategoTerm thisNext = termsThis.next(), otherNext = termsOther.next()
                   ; termsThis.hasNext()
                   ; thisNext = termsThis.next(), otherNext = termsOther.next()) {
                    if(thisNext != otherNext && !thisNext.match(otherNext)) {
                        return false;
                    }
                }
            }

            return true;
        }

        final IStrategoList snd = (IStrategoList) second;

        if(!isEmpty()) {
            IStrategoTerm head = head();
            IStrategoTerm head2 = snd.head();
            if(head != head2 && !head.match(head2))
                return false;

            IStrategoList tail = tail();
            IStrategoList tail2 = snd.tail();

            for(IStrategoList cons = tail, cons2 = tail2; !cons.isEmpty(); cons = cons.tail(), cons2 = cons2.tail()) {
                IStrategoTerm consHead = cons.head();
                IStrategoTerm cons2Head = cons2.head();
                if(!cons.getAnnotations().match(cons2.getAnnotations())) {
                    return false;
                }
                if(consHead != cons2Head && !consHead.match(cons2Head))
                    return false;
            }
        }

        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if(annotations == secondAnnotations) {
            return true;
        } else if(annotations.match(secondAnnotations)) {
            if(commonStorageType == SHARABLE) {
                internalSetAnnotations(secondAnnotations);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override protected int hashFunction() {
        if(terms == null)
            return 0;

        int result = 1;
        for(int i = offset; i < terms.length; i++) {
            IStrategoTerm element = terms[i];
            result = 31 * result + (element == null ? 0 : element.hashCode());
        }

        return result;
    }

    @Override public TermIndex termIndex() {
        return termIndex;
    }
}
