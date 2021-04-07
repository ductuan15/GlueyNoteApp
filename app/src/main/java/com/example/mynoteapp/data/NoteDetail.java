package com.example.mynoteapp.data;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NoteDetail {
    private long ID;
    private String title;
    private String content;
    private String rawContent;
    private Date editDate;
    private static long nextID = 0;
    private boolean isSaved;
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private ArrayList<String> tags = new ArrayList<>();

    public NoteDetail() {
        this.ID = nextID;
        this.title = "";
        this.content = "";
        this.editDate = new Date(System.currentTimeMillis());
        this.isSaved = false;
    }

    public NoteDetail(long ID, String title, String content, Date editDate, ArrayList<String> tags) {
        this.ID = ID;
        this.title = title;
        this.content = content;
        this.editDate = editDate;
        this.tags = tags;
        this.isSaved = true;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public ArrayList<String> getAllTags() {
        return this.tags;
    }

    @NonNull
    @Override
    public String toString() {

        StringBuilder data = new StringBuilder(String.valueOf(this.ID) + "\n" + this.title + "\n" + simpleDateFormat.format(this.editDate) + "\n" +  String.valueOf(this.tags.size()) + "\n");
        for (String tag : tags) {
            data.append(tag);
            data.append("\n");
        }
        data.append(this.content);
        return data.toString();
    }

    public static NoteDetail parseString(String data) {
        String[] para = data.split("\n");
        int paraPos = 0;

        long ID = Long.parseLong(para[paraPos]);
        paraPos++;

        String title = para[paraPos];
        paraPos++;

        Date editDate = null;
        try {
            editDate = simpleDateFormat.parse(para[paraPos]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        paraPos++;

        int numTags = Integer.parseInt(para[paraPos]);
        paraPos++;

        ArrayList<String> tags = new ArrayList<String>();
        for (int i = 0; i < numTags; i++) {
            tags.add(para[paraPos]);
            paraPos++;
        }
        StringBuilder content = new StringBuilder();
        for (; paraPos < para.length; paraPos++) {
            content.append(para[paraPos]);
            content.append("\n");
        }
        if (content.length() != 0) {
            content.deleteCharAt(content.length() - 1);
        }
        NoteDetail noteDetail = new NoteDetail(ID, title, content.toString(), editDate, tags);
        noteDetail.saveRawContent();
        return noteDetail;
    }

    public static void updateID() {
        nextID += 1;
    }

    public static long getNextID() {
        return nextID;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void resetTag() {
        this.tags = new ArrayList<String>();
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved() {
        this.isSaved = true;
    }

    public void saveRawContent(){
        Document doc = Jsoup.parse(this.getContent());
        Element body = doc.body();
        this.rawContent = body.text();
    }

    public boolean isHaveString(String searchString) {
        searchString = searchString.toLowerCase();
        for (String tag : this.tags) {
            if (tag.toLowerCase().contains(searchString)) {
                return true;
            }
        }
        if (this.title.toLowerCase().contains(searchString)) {
            return true;
        }
        if (this.rawContent.toLowerCase().contains(searchString)) {
            return true;
        }
        return false;
    }
    public boolean compare(NoteDetail noteDetail) {
        if (this.content.length() != noteDetail.getContent().length() || this.title.length() != noteDetail.getTitle().length() || this.tags.size() != noteDetail.getAllTags().size()) {
            return false;
        }
        if (this.content.compareTo(noteDetail.getContent()) != 0 || this.title.compareTo(noteDetail.getTitle()) != 0)
            return false;
        for (int i = 0; i < this.tags.size(); i++) {
            if (this.tags.get(i).compareTo(noteDetail.getAllTags().get(i)) != 0) return false;
        }
        return true;
    }
}
