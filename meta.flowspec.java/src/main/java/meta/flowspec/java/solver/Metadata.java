package meta.flowspec.java.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import meta.flowspec.java.lattice.CompleteLattice;

@Immutable
public abstract class Metadata {
    public enum Direction {
        Forward,
        Backward,
        FlowInsensitive;
        
        public static Direction fromIStrategoTerm(IStrategoTerm term) {
            assert term instanceof IStrategoAppl;
            IStrategoAppl appl = (IStrategoAppl) term;
            assert appl.getSubtermCount() == 0;
            switch (appl.getConstructor().getName()) {
                case "Bw": {
                    return Backward;
                }
                case "Fw": {
                    return Forward;
                }
                case "NA": {
                    return FlowInsensitive;
                }
                default: {
                    throw new RuntimeException("Parse error while reading in direction of property");
                }
            }
        }
    }

    @Parameter public abstract Direction dir();
    @Parameter public abstract CompleteLattice<Object> lattice();
    @Parameter public abstract Type type();
}
