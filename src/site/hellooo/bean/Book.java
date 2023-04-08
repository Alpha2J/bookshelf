package cn.alpha2j.bean;

/**
 * Created by cn.alpha2j on 2016/11/22.
 */
public class Book {

    private String isbn;
    private String bookName;
    private String author;
    private String press;
    private String totalNum;//书籍总数量

    public Book() {

    }

    public Book(String isbn, String bookName, String author, String press, String totalNum) {
        this.isbn = isbn;
        this.bookName = bookName;
        this.author = author;
        this.press = press;
        this.totalNum = totalNum;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }
}