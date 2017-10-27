package model;

public class Arm {

	private ArmType armType;
	private int maxWeight;

	public Arm(ArmType armType, int maxWeight) {
		super();
		this.armType = armType;
		this.maxWeight = maxWeight;
	}

	public int getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}

	public ArmType getArmType() {
		return armType;
	}

	public void setArmType(ArmType armType) {
		this.armType = armType;
	}
	
	
	
}
