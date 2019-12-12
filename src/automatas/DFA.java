package automatas;


import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;

import objects.State;
import objects.Alphabet;


/**
 * Classic finite state automata.
 * @author vick08bv
 */
public class DFA extends FSA{
    
    
    /**
     * Casts a DFA to a NFA.
     * @param m Given DFA.
     * @return The same DFA but with NFA structure.
     */
    public static NFA DFAtoNFA(DFA m){
        
        int mSize = m.numberStates();
        
        NFA p = new NFA(m.id, m.name);
        
        if(mSize == 0){
            return p;
        }
        
        for(Character character: m.alphabet.alphabet()){
            p.addToAlphabet(character);
        }
        
        ArrayList<Integer> mstates = new ArrayList<>(mSize);
        mstates.addAll(m.states.keySet());
        Collections.sort(mstates);
        
        for(int i = 0; i < mSize; i++){
            
            p.addState(m.states.get(mstates.get(i)).getName());
            
            if(m.finals.contains(mstates.get(i))){
                p.addFinalState(i + 1);
            }
            
        }
        
        p.setInitialState(mstates.indexOf(m.initial) + 1);
        
        for(int i = 0; i < mSize; i++){
            for(Character character: p.alphabet()){
                
                p.addArrow(i + 1, character, 
                mstates.indexOf(
               m.states.get(mstates.get(i)).
            transitions.get(character)) + 1);
            
            }            
        }

        return p;

    }
    
    
    protected int initial;
    protected int current;
    protected HashSet<Integer> finals;
    protected HashMap<Integer, DFAState> states;

    
    public DFA(int id, String name){
        
        super(id, name);
        this.current = 0;
        this.initial = 0;
        this.states = new HashMap<>();
        this.finals = new HashSet<>();
        
    }
    
    
    public DFA(int id, String name, Alphabet alphabet){
        
        super(id, name, alphabet);
        this.current = 0;
        this.initial = 0;
        this.states = new HashMap<>();
        this.finals = new HashSet<>();
        
    }
    
    
    @Override
    public boolean addToAlphabet(Character character){
        
        if(this.alphabet.addCharacter(character)){
            for(DFAState state: this.states.values()){
                state.transitions.put(character, 0);
            }
            return true;
        }
        return false;
        
    }
    
    
    public int initialState(){
        return this.initial;        
    }
    
    
    public int currentState(){    
        return this.current;        
    }
    
    
    public HashSet<Integer> finalStates(){    
        return (HashSet<Integer>) this.finals;    
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
            for(DFAState state: this.states.values()){
                state.transitions.remove(character);
            }
            return true;
        }
        return false;
        
    }

    @Override
    public void clearAlphabet() {
        
        this.alphabet.clearAlphabet();
        for(DFAState state: this.states.values()){
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
    public void addState(String name) {
        
        DFAState state = new DFAState(this.idStates, name);
        for(Character character: this.alphabet.alphabet()){
            state.transitions.put(character, 0);
        }
        this.states.put(this.idStates, state);
        this.idStates++;

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
        
        if(this.containsState(id)){
            this.current = id;
        }
        
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
                this.alphabet.containsCharacter(character)){
                this.states.get(idD).transitions.put(character, idI);
                return true;
        }
        return false;
        
    }
    
    
    public boolean removeArrow(int idD, Character character, int idI){
    
        if(this.containsState(idD) && 
                this.alphabet.containsCharacter(character)){
                this.states.get(idD).transitions.replace(character, idI, 0);
                return true;
        }
        return false;
        
    }
    
    
    public void start() {
        this.current = this.initial;
    }
    
    
    @Override
    public void readCharacter(Character character){
        
        this.current = (int) this.states.get(this.current).
                             transitions.get(character);
    
    }
    
    
    @Override
    public void readString(CharSequence string){
    
        for(int i = 0; i < string.length(); i ++){
            this.readCharacter(string.charAt(i));
        }
        
    }
    
    
    @Override
    public boolean accepts(){
        return this.finals.contains(this.current);
    }
    
    
    @Override
    public boolean accepts(CharSequence string) {
        
        this.start();
        this.readString(string);
        return this.accepts();
        
    }
    
    
    private boolean consistentState(int id){
        
        HashMap<Character,Integer> map = this.states.get(id).transitions;
        
        for(Integer i: map.values()){
            if(this.states.keySet().contains(i)){
                continue;
            } else {
                return false;
            }
        }
        return true;
        
    }
    
    
    private boolean completeState(int id){
        
        HashMap<Character,Integer> map = this.states.get(id).transitions;
        if(this.consistentState(id)){
            if(map.keySet().containsAll(this.alphabet.alphabet())){
                return true;
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
            if(!this.consistentState(i)){
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
        
        String string = "\nDFA: " + this.name + 
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
 * State for a DFA.
 * @author vick08bv
 */
protected class DFAState extends State{   
    
    protected HashMap<Character, Integer> transitions;
    
    protected DFAState(int id, String name){
        super(id, name);
        this.transitions = new HashMap<>();
    }

    @Override
    public String toString(){
        
        String string = "State: " + this.name + 
                        ", Id: " + String.valueOf(this.id) + 
                        "\nTransitions:";
        
        ArrayList<Character> characters = new ArrayList<>(this.transitions.keySet().size());
        characters.addAll(this.transitions.keySet());
        Collections.sort(characters);
        
        for(Character character: characters){
            string += "\n" + String.valueOf(character) + " --> " + 
                             String.valueOf(this.transitions.get(character));
        }
        return string;
        
    }
    
}


}