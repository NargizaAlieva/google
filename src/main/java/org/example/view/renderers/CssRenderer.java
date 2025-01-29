package org.example.view.renderers;

import org.example.model.renderTree.RenderNode;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;

public class CssRenderer {
    public void setFont(Graphics2D g2d, RenderNode node) {
        Font orgFont = g2d.getFont();
        String fontFamily = orgFont.getFamily();
        int fontSize = orgFont.getSize();
        int fontStyle = orgFont.getStyle();

        applyDefaultStyles(node);

        if (node.getAppliedStyles().containsKey("font-size")) {
            String fontSizeStr = node.getAppliedStyles().get("font-size");
            fontSize = parseFontSize(fontSizeStr, orgFont.getSize());
        }

        if (node.getAppliedStyles().containsKey("font-weight")) {
            fontStyle = parseFontWeight(node.getAppliedStyles().get("font-weight"), fontStyle);
        }

        if (node.getAppliedStyles().containsKey("font-style")) {
            fontStyle = parseFontStyle(node.getAppliedStyles().get("font-style"), fontStyle);
        }

        setColor(g2d, node);

        g2d.setFont(new Font(fontFamily, fontStyle, fontSize));
    }

    private void applyDefaultStyles(RenderNode node) {
        String tagName = node.getTagName();

        if (!node.getAppliedStyles().containsKey("font-size")) {
            if ("h1".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-size", "32px");
            } else if ("h2".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-size", "28px");
            } else if ("h3".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-size", "24px");
            } else if ("h4".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-size", "20px");
            } else if ("h5".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-size", "18px");
            } else if ("h6".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-size", "16px");
            } else if ("p".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-size", "16px");
            }
        }

        if (!node.getAppliedStyles().containsKey("font-weight")) {
            if ("h1".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-weight", "bold");
            } else if ("h2".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-weight", "bold");
            } else if ("h3".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-weight", "bold");
            } else if ("h4".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-weight", "bold");
            } else if ("h5".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-weight", "bold");
            } else if ("h6".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-weight", "bold");
            } else if ("p".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-weight", "bold");
            } else if ("strong".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-weight", "bold");
            } else if ("b".equalsIgnoreCase(tagName)) {
                node.getAppliedStyles().put("font-weight", "bold");
            }

            if (!node.getAppliedStyles().containsKey("font-style")) {
                if ("em".equalsIgnoreCase(tagName)) {
                    node.getAppliedStyles().put("font-style", "italic");
                } else if ("i".equalsIgnoreCase(tagName)) {
                    node.getAppliedStyles().put("font-style", "italic");
                }
            }
        }
    }

    private int parseFontSize(String fontSizeStr, int defaultSize) {
        try {
            if (fontSizeStr.endsWith("px")) {
                return Integer.parseInt(fontSizeStr.replace("px", ""));
            } else {
                return Integer.parseInt(fontSizeStr);
            }
        } catch (NumberFormatException e) {
            return defaultSize;
        }
    }

    private int parseFontWeight(String weight, int currentStyle) {
        if ("bold".equalsIgnoreCase(weight)) {
            currentStyle |= Font.BOLD;
        } else if ("normal".equalsIgnoreCase(weight)) {
            currentStyle &= ~Font.BOLD;
        }
        return currentStyle;
    }

    private int parseFontStyle(String style, int currentStyle) {
        if ("italic".equalsIgnoreCase(style)) {
            currentStyle |= Font.ITALIC;
        } else if ("normal".equalsIgnoreCase(style)) {
            currentStyle &= ~Font.ITALIC;
        }
        return currentStyle;
    }


    public void setColor(Graphics2D g2d, RenderNode node) {
        if (node.getAppliedStyles().containsKey("color")) {
            String colorStr = node.getAppliedStyles().get("color");

            Color color = parseColor(colorStr);

            if (color != null) {
                g2d.setColor(color);
            }
        }
    }

    private Color parseColor(String colorStr) {
        try {
            if (colorStr.startsWith("#")) {
                if (colorStr.length() == 7) {
                    return Color.decode(colorStr);
                } else if (colorStr.length() == 9) {
                    return Color.decode(colorStr.substring(0, 7));
                }
            }

            else if (colorStr.startsWith("rgb(") && colorStr.endsWith(")")) {
                String[] rgb = colorStr.substring(4, colorStr.length() - 1).split(",");
                if (rgb.length == 3) {
                    int r = Integer.parseInt(rgb[0].trim());
                    int g = Integer.parseInt(rgb[1].trim());
                    int b = Integer.parseInt(rgb[2].trim());
                    return new Color(r, g, b);
                }
            }

            return (Color) Color.class.getField(colorStr.toLowerCase()).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public void resetFont(Graphics2D g2d) {
        Font font = g2d.getFont();
        g2d.setFont(new Font(font.getFontName(), Font.PLAIN, font.getSize()));
    }
}
