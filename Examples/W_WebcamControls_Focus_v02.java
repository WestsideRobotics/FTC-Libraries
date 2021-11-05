/* 

Demonstrates direct gamepad control of webcam Focus, using FTC SDK 7.0.

v01 dated 11/4/21
- modified from similar OpMode for Exposure & Gain.

v02
Don't look for exception thrown on unsupported get().
Unlike GainControl, FocusControl returns a negative number (-1.00) if 
a requested property is not available.  It does not throw an exception,
so the previous code does honor the "limits" of -1.00 and -1.00.
Also FocusControl has boolean methods indicating whether focus length
and mode are supported.

 */

package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.FocusControl;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

@TeleOp(name="Webcam Controls - Focus v02", group ="Webcam Controls")

public class W_WebcamControls_Focus_v02 extends LinearOpMode {

    private static final String VUFORIA_KEY =
            "AY8CNyf/////AAABmacTilnRckNipYdjO0lQTT6LIXxm2Y2jY7UsLAx+RIhtFqQuYjalD/A2ursWB0/PDX6m32jzxfrEEBkCUt/9M4yAJ5CHCpC/nf//QGGEgPGk6KMnnFuUR0BZMfGqVu9pHp8zurEXQDQ1nEuvhiU9TClgRIh4ZFhp0sF1G/G7RgemQY+t4kfeTQC//vjHoPpJu5l/tfJZrUm3E01GdcpKlgfs4Rt6QBwHmq2PQlnMGB70PlA2lw6KIX96Ngt9oUOTzen9hClsGeQkytxawnu3AXGV2Adkc1BAhhqFlphvMGGHHQ2SodTcFsvXfl1+A0VeRtZpL//0gDf8arizf+bIwyXo6eh6jcukQy8mkDUG461U";

    // Class Members
    private VuforiaLocalizer vuforia    = null;
    private WebcamName webcamName       = null;

    FocusControl myFocusControl;  // instantiate Focus Control object
    double minFocus;              // focal length, in mm?
    double maxFocus;
    double curFocus;
    double focusIncrement = 10;   // for manual gamepad adjustment
    boolean isFocusSupported;     // does this webcam support getFocusLength()?
    boolean isMinFocusSupported;  // does this webcam support getMinFocusLength()?
    boolean isMaxFocusSupported;  // does this webcam support getMaxFocusLength()?

    @Override public void runOpMode() {
        
        telemetry.setMsTransmissionInterval(50);
        
        // Connect to the webcam, using exact name per robot Configuration.
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         * We can pass Vuforia the handle to a camera preview resource (on the RC screen).
         * If no camera preview is desired, use the parameter-less constructor instead (commented out below).
         * Note: A preview window is required to view the Camera Stream on the Driver Station.
         */
        
        // One or both of these lines sends the live webcam view to the RC screen
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        // We also indicate which camera we wish to use.
        parameters.cameraName = webcamName;

        //  Set up the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Set up the focus control object, to use its methods.
        myFocusControl = vuforia.getCamera().getControl(FocusControl.class);

        // display current Focus Control Mode
        telemetry.addLine("\nTouch Start arrow to control webcam Focus");
        telemetry.addData("\nDefault focus mode", myFocusControl.getMode());
        telemetry.update();


        waitForStart();

        // set variable to current actual focal length of webcam, if supported
        curFocus = myFocusControl.getFocusLength();
        isFocusSupported = (curFocus >= 0.0);           // false if negative
        
        //isFocusSupported = true;  // can activate this line for testing

        // get webcam focal length limits, if provided
        minFocus = myFocusControl.getMinFocusLength();
        isMinFocusSupported = (minFocus >= 0.0);        // false if negative

        maxFocus = myFocusControl.getMaxFocusLength();
        isMaxFocusSupported = (maxFocus >= 0.0);        // false if negative

        // A non-default setting may persist in the camera, until changed again.
        myFocusControl.setMode(FocusControl.Mode.Fixed);

        // set initial focal length, if supported
        myFocusControl.setFocusLength(curFocus);

        checkFocusModes();     // display Focus Modes supported by this webcam

        while (opModeIsActive()) {

            // manually adjust the webcam focus variable
            if (gamepad1.right_bumper) {
                curFocus += focusIncrement;
            }  else if (gamepad1.left_bumper) {
                curFocus -= focusIncrement;
            }
    
            // ensure inputs are within webcam limits, if provided
            if (isMinFocusSupported) {
                curFocus = Math.max(curFocus, minFocus);
            } else {
                telemetry.addLine("minFocus not available on this webcam");
            }
            
            if (isMaxFocusSupported) {
                curFocus = Math.min(curFocus, maxFocus);
            } else {
                telemetry.addLine("maxFocus not available on this webcam");
            }
            
            // update the webcam's focal length setting
            myFocusControl.setFocusLength(curFocus);
            
            // display live feedback while user observes preview image
            if (isFocusSupported) {
                telemetry.addLine("Adjust focal length with Left & Right Bumpers");

                telemetry.addLine("\nWebcam properties (negative means not supported)");
                telemetry.addData("Focal Length", "Min: %.1f, Max: %.1f, Actual: %.1f",
                    minFocus, maxFocus, myFocusControl.getFocusLength());

                telemetry.addData("\nProgrammed Focal Length", "%.1f", curFocus);
            } else {
                telemetry.addLine("\nThis webcam does not support adustable focal length.");
            }
            
            telemetry.update();

            sleep(100);

        }   // end main while() loop

    }    // end OpMode


    // display Focus Modes supported by this webcam
    private void checkFocusModes() {
        
        while (!gamepad1.y && opModeIsActive()) {
            telemetry.addLine("Focus Modes supported by this webcam:");
            telemetry.addData("Auto", myFocusControl.isModeSupported(FocusControl.Mode.Auto));
            telemetry.addData("ContinuousAuto", myFocusControl.isModeSupported(FocusControl.Mode.ContinuousAuto));
            telemetry.addData("Fixed", myFocusControl.isModeSupported(FocusControl.Mode.Fixed));
            telemetry.addData("Infinity", myFocusControl.isModeSupported(FocusControl.Mode.Infinity));
            telemetry.addData("Macro", myFocusControl.isModeSupported(FocusControl.Mode.Macro));
            telemetry.addData("Unknown", myFocusControl.isModeSupported(FocusControl.Mode.Unknown));
            telemetry.addLine("*** PRESS Y TO CONTINUE ***");
            telemetry.update();
        }
        
    }   // end method checkFocusModes()

}   // end class
