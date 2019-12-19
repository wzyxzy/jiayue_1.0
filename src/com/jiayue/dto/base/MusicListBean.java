package com.jiayue.dto.base;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "MusicList")
public class MusicListBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "id", isId = true, autoGen = true)
    private Long id;// 歌曲id
    @Column(name = "list_id")
    private String list_id;// 歌单id
    @Column(name = "book_id")
    private String book_id;// book_id
    @Column(name = "list_name")
    private String list_name;// 歌单名字
    @Column(name = "music_name")
    private String music_name;// 歌曲名字
    @Column(name = "save_name")
    private String save_name;// 文件名字
    @Column(name = "old_path")
    private String old_path;// 原始路径
    @Column(name = "now_path")
    private String now_path;// 现在的路径
//    @Column(name = "list_id")
//    private String list_id;// 歌单id
//    @Column(name = "list_id")
//    private String list_id;// 歌单id


    public String getSave_name() {
        return save_name;
    }

    public void setSave_name(String save_name) {
        this.save_name = save_name;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public String getMusic_name() {
        return music_name;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }

    public String getOld_path() {
        return old_path;
    }

    public void setOld_path(String old_path) {
        this.old_path = old_path;
    }

    public String getNow_path() {
        return now_path;
    }

    public void setNow_path(String now_path) {
        this.now_path = now_path;
    }
}
