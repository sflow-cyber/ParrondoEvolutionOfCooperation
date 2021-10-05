package ncs;

public abstract class Network {
	
	public abstract void setSize(int size);
	public abstract void setDensity(double density);
	public abstract void setRo(double ro);
	public abstract void setGame(double s, double t);
	public abstract void setP(double p);
	public abstract void build();
	public abstract void populate();
	public abstract void playRound();
	public abstract void updateStrategy();
	public abstract double getCoopFrac();
	public abstract void resetSuccess();
	
	/*
	 * returns modulo according to mathematical
	 * definition
	 */
	public int modulo(int x, int modulus) {
		if(modulus <= 0)
			return -1;
		assert modulus > 0;
		return ((x%modulus)+modulus)%modulus;
	}
	
	/*
	 * method gets array with probability
	 * density function over an interval of
	 * integers going from 0 to array length
	 * minus 1 and, according to
	 * this pdf, draws a number between
	 * 0 and array length minus 1.
	 */
	public int getRndm(double[] pdf) {
		double kSum = kahanSum(pdf);
		if(kSum < 0.99 || kSum > 1.01) {
			System.out.println("Sum is not 1 but "+kSum);
			return -1;
		}
		assert kahanSum(pdf) > 0.99 && kahanSum(pdf) < 1.01;
		double rndm = Math.random();
		int counter = 0;
		double sum = 0d;
		while(counter < pdf.length) {
			sum += pdf[counter];
			if(sum > 1.01 || sum < -0.01)
				break;
			if(rndm <= sum)
				return counter;
			counter++;
		}
		System.out.println("Something went wrong "
				+ "while drawing a random number. "
				+ "Sum = " + sum);
		return -1;
	}
	
	/* returns the sum over a double array and is
	 * more accurate than the trivial sum-over- 
	 * double-array approach
	 */
	public double kahanSum(double[] x) {
		double sum = 0d;
		double c = 0d;
		for(int i = 0; i < x.length; i++) {
			double y = x[i] - c;
			double t = sum + y;
			c = (t - sum) - y;
			sum = t;
		}
		return sum;
	}
	
	/*
	 * returns the difference of two double
	 * numbers
	 */
	public double getDiff(double a, double b) {
		return Math.max(a,b) - Math.min(a,b);
	}

}
