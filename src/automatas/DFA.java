package automatas;


import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Classic finite state automata.
 * @author vick08bv
 */
public class DFA extends NDFA{

    
    public DFA(int id, String name){
        super(id, name);
    }

    
    public int currentState(){  
        if(this.currentStates.size() == 1){
            return (int) this.currentStates.iterator().next();
        }
        return 0;
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


    @Override
    public void setCurrentStates(HashSet<Integer> ids){
        throw new UnsupportedOperationException("DFA can be in only state at any time");
    }
    
    
    @Override
    public boolean addArrow(int idD, Character character, int idI){
    
        if(this.containsState(idD) && this.containsState(idI) &&
           this.containsCharacter(character)){
            
            HashSet<Integer> transitions = this.states.get(idD).transitions.get(character);
            
            if(transitions.isEmpty()){
                transitions.add(idI);
            } else {
                transitions.clear();
                transitions.add(idI);
            }
            return true;
        }
        return false;
    }
    
    
    @Override
    public boolean removeArrow(int idD, Character character, int idI){
    
        if(this.containsState(idD) && this.containsCharacter(character)){
            
            HashSet<Integer> transitions = this.states.get(idD).transitions.get(character);
            
            if(transitions.contains(idI)){
                transitions.clear();
            }
            return true;
        }
        return false;
        
    }
        

    public boolean removeArrow(int idD, Character character){
    
        if(this.containsState(idD) && this.containsCharacter(character)){
            this.states.get(idD).transitions.get(character).clear();
            return true;
        }
        return false;
        
    }
    
    
    private boolean completeState(int id){
        
        HashMap<Character,HashSet<Integer>> map = this.states.get(id).transitions;
        if(this.consistentState(id)){
            for(Character character: this.alphabet()){
                if(map.get(character).isEmpty()){return false;}
            }
            return true;
        } 
        return false;
    
    }
    
   
    public HashSet<Integer> incompleteStates(){
    
        HashSet<Integer> incompleteStates = new HashSet<>();
        
        for(int id: this.states.keySet()){
            if(!this.completeState(id)){
                incompleteStates.add(id);
            }
        }
        return incompleteStates;
        
    }
    
    
    @Override
    public String toString(){
        
        ArrayList<Integer> ids = new ArrayList<>(this.states.keySet().size());
        ids.addAll(this.states.keySet());
        ArrayList<Integer> idsF = new ArrayList<>(this.acceptStates.size());
        idsF.addAll(this.acceptStates);
        Collections.sort(ids);
        Collections.sort(idsF);
        
        String string = "\nDFA: " + this.name + 
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

    
}