package peaks;

import java.util.*;


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


public class movpeaks {

    /***** PARAMETER SETTINGS *****/

    /* 
     *  number of evaluations between changes. change_frequency
     *  =0 means that function never changes (or only if function change_peaks is called) 
     *  Scenario 1: 5000
     */
    private int change_frequency = 5000;

    /* 
     *  seed for built-in random number generator 
     */
    private long movrandseed = 1;

    /* 
     *  number of dimensions, or the number of double valued genes 
     *  Scenerio 1:  5
     */
    public static int geno_size = 5;

    /*  
     *  distance by which the peaks are moved, severity 
     *  Scenario 1: 1.0  
     */
    private double vlength = 10.0;  

    /* 
     *  severity of height changes, larger numbers mean larger severity 
     *  Scenario 1: 7.0
     */
    private double height_severity = 7.0;

    /* 
     *  severity of width changes, larger numbers mean larger severity 
     */
    private double width_severity = 0.01;

    /* 
     *  lambda determines whether there is a direction of the movement, or whether
     *   they are totally random. For lambda = 1.0 each move has the same direction,
     *   while for lambda = 0.0, each move has a random direction 
     */
    protected double lambda = 0.0;

    /* 
     *  number of peaks in the landscape 
     *  Scenario 1: 5
     */
    private int number_of_peaks = 5;

    /* 
     *  if set to 1, a static landscape (basis_function) is included in the fitness
     *  evaluation
     */
    protected boolean use_basis_function = false;

    /* saves computation time if not needed and set to 0 */
    private boolean calculate_average_error = true; //int calculate_average_error = 1;

    /* saves computation time if not needed and set to 0 */
    private boolean calculate_offline_performance = true;
    //int calculate_offline_performance = 1;

    /* saves computation time if not needed and set to 0 */
    private boolean calculate_right_peak = true; //int calculate_right_peak = 1;

    /* 
     *  minimum and maximum coordinate in each dimension 
     *  Scenario 1: 0.0 and 100.0
     */
    public double mincoordinate = 0.0;
    public double maxcoordinate = 100.0;

    /* 
     *  minimum and maximum height of the peaks          
     *  Scenario 1:  30.0 and 70.0
     */

    private double minheight = 30.0;
    private double maxheight = 70.0;

    /* 
     *  height chosen randomly when standardheight = 0.0 
     *  Scenario 1: 50.0
     */
    private double standardheight = 0.0;

    /*
     *  Scenario 1: 0.0001
     */
    private double minwidth = 0.0001;

    /*
     *  Scenario 1: 0.2
     */
    private double maxwidth = 0.2;

    /* 
     *  width chosen randomly when standardwidth = 0.0 
     *  Scenario 1:  0.1
     */
    private double standardwidth = 0.1;

    public Peak_Function pf = new Peak_Function1();  //  Scenario 1

    public Basis_Function bf = new Constant_Basis_Function();

    /***** END OF PARAMETER SECTION *****/

    //void change_peaks();   /* preliminary declaration of function change_peaks()*/
    private boolean recent_change = true; /* indicates that a change has just ocurred */
    private int current_peak; /* peak on which the current best individual is located */
    private int maximum_peak; /* number of highest peak */
    private double current_maximum; /* fitness value of currently best individual */
    private double offline_performance = 0.0;
    private double offline_error = 0.0;
    private double avg_error = 0; /* average error so far */
    private double current_error = 0; /* error of the currently best individual */
    private double global_max; /* absolute maximum in the fitness landscape */
    private int evals = 0; /* number of evaluations so far */

    /* data structure to store peak data */
    public static double[][] peak; //double * * peak; 

    private double[] shift; //double * shift;

    private double[] coordinates; //double * coordinates;

    /* which peaks are covered by the population ? */
    private int[] covered_peaks; //int * covered_peaks; 

    /* to store every peak's previous movement */
    private double[][] prev_movement; //double * * prev_movement; 

    //	/*
    //	 * two variables needed in method movnrand(). 
    //	 */
    //	static boolean backup = false;
    //	static double x2;
	
    /*
     * two variables needed in method change_stepsize_linear(). Perhaps it would be
     * appropriate to put change_stepsize_linear() in its own class.
     */
    private static int counter = 1;
    private static double frequency = 3.14159 / 20.0;
	
    private Random movrand;
    private Random movnrand;

    final int PEAKFUNCTION1 = 0;
    final int PEAKFUNCTIONCONE = 1;
    final int PEAKFUNCTIONSPHERE = 2;

    /*
     *  Constructor
     */
    public double baseF=0;
    public movpeaks(){
    }

    public movpeaks( int numberOfAttractors, 
		     int numberOfDimensions, 
		     int changeFrequency, 
		     double vlength,
		     double minwidth,
		     double maxwidth,
		     double stdwidth,
		     double lambda,
		     double heightSeverity,
		     double widthSeverity,
		     int peakType,
		     long seed){

	this.number_of_peaks = numberOfAttractors;
	geno_size = numberOfDimensions;

	this.change_frequency = changeFrequency;
	this.vlength = vlength;
	this.minwidth = minwidth;
	this.maxwidth = maxwidth;
	this.standardwidth = stdwidth;
	this.lambda = lambda;
	this.height_severity = heightSeverity;
	this.width_severity = widthSeverity;

	if ( peakType == PEAKFUNCTIONCONE )
	    pf = new Peak_Function_Cone();

	else if ( peakType == PEAKFUNCTIONSPHERE )
	    pf = new Peak_Function_Cone();

	long newMovrandseed = movrandseed + seed;
	movrand = new Random( newMovrandseed );
	movnrand = new Random( newMovrandseed );
    }
    

    /* initialize all variables at the beginning of the program */
    public void init_peaks() {
	int i, j;
	double dummy;

	shift = new double[geno_size];
	this.coordinates = new double[geno_size];
	this.covered_peaks = new int[this.number_of_peaks];
	movpeaks.peak = new double[this.number_of_peaks][];
	this.prev_movement = new double[this.number_of_peaks][];

	for (i = 0; i < this.number_of_peaks; i++) {
	    peak[i] = new double[geno_size + 2];
	    prev_movement[i] = new double[geno_size];
	}

	for (i = 0; i < this.number_of_peaks; i++)
	    for (j = 0; j < geno_size; j++) {
		peak[i][j] = 100.0 * this.movrand.nextDouble();
		this.prev_movement[i][j] = this.movrand.nextDouble() - 0.5;
	    }

	if (this.standardheight <= 0.0) {

	    for (i = 0; i < this.number_of_peaks; i++)
		peak[i][geno_size + 1] =
		    (this.maxheight - this.minheight) * this.movrand.nextDouble()
		    + this.minheight;

	}
	else {

	    for (i = 0; i < this.number_of_peaks; i++) {

		peak[i][geno_size + 1] = this.standardheight;
	    } // for
	} // else

	if (this.standardwidth <= 0.0) {

	    for (i = 0; i < this.number_of_peaks; i++) {

		peak[i][geno_size] =
		    (this.maxwidth - this.minwidth) * this.movrand.nextDouble() + this.minwidth;
	    } //for
	}
	else {

	    for (i = 0; i < this.number_of_peaks; i++) {

		peak[i][geno_size] = this.standardwidth;
	    } //for
	} // else

	if (this.calculate_average_error) {

	    this.global_max = -100000.0;

	    for (i = 0; i < this.number_of_peaks; i++) {

		for (j = 0; j < geno_size; j++) {

		    this.coordinates[j] = peak[i][j];
		} // for

		dummy = this.dummy_eval(coordinates);

		if (dummy > this.global_max)
		    this.global_max = dummy;
	    } //for
            this.global_max+=baseF;
	} //if
    } //init_peaks

    /* dummy evaluation function allows to evaluate without being counted */
    public double dummy_eval(double[] gen) {
	int i;
	double maximum = -100000.0, dummy;

	for (i = 0; i < this.number_of_peaks; i++) {
	    dummy = this.pf.calculate(gen, i);
	    if (dummy > maximum)
		maximum = dummy;
	}

	if (use_basis_function) {

	    dummy = bf.calculate(gen);
	    /* If value of basis function is higher return it */
	    if (maximum < dummy)
		maximum = dummy;
	}
        maximum=maximum;//+baseFun(gen);

	return (maximum);
    }

    public double baseFun(double[] gen){
        double ss=0;
        for (int i = 0; i < this.geno_size; i++) {
            ss=ss+Math.abs(3*Math.sin(0.1*gen[i])+2.5*Math.sin(0.3*gen[i]+Math.PI/4));//+1*Math.sin(20*gen[i]+(3*Math.PI)/2);
            //ss=ss+2*Math.sin(gen[i])+5*Math.sin(gen[i]+Math.PI);
        }
        return ss;
    }
    /* evaluation function */
    public double eval_movpeaks(double[] gen) {
	int i;
	double maximum = -100000.0, dummy;

	if ((this.change_frequency > 0)
	    && (this.evals % this.change_frequency == 0))
	    this.change_peaks();
            
	for (i = 0; i < this.number_of_peaks; i++) {
	    dummy = this.pf.calculate(gen, i);
	    if (dummy > maximum)
		maximum = dummy;
	}
        
	if (this.use_basis_function) {

	    dummy = this.bf.calculate(gen);
	    /* If value of basis function is higher return it */
	    if (maximum < dummy)
		maximum = dummy;
	}
        
        //System.out.println(maximum+":"+baseFun(gen));
        maximum=maximum;//+baseFun(gen);
	if (this.calculate_average_error) {
	    this.avg_error += this.global_max - maximum;
	}
	if (calculate_offline_performance) {
	    if (this.recent_change || (maximum > current_maximum)) {
                //System.out.println(this.global_max+":"+maximum);
		this.current_error = this.global_max - maximum;
		if (this.calculate_right_peak)
		    this.current_peak_calc(gen);
		this.current_maximum = maximum;
		this.recent_change = false;
	    }
	    this.offline_performance += this.current_maximum;
	    this.offline_error += this.current_error;
	}
	this.evals++; /* increase the number of evaluations by one */
	return (maximum);
    } //eval_movpeaks

    /* whenever this function is called, the peaks are changed */
    public void change_peaks() {
	int i, j;
	double sum, sum2, offset, dummy;

	for (i = 0; i < this.number_of_peaks; i++) {
	    /* shift peak locations */
	    sum = 0.0;
	    for (j = 0; j < geno_size; j++) {
		this.shift[j] = this.movrand.nextDouble() - 0.5;
		sum += this.shift[j] * this.shift[j];
	    }
	    if (sum > 0.0) {
		sum = this.vlength / Math.sqrt(sum);
	    } else {/* only in case of rounding errors */
		sum = 0.0;
	    }
	    sum2 = 0.0;
	    for (j = 0; j < geno_size; j++) {
		this.shift[j] =
		    sum * (1.0 - this.lambda) * this.shift[j]
		    + this.lambda * this.prev_movement[i][j];
		sum2 += this.shift[j] * this.shift[j];
	    }
	    if (sum2 > 0.0)
		sum2 = this.vlength / Math.sqrt(sum2);
	    else /* only in case of rounding errors */
		sum2 = 0.0;
	    for (j = 0; j < geno_size; j++) {
		this.shift[j] *= sum2;
		this.prev_movement[i][j] = this.shift[j];
		if ((peak[i][j] + this.prev_movement[i][j])
		    < this.mincoordinate) {
		    peak[i][j] =
			2.0 * this.mincoordinate
			- peak[i][j]
			- this.prev_movement[i][j];
		    this.prev_movement[i][j] *= -1.0;
		}
		else if (
			 (peak[i][j] + this.prev_movement[i][j])
			 > this.maxcoordinate) {
		    peak[i][j] =
			2.0 * this.maxcoordinate
			- peak[i][j]
			- this.prev_movement[i][j];
		    this.prev_movement[i][j] *= -1.0;
		}
		else
		    peak[i][j] += prev_movement[i][j];
	    }
	    /* change peak width */
	    j = geno_size;
	    offset = this.movnrand.nextGaussian() * this.width_severity;
	    if ((peak[i][j] + offset) < this.minwidth)
		peak[i][j] = 2.0 * this.minwidth - peak[i][j] - offset;
	    else if ((peak[i][j] + offset) > this.maxwidth)
		peak[i][j] = 2.0 * this.maxwidth - peak[i][j] - offset;
	    else
		peak[i][j] += offset;
	    /* change peak height */
	    j++;
	    offset = this.height_severity * this.movnrand.nextGaussian();
	    if ((peak[i][j] + offset) < this.minheight)
		peak[i][j] = 2.0 * this.minheight - peak[i][j] - offset;
	    else if ((peak[i][j] + offset) > this.maxheight)
		peak[i][j] = 2.0 * this.maxheight - peak[i][j] - offset;
	    else
		peak[i][j] += offset;
	}
	if (this.calculate_average_error) {
	    this.global_max = -100000.0;
	    for (i = 0; i < this.number_of_peaks; i++) {
		for (j = 0; j < geno_size; j++)
		    this.coordinates[j] = peak[i][j];
		dummy = this.dummy_eval(coordinates);
		if (dummy > this.global_max) {
		    this.global_max = dummy;
		    this.maximum_peak = i;
		}
	    }
            this.global_max+=baseF;

	}
	this.recent_change = true;
	//printPeakData();
    } //change_peaks

    /* current_peak_calc determines the peak of the current best individual */
    private void current_peak_calc(double[] gen) {
	int i;
	double maximum = -100000.0, dummy;

	this.current_peak = 0;
	maximum = this.pf.calculate(gen, 0);
	for (i = 1; i < this.number_of_peaks; i++) {
	    dummy = this.pf.calculate(gen, i);
	    if (dummy > maximum) {
		maximum = dummy;
		this.current_peak = i;
	    } // if
	} // for
    } // current_peak_calc

    //	/* simple random number generator solely for the test function */
    //	/* movrand creates random number between 0 and 1 */
    //	/* This RNG is taken from the book by Kernighan/Ritchie, maybe it would */
    //	/* be worth to try a better one. */
    //
    //	double movrand() {
    //		/*  static unsigned long int next;*/
    //		this.movrandseed = this.movrandseed * 1103515245 + 12345;
    //		return (double) ((int) (this.movrandseed / 65536) % 32768) / 32767;
    //	}
    //
    //	/* this function produces normally distributed random values */
    //	double movnrand() {
    //
    //		double x1, w;
    //
    //		if (movpeaks.backup) {
    //			movpeaks.backup = false;
    //			return (movpeaks.x2);
    //		}
    //		else {
    //			do {
    //				x1 = 2.0 * this.movn_rand.nextDouble() - 1.0;
    //				movpeaks.x2 = 2.0 * this.movn_rand.nextDouble() - 1.0;
    //				w = x1 * x1 + movpeaks.x2 * movpeaks.x2;
    //			}
    //			while (w >= 1.0);
    //			w = Math.sqrt((-2.0 * Math.log(w)) / w);
    //			movpeaks.x2 = w * movpeaks.x2;
    //			movpeaks.backup = true;
    //			return (x1 * w);
    //		}
    //	}

    /* free disc space at end of program */
    public void free_peaks() {
	int i;

	for (i = 0; i < this.number_of_peaks; i++) {
	    peak[i] = null;
	    prev_movement[i] = null;
	}
	System.gc();
    } //free_peaks

    /* The following procedures may be used to change the step size over time */

    private void change_stepsize_random() /* assigns vlength a value from a normal distribution */ {
	this.vlength = this.movnrand.nextGaussian();
    }

    private void change_stepsize_linear() /* sinusoidal change of the stepsize, */ {

	/* returns to same value after 20 changes */

	this.vlength = 1 + Math.sin((double) counter * frequency);
	counter++;
    }

    public double get_avg_error() /* returns the average error of all evaluation calls so far */ {
	return (this.avg_error / (double) this.evals);
    }

    public double get_current_error() /* returns the error of the best individual evaluated since last change */
    /* To use this function, calculate_average_error and calculate_offline_performance must be set */ {
	return this.current_error;
    }

    public double get_offline_performance() /* returns offline performance */ {
	return (this.offline_performance / (double) this.evals);
    }

    public double get_offline_error() /* returns offline error */ {
	return (this.offline_error / (double) this.evals);
    }

    public int get_number_of_evals() /* returns the number of evaluations so far */ {
	return this.evals;
    }

    public boolean get_right_peak() /* returns 1 if current best individual is on highest peak, 0 otherwise */ {
	if (this.current_peak == this.maximum_peak)
	    return true;
	else
	    return false;
    }

    public double[][] getPeakPositions(){

	double[][] positions = new double[ this.number_of_peaks ][ geno_size ];

	for ( int i = 0; i < this.number_of_peaks; i++ ){

	    for ( int j = 0 ; j < geno_size; j++ ){

		positions[ i ][ j ] = peak[ i ][ j ];
	    }
	}
	return positions;
    }

    public double[] getPeakHeights(){

	double[] temp = new double[ this.number_of_peaks ];
	int index = geno_size + 1;
	for ( int i = 0; i < number_of_peaks; i++ ){
	    temp[ i ] = peak[i][ index ];
	}
	return temp;
    }

    public void printPeakData(){

	double[] temp = getPeakHeights();
	System.out.print( "Peak heights:\t" );
	for ( int i = 0; i < temp.length; i++ ){
	    System.out.print( temp[ i ] + "\t" );
	}
	System.out.println();
	
	//System.out.println("Current peak: " + this.current_peak + "\tMax peak: " + this.maximum_peak );
    }

    public int getCurrentPeak(){

	return this.current_peak;
    }

    public int getMaximumPeak(){

	return this.maximum_peak;
    }
	

    public double getMinCoordinate(){

	return mincoordinate;
    }
    public double getMaxCoordinate(){

	return maxcoordinate;
    }
    public double getMaxHeight(){

	return maxheight;
    }

} //movpeaks
