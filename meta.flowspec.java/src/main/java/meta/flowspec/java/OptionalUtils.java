package meta.flowspec.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

public class OptionalUtils {
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> orElse(Optional<? extends T> first, Optional<? extends T> second) {
        if (first.isPresent()) {
            return (Optional<T>) first;
        } else {
            return (Optional<T>) second;
        }
    }

    public static <T> Collector<Optional<T>, ?, Optional<List<T>>> toOptionalList() {
        //@formatter:off
        return Collector.of(() -> 
            Optional.of(new ArrayList<>()),
                (ol, ot) -> ol.ifPresent(l -> ot.ifPresent(t -> l.add(t))), 
                (left, right) -> left.map(l -> {right.ifPresent(r -> l.addAll(r)); return l;}));
        //@formatter:on
    }
}
