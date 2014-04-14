package com.pchudzik.jsmtp.server.command;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 08:42
 */
public class Command {
	private final String commandString;

	Command(String commandString) {
		this.commandString = commandString;
	}

	public String getCommandString() {
		return commandString;
	}
}
