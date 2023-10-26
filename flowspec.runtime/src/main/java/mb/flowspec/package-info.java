@Value.Style(
    // @formatter:off
    typeAbstract = { "*" },
    get = { "is*", "get*" },
    with = "with*",
    defaults = @Value.Immutable(builder = false, prehash = true),
    // prevent generation of jakarta.annotation.*; bogus entry, because empty list = allow all
    allowedClasspathAnnotations = {Override.class},
    jdkOnly = true
    // @formatter:on
)
package mb.flowspec;

import org.immutables.value.Value;