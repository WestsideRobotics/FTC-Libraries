/* 

Starting with ConceptVuforiaNavigationWebcam, try to replicate functions
of test OpMode provided by @Windwoes for FTC PR 1959, 
"Add support for UVC gain/ISO control".

v01 works, archived. RC phone displays webcam preview.

v02 begin work on exposure control.
Start here with FTC API: https://javadoc.io/doc/org.firstinspires.ftc
Click RobotCore to get here: https://javadoc.io/doc/org.firstinspires.ftc/RobotCore/latest/index.html
Then click this: org.firstinspires.ftc.robotcore.external.hardware.camera.controls

which includes:
ExposureControl     
FocusControl     
GainControl     
PtzControl

We first try Exposure Control, suggested by @Windwoes here:
Ref. https://github.com/FIRST-Tech-Challenge/ftc_sdk/issues/2409

This class includes 2 methods from @Windwoes prompt:
boolean setMode(ExposureControl.Mode mode)
boolean setExposure(long duration, java.util.concurrent.TimeUnit durationUnit)

v03   remove navigation

v04  add min/max and gamepad input.

Unlike FocusControl and PtzControl, this class throws a null pointer exception
on get() for an unsupported feature.  Need to try/catch.

 */

package org.firstinspires.ftc.teamcode;

// OBJ auto-added the following 3 import statements, for webcam control code.
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import java.util.concurrent.TimeUnit;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

@TeleOp(name="Webcam Controls - Exp & Gain v04", group ="Webcam Controls")

public class W_WebcamControls_Exp_Gain_v04 extends LinearOpMode {

    private static final String VUFORIA_KEY =
            "AY8CNyf/////AAABmacTilnRckNipYdjO0lQTT6LIXxm2Y2jY7UsLAx+RIhtFqQuYjalD/A2ursWB0/PDX6m32jzxfrEEBkCUt/9M4yAJ5CHCpC/nf//QGGEgPGk6KMnnFuUR0BZMfGqVu9pHp8zurEXQDQ1nEuvhiU9TClgRIh4ZFhp0sF1G/G7RgemQY+t4kfeTQC//vjHoPpJu5l/tfJZrUm3E01GdcpKlgfs4Rt6QBwHmq2PQlnMGB70PlA2lw6KIX96Ngt9oUOTzen9hClsGeQkytxawnu3AXGV2Adkc1BAhhqFlphvMGGHHQ2SodTcFsvXfl1+A0VeRtZpL//0gDf8arizf+bIwyXo6eh6jcukQy8mkDUG461U";

    // Class Members
    private VuforiaLocalizer vuforia    = null;
    private WebcamName webcamName       = null;

    ExposureControl myExposureControl;  // instantiate exposure control object
    long minExp;
    long maxExp;
    long curExp = 15;
    boolean isMinExpSupported;      // does this webcam support getMinExp()?
    boolean isMaxExpSupported;

    GainControl myGainControl;      // instantiate gain control object
    int minGain;
    int maxGain;
    int curGain = 0;
    boolean isMinGainSupported;      // does this webcam support getMinGain()?
    boolean isMaxGainSupported;

    @Override public void runOpMode() {
        
        telemetry.setMsTransmissionInterval(50);
        
        // Connect to the webcam, using exact name per robot Configuration.
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         * We can pass Vuforia the handle to a camera preview resource (on the RC screen);
         * If no camera-preview is desired, use the parameter-less constructor instead (commented out below).
         * Note: A preview window is required if you want to view the camera stream on the Driver Station Phone.
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

        // Set up the exposure control object, to use its methods.
        myExposureControl = vuforia.getCamera().getControl(ExposureControl.class);

        // display mode to user
        telemetry.addLine("\nTouch Start arrow to control webcam Exposure and Gain");
        telemetry.addData("\nDefault exposure mode", myExposureControl.getMode());
        telemetry.update();


        waitForStart();

        // get webcam exposure limits, if provided
        try { 
            minExp = myExposureControl.getMinExposure(TimeUnit.MILLISECONDS);
            isMinExpSupported = true;
        } catch (Exception e) {
            isMinExpSupported = false;
        }
        
        try { 
            maxExp = myExposureControl.getMaxExposure(TimeUnit.MILLISECONDS);
            isMaxExpSupported = true;
        } catch (Exception e) {
            isMaxExpSupported = false;
        }

        // get webcam gain limits, if provided
        try { 
            minGain = myGainControl.getMinGain();
            isMinGainSupported = true;
        } catch (Exception e) {
            isMinGainSupported = false;
        }
        
        try { 
            maxGain = myGainControl.getMaxGain();
            isMaxGainSupported = true;
        } catch (Exception e) {
            isMaxGainSupported = false;
        }

        // Change mode from apparent default ContinuousAuto to Manual.
        // A non-default setting may persist in the camera, until changed again.
        myExposureControl.setMode(ExposureControl.Mode.Manual);

        /*
        Allowed values (enum constants of type java.lang.String) of ExposureControl.Mode are:
        AperturePriority 
        Auto 
        ContinuousAuto 
        Manual 
        ShutterPriority 
        Unknown 
        */

        // set intial exposure
        myExposureControl.setExposure(curExp, TimeUnit.MILLISECONDS);
        /*
        setExposure() takes 2 parameters:
        - duration, of type long.  5 is very dark, 50 looks about normal
        - unit of duration, of type java.util.concurrent.TimeUnit
        */

        myGainControl = vuforia.getCamera().getControl(GainControl.class);
        myGainControl.setGain(curGain);     // set initial gain


        while (opModeIsActive()) {

            // manually adjust the webcam brightness variables
            float changeExp = -gamepad1.left_stick_y;
            float changeGain = -gamepad1.right_stick_y;

            int changeExpInt = (int) (changeExp*5);
            int changeGainInt = (int) (changeGain*5);

            curExp += changeExpInt;
            curGain += changeGainInt;

            // ensure inputs are within webcam limits, if provided
            if (isMinExpSupported) {
                curExp = Math.max(curExp, minExp);
            } else {
                telemetry.addLine("minExp not available on this webcam");
            }
            
            if (isMaxExpSupported) {
                curExp = Math.min(curExp, maxExp);
            } else {
                telemetry.addLine("maxExp not available on this webcam");
            }

            if (isMinGainSupported) {
                curGain = Math.max(curGain, minGain);
            } else {
                telemetry.addLine("minGain not available on this webcam");
            }

            if (isMaxGainSupported) {
                curGain = Math.min(curGain, maxGain);
            } else {
                telemetry.addLine("maxGain not available on this webcam");
            }

            // update the webcam's settings while observing the preview image
            myExposureControl.setExposure(curExp, TimeUnit.MILLISECONDS);
            myGainControl.setGain(curGain);

            telemetry.addLine("\nExposure: left stick Y; Gain: right stick Y");
            telemetry.addData("Exposure", "Min:%d, Max:%d, Current:%d", minExp, maxExp, curExp);
            telemetry.addData("Gain", "Min:%d, Max:%d, Current:%d", minGain, maxGain, curGain);
            telemetry.update();

            sleep(100);

        }   // end main while() loop

    }    // end OpMode

}   // end OpMode class
