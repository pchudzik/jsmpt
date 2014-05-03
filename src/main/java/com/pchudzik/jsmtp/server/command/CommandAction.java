package com.pchudzik.jsmtp.server.command;

/**
 * Created by pawel on 14.04.14.
 */
public interface CommandAction {
	CommandResponse executeCommand() throws CommandExecutionException;
}
