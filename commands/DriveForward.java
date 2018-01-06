package org.usfirst.frc.team6013.robot.commands;

import org.usfirst.frc.team6013.robot.Robot;
import org.usfirst.frc.team6013.robot.interfaces.ICommand;

public class DriveForward extends ICommand {
	final double START_RAMP_DIST = 48; //in inches
	final double MIN_SPEED = 0.4;
	final double MAX_SPEED = 0.6;
	final double TURN_GAIN = 0.25; //works to 100% turn when 4" off of center
	
	private double distToTravel;
	private double distanceGone = 0;
	private boolean isForward;
	private double startAngle;
	
	public DriveForward(double dist) {
		//set how far we need to travel
		distToTravel = Math.abs(dist);
		if(dist > 0) {
			isForward = true;
		} else {
			isForward = false;
		}
	}
	
	@Override
	protected void initialize() {
		//zero the encoders
		Robot.leftEncoder.reset();
		Robot.rightEncoder.reset();
		startAngle = Robot.gyro.getAngle();
	}

	@Override
	protected void execute() {
		//drive robot forward
		double speed = 0;
		double turn = 0;
		double distRemain;
		double leftDist = Robot.leftEncoder.getDistance();
		double rightDist = Robot.rightEncoder.getDistance();
		
		distanceGone = Math.abs((leftDist + rightDist) / 2);
		distRemain = distToTravel - distanceGone;
		
		if(distRemain > START_RAMP_DIST) {
			speed = MAX_SPEED;
		} else {
			double rampPercent = distRemain / START_RAMP_DIST;
			speed = MIN_SPEED + (rampPercent * (MAX_SPEED - MIN_SPEED));
		}
		
		//compensation based on encoders
		//turn = -(leftDist - rightDist) * TURN_GAIN;
		
		//compensation based on gyro
		turn = (Robot.gyro.getAngle() - startAngle) * 0.05;
		
		if(isForward == false) {
			speed = -speed;
		} else {
			
		}
		
		Robot.driveTrain.arcadeDrive(-speed, turn);
	}

	@Override
	protected boolean isFinished() {
		//check to see if we have traveled the necessary distance
		if(distanceGone > distToTravel) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void end() {
		//command wheels to stop when at the distance we needed to travel
		Robot.driveTrain.arcadeDrive(0, 0);
		System.out.println("Finished distance command: " + distToTravel);
	}
}
