package classDiagram.verifyName;

import utility.Utility;

public class ValidationName {

    public static String checkAndAskName(String name, IVerifyName verification) throws SyntaxeNameException
    {
        String bufferName = name;
    
        while (true) {
            if (verification.verifyName(name))
                return name;
    
            if (name.isEmpty())
                name = Utility.proposeNewName("Please enter a name for rename \"" + bufferName + "\": ");
            else
            {
                bufferName = name;
                name = Utility.proposeNewName("The syntaxe for \"" + name + "\" is incorrect. Please enter a new name: ");
            }
                
            if (name.equals("-1"))
                throw new SyntaxeNameException();
        }
    }
}
