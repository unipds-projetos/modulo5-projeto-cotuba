package br.com.unipds.ebook.chapter;

import java.nio.file.Path;

public class Chapter {

    private String title;
    private Path fileMarkdown;
    private String markdown;
    private String html;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Path getFileMarkdown() {
        return fileMarkdown;
    }

    public void setFileMarkdown(Path fileMarkdown) {
        this.fileMarkdown = fileMarkdown;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

}
