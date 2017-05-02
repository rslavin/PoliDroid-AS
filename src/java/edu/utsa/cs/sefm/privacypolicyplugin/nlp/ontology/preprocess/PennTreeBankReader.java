package edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class PennTreeBankReader {
    String pennTreeString = null;
    private int i = 0;
    private int n;

    PennTreeBankReader(String pennTreeString) {
        this.pennTreeString = pennTreeString;
        n = pennTreeString.length();
    }

    private char nextChar() {
        char currentChar;
        currentChar = pennTreeString.charAt(i);
        i++;
        return currentChar;
    }

    private List<String> tokenize() {
        List<String> tokens = new ArrayList<>();
        char currentChar = nextChar();
        while (i < n){
            if ((currentChar == '(') || (currentChar == ')')) {
                String s = Character.toString(currentChar);
                tokens.add(s);
                currentChar = nextChar();
                continue;
            }
            if (currentChar == ' ') {
                currentChar = nextChar();
                continue;
            }
            StringBuilder st = new StringBuilder();
            while ((currentChar != '(') &&
                    (currentChar != ')') &&
                    (currentChar != ' ') &&
                    (i < n)) {
                st.append(Character.toString(currentChar));
                currentChar = nextChar();
            }
            if (!(st.length() == 0)) {
                tokens.add(st.toString());
            }
        }
        return tokens;
    }
    DefaultTreeModel ptbTreeBuilder() throws IOException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
        DefaultMutableTreeNode current = root;
        int state = 0;
        List<String> tokenList = tokenize();
        if (!tokenList.get(1).equalsIgnoreCase("root")){
            //throw new IllegalArgumentException("the ptb should start with root as the first tag");
             return new DefaultTreeModel(null);
        }

//        for (String token : tokenList){
//            System.out.println(token);
//        }
        for (String token : tokenList){
            switch(state){
                case 0:
                    if (token.equals("(")){
                        DefaultMutableTreeNode child = new DefaultMutableTreeNode();
                        current.add(child);
                        current = child;
                        state = 1;
                    } else {
                        throw new IllegalArgumentException("the ptb should start with [(]");
                    }
                    break;
                case 1:
                    if (token.equals("(") || token.equals(")")) {
                        throw new IllegalArgumentException("expecting [tag]");
                    } else {
                        current.setUserObject(token);
                        state = 2;
                    }
                    break;
                case 2:
                    switch (token) {
                        case ")":
                            throw new IllegalArgumentException("expecting [(] or [word]");
                        case "(": {
                            DefaultMutableTreeNode child = new DefaultMutableTreeNode();
                            current.add(child);
                            current = child;
                            state = 1;
                            break;
                        }
                        default: {
                            DefaultMutableTreeNode child = new DefaultMutableTreeNode(token);
                            current.add(child);
                            state = 3;
                            break;
                        }
                    }
                    break;
                case 3:
                    if (token.equals(")")) {
                        if (current == null) { // TODO: Mitra, this is always false. What was the intention?
                            throw new IllegalArgumentException("too much [)]");
                        }
                        current = (DefaultMutableTreeNode) current.getParent();
                        if (current.getParent() == null) {
                            break;
                        }
                    } else if (token.equals("(")) {
                        DefaultMutableTreeNode child = new DefaultMutableTreeNode();
                        current.add(child);
                        current = child;
                        state = 1;
                    }
                    break;
            }
        }
        return new DefaultTreeModel(root);
    }
}