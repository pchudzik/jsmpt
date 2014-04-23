package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.ClientRejectedException;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.pchudzik.jsmtp.server.command.rfc821.CommandUtils.newTransactionForClient;
import static org.mockito.Mockito.mock;

/**
 * Created by pawel on 23.04.14.
 */
abstract class AddressExtractingCommandTest {
	static final String wrongAddress = "<wrong address>";
	static final String email = "<somebody@example.com>";

	protected ClientConnection clientConnection;
	protected MailTransaction mailTx;

	@BeforeMethod
	public void setupClient() throws ClientRejectedException {
		clientConnection = mock(ClientConnection.class);
		mailTx = newTransactionForClient(clientConnection);
	}


	final static String rejectionEmailsDataProvider = "rejectionEmailsDataProvider";
	@DataProvider(name = rejectionEmailsDataProvider) abstract Object[][] rejectionEmailsDataProvider();
	@Test(dataProvider = rejectionEmailsDataProvider)
	public void emailRejectionTest(CommandAction action, Command command, SmtpResponse expectedResponse, String expectedMessage) throws CommandExecutionException {
		catchException(action).executeCommand(clientConnection, command);

		CommandExecutionExceptionAssert.assertThat(caughtException())
				.isNotCritical()
				.hasSmtpResponse(expectedResponse)
				.hasMessage(expectedMessage);
	}
}
