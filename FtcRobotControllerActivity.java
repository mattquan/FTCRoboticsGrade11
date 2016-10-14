/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.FtcRobotControllerService;
import com.qualcomm.ftccommon.FtcRobotControllerService.FtcRobotControllerBinder;
import com.qualcomm.ftccommon.LaunchActivityConstantsList;
import com.qualcomm.ftccommon.Restarter;
import com.qualcomm.ftccommon.UpdateUI;
import com.qualcomm.ftcrobotcontroller.opmodes.FtcOpModeRegister;
import com.qualcomm.ftcrobotcontroller.opmodes.states.AutonomousBlueStates;
import com.qualcomm.ftcrobotcontroller.opmodes.states.AutonomousRedStates;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonDefense;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueCameraCodesFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueCameraCodesFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedCameraCodesFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedCameraCodeFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousTemplate;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.Dimmer;
import com.qualcomm.robotcore.util.ImmersiveMode;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FtcRobotControllerActivity extends Activity {

  
  public static Camera mCamera;
  private static CameraPreview mPreview;
  public void initImageTakenPreview(final Bitmap image) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {

        ImageView replaceRobotIcon = (ImageView) findViewById(R.id.robotIcon);
        replaceRobotIcon.setImageBitmap(image);
      }
    });
  }
  public void initCameraPreview(final Camera camera, final com.qualcomm.ftcrobotcontroller.opmodes.deprecated.CameraTestOp context) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        context.preview = new CameraPreview(FtcRobotControllerActivity.this, camera);
        FrameLayout cameraPreviewLayout = (FrameLayout) findViewById(R.id.previewLayout);
        cameraPreviewLayout.addView(context.preview);
      }
    });
  }
  
}
