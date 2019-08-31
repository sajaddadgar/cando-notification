package rahnema.domain;

public class BookmarkDomain {
    long dueDate;
    long auctionId;
    String email;

    public long getDueDate() {
        return dueDate;
    }

    public BookmarkDomain setDueDate(long dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public long getAuctionId() {
        return auctionId;
    }

    public BookmarkDomain setAuctionId(long auctionId) {
        this.auctionId = auctionId;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public BookmarkDomain setEmail(String email) {
        this.email = email;
        return this;
    }
}
