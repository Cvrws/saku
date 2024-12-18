package cc.unknown.ui.clickgui.kerosene;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValueComponent {
    private Value<?> value;
    private int x, y, width, height;
    private boolean dragging = false;
    private boolean isExpanded = false;
    private Module module;

    private static final int SLIDER_PADDING_LEFT = 30;
    private static final int SLIDER_HEIGHT = 2;
    private static final int SLIDER_Y_OFFSET = 6;

    public ValueComponent(Value<?> value, int x, int y, int width, int height, Module module) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.module = module;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        if (value instanceof NumberValue) {
            int sliderY = y + height - SLIDER_Y_OFFSET;
            return mouseX >= x + SLIDER_PADDING_LEFT &&
                    mouseX <= x + width &&
                    mouseY >= sliderY - 2 &&
                    mouseY <= sliderY + SLIDER_HEIGHT + 2;
        } else if (value instanceof BoundsNumberValue) {
            BoundsNumberValue boundsValue = (BoundsNumberValue) value;
            int sliderY = y + height - SLIDER_Y_OFFSET;
            int sliderWidth = width - SLIDER_PADDING_LEFT - 4;
            int sliderX = x + SLIDER_PADDING_LEFT;

            double minVal = boundsValue.getMin().doubleValue();
            double maxVal = boundsValue.getMax().doubleValue();
            double currentMinVal = boundsValue.getValue().doubleValue();
            double currentMaxVal = boundsValue.getSecondValue().doubleValue();

            // Calcular los porcentajes de la posición de cada valor
            float sliderValueMin = (float) ((currentMinVal - minVal) / (maxVal - minVal));
            float sliderValueMax = (float) ((currentMaxVal - minVal) / (maxVal - minVal));

            // Convertir a las posiciones de los controles (ajustando 1 o 2 píxeles a la izquierda)
            int knobXMin = sliderX + (int) (sliderWidth * sliderValueMin) - 1;  // Ajustado a la izquierda por 1 píxel
            int knobXMax = sliderX + (int) (sliderWidth * sliderValueMax) - 1;  // Ajustado a la izquierda por 1 píxel

            // Comprobar si el ratón está sobre cualquiera de los dos controles (knobs)
            int knobRadius = 4; // Radio del knob para la detección del ratón (ajústalo según el tamaño de tus knobs)
            return (Math.abs(mouseX - knobXMin) <= knobRadius && mouseY >= sliderY - 2 && mouseY <= sliderY + SLIDER_HEIGHT + 2) ||
                   (Math.abs(mouseX - knobXMax) <= knobRadius && mouseY >= sliderY - 2 && mouseY <= sliderY + SLIDER_HEIGHT + 2);
        } else if (value instanceof BooleanValue) {
            int booleanWidth = 20;
            int booleanHeight = 10;
            int booleanY = y + (height - booleanHeight) / 6;

            return mouseX >= x + width - booleanWidth &&
                    mouseX <= x + width &&
                    mouseY >= booleanY &&
                    mouseY <= booleanY + booleanHeight;
        } else if (value instanceof ModeValue) {
            int modeWidth = 20;
            int modeHeight = 10;
            int modeY = y + (height - modeHeight) / 6;

            return mouseX >= x + width - modeWidth &&
                    mouseX <= x + width &&
                    mouseY >= modeY &&
                    mouseY <= modeY + modeHeight;
        } else {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }

    public void drawComponent(int mouseX, int mouseY) {
        if (value != null) {
            Fonts.ROBOTO.get(15, Weight.LIGHT).drawWithShadow(value.getName(), x + SLIDER_PADDING_LEFT, y + 2, -1);

            if (value instanceof BooleanValue) {
                boolean booleanValue = ((BooleanValue) value).getValue();
                int color = booleanValue ? new Color(0, 255, 0).getRGB() : new Color(255, 0, 0).getRGB();
                Fonts.ROBOTO.get(15, Weight.LIGHT).drawWithShadow(booleanValue ? "True" : "False", x + width - 19, y + 2, color);

            } else if (value instanceof NumberValue) {
                NumberValue numberValue = (NumberValue) value;
                Number min = numberValue.getMin();
                Number max = numberValue.getMax();
                Number current = numberValue.getValue();

                double minVal = min.doubleValue();
                double maxVal = max.doubleValue();
                double currentVal = current.doubleValue();

                int sliderWidth = width - SLIDER_PADDING_LEFT - 4;
                int sliderX = x + SLIDER_PADDING_LEFT;
                int sliderY = y + height - SLIDER_Y_OFFSET;

                RenderUtil.drawRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + SLIDER_HEIGHT, new Color(60, 60, 60, 200).getRGB());

                float sliderValue = (float) ((currentVal - minVal) / (maxVal - minVal));
                int filledWidth = Math.max(0, Math.min(sliderWidth, (int) (sliderWidth * sliderValue)));

                RenderUtil.drawRect(sliderX, sliderY, sliderX + filledWidth, sliderY + SLIDER_HEIGHT, new Color(60, 180, 255).getRGB());

                int knobX = sliderX + filledWidth;
                RenderUtil.drawRect(knobX - 2, sliderY - 2, knobX + 2, sliderY + SLIDER_HEIGHT + 2, new Color(60, 180, 255).getRGB());

                String currentValue = String.format("%.2f", currentVal);
                int textWidth = (int) Fonts.ROBOTO.get(15, Weight.LIGHT).width(currentValue);
                Fonts.ROBOTO.get(15, Weight.LIGHT).drawWithShadow(currentValue, x + width - textWidth - 3, y + 2, -1);

                if (dragging) {
                    updateSingleSlider(mouseX);
                }
                
            } else if (value instanceof BoundsNumberValue) {
                BoundsNumberValue bounds = (BoundsNumberValue) value;
                Number min = bounds.getMin();
                Number max = bounds.getMax();
                Number currentMin = bounds.getValue();
                Number currentMax = bounds.getSecondValue();

                double minVal = min.doubleValue();
                double maxVal = max.doubleValue();
                double currentMinVal = currentMin.doubleValue();
                double currentMaxVal = currentMax.doubleValue();

                int sliderWidth = width - SLIDER_PADDING_LEFT - 4;
                int sliderX = x + SLIDER_PADDING_LEFT;
                int sliderY = y + height - SLIDER_Y_OFFSET;

                RenderUtil.drawRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + SLIDER_HEIGHT, new Color(60, 60, 60, 200).getRGB());

                float sliderValueMin = (float) ((currentMinVal - minVal) / (maxVal - minVal));
                float sliderValueMax = (float) ((currentMaxVal - minVal) / (maxVal - minVal));

                int filledWidthMin = Math.max(0, Math.min(sliderWidth, (int) (sliderWidth * sliderValueMin)));
                int filledWidthMax = Math.max(0, Math.min(sliderWidth, (int) (sliderWidth * sliderValueMax)));

                RenderUtil.drawRect(sliderX + filledWidthMin, sliderY, sliderX + filledWidthMax, sliderY + SLIDER_HEIGHT, new Color(60, 180, 255).getRGB());

                int knobXMin = sliderX + filledWidthMin;
                RenderUtil.drawRect(knobXMin - 2, sliderY - 2, knobXMin + 2, sliderY + SLIDER_HEIGHT + 2, new Color(60, 180, 255).getRGB());

                int knobXMax = sliderX + filledWidthMax;
                RenderUtil.drawRect(knobXMax - 2, sliderY - 2, knobXMax + 2, sliderY + SLIDER_HEIGHT + 2, new Color(60, 180, 255).getRGB());

                String currentMinValue = String.format("%.2f", currentMinVal);
                String currentMaxValue = String.format("%.2f", currentMaxVal);

                Fonts.ROBOTO.get(15, Weight.LIGHT).drawWithShadow(currentMinValue, sliderX + filledWidthMin - 10, sliderY - 12, -1);
                Fonts.ROBOTO.get(15, Weight.LIGHT).drawWithShadow(currentMaxValue, sliderX + filledWidthMax - 10, sliderY - 12, -1);

                if (dragging) {
                    updateDoubleSlider(mouseX);
                }
            } else if (value instanceof DescValue) {
                Fonts.MONSERAT.get(18, Weight.BOLD).drawWithShadow(((DescValue) value).getValue(), x + SLIDER_PADDING_LEFT, y + 12, -1);
            } else if (value instanceof ModeValue) {
                ModeValue modeValue = (ModeValue) value;
                String currentMode = modeValue.getValue().getName().toString();
                int lightBlue = new Color(135, 206, 250).getRGB();
                Fonts.ROBOTO.get(15, Weight.LIGHT).drawWithShadow(currentMode, x + width - Fonts.ROBOTO.get(15, Weight.LIGHT).width(currentMode) - 2, y + 2, lightBlue);
            }
        }
    }
    
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        final boolean left = mouseButton == 0;
        final boolean right = mouseButton == 1;
        
    	if (isMouseOver(mouseX, mouseY)) {
    		if (left) {
    			if (value instanceof BooleanValue) {
                    BooleanValue booleanValue = (BooleanValue) value;
                    booleanValue.setValue(!booleanValue.getValue());
                } else if (value instanceof NumberValue) {
                    dragging = true;
                } else if (value instanceof BoundsNumberValue) {
                	dragging = true;
                } else if (value instanceof ModeValue) {
                    Mode<?> mode = null;
                    ModeValue modeValue = (ModeValue) value;
                    final int currentIndex = modeValue.getModes().indexOf(modeValue.getValue());

                    if (modeValue.getModes().size() <= currentIndex + 1) {
                    	mode = modeValue.getModes().get(0);
                    } else {
                    	mode = modeValue.getModes().get(currentIndex + 1);
                    }
                    if (mode != null) {
                        modeValue.update(mode);
                    }
                }
    		} else if (right) {
    			if (value instanceof NumberValue) {
                    NumberValue numberValue = (NumberValue) value;
                    numberValue.setValue(numberValue.getValue());
                } else if (value instanceof BoundsNumberValue) {
                	BoundsNumberValue boundsValue = (BoundsNumberValue) value;
                	boundsValue.setValue(boundsValue.getValue());
                	boundsValue.setSecondValue(boundsValue.getSecondValue());
                } else if (value instanceof ModeValue) {
                    Mode<?> mode = null;
                    ModeValue modeValue = (ModeValue) value;
                    final int currentIndex = modeValue.getModes().indexOf(modeValue.getValue());
                    
                    if (0 > currentIndex - 1) {
                        mode = modeValue.getModes().get(modeValue.getModes().size() - 1);
                    } else {
                        mode = modeValue.getModes().get(currentIndex - 1);
                    }
                    
                    if (mode != null) {
                        modeValue.update(mode);
                    }
                }
    		}
    	}
    }
    
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            dragging = false;
        }
    }

    public void mouseDragged(int mouseX, int mouseY) {
        if (dragging) {
        	updateSingleSlider(mouseX);
        } else if (dragging) {
        	updateDoubleSlider(mouseX);
        }
    }

    private void updateSingleSlider(int mouseX) {
        if (value instanceof NumberValue) {
            NumberValue numberValue = (NumberValue) value;
            Number min = numberValue.getMin();
            Number max = numberValue.getMax();
            Number current = numberValue.getValue();

            double minVal = min.doubleValue();
            double maxVal = max.doubleValue();
            double currentVal = current.doubleValue();

            int sliderWidth = width - SLIDER_PADDING_LEFT - 4;
            int sliderX = x + SLIDER_PADDING_LEFT;

            float percent = (float) (mouseX - sliderX) / sliderWidth;
            percent = Math.min(1, Math.max(0, percent));

            double newValue = minVal + (maxVal - minVal) * percent;

            if (min instanceof Integer) {
                numberValue.setValue((int) newValue);
            } else if (min instanceof Float) {
                numberValue.setValue((float) newValue);
            } else {
                numberValue.setValue(newValue);
            }
        }
    }

    private void updateDoubleSlider(int mouseX) {
        if (value instanceof BoundsNumberValue) {
            BoundsNumberValue boundsValue = (BoundsNumberValue) value;

            Number min = boundsValue.getMin();
            Number max = boundsValue.getMax();
            Number currentMin = boundsValue.getValue();
            Number currentMax = boundsValue.getSecondValue();

            double minVal = min.doubleValue();
            double maxVal = max.doubleValue();
            double currentMinVal = currentMin.doubleValue();
            double currentMaxVal = currentMax.doubleValue();

            double percentage1 = (-minVal + currentMinVal) / (-minVal + maxVal);
            double percentage2 = (-minVal + currentMaxVal) / (-minVal + maxVal);

            percentage1 = Math.min(Math.max(percentage1, 0), 1);
            percentage2 = Math.min(Math.max(percentage2, 0), 1);

            int sliderWidth = width - SLIDER_PADDING_LEFT - 4;
            int sliderX = x + SLIDER_PADDING_LEFT;

            float percent = (float) (mouseX - sliderX) / sliderWidth;
            percent = Math.min(1, Math.max(0, percent));

            double newValue = minVal + (maxVal - minVal) * percent;

            if (Math.abs(mouseX - (sliderX + (sliderWidth * percentage1))) <
                Math.abs(mouseX - (sliderX + (sliderWidth * percentage2)))) {
                if (newValue <= currentMaxVal) {
                    if (min instanceof Integer) {
                        boundsValue.setValue((int) newValue);
                    } else if (min instanceof Float) {
                        boundsValue.setValue((float) newValue);
                    } else {
                        boundsValue.setValue(newValue);
                    }
                }
            } else {
                if (newValue >= currentMinVal) {
                    if (max instanceof Integer) {
                        boundsValue.setSecondValue((int) newValue);
                    } else if (max instanceof Float) {
                        boundsValue.setSecondValue((float) newValue);
                    } else {
                        boundsValue.setSecondValue(newValue);
                    }
                }
            }

            if (boundsValue.getValue().doubleValue() > boundsValue.getSecondValue().doubleValue()) {
                boundsValue.setValue(boundsValue.getSecondValue());
                boundsValue.setSecondValue(currentMin);
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {

    }

    public void onGuiClosed() {
        dragging = false;
    }
}