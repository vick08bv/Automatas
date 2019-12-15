package automatas;


import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;

import objects.Alphabet;


/**
 * Finite state transducer.
 * @author vick08bv
 */
public class FST extends DFA{
    
    
    Alphabet walphabet;
    
    
    public FST(int id, String name){
        
        super(id, name);
        this.walphabet = new Alphabet();
        
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
        return this.walphabet.removeCharacter(character);        
    }
    
    
    public void clearWAlphabet() {        
        this.walphabet.clearAlphabet();       
    }

    
    public void changeWAlphabet(CharSequence characters) {
        this.clearWAlphabet();
        this.addToWAlphabet(characters);
    }
    
    
    @Override
    public void addState(String name) {
        
        FSTState state = new FSTState(this.idStates, name);
        
        for(Character character: this.walphabet.alphabet()){
            state.transductions.put(character, '\000');
        }
        for(Character character: this.alphabet.alphabet()){
            state.transitions.put(character, new HashSet<>(2));
        }
        this.states.put(this.idStates, state);
        this.idStates++;

    }

    
    public boolean addArrow(int idD, Character character, int idI, Character wcharacter){
    
        if(this.containsState(idD) && this.containsState(idI) && 
                   this.alphabet.containsCharacter(character) &&
                   this.walphabet.containsCharacter(wcharacter)){
            
            FSTState state = (FSTState) this.states.get(idD);
            
            HashSet<Integer> transitions = state.transitions.get(character);
            
            if(transitions.isEmpty()){
                transitions.add(idI);
            } else {
                transitions.clear();
                transitions.add(idI);
            }
            
            state.transductions.put(character, wcharacter);
            
            return true;
        }
        return false;
        
    }
    
    
    public void addTransduction(int idD, Character character, Character wcharacter){
    
        if(this.containsState(idD) && this.alphabet.containsCharacter(character)
                                   && this.walphabet.containsCharacter(wcharacter)){
            
            FSTState state = (FSTState) this.states.get(idD);
            state.transductions.put(character, wcharacter);
        
        }
        
    }
    
    
    public void removeTransduction(int idD, Character character, Character wcharacter){
    
        if(this.containsState(idD) && this.alphabet.containsCharacter(character)
                                   && this.walphabet.containsCharacter(wcharacter)){
            
            FSTState state = (FSTState) this.states.get(idD);
            state.transductions.replace(character, wcharacter, '\000');
        
        }
        
    }
    

    public Character transduceCharacter(Character character){
        
        FSTState state = (FSTState) this.states.get(this.currentStates.iterator().next());
        Character tCharacter = state.transductions.get(character);
        super.readCharacter(character);
        
        return tCharacter;
        
    }
    
    
    public String transduceString(CharSequence string){
    
        String tstring = "";
        
        for(int i = 0; i < string.length(); i ++){
            tstring += this.transduceCharacter(string.charAt(i));
        }
        
        return tstring;
        
    }
    
    
    public String transduce(CharSequence string) {
        this.start();
        return this.transduceString(string);
    }
    
    
    private boolean consistentStateTranslation(int id){
        
        FSTState state = (FSTState) this.states.get(id);
        HashMap<Character,Character> map = state.transductions;
        
        for(Character character: map.values()){
            if(this.walphabet.alphabet().contains(character)){
                continue;
            } else {
                return false;
            }
        }
        return true;
        
    }
    
    
    private boolean completeStateTranslation(int id){
        
        FSTState state = (FSTState) this.states.get(id);
        HashMap<Character,Character> map = state.transductions;
        if(this.consistentStateTranslation(id)){
            if(map.keySet().containsAll(this.alphabet.alphabet())){
                return true;
            }
        } 
        return false;
    
    }
    
    
    @Override
    public String toString(){
        
        ArrayList<Integer> ids = new ArrayList<>(this.states.keySet().size());
        ids.addAll(this.states.keySet());
        ArrayList<Integer> idsF = new ArrayList<>(this.acceptStates.size());
        idsF.addAll(this.acceptStates);
        Collections.sort(ids);
        Collections.sort(idsF);
        
        String string = "\nFST: " + this.name + 
                        ", Id: " + String.valueOf(this.id);
        
        string += "\nInput alphabet:" + this.alphabet.toString();
        string += "\nOutput alphabet:" + this.walphabet.toString();
        
        string += "\n\nStates: ";
      
        for(Integer id: ids){
            string += "\n\n" + this.states.get(id).toString();
        }
        
        string += "\n\nInitial State: " + String.valueOf(this.initialState);
        
        string += "\n\nFinal States:";
        for(Integer id: idsF){
            string += " " + String.valueOf(id);
        } 
        return string + "\n";
        
    }
    
    
/**
 * State for a FST.
 * @author vick08bv
 */
class FSTState extends NFAState{   
    
    
    HashMap<Character, Character> transductions;
    
    
    FSTState(int id, String name){
        
        super(id, name);
        this.transductions = new HashMap<>();
    
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
                             String.valueOf(this.transitions.get(character)) + ", " + 
                             String.valueOf(this.transductions.get(character));
        }
        return string;
        
    }
    
}


}