package meta.flowspec.lang.strategies;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class flowspec_solver_0_1 extends Strategy {
	public static flowspec_solver_0_1 INSTANCE = new flowspec_solver_0_1();

	@Override
	public IStrategoTerm invoke(final Context context, final IStrategoTerm current) {
		final ITermFactory factory = context.getFactory();
		final IOAgent ioAgent = context.getIOAgent();
		// TermIndex -> PropName -> ResultValue
		final Map<Integer, Map<String, Value>> results = new HashMap<>();

		switch (current.getTermType()) {
		case IStrategoTerm.LIST: {
			final IStrategoList list = (IStrategoList) current;

			for (IStrategoTerm term : list) {
				addPropConstraint(results, term, ioAgent);
			}

			return translateResults(results, factory);
		}
		default:
			// Invalid input, fall through to return null
		}

		return null;
	}

	public abstract class Value {
		public abstract IStrategoTerm toIStrategoTerm(ITermFactory factory);
	}

	public class TermIndex extends Value {
		public int index;

		public Map<Value, Value> childConditions;
		public Map<Value, Map<String, Value>> propConditions;

		/**
		 * @param index
		 * @param childConditions
		 * @param propConditions
		 */
		public TermIndex(int index, Map<Value, Value> childConditions, Map<Value, Map<String, Value>> propConditions) {
			this.index = index;
			this.childConditions = childConditions;
			this.propConditions = propConditions;
		}

		/**
		 * @param index
		 */
		public TermIndex(int index) {
			this.index = index;
		}

		@Override
		public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
			return factory.makeInt(index);
		}
	}

	public class Variable extends Value {
		public int index;
		public String varName;

		/**
		 * @param index
		 * @param varName
		 */
		public Variable(int index, String varName) {
			this.index = index;
			this.varName = varName;
		}

		@Override
		public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
			return factory.makeTuple(factory.makeInt(index), factory.makeString(varName));
		}
	}

	private void addPropConstraint(Map<Integer, Map<String, Value>> results, IStrategoTerm term, IOAgent ioAgent) {
		switch (term.getTermType()) {
		case IStrategoTerm.APPL:
			final IStrategoAppl appl = (IStrategoAppl) term;
			if (appl.getConstructor().getName() != "HasProp" || appl.getSubtermCount() != 4) {
				ioAgent.printError("[WARNING] Improper input to flowspec-solver inside list, ignoring. #a");
				return;
			}
			// TODO refactor the following methods to use Optional
			final Optional<Integer> subject = getTermIndexNumber(appl.getSubterm(0));
			if (!subject.isPresent()) {
				ioAgent.printError("[WARNING] Improper input to flowspec-solver inside list, ignoring. #b");
				return;
			}
			final Optional<String> propName = getString(appl.getSubterm(1));
			if (!propName.isPresent()) {
				ioAgent.printError("[WARNING] Improper input to flowspec-solver inside list, ignoring. #c");
				return;
			}
			final Optional<Value> propTarget = getValue(appl.getSubterm(2));
			if (!propTarget.isPresent()) {
				ioAgent.printError("[WARNING] Improper input to flowspec-solver inside list, ignoring. #d");
				return;
			}
			final IStrategoTerm conditions = appl.getSubterm(3);
			if (!results.containsKey(subject.get())) {
				results.put(subject.get(), new HashMap<>());
			}
			results.get(subject.get()).put(propName.get(), propTarget.get());
			return;
		}
		ioAgent.printError("[WARNING] Improper input to flowspec-solver inside list, ignoring. #e");
	}

	private Optional<Integer> getTermIndexNumber(IStrategoTerm term) {
		switch (term.getTermType()) {
		case IStrategoTerm.APPL:
			final IStrategoAppl appl = (IStrategoAppl) term;
			if (appl.getConstructor().getName() != "TermIndex" || appl.getSubtermCount() != 2) {
				return Optional.empty();
			}
			switch (appl.getSubterm(1).getTermType()) {
			case IStrategoTerm.INT:
				return Optional.of(((IStrategoInt) appl.getSubterm(1)).intValue());
			}
		}
		return Optional.empty();
	}

	private Optional<String> getString(IStrategoTerm term) {
		switch (term.getTermType()) {
		case IStrategoTerm.STRING:
			final IStrategoString string = (IStrategoString) term;
			return Optional.of(string.stringValue());
		}
		return Optional.empty();
	}

	private Optional<Value> getValue(IStrategoTerm term) {
		switch (term.getTermType()) {
		case IStrategoTerm.APPL:
			final IStrategoAppl appl = (IStrategoAppl) term;
			if (appl.getConstructor().getName() == "HasProp" && appl.getSubtermCount() == 3) {
				return getTermIndexNumber(appl).map(i -> new TermIndex(i));
			} else if (appl.getConstructor().getName() == "" && appl.getSubtermCount() == 3) {
				return getTermIndexNumber(appl.getSubterm(0))
						.flatMap(i -> getString(appl.getSubterm(1)).map(s -> new Variable(i, s)));
			}
		}
		return Optional.empty();
	}

	/**
	 * @param results
	 * @param factory
	 * @return The results as association lists in IStrategoTerm
	 */
	private IStrategoTerm translateResults(final Map<Integer, Map<String, Value>> results, final ITermFactory factory) {
		return makeList(
				results.entrySet()
						.stream().map(
								e1 -> factory
										.makeTuple(
												factory.makeInt(
														e1.getKey()),
												makeList(
														e1.getValue().entrySet().stream()
																.map(e2 -> factory.makeTuple(
																		factory.makeString(e2.getKey()),
																		e2.getValue().toIStrategoTerm(factory))),
														factory))),
				factory);
	}

	private IStrategoList makeList(final Stream<? extends IStrategoTerm> stream, final ITermFactory factory) {
		return factory.makeList(stream.collect(Collectors.toList()));
	}
}
