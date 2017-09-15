package peaks;

/**
 * Moving Peaks Function --- 10/99 
 * 
 * Copyright (C) 1999 Juergen Branke.
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License.
 *
 * This module is an example of how to use the Moving Peaks Evaluation 
 * Function, a dynamic benchmark problem changing over time.
 * 
 * 
 */

public class Five_Peaks_Basis_Function implements Basis_Function {

	public double calculate(double[] gen) {

		int i, j;
		double maximum = -100000.0, dummy;
		double[][] basis_peak = { { 8.0, 64.0, 67.0, 55.0, 4.0, 0.1, 50.0 }, {
				50.0, 13.0, 76.0, 15.0, 7.0, 0.1, 50.0 }, {
				9.0, 19.0, 27.0, 67.0, 24.0, 0.1, 50.0 }, {
				66.0, 87.0, 65.0, 19.0, 43.0, 0.1, 50.0 }, {
				76.0, 32.0, 43.0, 54.0, 65.0, 0.1, 50.0 }, };
		for (i = 0; i < 5; i++) {
			dummy = (gen[0] - basis_peak[i][0]) * (gen[0] - basis_peak[i][0]);
			for (j = 1; j < movpeaks.geno_size; j++)
				dummy += (gen[j] - basis_peak[i][j]) * (gen[j] - basis_peak[i][j]);
			dummy =
				basis_peak[i][movpeaks.geno_size
					+ 1]
					- (basis_peak[i][movpeaks.geno_size] * dummy);
			if (dummy > maximum)
				maximum = dummy;
		}
		return maximum;

	}

}
