package map.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import map.socialnetwork.Main;
import map.socialnetwork.domain.model.Friendship;
import map.socialnetwork.domain.model.Message;
import map.socialnetwork.domain.model.User;
import map.socialnetwork.domain.validator.FriendshipValidator;
import map.socialnetwork.domain.validator.UserValidator;
import map.socialnetwork.domain.validator.ValidationException;
import map.socialnetwork.repository.database.FriendshipDatabaseRepository;
import map.socialnetwork.repository.database.UserDatabaseRepository;
import map.socialnetwork.service.FriendshipService;
import map.socialnetwork.service.GroupChatService;
import map.socialnetwork.service.MessageService;
import map.socialnetwork.service.UserService;
import map.socialnetwork.util.Container;
import map.socialnetwork.views.wrapper.UserTableItemWrapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MainController implements InitializableController {

    private final UserService userService = (UserService) Container.getService("user");
    private final FriendshipService friendshipService = (FriendshipService) Container.getService("friendship");
    private final MessageService messageService = (MessageService) Container.getService("message");
    private final GroupChatService groupChatService = (GroupChatService) Container.getService("groupChat");
    private static final DateFormat YYYY_MM_DD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final ObservableList<UserTableItemWrapper> TABLE_ITEMS = FXCollections.observableArrayList();
    @FXML
    public Button showFriendsButton;
    @FXML
    public Button showFriendRequestsButton;
    @FXML
    public Button addFriendsButton;
    @FXML
    public Label loggedInAsUsernameLabel;
    @FXML
    public Button acceptButton;
    @FXML
    public Button sentRequestsButton;
    @FXML
    public Button cancelRequestButton;
    @FXML
    public Button showProfileButton;
    @FXML
    public Button showChatsButton;
    @FXML
    public Label loggedInAsName;
    @FXML
    public Label loggedInAsLastName;
    @FXML
    public Label sendRequestNotifyMessageLabel;
    @FXML
    public Button sendFriendRequestButton;
    @FXML
    public Button groupChatsButton;
    @FXML
    public Button conversationsButton;
    @FXML
    public Button conversationOpenButton;
    @FXML
    public Button conversationCloseButton;
    @FXML
    public Button sendMessageButton;
    @FXML
    public TextField sendMessageText;
    @FXML
    public Button openGroupChatCreationButton;
    @FXML
    public TextField groupChatName;
    @FXML
    public Button createGroupChatButton;
    @FXML
    public Button showFriendsButton1;
    @FXML
    public Button declineButton;
    @FXML
    public Button openGroupConversationButton;
    @FXML
    public ImageView welcomeImage;
    @FXML
    private Button logOutButton;
    @FXML
    private TableView<UserTableItemWrapper> tableView;
    @FXML
    public Button deleteButton;
    @FXML
    public ScrollPane chatScrollPane;
    @FXML
    public VBox chatTextArea;
    private Long userId;
    private final UserDatabaseRepository userDatabaseRepository = new UserDatabaseRepository(new UserValidator());
    private final FriendshipDatabaseRepository friendshipDatabaseRepository = new FriendshipDatabaseRepository(new FriendshipValidator(), userDatabaseRepository);

    @Override
    public void initializeData(Map<String, Object> parameters) {
        userId = (Long) parameters.get("userId");
        loggedInAsUsernameLabel.setText("Logged in as:\n" + userDatabaseRepository.findOne(userId).getUsername());
        loggedInAsName.setText(userDatabaseRepository.findOne(userId).getFirstName() + " " + userDatabaseRepository.findOne(userId).getLastName());
        tableView.setItems(TABLE_ITEMS);
    }

    public void userLogOut(ActionEvent event) throws IOException {
        Main.switchScene("login-view.fxml");
    }

    private boolean checkExistingFriendRequest(String newFriend) throws ValidationException {
        User friend = userDatabaseRepository.findOneByUsername(newFriend);
        if (friendshipDatabaseRepository.findOneByUser(userId, friend.getId()) != null) {
            sendRequestNotifyMessageLabel.setText("A friendship request already exists between you and " + friend.getFirstName() + "!");
            sendRequestNotifyMessageLabel.setVisible(true);
            return false;
        }
        if (userId.equals(friend.getId())) {
            sendRequestNotifyMessageLabel.setText("Can't send friend request to your own self!");
            sendRequestNotifyMessageLabel.setVisible(true);
            return false;
        }
        return true;
    }

    public void showProfile(ActionEvent event) {
        resetVisibility();
        loggedInAsUsernameLabel.setVisible(true);
        loggedInAsName.setVisible(true);
        logOutButton.setVisible(true);
        welcomeImage.setVisible(true);
    }

    public void showChats(ActionEvent event) {
        resetVisibility();
        conversationsButton.setVisible(true);
        groupChatsButton.setVisible(true);
        openGroupChatCreationButton.setVisible(true);
    }

    public void showConversations(ActionEvent event) {
        resetVisibility();
        conversationsButton.setVisible(true);
        groupChatsButton.setVisible(true);
        openGroupChatCreationButton.setVisible(true);
        conversationOpenButton.setVisible(true);
        conversationCloseButton.setVisible(true);
        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("Username", "userName");
        columnsDescription.put("First Name", "firstName");
        columnsDescription.put("Last Name", "lastName");
        columnsDescription.put("Date", "date");

        buildTableColumns(columnsDescription);

        setFriendshipsAsTableItems();
    }

    public void openConversation(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            assert messageService != null;
            List<Message> messages = messageService.getMessagesByParticipantIds(selectedItem.getId(), userId);
            assert userService != null;
            User selectedUser = userService.findById(selectedItem.getId());
            User loggedInUser = userService.findById(userId);
            chatTextArea.getChildren().clear();
            for (Message message : messages) {
                Label currentLabel;
                if (message.getSenderID().equals(selectedUser.getId())) {
                    currentLabel = new Label(selectedUser.getUsername() + ": " + message.getMessage() + "\n");
                    currentLabel.setAlignment(Pos.CENTER_LEFT);

                } else {
                    currentLabel = new Label(loggedInUser.getUsername() + ": " + message.getMessage() + "\n");
                    currentLabel.setAlignment(Pos.CENTER_RIGHT);
                }
                currentLabel.setStyle("-fx-pref-width:424px; ");
                currentLabel.setWrapText(true);
                chatTextArea.getChildren().add(currentLabel);
            }
            tableView.setDisable(true);
            chatScrollPane.setVisible(true);
            chatTextArea.setVisible(true);
            sendMessageText.setVisible(true);
            sendMessageButton.setVisible(true);
        } else {
            sendRequestNotifyMessageLabel.setText("Please make a selection!");
            sendRequestNotifyMessageLabel.setVisible(true);
        }

    }

    public void closeConversation(ActionEvent event) {
        tableView.setDisable(false);
        chatScrollPane.setVisible(false);
        chatTextArea.setVisible(false);
        sendMessageText.setVisible(false);
        sendMessageButton.setVisible(false);
    }

    public void sendMessage(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        Message message = new Message(userId, selectedItem.getId(), LocalDateTime.now(), sendMessageText.getText());
        assert messageService != null;
        messageService.sendMessage(message);
        assert userService != null;
        if (userService.findById(selectedItem.getId()) == null) {
            openGroupConversation(null);
        } else {
            openConversation(null);
        }
        sendMessageText.clear();
    }

    public void showGroupChats(ActionEvent event) {
        resetVisibility();
        conversationsButton.setVisible(true);
        groupChatsButton.setVisible(true);
        openGroupChatCreationButton.setVisible(true);
        conversationCloseButton.setVisible(true);
        openGroupConversationButton.setVisible(true);

        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("Name", "userName");

        buildTableColumns(columnsDescription);

        setGroupChatsAsTableItems();
    }

    public void openGroupChatCreationPage(ActionEvent event) {
        resetVisibility();
        conversationsButton.setVisible(true);
        groupChatsButton.setVisible(true);
        openGroupChatCreationButton.setVisible(true);
        createGroupChatButton.setVisible(true);
        groupChatName.setVisible(true);
        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("Username", "userName");
        columnsDescription.put("First Name", "firstName");
        columnsDescription.put("Last Name", "lastName");

        buildTableColumns(columnsDescription);

        setFriendshipsAsTableItems();
        tableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

    }

    public void openGroupConversation(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            assert messageService != null;
            assert userService != null;
            assert groupChatService != null;
            List<Message> messages = messageService.getMessagesByReceiverId(selectedItem.getId());
            Map<Long, User> participants = groupChatService.getAllGroupChatParticipants(selectedItem.getId());

            User loggedInUser = userService.findById(userId);
            chatTextArea.getChildren().clear();
            for (Message message : messages) {
                Label currentLabel;
                if (message.getSenderID().equals(loggedInUser.getId())) {
                    currentLabel = new Label(loggedInUser.getUsername() + ": " + message.getMessage() + "\n");
                    currentLabel.setAlignment(Pos.CENTER_RIGHT);

                } else {
                    User messageSender = participants.get(message.getSenderID());
                    currentLabel = new Label(messageSender.getUsername() + ": " + message.getMessage() + "\n");
                    currentLabel.setAlignment(Pos.CENTER_LEFT);
                }
                currentLabel.setStyle("-fx-pref-width:424px; ");
                currentLabel.setWrapText(true);
                chatTextArea.getChildren().add(currentLabel);
            }
            chatScrollPane.setVisible(true);
            chatTextArea.setVisible(true);
            sendMessageText.setVisible(true);
            sendMessageText.clear();
            sendMessageButton.setVisible(true);
            tableView.setDisable(true);
        } else {
            sendRequestNotifyMessageLabel.setText("Please make a selection!");
            sendRequestNotifyMessageLabel.setVisible(true);
        }
    }

    public void createGroupChat(ActionEvent event) {
        String groupName = groupChatName.getText();
        if (!groupName.isBlank()) {
            List<Long> groupParticipantIds = tableView.getSelectionModel().getSelectedItems()
                    .stream()
                    .map(UserTableItemWrapper::getId)
                    .collect(Collectors.toList());
            if (!groupParticipantIds.isEmpty()) {
                groupParticipantIds.add(userId);
                assert groupChatService != null;
                groupChatService.createGroupChat(groupName, groupParticipantIds);
            } else {
                sendRequestNotifyMessageLabel.setText("Please make a selection!");
                sendRequestNotifyMessageLabel.setVisible(true);
            }
        } else {
            sendRequestNotifyMessageLabel.setText("Please enter a group name!");
            sendRequestNotifyMessageLabel.setVisible(true);
        }
    }

    public void showUsers(ActionEvent event) throws IOException {
        resetVisibility();
        addFriendsButton.setVisible(true);
        showFriendRequestsButton.setVisible(true);
        sentRequestsButton.setVisible(true);
        showFriendsButton1.setVisible(true);
        sendFriendRequestButton.setVisible(true);

        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("Username", "userName");
        columnsDescription.put("First Name", "firstName");
        columnsDescription.put("Last Name", "lastName");

        buildTableColumns(columnsDescription);

        setUsersAsTableItems();
    }

    public void showFriends(ActionEvent event) throws IOException {
        resetVisibility();

        addFriendsButton.setVisible(true);
        deleteButton.setVisible(true);
        showFriendRequestsButton.setVisible(true);
        showFriendsButton1.setVisible(true);
        sentRequestsButton.setVisible(true);

        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("Username", "userName");
        columnsDescription.put("First Name", "firstName");
        columnsDescription.put("Last Name", "lastName");
        columnsDescription.put("Date", "date");

        buildTableColumns(columnsDescription);

        setFriendshipsAsTableItems();
    }

    public void showFriendRequests(ActionEvent event) {
        resetVisibility();
        addFriendsButton.setVisible(true);
        showFriendsButton1.setVisible(true);
        sentRequestsButton.setVisible(true);
        showFriendRequestsButton.setVisible(true);
        declineButton.setVisible(true);
        acceptButton.setVisible(true);
        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("From", "userName");
        columnsDescription.put("Date", "date");
        columnsDescription.put("Status", "status");

        buildTableColumns(columnsDescription);

        setFriendRequestAsTableItems();
    }

    public void showSentRequests(ActionEvent event) {
        resetVisibility();
        addFriendsButton.setVisible(true);
        showFriendRequestsButton.setVisible(true);
        sentRequestsButton.setVisible(true);
        showFriendsButton1.setVisible(true);
        cancelRequestButton.setVisible(true);
        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("Sent to", "userName");
        columnsDescription.put("Date", "date");
        columnsDescription.put("Status", "status");

        buildTableColumns(columnsDescription);

        setSentRequestAsTableItems();
    }

    public void sendFriendRequest(ActionEvent event) {
        sendRequestNotifyMessageLabel.setText("");
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            User newFriend = userDatabaseRepository.findOneByUsername(selectedItem.getUserName());
            if (checkExistingFriendRequest(newFriend.getUsername())) {
                LocalDateTime now = LocalDateTime.now();
                Timestamp date = Timestamp.valueOf(now);
                Friendship newFriendRequest = new Friendship(userId, newFriend.getId(), date);
                assert friendshipService != null;
                newFriendRequest.setId(friendshipDatabaseRepository.generateNextID());
                friendshipService.addToRepo(newFriendRequest);
                sendRequestNotifyMessageLabel.setText("Friend request sent!");
                sendRequestNotifyMessageLabel.setVisible(true);
            }
        } else {
            sendRequestNotifyMessageLabel.setText("Please make a selection!");
            sendRequestNotifyMessageLabel.setVisible(true);
        }
    }

    public void handleDelete(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Friendship friendship = friendshipDatabaseRepository.findOneByUser(userDatabaseRepository.findOneByUsername(selectedItem.getUserName()).getId(), userId);
            friendshipDatabaseRepository.delete(friendship.getId());
            setFriendRequestAsTableItems();
        } else {
            sendRequestNotifyMessageLabel.setText("Please make a selection!");
            sendRequestNotifyMessageLabel.setVisible(true);
        }
    }

    public void handleAccept(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Friendship friendship = friendshipDatabaseRepository.findOneByUser(userDatabaseRepository.findOneByUsername(selectedItem.getUserName()).getId(), userId);
            friendshipDatabaseRepository.update(friendship);
            setFriendRequestAsTableItems();
        } else {
            sendRequestNotifyMessageLabel.setText("Please make a selection!");
            sendRequestNotifyMessageLabel.setVisible(true);
        }
    }

    public void handleCancelRequest(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem!=null) {
            Friendship friendship = friendshipDatabaseRepository.findOneByUser(userDatabaseRepository.findOneByUsername(selectedItem.getUserName()).getId(), userId);
            friendshipDatabaseRepository.delete(friendship.getId());
            setSentRequestAsTableItems();
        } else {
            sendRequestNotifyMessageLabel.setText("Please make a selection!");
            sendRequestNotifyMessageLabel.setVisible(true);
        }
    }

    private void setUsersAsTableItems() {
        assert userService != null;
        List<UserTableItemWrapper> items = userService.getAll()
                .stream()
                .map(user -> {
                    UserTableItemWrapper userTableItemWrapper = new UserTableItemWrapper();
                    userTableItemWrapper.setUserName(user.getUsername());
                    userTableItemWrapper.setFirstName(user.getFirstName());
                    userTableItemWrapper.setLastName(user.getLastName());
                    return userTableItemWrapper;
                })
                .collect(Collectors.toList());

        TABLE_ITEMS.setAll(items);
    }

    private void setFriendRequestAsTableItems() {
        assert friendshipService != null;
        List<UserTableItemWrapper> items = friendshipService.getRequestsByID(userId)
                .stream()
                .map(request -> {
                    UserTableItemWrapper userTableItemWrapper = new UserTableItemWrapper();
                    userTableItemWrapper.setUserName(userDatabaseRepository.findOne(request.getUserID1()).getUsername());
                    userTableItemWrapper.setDate(YYYY_MM_DD_DATE_FORMAT.format(request.getFriendshipStartDate()));
                    userTableItemWrapper.setStatus("pending");
                    return userTableItemWrapper;
                })
                .collect(Collectors.toList());

        TABLE_ITEMS.setAll(items);
    }

    private void setSentRequestAsTableItems() {
        assert friendshipService != null;
        List<UserTableItemWrapper> items = friendshipService.getRequestsFrom(userId)
                .stream()
                .map(sentRequest -> {
                    UserTableItemWrapper userTableItemWrapper = new UserTableItemWrapper();
                    userTableItemWrapper.setUserName(userDatabaseRepository.findOne(sentRequest.getUserID2()).getUsername());
                    userTableItemWrapper.setDate(YYYY_MM_DD_DATE_FORMAT.format(sentRequest.getFriendshipStartDate()));
                    userTableItemWrapper.setStatus("pending");
                    return userTableItemWrapper;
                })
                .collect(Collectors.toList());

        TABLE_ITEMS.setAll(items);
    }

    private void setFriendshipsAsTableItems() {
        assert friendshipService != null;
        List<UserTableItemWrapper> items = friendshipService.getAllByUserId(userId)
                .stream()
                .map(friendship -> {
                    final Long friendId = friendship.getUserID1().equals(userId) ? friendship.getUserID2() : friendship.getUserID1();
                    UserTableItemWrapper userTableItemWrapper = new UserTableItemWrapper();
                    final User user = userDatabaseRepository.findOne(friendId);
                    userTableItemWrapper.setId(user.getId());
                    userTableItemWrapper.setUserName(user.getUsername());
                    userTableItemWrapper.setLastName(user.getLastName());
                    userTableItemWrapper.setFirstName(user.getFirstName());
                    userTableItemWrapper.setDate(YYYY_MM_DD_DATE_FORMAT.format(friendship.getFriendshipStartDate()));
                    return userTableItemWrapper;
                })
                .collect(Collectors.toList());

        TABLE_ITEMS.setAll(items);
    }

    private void setGroupChatsAsTableItems() {
        assert groupChatService != null;
        List<UserTableItemWrapper> items = groupChatService.getAllByUserId(userId)
                .stream()
                .map(groupChat -> {
                    UserTableItemWrapper userTableItemWrapper = new UserTableItemWrapper();
                    userTableItemWrapper.setId(groupChat.getId());
                    userTableItemWrapper.setUserName(groupChat.getName());
                    return userTableItemWrapper;
                })
                .collect(Collectors.toList());

        TABLE_ITEMS.setAll(items);
    }

    private void resetVisibility() {
        openGroupConversationButton.setVisible(false);
        showFriendsButton1.setVisible(false);
        openGroupChatCreationButton.setVisible(false);
        createGroupChatButton.setVisible(false);
        conversationsButton.setVisible(false);
        groupChatsButton.setVisible(false);
        createGroupChatButton.setVisible(false);
        groupChatName.setVisible(false);
        sendFriendRequestButton.setVisible(false);
        showFriendRequestsButton.setVisible(false);
        sendRequestNotifyMessageLabel.setVisible(false);
        sentRequestsButton.setVisible(false);
        addFriendsButton.setVisible(false);
        loggedInAsName.setVisible(false);
        logOutButton.setVisible(false);
        welcomeImage.setVisible(false);
        declineButton.setVisible(false);
        loggedInAsUsernameLabel.setVisible(false);
        cancelRequestButton.setVisible(false);
        conversationOpenButton.setVisible(false);
        conversationCloseButton.setVisible(false);
        acceptButton.setVisible(false);
        deleteButton.setVisible(false);
        chatScrollPane.setVisible(false);
        sendMessageButton.setVisible(false);
        tableView.setVisible(false);
        sendMessageText.setVisible(false);
        tableView.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );
        tableView.setDisable(false);
    }

    private void buildTableColumns(Map<String, String> columnsDescription) {
        tableView.getColumns().clear();
        List<TableColumn<UserTableItemWrapper, String>> columns = new ArrayList<>();
        for (Map.Entry<String, String> entry : columnsDescription.entrySet()) {
            TableColumn<UserTableItemWrapper, String> newColumn = new TableColumn<>(entry.getKey());
            newColumn.setMinWidth(80);
            newColumn.setCellValueFactory(new PropertyValueFactory<>(entry.getValue()));
            newColumn.setEditable(false);
            columns.add(newColumn);
        }
        if (!columns.isEmpty()) {
            tableView.getColumns().addAll(columns);
            tableView.setVisible(true);
        } else {
            tableView.setVisible(false);
        }
    }
}
