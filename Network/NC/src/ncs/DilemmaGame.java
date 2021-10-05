package ncs;

public class DilemmaGame {
	/*
	 * sucker payoff
	 */
	private double s;	
	
	/*
	 * temptation payoff
	 */
	private double t;
	
	/*
	 * a dilemma game is entirely determined
	 * by its sucker and temptation payoff
	 */
	public DilemmaGame(double s, double t) {
		assert -1d < s && s < 1d;
		assert 0 < t && t < 2d;
		assert s < t;
		this.s = s;	// sucker
		this.t = t; // temptation
	}

	/*
	 * when two agents gamble, each of them 
	 * receives a payoff that depends on
	 * both agents' strategies... 
	 */
	public DoublePair gamble(Agent x, Agent y) {
		if(x == null || y == null)
			return null;
		assert x != null && y != null;
		if(!x.getStrategy().toString().equals("COOP")
				&& !x.getStrategy().toString().equals("DEF")) 
			return null;
		assert x.getStrategy().toString().equals("COOP") ||
			x.getStrategy().toString().equals("DEF");
		if(!y.getStrategy().toString().equals("COOP")
				&& !y.getStrategy().toString().equals("DEF")) 
			return null;
		assert y.getStrategy().toString().equals("COOP") ||
			y.getStrategy().toString().equals("DEF");
		// if both cooperate, each receives a payoff of 1
		if(x.getStrategy().toString().equals("COOP") &&
				y.getStrategy().toString().equals("COOP")) {
			return new DoublePair(1d,1d);
		}
		/*
		 *  if one agent cooperates and the other defects, 
		 *  the agent who cooperates gets the sucker payoff
		 *  and the agent who defects gets the temptation
		 *  payoff
		 */
		if(x.getStrategy().toString().equals("COOP") &&
				y.getStrategy().toString().equals("DEF")) {
			return new DoublePair(s,t);
		}
		if(x.getStrategy().toString().equals("DEF") &&
				y.getStrategy().toString().equals("COOP")) {
			return new DoublePair(t,s);
		}
		// if both agents defect, they both get 0
		if(x.getStrategy().toString().equals("DEF") &&
				y.getStrategy().toString().equals("DEF")) {
			return new DoublePair(0d,0d);
		}
		return null;
	}
	
	public double getS() {
		return this.s;
	}
	
	public double getT() {
		return this.t;
	}

}
