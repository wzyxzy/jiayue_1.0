package com.jiayue.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "MusicCount")
public class MusicList {

    @Column(name = "list_name")
    private String name;//歌单名字
    @Column(name = "id", isId = true, autoGen = false)
    private String id;//歌单id
    @Column(name = "music_num")
    private int music_num;//歌单内歌曲数量

    private boolean playing;

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public MusicList() {
    }

    public MusicList(String name, String id, int music_num) {
        this.name = name;
        this.id = id;
        this.music_num = music_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMusic_num() {
        return music_num;
    }

    public void setMusic_num(int music_num) {
        this.music_num = music_num;
    }
}
