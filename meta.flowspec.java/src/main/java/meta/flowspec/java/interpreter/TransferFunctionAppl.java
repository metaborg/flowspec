package meta.flowspec.java.interpreter;

import java.util.Arrays;

import com.oracle.truffle.api.Truffle;


public class TransferFunctionAppl {
    private final int tfOffset;
    private final Object[] args;

    public TransferFunctionAppl(int tf, Object[] args) {
        this.tfOffset = tf;
        this.args = Arrays.copyOf(args, args.length+1);
    }

    public Object call(TransferFunction[] tfs, Object arg) {
        args[args.length-1] = arg;
        return Truffle.getRuntime().createCallTarget(tfs[tfOffset]).call(args);
    }
}
