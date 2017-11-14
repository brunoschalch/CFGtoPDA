package com.chomskygrammars;

import java.util.*;

public class PDA {

    //int[3] {q, a, A} --> int[2] {q, S}
    // state q, reading a on the input ("tape"), with A popped from the stack --> new state q and push A to the stack
    HashMap<ArrayList<String>, ArrayList<String[]>> transitionFunctions;

    public PDA (String[] nonTerminals, String[] terminals, String startingSymbol, ArrayList<String[]> productions) {

        transitionFunctions = new HashMap<>();
        buildPDA(nonTerminals, terminals, startingSymbol, productions);

    }


    private void buildPDA(String[] nonTerminals, String[] terminals, String startingSymbol, ArrayList<String[]> productions) {
        ArrayList<String[]> functionResult = new ArrayList<String[]>();

        //first add transition for first move: {q0, "", Ƶ} --> {q1, SƵ}
        functionResult = new ArrayList<String[]>();
        functionResult.add(new String[]{"q1", startingSymbol+"Ƶ"});
        transitionFunctions.put(new ArrayList<String>(Arrays.asList("q0", "", "Ƶ")), functionResult);

        //now for each terminal, {q1, a, a} --> {q1, ""}
        for (String terminal : terminals) {
            functionResult = new ArrayList<String[]>();  //object reuse here works because Java is pass by value
            functionResult.add(new String[]{"q1", ""});
            transitionFunctions.put(new ArrayList<String>(Arrays.asList("q1", terminal, terminal)), functionResult);
        }

        //now for each nonTerminal {q1, "", S} --> {{q1, a}, {q1, aSa}, {q1, b}, {q1, aBS}, ...} (we are pushing the right side of the production rule)
        for (String nonTerminal : nonTerminals) {
            //get all production results associated with this nonTerminal; for example A-->ASA, A-->b returns ArrayList<String> {ASA, b}
            ArrayList<String> results = productionResults(nonTerminal, productions);
            functionResult = new ArrayList<>();

            for (String result : results) {
                functionResult.add(new String[]{"q1", result});
            }

            transitionFunctions.put(new ArrayList<String>(Arrays.asList("q1", "", nonTerminal)), functionResult);
        }


        //finally ending transition {q1, "", Ƶ} --> (q2, Ƶ)
        functionResult = new ArrayList<String[]>();
        functionResult.add(new String[]{"q2", "Ƶ"});
        transitionFunctions.put(new ArrayList<String>(Arrays.asList("q1", "", "Ƶ")), functionResult);
    }

    private ArrayList<String> productionResults(String nonTerminal, ArrayList<String[]> productions) {
        ArrayList<String> output = new ArrayList<>();
        for (String[] production : productions) {
            if (production[0].equals(nonTerminal)) {
                output.add(production[1]);
            }
        }
        return output;
    }

    public String toString() {
        String result = "-----------------------Pushdown Automaton----------------------\n";

        Set< Map.Entry< ArrayList<String>, ArrayList<String[]> > > entrySet = transitionFunctions.entrySet();
        for (Map.Entry entry : entrySet) {
            result+="\n";
            ArrayList<String> function = (ArrayList<String>)entry.getKey();
            ArrayList<String[]> functionResults = ( ArrayList<String[]> ) entry.getValue();
            result+="δ("+function.get(0)+", "+ (function.get(1).equals("") ? "ε" : function.get(1)) +", "+ function.get(2)+") --> { ";
            for (String[] functionResult : functionResults) {
                result+= "("+functionResult[0]+", "+(functionResult[1].equals("") ? "ε" : functionResult[1])+"), ";
            }
            result = result.substring(0, result.length()-2);
            result+=" }";

        }

        return result;
    }

    public String validateString(String input) {
        LinkedList<String> stack = new LinkedList<>();
        stack.push("Ƶ");
        return processString("q0", input, stack, "(q0," + input+", Ƶ)");
    }

    private String processString(String currentState, String input, LinkedList<String> stack, String history) {

        //if number of nonterminals on stack is greater than input length, return null

        System.out.println("history right now:"+ history+"END");
        System.out.println("Trying: "+stack.peek());
        // try (state, nonTerminalFromTape, stack)
        ArrayList<String[]> transitionResult = transitionFunctions.get(new ArrayList<String>(Arrays.asList(currentState, String.valueOf(input.charAt(0)), stack.peek())));
        if (transitionResult!=null) System.out.println("Worked1: "+currentState+","+String.valueOf(input.charAt(0))+","+stack.peek());
        if (transitionResult!=null)   input = input.substring(1);

  //      if (transitionResult==null) System.out.println("TESTFAILED: "+currentState+","+String.valueOf(input.charAt(0))+","+stack.peek());

        //last one didn't work, try (state, ε, stack)
        if (transitionResult==null) {
           transitionResult =  transitionFunctions.get(new ArrayList<String>(Arrays.asList(currentState, "", stack.peek())));; //let's try with epsilon transition
            if (transitionResult!=null) System.out.println("Worked2: "+currentState+","+""+","+stack.peek());
        }

        if (transitionResult!=null) {
            stack.pop();
        } else {
            System.out.println("THIS IS FAILURE");
            return null; //no available PDA function for current setting
        }

        if (input.length()==0 && stack.size()==1) {
            return "Y"+ history;
        } else if(input.length()==0) {
            return null;
        }

        //iterate through each possible solution and call recursively
        for (String[] result : transitionResult) {
            String temp;
            LinkedList<String> anotherStack = new LinkedList<>(stack);
            for (int i = result[1].length()-1 ; i>=0; i--) {
                anotherStack.push(String.valueOf(result[1].charAt(i)));
            }
            System.out.println("PUSHED: "+ result[1]);
            String anotherHistory = history+ "-->" + "( "+result[0]+","+input+","+ stackToString(anotherStack)+" )";
            String anotherInput = new String(input);
            temp = processString(result[0], anotherInput, anotherStack, anotherHistory);
            if (temp!=null && temp.charAt(0)=='Y') return temp;

        }

        //if character in stack is nonTerminal, derive, if empty and in q1 go to final state and if terminal, match it with tape character

        return null; //recursive call but return history or null in the end . Here we can debug "false paths  by returning FAILURE followed by history instead of null or something like that
    }

    private String stackToString(LinkedList<String> stack) {
        String result="";
        for (String element : stack) {
            result+=element;
        }
        return result;
    }



}
