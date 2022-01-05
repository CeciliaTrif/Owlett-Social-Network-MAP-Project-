package map.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import map.socialnetwork.views.wrapper.UserTableItemWrapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
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
    public Button addButton;
    @FXML
    public Label logInStatus;
    @FXML
    public Button acceptButton;
    @FXML
    public Button openChatButton;
    @FXML
    private Button logOutButton;
    @FXML
    private TableView<UserTableItemWrapper> tableView;
    @FXML
    public Button deleteButton;
    private Long userId;
    @FXML
    public TextField friendUsername;
    @FXML
    public Label sendRequestMessage;
    private final UserDatabaseRepository userDatabaseRepository = new UserDatabaseRepository(new UserValidator());
    private final FriendshipDatabaseRepository friendshipDatabaseRepository = new FriendshipDatabaseRepository(new FriendshipValidator(), userDatabaseRepository);

    @Override
    public void initializeData(Map<String, Object> parameters) {
        userId = (Long) parameters.get("userId");
        logInStatus.setText("Logged in as: " + userDatabaseRepository.findOne(userId).getUsername());
        tableView.setItems(TABLE_ITEMS);
    }

    public void userLogOut(ActionEvent event) throws IOException {
        Main.switchScene("login-view.fxml");
    }

    public void sendRequest(ActionEvent event) throws IOException {
        resetVisibility();
        sendRequestMessage.setVisible(true);
        sendRequestMessage.setText("Enter the username of the person you want to connect with:");
        addButton.setVisible(true);
        friendUsername.setVisible(true);
    }

    public void addFriends(ActionEvent event) throws IOException {
        if (checkUsername()) {
            User friend = userDatabaseRepository.findOneByUsername(friendUsername.getText());
            LocalDateTime now = LocalDateTime.now();
            Timestamp date = Timestamp.valueOf(now);

            Friendship friendship = new Friendship(userId, friend.getId(), date);
            assert friendshipService != null;
            friendship.setId(friendshipDatabaseRepository.generateNextID());
            friendshipService.addToRepo(friendship);
            sendRequestMessage.setText("Friend request sent!");
        }
        addButton.setVisible(false);
        friendUsername.setVisible(false);
    }

    private boolean checkUsername() throws ValidationException, IOException {
        User friend = userDatabaseRepository.findOneByUsername(friendUsername.getText());

        if (userDatabaseRepository.getUsername(friendUsername.getText()) == null) {
            sendRequestMessage.setText("User does not exist!");
            return false;
        } else if (friendshipDatabaseRepository.findOneByUser(userId, friend.getId()) != null) {
            sendRequestMessage.setText("A request between the users already pending!");
            return false;
        }
        return true;
    }

    public void showFriends(ActionEvent event) throws IOException {
        resetVisibility();
        deleteButton.setVisible(true);

        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("Username", "userName");
        columnsDescription.put("Date", "date");

        buildTableColumns(columnsDescription);

        setFriendshipsAsTableItems();
    }

    public void showFriendRequests(ActionEvent event) {
        resetVisibility();
        deleteButton.setVisible(true);
        acceptButton.setVisible(true);
        Map<String, String> columnsDescription = new LinkedHashMap<>();
        //The key must be the column Name and the value must be the attribute from the UserTableItemWrapper class
        columnsDescription.put("Username", "userName");
        columnsDescription.put("Date", "date");
        columnsDescription.put("Status", "status");

        buildTableColumns(columnsDescription);

        setFriendRequestAsTableItems();
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

    public void handleChat(ActionEvent event) {
        UserTableItemWrapper selectedItem = tableView.getSelectionModel().getSelectedItem();

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

    private void setFriendshipsAsTableItems() {
        assert friendshipService != null;
        List<UserTableItemWrapper> items = friendshipService.getAll()
                .stream()
                .map(friendship -> {
                    UserTableItemWrapper userTableItemWrapper = new UserTableItemWrapper();
                    userTableItemWrapper.setUserName(userDatabaseRepository.findOne(friendship.getUserID1()).getUsername());
                    userTableItemWrapper.setDate(YYYY_MM_DD_DATE_FORMAT.format(friendship.getFriendshipStartDate()));
                    return userTableItemWrapper;
                })
                .collect(Collectors.toList());

        TABLE_ITEMS.setAll(items);
    }

    private void resetVisibility() {
        acceptButton.setVisible(false);
        addButton.setVisible(false);
        deleteButton.setVisible(false);
        tableView.setVisible(false);
        sendRequestMessage.setVisible(false);
        friendUsername.setVisible(false);
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
