package meta.flowspec.java;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Pair<L,R> {
    @Value.Parameter public abstract L left();
    @Value.Parameter public abstract R right();
}
