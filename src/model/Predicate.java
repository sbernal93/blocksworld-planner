package model;
import java.util.List;

public class Predicate {
	
	private PredicateName name;//ON, ON-Table, CLEAR
	private List<Block> blocks;//Blocks: A, B, C, D...
	
	public Predicate(PredicateName name, List<Block>  blocks) {
		super();
		this.name = name;
		this.blocks = blocks;
	}

	public PredicateName getName() {
		return name;
	}

	public void setName(PredicateName name) {
		this.name = name;
	}

	public List<Block> getVariables() {
		return blocks;
	}

	public void setVariables(List<Block> blocks) {
		this.blocks = blocks;
	}

	@Override
	public String toString() {
		return "Predicate [" + name+ "(" + blocks+ ")]";
	}
	
	public boolean equalPredicate(Predicate otherPredicate) {
		if(otherPredicate.getName().equals(this.name) &&
				otherPredicate.getVariables().get(0).getName().equals(this.getVariables().get(0).getName())){
			return true;
		}
		return false;
	}

}
