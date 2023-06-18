package imageRecognition;

import java.awt.*;
import java.util.ArrayList;

public class RGBtoHSVConverter {
    public static void main(String[] args) {

        Color white = new Color(240, 237, 213);
        Color orange = new Color(246, 162, 93);
        Color black = new Color(81, 83, 82);
        Color green = new Color(61,143,100);

        float[] hsv = convertRGBtoHSV(black);
        System.out.println("BLACK - HSV values: Hue = " + hsv[0] + ", Saturation = " + hsv[1] + ", Value = " + hsv[2]);
         hsv = convertRGBtoHSV(orange);
        System.out.println("orange - HSV values: Hue = " + hsv[0] + ", Saturation = " + hsv[1] + ", Value = " + hsv[2]);
         hsv = convertRGBtoHSV(white);
        System.out.println("white - HSV values: Hue = " + hsv[0] + ", Saturation = " + hsv[1] + ", Value = " + hsv[2]);
        hsv = convertRGBtoHSV(green);
        System.out.println("green - HSV values: Hue = " + hsv[0] + ", Saturation = " + hsv[1] + ", Value = " + hsv[2]);

    }



    public static float[] convertRGBtoHSV(Color color) {
        float[] hsv = new float[3];

        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        // Calculate Hue
        if (delta == 0) {
            hsv[0] = 0;
        } else if (max == r) {
            hsv[0] = ((g - b) / delta) % 6;
        } else if (max == g) {
            hsv[0] = (b - r) / delta + 2;
        } else {
            hsv[0] = (r - g) / delta + 4;
        }
        hsv[0] *= 60;
        if (hsv[0] < 0) {
            hsv[0] += 360;
        }

        // Calculate Saturation
        if (max == 0) {
            hsv[1] = 0;
        } else {
            hsv[1] = delta / max;
        }

        // Calculate Value
        hsv[2] = max;

        Color.getHSBColor(hsv[0],hsv[1],hsv[2]);
        return hsv;
    }
}
