package model;
import java.util.ArrayList;
import java.util.List;

public class Operator {
	
	private OperatorName name;
	private List<Predicate> preconditions;
	private List<Predicate> addList;
	private List<Predicate> delList;
	private List<Block> paramList;
	private ArmType arm;
	
	public Operator() {
		super();
	}

	public Operator(OperatorName name, List<Predicate> preconditions, List<Predicate> addList,
			List<Predicate> delList, List<Block> paramList, ArmType arm) {
		super();
		this.name = name;
		this.preconditions = preconditions;
		this.addList = addList;
		this.delList = delList;
		this.paramList = paramList;
		this.arm = arm;
	}

	public OperatorName getName() {
		return name;
	}

	public void setName(OperatorName name) {
		this.name = name;
	}
	
	public ArmType getArm() {
		return arm;
	}

	public void setArm(ArmType arm) {
		this.arm = arm;
	}

	public List<Predicate> getPreconditions() {
		return preconditions;
	}

	public void setPreconditions(List<Predicate> preconditions) {
		this.preconditions = preconditions;
	}

	public List<Predicate> getAddList() {
		return addList;
	}

	public void setAddList(List<Predicate> addList) {
		this.addList = addList;
	}
	
	public List<Predicate> getDelList() {
		return delList;
	}

	public void setDelList(List<Predicate> delList) {
		this.delList = delList;
	}

	public List<Block> getParamList() {
		return paramList;
	}

	public void setParamList(List<Block> paramList) {
		this.paramList = paramList;
	}

	public void addPredicateToAddList(Predicate predicate) {
		if(this.addList == null) {
			this.addList = new ArrayList<>();
		}
		this.addList.add(predicate);
	}
	
	public void addPredicateToDelList(Predicate predicate) {
		if(this.delList == null) {
			this.delList = new ArrayList<>();
		}
		this.delList.add(predicate);
	}
	
	public void addPredicateToPreconditions(Predicate predicate) {
		if(this.preconditions == null) {
			this.preconditions = new ArrayList<>();
		}
		this.preconditions.add(predicate);
	}

	@Override
	public String toString() {
		if(this.paramList.size() == 1) {
			return this.name + "(" + this.paramList.get(0).getName() + ", Arm: " + arm + ")";
		}
		return this.name + "(" + this.paramList.get(0).getName() + ", " + this.paramList.get(1).getName() + ", Arm: " + arm + ")";
	}

	public boolean areEqualOperators(Operator other) {
		try {
			if(other.getName().equals(this.getName()) && other.getArm().equals(this.getArm())) {
				if(this.getParamList().size() == other.getParamList().size()) {
					if(this.getParamList().size() == 1) {
						if(this.getParamList().get(0).getName().equals(other.getParamList().get(0).getName())) {
							return true;
						}
					} else {
						if(this.getParamList().get(0).getName().equals(other.getParamList().get(0).getName()) &&
								this.getParamList().get(1).getName().equals(other.getParamList().get(1).getName())) {
							return true;
						}
					}
				}
			}
		} catch(Exception e) {
			
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return areEqualOperators((Operator) obj);
	}
	
	
	
	
}
