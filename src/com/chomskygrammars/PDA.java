package com.chomskygrammars;

import java.util.*;

public class PDA {

    //int[3] {q, a, A} --> int[2] {q, S}
    // state q, reading a on the input ("tape"), with A popped from the stack --> new state q and push A to the stack
    HashMap<String[], ArrayList<String[]>> transitionFunctions;

    public PDA (String[] nonTerminals, String[] terminals, String startingSymbol, ArrayList<String[]> productions) {

        transitionFunctions = new HashMap<>();
        buildPDA(nonTerminals, terminals, startingSymbol, productions);

    }


    private void buildPDA(String[] nonTerminals, String[] terminals, String startingSymbol, ArrayList<String[]> productions) {
        ArrayList<String[]> functionResult = new ArrayList<String[]>();

        //first add transition for first move: {q0, "", Ƶ} --> {q1, SƵ}
        functionResult = new ArrayList<String[]>();
        functionResult.add(new String[]{"q1", startingSymbol+"Ƶ"});
        transitionFunctions.put(new String[]{"q0", "", "Ƶ"}, functionResult);

        //now for each terminal, {q1, a, a} --> {q1, ""}
        for (String terminal : terminals) {
            functionResult = new ArrayList<String[]>();  //object reuse here works because Java is pass by value
            functionResult.add(new String[]{"q1", ""});
            transitionFunctions.put(new String[]{"q1", terminal, terminal}, functionResult);
        }

        //now for each nonTerminal {q1, "", S} --> {{q1, a}, {q1, aSa}, {q1, b}, {q1, aBS}, ...} (we are pushing the right side of the production rule)
        for (String nonTerminal : nonTerminals) {
            //get all production results associated with this nonTerminal; for example A-->ASA, A-->b returns ArrayList<String> {ASA, b}
            ArrayList<String> results = productionResults(nonTerminal, productions);
            functionResult = new ArrayList<>();

            for (String result : results) {
                functionResult.add(new String[]{"q1", result});
            }

            transitionFunctions.put(new String[]{"q1", "", nonTerminal}, functionResult);
        }


        //finally ending transition {q1, "", Ƶ} --> (q2, Ƶ)
        functionResult = new ArrayList<String[]>();
        functionResult.add(new String[]{"q2", "Ƶ"});
        transitionFunctions.put(new String[]{"q1", "", "Ƶ"}, functionResult);
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

        Set<Map.Entry<String[], ArrayList<String[]> > > entrySet = transitionFunctions.entrySet();
        for (Map.Entry entry : entrySet) {
            result+="\n";
            String[] function = (String[])entry.getKey();
            ArrayList<String[]> functionResults = ( ArrayList<String[]> ) entry.getValue();
            result+="δ("+function[0]+", "+ (function[1].equals("") ? "ε" : function[1]) +", "+ function[2]+") --> { ";
            for (String[] functionResult : functionResults) {
                result+= "("+functionResult[0]+", "+(functionResult[1].equals("") ? "ε" : functionResult[1])+"), ";
            }
            result = result.substring(0, result.length()-2);
            result+=" }";

        }

        return result;
    }

    private String processString(String currentState, String input, LinkedList<String> stack, String history) {
        String steps="";

        //if character in stack is nonTerminal, derive, if empty and in q1 go to final state and if terminal, match it with tape character


        return steps; //recursive call but return history or null in the end . Here we can debug "false paths  by returning FAILURE followed by history instead of null or something like that
    }



}
