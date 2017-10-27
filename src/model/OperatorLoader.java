package model;

import java.util.ArrayList;
import java.util.List;

public class OperatorLoader {
	
	public static List<Operator> loadOperators(){
		List<Operator> operators = new ArrayList<>();
		
		operators.add(loadStack("X", "Y"));
		operators.add(loadUnStack("X", "Y"));
		operators.add(loadUnStackLeft("X", "Y"));
		operators.add(loadPickUp("X"));
		operators.add(loadPickUpLeft("X"));
		operators.add(loadLeave("X"));
		return operators;
	}
	
	public static Operator loadStack(String x, String y) {
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		
		Block blockY = new Block(y);
		Block blockX = new Block(x);

		blockList.add(blockY);
		preconditions.add(new Predicate(PredicateName.CLEAR, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.HOLDING, blockList));
		delList.add(new Predicate(PredicateName.HOLDING, blockList));
		delList.add(new Predicate(PredicateName.CLEAR, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockY);
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.HEAVIER, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		blockList.add(blockY);
		addList.add(new Predicate(PredicateName.ON, blockList));
		
		addList.add(new Predicate(PredicateName.EMPTY_ARM, null));
		
		return new Operator(OperatorName.STACK, preconditions, addList, delList, 2);
		
	}
	
	private static Operator loadBasicUnstack(String x, String y){ 
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();

		Block blockX = new Block(x);
		Block blockY = new Block(y);
		
		preconditions.add(new Predicate(PredicateName.EMPTY_ARM, null));
		delList.add(new Predicate(PredicateName.EMPTY_ARM, null));

		blockList.add(blockY);
		addList.add(new Predicate(PredicateName.CLEAR, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.CLEAR, blockList));
		addList.add(new Predicate(PredicateName.ON_TABLE, blockList));
		addList.add(new Predicate(PredicateName.HOLDING, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		blockList.add(blockY);
		preconditions.add(new Predicate(PredicateName.ON, blockList));
		
		
		return new Operator(OperatorName.UNSTACK, preconditions, addList, delList, 2);
	}
	
	public static Operator loadUnStack(String x, String y) {
		return loadBasicUnstack(x,y);
	}
	
	
	public static Operator loadUnStackLeft(String x, String y) {
		Operator operator = loadBasicUnstack(x,y);
		operator.setName(OperatorName.UNSTACK_LEFT);
		
		List<Block> blockList = new ArrayList<>();
		Block blockX = new Block(x);
		blockList.add(blockX);
		operator.addPredicateToPreconditions(new Predicate(PredicateName.LIGHT_BLOCK,blockList));
		return operator;
	}
	
	private static Operator loadBasicPickUp(String x){
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		
		Block blockX = new Block(x);
	
		preconditions.add(new Predicate(PredicateName.EMPTY_ARM, null));

		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.CLEAR, blockList));
		preconditions.add(new Predicate(PredicateName.ON_TABLE, blockList));
		addList.add(new Predicate(PredicateName.HOLDING, blockList));
		delList.add(new Predicate(PredicateName.ON_TABLE, blockList));
		
		return new Operator(OperatorName.PICK_UP, preconditions, addList, delList, 1);
	}
	
	public static Operator loadPickUp(String x) {
		return loadBasicPickUp(x);
	}
	
	public static Operator loadPickUpLeft(String x){
		Operator operator = loadBasicPickUp(x);
		operator.setName(OperatorName.PICKUP_LEFT);
		
		List<Block> blockList = new ArrayList<>();
		Block blockX = new Block("X");
		blockList.add(blockX);
		operator.addPredicateToPreconditions(new Predicate(PredicateName.LIGHT_BLOCK,blockList));
		return operator;
	}
	
	public static Operator loadLeave(String x) {
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		
		Block blockX = new Block(x);

		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.HOLDING, blockList));
		delList.add(new Predicate(PredicateName.HOLDING, blockList));
		addList.add(new Predicate(PredicateName.ON_TABLE, blockList));
		
		addList.add(new Predicate(PredicateName.EMPTY_ARM, null));
		
		return new Operator(OperatorName.LEAVE, preconditions, addList, delList, 1);
		
	}

	

}
