package classDiagram.verifyName;

import classDiagram.components.Type;

public class TypeName implements IVerifyName {
    
    private static TypeName instance;
    public static TypeName getInstance() 
    {
        if (instance == null)
            instance = new TypeName();
        
        return instance;
    }

    @Override
    public boolean verifyName(String name)
    {
        return !name.isEmpty() && Type.checkSemantic(name);
    }

    public static String verifyAndAskNewName(String name) throws SyntaxeNameException
    {
        return ValidationName.checkAndAskName(name, TypeName.getInstance());
    }
}
