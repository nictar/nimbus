package nimbus.arcane;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

/* Testuing unit for Chat Activity class */

public class ChatActivityTesting {

    @Mock
    private DatabaseReference mockedDatabaseReference;

    private ChatActivity mockChatClass;

    @Before
    /* Mocking all nessecary database references */
    public void setUp() {

        mockedDatabaseReference = Mockito.mock(DatabaseReference.class);

        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);

        mockChatClass = Mockito.mock(ChatActivity.class);
        mockChatClass.setDataBase(mockedFirebaseDatabase.getReference());
    }

    @Test
    /* Testing all of the functions for Chat Activity class*/
    public void testSendMessage() {
        String mockTypeString = "test";
        String mockMessageString = "testing";
        mockChatClass.sendMessage(mockTypeString,mockMessageString);
        String expectedType = "test";
        String expectedMessage = "testing";
        verify(mockChatClass).sendMessage(expectedType,expectedMessage);
    }

    @Test
    public void testLoadMessage() {
        mockChatClass.loadMessages();
        verify(mockChatClass).loadMessages();
    }
}
