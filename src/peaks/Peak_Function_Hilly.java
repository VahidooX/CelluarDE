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

public class Peak_Function_Hilly implements Peak_Function  {

	public double calculate(double[] gen, int peak_number)  {
		
		int j;
  		double dummy;

  		dummy =  (gen[0]-movpeaks.peak[peak_number][0])*(gen[0]-movpeaks.peak[peak_number][0]); 
  		for (j=1; j< movpeaks.geno_size; j++)
    		dummy += (gen[j]-movpeaks.peak[peak_number][j])*(gen[j]-movpeaks.peak[peak_number][j]); 
  
  		return movpeaks.peak[peak_number][movpeaks.geno_size+1]-(movpeaks.peak[peak_number][movpeaks.geno_size]*dummy)-0.01*Math.sin(20.0*dummy);	
		
		
	}

}
