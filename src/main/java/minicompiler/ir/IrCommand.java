package minicompiler.ir;

public abstract class IrCommand {
    public abstract String getAssignedVar(); // Null if the command doesn't assign to anything.
}
