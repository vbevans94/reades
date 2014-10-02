package ua.org.cofriends.reades.entity;

import com.orm.SugarRecord;

public class Page extends SugarRecord<Page> {

    private final String content;

    private final int pageNumber;

    private final Book book;

    public Page(String content, int pageNumber, Book book) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.book = book;
    }

    public Page() {
        this(null, 0, null);
    }

    public Book getBook() {
        return book;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Page{" +
                "content='" + content + '\'' +
                ", pageNumber=" + pageNumber +
                ", book=" + book +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        if (pageNumber != page.pageNumber) return false;
        if (book != null ? !book.equals(page.book) : page.book != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pageNumber;
        result = 31 * result + (book != null ? book.hashCode() : 0);
        return result;
    }
}
