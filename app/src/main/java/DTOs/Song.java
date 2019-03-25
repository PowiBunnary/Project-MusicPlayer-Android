package DTOs;

public class Song {
    private String name;
    private  int file, duration;

    public Song(String name, int file) {
        this.name = name;
        this.file = file;
    }

    public Song(String name, int file, int duration) {
        this.name = name;
        this.file = file;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFile() {
        return file;
    }

    public void setFile(int file) {
        this.file = file;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
