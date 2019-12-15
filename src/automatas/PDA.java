package automatas;


import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.AbstractMap.SimpleImmutableEntry;

import objects.State;
import objects.Alphabet;


/**
 * Deterministic implementation of pushdown automata.
 * @author vick08bv
 */
public class PDA extends FSA{
    
    
    int initialState;
    Alphabet walphabet;
    Character initialCharacter;
    ArrayDeque<Character> stack;
    HashSet<Integer> acceptStates;
    HashSet<Integer> currentStates;
    HashMap<Integer, PDAState> states;
    
    
    public PDA(int id, String name){
        
        super(id, name);
        
        this.initialState = 0;
        this.initialCharacter = null;
        this.states = new HashMap<>();
        this.acceptStates = new HashSet<>();
        this.currentStates = new HashSet<>(2);
        this.walphabet = new Alphabet();
        this.stack = new ArrayDeque<Character>();
        
    }

    
    @Override
    public boolean addToAlphabet(Character character){
        
        return this.alphabet.addCharacter(character);
        
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
            this.cleanTransitions();
            return true;
        }        
        return false;
        
    }

    @Override
    public void clearAlphabet() {
        
        this.alphabet.clearAlphabet();
        this.cleanTransitions();
        
    }

    @Override
    public void changeAlphabet(CharSequence characters) {
        
        this.clearAlphabet();
        this.addToAlphabet(characters);
        this.cleanTransitions();
        
    }
    
    
    public boolean addToWAlphabet(Character character){
        
        return this.walphabet.addCharacter(character);
        
    }

    
    public void addToWAlphabet(CharSequence characters){
        
        for(int i = 0; i < characters.length(); i++){            
            this.addToWAlphabet(characters.charAt(i));
        }
        
    }
    
    
    public boolean removeFromWAlphabet(Character character) {       
        
        if(this.walphabet.removeCharacter(character)){
            this.cleanTransitions();
            return true;
        }        
        return false;
        
    }
    
    
    public void clearWAlphabet() {   
        
        this.walphabet.clearAlphabet();   
        this.cleanTransitions();
        
    }

    
    public void changeWAlphabet(CharSequence characters) {
        
        this.clearWAlphabet();
        this.addToWAlphabet(characters);
        this.cleanTransitions();
        
    }
    
    
    private void cleanTransitions(){

        PDAState state = null;
        HashSet<SimpleImmutableEntry<Character, Character>> nTransitions = new HashSet<>();
        
        if(this.alphabet.getSize() == 0 || this.walphabet.getSize() == 0){

            for(int id: this.states.keySet()){
                state = (PDAState) this.states.get(id);
                state.transitions.clear();
            }
        
        } else {
            
            for(int id: this.states.keySet()){
                
                state = (PDAState) this.states.get(id);
                
                for(SimpleImmutableEntry<Character,Character> arg: state.transitions.keySet()){

                    if(!this.allowedEntry(arg, state.transitions.get(arg))){
                        nTransitions.add(arg);
                    }
                    
                }
                
                for(SimpleImmutableEntry<Character,Character> narg: nTransitions){
                    state.transitions.remove(narg);
                }
                
                nTransitions.clear();
                
            }
            
        }
        
    }
    
    
    public boolean allowedEntry(SimpleImmutableEntry<Character,Character> arg, 
                                SimpleImmutableEntry<Integer,Character> img){
    
    return (this.containsCharacter(arg.getKey())) &&
           (this.allowedStackTransition(arg.getValue(), img.getValue())) &&
           (this.states.containsKey(img.getKey()));
    
    }
    
    
    private boolean allowedStackTransition(Character out, Character in){
    
        return (in == null || this.walphabet.containsCharacter(in)) && 
               (out == null || this.walphabet.containsCharacter(out));
    
    }
    
    
    private boolean allowedStackTransition(HashMap<Character, Character> 
                                                       stackTransitions){
    
        if(stackTransitions == null){return false;}
        
        for(Character key: stackTransitions.keySet()){
            if(!this.allowedStackTransition(key, stackTransitions.get(key))){
                return false;
            }
        }
        return true;
        
    }
    
    
    public boolean containsState(int id){    
        return this.states.containsKey(id);   
    }
    
    
    public int initialState(){    
        return this.initialState;        
    }
    
    
    public int currentState(){  
        if(this.currentStates.size() == 1){
            return (int) this.currentStates.iterator().next();
        }
        return 0;
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

    
    @Override
    public void addState(String name) {
    
        PDAState state = new PDAState(this.idStates, name);
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
            
            if(this.acceptStates.contains(id)){
                this.removeFinalState(id);
            }
            this.states.remove(id);
            this.cleanTransitions();

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
    
    
    public void start() {
        
        this.currentStates.clear();
        this.currentStates.add(this.initialState);
        this.stack.clear();
        
        if(this.initialCharacter == null){
        } else {
            this.stack.offerFirst(this.initialCharacter);
        }
        
    }
   
    public boolean setInitialCharacter(Character character){
        
        if(this.walphabet.containsCharacter(character)){
            this.initialCharacter = character;
        }
        return false;
    
    }
    
    
    public void setInitialCharacter(){
            this.initialCharacter = null;
    }
    
    
    public boolean addArrow(int idD, Character character, Character stackOut, 
                            int idI, Character stackIn){
    
        if(this.containsState(idD) && this.containsState(idI) 
        && this.containsCharacter(character)
        && this.allowedStackTransition(stackIn, stackOut)){
            
            this.states.get(idD).transitions.put(
            new SimpleImmutableEntry<>(character, stackOut), 
            new SimpleImmutableEntry<>(idI, stackIn));
                        
            return true;
        }
        return false;
        
    }


    public boolean removeArrow(int idD, Character character){
    
        if(this.containsState(idD) && this.containsCharacter(character)){
            
            HashSet<SimpleImmutableEntry<Character, Character>> ntransitions = new HashSet<>();
            
            for(SimpleImmutableEntry<Character, Character> arg: 
                this.states.get(idD).transitions.keySet()){
                
                if(arg.getKey().equals(character)){ntransitions.add(arg);}
            
            }
            
            for(SimpleImmutableEntry<Character, Character> arg: ntransitions){
                this.states.get(idD).transitions.remove(arg);
            }
            
            return true;
        }
        return false;
        
    }
    
    
    public boolean removeArrow(int idD, Character character, Character stackOut){
    
        if(this.containsState(idD) && this.containsCharacter(character) && 
                             this.alphabet.containsCharacter(stackOut)){
            
            this.states.get(idD).transitions.remove(
            new SimpleImmutableEntry<>(character, stackOut));
           
            return true;
        }
        return false;
        
    }
    
    
    @Override
    public void readCharacter(Character character){

        HashSet<Integer> news = new HashSet<>(2);
        HashSet<SimpleImmutableEntry<Character,Character>> args = new HashSet<>();
        
        for(int id: this.currentStates){
            
            for(SimpleImmutableEntry<Character, Character> arg: 
                this.states.get(id).transitions.keySet()){
            
                if(arg.getKey() == character){
                    args.add(arg);
                }
                
            }
                    
            for(SimpleImmutableEntry<Character, Character> arg: args){
                
                if(arg.getValue() == null){  
                    
                    news.add(this.states.get(id).transitions.get(arg).getKey());
                    
                    if(this.states.get(id).transitions.get(arg).getValue() == null){
                    } else {
                        this.stack.offerFirst(this.states.get(id).transitions.get(arg).getValue());
                    }
                    this.currentStates = news;
                    return;
                }
                
            }
            
            for(SimpleImmutableEntry<Character, Character> arg: args){
                
                if(arg.getValue() == this.stack.peekFirst()){ 
                    
                    news.add(this.states.get(id).transitions.get(arg).getKey());
                    
                    this.stack.pollFirst();
                    
                    if(this.states.get(id).transitions.get(arg).getValue() == null){
                    } else {
                        this.stack.offerFirst(this.states.get(id).transitions.get(arg).getValue());
                    }
                    this.currentStates = news;
                    return;
                }
            }
            args.clear();
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
            for(int i: this.currentStates){
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
    
    
    @Override
    public String toString(){
        
        ArrayList<Integer> ids = new ArrayList<>(this.states.keySet().size());
        ids.addAll(this.states.keySet());
        ArrayList<Integer> idsF = new ArrayList<>(this.acceptStates.size());
        idsF.addAll(this.acceptStates);
        Collections.sort(ids);
        Collections.sort(idsF);
        
        String string = "\nPDA: " + this.name + 
                        ", Id: " + String.valueOf(this.id);
        
        string += "\nInput alphabet:" + this.alphabet.toString();
        string += "\nOutput alphabet:" + this.walphabet.toString();
        
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
    
    
}

/**
 * State for a NDFA.
 * @author vick08bv
 */
class PDAState extends State {
    
    
    HashMap<SimpleImmutableEntry<Character, Character>, 
            SimpleImmutableEntry<Integer, Character>> transitions;    
    
    
    PDAState(int id, String nombre){
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
        
        for(SimpleImmutableEntry<Character, Character> key: this.transitions.keySet()){
                
            string += "\n" + String.valueOf(key.getKey()) + ", " + 
                             String.valueOf(key.getValue()) + " --> " + 
                             String.valueOf(this.transitions.get(key).getKey()) + ", " +
                             String.valueOf(this.transitions.get(key).getValue());
            
        }
        return string;
        
    }

    
}