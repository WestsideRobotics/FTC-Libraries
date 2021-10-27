/*
OBJ lib non annotated

*/

package org.firstinspires.ftc.teamcode;

// OBJ and Android Studio automatically create these import statements.
//import org.firstinspires.ftc.robotcore.external.ExportToBlocks;
//import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
//import com.example.google.ftc.software.obj.MoreMath;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.apache.commons.math3.stat.Frequency;

@TeleOp(name = "W_Libraries_Sample_v01", group = "Samples")


// BlocksOpModeCompanion provides many useful FTC objects to this class.
public class W_Libraries_Sample_v01 extends LinearOpMode {

@Override
public void runOpMode() {

        // wait for the start button to be pressed.
        waitForStart();
        String stringA = "A";
        char A = stringA.charAt(0);
        String stringB = "B";
        char B = stringB.charAt(0);
        double RunningCalcSum;
        double percentSum;
        
    // input data from gamepad until red X
    while (!gamepad1.x && opModeIsActive())  {
        
        if (gamepad1.a) {
            Frequency.addValue(1);
        }   else if (gamepad1.b)  {
            Frequency.addValue(2);
        }
        telemetry.addData("LIB A count", Frequency.getCount(1));
        telemetry.addData("LIB B count", Frequency.getCount(2));
        RunningCalcSum = (double) Frequency.getCount(1) + Frequency.getCount(2);
        telemetry.addData("Calculated sum", RunningCalcSum);
        telemetry.addData("*******", "********");
        telemetry.addData("Input repeatedly", "A or B");
        telemetry.addData("*******", "********");       
        telemetry.addData("To end input", "Press X");
        telemetry.update();
    }
    
    while (!gamepad1.y && opModeIsActive())  {
    
        telemetry.addData("Calculated sum", RunningCalcSum);
        telemetry.addData("LIB Sum of all frequencies", Frequency.getSumFreq());
        telemetry.addData("LIB Unique count, # of values in freq table", Frequency.getUniqueCount());
        telemetry.addData("LIB Percentage A", Frequency.getPct(1));
        telemetry.addData("LIB Percentage B", Frequency.getPct(2));
        percentSum = (double) Frequency.getPct(1) + Frequency.getPct(2);
        telemetry.addData("Calculated percent sum", percentSum);
        telemetry.addData("*******", "********");
        telemetry.addData("To end", "Press Y");
        telemetry.update();
    }
    

    /*

    // This annotation must directly precede a myBlock method
    @ExportToBlocks (
        comment = "returns average of 2 inputs",
        tooltip = "test non annot lib",
        parameterLabels = {"input 1", "input 2"}
        )
    // This is a myBlock method with two inputs and one output.
    public static double takeTheAverage (double input1, double input2) {
        return MoreMath.average(input1, input2);
      
      */
      
        
}   // end of OpMode
}   // end of class

// end of class W_Librari
