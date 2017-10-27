package model;

import java.util.List;

public class State {
	
	private List<Predicate> predicates;
	private Operator operatorUsedToReachState;

	public State() {
		super();
	}

	public State(List<Predicate> predicates) {
		super();
		this.predicates = predicates;
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
