package model;

import java.util.ArrayList;
import java.util.List;

public class OperatorLoader {
	
	public static Operator load(OperatorName name, String x, String y, ArmType type) {
		switch (name) {
		case LEAVE:
			return loadLeave(x, type);
		case PICK_UP:
			return loadPickUp(x);
		case PICKUP_LEFT:
			return loadPickUpLeft(x);
		case STACK:
			return loadStack(x, y, type);
		case UNSTACK:
			return loadUnStack(x, y);
		case UNSTACK_LEFT:
			return loadUnStackLeft(x, y);
		}
		return null;
	}
	
	public static List<Operator> loadOperators(){
		List<Operator> operators = new ArrayList<>();
		
		operators.add(loadStack("X", "Y", ArmType.GENERIC));
		operators.add(loadUnStack("X", "Y"));
		operators.add(loadUnStackLeft("X", "Y"));
		operators.add(loadPickUp("X"));
		operators.add(loadPickUpLeft("X"));
		operators.add(loadLeave("X", ArmType.GENERIC));
		return operators;
	}
	
	public static Operator loadStack(String x, String y, ArmType type) {
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
		preconditions.add(new Predicate(PredicateName.HOLDING, blockList, type));
		delList.add(new Predicate(PredicateName.HOLDING, blockList, type));
		delList.add(new Predicate(PredicateName.CLEAR, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockY);
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.HEAVIER, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		blockList.add(blockY);
		addList.add(new Predicate(PredicateName.ON, blockList));
		
		addList.add(new Predicate(PredicateName.EMPTY_ARM, type));
		
		return new Operator(OperatorName.STACK, preconditions, addList, delList, blockList);
		
	}
	
	private static Operator loadBasicUnstack(String x, String y, ArmType type){ 
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();

		Block blockX = new Block(x);
		Block blockY = new Block(y);
		
		preconditions.add(new Predicate(PredicateName.EMPTY_ARM, type));
		delList.add(new Predicate(PredicateName.EMPTY_ARM, type));

		blockList.add(blockY);
		addList.add(new Predicate(PredicateName.CLEAR, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.CLEAR, blockList));
		addList.add(new Predicate(PredicateName.ON_TABLE, blockList));
		addList.add(new Predicate(PredicateName.HOLDING, blockList, type));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		blockList.add(blockY);
		preconditions.add(new Predicate(PredicateName.ON, blockList));
		
		
		return new Operator(OperatorName.UNSTACK, preconditions, addList, delList, blockList);
	}
	
	public static Operator loadUnStack(String x, String y) {
		return loadBasicUnstack(x,y, ArmType.RIGHT);
	}
	
	
	public static Operator loadUnStackLeft(String x, String y) {
		Operator operator = loadBasicUnstack(x,y, ArmType.LEFT);
		operator.setName(OperatorName.UNSTACK_LEFT);
		
		List<Block> blockList = new ArrayList<>();
		Block blockX = new Block(x);
		blockList.add(blockX);
		operator.addPredicateToPreconditions(new Predicate(PredicateName.LIGHT_BLOCK,blockList));
		return operator;
	}
	
	private static Operator loadBasicPickUp(String x, ArmType type){
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		
		Block blockX = new Block(x);
	
		preconditions.add(new Predicate(PredicateName.EMPTY_ARM, type));

		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.CLEAR, blockList));
		preconditions.add(new Predicate(PredicateName.ON_TABLE, blockList));
		addList.add(new Predicate(PredicateName.HOLDING, blockList, type));
		delList.add(new Predicate(PredicateName.ON_TABLE, blockList));
		
		return new Operator(OperatorName.PICK_UP, preconditions, addList, delList, blockList);
	}
	
	public static Operator loadPickUp(String x) {
		return loadBasicPickUp(x, ArmType.RIGHT);
	}
	
	public static Operator loadPickUpLeft(String x){
		Operator operator = loadBasicPickUp(x, ArmType.LEFT);
		operator.setName(OperatorName.PICKUP_LEFT);
		
		List<Block> blockList = new ArrayList<>();
		Block blockX = new Block("X");
		blockList.add(blockX);
		operator.addPredicateToPreconditions(new Predicate(PredicateName.LIGHT_BLOCK,blockList));
		return operator;
	}
	
	public static Operator loadLeave(String x, ArmType type) {
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		
		Block blockX = new Block(x);

		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.HOLDING, blockList, type));
		delList.add(new Predicate(PredicateName.HOLDING, blockList, type));
		addList.add(new Predicate(PredicateName.ON_TABLE, blockList));
		
		addList.add(new Predicate(PredicateName.EMPTY_ARM, type));
		
		return new Operator(OperatorName.LEAVE, preconditions, addList, delList, blockList);
		
	}

	

}
