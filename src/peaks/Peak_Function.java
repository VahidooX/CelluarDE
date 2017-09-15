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

public interface Peak_Function {


	public double calculate(double[] gen, int peak_number);


} //Peak_Function
