/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.util;

import org.python.util.PythonInterpreter;

public class PythonExecutor {
    public static final PythonInterpreter PY_EXEC = new PythonInterpreter();
    
    /*
    public static void main(String[] args) {
        
        PY_EXEC.exec("print('Hello from Jython')");
    }
    // */
    
    public static void run(String pgm) {
        PY_EXEC.exec(pgm);
    }
}
