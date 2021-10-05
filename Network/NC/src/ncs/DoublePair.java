package ncs;

public class DoublePair implements NumbPair {
	
	private Double a;
	private Double b;
	
	public DoublePair(double a, double b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public Number getA() {
		return this.a;
	}

	@Override
	public Number getB() {
		return this.b;
	}

}
