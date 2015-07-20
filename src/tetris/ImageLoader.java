package tetris;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageLoader {
    public static Image[] loadImage(String path, int w) throws IOException {
        BufferedImage load = ImageIO.read(ImageLoader.class.getResource(path));
        Image[] images = new Image[load.getWidth()/w];
        for(int i = 0; i < images.length; i++) {
            images[i] = load.getSubimage(i*w, 0, w, w);
        }
        return images;
    }
}
