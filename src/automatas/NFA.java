package automatas;


import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;

import objects.State;
import objects.Alphabet;


/**
 * Deterministic finite automata.
 * @author vick08bv
 */
public class NFA extends FSA{
    
    
    /**
     * Returns the power set of a given set.
     * @param <T> The objects type stored in the set.
     * @param originalSet Given set.
     * @return The power set.
     */
    protected static <T> ArrayList<ArrayList<T>> powerSet(List<T> originalSet) {
        
        ArrayList<ArrayList<T>> sets = new ArrayList<>(
                    (int)Math.pow(2,originalSet.size()));
        
        if (originalSet.isEmpty()) {
            sets.add(new ArrayList<>(originalSet.size()));
            return sets;
        }
    
        List<T> list = new ArrayList<>(originalSet);
        T head = list.get(0);
        List<T> rest = new ArrayList<>(list.subList(1, list.size())); 
    
        for (ArrayList<T> set : powerSet(rest)) {
            ArrayList<T> newSet = new ArrayList<>(originalSet.size());
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }       
        return sets;
    } 
    
    
    /**
     * Makes a DFA equivalent to given NFA.
     * @param n NFA to be "converted".
     * @return A new DFA.
     */
    public static DFA NFAtoDFA(NFA n){
        
        int DfaSize = (int) Math.pow(2, n.numberStates());
        
        if(DfaSize == 0){
            return new DFA(n.id(), n.name());
        }
        
        DFA p = new DFA(n.id(), n.name()); 
        for(Character character: n.alphabet.alphabet()){
            p.addToAlphabet(character);
        }
        
        ArrayList<Integer> dfaCurrent = new ArrayList<>(DfaSize);
        dfaCurrent.addAll(n.states.keySet());
        
        ArrayList<ArrayList<Integer>> dfaStates = powerSet(dfaCurrent);
        
        dfaStates.forEach(Collections::sort);
        
        Collections.sort(dfaStates, (ArrayList<Integer> i, ArrayList<Integer> j) -> {
            int l = i.size(); int m = j.size();            
            if(l == m){
                for(int k = 0; k < l; k++){
                    if(i.get(k) < j.get(k)){ return -1; }
                    if(i.get(k) > j.get(k)){ return 1; }
                } return 0;
            }
            if(l < m){ return -1; } return 1;
        });
        
        p.addState("{}");
        
        String dfaStateName = "";
        
        for(int i = 1; i < DfaSize ; i++){
            dfaStateName = "{";
            for(int j = 0; j < dfaStates.get(i).size(); j++){            
                dfaStateName += String.valueOf(dfaStates.get(i).get(j)) + ", ";             
            }
            p.addState(dfaStateName.substring(0, dfaStateName.length()-2) + "}");
        }
        
        dfaCurrent.clear();
        
        n.start();
        n.readNull();
        dfaCurrent.addAll(n.currentStates());
        Collections.sort(dfaCurrent);
        
        p.setInitialState(dfaStates.indexOf(dfaCurrent)+1);
        
        for(int i = 1; i < DfaSize ; i++){
            for(int finalState: n.finalStates()){
                if(dfaStates.get(i).contains(finalState)){
                    p.addFinalState(i+1);
                    break;
                }
            }
        }
        
        HashSet<Integer> possibleStates = new HashSet<>(DfaSize);
        HashSet<Integer> nfaCurrent = new HashSet<>();
        
        for(int i = 1; i < DfaSize ; i++){
            for(Character character: p.alphabet()){
                
                dfaCurrent.clear();
                nfaCurrent.clear();
                nfaCurrent.addAll(dfaStates.get(i));
                
                n.setCurrentStates(nfaCurrent);
                n.readCharacter(character);
                
                dfaCurrent.addAll(n.currentStates());
                Collections.sort(dfaCurrent);
                
                p.addArrow(i+1, character, dfaStates.indexOf(dfaCurrent)+1);
                
                possibleStates.add(dfaStates.indexOf(dfaCurrent)+1);
            
            }
        }
        
        for(int i = 1; i <= DfaSize ; i++){
            if(!possibleStates.contains(i) && p.initialState() != i){
                p.deleteState(i);
            }
        }
        
        for(Character character: p.alphabet()){
            p.addArrow(1, character, 1);
        }

        return p;

    }
    
    
    /**
     * Makes a NFA that recognizes the union of the languages of the given NFAs.
     * @param m First NFA.
     * @param n Second NFA.
     * @return A new NFA.
     */
    public static NFA Union(NFA m, NFA n){
        
        int mSize = m.numberStates();
        int nSize = n.numberStates();
        
        if(mSize == 0 || nSize == 0 || !(m.alphabet.equals(n.alphabet))){
            return new NFA(m.id + n.id, m.name + " U " + n.name);
        }
        
        NFA p = new NFA(m.id + n.id, m.name + "U" + n.name); 
        for(Character character: m.alphabet.alphabet()){
            p.addToAlphabet(character);
        }
        
        p.addState("Initial");
        p.setInitialState(1);
        
        ArrayList<Integer> mstates = new ArrayList<>(mSize + 1);
        mstates.addAll(m.states.keySet());
        Collections.sort(mstates);
        
        for(int i = 0; i < mSize; i++){
            p.addState(m.name + " " + String.valueOf(mstates.get(i)));
            if(m.finals.contains(mstates.get(i))){
                p.addFinalState(i + 2);
            }
        }
        
        ArrayList<Integer> nstates = new ArrayList<>(nSize + 1);
        nstates.addAll(n.states.keySet());
        Collections.sort(nstates);
        
        for(int i = 0; i < nSize; i++){
            p.addState(n.name + " " + String.valueOf(nstates.get(i)));
            if(n.finals.contains(nstates.get(i))){
                p.addFinalState(i + mSize + 2);
            }
        }
        
        
        for(int i = 0; i < mSize; i++){
            for(Character character: p.alphabet()){
                for(int idI: m.states.get(mstates.get(i)).transitions.get(character)){
                    p.addArrow(i + 2, character, mstates.indexOf(idI) + 2);
                }
            }
            for(int idI: m.states.get(mstates.get(i)).transitions.get(null)){
                p.addArrow(i + 2, null, mstates.indexOf(idI) + 2);
            }
            
        }
        
        
        for(int i = 0; i < nSize; i++){
            for(Character character: p.alphabet()){
                for(int idI: n.states.get(nstates.get(i)).transitions.get(character)){
                    p.addArrow(i + nSize + 2, character, nstates.indexOf(idI) + nSize + 2);
                }
            }
            for(int idI: n.states.get(nstates.get(i)).transitions.get(null)){
                p.addArrow(i + nSize + 2, null, nstates.indexOf(idI) + nSize + 2);
            }
            
        }

        p.addArrow(1, null, mstates.indexOf(m.initial) + 2);
        p.addArrow(1, null, nstates.indexOf(n.initial) + nSize + 2);

        return p;

    }
    
    
    /**
     * Makes a NFA that recognizes the concatenaton of the languages of the given NFAs.
     * @param m First NFA.
     * @param n Second NFA.
     * @return A new NFA.
     */
    public static NFA Concatenation(NFA m, NFA n){
        
        int mSize = m.numberStates();
        int nSize = n.numberStates();
        
        if(mSize == 0 || nSize == 0 || !(m.alphabet.equals(n.alphabet))){
            return new NFA(m.id + n.id, m.name + "U" + n.name);
        }
        
        NFA p = new NFA(m.id + n.id, m.name + " O " + n.name); 
        for(Character character: m.alphabet.alphabet()){
            p.addToAlphabet(character);
        }
        
        
        ArrayList<Integer> mstates = new ArrayList<>(mSize + 1);
        mstates.addAll(m.states.keySet());
        Collections.sort(mstates);
        
        for(int i = 0; i < mSize; i++){
            p.addState(m.name + " " + String.valueOf(mstates.get(i)));
        }
        
        p.setInitialState(mstates.indexOf(m.initial) + 1);
        
        ArrayList<Integer> nstates = new ArrayList<>(nSize + 1);
        nstates.addAll(n.states.keySet());
        Collections.sort(nstates);
        
        for(int i = 0; i < nSize; i++){
            p.addState(n.name + " " + String.valueOf(nstates.get(i)));
            if(n.finals.contains(nstates.get(i))){
                p.addFinalState(i + mSize + 2);
            }
        }
        
        
        for(int i = 0; i < mSize; i++){
            for(Character character: p.alphabet()){
                for(int idI: m.states.get(mstates.get(i)).transitions.get(character)){
                    p.addArrow(i + 2, character, mstates.indexOf(idI) + 2);
                }
            }
            for(int idI: m.states.get(mstates.get(i)).transitions.get(null)){
                p.addArrow(i + 2, null, mstates.indexOf(idI) + 2);
            }
            
        }
        
        
        for(int i = 0; i < nSize; i++){
            for(Character character: p.alphabet()){
                for(int idI: n.states.get(nstates.get(i)).transitions.get(character)){
                    p.addArrow(i + nSize + 2, character, nstates.indexOf(idI) + nSize + 2);
                }
            }
            for(int idI: n.states.get(nstates.get(i)).transitions.get(null)){
                p.addArrow(i + nSize + 2, null, nstates.indexOf(idI) + nSize + 2);
            }
            
        }

        for(int i: m.finals){
            p.addArrow(mstates.indexOf(i + 1), null, 
                       mstates.indexOf(m.initial) + 1);
        }

        return p;

    }
    
    
    /**
     * Makes a NFA that recognizes the kleene star of the language of the given NFA.
     * @param n Given NFA.
     * @return A new NFA.
     */
    public static NFA KleeneStar(NFA n){
        
        int nSize = n.numberStates();
        
        NFA p = new NFA(n.id, n.name + " * ");
        
        if(nSize == 0){
            return p;
        }
        
        for(Character character: n.alphabet.alphabet()){
            p.addToAlphabet(character);
        }
        
        p.addState("Initial");
        p.setInitialState(1);
        
        ArrayList<Integer> nstates = new ArrayList<>(nSize + 1);
        nstates.addAll(n.states.keySet());
        Collections.sort(nstates);
        
        for(int i = 0; i < nSize; i++){
            p.addState(n.states.get(nstates.get(i)).getName());
            if(n.finals.contains(nstates.get(i))){
                p.addFinalState(i + 2);
            }
        }
        
        
        for(int i = 0; i < nSize; i++){
            for(Character character: p.alphabet()){
                for(int idI: n.states.get(nstates.get(i)).transitions.get(character)){
                    p.addArrow(i + 2, character, nstates.indexOf(idI) + 2);
                }
            }
            for(int idI: n.states.get(nstates.get(i)).transitions.get(null)){
                p.addArrow(i + 2, null, nstates.indexOf(idI) + 2);
            }
            
        } 

        for(int i: n.finals){
            p.addArrow(nstates.indexOf(i + 1), null, 1);
        }

        return p;

    }
    
    
    protected int initial;
    protected HashSet<Integer> current;
    protected HashSet<Integer> finals;
    protected HashMap<Integer, NFAState> states;

    
    public NFA(int id, String name){
        
        super(id, name);
        this.initial = 0;
        this.states = new HashMap<>();
        this.finals = new HashSet<>();
        this.current = new HashSet<>();
        
    }
    
    
    public NFA(int id, String name, Alphabet alphabet){
        
        super(id, name, alphabet);       
        this.initial = 0;
        this.states = new HashMap<>();
        this.finals = new HashSet<>();
        this.current = new HashSet<>();
        
    }
    
    
    public int initialState(){    
        return this.initial;        
    }
    
    
    public HashSet<Integer> currentStates(){    
        return (HashSet<Integer>) this.current;    
    }
    
    
    public HashSet<Integer> finalStates(){    
        return (HashSet<Integer>) this.finals;    
    }

    
    @Override
    public boolean addToAlphabet(Character character){
        
        if(character == null){
            return false;
        }
        
        if(this.alphabet.addCharacter(character)){
            for(NFAState state: this.states.values()){
                state.transitions.put(character, new HashSet<>());
            }
            return true;
        }
        return false;
        
    }

    
    @Override
    public void addToAlphabet(CharSequence characters){
        
        for(int i = 0; i < characters.length(); i++){            
            this.addToAlphabet(characters.charAt(i));
        }

    }
    
    
    @Override
    public boolean removeFromAlphabet(Character character) {
        
        if(character == null){
            return false;
        }
        
        if(this.alphabet.removeCharacter(character)){
            for(NFAState state: this.states.values()){
                state.transitions.remove(character);
            }
            return true;
        }
        return false;
        
    }

    @Override
    public void clearAlphabet() {
        
        this.alphabet.clearAlphabet();
        for(NFAState state: this.states.values()){
            state.transitions.clear();
        }
        
    }

    @Override
    public void changeAlphabet(CharSequence characters) {
        
        this.clearAlphabet();
        this.addToAlphabet(characters);
        
    }
    
    
    public boolean containsState(int id){    
        return this.states.containsKey(id);   
    }
    
    
    @Override
    public int setInitialState(int id){
        
        if(this.containsState(id)){
            if(this.initial == id){
                return 0;
            }
            this.initial = id;
            return 1;
        } else {
            return -1;
        }
        
    }
    
    
    public void setCurrentState(int id){
        
        if(this.states.keySet().contains(id)){
            this.current.clear();
            this.current.add(id);
        }
        
    }
    

    public void setCurrentStates(HashSet<Integer> ids){
        
        if(this.states.keySet().containsAll(ids)){
            this.current.clear();
            for(Integer id: ids){
                this.current.add(id);
            }
        }
        
    }
    
    
    @Override
    public void addState(String name) {
        
        NFAState state = new NFAState(this.idStates, name);
        state.transitions.put(null, new HashSet<>());
        for(Character character: this.alphabet.alphabet()){
            state.transitions.put(character, new HashSet<>());
        }
        this.states.put(this.idStates, state);
        this.idStates++;
        
    }
    
    
    @Override
    public int numberStates(){
        return this.states.size();
    }

    
    @Override
    public void deleteState(int id) {
        
        if(this.containsState(id)){          
            for(int i: this.states.keySet()){
                for(Character character: this.alphabet.alphabet()){
                    this.removeArrow(i, character, id);
                }
                this.removeArrow(i, null, id);
            }            
            if(this.finals.contains(id)){
                this.removeFinalState(id);
            }
            this.states.remove(id);
        }
        
    }

    
    public void addFinalState(int id){
    
        if(this.containsState(id)){
            this.finals.add(id);
        }
    
    }
    
    
    public void removeFinalState(int id){
    
        if(this.containsState(id)){
            this.finals.remove(id);
        }
    
    }
    
    
    public boolean addArrow(int idD, Character character, int idI){
    
        if(this.containsState(idD) && this.containsState(idI) &&
          (this.alphabet.containsCharacter(character) || character == null)){
            
            this.states.get(idD).transitions.get(character).add(idI);
            return true;
        }
        return false;
    }
    
    
    public boolean removeArrow(int idD, Character character, int idI){
    
        if(this.containsState(idD) && this.containsState(idI) &&
          (this.alphabet.containsCharacter(character) || character == null)){
            
            this.states.get(idD).transitions.get(character).remove(idI);
            return true;
        }
        return false;
        
    }
    
    
    public void start() {
        
        this.current.clear();
        this.current.add(this.initial);
        
    }
    
    
    private void readNull(){
    
        int old;
        ArrayList<Integer> news;
        
        while(true){
            
            old = this.current.size();
            news = new ArrayList<>();
            
            for(int i: this.current){
                news.addAll(this.states.get(i).transitions.get(null));
            }            
            
            this.current.addAll(news);
            
            if(old == this.current.size()){
                break;
            }     
            
        }
    
    }
    
    
    @Override
    public void readCharacter(Character character){
        
        this.readNull();
        
        HashSet<Integer> news = new HashSet<>();                
        
        for(int i: this.current){
            news.addAll(this.states.get(i).transitions.get(character));        
        }
        
        this.current = news;
        
        this.readNull();
        
    }
    
    
    @Override
    public void readString(CharSequence string){
    
        for(int i = 0; i < string.length(); i ++){
            this.readCharacter(string.charAt(i));
        }
        
    }
    
    
    @Override
    public boolean accepts(){
    
        if(this.current.isEmpty()){
            return false;
        } else {
            for(Integer i: this.current){
                if(this.finals.contains(i)){
                    return true;
                }
            }
            return false;
        }
        
    }
    
    
    @Override
    public boolean accepts(CharSequence string) {
        
        this.start();
        this.readString(string);
        return this.accepts();
        
    }
    
    
    private boolean consistentState(int id){
        
        for(HashSet<Integer> value: this.states.get(id).transitions.values()){
            
            for(int i: value){
                if(!this.states.keySet().contains(i)){
                    return false;
                }
            }
            
        }
        return true;
        
    }
    
    
    private boolean completeState(int id){
        
        HashMap<Character,HashSet<Integer>> map = this.states.get(id).transitions;
        if(this.consistentState(id)){
            if(map.keySet().containsAll(this.alphabet.alphabet())){
                return map.keySet().size() == this.alphabet.getSize() + 1;
            }
        } 
        return false;
    
    }
    
    
    public ArrayList<Integer> inconsistentStates(){
    
        ArrayList<Integer> inconsistentStates = new ArrayList<>(20);
        
        for(int i: this.states.keySet()){
            if(!this.consistentState(i)){
                inconsistentStates.add(i);
            }
        }
        return inconsistentStates;
        
    }
    
    
    public ArrayList<Integer> incompleteStates(){
    
        ArrayList<Integer> incompleteStates = new ArrayList<>(20);
        
        for(int i: this.states.keySet()){
            if(!this.completeState(i)){
                incompleteStates.add(i);
            }
        }
        return incompleteStates;
        
    }
    
    
    @Override
    public String toString(){
        
        ArrayList<Integer> ids = new ArrayList<>(this.states.keySet().size());
        ids.addAll(this.states.keySet());
        ArrayList<Integer> idsF = new ArrayList<>(this.finals.size());
        idsF.addAll(this.finals);
        Collections.sort(ids);
        Collections.sort(idsF);
    
        String string = "\nNFA: " + this.name + 
                        ", Id: " + String.valueOf(this.id);
        
        string += "\nAlphabet:" + this.alphabet.toString();
        
        string += "\n\nStates: ";
        
        for(int id: ids){
            string += "\n\n" + this.states.get(id).toString();
        }
        
        string += "\n\nInitial State: " + String.valueOf(this.initial);
        
        string += "\n\nFinal States:";
        for(int id: idsF){
            string += " " + String.valueOf(id);
        } 
        return string + "\n";
        
    }


/**
 * State for a NFA.
 * @author vick08bv
 */
private class NFAState extends State {
    
    private HashMap<Character,HashSet<Integer>> transitions;    
    
    private NFAState(int id, String nombre){
        super(id, nombre);
        this.transitions = new HashMap<>();
    }
    
    @Override
    public String toString(){
        
        String string = "State: " + this.name + 
                        ", Id: " + String.valueOf(this.id) + 
                        "\nTransitions:";
        
        
        ArrayList<Character> characters = new ArrayList<>(this.transitions.keySet().size());
        characters.addAll(this.transitions.keySet());
//        characters.add(characters.indexOf(null), '\000');
//        characters.remove(null);
//        Collections.sort(characters);
        ArrayList<Integer> ids = new ArrayList<>();
        
        for(Character character: characters){
            
            ids.clear();
            ids.addAll(this.transitions.get(character));
            Collections.sort(ids);
            
            string += "\n" + String.valueOf(character) + " --> " + ids.toString();
        }
        return string;
        
    }
    
}


}