package pl.mpak.g2;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PictureList {

  private ArrayList<BufferedImage> imageList;

  public PictureList() {
    super();
    imageList = new ArrayList<BufferedImage>();
  }

  /**
   * <p>Constructor loading images and put all to the list
   * @param filePattern for example "filename%04d.png"
   * @param pictures how many pictures are loaded
   * @throws IllegalArgumentException
   * @throws InterruptedException
   * @throws MalformedURLException 
   */
  public PictureList(String filePattern, int pictures) throws IllegalArgumentException, InterruptedException, MalformedURLException {
    this();
    loadImages(filePattern, pictures);
  }
  
  public PictureList(String fileName, int picturesX, int picturesY) throws IllegalArgumentException, InterruptedException, MalformedURLException {
    this();
    createFromImage(fileName, picturesX, picturesY);
  }
  
  public PictureList(URL url, int picturesX, int picturesY) throws IllegalArgumentException, InterruptedException {
    this();
    createFromImage(url, picturesX, picturesY);
  }
  
  private void loadImages(String filePattern, int pictures) throws IllegalArgumentException, InterruptedException, MalformedURLException {
    for (int i=0; i<pictures; i++) {
      addImage(String.format(filePattern, new Object[] {i}));
    }
  }
  
  private void createFromImage(String fileName, int picturesX, int picturesY) throws IllegalArgumentException, InterruptedException, MalformedURLException {
    createFromImage(getClass().getResource(fileName), picturesX, picturesY);
  }

  private void createFromImage(URL url, int picturesX, int picturesY) throws IllegalArgumentException, InterruptedException {
    Image image = Toolkit.getDefaultToolkit().createImage(url);
    G2Util.loadImage(image);
//    Image image = new ImageIcon(fileName).getImage();
    int picWidth = image.getWidth(null) /picturesX;
    int picHeight = image.getHeight(null) /picturesY;
    
    for (int y=0; y<picturesY; y++) {
      for (int x=0; x<picturesX; x++) {
        BufferedImage bi = new BufferedImage(picWidth, picHeight, BufferedImage.TYPE_INT_ARGB);
        bi.getGraphics().drawImage(image, 0, 0, picWidth -1, picHeight -1, x *picWidth, y *picHeight, x *picWidth +picWidth, y *picHeight +picHeight, null);
        imageList.add(bi);
      }
    }
  }

  public void addImage(String fileName) throws IllegalArgumentException, InterruptedException, MalformedURLException {
    addImage(getClass().getResource(fileName));
  }

  public void addImage(URL url) throws IllegalArgumentException, InterruptedException {
    imageList.add(G2Util.createImage(url));
  }

  public BufferedImage getImage(int picture) {
    return imageList.get(picture);
  }
  
  public int getCount() {
    return imageList.size();
  }

}
