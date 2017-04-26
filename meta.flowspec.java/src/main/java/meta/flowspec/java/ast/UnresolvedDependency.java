package meta.flowspec.java.ast;

public class UnresolvedDependency implements Dependency {
    public final String relation;
    public final Variable var;
    
    /**
     * @param relation
     * @param var
     */
    public UnresolvedDependency(String relation, Variable var) {
        this.relation = relation;
        this.var = var;
    }
}
