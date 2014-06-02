package com.pchudzik.jsmtp.server.command;

import java.util.Collection;

/**
 * Created by pawel on 02.06.14.
 */
public interface CommandsProvider {
	Collection<? extends CommandActionFactory> getCommands();
}
