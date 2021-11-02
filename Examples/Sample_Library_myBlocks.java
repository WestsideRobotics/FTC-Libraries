/*
This example is used in a tutorial on FTC External Libraries.

The imported class is contained in the uploaded .jar file.
This external library does not have FTC annotations.  Its class methods
are provided here to Blocks users with a wrapper myBlock method.

*/

package org.firstinspires.ftc.teamcode;

// OBJ automatically creates these import statements
import org.firstinspires.ftc.robotcore.external.ExportToBlocks;
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;

// this class is contained in GeometryForObj.jar
import com.example.google.ftc.Geometry;

// extend the basic class with BlocksOpModeCompanion
public class Sample_Library_myBlocks extends BlocksOpModeCompanion {

    // This annotation must directly precede a myBlock method
    @ExportToBlocks (
        comment = "This myBlock returns the circumference of the circle " +
                  "whose radius is the given number.",
        tooltip = "Circumference of circle",
        parameterLabels = {"circle radius"}
    )
    public static double myCircumference(double r) {
        return Geometry.circleCircumference(r);
    }
    
    @ExportToBlocks (
        comment = "This myBlock returns the area of the circle " +
                  "whose radius is the given number.",
        tooltip = "Area of a circle",
        parameterLabels = {"circle radius"}
    )
    public static double myArea(double r) {
        return Geometry.circleArea(r);
    }

    @ExportToBlocks (
        comment = "This myBlock returns the hypotenuse (longest side) of the right " +
                  "triangle with legs whose lengths are specified by the two given numbers.",
        tooltip = "Calculate hypotenuse of 2 sides",
        parameterLabels = {"side a", "side b"}
    )
    public static double myHypotenuse(double a, double b) {
        return Geometry.hypot(a, b);
    }

}       // end of class Sample_Library_myBlocks
