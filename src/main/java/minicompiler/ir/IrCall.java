package minicompiler.ir;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.List;

public class IrCall extends IrCommand {
    public final String assignedVar;
    public final String functionName;
    public final List<IrRValue> args;

    public IrCall(String returnVar, String functionName, List<IrRValue> args) {
        this.assignedVar = returnVar;
        this.functionName = functionName;
        this.args = args;
    }
    
    public IrCall(String returnVar, String functionName, IrRValue... args) {
        this(returnVar, functionName, Arrays.asList(args));
    }
    
    @Override
    public void accept(IrVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String getAssignedVar() {
        return assignedVar;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IrCall) {
            IrCall that = (IrCall)obj;
            return
                    this.assignedVar.equals(that.assignedVar) &&
                    this.functionName.equals(that.functionName) &&
                    this.args.equals(that.args);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ((this.assignedVar.hashCode() << 16) | functionName.hashCode()) + args.hashCode();
    }

    @Override
    public String toString() {
        return assignedVar + " := " + functionName + "(" + Joiner.on(", ").join(args) + ")";
    }
}
