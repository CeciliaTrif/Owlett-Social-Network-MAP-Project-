package map.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import map.socialnetwork.Main;
import map.socialnetwork.domain.model.Friendship;
import map.socialnetwork.domain.model.User;
import map.socialnetwork.domain.validator.FriendshipValidator;
import map.socialnetwork.domain.validator.UserValidator;
import map.socialnetwork.domain.validator.ValidationException;
import map.socialnetwork.repository.database.FriendshipDatabaseRepository;
import map.socialnetwork.repository.database.UserDatabaseRepository;
import map.socialnetwork.service.FriendshipService;
import map.socialnetwork.service.UserService;
import map.socialnetwork.util.Container;
import map.socialnetwork.views.ViewResolver;
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
    public Button chatButton;
    @FXML
    public Button sentRequestsButton;
    @FXML
    public Button cancelRequestButton;
    @FXML
    public Button showProfileButton;
    @FXML
    public Button showConversationsButton;
    @FXML
    public Label loggedInAsName;
    @FXML
    public Label loggedInAsLastName;
    @FXML
    public Label sendRequestNotifyMessageLabel;
    @FXML
    public Button sendFriendRequestButton;
    @FXML
    private Button logOutButton;
    @FXML
    private TableView<UserTableItemWrapper> tableView;
    @FXML
    public Button deleteButton;
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
            sendRequestNotifyMessageLabel.setText("Friend request already sent!");
            return false;
        }
        if (userId.equals(friend.getId())) {
            sendRequestNotifyMessageLabel.setText("Can't send friend request to your own self!");
            return false;
        }
        return true;
    }

    public void showProfile(ActionEvent event) {
        resetVisibility();
        loggedInAsUsernameLabel.setVisible(true);
        loggedInAsName.setVisible(true);
        logOutButton.setVisible(true);
    }

    public void showConversations(ActionEvent event) {
        resetVisibility();
    }

    public void showUsers(ActionEvent event) throws IOException {
        resetVisibility();
        addFriendsButton.setVisible(true);
        showFriendRequestsButton.setVisible(true);
        sentRequestsButton.setVisible(true);
        sendFriendRequestButton.setVisible(true);
        sendRequestNotifyMessageLabel.setVisible(true);

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
        sentRequestsButton.setVisible(true);

        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("Username", "userName");
        columnsDescription.put("Date", "date");

        buildTableColumns(columnsDescription);

        setFriendshipsAsTableItems();
    }

    public void showFriendRequests(ActionEvent event) {
        resetVisibility();
        addFriendsButton.setVisible(true);
        sentRequestsButton.setVisible(true);
        showFriendRequestsButton.setVisible(true);
        deleteButton.setVisible(true);
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
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        User newFriend = userDatabaseRepository.findOneByUsername(selectedItem.getUserName());
        if (checkExistingFriendRequest(newFriend.getUsername())) {
            LocalDateTime now = LocalDateTime.now();
            Timestamp date = Timestamp.valueOf(now);
            Friendship newFriendRequest = new Friendship(userId, newFriend.getId(), date);
            assert friendshipService != null;
            newFriendRequest.setId(friendshipDatabaseRepository.generateNextID());
            friendshipService.addToRepo(newFriendRequest);
            sendRequestNotifyMessageLabel.setText("Friend request sent!");
        }
    }

    public void handleDelete(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        Friendship friendship = friendshipDatabaseRepository.findOneByUser(userDatabaseRepository.findOneByUsername(selectedItem.getUserName()).getId(), userId);
        friendshipDatabaseRepository.delete(friendship.getId());
        setFriendRequestAsTableItems();
    }

    public void handleAccept(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        Friendship friendship = friendshipDatabaseRepository.findOneByUser(userDatabaseRepository.findOneByUsername(selectedItem.getUserName()).getId(), userId);
        friendshipDatabaseRepository.update(friendship);
        setFriendRequestAsTableItems();
    }

    public void handleCancelRequest(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
        Friendship friendship = friendshipDatabaseRepository.findOneByUser(userDatabaseRepository.findOneByUsername(selectedItem.getUserName()).getId(), userId);
        friendshipDatabaseRepository.delete(friendship.getId());
        setSentRequestAsTableItems();
    }

    public void handleChat(ActionEvent event) throws IOException {
        // TODO
        try {
//            UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ViewResolver.getView("chat-view.fxml"));

            BorderPane root = loader.load();

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat");
            chatStage.initModality(Modality.NONE);

            Scene scene = new Scene(root);
            chatStage.setScene(scene);

            chatStage.show();

        } catch (IOException e) {
            e.printStackTrace();
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
                    Long friendId = friendship.getUserID1().equals(userId) ? friendship.getUserID2() : friendship.getUserID1();
                    UserTableItemWrapper userTableItemWrapper = new UserTableItemWrapper();
                    userTableItemWrapper.setUserName(userDatabaseRepository.findOne(friendId).getUsername());
                    userTableItemWrapper.setDate(YYYY_MM_DD_DATE_FORMAT.format(friendship.getFriendshipStartDate()));
                    return userTableItemWrapper;
                })
                .collect(Collectors.toList());

        TABLE_ITEMS.setAll(items);
    }

    private void resetVisibility() {
        sendFriendRequestButton.setVisible(false);
        sendRequestNotifyMessageLabel.setVisible(false);
        addFriendsButton.setVisible(false);
        loggedInAsName.setVisible(false);
        showFriendRequestsButton.setVisible(false);
        sentRequestsButton.setVisible(false);
        logOutButton.setVisible(false);
        loggedInAsUsernameLabel.setVisible(false);
        cancelRequestButton.setVisible(false);
        chatButton.setVisible(false);
        acceptButton.setVisible(false);
        deleteButton.setVisible(false);
        tableView.setVisible(false);
    }

    private void buildTableColumns(Map<String, String> columnsDescription) {
        tableView.getColumns().clear();
        List<TableColumn<UserTableItemWrapper, String>> columns = new ArrayList<>();
        for (Map.Entry<String, String> entry : columnsDescription.entrySet()) {
            TableColumn<UserTableItemWrapper, String> newColumn = new TableColumn<>(entry.getKey());
            newColumn.setMinWidth(80);
            newColumn.setCellValueFactory(new PropertyValueFactory<>(entry.getValue()));

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
