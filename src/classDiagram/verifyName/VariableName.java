package classDiagram.verifyName;

import classDiagram.components.Variable;

public class VariableName implements IVerifyName {
    
    private static VariableName instance;
    public static VariableName getInstance() 
    {
        if (instance == null)
            instance = new VariableName();
        
        return instance;
    }

    @Override
    public boolean verifyName(String name)
    {
        return !name.isEmpty() && Variable.checkSemantic(name);
    }
    
    public static String verifyAndAskNewName(String name) throws SyntaxeNameException
    {
        return ValidationName.checkAndAskName(name, VariableName.getInstance());
    }

}
