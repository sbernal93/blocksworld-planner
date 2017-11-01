package model;
import java.util.List;

public class Predicate {
	
	private PredicateName name;//ON, ON-Table, CLEAR
	private List<Block> blocks;//Blocks: A, B, C, D...
	private int col; //for used columns
	private ArmType arm; //for empty arm
	
	public Predicate(PredicateName name, List<Block>  blocks) {
		super();
		this.name = name;
		this.blocks = blocks;
	}
	
	public Predicate(PredicateName name, List<Block> blocks, ArmType arm) {
		super();
		this.name = name;
		this.blocks = blocks;
		this.arm = arm;
	}
	public Predicate(PredicateName name, ArmType arm) {
		super();
		this.name = name;
		this.arm = arm;
	}
	public Predicate(PredicateName name, int col) {
		super();
		this.name = name;
		this.col = col;
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

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public ArmType getArm() {
		return arm;
	}

	public void setArm(ArmType arm) {
		this.arm = arm;
	}

	@Override
	public String toString() {
		if(this.name.equals(PredicateName.HOLDING)) {
			return "Predicate [" + name+ "(" + blocks+ ", " + this.arm.name() + ")]";
		} 
		if(this.name.equals(PredicateName.EMPTY_ARM)) {
			return "Predicate [" + name+ "(" + this.arm.name() + ")]";
		}
		if(this.name.equals(PredicateName.USED_COLS_NUM)) {
			return "Predicate [" + name+ "(" + col + ")]";
		}
		return "Predicate [" + name+ "(" + blocks+ ")]";
	}
	
	public boolean equalPredicate(Predicate otherPredicate) {
		//TODO: return otherPredicate.equal(this)?
		if(this.name.equals(PredicateName.HOLDING)) {
			return (otherPredicate.getName().equals(this.name) &&
					otherPredicate.getVariables().get(0).equals(this.getVariables().get(0))
					&& this.arm.equals(otherPredicate.getArm()));
		} else if(this.blocks != null && this.blocks.size()>0) {
			if(otherPredicate.getVariables() == null || otherPredicate.getVariables().size()!=this.blocks.size()) {
				return false;
			}
			if(this.blocks.size() == 2) {
				return (otherPredicate.getName().equals(this.name) &&
						otherPredicate.getVariables().get(0).equals(this.getVariables().get(0))
						&& otherPredicate.getVariables().get(1).equals(this.getVariables().get(1)));
			}
			return (otherPredicate.getName().equals(this.name) &&
					otherPredicate.getVariables().get(0).equals(this.getVariables().get(0)));
		} else if (this.arm != null) {
			return (this.arm.equals(otherPredicate.getArm()));
		} else {
			return (this.col == otherPredicate.col);
		}

	}

	@Override
	public boolean equals(Object obj) {
		return equalPredicate((Predicate) obj);
	}

	
}
