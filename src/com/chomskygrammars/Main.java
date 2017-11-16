package com.chomskygrammars;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    /*
    Features!
    -Character 'ε' supported (for example A->ε)
    -Prints full list of PDA transitions
    -Can handle thousands of complex rules and long strings without crashing
    -PDA always has initial state q1 and final state q2
     */

    public static void main(String[] args) {


        String[] nonTerminals=null;
        String[] terminals = null;
        String startingSymbol="";
        String inputString="";
        ArrayList<String[]> productions = new ArrayList<>();
        String productionsUnparsed="";

        String readLine = "";
        try {

            File f = new File("./input/input.txt");

            BufferedReader b = new BufferedReader(new FileReader(f));



            int i=0;

            while ((readLine = b.readLine()) != null) {
                i++;

                if (i==1) {
                    nonTerminals = readLine.split(",");
                } else if(i==2) {
                    terminals = readLine.split(",");
                } else if (i==3) {
                    startingSymbol = readLine;
                } else if (i==4) {
                    inputString = readLine;
                } else if (i==5) {
                    productionsUnparsed +=readLine;
                } else {
                    productionsUnparsed +="\n"+readLine;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(productionsUnparsed);
        while (scanner.hasNextLine()) {
            readLine = scanner.nextLine();
            productions.add(readLine.split("->"));
        }

        PDA pushDown = new PDA(nonTerminals, terminals, startingSymbol, productions);
      //
        System.out.println("\n\nSTRING COMPUTATION RESULT:\n"+ pushDown.validateString(inputString)+"\n");
        System.out.println(pushDown);

    }




}
