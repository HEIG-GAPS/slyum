package classDiagram.verifyName;

import classDiagram.components.Method;

public class MethodName implements IVerifyName {
    
    private static MethodName instance;
    public static MethodName getInstance() 
    {
        if (instance == null)
            instance = new MethodName();
        
        return instance;
    }
    
    private MethodName(){}

    @Override
    public boolean verifyName(String name)
    {
        return !name.isEmpty() && Method.checkSemantic(name);
    }

    public static String verifyAndAskNewName(String name) throws SyntaxeNameException
    {
        return ValidationName.checkAndAskName(name, MethodName.getInstance());
    }
}
