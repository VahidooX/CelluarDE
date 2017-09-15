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


public class movmain {

	private movpeaks FktnLib = new movpeaks();

	/* Help Text */
	private static void help_arg() {

		System.out.println("-e<func> : sets type of peak-function (cone, hilly, twin, 1)\n");
		System.out.println("-b<func> : sets basis-function (const, off)\n");
		System.out.println("-Y<double> : sets correlation value lambda [0..1]\n");

	} // help_arg

	private void handle(String arg) {

		double newdouble;
		String doubleinput;

		if (arg.charAt(0) == '-') {

			switch (arg.charAt(1)) {

				case 'e' :

					if (arg.equals("-econe")) {

						this.FktnLib.pf = new Peak_Function_Cone();
						System.gc();

					}
					else {

						if (arg.equals("-ehilly")) {

							this.FktnLib.pf = new Peak_Function_Hilly();
							System.gc();

						} else {
							
							if (arg.equals("-etwin")) {

								this.FktnLib.pf = new Peak_Function_Twin();
								System.gc();

							} else {	
							
								if (arg.equals("-e1")) {
									this.FktnLib.pf = new Peak_Function1();
									System.gc();
								}
							}
							
						}
					}
					break;

				case 'b' :

					if (arg.equals("-bconst")) {

						this.FktnLib.bf = new Constant_Basis_Function();
						System.gc();

					}
					else {

						if (arg.equals("-boff")) {

							this.FktnLib.use_basis_function = false;

						}
					}
					break;

				case 'Y' :

					try {

						doubleinput = arg.substring(2);
						newdouble = Double.parseDouble(doubleinput);

						if (newdouble < -0.05 || newdouble > 1.05) {
							System.out.println(
								"PGA: The lambda must be between 0 and 1.\n");
							System.exit(1);
						}
						else {

							FktnLib.lambda = newdouble;
						}

					} //try

					catch (Exception e) {

						System.out.println(
							"An error occured while reading in the lambda.\n");
						System.exit(1);

					}

					break;

				case 'h' :

					help_arg();
					System.exit(0);
					break;

				default :

					System.out.println("Unknowm arguments!\n");
					System.exit(0);
					break;

			} // switch

		} //if	

	} //handle

	private void run(String[] arg) {

		int i;
		double dummy;
		/* just an arbitrary individual to test functionality */
		double[] genotype = { 51.0, 30.0, 94.0, 70.0, 49.0 };
		//  { 5.0, 10.0, 12.0, 15.5, 2.3};
		int n;

		for (n = 0; n < arg.length; n++) {

			this.handle(arg[n]);

		} // for

		/* initialize peaks at beginning */
		this.FktnLib.init_peaks();

		/* evaluation of an individual */
		dummy = this.FktnLib.eval_movpeaks(genotype);
		System.out.println(dummy + "\n");

		/* change the peaks */
		for (i = 0; i < 50; i++) {

			this.FktnLib.change_peaks();

		}

		dummy = FktnLib.eval_movpeaks(genotype);
		System.out.println(dummy + "\n");

		/* free memory space at end of program */
		FktnLib.free_peaks();

	}

	public static void main(String[] args) {

		movmain instance = new movmain();

		instance.run(args);

	} // main

} //movmain
