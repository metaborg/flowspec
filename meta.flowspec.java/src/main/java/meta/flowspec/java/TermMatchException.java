package meta.flowspec.java;

public class TermMatchException extends Exception {
    private static final long serialVersionUID = -5302097636293457608L;
    
    public TermMatchException() {
        super();
    }
    
    public TermMatchException(String expected, String got) {
        super("expected: " + expected + ", but got: " + got);
    }
}
