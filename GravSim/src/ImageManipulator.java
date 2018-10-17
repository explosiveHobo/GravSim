import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

class ImageManipulator {

    static BufferedImage trimImage(BufferedImage image) {
        WritableRaster raster = image.getAlphaRaster();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int left = 0;
        int top = 0;
        int right = width - 1;
        int bottom = height - 1;
        int minRight = width - 1;
        int minBottom = height - 1;

        top:
        for (; top < bottom; top++) {
            for (int x = 0; x < width; x++) {
                if (raster.getSample(x, top, 0) != 0) {
                    minRight = x;
                    minBottom = top;
                    break top;
                }
            }
        }

        left:
        for (; left < minRight; left++) {
            for (int y = height - 1; y > top; y--) {
                if (raster.getSample(left, y, 0) != 0) {
                    minBottom = y;
                    break left;
                }
            }
        }

        bottom:
        for (; bottom > minBottom; bottom--) {
            for (int x = width - 1; x >= left; x--) {
                if (raster.getSample(x, bottom, 0) != 0) {
                    minRight = x;
                    break bottom;
                }
            }
        }

        right:
        for (; right > minRight; right--) {
            for (int y = bottom; y >= top; y--) {
                if (raster.getSample(right, y, 0) != 0) {
                    break right;
                }
            }
        }

        return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    static void invertImageFile(String imagePath) {

        if (imagePath.substring(0, 5).equals("invert")) {
            return;
        }

        BufferedImage inputFile = null;
        try {
            inputFile = ImageIO.read(new File("src/resources/" + imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert inputFile != null;
        for (int x = 0; x < inputFile.getWidth(); x++) {
            for (int y = 0; y < inputFile.getHeight(); y++) {
                int rgba = inputFile.getRGB(x, y);
                Color color = new Color(rgba, true);
                color = new Color(255 - color.getRed(),
                        255 - color.getGreen(),
                        255 - color.getBlue(), color.getAlpha());
                inputFile.setRGB(x, y, color.getRGB());
            }
        }

        try {
            File outputFile = new File("src/resources/" + "invert" + imagePath);
            ImageIO.write(inputFile, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Color averageColor(BufferedImage bi, int w, int h) {
        int sumr = 0, sumg = 0, sumb = 0;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }

        int total = w * h;

        return new Color(sumr / total, sumg / total, sumb / total);
    }

    static Color randomColor(Color imageColor, int bound) {

        if (imageColor == null) {
            return null;
        }

        Random rand = new Random(System.nanoTime());

        int r = imageColor.getRed() + (rand.nextInt(bound) - (bound / 2)), g = imageColor.getGreen() + (rand.nextInt(bound) - (bound / 2)), b = imageColor.getBlue() + (rand.nextInt(bound) - (bound / 2));

        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }

        return new Color(Math.abs(r), Math.abs(g), Math.abs(b));
    }

    //unused
    static void blendColor(Body base, Body toBlend) {

        Color baseColor = base.getColor();
        Color blendingColor = toBlend.getColor();

        if (baseColor == null || blendingColor == null) {
            return;
        }

        int rDiff = blendingColor.getRed() - baseColor.getRed();
        int gDiff = blendingColor.getGreen() - baseColor.getGreen();
        int bDiff = blendingColor.getBlue() - baseColor.getBlue();

        double prop = (toBlend.getMass() / base.getMass());

        int r = (int) (baseColor.getRed() + (prop * rDiff)), g = (int) (baseColor.getGreen() + (prop * gDiff)), b = (int) (baseColor.getBlue() + (prop * bDiff));

        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }

        base.setColor(new Color(Math.abs(r), Math.abs(g), Math.abs(b)));
    }



    //creates inverted copies of each png in resources that doesn't contain the word "invert" in its name
    public static void invertImages() {
        File resources = new File("resources/");
        for (File file : Objects.requireNonNull(resources.listFiles())) {
            if (file.getName().substring(file.getName().length() - 3).equals("png") && !file.getName().contains("invert")) {
                ImageManipulator.invertImageFile(file.getName());
            }
        }
    }

    //trims each png in resources to not have white space on the sides or top
    public static void trimImages() {
        for (File file : Objects.requireNonNull(new File("src/resources/").listFiles())) {
            if (file.getName().substring(file.getName().length() - 3).equals("png")) {
                try {
                    ImageIO.write(ImageManipulator.trimImage(ImageIO.read(file)), "png", new File("resources/" + file.getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
