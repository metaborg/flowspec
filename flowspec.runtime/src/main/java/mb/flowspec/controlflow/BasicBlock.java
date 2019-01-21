package mb.flowspec.controlflow;

import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BasicBlock implements IBasicBlock {
    private final Deque<ICFGNode> nodes;
    private final boolean inversed;
    private BasicBlock inverse = null;
    public final Set<ICFGNode> ignored;

    public BasicBlock(Deque<ICFGNode> nodes) {
        this.nodes = nodes;
        this.inversed = false;
        this.ignored = new HashSet<>(nodes.size() * 2);
    }

    protected BasicBlock(Deque<ICFGNode> nodes, BasicBlock inverse) {
        this.nodes = nodes;
        this.inversed = true;
        this.inverse = inverse;
        this.ignored = inverse.ignored;
    }

    @Override public ICFGNode first() {
        if(inversed) {
            return nodes.peekLast();
        }
        return nodes.peekFirst();
    }

    @Override public ICFGNode last() {
        if(inversed) {
            return nodes.peekFirst();
        }
        return nodes.peekLast();
    }

    @Override public Iterator<ICFGNode> iterator() {
        if(inversed) {
            return nodes.descendingIterator();
        }
        return nodes.iterator();
    }

    @Override public IBasicBlock inverse() {
        if(inverse == null) {
            inverse = new BasicBlock(nodes, this);
        }
        return inverse;
    }

    @Override public boolean ignored(ICFGNode node) {
        return ignored.contains(node);
    }

    @Override public void ignore(ICFGNode node) {
        this.ignored.add(node);
    }

    @Override public void clearIgnored() {
        this.ignored.clear();
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (inversed ? 1231 : 1237);
        result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        BasicBlock other = (BasicBlock) obj;
        if(inversed != other.inversed)
            return false;
        if(nodes == null) {
            if(other.nodes != null)
                return false;
        } else if(!nodes.equals(other.nodes))
            return false;
        return true;
    }
}
