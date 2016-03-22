package io.cvet.editor.gui.commands;

public abstract class Command {

	protected String name;
	protected int argumentCount;
	
	public Command(String name, int argumentCount) {
		this.name = name;
		this.argumentCount = argumentCount;
	}

	public abstract void action(String[] arguments);

	public int getArgumentCount() {
		return argumentCount;
	}

	public abstract String getHelp();

}
