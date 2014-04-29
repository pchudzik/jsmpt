package com.pchudzik.jsmtp.server.command;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 08:42
 */
public class Command {
	private final String commandString;
	private final String originalCommandString;

	public Command(String commandString) {
		Preconditions.checkArgument(StringUtils.isNotBlank(commandString));
		this.originalCommandString = commandString;
		this.commandString = commandString.trim().toLowerCase();
	}

	public String getOriginalCommandString() {
		return originalCommandString;
	}

	public String getCommandString() {
		return commandString;
	}
}
