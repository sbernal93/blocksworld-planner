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
		for(Predicate predicate : predicates) {
			if (otherState.getPredicates().stream().filter(p -> p.getName().equals(predicate.getName()) &&
					p.getVariables().equals(predicate.getVariables())).count() <= 0) {
						return false;
					}
		}
		return true;
	}
	

}
