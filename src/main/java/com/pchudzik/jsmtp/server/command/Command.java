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

	public Command(String commandString) {
		Preconditions.checkArgument(StringUtils.isNotBlank(commandString));
		this.commandString = commandString;
	}

	public String getCommandString() {
		return commandString;
	}
}
