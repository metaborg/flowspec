package mb.flowspec.compiled;

import java.util.Map;
import java.util.function.BiFunction;

import org.metaborg.util.Ref;

import mb.flowspec.controlflow.ICFGNode;

/**
 * Instances of these can probably be methods from the instances of {@see ICompiledFlowSpecProperty}.
 */
public interface ITransferFunction<T> extends BiFunction<ICFGNode, Map<String, Map<ICFGNode, Ref<?>>>, T> {
}
