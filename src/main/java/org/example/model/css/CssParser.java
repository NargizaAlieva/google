package org.example.model.css;

import org.example.model.css.cssom.CssRule;
import org.example.model.css.cssom.CssTree;
import org.example.model.html.HtmlElement;
import org.example.model.socket.CssResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CssParser {
    CssTree cssTree = new CssTree();

    public CssParser() {}

    public CssTree parse(List<CssResource> cssResources) {
        for (CssResource cssResource : cssResources) {
            splitToBlocks(cssResource.getContent());
        }
        return cssTree;
    }

    public void splitToBlocks(String content) {
        int i = 0;
        boolean isAt = false;
        int whereStartAt = 0;
        String media = "";
        Stack<Character> stack = new Stack<>();
        ArrayList<String> mediaBlocks = new ArrayList<>();

        while (i < content.length()) {
            char currentChar = content.charAt(i);

            if (currentChar == '@') {
                isAt = true;
                whereStartAt = i;
            }

            if (currentChar == '{') {
                if (isAt) {
                    media = content.substring(whereStartAt, i).trim();
                    isAt = false;
                }
                stack.push('{');
            } else if (currentChar == '}') {
                if (!stack.isEmpty() && stack.peek() == '{') {
                    stack.pop();
                }
                if (stack.isEmpty() && !media.isEmpty()) {
                    String block = content.substring(whereStartAt, i + 1).trim();
                    mediaBlocks.add(block);

                    content = content.substring(0, whereStartAt) + content.substring(i + 1);

                    i = whereStartAt - 1;
                    media = "";
                }
            }

            i += 1;
        }

        setMediaCss(mediaBlocks);
        setUsualCss(content.split("}"), null);
    }
    private void setMediaCss(ArrayList<String> mediaBlocks) {
        for (String mediaBlock : mediaBlocks) {
            String media = "";

            Pattern mediaPattern = Pattern.compile("@[^{]+\\{");
            Matcher mediaMatcher = mediaPattern.matcher(mediaBlock);
            if (mediaMatcher.find()) {
                media = mediaMatcher.group().trim();
            }

            String innerContent = mediaBlock.substring(media.length(), mediaBlock.length() - 1).trim();

            ArrayList<String> cssBlocks = new ArrayList<>();
            Pattern blockPattern = Pattern.compile("[^{}]+\\{[^}]*\\}");
            Matcher blockMatcher = blockPattern.matcher(innerContent);
            while (blockMatcher.find()) {
                cssBlocks.add(blockMatcher.group().trim());
            }

            String[] cssBlocksArray = cssBlocks.toArray(new String[0]);

            setUsualCss(cssBlocksArray, media);
        }
    }


    private void setUsualCss(String[] blocks, String media){
        for (String block : blocks) {
            block = block.trim();
            if (block.isEmpty()) continue;

            String[] parts = block.split("\\{", 2);
            if (parts.length != 2) continue;

            String selectorsPart = parts[0].trim();

            selectorsPart = selectorsPart.replaceAll("/\\*.*?\\*/", "").trim();

            if (selectorsPart.isEmpty()) continue;

            selectorsPart = selectorsPart.replace(".", "").trim();

            String[] rawSelectors = selectorsPart.split("\\s+");

            List<String> filteredSelectors = new ArrayList<>();
            for (String selector : rawSelectors) {
                if (!selector.isEmpty() && selector.matches("[a-zA-Z0-9_-]+")) {
                    filteredSelectors.add(selector);
                }
            }

            if (filteredSelectors.isEmpty()) continue;

            CssRule rule = new CssRule(filteredSelectors);
            rule.setMedia(media);

            String propertiesBlock = parts[1].trim();
            String[] properties = propertiesBlock.split(";");
            for (String property : properties) {
                String[] keyValue = property.split(":", 2);
                if (keyValue.length == 2) {
                    rule.addProperty(keyValue[0].trim(), keyValue[1].trim());
                }
            }
            cssTree.addRule(rule);
        }
    }

    public void findCssOfHtml(HtmlElement htmlElement, List<String> selectors, CssRule cssRule) {
        if (selectors.isEmpty()) {
            return;
        }

        if (htmlElement.getClasses() == null) {
            return;
        }

        String currentSelector = selectors.get(0);

        if (Arrays.asList(htmlElement.getClasses()).contains(currentSelector) || htmlElement.getTag().equals(currentSelector)) {
            selectors = selectors.subList(1, selectors.size());

            for (HtmlElement child : htmlElement.getChildren()) {
                findCssOfHtml(child, selectors, cssRule);
            }
        }

        for (HtmlElement child : htmlElement.getChildren()) {
            findCssOfHtml(child, selectors, cssRule);
        }
    }
}