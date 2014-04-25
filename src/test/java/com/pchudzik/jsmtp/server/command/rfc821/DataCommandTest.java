package com.pchudzik.jsmtp.server.command.rfc821;
 
import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.ClientRejectedException;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.pchudzik.jsmtp.server.command.rfc821.CommandUtils.newTransactionForClient;
import static org.mockito.Mockito.mock;
 
public class DataCommandTest {
    protected ClientConnection clientConnection;
    protected MailTransaction mailTx;
 
    @BeforeMethod
    public void setupClient() throws ClientRejectedException {
        clientConnection = mock(ClientConnection.class);
        mailTx = newTransactionForClient(clientConnection);
    }
 
    @Test
    public void shouldInitializeUserInput() throws CommandExecutionException {
        CommandResponse response = new DataCommand().executeCommand(clientConnection, new Command("DATA"));
 
        CommandResponseAssert.assertThat(response)
                .hasSmtpResponse(SmtpResponse.MAIL_INPUT_START)
                .isNotFinished();
    }
 
    @Test
    public void shouldReadDataFromClientUntilThereIsInput() throws CommandExecutionException {
        CommandResponse response = new DataCommand().executeCommand(clientConnection, null);
         
    }
 
}