package automatas;


import objects.Alphabet;
import java.util.HashSet;


/**
 * Template for finite state automatas.
 * @author vick08bv
 */
public abstract class FSA {
    
    protected final int id;
    protected int idStates;
    protected String name;
    protected Alphabet alphabet;
    
    
    protected FSA(int id, String name){
    
        this.id = id;
        this.idStates = 1;
        this.name = name;
        this.alphabet = new Alphabet();
        
    }
    
    
    protected FSA(int id, String name, Alphabet alphabet){
    
        this.id = id;
        this.idStates = 1;
        this.name = name;
        this.alphabet = alphabet;
        
    }
    
    
    public int id(){    
        return this.id;
    }
    
    
    public String name(){    
        return this.name;
    }
    
    
    public boolean containsCharacter(Character character){
    
        return this.alphabet.containsCharacter(character);
    
    }
    
    
    public HashSet<Character> alphabet(){
    
        return this.alphabet.alphabet();
    
    }
    
    
    public abstract int setInitialState(int id);
    
    public abstract int numberStates();
    
    public abstract boolean addToAlphabet(Character character);
    
    public abstract boolean removeFromAlphabet(Character character);
    
    public abstract void clearAlphabet();
    
    public abstract void addToAlphabet(CharSequence characters);
    
    public abstract void changeAlphabet(CharSequence characters);
    
    public abstract void addState(String name);
    
    public abstract void deleteState(int id);
    
    public abstract void readCharacter(Character character); 
    
    public abstract void readString(CharSequence string);
    
    public abstract boolean accepts();
    
    public abstract boolean accepts(CharSequence string);
            
}