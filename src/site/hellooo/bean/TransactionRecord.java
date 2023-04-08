package cn.alpha2j.bean;

/**
 * Created by cn.alpha2j on 2016/11/24.
 * <p>
 * 记录借书者和图书的对应关系
 */
public class TransactionRecord {

    private String customerId;
    private String customerName;
    private String isbn;
    private String bookName;
    private String borrowTime;  //借书日期
    private String returnTime;  //还书日期

    public TransactionRecord() {

    }

    public TransactionRecord(String customerId, String customerName, String isbn, String bookName, String borrowTime, String returnTime) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.isbn = isbn;
        this.bookName = bookName;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(String borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof TransactionRecord)) {
            return false;
        }

        TransactionRecord tr = (TransactionRecord) o;

        return tr.customerId.equals(customerId)
                && tr.customerName.equals(customerName)
                && tr.isbn.equals(isbn)
                && tr.bookName.equals(bookName)
                && tr.borrowTime.equals(borrowTime)
                && tr.returnTime.equals(returnTime);
    }

    @Override
    public int hashCode() {
        return customerId.hashCode()
                + customerName.hashCode()
                + isbn.hashCode()
                + bookName.hashCode()
                + borrowTime.hashCode()
                + returnTime.hashCode();
    }
}
