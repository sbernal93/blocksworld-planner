package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.ArmType;
import model.Block;
import model.InputLoader;
import model.Operator;
import model.OperatorLoader;
import model.OperatorName;
import model.OutputLoader;
import model.Plan;
import model.Planner;
import model.Predicate;
import model.PredicateName;
import model.State;


public class BlockWorldController {

	private List<Block> blocks;
	private int maxColumns;
	private State initialState;
	private State goalState;
	private List<Operator> operators;
	private Planner planner;
	private List<Plan> auxPlans;
	private List<Plan> failedPlans;
	
	@SuppressWarnings("unchecked")
	public void loadBlockWorld(String filepath){
		operators = OperatorLoader.loadOperators();
		Map<String, Object> inputMap = InputLoader.loadInput(filepath);
		blocks = (List<Block>) inputMap.get("Blocks");
		maxColumns = (int) inputMap.get("MaxColumns");
		initialState = (State) inputMap.get("InitialState");
		goalState =  (State) inputMap.get("GoalState");
		failedPlans = new ArrayList<>();
	}
	
	
	//TODO: test 
	public void start(){
		System.out.println("Starting planner");
		if(isPossibleState(goalState, null, true) && isPossibleState(initialState, null, true)) {
			System.out.println("Goal state is valid, calculating");
			planner = new Planner();
			List< State> newPossibleStates = calculateNewStates(goalState);
			newPossibleStates = removeRepeatedOperators(newPossibleStates);
			for(State posibleState : newPossibleStates) {
				if(isPossibleState(posibleState, null, false)) {
					Plan plan = new Plan(goalState);
					plan.addState(posibleState);
					planner.addPlan(plan);
				}
			}
			auxPlans = new ArrayList<Plan>();
			int count = 0;
			cyclePlanner(count);
			//at the end of this loop the planner object should have all the possible plans
			//that where created, including failed ones and valid ones
			OutputLoader.generateOutput(planner);
		} else {
			System.out.println("Goal state or initial state are invalid");
		}
	}
	
	/**
	 * Recursive method since java doesnt allow us to dynamically change a list,
	 * this method cycles the plans stored in the planner and any new plans are
	 * added to an aux list, if any new plans are added to the aux list, they are added
	 * to the planner, the aux list is reset, and the method is called again.
	 * 
	 */
	private void cyclePlanner(int count) {
		boolean finished = false;
		for(Plan plan : planner.getPlans()) {
		/*for (currentIterator = planner.getPlans().iterator(); currentIterator.hasNext(); ) {
			Plan plan = currentIterator.next();*/
			//while(!plan.isCompletedPlan()) {
				calculatePlan(plan);
			//}
			if(plan.isValidPlan() && plan.isCompletedPlan()) {
				//We found a completed plan, no need to keep looking (not sure about this, needs to be tested)
				planner.setCompletedPlan(plan);
				finished = true;
				break;
			}
		}
		if(auxPlans.size()>0 && !finished) {
			planner.setPlans(auxPlans.stream().filter(p -> p.isValidPlan() == true).collect(Collectors.toList()));
			auxPlans = new ArrayList<>();
			if(count>60) {
				System.out.println("Too many iterations check initialState and goalState, validate Predicates like Ligth-block"
						+ " and heavier are set");
				return;
			}
			count++;
			cyclePlanner(count);
			return;
		}
		if(auxPlans.size() == 0)
			return;
	}
	
	/**
	 *
	 * this method calculates the next possible states that can be the outcome of a plan
	 * by applying diferent operators.
	 * @param planner
	 * @param plan
	 * @return
	 */
	private void calculatePlan( Plan plan) {
		//the last state of the plan is used to validate if we finished
		//or to determine where to continue
		State finalStateInList = plan.getStates().get(plan.getStates().size()-1);
		//if the state is equal to the initial state this plan is finished
		if(finalStateInList.areEqualStates(initialState)) {
			plan.setCompletedPlan(true);
			plan.setValidPlan(true);
		} else {
			if(isPossibleState(finalStateInList, plan, false)) {
				//List< State> newPossibleStates = calculateNewStates(goalState);
				List< State> newPossibleStates = calculateNewStates(finalStateInList);
				newPossibleStates = removeRepeatedOperators(newPossibleStates);
				boolean firstIt = true;
				for(State possibleState : newPossibleStates) {
					if(isPossibleState(possibleState, plan, false)) {  
						if(firstIt) {
							//the current plan is the first to change, then we generate new plans that may be possible
							/*if(possibleState.areEqualStates(initialState)) {
								plan.setCompletedPlan(true);
							}*/
							plan.addState(possibleState);
							firstIt = false;
							/*if(plan.getStates().size()>6) {
								System.out.println("epa");
							}*/
							if(possibleState.areEqualStates(initialState)) {
								plan.setCompletedPlan(true);
								plan.setValidPlan(true);
							}
							auxPlans.add(plan);
						} else {
							//a new plan is generated exactly as the one we are calculating but
							//with a new state resulting from a different operator
							//this new plan is added to the planner to be processed later
							//this plan may not be possible
							Plan newPlan = plan.makeCopyPlan();
							newPlan.addState(possibleState);
							//planner.addPlan(newPlan);
							/*if(possibleState.areEqualStates(initialState)) {
								newPlan.setCompletedPlan(true);
							}*/
							if(possibleState.areEqualStates(initialState)) {
								newPlan.setCompletedPlan(true);
								newPlan.setValidPlan(true);
							}
							auxPlans.add(newPlan);
						}
					} else {
						Plan newPlan = plan.makeCopyPlan();
						newPlan.addState(possibleState);
						failedPlans.add(newPlan);
					}
				}
				if(firstIt) {
					plan.setValidPlan(false);
					plan.setCompletedPlan(true);
				}
			} else {
				//the plan is invalid since we reached an impossible state
				//the plan is completed because we cannot continue this path
				plan.setValidPlan(false);
				plan.setCompletedPlan(true);
			}
		}
		if(!finalStateInList.isValid()) {
			plan.setValidPlan(false);
			plan.setCompletedPlan(true);
		}
	}
	
	private List<State> removeRepeatedOperators(List<State> states) {
		List<State> newList = new ArrayList<>();
		
		for(State s : states) {
			boolean found = false;
			for(State s2 : newList) {
				if(s2.getOperatorUsedToReachState().areEqualOperators(s.getOperatorUsedToReachState())) {
					found = true;
				}
			}
			if(!found) {
				newList.add(s);
			}
		}
		return newList;
	/*	int count = 0;
		for(State s : states) {
			if(s.getOperatorUsedToReachState().areEqualOperators(state.getOperatorUsedToReachState())) {
				count++;
			}
			if(count == 2) {
				return true;
			}
		}
		/*if(states.stream().filter(s -> s.getOperatorUsedToReachState().equals(state.getOperatorUsedToReachState())).count() > 1) {
			return true;
		}*/
		//return false;*/
	}
	
	
	/**
	 * Checks if a state is valid.. cases where it can be invalid:
	 * 	1.- two contradicting predicates (Hold and On-table, on(a,b) and on(b,a), etc
	 * 	2.- max columns allowed passed
	 * 	3.- if its equal to goal state
	 *  4.- if it is equal to any previous state
	 *  5.- Using an invalid arm for picking up block (left arm for heavy blocks)
	 *  6.- Putting a heavier block on top of a lighter one
	 * @param state
	 * @return
	 */
	private boolean isPossibleState(State state, Plan plan, boolean onlyPred) {
		//if plan is null, where only checking the states predicates
		if(onlyPred) {
			return !hasContradictingPredicates(state);
		}
		//some states can already have isValid set to false previously
		if(state.isValid() && !hasContradictingPredicates(state) && !isEqualToAnyPreviousState(state, plan)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if any of the predicates are contradicting, sets the state isValid to false
	 * and sets the reason for the invalid state
	 * @param state
	 * @return
	 */
	private boolean hasContradictingPredicates(State state) {
		boolean hasContradictions = false;
		for(Predicate predicate : state.getPredicates()) {
			switch(predicate.getName()) {
			/**
			 * CLEAR(x) is invalid if: ON(y, x) or HOLDING(X) exists
			 */
			case CLEAR:
				for (Predicate p: state.getPredicates()) {
					if(!p.equalPredicate(predicate)) {
						if((p.getName().equals(PredicateName.ON) &&
								p.getVariables().get(1).equals(predicate.getVariables().get(0))) || 
						(p.getName().equals(PredicateName.HOLDING) && 
								p.getVariables().get(0).equals(predicate.getVariables()))) {
							hasContradictions = true;
							state.setValid(false);
							state.setReasonForInvalidState("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							//System.out.println("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							break;
						}
					}
				}
				break;
			/**
			 * EMPTY_ARM(a) is invalid if: Holding(a) exists
			 */
			case EMPTY_ARM:
				for (Predicate p: state.getPredicates()) {
					if(!p.equalPredicate(predicate)) {
						if(p.getName().equals(PredicateName.HOLDING) && 
								p.getArm().equals(predicate.getArm())) {
							hasContradictions = true;
							state.setValid(false);
							state.setReasonForInvalidState("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							//System.out.println("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							break;
						}
					}
				}
				break;
			/**
			 * HEAVIER(x,y) is invalid if: ON(X,Y) exists
			 */
			case HEAVIER:
				for (Predicate p: state.getPredicates()) {
					if(!p.equalPredicate(predicate)) {
						if(p.getName().equals(PredicateName.ON) && 
								p.getVariables().get(0).equals(predicate.getVariables().get(0)) &&
										p.getVariables().get(1).equals(predicate.getVariables().get(1))) {
							hasContradictions = true;
							state.setValid(false);
							state.setReasonForInvalidState("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							//System.out.println("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							break;
						}
					}
				}
				break;
			/**
			 * HOLDING(x,a) is invalid if: CLEAR(X), ON(X,Y), ON(Y,X), ON_TABLE(X) EMPTY_ARM(A) exists
			 */
			case HOLDING:
				if(predicate.getArm().equals(ArmType.LEFT) && predicate.getVariables().get(0).getWeight()!=1) {
					hasContradictions = true;
					state.setValid(false);
					state.setReasonForInvalidState("Cannot hold block with left arm");
					break;
				}
				for (Predicate p: state.getPredicates()) {
					if(!p.equalPredicate(predicate)) {
						if((p.getName().equals(PredicateName.ON) && (
								p.getVariables().get(0).equals(predicate.getVariables().get(0)) &&
										p.getVariables().get(1).equals(predicate.getVariables().get(0))))|| 
							(p.getName().equals(PredicateName.CLEAR) && 
									p.getVariables().get(0).equals(predicate.getVariables().get(0))) || 
							(p.getName().equals(PredicateName.ON_TABLE) && 
									p.getVariables().get(0).equals(predicate.getVariables().get(0))) ||
							(p.getName().equals(PredicateName.EMPTY_ARM) && 
									p.getArm().equals(predicate.getArm()))) {
							hasContradictions = true;
							state.setValid(false);
							state.setReasonForInvalidState("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							//System.out.println("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							break;
						}
					}
				}
				break;
			case LIGHT_BLOCK:
				break;
			/**
			 * ON(X,Y) is invalid if: HOLDING(X), HOLDING(Y), ON_TABLE(X), CLEAR(Y), HEAVIER(X,Y), ON(Z,Y),
			 * ON(Y,X) exists
			 */
			case ON:
				if(predicate.getVariables().get(0).getWeight()> predicate.getVariables().get(1).getWeight()) {
					hasContradictions = true;
					state.setValid(false);
					state.setReasonForInvalidState("Predicate: [" + predicate + "] impossible because of the weight of the blocks");
					break;
				}
				for (Predicate p: state.getPredicates()) {
					if(!p.equalPredicate(predicate)) {
						if((p.getName().equals(PredicateName.ON) && (
								p.getVariables().get(0).equals(predicate.getVariables().get(1)) &&
										p.getVariables().get(1).equals(predicate.getVariables().get(0))))|| 
							(p.getName().equals(PredicateName.ON) && 
								p.getVariables().get(1).equals(predicate.getVariables().get(1)))||
							(p.getName().equals(PredicateName.CLEAR) && 
									p.getVariables().get(0).equals(predicate.getVariables().get(1))) || 
							(p.getName().equals(PredicateName.ON_TABLE) && 
									p.getVariables().get(0).equals(predicate.getVariables().get(0))) ||
							(p.getName().equals(PredicateName.HOLDING) && (
									p.getVariables().get(0).equals(predicate.getVariables().get(0)) || 
									p.getVariables().get(0).equals(predicate.getVariables().get(1)))) || 
							(p.getName().equals(PredicateName.CLEAR) && 
									p.getVariables().get(0).equals(predicate.getVariables().get(1))) /*||
							(p.getName().equals(PredicateName.HEAVIER) && (
									p.getVariables().get(0).equals(predicate.getVariables().get(0)) && 
									p.getVariables().get(1).equals(predicate.getVariables().get(1))))*/ ) {
							hasContradictions = true;
							state.setValid(false);
							state.setReasonForInvalidState("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							//System.out.println("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							break;
						}
					}
				}
				break;
			/**
			 * ON_TABLE(X) is invalid if: HOLDING(X,A), ON(X,Y) or USED_COLS_NUM(m>n) exist
			 */
			case ON_TABLE:
				for (Predicate p: state.getPredicates()) {
					if(!p.equalPredicate(predicate)) {
						if((p.getName().equals(PredicateName.ON) &&
								p.getVariables().get(0).equals(predicate.getVariables().get(0))) || 
						(p.getName().equals(PredicateName.HOLDING) && 
								p.getVariables().get(0).equals(predicate.getVariables().get(0))) ||
						(p.getName().equals(PredicateName.USED_COLS_NUM) && 
								p.getCol() > maxColumns) ) {
							hasContradictions = true;
							state.setValid(false);
							state.setReasonForInvalidState("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							//System.out.println("Found contradicting predicates: [" + predicate + "] with: [" + p + "]");
							break;
						}
					}
				}
				break;
			/**
			 * USED_COLS_NUM is invalid if: max number of columns exists. Here it also validates that the current amount 
			 * is accurate
			 */
			case USED_COLS_NUM:
				int foundColsUsed = (int) state.getPredicates().stream().filter(p -> p.getName().equals(PredicateName.ON_TABLE)).count();
				if(predicate.getCol() != foundColsUsed) {
					predicate.setCol(foundColsUsed);
				}
				if(predicate.getCol() > maxColumns) {
					hasContradictions = true;
					state.setValid(false);
					state.setReasonForInvalidState("Max number of columns was passed");
				}
				break;
			}
			if(!state.isValid()) {
				break;
			}
		}
		return hasContradictions;
	}
	
	/**
	 * Checks if it is equal to any other state in the plan also checking the goalstate
	 * @param state
	 * @param plan
	 * @return
	 */
	private boolean isEqualToAnyPreviousState(State state, Plan plan) {
		if(!state.areEqualStates(goalState) && !state.areEqualStates(initialState)) {
			if(planner.getPlans() != null ) {
				for(Plan p : planner.getPlans()) {
					if(plan!=null){
						if(!p.equals(plan)) {
							List<State> states = p.getStates().stream().filter(s -> s.areEqualStates(state)).collect(Collectors.toList());
							if(states.size()>1) {
								 state.setReasonForInvalidState("State is equal to another previous state already found");
								 state.setValid(false);
								 return true;
							 }
						}
					} else {
					List<State> states = p.getStates().stream().filter(s -> s.areEqualStates(state)).collect(Collectors.toList());
					 if(states.size()>2) {
						 state.setReasonForInvalidState("State is equal to another previous state already found");
						 state.setValid(false);
						 return true;
					 }
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Calculates all the possible new states based on current state
	 * @param currentState
	 * @return
	 */
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
				List<State> newStates = stateFoundAfterOperator(currentState, operator, entry.getKey());
				stateList.addAll(newStates.stream().filter(s -> isPossibleState(s, null, false) && s.isValid()).collect(Collectors.toList()));
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
				if(!(predicate.getArm()!= null && predicate.getArm().equals(ArmType.LEFT) &&
						(operator.getName().equals(OperatorName.PICK_UP) || 
						operator.getName().equals(OperatorName.STACK)))){
					operatorList.add(operator);
				}
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
	private List<State> stateFoundAfterOperator(State state, Operator operator, Predicate predicate) {
		List<State> statesFound = new ArrayList<>();
		//stateFound.setOperatorUsedToReachState(operator);
		List<Predicate> predicates = new ArrayList<Predicate>(state.getPredicates());
		List<Operator> operatorsWithBlocks = getOperatorsWithBlocks(operator, predicate);
		if(operatorsWithBlocks != null) {
			for(Operator operatorWithBlocks: operatorsWithBlocks) {
				 predicates = new ArrayList<Predicate>(state.getPredicates());
				State stateFound = new State();
				//Java 8 magic, finds if any of the predicates in the del list of the operator
				//matches any of the predicates in the current state (if so this state is invalid)
				/*List<Predicate> found = new ArrayList<>();
				found = operatorWithBlocks.getDelList().parallelStream()
						 .filter( operatorDelPredicate-> state.getPredicates()
		                   .parallelStream()
		                   .anyMatch(operatorDelPredicate::equalPredicate)
						 )
						  .collect(Collectors.toList());

				/*return state.getPredicates()
									 .parallelStream()
									 .anyMatch(operatorDelPredicate::equalPredicate);*/
				//.anyMatch( statePredicate->operatorDelPredicate.equalPredicate(statePredicate) );
				/*if(found.size()>0) {
					stateFound.setValid(false);
					stateFound.setReasonForInvalidState("After applying operator: [" + operator.toString() + "] "
							+ " predicate: [" + found.get(0).toString() + "] would have been removed");
				}*/
				//Checks that the operators add list corresponds to the current state
				//removes the predicates when found, if not found then state is invalid
				for(Predicate p : operatorWithBlocks.getAddList() ) {
					boolean found = false;
					for(Predicate pr : predicates) {
						if(pr.equalPredicate(p)) {
							predicates.remove(pr);
							found = true;
							break;
						}
					}
					if(!found) {
						stateFound.setValid(false);
						stateFound.setReasonForInvalidState("For operator: [" + operatorWithBlocks.toString() + "] to be used"
								+ " State needed to have: " + p);
					}
					/*if(predicates.stream().anyMatch(pr -> p.equals(p))) {
						predicates.remove(p);
					} else {
						stateFound.setValid(false);
						stateFound.setReasonForInvalidState("For operator: [" + operator.toString() + "] to be used"
								+ " State needed to have: " + p);
					}*/
				}
				//predicates.remove(predicate);
				predicates.addAll(operatorWithBlocks.getPreconditions());
				stateFound.setOperatorUsedToReachState(operatorWithBlocks);
				stateFound.setPredicates(predicates);
				stateFound = removeRepeatedPredicates(stateFound);
				if(isPossibleState(stateFound, null, false) && stateFound.isValid()) {
					statesFound.add(stateFound);
				}
			}
			
		} else {
			State stateFound = new State();
			stateFound.setValid(false);
			stateFound.setReasonForInvalidState("Operator: " + operator + " was impossible to apply");
			stateFound.setOperatorUsedToReachState(operator);
			stateFound.setPredicates(state.getPredicates());
			//statesFound.add(stateFound);
		}
		return statesFound;
		
	}
	
	
	/**
	 * Removes any repeated predicate a state may have
	 * @param state
	 * @return
	 */
	private State removeRepeatedPredicates(State state) {
		List<Predicate> predicates = state.getPredicates().stream().distinct().collect(Collectors.toList());
		state.setPredicates(predicates);
		return state;
	}

	/**
	 * Method that gets the operator with the correct blocks assigned so it matches the predicate
	 * @param operator
	 * @param predicate
     * @return
     */
	private List<Operator> getOperatorsWithBlocks(Operator operator, Predicate predicate) {
		Predicate addPredicateFromOperator;
		List<Operator> operatorsWithBlocks = new ArrayList<>();
		Operator operatorWithBlocks = new Operator();
		ArmType armType = ArmType.RIGHT;
		if((operator.getName().equals(OperatorName.LEAVE) || operator.getName().equals(OperatorName.STACK))
				&& predicate.getArm()!=null) {
			armType = predicate.getArm();
		}
		if(predicate.getVariables() != null && (operator.getParamList().size() == predicate.getVariables().size())) {
			//Tries to find the operator so that the add list element matches the predicate blocks
			if(operator.getParamList().size() == 2) {
				operatorWithBlocks = OperatorLoader.load(operator.getName(), predicate.getVariables().get(0), predicate.getVariables().get(1), armType);
				addPredicateFromOperator = operatorWithBlocks.getAddList().stream().filter(p -> p.getName().equals(predicate.getName())).findAny().orElse(null);
				if(!addPredicateFromOperator.equalPredicate(predicate)) {
					//addPredicateFromOperator = operatorWithBlocks.getAddList().stream().filter(p -> p.getName().equals(predicate.getName())).findAny().orElse(null);
					operatorWithBlocks = OperatorLoader.load(operator.getName(), predicate.getVariables().get(1), predicate.getVariables().get(0), armType);
				}
			} else {
					operatorWithBlocks = OperatorLoader.load(operator.getName(), predicate.getVariables().get(0), null, armType);
			}
			operatorsWithBlocks.add(operatorWithBlocks);
		} else {
			if(predicate.getVariables() == null) {
				//This is for cases like EMPTY_ARM that can hava operators like STACK(X,Y), since empty arm
				//doesnt have variables, both blocks are param
				operatorsWithBlocks = calculateValueForParam(operator, 2, armType);
			} else if(operator.getParamList().size() == 2) {
				operatorWithBlocks = OperatorLoader.load(operator.getName(), predicate.getVariables().get(0), new Block("X"), armType);
				addPredicateFromOperator = operatorWithBlocks.getAddList().stream().filter(p -> p.getName().equals(predicate.getName())).findAny().orElse(null);
				if(!addPredicateFromOperator.equalPredicate(predicate)) {
					operatorWithBlocks = OperatorLoader.load(operator.getName(), new Block("X"), predicate.getVariables().get(0), armType);
					//addPredicateFromOperator = operatorWithBlocks.getAddList().stream().filter(p -> p.getName().equals(predicate.getName())).findAny().orElse(null);
					operatorsWithBlocks = calculateValueForParam(operatorWithBlocks, 0, armType);
				} else {
					operatorsWithBlocks = calculateValueForParam(operatorWithBlocks, 1, armType);
				}
			}
		}
		if((operator.getName().equals(OperatorName.LEAVE) || operator.getName().equals(OperatorName.STACK))
				&& predicate.getArm()==null  && predicate.getVariables().get(0).getWeight()==1) {
			if(operator.getName().equals(OperatorName.STACK)) {
				if(predicate.getVariables().size() <2) {
					operatorsWithBlocks = null;
				} else {
					operatorsWithBlocks.add(OperatorLoader.load(operatorWithBlocks.getName(), predicate.getVariables().get(0),predicate.getVariables().get(1),ArmType.LEFT));
				}
			} else {
				operatorsWithBlocks.add(OperatorLoader.load(operatorWithBlocks.getName(), predicate.getVariables().get(0),null, ArmType.LEFT));
			}
		}
		return operatorsWithBlocks;
	}

	/**
	 * This method calculates all the possible values for the para, in an operator,
	 * it does not validate if the param is valid
	 * @param operator
	 * @param posOfParam
	 * @return
	 */
	private List<Operator> calculateValueForParam(Operator operator, int posOfParam, ArmType type) {
		List<Operator> possibleOperators = new ArrayList<>();
		if(posOfParam == 2) {
			for (Block block : blocks) {
				for (Block b : blocks) {
					if(!b.getName().equals(block.getName())) {
						Operator opParam = new Operator();
						opParam = OperatorLoader.load(operator.getName(), block, b, type);
						possibleOperators.add(opParam);
					}
				}
			}
		} else {
			for (Block block : blocks) {
				Operator opParam = new Operator();
				if(posOfParam == 0) {
					if(block.getName() != operator.getParamList().get(1).getName()) {
						opParam = OperatorLoader.load(operator.getName(), block, operator.getParamList().get(1), type);
						possibleOperators.add(opParam);
					}
				} else {
					if(block.getName() != operator.getParamList().get(0).getName()) {
						opParam = OperatorLoader.load(operator.getName(), operator.getParamList().get(0), block, type);
						possibleOperators.add(opParam);
					}
				}
			}	
		}
		return possibleOperators;
	}
}
