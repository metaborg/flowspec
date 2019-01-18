package mb.flowspec.terms;

import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoTerm;

final class StrategoArrayListIterator implements Iterator<IStrategoTerm> {
    private final StrategoArrayList strategoArrayList;
    private int position;

    StrategoArrayListIterator(StrategoArrayList strategoArrayList) {
        this.strategoArrayList = strategoArrayList;
        this.position = 0;
    }


    @Override public IStrategoTerm next() {
        IStrategoTerm value = this.strategoArrayList.getSubterm(position);
        position += 1;
        return value;
    }

    @Override public boolean hasNext() {
        return position < this.strategoArrayList.getSubtermCount();
    }
}