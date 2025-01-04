package cc.unknown.util.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphicsUtil {
    private Graphics2D currentContext;

    public void setGraphicsContext(Graphics context) {
        currentContext = (Graphics2D) context;
    }

    public void setFont(Font font) {
        currentContext.setFont(font);
    }

    public void rect(int x, int y, int width, int height, Color color) {
        currentContext.setColor(color);
        currentContext.fillRect(x, y, width, height);
    }

    public void rect2(int left, int top, int right, int bottom, Color color) {
        rect(left, top, right - left, bottom - top, color);
    }

    public void horizontalGradientRect(int x, int y, int width, int height, Color startColor, Color endColor) {
        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {startColor, endColor};
        currentContext.setPaint(new LinearGradientPaint(x, y, x + width, y + .01f, fractions, colors));
        currentContext.fillRect(x, y, width, height);
    }

    public void horizontalGradientRect2(int left, int top, int right, int bottom, Color startColor, Color endColor) {
        horizontalGradientRect(left, top, right - left, bottom - top, startColor, endColor);
    }

    public void verticalGradientRect(int x, int y, int width, int height, Color startColor, Color endColor) {
        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {startColor, endColor};
        currentContext.setPaint(new LinearGradientPaint(x, y, x + .01f, y + height, fractions, colors));
        currentContext.fillRect(x, y, width, height);
    }

    public void verticalGradientRect2(int left, int top, int right, int bottom, Color startColor, Color endColor) {
        verticalGradientRect(left, top, right - left, bottom - top, startColor, endColor);
    }

    public void drawString(String text, int x, int y, Color color) {
        currentContext.setColor(color);
        currentContext.drawString(text, x, y);
    }
}
