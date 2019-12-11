package examples;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;

import objects.Alphabet;

import automatas.DFA;
import automatas.NFA;
import automatas.FST;


/**
 * Tests.
 * @author vick08bv
 */
public class test {
    
    
    public static void main(String[] args){
        
        DFA m = new DFA(1, "Machine");
        
        m.addToAlphabet("01");
        m.addState("1");
        m.addState("2");
        m.addState("3");
        m.setInitialState(1);
        m.addFinalState(3);
        m.addArrow(1, '0', 1);
        m.addArrow(1, '1', 2);
        m.addArrow(2, '0', 2);
        m.addArrow(2, '1', 3);
        m.addArrow(3, '0', 1);
        m.addArrow(3, '1', 3);
        
        NFA n = new NFA(2, "Super Machine");
        
        n.addToAlphabet("abcde");
        n.addState("t");
        n.addState("u");
        n.addState("v");
        n.addState("w");
        n.setInitialState(2);
        n.addFinalState(3);
        n.addFinalState(4);
        n.addArrow(2, null, 1);
        n.addArrow(2, 'a', 4);
        n.addArrow(1, 'e', 1);
        n.addArrow(1, 'd', 3);
        n.addArrow(3, 'c', 3);
        n.addArrow(4, 'a', 4);
        n.addArrow(4, 'b', 4);
        n.addArrow(4, 'c', 4);
        n.addArrow(4, 'd', 4);
        n.addArrow(4, 'e', 4);
        
        DFA p = NFA.NFAtoDFA(n);
        
        FST t = new FST(1, "Translater");
        
        t.addToAlphabet("01");
        t.addToWAlphabet("ab");
        t.addState("1");
        t.addState("2");
        t.addState("3");
        t.setInitialState(1);
        t.addFinalState(3);
        t.addArrow(1, '0', 1, 'a');
        t.addArrow(1, '1', 2, 'a');
        t.addArrow(2, '0', 2, 'a');
        t.addArrow(2, '1', 3, 'b');
        t.addArrow(3, '0', 1, 'b');
        t.addArrow(3, '1', 3, 'b');
        
        System.out.println(t.transduce("101010"));
        System.out.println(t);
        
    }

    
}
