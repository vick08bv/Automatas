package automatas;


import objects.Alphabet;


/**
 * An All-NFA.
 * @author vick0
 */
public class AllNFA extends NFA {
    
    
    public AllNFA(int id, String name){
        super(id, name);
    }
    
    
    public AllNFA(int id, String name, Alphabet alphabet){
        super(id, name, alphabet);
    }
    
    
    /**
     * All-NFA accepts a string iff all of its 
     * final states are accept states.
     * @return 
     */
    @Override
    public boolean accepts(){
    
        if(this.current.isEmpty()){
            return false;
        }
        return this.finals.containsAll(this.current);
        
    }
    
    
}
