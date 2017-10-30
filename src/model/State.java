package model;

import java.util.List;

public class State {
	
	private List<Predicate> predicates;
	private Operator operatorUsedToReachState;
	private boolean isValid;
	private String reasonForInvalidState;

	public State() {
		super();
		this.isValid = true;
	}

	public State(List<Predicate> predicates) {
		super();
		this.predicates = predicates;
		this.isValid = true;
	}

	public List<Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
	}

	public Operator getOperatorUsedToReachState() {
		return operatorUsedToReachState;
	}

	public void setOperatorUsedToReachState(Operator operatorUsedToReachState) {
		this.operatorUsedToReachState = operatorUsedToReachState;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getReasonForInvalidState() {
		return reasonForInvalidState;
	}

	public void setReasonForInvalidState(String reasonForInvalidState) {
		this.reasonForInvalidState = reasonForInvalidState;
	}

	public boolean areEqualStates(State otherState) {
		boolean predicateFound = false;
		for(Predicate predicate : predicates) {
			for(Predicate otherPredicate : otherState.getPredicates()) {
				if(predicate.equalPredicate(otherPredicate)){
					predicateFound = true;
				}
			}
			if(!predicateFound) {
				return false;
			}
			predicateFound = false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "State [predicates=" + predicates + ", operatorUsedToReachState=" + operatorUsedToReachState
				+ ", isValid=" + isValid + ", reasonForInvalidState=" + reasonForInvalidState + "]";
	}
	
	
}
