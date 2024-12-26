package marlon.dev.bussing.ui.history;

public class CardHistoryLists {

    private int TransactionTypeIcon;
    private String TransactionType;
    private String TransactionDate;

    public CardHistoryLists(int transactionTypeIcon, String transactionType, String transactionDate) {
        this.TransactionTypeIcon = transactionTypeIcon;
        this.TransactionType = transactionType;
        this.TransactionDate = transactionDate;
    }

    public int getTransactionTypeIcon() {
        return TransactionTypeIcon;
    }

    public void setTransactionTypeIcon(int transactionTypeIcon) {
        TransactionTypeIcon = transactionTypeIcon;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

}