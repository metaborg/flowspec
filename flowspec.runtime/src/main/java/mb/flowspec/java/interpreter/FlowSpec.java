package mb.flowspec.java.interpreter;

import org.metaborg.meta.nabl2.stratego.StrategoTerms;
import org.metaborg.meta.nabl2.terms.unification.PersistentUnifier;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.Source;

@TruffleLanguage.Registration(name = "FlowSpec", version = FlowSpec.VERSION, mimeType = FlowSpec.MIME_TYPE)
public final class FlowSpec extends TruffleLanguage<Context> {
    protected static final String VERSION = "0.1.0";
    public static final String MIME_TYPE = "application/x-flowspec";

    public FlowSpec() {
    }

    @Override
    protected Context createContext(com.oracle.truffle.api.TruffleLanguage.Env env) {
        return new Context();
    }
    
    @Override
    protected Object findExportedSymbol(Context context, String globalName, boolean onlyExplicit) {
        return null;
    }
    
    @Override
    protected Object getLanguageGlobal(Context context) {
        return context;
    }
    
    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }
    
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();
        
        ITermFactory f = new TermFactory();
        StrategoTerms strategoTerms = new StrategoTerms(f);
        
        IStrategoTerm term = f.parseFromString(source.getCode());
        
        TransferFunction rootNode = TransferFunction.match(this, new FrameDescriptor()).match(strategoTerms.fromStratego(term), PersistentUnifier.Immutable.of()).get();
        
        return Truffle.getRuntime().createCallTarget(rootNode);
    }
}
