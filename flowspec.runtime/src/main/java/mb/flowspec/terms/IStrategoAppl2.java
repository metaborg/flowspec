package mb.flowspec.terms;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.TermList;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;

public interface IStrategoAppl2 extends IStrategoAppl, TermIndexed {

    // TermIndexed

    @Override default @Nullable TermIndex termIndex() {
        return null;
    }

    // IStrategoNamed

    String getName();

    // ISimpleTerm

    int getSubtermCount();

    // IStrategoTerm

    IStrategoTerm[] getAllSubterms();

    @Override default List<IStrategoTerm> getSubterms() {
        return TermList.of(getAllSubterms());
    }

    // IStrategoAppl

    // Not this, it will interfere with constructor sharing assumed in Stratego code. 
//    @Override default IStrategoConstructor getConstructor() {
//        return new StrategoConstructor(getName(), getSubtermCount());
//    }

    // ISimpleTerm

    @Override default IStrategoTerm getSubterm(int i) {
        return getAllSubterms()[i];
    }

    @Override default <T extends ITermAttachment> T getAttachment(TermAttachmentType<T> type) {
        return null;
    }

    @Override default void putAttachment(ITermAttachment resourceAttachment) {
    }

    @Override default ITermAttachment removeAttachment(TermAttachmentType<?> attachmentType) {
        return null;
    }

    @Override default boolean isList() {
        return false;
    }

    // IStrategoTerm

    @Override default int getTermType() {
        return IStrategoTerm.APPL;
    }

    @SuppressWarnings("deprecation") @Override default IStrategoList getAnnotations() {
        return TermFactory.EMPTY_LIST;
    }

    @Override default boolean match(IStrategoTerm second) {
        if(this == second) {
            return true;
        }
        if(null == second) {
            return false;
        }
        if(this.getTermType() == second.getTermType()) {
            IStrategoAppl appl = (IStrategoAppl) second;
            return this.getName().equals(appl.getName())
                && Arrays.equals(this.getAllSubterms(), second.getAllSubterms());
        }
        return false;
    }

    @Deprecated @Override default void prettyPrint(ITermPrinter pp) {
        try {
            writeAsString(pp, IStrategoTerm.INFINITE);
        } catch(IOException e) {
        }
    }

    @Override default String toString(int maxDepth) {
        StringBuilder result = new StringBuilder();
        try {
            writeAsString(result, maxDepth);
        } catch(IOException e) {
        }
        return result.toString();
    }

    @Override default void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append(this.getName());
        IStrategoTerm[] kids = getAllSubterms();
        if(kids.length > 0) {
            output.append('(');
            if(maxDepth == 0) {
                output.append("...");
            } else {
                kids[0].writeAsString(output, maxDepth - 1);
                for(int i = 1; i < kids.length; i++) {
                    output.append(',');
                    kids[i].writeAsString(output, maxDepth - 1);
                }
            }
            output.append(')');
        }
        IStrategoList annos = getAnnotations();
        if(annos.size() == 0)
            return;

        output.append('{');
        annos.getSubterm(0).writeAsString(output, maxDepth);
        for(annos = annos.tail(); !annos.isEmpty(); annos = annos.tail()) {
            output.append(',');
            annos.head().writeAsString(output, maxDepth);
        }
        output.append('}');
    }

    // Iterable<IStrategoTerm>

    @Override default Iterator<IStrategoTerm> iterator() {
        return Arrays.asList(getAllSubterms()).iterator();
    }
}
