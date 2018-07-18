package com.sarnava.myassignment;

public class Video {
    private String video_id, name;

    public Video(String video_id, String name) {
        this.video_id = video_id;
        this.name = name;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
