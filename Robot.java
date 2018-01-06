package org.usfirst.frc.team6013.robot;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team6013.robot.commands.DriveForward;
import org.usfirst.frc.team6013.robot.commands.Turn;
import org.usfirst.frc.team6013.robot.interfaces.ICommandGroup;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "No Move";
	final String leftAuto = "Left";
	final String centerAuto = "Center";
	final String rightAuto = "Right";
	final String testAuto = "Test";
	String autoSelected;
	double autoStartTime;
	SendableChooser<String> autoChooser = new SendableChooser<>();
	
	Victor leftDrive;
	Victor rightDrive;
	Spark climbMotor;
	Joystick driverController;
	public static RobotDrive driveTrain;
	
	public static Encoder leftEncoder;
	public static Encoder rightEncoder;
	public static ADXRS450_Gyro gyro;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//zero equals joystick port 1 on the driver station
		driverController = new Joystick(0);
		
		//left and right drive channels, maps to PWM 0 and 1 on the robot
		leftDrive = new Victor(0);
		rightDrive = new Victor(1);
		climbMotor = new Spark(7);
		
		//drivetrain object to run tank/arcade drive for us
		driveTrain = new RobotDrive(leftDrive, rightDrive);
		
    	//24" = 1790 * 4
    	leftEncoder = new Encoder(0, 1, true, EncodingType.k4X);
    	rightEncoder = new Encoder(2, 3, false, EncodingType.k4X);
    	
    	leftEncoder.setDistancePerPulse(0.053631);
    	rightEncoder.setDistancePerPulse(0.053631);
    	
    	gyro = new ADXRS450_Gyro();
    	
		autoChooser.addObject("No Move", defaultAuto);
		autoChooser.addObject("Left Side", leftAuto);
		autoChooser.addDefault("Center", centerAuto);
		autoChooser.addObject("Right Side", rightAuto);
		autoChooser.addObject("Test Mode", testAuto);
		
		//camera code to display on the dashboard, runs on an independent thread as the robot code (dual core micro)
		new Thread( () -> {
			try {
				UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
				cam.setResolution(640, 480);
				CvSink cvSink = CameraServer.getInstance().getVideo();
				CvSource output = CameraServer.getInstance().putVideo("Camera", 640, 480);
				
				Mat source = new Mat();
				Mat outputFrame = new Mat();
				
				//main loop of camera code
				while(true) {
					//read frame
					cvSink.grabFrame(source);
					//process image? (part of the default code, maybe gives some time so this loop doesn't go crazy
					Imgproc.cvtColor(source, outputFrame, Imgproc.COLOR_BGR2GRAY);
					//send to output buffer
					output.putFrame(outputFrame);
				}
			} catch (Exception e) {
				System.out.println("Camera exception");
			}
		}).start();
	}

	@Override
	public void disabledInit() {

	}
	
	@Override
	public void disabledPeriodic() {
	}
	
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	ICommandGroup autoGroup;
	@Override
	public void autonomousInit() {
		//read what the selector says at the beginning of auto mode
		autoSelected = autoChooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
		
    	autoGroup = new ICommandGroup();

		switch (autoSelected) {
		case leftAuto:
	    	autoGroup.addSequential(new DriveForward(100));
	    	autoGroup.addSequential(new Turn(43));
	    	autoGroup.addSequential(new DriveForward(24));
			break;
		case centerAuto:
			autoGroup.addSequential(new DriveForward(80));
			break;
		case rightAuto:
	    	autoGroup.addSequential(new DriveForward(96));
	    	autoGroup.addSequential(new Turn(-53));
	    	autoGroup.addSequential(new DriveForward(41));
			break;
		case defaultAuto:
		default:
			driveTrain.arcadeDrive(0, 0);
			break;
		}
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		autoGroup.execute();
	}

	final double SPEED_RATIO = 0.8;
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		//axis 1 is left stick forward/backwards, axis 4 is right stick left/right
		//constants added to slow down the robot to make it more controllable, can be tuned for drivers
		double speed = deadband(driverController.getRawAxis(1)) * SPEED_RATIO;
		double turn = -deadband(driverController.getRawAxis(4)) * 0.7;
		//boost goes from 0-0.3 (trigger is 0-1, no negative)
		double boost = driverController.getRawAxis(3);
		
		speed =  speed * (SPEED_RATIO + ((1-SPEED_RATIO) * boost));
		
		//command the motors to drive, drivetrain object handles all the turning logic for us
		driveTrain.arcadeDrive(speed, turn);
		
		//climb code
		double climbSpeed = driverController.getRawAxis(2);
		if (driverController.getRawButton(5) == true) {
			//drive climber in reverse
			climbMotor.set(-0.6);
		} else {
			climbMotor.set(climbSpeed);
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		//test mode is basically the same as teleop, only used for practice
		teleopPeriodic();
	}
	
    /**
     * This function is called periodically during operator control
     */
    public void robotPeriodic() {
    	SmartDashboard.putNumber("LeftEncoder", leftEncoder.getDistance());
    	SmartDashboard.putNumber("RightEncoder", rightEncoder.getDistance());
    	SmartDashboard.putNumber("Gyro Angle", Robot.gyro.getAngle());
    	SmartDashboard.putData("Auto choices", autoChooser);
		String autoMode = autoChooser.getSelected();
		if(autoMode == null) {
			autoMode = "(null)";
		}
    	SmartDashboard.putString("Selected Auto Mode", autoMode);
    }
    
	
	final double DEADBAND = 0.2;
	private double deadband(double input) {
    	if((-DEADBAND < input) && (input < DEADBAND)) {
    		return 0;
    	} else {
    		return input;
    	}
	}
}

