package projects.java.boxes;

// Note to future self -- fix this shit.

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;

public class Box {
    Scanner file = null;

    Stack<Integer> stack = new Stack<Integer>();
    // make this an integer? v
    Map<String, String> dictionary = new HashMap<String, String>();
    Map<String, ArrayList<String>> phonebook = new HashMap<String, ArrayList<String>>();

    public static void main (String... args) {
        Box box = new Box(args[0]);
    }

    // path -- Path to the file to be interpreted
    public Box (String path) {
        try {
            file = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println("Wheres-The-Fucking-File?-Exception");
        } 

        // the entire file inside a string for easy lookup
        ArrayList<String> fileString = new ArrayList<String>();

        while (file.hasNext()) 
            fileString.add(file.next());

        execute(fileString);

        file.close();
    }

    public void execute (ArrayList<String> file) {
        for (int i = 0; i < file.size(); i++) {
            if (isVariableCall(file.get(i))) {
                stack.add(Integer.parseInt(dictionary.get(file.get(i))));
            } else if (isWordCall(file.get(i))) {
                execute(phonebook.get(file.get(i)));
            } else if (isNumber(file.get(i))) {
                stack.add(Integer.parseInt(file.get(i)));
            } else if (isOperator(file.get(i))) {
                //stack.add(file.get(i));
                //make the stack object orientated? not integers?
                executeStack(file.get(i));
            }
            // A variable must have a length of 3 arguments
            // <variable name> = <value>
            else if ((i < file.size() - 2) && isVariable(file.get(i + 1))) {
                dictionary.put(file.get(i), file.get(i + 2));
                i += 2;
            } else if (isWord(file.get(i))) {
                int index = findEndOfWord(file, i);

                String name = file.get(i++ + 1);
                ArrayList<String> body = new ArrayList<String>();

                for (int j = i + 1; j < index; j++, i++) {
                    body.add(file.get(j));
                }

                phonebook.put(name, body);
            }
        }
    }

    public int findEndOfWord (ArrayList<String> file, int j) {
        for (int i = j; i < file.size(); i++) {
            if (file.get(i).equals(";")) return i;
        }

        System.out.println("Word has no end, oh god no!");
        return 0;
    }

    public void executeStack (String operator) {
        int result = -1;

        try {
            switch (operator) {
                case "+": stack.add(stack.pop() + stack.pop()); 
                          break;
                case "-": stack.add(stack.pop() - stack.pop());
                          break;
                case "*": stack.add(stack.pop() * stack.pop());
                          break;
                case "/": stack.add(stack.pop() / stack.pop());
                          break;
                case "dup": result = stack.pop(); 
                            stack.add(result);
                            stack.add(result);
                            break;
                case "rot": int tempA = stack.pop();
                            int tempB = stack.pop();
                            result = stack.pop();
                            stack.add(tempB); 
                            stack.add(tempA);
                            stack.add(result);
                            break;
                case "swap": int temp = stack.pop();
                             result = stack.pop();
                             stack.add(temp);
                             stack.add(result);
                             break;
                case "cr": System.out.println(); 
                           break;
                case ".": System.out.print(stack.pop());
                          break;
            }
        } catch (java.util.EmptyStackException fuck) {
            System.out.println(fuck);
        }
    }

    public boolean isWordCall (String wordName) {
        return phonebook.containsKey(wordName);
    }

    public boolean isVariableCall (String variableName) {
        return dictionary.containsKey(variableName);
    }

    public boolean isOperator (String defAnOperator) {
        return defAnOperator.equals("+")    ||
               defAnOperator.equals("-")    ||
               defAnOperator.equals("*")    ||
               defAnOperator.equals("/")    ||
               defAnOperator.equals("dup")  || // same v
               defAnOperator.equals("swap") || 
               defAnOperator.equals("rot")  || 
               defAnOperator.equals("cr")   ||
               defAnOperator.equals("."); // specially temporary
    }

    public boolean isWord(String itHasToBeAWord) {
        return itHasToBeAWord.equals(":");
    }

    public boolean isVariable(String hopefullyAnEqualSign) {
        return hopefullyAnEqualSign.equals("=");
    }

    public boolean isNumber (String maybeANumber) {
        try {
            int itIsANumber = Integer.parseInt(maybeANumber);
        } catch (NumberFormatException okItsNotANumber) {
            //System.out.println("It was not a number");
            return false;
        }
        return true;
    }
}
