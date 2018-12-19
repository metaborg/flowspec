package mb.flowspec.runtime.interpreter;

import com.oracle.truffle.api.TruffleLanguage;

@TruffleLanguage.Registration(name = "FlowSpec", version = FlowSpec.VERSION, mimeType = FlowSpec.MIME_TYPE)
public final class FlowSpec extends TruffleLanguage<Context> {
    protected static final String VERSION = "0.1.0";
    public static final String MIME_TYPE = "application/x-flowspec";

    public FlowSpec() {
    }

    @Override protected Context createContext(com.oracle.truffle.api.TruffleLanguage.Env env) {
        return new Context();
    }

    @Override protected Object findExportedSymbol(Context context, String globalName, boolean onlyExplicit) {
        return null;
    }

    @Override protected Object getLanguageGlobal(Context context) {
        return context;
    }

    @Override protected boolean isObjectOfLanguage(Object object) {
        return false;
    }
}
