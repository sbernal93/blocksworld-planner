package model;
import java.util.ArrayList;
import java.util.List;

public class Operator {
	
	private OperatorName name;
	private List<Predicate> preconditions;
	private List<Predicate> addList;
	private List<Predicate> delList;
	private int paramAmount;
	
	public Operator() {
		super();
	}

	public Operator(OperatorName name, List<Predicate> preconditions, List<Predicate> addList,
			List<Predicate> delList, int paramAmount) {
		super();
		this.name = name;
		this.preconditions = preconditions;
		this.addList = addList;
		this.delList = delList;
		this.paramAmount = paramAmount;
	}

	public OperatorName getName() {
		return name;
	}

	public void setName(OperatorName name) {
		this.name = name;
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
	
	
	public int getParamAmount() {
		return paramAmount;
	}

	public void setParamAmount(int paramAmount) {
		this.paramAmount = paramAmount;
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
}
