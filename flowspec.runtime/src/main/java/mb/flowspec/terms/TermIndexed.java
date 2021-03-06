package mb.flowspec.terms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.AbstractTermFactory;
import org.spoofax.terms.StrategoArrayList;
import org.spoofax.terms.StrategoTerm;

import static org.spoofax.terms.AbstractTermFactory.EMPTY_TERM_ARRAY;

public interface TermIndexed {

    @Nullable TermIndex termIndex();

    static @Nullable TermIndex filterAnnos(StrategoTerm term) {
        TermIndex termIndex = null;
        IStrategoList annotations = term.getAnnotations();
        if(!annotations.isEmpty()) {
            ArrayList<IStrategoTerm> annos = new ArrayList<>(annotations.size());
            for(IStrategoTerm anno : annotations) {
                Optional<TermIndex> index = TermIndex.matchTermIndex(anno);
                if(index.isPresent()) {
                    termIndex = index.get();
                } else {
                    annos.add(anno);
                }
            }
            annotations = new StrategoArrayList(annos.toArray(EMPTY_TERM_ARRAY));
            term.internalSetAnnotations(annotations);
        }
        return termIndex;
    }

    @SuppressWarnings("deprecation")
    static IStrategoTerm addTermIndexToAnnos(ITermFactory tf, IStrategoTerm term) {
        if(!(term instanceof TermIndexed)) {
            return term;
        }
        if(term instanceof TermIndex) {
            return term;
        }
        final TermIndex index = ((TermIndexed) term).termIndex();
        final IStrategoList annotations = term.getAnnotations();
        final List<IStrategoTerm> annos = new ArrayList<>(annotations.size() + 1);
        if(index != null) {
            annos.add(index);
        }
        annos.addAll(Arrays.asList(annotations.getAllSubterms()));
        final IStrategoTerm[] children = term.getAllSubterms();
        final IStrategoList annotations2;
        if(annos.isEmpty()) {
            annotations2 = AbstractTermFactory.EMPTY_LIST;
        } else {
            annotations2 = new StrategoArrayList(annos.toArray(EMPTY_TERM_ARRAY));
        }
        switch(term.getType()) {
            case APPL:
                return tf.makeAppl(((IStrategoAppl) term).getConstructor(), addTermIndexToAnnos(tf, children), annotations2);
            case LIST:
                return tf.makeList(addTermIndexToAnnos(tf, children), annotations2);
            case INT:
                return tf.annotateTerm(tf.makeInt(((IStrategoInt) term).intValue()), annotations2);
            case STRING:
                return tf.annotateTerm(tf.makeString(((IStrategoString) term).stringValue()), annotations2);
            case TUPLE:
                return tf.makeTuple(addTermIndexToAnnos(tf, children), annotations2);
            default:
                return term;
        }
    }

    static IStrategoTerm[] addTermIndexToAnnos(ITermFactory tf, final IStrategoTerm[] children) {
        final IStrategoTerm[] children2 = new IStrategoTerm[children.length];
        for(int i = 0; i < children.length; i++) {
            children2[i] = TermIndexed.addTermIndexToAnnos(tf, children[i]);
        }
        return children2;
    }

    static IStrategoTerm excludeTermIndexFromEqual(IStrategoTerm term) {
        if(term instanceof TermIndexed) {
            return term;
        }
        switch(term.getType()) {
            case APPL:
                return new FSAppl(((IStrategoAppl) term).getConstructor(),
                    excludeTermIndexFromEqual(term.getAllSubterms()), term.getAnnotations());
            case LIST:
                return new StrategoArrayList(excludeTermIndexFromEqual(term.getAllSubterms()), term.getAnnotations());
            case INT:
                return new FSInt(((IStrategoInt) term).intValue(), term.getAnnotations());
            case STRING:
                return new FSString(((IStrategoString) term).stringValue(), term.getAnnotations());
            case TUPLE:
                return new FSTuple(excludeTermIndexFromEqual(term.getAllSubterms()), term.getAnnotations());
            default:
                return term;
        }
    }

    static IStrategoTerm[] excludeTermIndexFromEqual(final IStrategoTerm[] children) {
        final IStrategoTerm[] children2 = new IStrategoTerm[children.length];
        for(int i = 0; i < children.length; i++) {
            children2[i] = excludeTermIndexFromEqual(children[i]);
        }
        return children2;
    }
}
