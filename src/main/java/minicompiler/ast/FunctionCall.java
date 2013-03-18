package minicompiler.ast;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.List;

public class FunctionCall implements Expr {
    public final String functionName;
    public final List<Expr> arguments;

    public FunctionCall(String functionName, List<Expr> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }
    
    public FunctionCall(String functionName, Expr... arguments) {
        this(functionName, Arrays.asList(arguments));
    }
    
    public void accept(AstVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionCall) {
            FunctionCall that = (FunctionCall)obj;
            return
                    this.functionName.equals(that.functionName) &&
                    this.arguments.equals(that.arguments);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (this.functionName.hashCode() << 16) | arguments.hashCode();
    }

    @Override
    public String toString() {
        return functionName + "(" + Joiner.on(", ").join(arguments) + ")";
    }
}
