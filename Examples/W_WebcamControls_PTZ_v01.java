/* 

Demonstrates direct gamepad control of webcam virtual pan, tilt and zoom (PTZ),
using FTC SDK 7.0.

v01 dated 11/4/21
- modified from similar OpModes for Focus, and for Exposure & Gain.

Unlike Exposure Control, PTZ Control does not throw an exception on get()
if a feature is not supported.  But this requires testing to determine 
unsupported features.
Focus Control has methods to indicate support, and returned values are negative.

 */

package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.PtzControl;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat;


@TeleOp(name="Webcam Controls - PTZ v01", group ="Webcam Controls")

public class W_WebcamControls_PTZ_v01 extends LinearOpMode {

    private static final String VUFORIA_KEY =
            "AY8CNyf/////AAABmacTilnRckNipYdjO0lQTT6LIXxm2Y2jY7UsLAx+RIhtFqQuYjalD/A2ursWB0/PDX6m32jzxfrEEBkCUt/9M4yAJ5CHCpC/nf//QGGEgPGk6KMnnFuUR0BZMfGqVu9pHp8zurEXQDQ1nEuvhiU9TClgRIh4ZFhp0sF1G/G7RgemQY+t4kfeTQC//vjHoPpJu5l/tfJZrUm3E01GdcpKlgfs4Rt6QBwHmq2PQlnMGB70PlA2lw6KIX96Ngt9oUOTzen9hClsGeQkytxawnu3AXGV2Adkc1BAhhqFlphvMGGHHQ2SodTcFsvXfl1+A0VeRtZpL//0gDf8arizf+bIwyXo6eh6jcukQy8mkDUG461U";

    // Class Members
    private VuforiaLocalizer vuforia    = null;
    private WebcamName webcamName       = null;

    PtzControl myPtzControl;                // instantiate PTZ Control object
    
    PtzControl.PanTiltHolder minPanTilt;    // instantiate Holder for min
    int minPan;
    int minTilt;

    PtzControl.PanTiltHolder maxPanTilt;    // instantiate Holder for max
    int maxPan;
    int maxTilt;

    PtzControl.PanTiltHolder curPanTilt;    // instantiate Holder for current
    int curPan;
    int curTilt;

    int minZoom;
    int maxZoom;
    int curZoom;
    
    int panIncrement = 1;           // for manual gamepad control
    int tiltIncrement = 1;
    int zoomIncrement = 1;

    boolean isPanSupported;         // does this webcam support virtual Pan?
    boolean isTiltSupported;
    boolean isZoomSupported;

    boolean isPanRangeProvided;     // does this webcam support min & max Pan?
    boolean isTiltRangeProvided;
    boolean isZoomRangeProvided;

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

        // Set up the PTZ control object, to use its methods.
        myPtzControl = vuforia.getCamera().getControl(PtzControl.class);

        // display current PTZ values to user
        telemetry.addLine("\nTouch Start arrow to control webcam Pan, Tilt & Zoom (PTZ)");

        // Get the current properties from the webcam.  May be dummy zeroes.
        curPanTilt = myPtzControl.getPanTilt();
        curPan = curPanTilt.pan;
        curTilt = curPanTilt.tilt;
        curZoom = myPtzControl.getZoom();
        
        telemetry.addData("\nInitial pan value", curPan);
        telemetry.addData("Initial tilt value", curTilt);
        telemetry.addData("Initial zoom value", curZoom);

        telemetry.setDisplayFormat(Telemetry.DisplayFormat.CLASSIC);
        telemetry.update();


        waitForStart();

        // Get webcam PTZ limits; may be dummy zeroes.
        minPanTilt = myPtzControl.getMinPanTilt();
        minPan = minPanTilt.pan;
        minTilt = minPanTilt.tilt;
        
        maxPanTilt = myPtzControl.getMaxPanTilt();
        maxPan = maxPanTilt.pan;
        maxTilt = maxPanTilt.tilt;

        minZoom = myPtzControl.getMinZoom();
        maxZoom = myPtzControl.getMaxZoom();
    
        // check if this webcam supports virtual pan, tilt and/or zoom
        checkPtzSupport();
        
        while (opModeIsActive()) {

            // manually adjust the webcam PTZ variables
            if (gamepad1.dpad_right) {
                curPan += panIncrement;
            }  else if (gamepad1.dpad_left) {
                curPan -= panIncrement;
            }

            if (gamepad1.dpad_up) {
                curTilt += tiltIncrement;
            }  else if (gamepad1.dpad_down) {
                curTilt -= tiltIncrement;
            }
            
            if (gamepad1.y) {
                curZoom += zoomIncrement;
            }  else if (gamepad1.a) {
                curZoom -= zoomIncrement;
            }

            // ensure inputs are within webcam limits, if provided
            checkPtzLimits();
            
            // update the webcam's settings
            curPanTilt.pan = curPan;
            curPanTilt.tilt = curTilt;
            myPtzControl.setPanTilt(curPanTilt);
            myPtzControl.setZoom(curZoom);
            
            // display live feedback while user observes preview image
            telemetry.addLine("\nPAN: Dpad up/dn; TILT: Dpad L/R; ZOOM: Y/A");

            telemetry.addLine("\nWebcam properties (zero may mean not supported)");
            telemetry.addData("Pan", "Min: %.1s, Max: %.1s, Actual: %.1s",
                minPan, maxPan, myPtzControl.getPanTilt().pan);
            telemetry.addData("Programmed Pan", curPan);

            telemetry.addData("\nTilt", "Min: %.1s, Max: %.1s, Actual: %.1s",
                minTilt, maxTilt, myPtzControl.getPanTilt().tilt);
            telemetry.addData("Programmed Tilt", curTilt);
                
            telemetry.addData("\nZoom", "Min: %.1s, Max: %.1s, Actual: %.1s",
                    minZoom, maxZoom, myPtzControl.getZoom());
            telemetry.addData("Programmed Zoom", curZoom);

            telemetry.update();

            sleep(100);

        }   // end main while() loop

    }    // end OpMode


    // check if this webcam supports virtual pan, tilt and/or zoom
    private void checkPtzSupport() {

        int savedPan = curPan;
        curPanTilt.pan = curPan + panIncrement;
        myPtzControl.setPanTilt(curPanTilt);        // set new value
        curPanTilt = myPtzControl.getPanTilt();     // get actual value
        isPanSupported = (curPanTilt.pan != savedPan);   // true if Pan actually changed
        curPanTilt.pan = savedPan;     // revert to original

        int savedTilt = curTilt;
        curPanTilt.tilt = curTilt + tiltIncrement;
        myPtzControl.setPanTilt(curPanTilt);        // set new value
        curPanTilt = myPtzControl.getPanTilt();     // get actual value
        isTiltSupported = (curPanTilt.tilt != savedTilt);   // true if Tilt actually changed
        curPanTilt.tilt = savedTilt;     // revert to original

        int savedZoom = curZoom;
        myPtzControl.setZoom(curZoom + zoomIncrement);  // set new value
        curZoom = myPtzControl.getZoom();               // get actual value
        isZoomSupported = (curZoom != savedZoom);   // true if Zoom actually changed
        curZoom = savedZoom;     // revert to original

        if (isPanSupported) {
            isPanRangeProvided = (maxPan - minPan) != 0;    // false if no range
        }
        
        if (isTiltSupported) {
            isTiltRangeProvided = (maxTilt - minTilt) != 0; // false if no range
        }

        if (isZoomSupported) {
            isZoomRangeProvided = (maxZoom - minZoom) != 0; // false if no range
        }
        

    }  // end method checkPtzSupport()


    // ensure inputs are within webcam limits, if provided
    private void checkPtzLimits() {
        
        if (isPanSupported && isPanRangeProvided) {
            curPan = Math.max(curPan, minPan);
            curPan = Math.min(curPan, maxPan);
        } else {
            telemetry.addLine("min & max Pan not available on this webcam");
        }

        if (isTiltSupported && isTiltRangeProvided) {
            curTilt = Math.max(curTilt, minTilt);
            curTilt = Math.min(curTilt, maxTilt);
        } else {
            telemetry.addLine("min & max Tilt not available on this webcam");
        }

        if (isZoomSupported && isZoomRangeProvided) {
            curZoom = Math.max(curZoom, minZoom);
            curZoom = Math.min(curZoom, maxZoom);
        } else {
            telemetry.addLine("min & max Zoom not available on this webcam");
        }

    }   // end method checkPtzLimits()

}   // end OpMode class
