package com.pchudzik.jsmtp.server.command.rfc821;

import java.io.BufferedReader;
import java.io.StringReader;

import static com.pchudzik.jsmtp.server.command.rfc821.CommandUtils.newTransactionForClient;
import static com.pchudzik.jsmtp.server.command.rfc821.DataCommandFactory.DataCommandAction.commandEnd;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.ClientRejectedException;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataCommandTest {
	protected ClientConnection clientConnection;
	protected MailTransaction mailTxMock;

	@BeforeMethod
	public void setupClient() throws ClientRejectedException {
		clientConnection = mock(ClientConnection.class);
		mailTxMock = newTransactionForClient(clientConnection);
	}

	@Test
	public void shouldInitializeUserInput() throws CommandExecutionException {
		CommandResponse response = new DataCommandFactory()
				.create(clientConnection, new Command("DATA"))
				.executeCommand();

		CommandResponseAssert.assertThat(response)
				.hasSmtpResponse(SmtpResponse.MAIL_INPUT_START)
				.isNotFinished();
	}

	@Test
	public void shouldReadDataFromClientUntilThereIsInput() throws CommandExecutionException {
		final String newLine = "\n",
				anyString = "x" + newLine + "" + newLine + "y" + newLine;
		final StringBuilder receivedData = new StringBuilder();
		final CommandAction dataCommand = new DataCommandFactory()
				.create(clientConnection, null);

		when(mailTxMock.dataInProgress()).thenReturn(true);
		when(clientConnection.getReader())
				.thenReturn(new BufferedReader(new StringReader(anyString + commandEnd + newLine)));
		doAnswer((invocation) -> {
			StringBuilder arg = (StringBuilder) invocation.getArguments()[0];
			receivedData.append(arg.toString());
			return null;
		}).when(mailTxMock).addUserData(any(StringBuilder.class));

		dataCommand.executeCommand();

		verify(mailTxMock).userDataFinished();
		assertThat(receivedData.toString()).
				isEqualTo(anyString);
	}
}