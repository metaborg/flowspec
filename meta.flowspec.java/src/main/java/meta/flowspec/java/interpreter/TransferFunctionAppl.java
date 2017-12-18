package meta.flowspec.java.interpreter;

import java.util.Arrays;


public class TransferFunctionAppl {
    public final int tfOffset;
    public final Object[] args;

    public TransferFunctionAppl(int tf, Object[] args) {
        this.tfOffset = tf;
        this.args = Arrays.copyOf(args, args.length+1);
    }
}
