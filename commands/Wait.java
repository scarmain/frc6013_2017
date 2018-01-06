package org.usfirst.frc.team6013.robot.commands;

import org.usfirst.frc.team6013.robot.interfaces.ICommand;

import edu.wpi.first.wpilibj.Timer;

public class Wait extends ICommand {
	double delayTime = 0;
	double startTime;
	
	public Wait (double time) {
		delayTime = time;
	}
	
	@Override
	protected void initialize() {
		startTime = Timer.getFPGATimestamp();
	}

	@Override
	protected void execute() {

	}

	@Override
	protected boolean isFinished() {
		double currentTime = Timer.getFPGATimestamp();
		if((startTime+delayTime) < currentTime) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void end() {
	}

}
