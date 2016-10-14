package com.qualcomm.ftcrobotcontroller.opmodes.worlds;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Beacon;
import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.ftcrobotcontroller.Vision;
import com.qualcomm.ftcrobotcontroller.VisionProcess;
import com.qualcomm.ftcrobotcontroller.XYCoor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mattquan on 2/18/2016.
 */
public class AutonomousBlueCameraCodesFromClosePos extends LinearOpMode {
    private Camera mCamera;
    public CameraPreview preview;
    public Bitmap image;


    
    ElapsedTime timer = new ElapsedTime(), timer2 = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {
       
        mCamera = ((FtcRobotControllerActivity) hardwareMap.appContext).mCamera;
//i need to init the camera and also get the instance of the camera        //on pic take protocol
        
        ((FtcRobotControllerActivity) hardwareMap.appContext).initCameraPreview(mCamera, this);

        //wait, because I have handler wait three seconds b4 it'll take a picture, in initCamera
        timer2.reset();
        int timeItTakes = (int)(timer2.time() * 1000);
        sleep(Vision.RETRIEVE_FILE_TIME - timeItTakes);
        //now we are going to retreive the image and convert it to bitmap
        SharedPreferences prefs = hardwareMap.appContext.getApplicationContext().getSharedPreferences(
                "com.quan.companion", Context.MODE_PRIVATE);
        String path = prefs.getString(Keys.pictureImagePathSharedPrefsKeys, "No path found");
        Log.e("path",path);
        //debug stuff - telemetry.addData("camera", "path: " + path);
        File imgFile = new File(path);
        image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        Log.e("image",image.toString());
        //cool now u have the image file u just took the picture of
        VisionProcess mVP = new VisionProcess(image);
        Log.e("starting output","start");
        telemetry.addData("starting output","doing smart computer stuff now");
        Beacon beacon = mVP.output(hardwareMap.appContext);
        Log.e("beacon",beacon.toString());
        telemetry.addData("beacon",beacon);
        if (!beacon.error()) {
            if (beacon.oneSideUnknown()) {
                //assume this is the right side, assume left side got chopped off
                if (beacon.getRight() == Beacon.COLOR_BLUE) {
                    telemetry.addData("beacon", 1);
                    //this is what i want, since im on red team. hit right side
                    pushRightButton();
                    sleep(500);
                    returnToOrigPosAfterPushRightButton();
                } else {
                    //the other side must be red
                    //drop servo arm, then move forward
                    telemetry.addData("beacon", 2);
                    adjustAndPressLeft();
                    sleep(500);
                    returnToOrigPosAfterAdjustAndPressLeft();
                    //park
                    //parkFromLeftSide();
                }
            } else {
                if (beacon.whereIsBlue().equals(Beacon.RIGHT)) {
                    pushRightButton();
                    sleep(500);
                    returnToOrigPosAfterPushRightButton();
                } else if (beacon.whereIsBlue().equals(Beacon.LEFT)) {
                    telemetry.addData("beacon", 4);
                    adjustAndPressLeft();
                    sleep(500);
                    returnToOrigPosAfterAdjustAndPressLeft();
                }
            }
        }
        
    }

    public void smoothMoveVol2 (double inches, boolean backwards) {
        //works best for at least 1000 ticks = 11.2 inches approx
        double rotations = inches / (Keys.WHEEL_DIAMETER * Math.PI);
        double totalTicks = rotations * 1120 * 3 / 2;
        int positionBeforeMovement = fl.getCurrentPosition();
        double ticksToGo = positionBeforeMovement+totalTicks;
        //p;us one because make the first tick 1, not 0, so fxn will never be 0
        double savedPower=0;
        double savedTick=0;
        while (fl.getCurrentPosition() < ticksToGo+1) {
            telemetry.addData("front left encoder: ", fl.getCurrentPosition());
            telemetry.addData("ticksFor", totalTicks);
            collector.setPower(-1*Keys.COLLECTOR_POWER);
            //convert to radians
            int currentTick = fl.getCurrentPosition() - positionBeforeMovement +1 ;
            if (currentTick<ticksToGo/2) {
                //use an inv tan function as acceleration
                //power = ((2/pi)*.86) arctan (x/totalticks*.1)
                double power = ((2/Math.PI)*Keys.MAX_SPEED) * Math.atan(currentTick/totalTicks/2*10);
                telemetry.addData("power","accel"+power);
                if (power<Keys.MIN_SPEED_SMOOTH_MOVE) {
                    telemetry.addData("bool",power<Keys.MIN_SPEED_SMOOTH_MOVE);
                    power = Keys.MIN_SPEED_SMOOTH_MOVE;
                    telemetry.addData("power","adjusted"+power);
                }
                telemetry.addData("power", power);
                setMotorPowerUniform(power, backwards);
                savedPower=power;
                savedTick=currentTick;
            }
            else {
                //decelerate using
                double newCurrentCount = currentTick+1-savedTick;
                //current tick changes, savedTick is constant
                double horizontalStretch = totalTicks/2*.2;
                if (newCurrentCount<horizontalStretch) {
                    //becuase of domain restrictions
                    setMotorPowerUniform(savedPower,backwards);
                }
                else {
                    //in the domain

                    double power = (2/Math.PI)*savedPower*Math.asin(horizontalStretch/newCurrentCount);
                    telemetry.addData("power","decel"+power);
                    if (power<Keys.MIN_SPEED_SMOOTH_MOVE) {
                        power = Keys.MIN_SPEED_SMOOTH_MOVE;
                        telemetry.addData("power","adjusted"+power);
                    }
                    setMotorPowerUniform(power,backwards);
                }

            }

        }
        rest();
    }


}
