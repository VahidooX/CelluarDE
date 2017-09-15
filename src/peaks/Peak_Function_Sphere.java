package peaks;



public class Peak_Function_Sphere implements Peak_Function {

	public double calculate(double[] gen, int peak_number) {

		
		int j;
		double dummy;

		dummy =	(gen[0] - movpeaks.peak[peak_number][0]) * (gen[0] - movpeaks.peak[peak_number][0]);
		for (j = 1; j < movpeaks.geno_size; j++)
			dummy += (gen[j] - movpeaks.peak[peak_number][j])
				* (gen[j] - movpeaks.peak[peak_number][j]);

		return movpeaks.peak[peak_number][movpeaks.geno_size + 1] - dummy;

	}

}
