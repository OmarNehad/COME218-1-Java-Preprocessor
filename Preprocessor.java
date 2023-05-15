
import java.io.*;
import java.util.*;

class Preprocessor {
    public static void main (String[] args)  {

        try
        {
            File input = new File("Input.txt");
            
            Scanner codeWithComments = new Scanner(input);
            String cleanCode = removeComments(codeWithComments);

            FileWriter Output = new FileWriter("Output.txt");
            Output.write(cleanCode);
            System.out.println(input.getName() + " file comments have been removed and saved to: Output.txt");
            Output.close();

            List<String> constructs = new ArrayList<String>();
            Scanner consScanner = new Scanner(new File("Constructs.txt"));
            while(consScanner.hasNextLine())
            {
                constructs.add(consScanner.nextLine().trim());
            }
            System.out.println("");
            identifyLoopsAndFuncs( new Scanner(cleanCode));
            System.out.println("");
            identifyKeyWords(new Scanner(cleanCode),constructs);

            
            codeWithComments.close();

        }
        catch (Exception err)
        {
            System.out.println(err.getMessage());
        }
    }


    public enum CodeState
    {
        CODE,
        INLINE_COMMENT,
        BLOCK_COMMENT
    }

    public static String removeComments(Scanner code)
    {   
        String FinalCode = "";
        CodeState currentState = CodeState.CODE;
        code.useDelimiter("");
        while(code.hasNext())
        {
            String c1 = code.next();
            switch (currentState)
            {
                case CODE:
  

                    if(c1.equals("/"))
                    {
                        String c2 = code.next();
                        if(c2.equals("/")) currentState = CodeState.INLINE_COMMENT;
                        else if(c2.equals("*")) currentState = CodeState.BLOCK_COMMENT;
                        else FinalCode += c1 + c2; 
                    }
                    else FinalCode += c1;
                    break;
                case INLINE_COMMENT:
                    if(c1.equals("\n"))
                    {
                        currentState = CodeState.CODE;
                        FinalCode+="\n";

                    }
                    break;
                case BLOCK_COMMENT:
                    if(c1.equals("*") && code.hasNext())
                    {
                        if(code.next().equals("/"))
                        {
                            currentState = CodeState.CODE;
                        }
                    }
                    break;
            }

        }

        return FinalCode;   
    }
        
    public static void identifyKeyWords(Scanner code,List<String> constructs){
        List<String> foundKeyWords = new ArrayList<String>();

        code.useDelimiter("[(){}; ]");
        System.out.println("KeyWords Found:");

        while(code.hasNext())
        {
            String nextCode = code.next();
            if(constructs.contains(nextCode) && !foundKeyWords.contains(nextCode))
            {
                foundKeyWords.add(nextCode);
                System.out.println(nextCode);
            }
        }
    }
    public static void identifyLoopsAndFuncs(Scanner code)
    {

        int currentLine = 0;

        

        Map<Integer,String> Trackerloops = new HashMap<Integer,String>();
        List<String> loops = new ArrayList<String>();
        int loopBrackets = 0;

        boolean insideMethod = false;
        int methodBrackets = 0;
        boolean lookingForOpenBrackets = false;
        

        List<String> methods= new ArrayList<String>();

        while(code.hasNextLine())
        {
            currentLine ++;
            String lineString = code.nextLine().strip();
        
            // Method identification
            if(lineString.contains("(") && !insideMethod && !lineString.contains(" new "))
            {
                methods.add(lineString.replaceAll("\\(.*","") + "()");
                insideMethod = true;
                lookingForOpenBrackets = true;
            }
            if(lineString.contains("{") && insideMethod)
            {
                methodBrackets++;
                if(lookingForOpenBrackets)
                {
                    methods.set(methods.size()-1, methods.get(methods.size()-1) + " " + currentLine + " - ");
                    lookingForOpenBrackets = false;
                }
            }
            if(lineString.contains("}"))
            {
                methodBrackets--;
                if(methodBrackets == 0)
                {
                    methods.set(methods.size()-1, methods.get(methods.size()-1) + currentLine);
                    insideMethod = false;
                }

            } 
            Scanner nextLine = new Scanner(lineString);
            
            nextLine.useDelimiter("[( ]"); 
            

            //Loops Identification
            while(nextLine.hasNext())
            {
                String nextCode = nextLine.next();

                if(Arrays.asList("while","for","do").contains(nextCode.replace("{", "")))
                {
                    Trackerloops.put(loopBrackets+1,nextCode.replace("{", "")+ " loop from line: ");
                }

                if(nextCode.contains("{"))
                {
                    
                    loopBrackets++;
                    if(Trackerloops.containsKey(loopBrackets))
                    {
                        Trackerloops.put(loopBrackets, Trackerloops.get(loopBrackets) + currentLine + " - ");
                    }
                }
                if(nextCode.contains("}"))
                { 

                    if(Trackerloops.containsKey(loopBrackets))
                    {
                        loops.add(Trackerloops.get(loopBrackets) + currentLine);
                        Trackerloops.remove(loopBrackets);
                    }
                    loopBrackets--;

                }
            }
           nextLine.close();
        }
        System.out.println("Loops: ");
        for (String loop : loops)
        {
            System.out.println(loop);
        }
        System.out.println("Methods: ");
        
        for (String method : methods)
        {
            System.out.println(method);
        }
        
        
    }
}
