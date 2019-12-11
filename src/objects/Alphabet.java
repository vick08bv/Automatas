package objects;


import java.util.HashSet;


/**
 * Alphabet for finite state automatas.
 * @author vick08bv
 */
public class Alphabet {
    
    
    private final HashSet<Character> characters;
    
    
    /**
     * New alphabet in blank.
     */
    public Alphabet(){
    
        this.characters = new HashSet<>();
    
    }
    
    
    /**
     * New alphabet from a sequence of characters.
     * @param characters characters to add. 
     */
    public Alphabet(CharSequence characters){
    
        this.characters = new HashSet<>();
        
        for(int i = 0; i < characters.length(); i++){
        
            this.characters.add(characters.charAt(i));
        
        }

    }
    
    
    /**
     * Verifies if a character is in the alphabet.
     * @param character character.
     * @return if the alphabet contains the character.
     */
    public boolean containsCharacter(Character character){
    
        return this.characters.contains(character);
    
    }
    
    
    /**
     * Add a character to alphabet.
     * @param character character to add.
     * @return if character was added.
     */
    public boolean addCharacter(Character character){
    
        return this.characters.add(character);
    
    }
    
    
    /**
     * Removes a character from the alphabet.
     * @param character character to remove.
     * @return if the character was removed.
     */
    public boolean removeCharacter(Character character){
    
        return this.characters.remove(character);
    
    }
    
    
    /**
     * Removes all the characters in the alphabet.
     */
    public void clearAlphabet(){
    
        this.characters.clear();
    
    }
    
    
    /**
     * Returns the alphabet.
     * @return the set of characters in the alphabet.
     */
    public HashSet<Character> alphabet(){
        
        return this.characters;
    
    }
    
    
    /**
     * Size of the alphabet.
     * @return the size.
     */
    public int getSize(){
    
        return this.characters.size();
    
    }
    
    
    /**
     * Compares the alphabet with other.
     * @param alphabet other alphabet.
     * @return if the alphabet is a subset of the other.
     */
    public boolean isSubset(Alphabet alphabet){
    
        return this.characters.containsAll(alphabet.alphabet());
    
    }
    
    
    @Override
    public String toString(){
        
        String string = "";
        
        for(Character character: this.characters){
            string += " " + String.valueOf(character);
        }
        return string;
        
    }
    
    
}