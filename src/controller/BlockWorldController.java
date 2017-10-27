package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Block;
import model.InputLoader;
import model.Operator;
import model.OperatorLoader;
import model.Plan;
import model.Planner;
import model.Predicate;
import model.State;

public class BlockWorldController {

	private List<Block> blocks;
	private int maxColumns;
	private State initialState;
	private State goalState;
	private State currentState;
	private List<Operator> operators;
	private int usedColumns;
	private int numOfSteps;
	private Planner planner;
	
	@SuppressWarnings("unchecked")
	public void loadBlockWorld(){
		operators = OperatorLoader.loadOperators();
		Map<String, Object> inputMap = InputLoader.loadInput();
		blocks = (List<Block>) inputMap.get("Blocks");
		maxColumns = (int) inputMap.get("MaxColumns");
		initialState = (State) inputMap.get("InitialState");
		goalState =  (State) inputMap.get("GoalState");
	}
	
	
	//TODO: test 
	public void start(){
		planner = new Planner();
		List< State> newPossibleStates = calculateNewStates(goalState);
		for(State posibleState : newPossibleStates) {
			Plan plan = new Plan(goalState);
			plan.addState(posibleState);
			planner.addPlan(plan);
		}
		for(Plan plan : planner.getPlans()) {
			while(!plan.isCompletedPlan()) {
				//TODO: validate that plan changes inside method
				planner = calculatePlan(planner, plan);
			}
		}
		//at the end of this loop the planner object should have all the possible plans
		//that where created, including failed ones and valid ones
	}
	
	/**
	 * Planner is returned because new plans can result from calculating a plan
	 * this method calculate the next possible states that can be the outcome of a plan
	 * by applying diferent operators.
	 * @param planner
	 * @param plan
	 * @return
	 */
	private Planner calculatePlan(Planner planner, Plan plan) {
		//the last state of the plan is used to validate if we finished
		//or to determine where to continue
		State finalStateInList = plan.getStates().get(plan.getStates().size()-1);
		if(finalStateInList.areEqualStates(initialState)) {
			plan.setCompletedPlan(true);
			plan.setValidPlan(true);
		}
		if(isPosibleState(finalStateInList)) {
			List< State> newPossibleStates = calculateNewStates(goalState);
			boolean firstIt = true;
			for(State possibleState : newPossibleStates) {
				if(firstIt) {
					//the current plan is the first to change, then we generate new plans that may be possible
					plan.addState(possibleState);
					firstIt = false;
				} else {
					//a new plan is generated exactly as the one we are calculating but
					//with a new state resulting from a different operator
					//this new plan is added to the planner to be processed later
					//this plan may not be possible
					Plan newPlan = plan.makeCopyPlan();
					newPlan.addState(possibleState);
					planner.addPlan(newPlan);
				}
			}
		} else {
			//the plan is invalid since we reached an impossible state
			//the plan is completed because we cannot continue this path
			plan.setValidPlan(false);
			plan.setCompletedPlan(true);
		}
		return planner;
	}
	
	
	private boolean isPosibleState(State state) {
		return false;
	}
	
	private List< State> calculateNewStates(State currentState) {
		List<Operator> operatorsFound = new ArrayList<>();
		Map<Predicate, List<Operator>> operatorsFoundForPredicate = new HashMap<>();
		for (Predicate predicate : currentState.getPredicates()) {
			operatorsFound = getOperatorsThatHasPredicateInAddList(predicate);
			operatorsFoundForPredicate.put(predicate, operatorsFound);
		}
		List< State> stateList = new ArrayList<>();
		for(Map.Entry<Predicate, List<Operator>> entry : operatorsFoundForPredicate.entrySet()) {
			for(Operator operator : entry.getValue()) {
				stateList.add(stateFoundAfterOperator(currentState, operator, entry.getKey()));
			}
		}
		/*for(Operator operator : operatorsFound) {
			stateList.add(stateFoundAfterOperator(currentState, operator));
		}*/
		return stateList;
	}
	
	private List<Operator> getOperatorsThatHasPredicateInAddList(Predicate predicate) {
		List<Operator> operatorList = new ArrayList<>();
		for(Operator operator : operators) {
			if(operator.getAddList().stream().anyMatch(p -> p.getName().equals(predicate.getName()))) {
				operatorList.add(operator);
			}
		}
		return operatorList;
	}
	
	/**
	 * This method applies the operator to the state, even if the output is an impossible state,
	 * it will be validated later
	 * @param state
	 * @param operator
	 * @return
	 */
	private State stateFoundAfterOperator(State state, Operator operator, Predicate predicate) {
		State stateFound = new State();
		stateFound.setOperatorUsedToReachState(operator);
		//TODO: apply operator
		List<Predicate> predicates = state.getPredicates();
		List<Predicate> preconditionsToAdd = new ArrayList<>();
		/*for(Predicate precondition : operator.getPreconditions()) {
			List<Block> blocks = new ArrayList<>();
			if(precondition.getVariables().size() == predicate.getVariables().size()) {
				blocks = predicate.getVariables();
			} else {
				
			}

		}*/
		return state;
	}
	
}
