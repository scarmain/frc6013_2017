package org.usfirst.frc.team6013.robot.interfaces;
import java.util.ArrayList;

public class IParallelGroup extends ICommand {
	ArrayList<ICommand> cmdList = new ArrayList<ICommand>();
	
	public void addParallel(ICommand cmd) {
		cmdList.add(cmd);
	}
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected final void execute() {
		ArrayList<ICommand> removeList = new ArrayList<ICommand>();
		if(!cmdList.isEmpty()) {
			for(ICommand item:cmdList) {
				if(item._initialized == false) {
					item.initialize();
					item._initialized = true;
				}
				item.execute();
				
				if(item.isFinished()) {
					item.end();
					//cannot remove while iterating through the list
					removeList.add(item);
				} 
			}
			
			for(ICommand item:removeList) {
				cmdList.remove(item);
			}
		}
	}

	@Override
	protected boolean isFinished() {
		// TODO Auto-generated method stub
		return cmdList.isEmpty();
	}

	@Override
	protected void end() {
		// TODO Auto-generated method stub
		
	}
}
