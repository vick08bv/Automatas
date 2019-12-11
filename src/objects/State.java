package objects;


import java.util.Objects;


/**
 * Template for state.
 * @author vick08bv
 */
public abstract class State {
    
    
    protected int id;
    protected String name;
    
    
    public State(int id, String nombre){
        
        this.id= id;
        this.name = nombre;

    }
    
    
    public int getId(){    
        return this.id;       
    }
    
    
    public String getName(){    
        return this.name;   
    }
    
    
    public void setName(String name){    
        this.name = name;   
    }
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.id;
        return hash;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final State other = (State) obj;
        return Objects.equals(this.id, other.id);
    }
    
    
}