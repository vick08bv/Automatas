package automatas;


import java.util.HashSet;
import java.util.ArrayList;


/**
 * NDFA with epsilon-arrows.
 * @author vick08bv
 */
public class NFA extends NDFA{
    
    
    public NFA(int id, String name){
        super(id, name);
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
    public void deleteState(int id) {
        
        if(this.containsState(id)){          
            for(int i: this.states.keySet()){
                for(Character character: this.alphabet.alphabet()){
                    this.removeArrow(i, character, id);
                }
                this.removeArrow(i, null, id);
            }            
            if(this.acceptStates.contains(id)){
                this.removeFinalState(id);
            }
            this.states.remove(id);
        }
        
    }
    
    
    @Override
    public boolean addArrow(int idD, Character character, int idI){
    
        if(this.containsState(idD) && this.containsState(idI) &&
          (this.alphabet.containsCharacter(character) || character == null)){
            
            this.states.get(idD).transitions.get(character).add(idI);
            return true;
        }
        return false;
    }
    
    
    @Override
    public boolean removeArrow(int idD, Character character, int idI){
    
        if(this.containsState(idD) && this.containsState(idI) &&
          (this.alphabet.containsCharacter(character) || character == null)){
            
            this.states.get(idD).transitions.get(character).remove(idI);
            return true;
        }
        return false;
        
    }
    
    
    void readNull(){
    
        int old;
        ArrayList<Integer> news;
        
        while(true){
            
            old = this.currentStates.size();
            news = new ArrayList<>();
            
            for(int i: this.currentStates){
                news.addAll(this.states.get(i).transitions.get(null));
            }            
            
            this.currentStates.addAll(news);
            
            if(old == this.currentStates.size()){
                break;
            }     
            
        }
    
    }
    
    
    @Override
    public void readCharacter(Character character){
        
        super.readCharacter(character);
        this.readNull();
        
    }
    
    
    public HashSet<Integer> epsilonNeighbors(int id){
    
        if(this.states.keySet().contains(id)){
            
            this.setCurrentState(id);
            this.readNull();
            return this.currentStates();
        
        }
        return null;
        
    }
    
    
    public HashSet<Integer> epsilonNeighbors(int id, Character character){
    
        if(this.states.keySet().contains(id) && this.containsCharacter(character)){

            this.setCurrentState(id);
            this.readCharacter(character);
            return this.currentStates();
            
        }
        return null;
        
    }
    
    
    @Override
    public boolean accepts(CharSequence string) {
        
        for(int i = 0; i < string.length(); i++){
            if(!this.containsCharacter(string.charAt(i))){
                return false;
            }
        }
        
        this.start();
        this.readNull();
        this.readString(string);
        return this.accepts();
        
    }
    
    
}