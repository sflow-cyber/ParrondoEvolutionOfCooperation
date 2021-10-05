package ncs;

public class IntPair implements NumbPair {
	
	private Integer a;
	private Integer b;
	
	public IntPair(int a, int b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public Integer getA(){
		return a;
	}
	
	@Override
	public Integer getB() {
		return b;
	}
	
	public boolean equ(int x, int y) {
		return (a == x && b == y) || 
				(a == y && b == x);
	}

}
