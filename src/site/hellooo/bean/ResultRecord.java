package cn.alpha2j.bean;

/**
 * Created by cn.alpha2j on 2016/11/30.
 */
public class ResultRecord implements Comparable<ResultRecord> {

    private String isbn;
    private String bookName;
    private int historyBorrowNum;

    public ResultRecord() {

    }

    public ResultRecord(String isbn, String bookName) {
        this.isbn = isbn;
        this.bookName = bookName;
    }

    public ResultRecord(String isbn, String bookName, int historyBorrowNum) {
        this.isbn = isbn;
        this.bookName = bookName;
        this.historyBorrowNum = historyBorrowNum;
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

    public int getHistoryBorrowNum() {
        return historyBorrowNum;
    }

    public void setHistoryBorrowNum(int historyBorrowNum) {
        this.historyBorrowNum = historyBorrowNum;
    }


    /**
     * 只要isbn 和 bookName 相同就认为这是相同的记录
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ResultRecord)) {
            return false;
        }

        ResultRecord rr = (ResultRecord) o;

        return rr.isbn.equals(isbn)
                && rr.bookName.equals(bookName);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode() + bookName.hashCode();
    }

    @Override
    public int compareTo(ResultRecord o) {
        if (historyBorrowNum < o.historyBorrowNum) {
            return -1;
        } else if (historyBorrowNum == o.historyBorrowNum) {
            return 0;
        } else {
            return 1;
        }
    }
}
