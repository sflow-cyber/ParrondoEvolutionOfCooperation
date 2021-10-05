package ncs;

public class AgentPair {
	private Agent a;
	private Agent b;

	public AgentPair(Agent a, Agent b) {
		this.a = a;
		this.b = b;
	}

	/* are the two agents of this pair identical to
	 * agent x and agent y?
	 * */
	public boolean equ(Agent x, Agent y) {
		if(a == null || b == null || x == null || y == null)
			return false;
		return (a == x && b == y) || (b == x && a == y); 
	}
}
