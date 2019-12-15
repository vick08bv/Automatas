package automatas;


import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;

import objects.State;


/**
 * Deterministic finite automata.
 * @author vick08bv
 */
public abstract class NDFA extends FSA{
    
    
    int initialState;
    HashSet<Integer> currentStates;
    HashSet<Integer> acceptStates;
    HashMap<Integer, NFAState> states;

    
    public NDFA(int id, String name){
        
        super(id, name);
        
        this.initialState = 0;
        this.states = new HashMap<>();
        this.acceptStates = new HashSet<>();
        this.currentStates = new HashSet<>(2);
        
    }

    
    @Override
    public boolean addToAlphabet(Character character){

        if(this.alphabet.addCharacter(character)){
            for(NFAState state: this.states.values()){
                state.transitions.put(character, new HashSet<>(2));
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
    
    
    public int initialState(){    
        return this.initialState;        
    }
    
    
    public HashSet<Integer> currentStates(){    
        HashSet<Integer> currentStates = new HashSet<>();
        currentStates.addAll(this.currentStates);
        return currentStates;    
    }
    
    
    public HashSet<Integer> acceptStates(){    
        HashSet<Integer> acceptStates = new HashSet<>();
        acceptStates.addAll(this.acceptStates);
        return acceptStates;    
    }

    
    @Override
    public int setInitialState(int id){
        
        if(this.containsState(id)){
            if(this.initialState == id){
                return 0;
            }
            this.initialState = id;
            return 1;
        } else {
            return -1;
        }
        
    }
    
    
    public void setCurrentState(int id){
        
        if(this.containsState(id)){
            this.currentStates.clear();
            this.currentStates.add(id);
        }
        
    }
    

    public void setCurrentStates(HashSet<Integer> ids){
        
        if(this.states.keySet().containsAll(ids)){
            this.currentStates.clear();
            for(Integer id: ids){
                this.currentStates.add(id);
            }
        }
        
    }
    
    
    @Override
    public void addState(String name) {
        
        NFAState state = new NFAState(this.idStates, name);
        
        for(Character character: this.alphabet.alphabet()){
            state.transitions.put(character, new HashSet<>(2));
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
            }            
            if(this.acceptStates.contains(id)){
                this.removeFinalState(id);
            }
            this.states.remove(id);
        }
        
    }

    
    public void addFinalState(int id){
    
        if(this.containsState(id)){
            this.acceptStates.add(id);
        }
    
    }
    
    
    public void removeFinalState(int id){
    
        if(this.containsState(id)){
            this.acceptStates.remove(id);
        }
    
    }
    
    
    public abstract boolean addArrow(int idD, Character character, int idI);
    
    public abstract boolean removeArrow(int idD, Character character, int idI);
    
    
    public void start() {
        
        this.currentStates.clear();
        this.currentStates.add(this.initialState);
        
    }
    
    
    @Override
    public void readCharacter(Character character){

        HashSet<Integer> news = new HashSet<>(2);                
        
        for(int i: this.currentStates){
            news.addAll(this.states.get(i).transitions.get(character));        
        }
        
        this.currentStates = news;
        
    }
    
    
    @Override
    public void readString(CharSequence string){
    
        for(int i = 0; i < string.length(); i ++){
            this.readCharacter(string.charAt(i));
        }
        
    }
    
    
    @Override
    public boolean accepts(){
    
        if(this.currentStates.isEmpty()){
            return false;
        } else {
            for(Integer i: this.currentStates){
                if(this.acceptStates.contains(i)){
                    return true;
                }
            }
            return false;
        }
        
    }
    
    
    @Override
    public boolean accepts(CharSequence string) {
        
        for(int i = 0; i < string.length(); i++){
            if(!this.containsCharacter(string.charAt(i))){
                return false;
            }
        }
        
        this.start();
        this.readString(string);
        return this.accepts();
        
    }
    
    
    public boolean strictlyAccepts(){
    
        if(this.currentStates.isEmpty()){
            return false;
        } else {
            return this.currentStates.containsAll(this.acceptStates);
        }
        
    }
    
    
    public boolean strictlyAccepts(CharSequence string) {
        
        for(int i = 0; i < string.length(); i++){
            if(!this.containsCharacter(string.charAt(i))){
                return false;
            }
        }
        
        this.start();
        this.readString(string);
        return this.strictlyAccepts();
        
    }
    
    
    boolean consistentState(int id){
        
        for(HashSet<Integer> value: this.states.get(id).transitions.values()){
            for(int i: value){
                if(!this.containsState(i)){
                    return false;
                }
            }
        }
        return true;
        
    }

    
    public HashSet<Integer> inconsistentStates(){
    
        HashSet<Integer> inconsistentStates = new HashSet<>();
        
        for(int id: this.states.keySet()){
            if(!this.consistentState(id)){
                inconsistentStates.add(id);
            }
        }
        return inconsistentStates;
        
    }
    
    
    @Override
    public String toString(){
        
        ArrayList<Integer> ids = new ArrayList<>(this.states.keySet().size());
        ids.addAll(this.states.keySet());
        ArrayList<Integer> idsF = new ArrayList<>(this.acceptStates.size());
        idsF.addAll(this.acceptStates);
        Collections.sort(ids);
        Collections.sort(idsF);
    
        String string = "\nNFA: " + this.name + 
                        ", Id: " + String.valueOf(this.id);
        
        string += "\nAlphabet:" + this.alphabet.toString();
        
        string += "\n\nStates: ";
        
        for(int id: ids){
            string += "\n\n" + this.states.get(id).toString();
        }
        
        string += "\n\nInitial State: " + String.valueOf(this.initialState);
        
        string += "\n\nFinal States:";
        for(int id: idsF){
            string += " " + String.valueOf(id);
        } 
        return string + "\n";
        
    }

    
    
    
    
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
     * Makes a DFA equivalent to given NDFA.
     * @param n NDFA to be "converted".
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
            for(int finalState: n.acceptStates()){
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
     * Makes a NDFA that recognizes the union of the languages of the given NFAs.
     * @param m First NDFA.
     * @param n Second NDFA.
     * @return A new NDFA.
     */
    public static NDFA Union(NFA m, NFA n){
        
        int mSize = m.numberStates();
        int nSize = n.numberStates();
        
        NFA p = new NFA(m.id + n.id, m.name + "U" + n.name);
        
        if(mSize == 0 || nSize == 0 || !(m.alphabet.equals(n.alphabet))){
            return p;
        }
        
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
            if(m.acceptStates.contains(mstates.get(i))){
                p.addFinalState(i + 2);
            }
        }
        
        ArrayList<Integer> nstates = new ArrayList<>(nSize + 1);
        nstates.addAll(n.states.keySet());
        Collections.sort(nstates);
        
        for(int i = 0; i < nSize; i++){
            p.addState(n.name + " " + String.valueOf(nstates.get(i)));
            if(n.acceptStates.contains(nstates.get(i))){
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

        p.addArrow(1, null, mstates.indexOf(m.initialState) + 2);
        p.addArrow(1, null, nstates.indexOf(n.initialState) + nSize + 2);

        return p;

    }
    
    
    /**
     * Makes a NDFA that recognizes the concatenaton of the languages of the given NFAs.
     * @param m First NDFA.
     * @param n Second NDFA.
     * @return A new NDFA.
     */
    public static NFA Concatenation(NFA m, NFA n){
        
        int mSize = m.numberStates();
        int nSize = n.numberStates();
        
        NFA p = new NFA(m.id + n.id, m.name + " O " + n.name);
        
        if(mSize == 0 || nSize == 0 || !(m.alphabet.equals(n.alphabet))){
            return p;
        }
        
        for(Character character: m.alphabet.alphabet()){
            p.addToAlphabet(character);
        }
               
        ArrayList<Integer> mstates = new ArrayList<>(mSize + 1);
        mstates.addAll(m.states.keySet());
        Collections.sort(mstates);
        
        for(int i = 0; i < mSize; i++){
            p.addState(m.name + " " + String.valueOf(mstates.get(i)));
        }
        
        p.setInitialState(mstates.indexOf(m.initialState) + 1);
        
        ArrayList<Integer> nstates = new ArrayList<>(nSize + 1);
        nstates.addAll(n.states.keySet());
        Collections.sort(nstates);
        
        for(int i = 0; i < nSize; i++){
            p.addState(n.name + " " + String.valueOf(nstates.get(i)));
            if(n.acceptStates.contains(nstates.get(i))){
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

        for(int i: m.acceptStates){
            p.addArrow(mstates.indexOf(i + 1), null, 
                       mstates.indexOf(m.initialState) + 1);
        }

        return p;

    }
    
    
    /**
     * Makes a NDFA that recognizes the kleene star of the language of the given NDFA.
     * @param n Given NDFA.
     * @return A new NDFA.
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
            if(n.acceptStates.contains(nstates.get(i))){
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

        for(int i: n.acceptStates){
            p.addArrow(nstates.indexOf(i + 1), null, 1);
        }

        return p;

    }
    
    
    /**
     * Makes a NDFA that recognizes the reverse language of the given NDFA.
     * @param n Given NDFA.
     * @return A new NDFA.
     */
    public static NFA Reverse(NFA n){
        
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
        }
        
        p.addFinalState(nstates.indexOf(n.initialState()) + 2);
        
        
        for(int i = 0; i < nSize; i++){
            for(Character character: p.alphabet()){
                for(int idI: n.states.get(nstates.get(i)).transitions.get(character)){
                    p.addArrow(nstates.indexOf(idI) + 2, character, i + 2);
                }
            }
            for(int idI: n.states.get(nstates.get(i)).transitions.get(null)){
                p.addArrow(nstates.indexOf(idI) + 2, null, i + 2);
            }
            
        } 

        for(int i: n.acceptStates){
            p.addArrow(1, null, nstates.indexOf(i + 1));
        }

        return p;

    }
    
    
}

        
/**
 * State for a NDFA.
 * @author vick08bv
 */
class NFAState extends State {
    
    
    HashMap<Character,HashSet<Integer>> transitions;    
    
    
    NFAState(int id, String nombre){
        super(id, nombre);
        this.transitions = new HashMap<>();
    }
    
    
    @Override
    public String toString(){
        
        String string = "State: " + this.name + 
                        ", Id: " + String.valueOf(this.id) + 
                        "\nTransitions:";
        
//        ArrayList<Character> characters = new ArrayList<>(this.transitions.keySet().size());
//        characters.addAll(this.transitions.keySet());
//        characters.add(characters.indexOf(null), '\000');
//        characters.remove(null);
//        Collections.sort(characters);
        
        ArrayList<Integer> ids = new ArrayList<>();
        
        for(Character character: this.transitions.keySet()){
            
            ids.clear();
            ids.addAll(this.transitions.get(character));
            Collections.sort(ids);
            
            string += "\n" + String.valueOf(character) + " --> " + ids.toString();
        }
        return string;
        
    }
    
    
}