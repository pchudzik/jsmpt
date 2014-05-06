package com.pchudzik.jsmtp.server.command;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 08:42
 */
public class Command {
	@Getter private final String commandString;
	@Getter private final String originalCommandString;

	public Command(String commandString) {
		Preconditions.checkArgument(StringUtils.isNotBlank(commandString));
		this.originalCommandString = commandString;
		this.commandString = commandString.trim().toLowerCase();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("commandString", commandString)
				.toString();
	}
}
