package mb.flowspec.controlflow;

public interface IBasicBlock extends Iterable<ICFGNode> {
    ICFGNode first();

    ICFGNode last();

    IBasicBlock inverse();
}
