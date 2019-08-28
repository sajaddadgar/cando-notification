package rahnema.domain;

public class BookmarkDomain {
    long dueDate;
    long auctionId;

    public long getDueDate() {
        return dueDate;
    }

    public long getAuctionId() {
        return auctionId;
    }

    public BookmarkDomain setDueDate(long dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public BookmarkDomain setAuctionId(long auctionId) {
        this.auctionId = auctionId;
        return this;
    }
}
