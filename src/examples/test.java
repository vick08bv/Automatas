package examples;

import automatas.DFA;
import automatas.NDFA;
import automatas.FST;
import automatas.PDA;


/**
 * Tests.
 * @author vick08bv
 */
public class test {
    
    
    public static void main(String[] args){
        
//        DFA m = new DFA(1, "Machine");
//        
//        m.addToAlphabet("012");
//        m.addState("uno");
//        m.addState("dos");
//        m.addState("tres");
//        m.setInitialState(1);
//        m.addFinalState(3);
//        m.addFinalState(4);
//        m.addArrow(1, '0', 1);
//        m.addArrow(1, '1', 2);
//        m.addArrow(1, '2', 1);
//        m.addArrow(2, '0', 2);
//        m.addArrow(2, '1', 3);
//        m.addArrow(2, '2', 2);
//        m.addArrow(3, '0', 1);
//        m.addArrow(3, '1', 3);
//        m.addArrow(3, '2', 3);
//        
//        System.out.println(m);
//        System.out.println(m.inconsistentStates());
//        System.out.println(m.incompleteStates());
//        
//        System.out.println(m.accepts("11"));
       
//        NDFA n = new NDFA(2, "Super Machine");
//        
//        n.addToAlphabet("abcde");
//        n.addState("t");
//        n.addState("u");
//        n.addState("v");
//        n.addState("w");
//        n.setInitialState(2);
//        n.addFinalState(3);
//        n.addFinalState(4);
//        n.addArrow(2, null, 1);
//        n.addArrow(2, 'a', 4);
//        n.addArrow(1, 'e', 1);
//        n.addArrow(1, 'd', 3);
//        n.addArrow(3, 'c', 3);
//        n.addArrow(4, 'a', 4);
//        n.addArrow(4, 'b', 4);
//        n.addArrow(4, 'c', 4);
//        n.addArrow(4, 'd', 4);
//        n.addArrow(4, 'e', 4);
//        
//        DFA p = NDFA.NFAtoDFA(n);
//        System.out.println(p);
//        NDFA q = DFA.DFAtoNFA(p);
//        System.out.println(q);
//        
//        FST t = new FST(1, "Translater");
//        
//        t.addToAlphabet("01");
//        t.addToWAlphabet("ab");
//        t.addState("1");
//        t.addState("2");
//        t.addState("3");
//        t.setInitialState(1);
//        t.addFinalState(3);
//        t.addArrow(1, '0', 1, 'a');
//        t.addArrow(1, '1', 2, 'a');
//        t.addArrow(2, '0', 2, 'a');
//        t.addArrow(2, '1', 3, 'b');
//        t.addArrow(3, '0', 1, 'b');
//        t.addArrow(3, '1', 3, 'b');
//        
//        System.out.println(t.transduce("101010"));
//        System.out.println(t);

        PDA p = new PDA(1, "01's");
        
        p.addToAlphabet("01");
        p.addToWAlphabet("0*");
        
        p.addState("inicio");
        p.addState("ceros");
        p.addState("unos");
        p.addState("final");
        
        p.setInitialState(1);
        p.addFinalState(4);
        
        p.addArrow(1, '0', null, 2, '*');
        p.addArrow(2, '0', null, 2, '0');
        p.addArrow(2, '1', '0', 3, null);
        p.addArrow(2, '1', '*', 4, null);
        p.addArrow(3, '1', '0', 3, null);
        p.addArrow(3, '1', '*', 4, null);
        
        System.out.println(p.accepts("00000000000001111111111111"));
        
        System.out.println(p);
        
    }

    
}
