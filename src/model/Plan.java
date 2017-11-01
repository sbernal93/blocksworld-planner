package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plan {

	private State initialState;
	private List<State> states;
	private boolean isValidPlan;
	private boolean isCompletedPlan;
	
	public Plan(State initialState) {
		this(initialState, new ArrayList<>(), true, false);
	}
	public Plan(State initialState, List<State> states, boolean isValidPlan, boolean isCompletedPlan) {
		super();
		this.initialState = initialState;
		this.states = states;
		this.isValidPlan = isValidPlan;
		this.isCompletedPlan = isCompletedPlan;
	}
	public State getInitialState() {
		return initialState;
	}
	public void setInitialState(State initialState) {
		this.initialState = initialState;
	}
	public List<State> getStates() {
		return states;
	}
	public void setStates(List<State> states) {
		this.states = states;
	}
	public boolean isValidPlan() {
		return isValidPlan;
	}
	public void setValidPlan(boolean isValidPlan) {
		this.isValidPlan = isValidPlan;
	}
	public boolean isCompletedPlan() {
		return isCompletedPlan;
	}
	public void setCompletedPlan(boolean isCompletedPlan) {
		this.isCompletedPlan = isCompletedPlan;
	}
	public void addState(State state) {
		if(this.states == null ){
			this.states =  new ArrayList<>();
		}
		this.states.add(state);
	}
	
	public Plan makeCopyPlan() {
		List<State> copyStateList =   new ArrayList(this.states);
		copyStateList.remove(this.states.size()-1);
		return new Plan(this.initialState, copyStateList, true, false);

	}
	@Override
	public String toString() {
		return "Plan [initialState=" + initialState + ", states=" + states + ", isValidPlan=" + isValidPlan
				+ ", isCompletedPlan=" + isCompletedPlan + "]";
	}
	
}
