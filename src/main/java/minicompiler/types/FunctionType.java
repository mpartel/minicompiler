package minicompiler.types;

import java.util.List;
import minicompiler.misc.StringUtils;

public class FunctionType extends Type {
    public final List<Type> argTypes;
    public final Type returnType;

    public FunctionType(List<Type> argTypes, Type returnType) {
        this.argTypes = argTypes;
        this.returnType = returnType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionType) {
            FunctionType that = ((FunctionType)obj);
            return
                    this.argTypes.equals(that.argTypes) &&
                    this.returnType.equals(that.returnType);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return (argTypes.hashCode() << 16) | returnType.hashCode();
    }

    @Override
    public String toString() {
        return "((" + StringUtils.join(argTypes, ", ") + ") -> " + returnType + ")";
    }
}
