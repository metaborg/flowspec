package meta.flowspec.java.interpreter.patterns;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;
import org.metaborg.util.functions.Function2;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.Types;

public class TuplePatternNode extends PatternNode {
    @Children
    private final PatternNode[] children;

    public TuplePatternNode(PatternNode[] children) {
        super();
        this.children = children;
    }

    @Override
    public boolean matchGeneric(VirtualFrame frame, Object value) {
        ITerm term = Types.asITerm(value);
        
        return M.tuple(appl -> {
            Object[] valueChildren = appl.getArgs().toArray();
            return zip(Arrays.stream(children), Arrays.stream(valueChildren), (c, vc) -> c.matchGeneric(frame, vc)).allMatch(b -> b);
        }).match(term).orElse(false);
    }

    public static IMatcher<TuplePatternNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2(
                "Tuple", 
                PatternNode.matchPattern(frameDescriptor), 
                M.listElems(PatternNode.matchPattern(frameDescriptor)),
                (appl, first, others) -> {
                    PatternNode[] exprs = new PatternNode[others.size() + 1];
                    int i = 0;
                    exprs[i] = first;
                    for(PatternNode expr : others) {
                        i++;
                        exprs[i] = expr;
                    }
                    return new TuplePatternNode(exprs);
                });
    }
    
    public void init(ISolution solution) {
        for (PatternNode child : children) {
            child.init(solution);
        }
    }

    // from: https://gist.github.com/kjkrol/51a5a7612f0411849c62
    public static <A, B, C> Stream<C> zip(Stream<A> streamA, Stream<B> streamB, Function2<A, B, C> zipper) {
        final Iterator<A> iteratorA = streamA.iterator();
        final Iterator<B> iteratorB = streamB.iterator();
        final Iterator<C> iteratorC = new Iterator<C>() {
            @Override
            public boolean hasNext() {
                return iteratorA.hasNext() && iteratorB.hasNext();
            }

            @Override
            public C next() {
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
