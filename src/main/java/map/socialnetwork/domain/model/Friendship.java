package map.socialnetwork.domain.model;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class Friendship extends Entity<Long> {

    private final Long userID1;
    private final Long userID2;
    private final Timestamp friendshipStartDate;

    public Friendship(Long userID1, Long userID2, Timestamp friendshipStartDate) {
        this.friendshipStartDate = friendshipStartDate;
        this.userID1 = userID1;
        this.userID2 = userID2;
    }

    public Friendship(Friendship friendship) {
        userID1 = friendship.getUserID1();
        userID2 = friendship.getUserID2();
        friendshipStartDate = friendship.getFriendshipStartDate();
    }

    public Long getUserID1() {
        return userID1;
    }

    public Long getUserID2() {
        return userID2;
    }

    public Timestamp getFriendshipStartDate() {
        return friendshipStartDate;
    }

    @Override
    public String toString() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(friendshipStartDate);
        return "Friends:\n" +
                "User with ID: " + userID1 +
                " and User with ID: " + userID2 +
                ", since " + date +
                ".\n";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Friendship that)) return false;
        return getUserID1().equals(that.getUserID1()) && getUserID2().equals(that.getUserID2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserID1(), getUserID2());
    }
}
