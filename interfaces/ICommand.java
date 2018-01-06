package org.usfirst.frc.team6013.robot.interfaces;
/** 
 * This class replicates a simple version of the Command class provided by WPILib.  The idea
 * is to partition the code in a way that is simple to run/execute steps easily, but not have
 * all the requirements/baggage of the full command based programming.
 *  
 * See edu.wpi.first.wpilibj.Command for full example.*/

public abstract class ICommand {
	protected boolean _initialized = false;

	/**
	 * The initialize method is called the first time this Command is run after being started.
	 */
	protected abstract void initialize();
	
	/**
	 * The execute method is called repeatedly until this Command either finishes or is canceled.
	 */
	protected abstract void execute();
	
	/**
	 * Returns whether this command is finished. If it is, then the command will be removed and {@link
	 * Command#end() end()} will be called.
	 *
	 * <p>Returning false will result in the command never ending automatically. Returning true will 
	 * result in the command executing once and finishing immediately. 
	 *
	 * @return whether this command is finished.
	 */
	protected abstract boolean isFinished();
		
	/**
	 * Called when the command ended peacefully. This is where you may want to wrap up loose ends,
	 * like shutting off a motor that was being used in the command.
	 */
	protected abstract void end();
}
