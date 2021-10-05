package ncs;

public class Agent {
	
		/*
		 * the agent's strategy can either be 
		 * to cooperate or to defect
		 */
		private Strategy strategy;	
		
		/*
		 * the strategy that the agent will adopt
		 * to in the next round
		 */
		private Strategy nextStrategy;
		
		/*
		 * how successful was the agent in 
		 * the last round?
		 */
		private double success; 
		
		public Agent(double p) {
			/*
			 * parameter p corresponds to the
			 * probability that the agent has
			 * a defective strategy when
			 * initialized
			 */
			if(Math.random() < p)
				strategy = Strategy.DEF;
			else
				strategy = Strategy.COOP;
		}
		
		public void setStrategy(Strategy strategy) {
			this.strategy = strategy;
		}
		
		public Strategy getStrategy() {
			return this.strategy;
		}
		
		public double getSuccess() {
			return this.success;
		}

		public void adaptSuccess(double a) {
			this.success += a;
		}
		
		public void resetSuccess() {
			this.success = 0d;
		}
		
		public void setNextStrategy(Strategy s) {
			this.nextStrategy = s;
		}
		
		public void updateStrategy() {
			this.strategy = this.nextStrategy;
			this.nextStrategy = null;
		}

}
