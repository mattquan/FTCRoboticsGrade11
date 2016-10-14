# FTCRoboticsGrade11
Shape detection and smooth move algorithms - written by Matt Quan

# Shape Detection
The file Vision.java contains the main algorithms (compressing, filtering, edge detection, identification, etc.)

The files FtcRobotControllerActivity.java shows the code used to initialize the camera of the device, and Autonomous.java is the code that is actually run by the robot (actual taking of picture and having the robot do certain tasks based on the outcome of the image analysis).

Beacon, CameraPreview, VisionProcess, and XYCoor are all auxiliary files.

# Smoooth Move
The file Autonomous.java contains the smooth move method in the method titled: SmoothMoveVol2();
To acclerate our motors smoothly, we use an inverse tangent function for acceleration and an inverse cosecant function for deceleration.

# Note:
The FtcRobotControllerActivity and Autonomous files will not run properly; I have eliminated code that does not pertain to these two algorithms.
For more information, check the Livingston Lancer Robotics ftc-app repository. This includes 100% of files including those not authored by me.
https://github.com/LivingstonLancerRobotics/ftc_app
