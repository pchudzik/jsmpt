package com.pchudzik.jsmtp.server.command.rfc821;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.pchudzik.jsmtp.server.command.rfc821.CommandUtils.newTransactionForClient;
import static org.mockito.Mockito.mock;

import com.pchudzik.jsmtp.common.function.ObjectAssert;
import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.ClientRejectedException;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
	public void emailRejectionTest(CommandActionFactory actionFactory, Command command, SmtpResponse expectedResponse, String expectedMessage) throws CommandExecutionException {
		catchException(actionFactory.create(clientConnection, command)).executeCommand();

		ObjectAssert.assertThat((CommandExecutionException)caughtException())
				.is(CommandExecutionException::isCritical, false)
				.isEqual(CommandExecutionException::getSmtpResponse, expectedResponse)
				.isEqual(CommandExecutionException::getMessage, expectedMessage);
	}
}
