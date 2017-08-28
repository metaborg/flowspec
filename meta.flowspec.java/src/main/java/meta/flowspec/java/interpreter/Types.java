package meta.flowspec.java.interpreter;

import org.pcollections.PSet;

import com.oracle.truffle.api.dsl.TypeSystem;

import meta.flowspec.java.interpreter.values.Function;
import meta.flowspec.java.interpreter.values.Tuple;

@TypeSystem({int.class, boolean.class, String.class, Tuple.class, Function.class, PSet.class})
public abstract class Types {

}
