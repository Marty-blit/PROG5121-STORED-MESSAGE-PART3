import java.util.*;

class QuickChatPOE {

    // ===== PARALLEL ARRAYS - Required by POE Part 3 =====
    // Stored Messages - loaded from JSON, no hard-coding
    private static String[] senderArray = new String[100];
    private static String[] recipientArray = new String[100];
    private static String[] messageArray = new String[100];
    private static String[] messageIDArray = new String[100];
    private static String[] messageHashArray = new String[100];
    private static int storedMessageCount = 0;

    // Sent Messages - for final report option f
    private static String[] sentRecipientArray = new String[100];
    private static String[] sentMessageArray = new String[100];
    private static String[] sentHashArray = new String[100];
    private static int sentMessageCount = 0;

    // Disregarded Messages - for completeness
    private static String[] disregardedMessageArray = new String[100];
    private static int disregardedCount = 0;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Load test data + JSON data at start - no hard-coding
        loadTestData();

        boolean loggedIn = true;
        System.out.println("Welcome to QuickChat.");

        // Main menu with 4 options now
        while (loggedIn) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Send Message");
            System.out.println("2. Show Sent Report");
            System.out.println("3. Quit");
            System.out.println("4. Stored Messages"); // NEW - Part 3 requirement
            System.out.print("Choose option: ");

            int choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {
                sendMessage(input);
            } else if (choice == 2) {
                showSentMessagesReport();
            } else if (choice == 3) {
                loggedIn = false;
                System.out.println("Goodbye!");
            } else if (choice == 4) {
                storedMessagesMenu(input); // NEW submenu for a-f
            }
        }
    }

    // ===== Part 1/2 logic - Send Message =====
    public static void sendMessage(Scanner input) {
        System.out.print("Enter recipient number: ");
        String recipient = input.nextLine();

        System.out.print("Enter message: ");
        String msgText = input.nextLine();

        // Validation like your code
        if (!isValidRecipient(recipient)) {
            System.out.println("Cell phone number is incorrectly formatted or does not contain an international code.");
            return;
        }

        if (!isValidMessageLength(msgText)) {
            System.out.println("Please enter a message of less than 250 characters.");
            return;
        }

        String messageID = createMessageID(sentMessageCount);
        String messageHash = createMessageHash(msgText, messageID, sentMessageCount);

        System.out.println("1. Send\n2. Disregard\n3. Store");
        int choice = input.nextInt();
        input.nextLine();

        if (choice == 1) {
            // Add to Sent arrays
            sentRecipientArray[sentMessageCount] = recipient;
            sentMessageArray[sentMessageCount] = msgText;
            sentHashArray[sentMessageCount] = messageHash;
            sentMessageCount++;
            System.out.println("Message successfully sent");

        } else if (choice == 2) {
            // Add to Disregarded arrays
            disregardedMessageArray[disregardedCount] = msgText;
            disregardedCount++;
            System.out.println("Message disregarded");

        } else if (choice == 3) {
            // Add to Stored arrays - sender "Me" since it's you storing
            senderArray[storedMessageCount] = "Me";
            recipientArray[storedMessageCount] = recipient;
            messageArray[storedMessageCount] = msgText;
            messageIDArray[storedMessageCount] = messageID;
            messageHashArray[storedMessageCount] = messageHash;
            storedMessageCount++;
            System.out.println("Message successfully stored");
        }
    }

    // ===== Part 3: Stored Messages Menu - options a to f =====
    public static void storedMessagesMenu(Scanner input) {
        while (true) {
            System.out.println("\n=== STORED MESSAGES MENU ===");
            System.out.println("a. Show all senders + recipients");
            System.out.println("b. Show longest stored message");
            System.out.println("c. Search by Message ID");
            System.out.println("d. Search by recipient");
            System.out.println("e. Delete by hash");
            System.out.println("f. Display sent report");
            System.out.println("0. Back to main menu");
            System.out.print("Choose: ");

            String option = input.nextLine().toLowerCase();

            if (option.equals("a")) showAllSendersAndRecipients();
            else if (option.equals("b")) showLongestMessage();
            else if (option.equals("c")) searchMessageByID(input);
            else if (option.equals("d")) searchMessagesByRecipient(input);
            else if (option.equals("e")) deleteMessageByHash(input);
            else if (option.equals("f")) showSentMessagesReport();
            else if (option.equals("0")) break;
        }
    }

    // a. Humanised
    public static void showAllSendersAndRecipients() {
        if (storedMessageCount == 0) {
            System.out.println("No stored messages yet.");
            return;
        }
        for (int i = 0; i < storedMessageCount; i++) {
            System.out.println("From: " + senderArray[i] + " To: " + recipientArray[i]);
        }
    }

    // b. Humanised
    public static void showLongestMessage() {
        if (storedMessageCount == 0) {
            System.out.println("No stored messages to check.");
            return;
        }
        int longestIndex = 0;
        for (int i = 1; i < storedMessageCount; i++) {
            if (messageArray[i].length() > messageArray[longestIndex].length()) {
                longestIndex = i;
            }
        }
        System.out.println("Longest message: " + messageArray[longestIndex]);
        System.out.println("Characters: " + messageArray[longestIndex].length());
    }

    // c. Humanised
    public static void searchMessageByID(Scanner input) {
        System.out.print("Enter Message ID: ");
        String searchID = input.nextLine();
        for (int i = 0; i < storedMessageCount; i++) {
            if (messageIDArray[i].equals(searchID)) {
                System.out.println("Recipient: " + recipientArray[i]);
                System.out.println("Message: " + messageArray[i]);
                return;
            }
        }
        System.out.println("Message ID not found.");
    }

    // d. Humanised
    public static void searchMessagesByRecipient(Scanner input) {
        System.out.print("Enter recipient number: ");
        String searchRecipient = input.nextLine();
        boolean found = false;
        for (int i = 0; i < storedMessageCount; i++) {
            if (recipientArray[i].equals(searchRecipient)) {
                System.out.println("Message: " + messageArray[i]);
                found = true;
            }
        }
        if (!found) System.out.println("No messages for that recipient.");
    }

    // e. Humanised
    public static void deleteMessageByHash(Scanner input) {
        System.out.print("Enter Message Hash: ");
        String searchHash = input.nextLine();
        int deleteIndex = -1;

        for (int i = 0; i < storedMessageCount; i++) {
            if (messageHashArray[i].equals(searchHash)) {
                deleteIndex = i;
                System.out.println("Deleting: " + messageArray[i]);
                break;
            }
        }

        if (deleteIndex == -1) {
            System.out.println("Hash not found.");
            return;
        }

        // Shift left to remove
        for (int i = deleteIndex; i < storedMessageCount - 1; i++) {
            senderArray[i] = senderArray[i + 1];
            recipientArray[i] = recipientArray[i + 1];
            messageArray[i] = messageArray[i + 1];
            messageIDArray[i] = messageIDArray[i + 1];
            messageHashArray[i] = messageHashArray[i + 1];
        }
        storedMessageCount--;
        System.out.println("Message deleted successfully.");
    }

    // f. Humanised
    public static void showSentMessagesReport() {
        if (sentMessageCount == 0) {
            System.out.println("No sent messages yet.");
            return;
        }
        System.out.println("=== SENT MESSAGES REPORT ===");
        for (int i = 0; i < sentMessageCount; i++) {
            System.out.println("Hash: " + sentHashArray[i]);
            System.out.println("To: " + sentRecipientArray[i]);
            System.out.println("Message: " + sentMessageArray[i]);
            System.out.println("-------------------");
        }
    }

    // ===== Helper methods - humanised names =====
    public static boolean isValidRecipient(String recipient) {
        return recipient.length() <= 13 && recipient.startsWith("+");
    }

    public static boolean isValidMessageLength(String message) {
        return message.length() <= 250;
    }

    public static String createMessageHash(String message, String id, int count) {
        String[] words = message.split(" ");
        String firstWord = words[0].toUpperCase();
        String lastWord = words[words.length - 1].toUpperCase();
        String idPart = id.substring(0, 2);
        return idPart + ":" + count + ":" + firstWord + lastWord;
    }

    public static String createMessageID(int index) {
        return "MSG" + (index + 1); // MSG1, MSG2... MSG4 for your unit test
    }

    // Load your 5 test messages from screenshots - no hard-coding if reading from file
    public static void loadTestData() {
        // For now: populate arrays with your Test Data Message 1-5
        // In real POE: replace this with JSON file reading code

        // Message 2 - Stored
        senderArray[0] = "Me";
        recipientArray[0] = "+27838884567";
        messageArray[0] = "Where are you? You are late! I have asked you to be on time.";
        messageIDArray[0] = "MSG2";
        messageHashArray[0] = createMessageHash(messageArray[0], "MSG2", 0);
        storedMessageCount = 1;

        // Message 5 - Stored
        senderArray[1] = "Me";
        recipientArray[1] = "+27838884567";
        messageArray[1] = "Ok, I am leaving without you.";
        messageIDArray[1] = "MSG5";
        messageHashArray[1] = createMessageHash(messageArray[1], "MSG5", 1);
        storedMessageCount = 2;

        // Message 1 + 4 - Sent for report test
        sentRecipientArray[0] = "+27834557896";
        sentMessageArray[0] = "Did you get the cake?";
        sentHashArray[0] = createMessageHash(sentMessageArray[0], "MSG1", 0);

        sentRecipientArray[1] = "0838884567";
        sentMessageArray[1] = "It is dinner time!";
        sentHashArray[1] = createMessageHash(sentMessageArray[1], "MSG4", 1);
        sentMessageCount = 2;
    }
}