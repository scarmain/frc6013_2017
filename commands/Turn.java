package org.usfirst.frc.team6013.robot.commands;

import org.usfirst.frc.team6013.robot.Robot;
import org.usfirst.frc.team6013.robot.interfaces.ICommand;

public class Turn extends ICommand{
	private double startAngle;
	private double destAngle;
	
	public Turn(double turn) {
		destAngle = turn;
	}
	
	
	@Override
	protected void initialize() {
		startAngle = Robot.gyro.getAngle();
	}

	@Override
	protected void execute() {
		double turn;
		if(destAngle > 0) {
			turn = 0.6;
		} else {
			turn = -0.6;
		}
		Robot.driveTrain.tankDrive(turn, -turn);
	}

	@Override
	protected boolean isFinished() {
		boolean finished = false;

		if(destAngle > 0) {
			if ((Robot.gyro.getAngle() - startAngle) > destAngle) {
				finished = true;
			}
		} else {
			if ((Robot.gyro.getAngle() - startAngle) < destAngle) {
				finished = true;
			}
		}

		return finished;
	}

	@Override
	protected void end() {
		Robot.driveTrain.arcadeDrive(0, 0);
	}

}
