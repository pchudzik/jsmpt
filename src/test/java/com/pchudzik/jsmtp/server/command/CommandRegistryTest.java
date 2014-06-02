package com.pchudzik.jsmtp.server.command;

import com.pchudzik.jsmtp.server.command.common.ContextConstant;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientContext;
import com.pchudzik.jsmtp.server.nio.pool.client.TestClientConnectionFactory;
import org.mockito.ArgumentMatcher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandRegistryTest {
	private final TestClientConnectionFactory clientConnectionFactory = new TestClientConnectionFactory();

	private CommandActionFactory factory1;
	private CommandActionFactory factory2;

	@BeforeMethod
	public void setup() {
		factory1 = mock(CommandActionFactory.class);
		factory2 = mock(CommandActionFactory.class);
	}

	@Test
	public void shouldRegisterCommandProvidersInOrder() {
		final CommandRegistry commandRegistry = newRegistry(factory1, factory2);

		assertThat(commandRegistry.availableCommands)
				.containsExactly(
						factory1,
						factory2);
	}

	@Test
	public void shouldUsePendingCommandIfIsInProgress() throws IOException {
		final Optional<CommandAction> pendingCommand = Optional.of(mock(CommandAction.class));
		final ClientConnection clientConnectionMock = clientConnectionFactory.newConnectionMock();
		final ClientContext clientContextMock = clientConnectionMock.getClientContext();
		final CommandRegistry commandRegistry = newRegistry(factory1, factory2);
		when(clientContextMock.<CommandAction>getObject(ContextConstant.pendingCommand))
				.thenReturn(pendingCommand);

		assertThat(commandRegistry.selectCommand(clientConnectionMock))
				.isEqualTo(pendingCommand);
	}

	@Test
	public void shouldHandleBlankClientInput() throws IOException {
		final ClientConnection clientConnectionMock = clientConnectionFactory.newConnectionMock();
		when(clientConnectionMock.getReader()).thenReturn(new BufferedReader(new StringReader("  ")));

		assertThat(newRegistry(factory1).selectCommand(clientConnectionMock).isPresent())
				.isFalse();
	}

	@Test
	public void shouldReturnCommandAction() throws IOException {
		final String command = "command";
		final CommandAction commandAction = mock(CommandAction.class);
		final ClientConnection clientConnectionMock = clientConnectionFactory.newConnectionMock();
		when(clientConnectionMock.getReader()).thenReturn(new BufferedReader(new StringReader(command)));
		doReturn(true).when(factory2).canExecute(argThat(new ArgumentMatcher<Command>() {
			@Override
			public boolean matches(Object argument) {
				Command cmd = (Command) argument;
				return command.equals(cmd.getOriginalCommandString());
			}
		}));
		when(factory2.create(eq(clientConnectionMock), any(Command.class))).thenReturn(commandAction);

		assertThat(newRegistry(factory1, factory2).selectCommand(clientConnectionMock))
				.isEqualTo(Optional.of(commandAction));
	}

	private CommandRegistry newRegistry(CommandActionFactory ... factories) {
		return new CommandRegistry(asList(factories)
				.stream()
				.map(factory -> (CommandsProvider) () -> asList(factory))
				.collect(Collectors.toList()));
	}
}