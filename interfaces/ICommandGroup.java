package org.usfirst.frc.team6013.robot.interfaces;
import java.util.ArrayList;

public class ICommandGroup extends ICommand {
	ArrayList<ICommand> cmdList = new ArrayList<ICommand>();
	
	public void addSequential(ICommand cmd) {
		cmdList.add(cmd);
	}
	
	public void addParallel(ICommand cmd) {
		int size = cmdList.size();
		
		if(size > 0) {
			ICommand lastItem = cmdList.get(size - 1);
			IParallelGroup newItem = new IParallelGroup();
			newItem.addParallel(lastItem);
			newItem.addParallel(cmd);
			cmdList.remove(lastItem);
			cmdList.add(newItem);
		} else {
			addSequential(cmd);
		}
		
		/* V1
		int size = cmdList.size();
		IParallelGroup newItem = null;
		
		if(size > 0) {
			ICommand lastItem = cmdList.get(size - 1);
			if(lastItem instanceof IParallelGroup) {
				newItem = (IParallelGroup)lastItem;
				newItem.addParallel(cmd);
			} else {
				newItem = new IParallelGroup();
				newItem.addParallel(lastItem);
				newItem.addParallel(cmd);
				cmdList.remove(lastItem);
			}
		} else {
			newItem = new IParallelGroup();
			newItem.addParallel(cmd);
		}
		cmdList.add(newItem); */
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public final void execute() {
		if(!cmdList.isEmpty()) {
			ICommand item = cmdList.get(0);
			
			if(item._initialized == false) {
				item.initialize();
				item._initialized = true;
			}
			item.execute();
			
			if(item.isFinished()) {
				item.end();
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
