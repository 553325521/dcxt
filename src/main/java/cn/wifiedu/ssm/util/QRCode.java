package cn.wifiedu.ssm.util;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCode
{
  private static final int IMAGE_WIDTH = 64;
  private static final int IMAGE_HEIGHT = 64;
  private static final int IMAGE_HALF_WIDTH = 32;
  private static final int FRAME_WIDTH = 2;
  private static MultiFormatWriter mutiWriter = new MultiFormatWriter();

  public static void encode(String content, int width, int height, String srcImagePath, String destImagePath)
  {
    try {
      ImageIO.write(genBarcode(content, width, height, srcImagePath), 
        "jpg", new File(destImagePath));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (WriterException e) {
      e.printStackTrace();
    }
  }

  public static BufferedImage genBarcode(String content, int width, int height, String srcImagePath)
    throws WriterException, IOException
  {
    BufferedImage scaleImage = scale(srcImagePath, 64, 
      64, true);
    int[][] srcPixels = new int[64][64];
    for (int i = 0; i < scaleImage.getWidth(); ++i) {
      for (int j = 0; j < scaleImage.getHeight(); ++j) {
        srcPixels[i][j] = scaleImage.getRGB(i, j);
      }
    }

    Map hint = new HashMap();
    hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
    hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

    BitMatrix matrix = mutiWriter.encode(content, BarcodeFormat.QR_CODE, 
      width, height, hint);

    int halfW = matrix.getWidth() / 2;
    int halfH = matrix.getHeight() / 2;
    int[] pixels = new int[width * height];

    for (int y = 0; y < matrix.getHeight(); ++y) {
      for (int x = 0; x < matrix.getWidth(); ++x)
      {
        Color color;
        int colorInt;
        if ((x > 0) && (x < 170) && (y > 0) && (y < 170)) {
          color = new Color(0, 0, 0);
          colorInt = color.getRGB();
          pixels[(y * width + x)] = ((matrix.get(x, y)) ? colorInt : 16777215);
        }
        else if ((x > halfW - 32) && 
          (x < halfW + 32) && 
          (y > halfH - 32) && 
          (y < halfH + 32)) {
          pixels[(y * width + x)] = srcPixels[(x - halfW + 32)][(y - halfH + 32)];
        }
        else if (((x > halfW - 32 - 2) && 
          (x < halfW - 32 + 2) && 
          (y > halfH - 32 - 2) && 
          (y < halfH + 
          32 + 2)) || 
          ((x > halfW + 32 - 2) && 
          (x < halfW + 32 + 2) && 
          (y > halfH - 32 - 2) && 
          (y < halfH + 
          32 + 2)) || 
          ((x > halfW - 32 - 2) && 
          (x < halfW + 32 + 2) && 
          (y > halfH - 32 - 2) && 
          (y < halfH - 
          32 + 2)) || (
          (x > halfW - 32 - 2) && 
          (x < halfW + 32 + 2) && 
          (y > halfH + 32 - 2) && 
          (y < halfH + 
          32 + 2))) {
          pixels[(y * width + x)] = 268435455;
        }
        else
        {
          color = new Color(0, 0, 0);
          colorInt = color.getRGB();

          pixels[(y * width + x)] = ((matrix.get(x, y)) ? colorInt : 16777215);
        }
      }
    }

    BufferedImage image = new BufferedImage(width, height, 
      1);
    image.getRaster().setDataElements(0, 0, width, height, pixels);

    return image;
  }

  public static BufferedImage genBarcode(String content, int width, int height)
    throws WriterException, IOException
  {
    Map hint = new HashMap();
    hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
    hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

    BitMatrix matrix = mutiWriter.encode(content, BarcodeFormat.QR_CODE, 
      width, height, hint);

    int halfW = matrix.getWidth() / 2;
    int halfH = matrix.getHeight() / 2;
    int[] pixels = new int[width * height];

    for (int y = 0; y < matrix.getHeight(); ++y) {
      for (int x = 0; x < matrix.getWidth(); ++x)
      {
        Color color;
        int colorInt;
        if ((x > 0) && (x < 170) && (y > 0) && (y < 170)) {
          color = new Color(0, 0, 0);
          colorInt = color.getRGB();
          pixels[(y * width + x)] = ((matrix.get(x, y)) ? colorInt : 16777215);
        }
        else if (((x > halfW - 32 - 2) && 
          (x < halfW - 32 + 2) && 
          (y > halfH - 32 - 2) && 
          (y < halfH + 
          32 + 2)) || 
          ((x > halfW + 32 - 2) && 
          (x < halfW + 32 + 2) && 
          (y > halfH - 32 - 2) && 
          (y < halfH + 
          32 + 2)) || 
          ((x > halfW - 32 - 2) && 
          (x < halfW + 32 + 2) && 
          (y > halfH - 32 - 2) && 
          (y < halfH - 
          32 + 2)) || (
          (x > halfW - 32 - 2) && 
          (x < halfW + 32 + 2) && 
          (y > halfH + 32 - 2) && 
          (y < halfH + 
          32 + 2))) {
          pixels[(y * width + x)] = 268435455;
        }
        else
        {
          color = new Color(0, 0, 0);
          colorInt = color.getRGB();

          pixels[(y * width + x)] = ((matrix.get(x, y)) ? colorInt : 16777215);
        }
      }
    }

    BufferedImage image = new BufferedImage(width, height, 
      1);
    image.getRaster().setDataElements(0, 0, width, height, pixels);

    return image;
  }

  private static BufferedImage scale(String srcImageFile, int height, int width, boolean hasFiller)
    throws IOException
  {
    double ratio = 0.0D;
    File file = new File(srcImageFile);
    BufferedImage srcImage = ImageIO.read(file);
    Image destImage = srcImage.getScaledInstance(width, height, 
      4);

    if ((srcImage.getHeight() > height) || (srcImage.getWidth() > width)) {
      if (srcImage.getHeight() > srcImage.getWidth())
        ratio = new Integer(height).doubleValue() / 
          srcImage.getHeight();
      else {
        ratio = new Integer(width).doubleValue() / 
          srcImage.getWidth();
      }
      AffineTransformOp op = new AffineTransformOp(
        AffineTransform.getScaleInstance(ratio, ratio), null);
      destImage = op.filter(srcImage, null);
    }
    if (hasFiller) {
      BufferedImage image = new BufferedImage(width, height, 
        1);
      Graphics2D graphic = image.createGraphics();
      graphic.setColor(Color.white);
      graphic.fillRect(0, 0, width, height);
      if (width == destImage.getWidth(null))
        graphic.drawImage(destImage, 0, 
          (height - destImage.getHeight(null)) / 2, 
          destImage.getWidth(null), destImage.getHeight(null), 
          Color.white, null);
      else
        graphic.drawImage(destImage, 
          (width - destImage.getWidth(null)) / 2, 0, 
          destImage.getWidth(null), destImage.getHeight(null), 
          Color.white, null);
      graphic.dispose();
      destImage = image;
    }
    return ((BufferedImage)destImage);
  }

  public static void main(String[] args) throws UnsupportedEncodingException
  {
    encode("http://www.baidu.com/", 512, 512, "/Users/x/Pictures/is-money.png", "/Users/x/Downloads/test.jpg");
  }
}