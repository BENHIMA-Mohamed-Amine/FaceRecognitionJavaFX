package org.example.facenetfx;

import javafx.scene.image.Image;
import org.opencv.core.Mat;

public class PersonFrame {
    private boolean isSamePerson;
    private Mat frame;
    private Image img;

    public PersonFrame(boolean isSamePerson, Mat frame) {
        this.isSamePerson = isSamePerson;
        this.frame = frame;
    }

    public boolean isSamePerson() {
        return isSamePerson;
    }

    public Mat getFrame() {
        return frame;
    }

    public void setImg(Image img){
        this.img = img;
    }

    public Image getImg() {
        return img;
    }
}
