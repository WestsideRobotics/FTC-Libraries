/*
This sample OBJ OpMode uses a real-world external library,
a feature of FTC SDK version 7.0.

This code centers around one method: Interval.contains()

Namely, is a test value inside a range defined by Interval.of()?


Testing issue: 

The coding and OpMode run fine in the intial work session.  Later, the 
RC app crashes every time upon opening, due to lacking a class, which triggers
and crashes OBJ's autocomplete.

Another OpMode using different libraries, crashed the RC app also.

*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.apache.commons.geometry.euclidean.oned.Interval;
import org.apache.commons.numbers.core.Precision;

@TeleOp(name = "W_Libraries_Geometry", group = "Samples")

public class W_Libraries_Sample_v02_geometry extends LinearOpMode {

@Override
public void runOpMode() {

    // wait for the start button to be pressed.
    waitForStart();
    double POINT_A = 1.0;
    double POINT_B = 2.0;
    
    Precision.DoubleEquivalence precision = Precision.doubleEquivalenceOfEpsilon(1e-3);
    
    Interval testRange = Interval.of(POINT_A, POINT_B, precision);
    
    double testPoint = 1.5;
    
    boolean isTestPointInRange = testRange.contains(testPoint);
    
    while (opModeIsActive()){
            
        telemetry.addData("Point A", "%.2f", POINT_A);
        telemetry.addData("Point B", "%.2f", POINT_B);
        telemetry.addData("Test Point", "%.2f", testPoint);
        
        telemetry.addData("*******", "********");
        telemetry.addData("Is Test Point in Range?", isTestPointInRange);
        telemetry.addData("*******", "********");       
        telemetry.update();
    }
    
}   // end of OpMode
}   // end of class
