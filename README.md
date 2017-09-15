# CellularrDE: Cellular Based Differential Evolution

This is the original implementation of **CellularDE** algorithm in Java. CellularDE is an optimization technique for dynamic environment. 

It is introduced in the following paper:

Vahid Noroozi, Ali B. Hashemi, and Mohammad Reza Meybodi, "[CellularDE: A Cellular Based Differential Evolution for Dynamic Optimization Problems](https://link.springer.com/chapter/10.1007%2F978-3-642-20282-7_35)", ICANNGA, 2011.

### How to run?
To run the code, you should set the parameters in the src/CellularDE/Parameters.java file, and then execute the main function in the src/CellularDE/MainProgram.java.

### Benchmark
The algorithm is evaluated on Moving Peak Benchmark (MPB) which is introduced by Juergen Branke in the following book:

Juergen Branke, "[Evolutionary optimization in dynamic environments](http://dl.acm.org/citation.cfm?id=578877)", Kluwer Academic, 2001.

The source code for the MPB library (scr/peaks) is from the original implementation by Juergen Branke.