package mb.flowspec.runtime.interpreter.patterns;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.metaborg.util.functions.Function2;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.Types;
import mb.flowspec.terms.M;

public class TuplePatternNode extends PatternNode {
    @Children private final PatternNode[] children;

    public TuplePatternNode(PatternNode[] children) {
        super();
        this.children = children;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        IStrategoTerm term = Types.asIStrategoTerm(value);

        M.tuple(term);
        return zip(Arrays.stream(children), Arrays.stream(term.getAllSubterms()), (c, vc) -> c.matchGeneric(frame, vc))
            .allMatch(b -> b);
    }

    // from: https://gist.github.com/kjkrol/51a5a7612f0411849c62
    public static <A, B, C> Stream<C> zip(Stream<A> streamA, Stream<B> streamB, Function2<A, B, C> zipper) {
        final Iterator<A> iteratorA = streamA.iterator();
        final Iterator<B> iteratorB = streamB.iterator();
        final Iterator<C> iteratorC = new Iterator<C>() {
            @Override public boolean hasNext() {
                return iteratorA.hasNext() && iteratorB.hasNext();
            }

            @Override public C next() {
                return zipper.apply(iteratorA.next(), iteratorB.next());
            }
        };
        final boolean parallel = streamA.isParallel() || streamB.isParallel();
        return iteratorToFiniteStream(iteratorC, parallel);
    }

    public static <T> Stream<T> iteratorToFiniteStream(Iterator<T> iterator, boolean parallel) {
        final Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }
}
